package com.loohp.interactivechat.objectholders;

import com.loohp.interactivechat.InteractiveChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ICPlayer extends OfflineICPlayer {

    public static final String LOCAL_SERVER_REPRESENTATION = "*local_server";
    public static final String EMPTY_SERVER_REPRESENTATION = "*invalid";
    private static final Inventory EMPTY_INVENTORY = Bukkit.createInventory(null, 54);
    private static final Inventory EMPTY_ENDERCHEST = Bukkit.createInventory(null, 18);
    private static final ICPlayerEquipment EMPTY_EQUIPMENT = new ICPlayerEquipment();

    private String remoteServer;
    private String remoteName;
    private boolean rightHanded;
    private Set<String> remoteNicknames;
    private Map<String, String> remotePlaceholders;

    protected ICPlayer(String server, String name, UUID uuid, boolean rightHanded, int selectedSlot, int experienceLevel, ICPlayerEquipment equipment, Inventory inventory, Inventory enderchest) {
        super(uuid, selectedSlot, rightHanded, experienceLevel, equipment, inventory, enderchest);
        this.remoteServer = server;
        this.remoteName = name;
        this.remoteNicknames = new HashSet<>();
        this.remotePlaceholders = new HashMap<>();
    }

    protected ICPlayer(Player player) {
        super(player.getUniqueId(), player.getInventory().getHeldItemSlot(), InteractiveChat.version.isOld() || player.getMainHand().equals(MainHand.RIGHT), player.getLevel(), EMPTY_EQUIPMENT, EMPTY_INVENTORY, EMPTY_ENDERCHEST);
        this.remoteServer = EMPTY_SERVER_REPRESENTATION;
        this.remoteName = player.getName();
        this.remoteNicknames = new HashSet<>();
        this.remotePlaceholders = new HashMap<>();
    }

    public boolean isLocal() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public boolean isValid() {
        return isLocal() || (remoteServer != null);
    }

    public Player getLocalPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getRemoteServer() {
        return remoteServer;
    }

    public void setRemoteServer(String server) {
        remoteServer = server;
    }

    public String getServer() {
        return isLocal() ? LOCAL_SERVER_REPRESENTATION : remoteServer;
    }

    @Override
    public String getName() {
        return isLocal() ? getLocalPlayer().getName() : remoteName;
    }

    public String getDisplayName() {
        return isLocal() ? getLocalPlayer().getDisplayName() : remoteName;
    }

    protected void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public boolean isRightHanded() {
        if (InteractiveChat.version.isOld()) {
            return true;
        } else {
            return isLocal() ? getLocalPlayer().getMainHand().name().equalsIgnoreCase("RIGHT") : rightHanded;
        }
    }

    public void setRemoteRightHanded(boolean rightHanded) {
        this.rightHanded = rightHanded;
    }

    protected void setRemoteEquipment(ICPlayerEquipment equipment) {
        this.remoteEquipment = equipment;
    }

    @Override
    public int getSelectedSlot() {
        return isLocal() ? getLocalPlayer().getInventory().getHeldItemSlot() : selectedSlot;
    }

    public void setRemoteSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    @Override
    public int getExperienceLevel() {
        return isLocal() ? getLocalPlayer().getLevel() : experienceLevel;
    }

    public void setRemoteExperienceLevel(int experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    @Override
    public EntityEquipment getEquipment() {
        return isLocal() ? getLocalPlayer().getEquipment() : remoteEquipment;
    }

    @Override
    public Inventory getInventory() {
        return isLocal() ? getLocalPlayer().getInventory() : remoteInventory;
    }

    public void setRemoteInventory(Inventory inventory) {
        remoteInventory = inventory;
    }

    @Override
    public ItemStack getMainHandItem() {
        return getInventory().getItem(getSelectedSlot());
    }

    @Override
    public ItemStack getOffHandItem() {
        return getInventory().getSize() > 40 ? getInventory().getItem(40) : null;
    }

    @Override
    public Inventory getEnderChest() {
        return isLocal() ? getLocalPlayer().getEnderChest() : remoteEnderchest;
    }

    public void setRemoteEnderChest(Inventory enderchest) {
        remoteEnderchest = enderchest;
    }

    public Set<String> getNicknames() {
        Set<String> nicknames = new HashSet<>(InteractiveChat.nicknameManager.getNicknames(uuid));
        if (!isLocal()) {
            nicknames.addAll(remoteNicknames);
        }
        return nicknames;
    }

    public Set<String> getRemoteNicknames() {
        return remoteNicknames;
    }

    public void setRemoteNicknames(Set<String> remoteNicknames) {
        this.remoteNicknames = remoteNicknames;
    }

    public Map<String, String> getRemotePlaceholdersMapping() {
        return remotePlaceholders;
    }

    @Override
    public ICPlayer getPlayer() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ICPlayer)) {
            return false;
        }
        ICPlayer other = (ICPlayer) obj;
        if (uuid == null) {
            return other.uuid == null;
        } else {
            return uuid.equals(other.uuid);
        }
    }

}
