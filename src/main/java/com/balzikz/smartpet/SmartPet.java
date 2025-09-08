package com.balzikz.smartpet;

import com.balzikz.smartpet.command.PetCommand;
import com.balzikz.smartpet.manager.PetManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SmartPet extends JavaPlugin {

    private PetManager petManager;

    @Override
    public void onEnable() {
        this.petManager = new PetManager();

        this.getCommand("pet").setExecutor(new PetCommand(petManager));

        getLogger().info("Smart-Pet plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Removing all smart pets...");
        petManager.removeAllPets();
        getLogger().info("Smart-Pet plugin has been disabled!");
    }
}
