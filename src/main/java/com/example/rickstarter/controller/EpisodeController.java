package com.example.rickstarter.controller;

import com.example.rickstarter.model.Episode;
import com.example.rickstarter.model.PageResponse;
import com.example.rickstarter.service.EpisodeService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class EpisodeController {

    private final EpisodeService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EpisodeController() {
        this.service = new EpisodeService();
        setup();
    }

    private void setup() {
        get("/episodio", (req, res) -> {
            int page = parseIntOrDefault(req.queryParams("page"), 0);
            int size = parseIntOrDefault(req.queryParams("size"), 10);
            PageResponse<Episode> pageResult = service.list(page, size);
            res.type("application/json");
            return objectMapper.writeValueAsString(pageResult);
        });

        get("/episodio/:id", (req, res) -> {
            Long id = Long.valueOf(req.params("id"));
            return service.findById(id)
                    .map(episode -> {
                        try {
                            res.type("application/json");
                            return objectMapper.writeValueAsString(episode);
                        } catch (Exception e) {
                            res.status(500);
                            return "";
                        }
                    })
                    .orElseGet(() -> {
                        res.status(404);
                        return "";
                    });
        });

        post("/episodio", (req, res) -> {
            try {
                Episode episode = objectMapper.readValue(req.body(), Episode.class);
                Episode created = service.create(episode);
                res.status(201);
                res.type("application/json");
                return objectMapper.writeValueAsString(created);
            } catch (Exception e) {
                res.status(400);
                return "";
            }
        });

        put("/episodio/:id", (req, res) -> {
            try {
                Long id = Long.valueOf(req.params("id"));
                Episode episode = objectMapper.readValue(req.body(), Episode.class);
                return service.update(id, episode)
                        .map(updated -> {
                            try {
                                res.type("application/json");
                                return objectMapper.writeValueAsString(updated);
                            } catch (Exception ex) {
                                res.status(500);
                                return "";
                            }
                        })
                        .orElseGet(() -> {
                            res.status(404);
                            return "";
                        });
            } catch (Exception e) {
                res.status(400);
                return "";
            }
        });

        delete("/episodio/:id", (req, res) -> {
            Long id = Long.valueOf(req.params("id"));
            boolean deleted = service.delete(id);
            if (deleted) {
                res.status(204);
                return "";
            } else {
                res.status(404);
                return "";
            }
        });
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            if (value == null) {
                return defaultValue;
            }
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}