package dk.statsbiblioteket.doms.guiclient;

import java.util.List;

public class SearchResultList {
    private List<SearchResult> searchResults;
    private long hitCount;

    public SearchResultList() {
    }

    public SearchResultList(List<SearchResult> searchResults, long hitCount) {
        this.searchResults = searchResults;
        this.hitCount = hitCount;
    }

    public long getHitCount() {
        return hitCount;
    }

    public void setHitCount(long hitCount) {
        this.hitCount = hitCount;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }
}
