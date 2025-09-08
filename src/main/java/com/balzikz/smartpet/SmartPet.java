package com.balzikz.smartpet;

import com.yourname.smartpet.command.PetCommand;
import com.yourname.smartpet.manager.PetManager;
import com.yourname.smartpet.service.GeminiService;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmartPet extends JavaPlugin {

    private PetManager petManager;
    private GeminiService geminiService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String apiKey = getConfig().getString("gemini-api-key");
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("ВСТАВЬ_СВОЙ_API_КЛЮЧ_СЮДА")) {
            getLogger().warning("API ключ для Gemini не настроен в config.yml! Плагин не сможет общаться.");
        }

        this.petManager = new PetManager(this);
        this.geminiService = new GeminiService(this, apiKey);

        this.getCommand("pet").setExecutor(new PetCommand(petManager, geminiService));

        getLogger().info("Smart-Pet plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Removing all smart pets...");
        petManager.removeAllPets();
        getLogger().info("Smart-Pet plugin has been disabled!");
    }
}
