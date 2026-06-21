package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.Album;
import com.example.picture.entity.DedupWhitelist;
import com.example.picture.entity.Picture;
import com.example.picture.repository.DedupWhitelistRepository;
import com.example.picture.repository.PictureRepository;
import com.example.picture.util.ImageHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DedupService {

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private DedupWhitelistRepository whitelistRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private TagService tagService;

    @Value("${upload.path:/app/images/}")
    private String uploadPath;

    @Transactional
    public void backfillHashes(Long userId) throws IOException {
        List<Picture> pictures = pictureRepository.findByUserIdWithDetails(userId);
        for (Picture picture : pictures) {
            if (Boolean.TRUE.equals(picture.getDeleted())) {
                continue;
            }
            boolean needSave = false;
            if (picture.getMd5Hash() == null || picture.getPerceptualHash() == null
                    || picture.getWidth() == null || picture.getHeight() == null) {
                String fileName = picture.getUrl().replace("/images/", "");
                File file = new File(uploadPath + fileName);
                if (file.exists()) {
                    if (picture.getMd5Hash() == null) {
                        picture.setMd5Hash(ImageHashUtil.computeMD5(file));
                        needSave = true;
                    }
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        if (picture.getPerceptualHash() == null) {
                            picture.setPerceptualHash(ImageHashUtil.computePerceptualHash(img));
                            needSave = true;
                        }
                        if (picture.getWidth() == null) {
                            picture.setWidth(img.getWidth());
                            needSave = true;
                        }
                        if (picture.getHeight() == null) {
                            picture.setHeight(img.getHeight());
                            needSave = true;
                        }
                    }
                }
                if (needSave) {
                    pictureRepository.save(picture);
                }
            }
        }
    }

    @Transactional
    public DedupResultDTO detectDuplicates(Long userId, String similarityLevel) throws IOException {
        backfillHashes(userId);

        List<Picture> pictures = pictureRepository.findByUserIdWithDetails(userId);
        pictures = pictures.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getDeleted()))
                .collect(Collectors.toList());

        Set<String> whitelistPairs = getWhitelistPairSet(userId);

        DedupResultDTO result = new DedupResultDTO();
        result.setTotalPicturesScanned(pictures.size());

        List<DuplicateGroupDTO> duplicateGroups = detectExactDuplicates(pictures, whitelistPairs);
        List<DuplicateGroupDTO> similarGroups = detectSimilarImages(pictures, similarityLevel, whitelistPairs, duplicateGroups);

        result.setDuplicateGroups(duplicateGroups);
        result.setSimilarGroups(similarGroups);
        result.setDuplicateGroupCount(duplicateGroups.size());
        result.setSimilarGroupCount(similarGroups.size());

        int dupPictureCount = 0;
        long totalReclaimable = 0;
        for (DuplicateGroupDTO g : duplicateGroups) {
            dupPictureCount += g.getPictureCount();
            totalReclaimable += g.getReclaimableSize() != null ? g.getReclaimableSize() : 0;
        }
        for (DuplicateGroupDTO g : similarGroups) {
            dupPictureCount += g.getPictureCount();
            totalReclaimable += g.getReclaimableSize() != null ? g.getReclaimableSize() : 0;
        }
        result.setDuplicatePictureCount(dupPictureCount);
        result.setTotalReclaimableSize(totalReclaimable);

        return result;
    }

    private List<DuplicateGroupDTO> detectExactDuplicates(List<Picture> pictures, Set<String> whitelistPairs) {
        Map<String, List<Picture>> md5Groups = new HashMap<>();
        for (Picture p : pictures) {
            if (p.getMd5Hash() != null) {
                md5Groups.computeIfAbsent(p.getMd5Hash(), k -> new ArrayList<>()).add(p);
            }
        }

        List<DuplicateGroupDTO> result = new ArrayList<>();
        int groupIdx = 0;
        for (Map.Entry<String, List<Picture>> entry : md5Groups.entrySet()) {
            List<Picture> group = entry.getValue();
            if (group.size() < 2) continue;
            filterWhitelisted(group, whitelistPairs);
            if (group.size() < 2) continue;

            DuplicateGroupDTO dto = new DuplicateGroupDTO();
            dto.setGroupId("dup-md5-" + (groupIdx++));
            dto.setGroupType("EXACT");
            dto.setSimilarityLevel("IDENTICAL");
            dto.setAverageSimilarity(1.0);
            dto.setPictureCount(group.size());
            dto.setSelected(false);

            List<PictureDedupDTO> picDtos = group.stream()
                    .sorted(Comparator.comparingLong(this::getQualityScore).reversed())
                    .map(this::toDedupDTO)
                    .collect(Collectors.toList());

            applyRecommendations(picDtos, dto);

            long totalSize = picDtos.stream().mapToLong(p -> p.getSize() != null ? p.getSize() : 0).sum();
            long keepSize = picDtos.stream().filter(p -> Boolean.TRUE.equals(p.getRecommended()))
                    .mapToLong(p -> p.getSize() != null ? p.getSize() : 0).sum();
            dto.setTotalSize(totalSize);
            dto.setReclaimableSize(totalSize - keepSize);
            dto.setPictures(picDtos);

            result.add(dto);
        }

        result.sort((a, b) -> Long.compare(b.getReclaimableSize(), a.getReclaimableSize()));
        return result;
    }

    private List<DuplicateGroupDTO> detectSimilarImages(List<Picture> pictures, String similarityLevel,
                                                        Set<String> whitelistPairs,
                                                        List<DuplicateGroupDTO> exactDuplicateGroups) {
        double threshold = ImageHashUtil.getThresholdForLevel(similarityLevel);

        Set<Long> alreadyInExactDup = new HashSet<>();
        for (DuplicateGroupDTO g : exactDuplicateGroups) {
            for (PictureDedupDTO p : g.getPictures()) {
                alreadyInExactDup.add(p.getId());
            }
        }

        List<Picture> candidates = pictures.stream()
                .filter(p -> p.getPerceptualHash() != null && !alreadyInExactDup.contains(p.getId()))
                .collect(Collectors.toList());

        List<List<Picture>> similarGroups = clusterSimilarImages(candidates, threshold, whitelistPairs);

        List<DuplicateGroupDTO> result = new ArrayList<>();
        int groupIdx = 0;
        for (List<Picture> group : similarGroups) {
            if (group.size() < 2) continue;

            DuplicateGroupDTO dto = new DuplicateGroupDTO();
            dto.setGroupId("dup-sim-" + (groupIdx++));
            dto.setGroupType("SIMILAR");

            double avgSim = 0;
            int count = 0;
            String bestLevel = "NONE";
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    double sim = ImageHashUtil.calculateSimilarity(
                            group.get(i).getPerceptualHash(),
                            group.get(j).getPerceptualHash());
                    avgSim += sim;
                    count++;
                    String level = ImageHashUtil.getSimilarityLevel(sim);
                    if (levelPriority(level) > levelPriority(bestLevel)) {
                        bestLevel = level;
                    }
                }
            }
            if (count > 0) avgSim /= count;
            dto.setAverageSimilarity(Math.round(avgSim * 1000.0) / 1000.0);
            dto.setSimilarityLevel(bestLevel);
            dto.setPictureCount(group.size());
            dto.setSelected(false);

            Picture firstPic = group.get(0);
            String firstPHash = firstPic != null ? firstPic.getPerceptualHash() : null;

            List<PictureDedupDTO> picDtos = group.stream()
                    .map(p -> {
                        PictureDedupDTO pd = toDedupDTO(p);
                        if (firstPHash != null && p.getPerceptualHash() != null) {
                            pd.setSimilarity(Math.round(ImageHashUtil.calculateSimilarity(
                                    firstPHash, p.getPerceptualHash()) * 1000.0) / 1000.0);
                        }
                        return pd;
                    })
                    .sorted(Comparator.comparingLong(this::getQualityScoreFromDto).reversed())
                    .collect(Collectors.toList());

            applyRecommendations(picDtos, dto);

            long totalSize = picDtos.stream().mapToLong(p -> p.getSize() != null ? p.getSize() : 0).sum();
            long keepSize = picDtos.stream().filter(p -> Boolean.TRUE.equals(p.getRecommended()))
                    .mapToLong(p -> p.getSize() != null ? p.getSize() : 0).sum();
            dto.setTotalSize(totalSize);
            dto.setReclaimableSize(totalSize - keepSize);
            dto.setPictures(picDtos);

            result.add(dto);
        }

        result.sort((a, b) -> Long.compare(b.getReclaimableSize(), a.getReclaimableSize()));
        return result;
    }

    private String findPHash(List<Picture> group, Long id) {
        for (Picture p : group) {
            if (p.getId().equals(id)) return p.getPerceptualHash();
        }
        return null;
    }

    private List<List<Picture>> clusterSimilarImages(List<Picture> candidates, double threshold, Set<String> whitelistPairs) {
        int n = candidates.size();
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Long id1 = candidates.get(i).getId();
                Long id2 = candidates.get(j).getId();
                if (isWhitelisted(whitelistPairs, id1, id2)) continue;
                double sim = ImageHashUtil.calculateSimilarity(
                        candidates.get(i).getPerceptualHash(),
                        candidates.get(j).getPerceptualHash());
                if (sim >= threshold) {
                    union(parent, i, j);
                }
            }
        }

        Map<Integer, List<Picture>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = find(parent, i);
            clusters.computeIfAbsent(root, k -> new ArrayList<>()).add(candidates.get(i));
        }

        return new ArrayList<>(clusters.values());
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    private void union(int[] parent, int x, int y) {
        int xr = find(parent, x);
        int yr = find(parent, y);
        if (xr != yr) parent[xr] = yr;
    }

    private int levelPriority(String level) {
        switch (level) {
            case "IDENTICAL": return 4;
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }

    private void applyRecommendations(List<PictureDedupDTO> picDtos, DuplicateGroupDTO dto) {
        if (picDtos.isEmpty()) return;

        for (int i = 0; i < picDtos.size(); i++) {
            PictureDedupDTO p = picDtos.get(i);
            p.setRecommended(i == 0);
            p.setSelectedForDelete(i != 0);
            int pixels = (p.getWidth() != null && p.getHeight() != null)
                    ? p.getWidth() * p.getHeight() : 0;
            p.setPixels(pixels);
            p.setQualityScore(calculateQualityLabel(p));
        }

        PictureDedupDTO best = picDtos.get(0);
        StringBuilder reason = new StringBuilder();
        if (best.getPixels() != null && best.getPixels() > 0) {
            reason.append("分辨率最高");
        }
        if (best.getSize() != null) {
            if (reason.length() > 0) reason.append("、");
            reason.append("文件最大");
        }
        dto.setRecommendedReason(reason.toString());
    }

    private String calculateQualityLabel(PictureDedupDTO p) {
        int pixels = p.getPixels() != null ? p.getPixels() : 0;
        if (pixels >= 8000000) return "超清";
        if (pixels >= 2000000) return "高清";
        if (pixels >= 800000) return "标清";
        return "普通";
    }

    private long getQualityScore(Picture p) {
        long pixels = (p.getWidth() != null && p.getHeight() != null)
                ? (long) p.getWidth() * p.getHeight() : 0;
        long size = p.getSize() != null ? p.getSize() : 0;
        return pixels * 1000 + size;
    }

    private long getQualityScoreFromDto(PictureDedupDTO p) {
        long pixels = p.getPixels() != null ? p.getPixels() : 0;
        long size = p.getSize() != null ? p.getSize() : 0;
        return pixels * 1000 + size;
    }

    private Set<String> getWhitelistPairSet(Long userId) {
        List<DedupWhitelist> list = whitelistRepository.findAllByUserId(userId);
        Set<String> set = new HashSet<>();
        for (DedupWhitelist w : list) {
            set.add(makePair(w.getPictureId1(), w.getPictureId2()));
        }
        return set;
    }

    private String makePair(Long a, Long b) {
        if (a < b) return a + "-" + b;
        return b + "-" + a;
    }

    private boolean isWhitelisted(Set<String> set, Long a, Long b) {
        return set.contains(makePair(a, b));
    }

    private void filterWhitelisted(List<Picture> group, Set<String> whitelistPairs) {
        if (group.size() < 2) return;
        int n = group.size();
        boolean[] excluded = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (excluded[i]) continue;
            for (int j = i + 1; j < n; j++) {
                if (!excluded[j] && isWhitelisted(whitelistPairs, group.get(i).getId(), group.get(j).getId())) {
                    excluded[j] = true;
                }
            }
        }
        List<Picture> filtered = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!excluded[i]) filtered.add(group.get(i));
        }
        group.clear();
        group.addAll(filtered);
    }

    public PictureDedupDTO toDedupDTO(Picture picture) {
        PictureDedupDTO dto = new PictureDedupDTO();
        dto.setId(picture.getId());
        dto.setName(picture.getName());
        dto.setUrl(picture.getUrl());
        dto.setSize(picture.getSize());
        dto.setWidth(picture.getWidth());
        dto.setHeight(picture.getHeight());
        dto.setCreateTime(picture.getCreateTime());
        int pixels = (picture.getWidth() != null && picture.getHeight() != null)
                ? picture.getWidth() * picture.getHeight() : 0;
        dto.setPixels(pixels);
        List<AlbumSimpleDTO> albumDTOs = picture.getAlbums().stream()
                .sorted(Comparator.comparing(Album::getDisplayOrder).thenComparing(Album::getCreateTime))
                .map(this::albumToSimpleDTO)
                .collect(Collectors.toList());
        dto.setAlbums(albumDTOs);
        return dto;
    }

    private AlbumSimpleDTO albumToSimpleDTO(Album album) {
        AlbumSimpleDTO dto = new AlbumSimpleDTO();
        dto.setId(album.getId());
        dto.setName(album.getName());
        dto.setCoverUrl(album.getCoverUrl());
        dto.setIsDefault(album.getIsDefault());
        dto.setDisplayOrder(album.getDisplayOrder());
        dto.setCreateTime(album.getCreateTime());
        return dto;
    }

    @Transactional
    public int applyRecommendations(DedupProcessRequest request, Long userId) {
        List<Long> allDeleteIds = new ArrayList<>();
        DedupResultDTO temp;
        try {
            temp = detectDuplicates(userId, request.getSimilarityLevel());
        } catch (IOException e) {
            throw new RuntimeException("检测失败: " + e.getMessage());
        }

        List<DuplicateGroupDTO> allGroups = new ArrayList<>();
        allGroups.addAll(temp.getDuplicateGroups());
        allGroups.addAll(temp.getSimilarGroups());

        Set<String> groupIdSet = request.getGroupIds() != null
                ? new HashSet<>(request.getGroupIds()) : null;

        for (DuplicateGroupDTO g : allGroups) {
            if (groupIdSet != null && !groupIdSet.contains(g.getGroupId())) continue;
            for (PictureDedupDTO p : g.getPictures()) {
                if (!Boolean.TRUE.equals(p.getRecommended())) {
                    allDeleteIds.add(p.getId());
                }
            }
        }

        return batchDeletePictures(allDeleteIds, userId);
    }

    @Transactional
    public int processSelections(DedupProcessRequest request, Long userId) {
        List<Long> allDeleteIds = new ArrayList<>();
        Set<String> groupIdSet = request.getGroupIds() != null
                ? new HashSet<>(request.getGroupIds()) : null;

        if (request.getKeepPictureIds() != null) {
            DedupResultDTO temp;
            try {
                temp = detectDuplicates(userId, request.getSimilarityLevel());
            } catch (IOException e) {
                throw new RuntimeException("检测失败: " + e.getMessage());
            }
            List<DuplicateGroupDTO> allGroups = new ArrayList<>();
            allGroups.addAll(temp.getDuplicateGroups());
            allGroups.addAll(temp.getSimilarGroups());
            Map<String, DuplicateGroupDTO> groupMap = new HashMap<>();
            for (DuplicateGroupDTO g : allGroups) groupMap.put(g.getGroupId(), g);

            for (Map.Entry<String, List<Long>> entry : request.getKeepPictureIds().entrySet()) {
                if (groupIdSet != null && !groupIdSet.contains(entry.getKey())) continue;
                DuplicateGroupDTO g = groupMap.get(entry.getKey());
                if (g == null) continue;
                Set<Long> keepSet = new HashSet<>(entry.getValue());
                for (PictureDedupDTO p : g.getPictures()) {
                    if (!keepSet.contains(p.getId())) {
                        allDeleteIds.add(p.getId());
                    }
                }
            }
        }

        return batchDeletePictures(allDeleteIds, userId);
    }

    @Transactional
    public int batchDeletePictures(List<Long> ids, Long userId) {
        int count = 0;
        Set<Long> processed = new HashSet<>();
        for (Long pid : ids) {
            if (processed.contains(pid)) continue;
            processed.add(pid);
            try {
                pictureService.deletePicture(pid, userId);
                count++;
            } catch (Exception ignored) {
            }
        }
        return count;
    }

    @Transactional
    public DedupWhitelist addToWhitelist(WhitelistAddRequest request, Long userId) {
        if (request.getPictureIds() != null && request.getPictureIds().size() >= 2) {
            for (int i = 0; i < request.getPictureIds().size(); i++) {
                for (int j = i + 1; j < request.getPictureIds().size(); j++) {
                    addPairToWhitelist(userId, request.getPictureIds().get(i), request.getPictureIds().get(j));
                }
            }
            return null;
        }
        if (request.getPictureId1() != null && request.getPictureId2() != null) {
            return addPairToWhitelist(userId, request.getPictureId1(), request.getPictureId2());
        }
        return null;
    }

    private DedupWhitelist addPairToWhitelist(Long userId, Long pid1, Long pid2) {
        DedupWhitelist existing = whitelistRepository.findByUserAndPicturePair(userId, pid1, pid2);
        if (existing != null) return existing;
        DedupWhitelist w = new DedupWhitelist();
        w.setUserId(userId);
        if (pid1 < pid2) {
            w.setPictureId1(pid1);
            w.setPictureId2(pid2);
        } else {
            w.setPictureId1(pid2);
            w.setPictureId2(pid1);
        }
        return whitelistRepository.save(w);
    }

    @Transactional
    public void removeFromWhitelist(Long whitelistId, Long userId) {
        DedupWhitelist w = whitelistRepository.findById(whitelistId).orElse(null);
        if (w != null && w.getUserId().equals(userId)) {
            whitelistRepository.delete(w);
        }
    }

    public List<WhitelistDTO> getWhitelist(Long userId) {
        List<DedupWhitelist> list = whitelistRepository.findAllByUserId(userId);
        Map<Long, Picture> pictureMap = new HashMap<>();
        List<Long> picIds = new ArrayList<>();
        for (DedupWhitelist w : list) {
            picIds.add(w.getPictureId1());
            picIds.add(w.getPictureId2());
        }
        if (!picIds.isEmpty()) {
            List<Picture> pics = pictureRepository.findAllById(picIds);
            for (Picture p : pics) pictureMap.put(p.getId(), p);
        }

        List<WhitelistDTO> result = new ArrayList<>();
        for (DedupWhitelist w : list) {
            WhitelistDTO dto = new WhitelistDTO();
            dto.setId(w.getId());
            dto.setPictureId1(w.getPictureId1());
            dto.setPictureId2(w.getPictureId2());
            dto.setCreateTime(w.getCreateTime());
            Picture p1 = pictureMap.get(w.getPictureId1());
            Picture p2 = pictureMap.get(w.getPictureId2());
            if (p1 != null) dto.setPicture1(toDedupDTO(p1));
            if (p2 != null) dto.setPicture2(toDedupDTO(p2));
            result.add(dto);
        }
        result.sort((a, b) -> {
            if (a.getCreateTime() == null || b.getCreateTime() == null) return 0;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });
        return result;
    }
}
