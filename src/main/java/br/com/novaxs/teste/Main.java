package br.com.novaxs.teste;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(8080);
        get("/", (req, res) -> "API Rick and Morty - Teste Novaxs Java");
    }
}
