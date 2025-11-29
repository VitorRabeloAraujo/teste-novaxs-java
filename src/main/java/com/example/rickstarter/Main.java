package com.example.rickstarter;

import static spark.Spark.get;
import static spark.Spark.port;

import com.example.rickstarter.controller.CharacterController;
import com.example.rickstarter.controller.EpisodeController;
import com.example.rickstarter.controller.ImportController;
import com.example.rickstarter.service.ImportService;
import com.example.rickstarter.service.RickAndMortyClient;

public class Main {

    public static void main(String[] args) {
        port(8080);

        RickAndMortyClient client = new RickAndMortyClient();
        ImportService importService = new ImportService(client);

        new ImportController(importService);
        new CharacterController();
        new EpisodeController();

        get("/", (req, res) -> "API Rick and Morty - Teste Novaxs Java");
        get("/health", (req, res) -> "OK");

        System.out.println("Server started at http://localhost:8080");
    }
}