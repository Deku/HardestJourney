package cl.josedev.HardestJourney;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import net.md_5.bungee.api.ChatColor;

public class DifficultyListener implements Listener {

	private HardestJourney plugin;
	
	public DifficultyListener(HardestJourney plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onMobSpawn(CreatureSpawnEvent event) {
		if (event.getEntity() instanceof Monster
				|| event.getEntity().getType() == EntityType.GHAST
				|| event.getEntity().getType() == EntityType.SLIME
				|| event.getEntity().getType() == EntityType.MAGMA_CUBE) {
			LivingEntity entity = event.getEntity();
			String world = entity.getWorld().getName();
			
			if (plugin.getConfig().getString("difficulty." + world) != null) {
				Location loc = new Location(entity.getWorld(),
						plugin.getConfig().getInt("difficulty." + world + ".location.x"),
						plugin.getConfig().getInt("difficulty." + world + ".location.y"),
						plugin.getConfig().getInt("difficulty." + world + ".location.z"));
	
				double distance = entity.getLocation().distance(loc);
				double healthMultiplier = plugin.getConfig().getDouble("difficulty." + world + ".healthMultiplier");
				double newHealth = Math.round(entity.getHealth() + Math.sqrt(distance * healthMultiplier));
	
				entity.setMaxHealth(newHealth);
				entity.setHealth(newHealth);
				entity.setCustomName(entity.getType().name() + " " + ChatColor.RED + (int)entity.getMaxHealth() + " HP");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player
				&& event.getEntity() instanceof LivingEntity
				&& event.getEntityType() != EntityType.PLAYER) {
			
			LivingEntity entity = (LivingEntity)event.getEntity();
			
			if (!entity.isDead()) {
				updateCustomName(entity, event.getFinalDamage(), true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onExternalDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof LivingEntity
				&& event.getEntityType() != EntityType.PLAYER) {
			LivingEntity entity = (LivingEntity)event.getEntity();
			
			if (!entity.isDead()) {
				updateCustomName(entity, event.getFinalDamage(), true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onMobHealthRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof LivingEntity 
				&& event.getEntityType() != EntityType.PLAYER) {
			LivingEntity entity = (LivingEntity)event.getEntity();
			updateCustomName(entity, event.getAmount(), false);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onExpDrop(EntityDeathEvent event) {
		
		if (event.getEntity() instanceof Monster) {
			LivingEntity entity = event.getEntity();
			String world = entity.getWorld().getName();
			
			if (plugin.getConfig().getString("difficulty." + world) != null) {
				Location loc = new Location(entity.getWorld(),
						plugin.getConfig().getInt("difficulty." + world + ".location.x"),
						plugin.getConfig().getInt("difficulty." + world + ".location.y"),
						plugin.getConfig().getInt("difficulty." + world + ".location.z"));
	
				double distance = entity.getLocation().distance(loc);
				double expMultiplier = plugin.getConfig().getDouble("difficulty." + world + ".expMultiplier");
				int newExp = (int)Math.round(event.getDroppedExp() + Math.sqrt(distance * expMultiplier));
	
				event.setDroppedExp(newExp);
			}
		}
	}
	
	public void updateCustomName(LivingEntity entity, double value, boolean isDamage) {
		if (isDamage && value > 0) {
			value *= -1;
		}
		
		double health = Math.ceil(entity.getHealth() + value);
		// Don't show negative numbers
		if (health < 0) {
			health = 0;
		}
		entity.setCustomName(entity.getType().name() + " " + ChatColor.RED + (int)health + " HP");
	}
}
