package com.example.picture.service;

import com.example.picture.dto.*;
import com.example.picture.entity.*;
import com.example.picture.repository.*;
import com.example.picture.util.ImageHashUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private SavedSearchRepository savedSearchRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private InteractionService interactionService;

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<PictureDTO> search(SearchRequest request, Long userId) {
        saveSearchHistory(request.getKeyword(), request, userId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Picture> query = cb.createQuery(Picture.class);
        Root<Picture> picture = query.from(Picture.class);

        List<Predicate> predicates = buildPredicates(request, userId, cb, picture);

        query.select(picture).distinct(true).where(predicates.toArray(new Predicate[0]));

        applySorting(request, cb, query, picture);

        List<Picture> pictures = entityManager.createQuery(query)
                .setFirstResult(getOffset(request))
                .setMaxResults(getPageSize(request))
                .getResultList();

        return pictures.stream()
                .map(p -> pictureService.toDTO(p, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PictureDTO> searchByType(String keyword, String searchType, Long userId) {
        SearchRequest request = new SearchRequest();
        request.setKeyword(keyword);
        request.setSearchType(searchType);

        if ("tag".equalsIgnoreCase(searchType)) {
            return pictureRepository.findByTagNameContainingAndUserId(keyword, userId).stream()
                    .map(p -> pictureService.toDTO(p, userId))
                    .collect(Collectors.toList());
        } else if ("album".equalsIgnoreCase(searchType)) {
            return pictureRepository.findByAlbumNameContainingAndUserId(keyword, userId).stream()
                    .map(p -> pictureService.toDTO(p, userId))
                    .collect(Collectors.toList());
        } else if ("color".equalsIgnoreCase(searchType)) {
            return pictureRepository.findByDominantColorInAndUserId(Collections.singletonList(keyword), userId).stream()
                    .map(p -> pictureService.toDTO(p, userId))
                    .collect(Collectors.toList());
        } else if ("content".equalsIgnoreCase(searchType)) {
            return searchByContent(keyword, userId);
        } else {
            return search(request, userId);
        }
    }

    @Transactional(readOnly = true)
    public List<PictureDTO> searchByContent(String contentKeyword, Long userId) {
        return pictureRepository.findByTagNameContainingAndUserId(contentKeyword, userId).stream()
                .map(p -> pictureService.toDTO(p, userId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PictureDTO> findSimilarPictures(Long pictureId, Long userId) {
        Picture targetPicture = pictureRepository.findByIdWithDetails(pictureId, userId);
        if (targetPicture == null || targetPicture.getPerceptualHash() == null) {
            return Collections.emptyList();
        }

        String targetHash = targetPicture.getPerceptualHash();
        List<Picture> allPictures = pictureRepository.findByUserIdWithPerceptualHash(userId);

        List<PictureDTO> similarPictures = allPictures.stream()
                .filter(p -> !p.getId().equals(pictureId) && p.getPerceptualHash() != null)
                .map(p -> {
                    double similarity = ImageHashUtil.calculateSimilarity(targetHash, p.getPerceptualHash());
                    PictureDTO dto = pictureService.toDTO(p, userId);
                    dto.setSimilarity(similarity);
                    return dto;
                })
                .filter(dto -> dto.getSimilarity() >= 0.6)
                .sorted((d1, d2) -> Double.compare(d2.getSimilarity(), d1.getSimilarity()))
                .limit(20)
                .collect(Collectors.toList());

        return similarPictures;
    }

    @Transactional(readOnly = true)
    public List<SearchSuggestionDTO> getSuggestions(String keyword, Long userId) {
        List<SearchSuggestionDTO> suggestions = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            List<String> historyKeywords = searchHistoryRepository.findDistinctKeywordsByUserId(userId);
            suggestions.addAll(historyKeywords.stream()
                    .limit(5)
                    .map(k -> createSuggestion(k, "history", 0L))
                    .collect(Collectors.toList()));
            return suggestions;
        }

        String lowerKeyword = keyword.toLowerCase();

        List<Tag> matchingTags = tagRepository.findByNameContainingAndUserId(lowerKeyword, userId);
        suggestions.addAll(matchingTags.stream()
                .map(t -> createSuggestion(t.getName(), "tag", t.getReferenceCount().longValue()))
                .collect(Collectors.toList()));

        List<Album> matchingAlbums = albumRepository.findByNameContainingAndUserId(lowerKeyword, userId);
        suggestions.addAll(matchingAlbums.stream()
                .map(a -> createSuggestion(a.getName(), "album", pictureRepository.countByAlbumIdAndUserId(a.getId(), userId)))
                .collect(Collectors.toList()));

        List<SearchHistory> matchingHistory = searchHistoryRepository.findByUserIdAndKeywordContaining(userId, keyword);
        Set<String> historyTexts = new HashSet<>();
        for (SearchHistory h : matchingHistory) {
            if (!historyTexts.contains(h.getKeyword())) {
                historyTexts.add(h.getKeyword());
                suggestions.add(createSuggestion(h.getKeyword(), "history", 0L));
            }
        }

        return suggestions.stream()
                .sorted((s1, s2) -> Long.compare(s2.getCount(), s1.getCount()))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SearchHistoryDTO> getSearchHistory(Long userId) {
        return searchHistoryRepository.findByUserIdOrderBySearchTimeDesc(userId).stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void clearSearchHistory(Long userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<SavedSearchDTO> getSavedSearches(Long userId) {
        return savedSearchRepository.findByUserIdOrderByCreateTimeDesc(userId).stream()
                .map(this::toSavedSearchDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SavedSearchDTO saveSearch(String name, String searchParams, Long userId) {
        SavedSearch savedSearch = new SavedSearch();
        savedSearch.setUserId(userId);
        savedSearch.setName(name);
        savedSearch.setSearchParams(searchParams);
        SavedSearch saved = savedSearchRepository.save(savedSearch);
        return toSavedSearchDTO(saved);
    }

    @Transactional
    public void deleteSavedSearch(Long id, Long userId) {
        savedSearchRepository.deleteByIdAndUserId(id, userId);
    }

    @Transactional(readOnly = true)
    public List<PictureDTO> executeSavedSearch(Long id, Long userId) {
        SavedSearch savedSearch = savedSearchRepository.findByIdAndUserId(id, userId).orElse(null);
        if (savedSearch == null) {
            return Collections.emptyList();
        }

        try {
            SearchRequest request = objectMapper.readValue(savedSearch.getSearchParams(), new TypeReference<SearchRequest>() {});
            return search(request, userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Predicate> buildPredicates(SearchRequest request, Long userId, CriteriaBuilder cb, Root<Picture> picture) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(picture.get("userId"), userId));
        predicates.add(cb.isFalse(picture.get("deleted")));

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String keyword = "%" + request.getKeyword().toLowerCase() + "%";
            Predicate namePredicate = cb.like(cb.lower(picture.get("name")), keyword);

            Join<Picture, Tag> tagJoin = picture.join("tags", JoinType.LEFT);
            Predicate tagPredicate = cb.like(cb.lower(tagJoin.get("name")), keyword);

            Join<Picture, Album> albumJoin = picture.join("albums", JoinType.LEFT);
            Predicate albumPredicate = cb.like(cb.lower(albumJoin.get("name")), keyword);

            predicates.add(cb.or(namePredicate, tagPredicate, albumPredicate));
        }

        if (request.getAlbumIds() != null && !request.getAlbumIds().isEmpty()) {
            Join<Picture, Album> albumJoin = picture.join("albums");
            predicates.add(albumJoin.get("id").in(request.getAlbumIds()));
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Join<Picture, Tag> tagJoin = picture.join("tags");
            predicates.add(tagJoin.get("id").in(request.getTagIds()));
        }

        if (request.getColors() != null && !request.getColors().isEmpty()) {
            predicates.add(picture.get("dominantColor").in(request.getColors()));
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            predicates.add(cb.between(picture.get("shootTime"), request.getStartDate(), request.getEndDate()));
        } else if (request.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(picture.get("shootTime"), request.getStartDate()));
        } else if (request.getEndDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(picture.get("shootTime"), request.getEndDate()));
        }

        if (request.getMinWidth() != null && request.getMaxWidth() != null) {
            predicates.add(cb.between(picture.get("width"), request.getMinWidth(), request.getMaxWidth()));
        } else if (request.getMinWidth() != null) {
            predicates.add(cb.greaterThanOrEqualTo(picture.get("width"), request.getMinWidth()));
        } else if (request.getMaxWidth() != null) {
            predicates.add(cb.lessThanOrEqualTo(picture.get("width"), request.getMaxWidth()));
        }

        if (request.getMinHeight() != null && request.getMaxHeight() != null) {
            predicates.add(cb.between(picture.get("height"), request.getMinHeight(), request.getMaxHeight()));
        } else if (request.getMinHeight() != null) {
            predicates.add(cb.greaterThanOrEqualTo(picture.get("height"), request.getMinHeight()));
        } else if (request.getMaxHeight() != null) {
            predicates.add(cb.lessThanOrEqualTo(picture.get("height"), request.getMaxHeight()));
        }

        if (request.getMinSize() != null && request.getMaxSize() != null) {
            predicates.add(cb.between(picture.get("size"), request.getMinSize(), request.getMaxSize()));
        } else if (request.getMinSize() != null) {
            predicates.add(cb.greaterThanOrEqualTo(picture.get("size"), request.getMinSize()));
        } else if (request.getMaxSize() != null) {
            predicates.add(cb.lessThanOrEqualTo(picture.get("size"), request.getMaxSize()));
        }

        if (Boolean.TRUE.equals(request.getHasLocation())) {
            predicates.add(cb.isTrue(picture.get("hasLocation")));
        }

        return predicates;
    }

    private void applySorting(SearchRequest request, CriteriaBuilder cb, CriteriaQuery<Picture> query, Root<Picture> picture) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "relevance";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "desc";

        Order order;
        switch (sortBy.toLowerCase()) {
            case "time":
                order = "asc".equalsIgnoreCase(sortOrder)
                        ? cb.asc(picture.get("createTime"))
                        : cb.desc(picture.get("createTime"));
                break;
            case "size":
                order = "asc".equalsIgnoreCase(sortOrder)
                        ? cb.asc(picture.get("size"))
                        : cb.desc(picture.get("size"));
                break;
            case "likes":
                Subquery<Long> likeCountSubquery = query.subquery(Long.class);
                Root<Like> likeRoot = likeCountSubquery.from(Like.class);
                likeCountSubquery.select(cb.count(likeRoot))
                        .where(cb.equal(likeRoot.get("pictureId"), picture.get("id")));
                order = "asc".equalsIgnoreCase(sortOrder)
                        ? cb.asc(likeCountSubquery)
                        : cb.desc(likeCountSubquery);
                break;
            case "relevance":
            default:
                order = cb.desc(picture.get("createTime"));
                break;
        }

        query.orderBy(order);
    }

    private int getOffset(SearchRequest request) {
        int page = request.getPage() != null ? request.getPage() : 0;
        int pageSize = getPageSize(request);
        return page * pageSize;
    }

    private int getPageSize(SearchRequest request) {
        return request.getPageSize() != null ? request.getPageSize() : 50;
    }

    private void saveSearchHistory(String keyword, SearchRequest request, Long userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        try {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setKeyword(keyword);
            history.setSearchParams(objectMapper.writeValueAsString(request));
            searchHistoryRepository.save(history);
        } catch (Exception ignored) {
        }
    }

    private SearchSuggestionDTO createSuggestion(String text, String type, Long count) {
        SearchSuggestionDTO dto = new SearchSuggestionDTO();
        dto.setText(text);
        dto.setType(type);
        dto.setCount(count);
        return dto;
    }

    private SearchHistoryDTO toHistoryDTO(SearchHistory history) {
        SearchHistoryDTO dto = new SearchHistoryDTO();
        dto.setId(history.getId());
        dto.setKeyword(history.getKeyword());
        dto.setSearchParams(history.getSearchParams());
        dto.setSearchTime(history.getSearchTime());
        return dto;
    }

    private SavedSearchDTO toSavedSearchDTO(SavedSearch savedSearch) {
        SavedSearchDTO dto = new SavedSearchDTO();
        dto.setId(savedSearch.getId());
        dto.setName(savedSearch.getName());
        dto.setSearchParams(savedSearch.getSearchParams());
        dto.setCreateTime(savedSearch.getCreateTime());
        dto.setUpdateTime(savedSearch.getUpdateTime());
        return dto;
    }
}
