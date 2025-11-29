package com.example.rickstarter.model;

import com.google.gson.annotations.SerializedName;

public class Episode {

    private Long id;
    private String name;

    @SerializedName("air_date")
    private String airDate;

    @SerializedName("episode")
    private String episodeCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAirDate() {
        return airDate;
    }

    public void setAirDate(String airDate) {
        this.airDate = airDate;
    }

    public String getEpisodeCode() {
        return episodeCode;
    }

    public void setEpisodeCode(String episodeCode) {
        this.episodeCode = episodeCode;
    }
}