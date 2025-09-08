package com.balzikz.smartpet.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class GeminiService {

    private final JavaPlugin plugin;
    private final String apiKey;
    private final Gson gson = new Gson();
    private final String API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=";

    public GeminiService(JavaPlugin plugin, String apiKey) {
        this.plugin = plugin;
        this.apiKey = apiKey;
    }

    public CompletableFuture<String> getResponse(String userPrompt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(API_ENDPOINT + apiKey);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String systemPrompt = "Ты - дружелюбный питомец-компаньон в игре Minecraft. Твоя задача - коротко и мило отвечать на сообщения игрока.";
                String finalPrompt = systemPrompt + "\n\nСообщение игрока: " + userPrompt;

                String jsonPayload = gson.toJson(new GeminiRequest(finalPrompt));

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream()
                ));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new RuntimeException("Gemini API error " + responseCode + ": " + response);
                }

                return extractTextFromResponse(response.toString());
            } catch (Exception e) {
                plugin.getLogger().severe("Error during Gemini API call: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    private String extractTextFromResponse(String jsonResponse) throws JsonSyntaxException {
        JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
        return root.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }

    private static class GeminiRequest {
        private final Content[] contents;
        public GeminiRequest(String text) {
            this.contents = new Content[]{ new Content(new Part[]{ new Part(text) }) };
        }
    }
    private static class Content {
        private final Part[] parts;
        public Content(Part[] parts) { this.parts = parts; }
    }
    private static class Part {
        private final String text;
        public Part(String text) { this.text = text; }
    }
}

