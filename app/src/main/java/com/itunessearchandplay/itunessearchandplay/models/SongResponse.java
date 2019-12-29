package com.itunessearchandplay.itunessearchandplay.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Author: Andreas Pribitzer
 */

public class SongResponse {
    @SerializedName("results")
    private List<Song> results;

    @SerializedName("total_results")
    private int totalResults;

    public List<Song> getResults() {
        return results;
    }

    public void setResults(List<Song> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
