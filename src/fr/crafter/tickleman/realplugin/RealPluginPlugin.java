package fr.crafter.tickleman.realplugin;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

//################################################################################ RealPluginPlugin
public class RealPluginPlugin extends JavaPlugin
{

	//------------------------------------------------------------------------------------- onCommand
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (sender.isOp()) {
			String command = cmd.getName().toLowerCase();
			if (command.equals("realtests") || command.equals("rt")) {
				Tests.run((args.length == 0) ? "all" : args[0], sender);
				return true;
			}
			if (command.equals("realrecipes") || command.equals("rr")) {
				if ((args.length == 1) && (args[0].equals("dump") || args[0].equals("d"))) {
					sender.sendMessage("Recipes dumped on console");
					RealRecipe.dumpAllRecipes();
					return true;
				}
				if ((args.length == 2) && (args[0].equals("dump") || args[0].equals("d"))) {
					sender.sendMessage("Recipes dumped on console for item " + args[1]);
					RealRecipe.dumpAllRecipes(args[1]);
					return true;
				}
			}
			if (command.equals("realplugin") || command.equals("rp")) {
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("l")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin == null) {
							try {
								getServer().getPluginManager().loadPlugin(new File("plugins/" + args[1]));
								sender.sendMessage("Plugin " + args[1] + " has been loaded.");
							}
							catch (UnknownDependencyException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : unknown dependency " + e.getMessage());
								e.printStackTrace();
							}
							catch (InvalidPluginException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid plugin " + e.getMessage());
								e.printStackTrace();
							}
							catch (InvalidDescriptionException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid description " + e.getMessage());
								e.printStackTrace();
							}
							plugin = getServer().getPluginManager().getPlugin(args[1]);
						}
						if (plugin != null) {
							if (!plugin.isEnabled()) {
								getServer().getPluginManager().enablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been enabled.");
							}
							else {
								sender.sendMessage("Plugin " + plugin.getName() + " is already enabled.");
							}
						}
						else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					}
					else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("d")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin != null) {
							if (plugin.isEnabled()) {
								getServer().getPluginManager().disablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been disabled.");
							}
							else {
								sender.sendMessage("Plugin " + plugin.getName() + " is already disabled.");
							}
						}
						else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					}
					else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("e")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin != null) {
							if (!plugin.isEnabled()) {
								getServer().getPluginManager().enablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been enabled.");
							}
							else {
								sender.sendMessage("Plugin " + plugin.getName() + " is already enabled.");
							}
						}
						else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					}
					else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin != null) {
							if (plugin.isEnabled()) {
								getServer().getPluginManager().disablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been disabled.");
							}
							try {
								getServer().getPluginManager().loadPlugin(new File("plugins/" + plugin.getName() + ".jar"));
								sender.sendMessage("Plugin " + args[1] + " has been loaded.");
							}
							catch (UnknownDependencyException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : unknown dependency " + e.getMessage());
								e.printStackTrace();
							}
							catch (InvalidPluginException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid plugin " + e.getMessage());
								e.printStackTrace();
							}
							catch (InvalidDescriptionException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid description " + e.getMessage());
								e.printStackTrace();
							}
							getServer().getPluginManager().enablePlugin(plugin);
							sender.sendMessage("Plugin " + plugin.getName() + " has been enabled.");
						}
						else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					}
				}
				else if (args.length == 1) {
					// /realplugin help
					if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
						String[] help = {
							ChatColor.YELLOW + "/rp r PluginName" + ChatColor.WHITE + " : reload any plugin",
							ChatColor.YELLOW + "/rp d PluginName" + ChatColor.WHITE + " : disable any plugin",
							ChatColor.YELLOW + "/rp e PluginName" + ChatColor.WHITE + " : enable any plugin",
						};
						for (String h : help) {
							sender.sendMessage(h);
						}
						return true;
					}
				}
				else {
					StringBuilder list = new StringBuilder("Plugins list: ");
					for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
						if (plugin.isEnabled()) {
							list.append(ChatColor.DARK_GREEN).append(plugin.getName()).append(" ");
						}
						else {
							list.append(ChatColor.RED).append(plugin.getName()).append(" ");
						}
					}
					sender.sendMessage(list.toString());
				}
			}
		}
		return false;
	}

}
