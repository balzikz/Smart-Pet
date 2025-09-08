package com.yourname.smartpet.manager;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetManager {

    private final Map<UUID, LivingEntity> activePets = new HashMap<>();

    public void createPet(Player owner, EntityType type) {
        if (activePets.containsKey(owner.getUniqueId())) {
            removePet(owner);
        }

        LivingEntity pet = (LivingEntity) owner.getWorld().spawnEntity(owner.getLocation(), type);

        pet.setInvulnerable(true);
        pet.setCustomName(ChatColor.AQUA + "Питомец " + owner.getName());
        pet.setCustomNameVisible(true);

        if (pet instanceof Tameable) {
            Tameable tameablePet = (Tameable) pet;
            tameablePet.setOwner(owner);
        }
        activePets.put(owner.getUniqueId(), pet);
    }

    public void removePet(Player owner) {
        LivingEntity pet = activePets.get(owner.getUniqueId());
        if (pet != null) {
            pet.remove();
            activePets.remove(owner.getUniqueId());
        }
    }

    public void removeAllPets() {
        for (LivingEntity pet : activePets.values()) {
            if (pet != null && pet.isValid()) {
                pet.remove();
            }
        }
        activePets.clear();
    }
}
