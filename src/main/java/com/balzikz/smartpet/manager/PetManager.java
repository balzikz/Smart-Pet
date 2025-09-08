package com.balzikz.smartpet.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetManager {

    private final JavaPlugin plugin;
    private final Map<UUID, LivingEntity> activePets = new HashMap<>();
    private final Map<UUID, String> petOriginalNames = new HashMap<>();

    public PetManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void createPet(Player owner, EntityType type) {
        if (activePets.containsKey(owner.getUniqueId())) {
            removePet(owner);
        }

        LivingEntity pet = (LivingEntity) owner.getWorld().spawnEntity(owner.getLocation(), type);

        pet.setInvulnerable(true);
        String originalName = ChatColor.AQUA + "Питомец " + owner.getName();
        pet.setCustomName(originalName);
        pet.setCustomNameVisible(true);

        if (pet instanceof Tameable) {
            ((Tameable) pet).setOwner(owner);
        }

        activePets.put(owner.getUniqueId(), pet);
        petOriginalNames.put(owner.getUniqueId(), originalName);
    }

    public void removePet(Player owner) {
        LivingEntity pet = activePets.remove(owner.getUniqueId());
        if (pet != null) {
            pet.remove();
        }
        petOriginalNames.remove(owner.getUniqueId());
    }

    public void removeAllPets() {
        activePets.values().forEach(pet -> {
            if (pet != null && pet.isValid()) {
                pet.remove();
            }
        });
        activePets.clear();
        petOriginalNames.clear();
    }
    
    public boolean hasPet(Player owner) {
        return activePets.containsKey(owner.getUniqueId());
    }

    public void makePetTalk(Player owner, String message) {
        LivingEntity pet = activePets.get(owner.getUniqueId());
        String originalName = petOriginalNames.get(owner.getUniqueId());
        
        if (pet == null || originalName == null || !pet.isValid()) {
            return;
        }

        pet.setCustomName(ChatColor.WHITE + message);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (pet.isValid()) {
                pet.setCustomName(originalName);
            }
        }, 100L);
    }
}
