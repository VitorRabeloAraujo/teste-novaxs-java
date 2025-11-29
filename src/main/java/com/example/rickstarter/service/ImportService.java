package com.example.rickstarter.service;

import com.example.rickstarter.model.Character;
import com.example.rickstarter.model.Episode;
import com.example.rickstarter.model.SpeciesByEpisodeSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportService {

    private final RickAndMortyClient client;
    private final CharacterService characterService;
    private final EpisodeService episodeService;

    public ImportService(RickAndMortyClient client) {
        this.client = client;
        this.characterService = new CharacterService();
        this.episodeService = new EpisodeService();
    }

    public List<Character> importAllCharacters() throws Exception {
        return client.fetchAllCharacters();
    }

    public Episode importEpisode(String url) throws Exception {
        return client.fetchEpisodeByUrl(url);
    }

    public void saveCharacters(List<Character> characters) {
        characterService.saveAll(characters);
    }

    public void saveEpisodes(List<Episode> episodes) {
        episodeService.saveAll(episodes);
    }

    public List<SpeciesByEpisodeSummary> speciesByEpisode() {
        List<Episode> episodes = episodeService.findAll();
        List<Character> characters = characterService.findAll();

        Map<Long, SpeciesByEpisodeSummary> map = new LinkedHashMap<>();

        for (Episode episode : episodes) {
            if (episode.getId() == null) {
                continue;
            }
            SpeciesByEpisodeSummary summary = new SpeciesByEpisodeSummary();
            summary.setEpisodeId(episode.getId());
            summary.setName(episode.getName());
            summary.setSpeciesCount(new HashMap<>());
            map.put(episode.getId(), summary);
        }

        for (Character character : characters) {
            if (character.getEpisodes() == null) {
                continue;
            }
            String species = character.getSpecies();
            if (species == null || species.isBlank()) {
                species = "Unknown";
            }
            for (String episodeUrl : character.getEpisodes()) {
                Long episodeId = extractEpisodeIdFromUrl(episodeUrl);
                if (episodeId == null) {
                    continue;
                }
                SpeciesByEpisodeSummary summary = map.get(episodeId);
                if (summary == null) {
                    continue;
                }
                Map<String, Integer> speciesCount = summary.getSpeciesCount();
                Integer current = speciesCount.get(species);
                if (current == null) {
                    current = 0;
                }
                speciesCount.put(species, current + 1);
            }
        }

        return new ArrayList<>(map.values());
    }

    private Long extractEpisodeIdFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        int index = url.lastIndexOf('/');
        if (index == -1 || index == url.length() - 1) {
            return null;
        }
        String idPart = url.substring(index + 1);
        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}