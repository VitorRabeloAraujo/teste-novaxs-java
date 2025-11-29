package com.example.rickstarter.controller;

import com.example.rickstarter.model.Character;
import com.example.rickstarter.model.PageResponse;
import com.example.rickstarter.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static spark.Spark.*;

public class CharacterController {

    private final CharacterService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CharacterController() {
        this.service = new CharacterService();
        setup();
    }

    private void setup() {
        get("/personagem", (req, res) -> {
            int page = parseIntOrDefault(req.queryParams("page"), 0);
            int size = parseIntOrDefault(req.queryParams("size"), 10);
            PageResponse<Character> pageResult = service.list(page, size);
            res.type("application/json");
            return objectMapper.writeValueAsString(pageResult);
        });

        get("/personagem/:id", (req, res) -> {
            Long id = Long.valueOf(req.params("id"));
            return service.findById(id)
                    .map(character -> {
                        try {
                            res.type("application/json");
                            return objectMapper.writeValueAsString(character);
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

        post("/personagem", (req, res) -> {
            try {
                Character character = objectMapper.readValue(req.body(), Character.class);
                Character created = service.create(character);
                res.status(201);
                res.type("application/json");
                return objectMapper.writeValueAsString(created);
            } catch (Exception e) {
                res.status(400);
                return "";
            }
        });

        put("/personagem/:id", (req, res) -> {
            try {
                Long id = Long.valueOf(req.params("id"));
                Character character = objectMapper.readValue(req.body(), Character.class);
                return service.update(id, character)
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

        delete("/personagem/:id", (req, res) -> {
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