package me.head_block.xpbank.utils;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import me.head_block.xpbank.Main;

public class XpBPlayer implements Player {

	private Player player;
	
	public XpBPlayer(Player p) {
		this.player = p;
		this.checkBalInstance();
	}
	
	@Override
	public void sendMessage(String message) {
		if (player != null) player.sendMessage(message);
	}
	
	public void sendPlaceholderMessage(String message) {
		if (player != null) sendMessage(Utils.replacePlaceholders(message, player));
	}
	
	public int getStoredXp() {
		if (player != null) return Main.xps.get(player.getUniqueId().toString());
		else return -1;
	}
	
	public void setStoredXp(int xp) {
		if (player != null) Main.xps.put(player.getUniqueId().toString(), xp);
	}
	
	public int totalXp() {
		if (player != null) return Utils.totalXp(player);
		else return -1;
	}
	
	public boolean hasPermission(String perm) {
		if (player != null) return player.hasPermission(perm);
		else return false;
	}
	
	public UUID getUniqueId() {
		if (player != null) return player.getUniqueId();
		else return null;
	}
	
	public void checkBalInstance() {
		if (!Main.xps.containsKey(player.getUniqueId().toString())) {
			Main.xps.put(player.getUniqueId().toString(), 0);
		}
	}
	
	public void addXp(int xp) {
		if (player != null) {
			int newTotal = Utils.totalXp(player) + xp;
			int newLevel = Utils.level(newTotal);
			float newXp = Utils.xp(newTotal, newLevel);
			
			if (newXp == 1) {
				newLevel++;
				newXp = 0;
			}
			player.setLevel(newLevel);
			player.setExp(newXp);
		}
	}
	
	public void removeXp(int xp) {
		if (player != null) {
			int newTotal = Utils.totalXp(player) - xp;
			int newLevel = Utils.level(newTotal);
			float newXp = Utils.xp(newTotal, newLevel);
			if (newXp == 1) {
				newLevel++;
				newXp = 0;
			}
			player.setLevel(newLevel);
			player.setExp(newXp);
		}
	}
	
	public void addLevels(int numLevels) {
		if (player != null) {
			int xpToAdd = Utils.totalXp(player.getLevel() + numLevels) - Utils.totalXp(player.getLevel());
			int newTotal = Utils.totalXp(player) + xpToAdd;
			int newLevel = Utils.level(newTotal);
			float newXp = Utils.xp(newTotal, newLevel);
			if (newXp == 1) {
				newLevel++;
				newXp = 0;
			}
			player.setLevel(newLevel);
			player.setExp(newXp);
		}
	}
	
	public void removeLevels(int numLevels) {
		if (player != null) {
			int xpToLose = Utils.totalXp(player.getLevel()) - Utils.totalXp(player.getLevel() - numLevels);
			int newTotal = Utils.totalXp(player) - xpToLose;
			int newLevel = Utils.level(newTotal);
			float newXp = Utils.xp(newTotal, newLevel);
			if (newXp == 1) {
				newLevel++;
				newXp = 0;
			}
			player.setLevel(newLevel);
			player.setExp(newXp);
		}
	}

	
	/*
	 * Implemented Player methods
	 */
	
	@Override
	public void closeInventory() {
		player.closeInventory();
	}

	@Override
	public Inventory getEnderChest() {
		return player.getEnderChest();
	}

	@Override
	public float getExhaustion() {
		return player.getExhaustion();
	}

	@Override
	public int getExpToLevel() {
		return player.getExpToLevel();
	}

	@Override
	public int getFoodLevel() {
		return player.getFoodLevel();
	}

	@Override
	public GameMode getGameMode() {
		return player.getGameMode();
	}

	@Override
	public PlayerInventory getInventory() {
		return player.getInventory();
	}

	@Override
	@Deprecated
	public ItemStack getItemInHand() {
		return player.getItemInHand();
	}

	@Override
	public ItemStack getItemOnCursor() {
		return player.getItemOnCursor();
	}

	@Override
	public InventoryView getOpenInventory() {
		return player.getOpenInventory();
	}

	@Override
	public float getSaturation() {
		return player.getSaturation();
	}

	@Override
	public int getSleepTicks() {
		return player.getSleepTicks();
	}

	@Override
	public boolean isBlocking() {
		return player.isBlocking();
	}

	@Override
	public InventoryView openEnchanting(Location arg0, boolean arg1) {
		return player.openEnchanting(arg0, arg1);
	}

	@Override
	public InventoryView openInventory(Inventory arg0) {
		return player.openInventory(arg0);
	}

	@Override
	public void openInventory(InventoryView arg0) {
		player.openInventory(arg0);
	}

	@Override
	public InventoryView openWorkbench(Location arg0, boolean arg1) {
		return player.openWorkbench(arg0, arg1);
	}

	@Override
	public void setExhaustion(float arg0) {
		player.setExhaustion(arg0);
	}

	@Override
	public void setFoodLevel(int arg0) {
		player.setFoodLevel(arg0);
	}

	@Override
	public void setGameMode(GameMode arg0) {
		player.setGameMode(arg0);
	}

	@Override
	@Deprecated
	public void setItemInHand(ItemStack arg0) {
		player.setItemInHand(arg0);
	}

	@Override
	public void setItemOnCursor(ItemStack arg0) {
		player.setItemOnCursor(arg0);
	}

	@Override
	public void setSaturation(float arg0) {
		player.setSaturation(arg0);
	}

	@Override
	public boolean setWindowProperty(Property arg0, int arg1) {
		return player.setWindowProperty(arg0, arg1);
	}

	@Override
	public boolean addPotionEffect(PotionEffect arg0) {
		return player.addPotionEffect(arg0);
	}

	@Override
	@Deprecated
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
		return player.addPotionEffect(arg0, arg1);
	}

	@Override
	public boolean addPotionEffects(Collection<PotionEffect> arg0) {
		return player.addPotionEffects(arg0);
	}

	@Override
	public Collection<PotionEffect> getActivePotionEffects() {
		return player.getActivePotionEffects();
	}

	@Override
	public boolean getCanPickupItems() {
		return player.getCanPickupItems();
	}

	@Override
	public EntityEquipment getEquipment() {
		return player.getEquipment();
	}

	@Override
	public double getEyeHeight() {
		return player.getEyeHeight();
	}

	@Override
	public double getEyeHeight(boolean arg0) {
		return player.getEyeHeight(arg0);
	}

	@Override
	public Location getEyeLocation() {
		return player.getEyeLocation();
	}

	@Override
	public Player getKiller() {
		return player.getKiller();
	}

	@Override
	public double getLastDamage() {
		return player.getLastDamage();
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(Set<Material> arg0, int arg1) {
		return player.getLastTwoTargetBlocks(arg0, arg1);
	}

	@Override
	public Entity getLeashHolder() throws IllegalStateException {
		return player.getLeashHolder();
	}

	@Override
	public List<Block> getLineOfSight(Set<Material> arg0, int arg1) {
		return player.getLineOfSight(arg0, arg1);
	}

	@Override
	public int getMaximumAir() {
		return player.getMaximumAir();
	}

	@Override
	public int getMaximumNoDamageTicks() {
		return player.getMaximumNoDamageTicks();
	}

	@Override
	public int getNoDamageTicks() {
		return player.getNoDamageTicks();
	}

	@Override
	public int getRemainingAir() {
		return player.getRemainingAir();
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return player.getRemoveWhenFarAway();
	}

	@Override
	public Block getTargetBlock(Set<Material> arg0, int arg1) {
		return player.getTargetBlock(arg0, arg1);
	}


	@Override
	public boolean hasLineOfSight(Entity arg0) {
		return player.hasLineOfSight(arg0);
	}
		
	@Override
	public boolean hasPotionEffect(PotionEffectType arg0) {
		return player.hasPotionEffect(arg0);
	}

	@Override
	public boolean isLeashed() {
		return player.isLeashed();
	}

	@Override
	public boolean isSleeping() {
		return player.isSleeping();
	}

	@Override
	public void removePotionEffect(PotionEffectType arg0) {
		player.removePotionEffect(arg0);
		
	}

	@Override
	public void setCanPickupItems(boolean arg0) {
		player.setCanPickupItems(arg0);
	}

	@Override
	public void setLastDamage(double arg0) {
		player.setLastDamage(arg0);
	}

	@Override
	public boolean setLeashHolder(Entity arg0) {
		return player.setLeashHolder(arg0);
	}

	@Override
	public void setMaximumAir(int arg0) {
		player.setMaximumAir(arg0);
	}

	@Override
	public void setMaximumNoDamageTicks(int arg0) {
		player.setMaximumNoDamageTicks(arg0);
	}

	@Override
	public void setNoDamageTicks(int arg0) {
		player.setNoDamageTicks(arg0);
	}

	@Override
	public void setRemainingAir(int arg0) {
		player.setRemainingAir(arg0);
	}

	@Override
	public void setRemoveWhenFarAway(boolean arg0) {
		player.setRemoveWhenFarAway(arg0);
	}

	@Override
	public void damage(double arg0) {
		player.damage(arg0);
	}

	@Override
	public void damage(double arg0, Entity arg1) {
		player.damage(arg0, arg1);
	}

	@Override
	public double getHealth() {
		return player.getHealth();
	}

	@Override
	@Deprecated
	public double getMaxHealth() {
		return player.getMaxHealth();
	}

	@Override
	@Deprecated
	public void resetMaxHealth() {
		player.resetMaxHealth();
	}

	@Override
	public void setHealth(double arg0) {
		player.setHealth(arg0);
	}

	@Override
	@Deprecated
	public void setMaxHealth(double arg0) {
		player.setMaxHealth(arg0);
	}

	@Override
	public boolean eject() {
		return player.eject();
	}

	@Override
	public int getEntityId() {
		return player.getEntityId();
	}

	@Override
	public float getFallDistance() {
		return player.getFallDistance();
	}

	@Override
	public int getFireTicks() {
		return player.getFireTicks();
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return player.getLastDamageCause();
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public Location getLocation(Location arg0) {
		return player.getLocation(arg0);
	}

	@Override
	public int getMaxFireTicks() {
		return player.getMaxFireTicks();
	}

	@Override
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
		return player.getNearbyEntities(arg0, arg1, arg2);
	}

	@Override
	@Deprecated
	public Entity getPassenger() {
		return player.getPassenger();
	}

	@Override
	public Server getServer() {
		return player.getServer();
	}

	@Override
	public int getTicksLived() {
		return player.getTicksLived();
	}

	@Override
	public EntityType getType() {
		return player.getType();
	}

	@Override
	public Entity getVehicle() {
		return player.getVehicle();
	}

	@Override
	public Vector getVelocity() {
		return player.getVelocity();
	}

	@Override
	public World getWorld() {
		return player.getWorld();
	}

	@Override
	public boolean isCustomNameVisible() {
		return player.isCustomNameVisible();
	}

	@Override
	public boolean isDead() {
		return player.isDead();
	}

	@Override
	public boolean isEmpty() {
		return player.isEmpty();
	}

	@Override
	public boolean isInsideVehicle() {
		return player.isInsideVehicle();
	}

	@Override
	public boolean isValid() {
		return player.isValid();
	}

	@Override
	public boolean leaveVehicle() {
		return player.leaveVehicle();
	}

	@Override
	public void playEffect(EntityEffect arg0) {
		player.playEffect(arg0);
	}

	@Override
	public void remove() {
		player.remove();		
	}

	@Override
	public void setCustomNameVisible(boolean arg0) {
		player.setCustomNameVisible(arg0);
	}

	@Override
	public void setFallDistance(float arg0) {
		player.setFallDistance(arg0);
	}

	@Override
	public void setFireTicks(int arg0) {
		player.setFireTicks(arg0);
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent arg0) {
		player.setLastDamageCause(arg0);		
	}

	@Override
	@Deprecated
	public boolean setPassenger(Entity arg0) {
		return player.setPassenger(arg0);
	}

	@Override
	public void setTicksLived(int arg0) {
		player.setTicksLived(arg0);		
	}

	@Override
	public void setVelocity(Vector arg0) {
		player.setVelocity(arg0);		
	}

	@Override
	public boolean teleport(Location arg0) {
		return player.teleport(player);
	}

	@Override
	public boolean teleport(Entity arg0) {
		return player.teleport(arg0);
	}

	@Override
	public boolean teleport(Location arg0, TeleportCause arg1) {
		return player.teleport(arg0, arg1);
	}

	@Override
	public boolean teleport(Entity arg0, TeleportCause arg1) {
		return player.teleport(arg0, arg1);
	}

	@Override
	public List<MetadataValue> getMetadata(String arg0) {
		return player.getMetadata(arg0);
	}

	@Override
	public boolean hasMetadata(String arg0) {
		return player.hasMetadata(arg0);
	}

	@Override
	public void removeMetadata(String arg0, Plugin arg1) {
		player.removeMetadata(arg0, arg1);		
	}

	@Override
	public void setMetadata(String arg0, MetadataValue arg1) {
		player.setMetadata(arg0, arg1);		
	}

	@Override
	public void sendMessage(String... arg0) {
		player.sendMessage(arg0);		
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return player.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return player.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return player.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return player.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return player.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return player.hasPermission(arg0);
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return player.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return player.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		player.recalculatePermissions();		
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		player.removeAttachment(arg0);
	}

	@Override
	public boolean isOp() {
		return player.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		player.setOp(arg0);		
	}

	@Override
	public String getCustomName() {
		return player.getCustomName();
	}

	@Override
	public void setCustomName(String arg0) {
		player.setCustomName(arg0);		
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
		return player.launchProjectile(arg0);
	}

	@Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0, Vector arg1) {
		return player.launchProjectile(arg0, arg1);
	}

	@Override
	public void abandonConversation(Conversation arg0) {
		player.abandonConversation(arg0);		
	}

	@Override
	public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
		player.abandonConversation(arg0, arg1);		
	}

	@Override
	public void acceptConversationInput(String arg0) {
		player.acceptConversationInput(arg0);		
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		return player.beginConversation(arg0);
	}

	@Override
	public boolean isConversing() {
		return player.isConversing();
	}

	@Override
	public void decrementStatistic(Statistic arg0) throws IllegalArgumentException {
		player.decrementStatistic(arg0);		
	}

	@Override
	public void decrementStatistic(Statistic arg0, int arg1) throws IllegalArgumentException {
		player.decrementStatistic(arg0, arg1);		
	}

	@Override
	public void decrementStatistic(Statistic arg0, Material arg1) throws IllegalArgumentException {
		player.decrementStatistic(arg0, arg1);		
	}

	@Override
	public void decrementStatistic(Statistic arg0, EntityType arg1) throws IllegalArgumentException {
		player.decrementStatistic(arg0, arg1);		
	}

	@Override
	public void decrementStatistic(Statistic arg0, Material arg1, int arg2) throws IllegalArgumentException {
		player.decrementStatistic(arg0, arg1, arg2);		
	}

	@Override
	public void decrementStatistic(Statistic arg0, EntityType arg1, int arg2) {
		player.decrementStatistic(arg0, arg1, arg2);
	}

	@Override
	public long getFirstPlayed() {
		return player.getFirstPlayed();
	}

	@Override
	public long getLastPlayed() {
		return player.getLastPlayed();
	}

	@Override
	public Player getPlayer() {
		return player.getPlayer();
	}

	@Override
	public int getStatistic(Statistic arg0) throws IllegalArgumentException {
		return player.getStatistic(arg0);
	}

	@Override
	public int getStatistic(Statistic arg0, Material arg1) throws IllegalArgumentException {
		return player.getStatistic(arg0, arg1);
	}

	@Override
	public int getStatistic(Statistic arg0, EntityType arg1) throws IllegalArgumentException {
		return player.getStatistic(arg0, arg1);
	}

	@Override
	public boolean hasPlayedBefore() {
		return player.hasPlayedBefore();
	}

	@Override
	public void incrementStatistic(Statistic arg0) throws IllegalArgumentException {
		player.incrementStatistic(arg0);		
	}

	@Override
	public void incrementStatistic(Statistic arg0, int arg1) throws IllegalArgumentException {
		player.incrementStatistic(arg0, arg1);	
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1) throws IllegalArgumentException {
		player.incrementStatistic(arg0, arg1);
		
	}

	@Override
	public void incrementStatistic(Statistic arg0, EntityType arg1) throws IllegalArgumentException {
		player.incrementStatistic(arg0, arg1);
		
	}

	@Override
	public void incrementStatistic(Statistic arg0, Material arg1, int arg2) throws IllegalArgumentException {
		player.incrementStatistic(arg0, arg1, arg2);
		
	}

	@Override
	public void incrementStatistic(Statistic arg0, EntityType arg1, int arg2) throws IllegalArgumentException {
		player.incrementStatistic(arg0, arg1, arg2);
	}

	@Override
	public boolean isBanned() {
		return player.isBanned();
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
	}

	@Override
	public boolean isWhitelisted() {
		return player.isWhitelisted();
	}

	@Override
	public void setStatistic(Statistic arg0, int arg1) throws IllegalArgumentException {
		player.setStatistic(arg0, arg1);		
	}

	@Override
	public void setStatistic(Statistic arg0, Material arg1, int arg2) throws IllegalArgumentException {
		player.setStatistic(arg0, arg1, arg2);		
	}

	@Override
	public void setStatistic(Statistic arg0, EntityType arg1, int arg2) {
		player.setStatistic(arg0, arg1, arg2);
	}

	@Override
	public void setWhitelisted(boolean arg0) {
		player.setWhitelisted(arg0);		
	}

	@Override
	public Map<String, Object> serialize() {
		return player.serialize();
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return player.getListeningPluginChannels();
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
		player.sendPluginMessage(arg0, arg1, arg2);
		
	}

	@Override
	public boolean canSee(Player arg0) {
		return player.canSee(arg0);
	}

	@Override
	public void chat(String arg0) {
		player.chat(arg0);		
	}

	@Override
	public InetSocketAddress getAddress() {
		return player.getAddress();
	}

	@Override
	public boolean getAllowFlight() {
		return player.getAllowFlight();
	}

	@Override
	public Location getBedSpawnLocation() {
		return player.getBedSpawnLocation();
	}

	@Override
	public Location getCompassTarget() {
		return player.getCompassTarget();
	}

	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}

	@Override
	public float getExp() {
		return player.getExp();
	}

	@Override
	public float getFlySpeed() {
		return player.getFlySpeed();
	}

	@Override
	public double getHealthScale() {
		return player.getHealthScale();
	}

	@Override
	public int getLevel() {
		return player.getLevel();
	}

	@Override
	public String getName() {
		return player.getName();
	}

	@Override
	public String getPlayerListName() {
		return player.getPlayerListName();
	}

	@Override
	public long getPlayerTime() {
		return player.getPlayerTime();
	}

	@Override
	public long getPlayerTimeOffset() {
		return player.getPlayerTimeOffset();
	}

	@Override
	public WeatherType getPlayerWeather() {
		return player.getPlayerWeather();
	}

	@Override
	public Scoreboard getScoreboard() {
		return player.getScoreboard();
	}

	@Override
	public Entity getSpectatorTarget() {
		return player.getSpectatorTarget();
	}

	@Override
	public int getTotalExperience() {
		return player.getTotalExperience();
	}

	@Override
	public float getWalkSpeed() {
		return player.getWalkSpeed();
	}

	@Override
	public void giveExp(int arg0) {
		player.giveExp(arg0);
		
	}

	@Override
	public void giveExpLevels(int arg0) {
		player.giveExpLevels(arg0);
		
	}

	@Override
	@Deprecated
	public void hidePlayer(Player arg0) {
		player.hidePlayer(arg0);		
	}

	@Override
	public boolean isFlying() {		
		return player.isFlying();
	}

	@Override
	public boolean isHealthScaled() {
		return player.isHealthScaled();
	}

	@Override
	@Deprecated
	public boolean isOnGround() {
		return player.isOnGround();
	}

	@Override
	public boolean isPlayerTimeRelative() {
		return player.isPlayerTimeRelative();
	}

	@Override
	public boolean isSleepingIgnored() {
		return player.isSleepingIgnored();
	}

	@Override
	public boolean isSneaking() {
		return player.isSneaking();
	}

	@Override
	public boolean isSprinting() {
		return player.isSprinting();
	}

	@Override
	public void kickPlayer(String arg0) {
		player.kickPlayer(arg0);
		
	}

	@Override
	public void loadData() {
		player.loadData();		
	}

	@Override
	public boolean performCommand(String arg0) {
		return player.performCommand(arg0);
	}

	@Override
	@Deprecated
	public void playEffect(Location arg0, Effect arg1, int arg2) {
		player.playEffect(arg0, arg1, arg2);		
	}

	@Override
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
		player.playEffect(arg0, arg1, arg2);
	}

	@Override
	@Deprecated
	public void playNote(Location arg0, byte arg1, byte arg2) {
		player.playNote(arg0, arg1, arg2);		
	}

	@Override
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
		player.playNote(arg0, arg1, arg2);		
	}

	@Override
	public void playSound(Location arg0, Sound arg1, float arg2, float arg3) {
		player.playSound(arg0, arg1, arg2, arg3);		
	}

	@Override
	public void playSound(Location arg0, String arg1, float arg2, float arg3) {
		player.playSound(arg0, arg1, arg2, arg3);		
	}

	@Override
	public void resetPlayerTime() {
		player.resetPlayerTime();		
	}

	@Override
	public void resetPlayerWeather() {
		player.resetPlayerWeather();		
	}

	@Override
	public void resetTitle() {
		player.resetTitle();		
	}

	@Override
	public void saveData() {
		player.saveData();		
	}

	@Override
	@Deprecated
	public void sendBlockChange(Location arg0, Material arg1, byte arg2) {
		player.sendBlockChange(arg0, arg1, arg2);		
	}

	@Override
	public void sendMap(MapView arg0) {
		player.sendMap(arg0);		
	}

	@Override
	public void sendRawMessage(String arg0) {
		player.sendRawMessage(arg0);		
	}

	@Override
	public void sendSignChange(Location arg0, String[] arg1) throws IllegalArgumentException {
		player.sendSignChange(arg0, arg1);		
	}

	@Override
	@Deprecated
	public void sendTitle(String arg0, String arg1) {
		player.sendTitle(arg0, arg1);		
	}

	@Override
	public void setAllowFlight(boolean arg0) {
		player.setAllowFlight(arg0);		
	}

	@Override
	public void setBedSpawnLocation(Location arg0) {
		player.setBedSpawnLocation(arg0);		
	}

	@Override
	public void setBedSpawnLocation(Location arg0, boolean arg1) {
		player.setBedSpawnLocation(arg0, arg1);		
	}

	@Override
	public void setCompassTarget(Location arg0) {
		player.setCompassTarget(arg0);		
	}

	@Override
	public void setDisplayName(String arg0) {
		player.setDisplayName(arg0);		
	}

	@Override
	public void setExp(float arg0) {
		player.setExp(arg0);		
	}

	@Override
	public void setFlySpeed(float arg0) throws IllegalArgumentException {
		player.setFlySpeed(arg0);		
	}

	@Override
	public void setFlying(boolean arg0) {
		player.setFlying(arg0);		
	}

	@Override
	public void setHealthScale(double arg0) throws IllegalArgumentException {
		player.setHealthScale(arg0);		
	}

	@Override
	public void setHealthScaled(boolean arg0) {
		player.setHealthScaled(arg0);		
	}

	@Override
	public void setLevel(int arg0) {
		player.setLevel(arg0);		
	}

	@Override
	public void setPlayerListName(String arg0) {
		player.setPlayerListName(arg0);		
	}

	@Override
	public void setPlayerTime(long arg0, boolean arg1) {
		player.setPlayerTime(arg0, arg1);		
	}

	@Override
	public void setPlayerWeather(WeatherType arg0) {
		player.setPlayerWeather(arg0);		
	}

	@Override
	public void setResourcePack(String arg0) {
		player.setResourcePack(arg0);		
	}

	@Override
	public void setScoreboard(Scoreboard arg0) throws IllegalArgumentException, IllegalStateException {
		player.setScoreboard(arg0);		
	}

	@Override
	public void setSleepingIgnored(boolean arg0) {
		player.setSleepingIgnored(arg0);		
	}

	@Override
	public void setSneaking(boolean arg0) {
		player.setSneaking(arg0);		
	}

	@Override
	public void setSpectatorTarget(Entity arg0) {
		player.setSpectatorTarget(arg0);		
	}

	@Override
	public void setSprinting(boolean arg0) {
		player.setSprinting(arg0);		
	}

	@Override
	@Deprecated
	public void setTexturePack(String arg0) {
		player.setTexturePack(arg0);		
	}

	@Override
	public void setTotalExperience(int arg0) {
		player.setTotalExperience(arg0);		
	}

	@Override
	public void setWalkSpeed(float arg0) throws IllegalArgumentException {
		player.setWalkSpeed(arg0);		
	}

	@Override
	@Deprecated
	public void showPlayer(Player arg0) {
		player.showPlayer(arg0);		
	}

	@Override
	public Spigot spigot() {
		return player.spigot();
	}

	@Override
	public void updateInventory() {
		player.updateInventory();		
	}

	@Override
	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
		return player.getLineOfSight(transparent, maxDistance);
	}

	@Override
	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
		return player.getTargetBlock(transparent, maxDistance);
	}

	@Override
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
		return player.getLastTwoTargetBlocks(transparent, maxDistance);
	}

	@Override
	public Egg throwEgg() {
		return player.throwEgg();
	}

	@Override
	public Snowball throwSnowball() {
		return player.throwSnowball();
	}

	@Override
	public Arrow shootArrow() {
		return player.shootArrow();
	}

	@Override
	public int _INVALID_getLastDamage() {
		return player._INVALID_getLastDamage();
	}

	@Override
	public void _INVALID_setLastDamage(int damage) {
		player._INVALID_setLastDamage(damage);
	}

	@Override
	public void _INVALID_damage(int amount) {
		player._INVALID_damage(amount);
	}

	@Override
	public void _INVALID_damage(int amount, Entity source) {
		player._INVALID_damage(amount, source);
	}

	@Override
	public int _INVALID_getHealth() {
		return player._INVALID_getHealth();
	}

	@Override
	public void _INVALID_setHealth(int health) {
		player._INVALID_setHealth(health);
	}

	@Override
	public int _INVALID_getMaxHealth() {
		return player._INVALID_getMaxHealth();
	}

	@Override
	public void _INVALID_setMaxHealth(int health) {
		player._INVALID_setMaxHealth(health);
	}

	@Override
	public void setBanned(boolean banned) {
		player.setBanned(banned);
	}

	@Override
	public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
		return player.sendChunkChange(loc, sx, sy, sz, data);
	}

	@Override
	public void sendBlockChange(Location loc, int material, byte data) {
		player.sendBlockChange(loc, material, data);
	}

	@Override
	public void awardAchievement(Achievement achievement) {
		player.awardAchievement(achievement);
	}

	@Override
	public void removeAchievement(Achievement achievement) {
		player.removeAchievement(achievement);
	}

	@Override
	public boolean hasAchievement(Achievement achievement) {
		return player.hasAchievement(achievement);
	}
	
	
}
