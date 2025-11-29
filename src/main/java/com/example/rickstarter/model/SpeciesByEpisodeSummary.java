package com.example.rickstarter.model;

import java.util.Map;

public class SpeciesByEpisodeSummary {

    private Long episodeId;
    private String name;
    private Map<String, Integer> speciesCount;

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getSpeciesCount() {
        return speciesCount;
    }

    public void setSpeciesCount(Map<String, Integer> speciesCount) {
        this.speciesCount = speciesCount;
    }
}