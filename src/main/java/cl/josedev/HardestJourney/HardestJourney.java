package cl.josedev.HardestJourney;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class HardestJourney extends JavaPlugin {

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		this.getServer().getPluginManager().registerEvents(new DifficultyListener(this), this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("dificultad")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can use this command!");
				return true;
			}
			
			Player player = (Player) sender;
			String world = player.getWorld().getName();
			
			if (this.getConfig().getString("difficulty." + world) != null) {
				Location loc = new Location(player.getWorld(),
						this.getConfig().getInt("difficulty." + world + ".location.x"),
						this.getConfig().getInt("difficulty." + world + ".location.y"),
						this.getConfig().getInt("difficulty." + world + ".location.z"));
	
				double distance = player.getLocation().distance(loc);
				double healthMultiplier = this.getConfig().getDouble("difficulty." + world + ".healthMultiplier");
				player.sendMessage(ChatColor.AQUA + "Estas a " + ChatColor.DARK_AQUA + Math.round(distance) + ChatColor.AQUA + " bloques del spawn");
				player.sendMessage(ChatColor.AQUA + "Los mobs tendran aproximadamente " + ChatColor.WHITE +"+" + ChatColor.RED + Math.round(Math.sqrt(distance * healthMultiplier)) + " HP");
			} else {
				player.sendMessage(ChatColor.GOLD + "Este mundo no tiene multiplicador de dificultad!");
			}
			
			return true;
			
		} else if (cmd.getName().equalsIgnoreCase("journey")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can use this command!");
				return true;
			}
			
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GREEN + "/journey set <option>");
				return true;
			}
			
			Player player = (Player) sender;
			String world = player.getWorld().getName();
			
			// journey set <config> <value>
			if (args[0].equalsIgnoreCase("set")) {
				if (!sender.hasPermission("hj.admin")) {
					sender.sendMessage(ChatColor.RED + "No tienes permiso para hacer esto");
					return true;
				}
				
				if (args.length == 1) {
					sender.sendMessage(ChatColor.GREEN + "/journey set spawn");
					sender.sendMessage(ChatColor.GREEN + "/journey set exp <value>");
					sender.sendMessage(ChatColor.GREEN + "/journey set health <value>");
					return true;
				}
				
				if (args[1].equalsIgnoreCase("spawn")) {
					this.getConfig().set("difficulty." + world + ".location.x", player.getLocation().getBlockX());
					this.getConfig().set("difficulty." + world + ".location.y", player.getLocation().getBlockY());
					this.getConfig().set("difficulty." + world + ".location.z", player.getLocation().getBlockZ());
					this.saveConfig();
					
					sender.sendMessage(ChatColor.GREEN + "Se ha establecido un nuevo punto central para este mundo.");
					return true;
				} else if (args[1].equalsIgnoreCase("exp")) {
					if (args.length > 2) {
						try {
							double expMult = Double.parseDouble(args[2]);
							this.getConfig().set("difficulty." + world + ".expMultiplier", expMult);
							this.saveConfig();
							
							sender.sendMessage(ChatColor.GREEN + "Multiplicador de experiencia guardado!");
							return true;
						} catch (Exception ex) {
							sender.sendMessage(ChatColor.RED + "Numero invalido!");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Faltan argumentos!");
						return true;
					}
				} else if (args[1].equalsIgnoreCase("health")) {
					if (args.length > 2) {
						try {
							double healthMult = Double.parseDouble(args[2]);
							this.getConfig().set("difficulty." + world + ".healthMultiplier", healthMult);
							this.saveConfig();
							
							sender.sendMessage(ChatColor.GREEN + "Multiplicador de vida guardado!");
							return true;
						} catch (Exception ex) {
							sender.sendMessage(ChatColor.RED + "Numero invalido!");
							return true;
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Faltan argumentos!");
						return true;
					}
				}
				
			}
		}
		
		return false;
	}
}
