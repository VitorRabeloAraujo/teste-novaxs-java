package com.example.rickstarter.controller;

import static spark.Spark.get;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.rickstarter.model.Character;
import com.example.rickstarter.model.Episode;
import com.example.rickstarter.model.SpeciesByEpisodeSummary;
import com.example.rickstarter.service.ImportService;
import com.example.rickstarter.util.JsonUtil;

public class ImportController {

    private final ImportService service;

    public ImportController(ImportService service) {
        this.service = service;
        setup();
    }

    private void setup() {
        get("/import", (req, res) -> {
            res.type("application/json");
            try {
                List<Character> characters = service.importAllCharacters();
                List<Episode> episodes = new ArrayList<>();

                for (Character character : characters) {
                    if (character.getEpisodes() == null) {
                        continue;
                    }
                    for (String episodeUrl : character.getEpisodes()) {
                        Episode episode = service.importEpisode(episodeUrl);
                        episodes.add(episode);
                    }
                }

                service.saveCharacters(characters);
                service.saveEpisodes(episodes);

                return JsonUtil.toJson(characters);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return JsonUtil.toJson(
                    Map.of("error", e.getMessage())
                );
            }
        });

        get("/speciesByEpisode", (req, res) -> {
            res.type("application/json");
            try {
                List<SpeciesByEpisodeSummary> summaries = service.speciesByEpisode();
                return JsonUtil.toJson(summaries);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return JsonUtil.toJson(
                    Map.of("error", e.getMessage())
                );
            }
        });
    }
}