package fr.crafter.tickleman.realplugin;

import java.io.File;
import java.io.IOException;

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

	//-------------------------------------------------------------------------------------- download
	public boolean download(String plugin)
	{
		return download(plugin, "release");
	}

	//-------------------------------------------------------------------------------------- download
	public boolean download(String plugin, String version)
	{
		boolean result = false;
		int bakTimeout = RealFileTools.httpTimeout;
		RealFileTools.httpTimeout = 10000;
		String urlplugin = plugin.toLowerCase().replace("realshop2", "realshop");
		String url = null;
		String buffer = "";
		try {
			buffer = RealFileTools.getFileContents(
				"http://dev.bukkit.org/server-mods/" + urlplugin + "/files"
			);
		} catch (IOException e) {
		}
		String[] filesBuffer = buffer.toLowerCase().split("<td class=\"col-file\">");
		for (int i = 1; i < filesBuffer.length; i++) {
			String fileBuffer = filesBuffer[i];
			String fileVersion = fileBuffer.split(">" + urlplugin + " v")[1].split("<")[0];
			if ((fileVersion == version) || (version == "release")) {
				String fileUrl = "http://dev.bukkit.org"
					+ fileBuffer.split("<a href=\"")[1].split("\">")[0];
				fileBuffer = "";
				try {
					fileBuffer = RealFileTools.getFileContents(fileUrl);
				} catch (IOException e) {
				}
				if (
					fileBuffer.contains("<li class=\"user-action user-action-download\">")
					&& fileBuffer.contains("<a href=\"")
				) {
					fileUrl = fileBuffer.split("<li class=\"user-action user-action-download\">")[1]
						.split("<a href=\"")[1].split("\">")[0];
					url = fileUrl;
				}
				break;
			}
		}
		if ((url != null) && RealFileTools.downloadFile(url, "plugins/" + plugin + ".jar")) {
			result = true;
		}
		RealFileTools.httpTimeout = bakTimeout;
		return result;
	}

	//------------------------------------------------------------------------------------- onCommand
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (sender.isOp()) {
			String command = cmd.getName().toLowerCase();
			if (command.equals("realplugin") || command.equals("rp")) {
				if (args.length == 3) {
					if (args[0].equalsIgnoreCase("download") || args[0].equalsIgnoreCase("dl")) {
						// /realplugin download <plugin> <version>
						if (download(args[1], args[2])) {
							sender.sendMessage(
								"Plugin " + args[1] + " version " + args[2] + " has been downloaded."
								+ " /stop or /reload your server to load it."
							);
							return true;
						} else {
							sender.sendMessage("plugin " + args[1] + " could not be downloaded or installed.");
						}
					}
				} else if (args.length == 2) {
					if (
						args[0].equalsIgnoreCase("download") || args[0].equalsIgnoreCase("dl")
						|| args[0].equalsIgnoreCase("update") || args[0].equalsIgnoreCase("upd")
					) {
						// /realplugin download <plugin>
						if (download(args[1])) {
							sender.sendMessage(
								"plugin " + args[1] + " release has been downloaded."
								+ " /stop or /reload your server to load it."
							);
							return true;
						} else {
							sender.sendMessage("plugin " + args[1] + " could not be downloaded or installed.");
						}
					} else if (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("l")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin == null) {
							try {
								getServer().getPluginManager().loadPlugin(new File("plugins/" + args[1]));
								sender.sendMessage("Plugin " + args[1] + " has been loaded.");
							} catch (UnknownDependencyException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : unknown dependency " + e.getMessage());
								e.printStackTrace();
							} catch (InvalidPluginException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid plugin " + e.getMessage());
								e.printStackTrace();
							} catch (InvalidDescriptionException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid description " + e.getMessage());
								e.printStackTrace();
							}
							plugin = getServer().getPluginManager().getPlugin(args[1]);
						}
						if (plugin != null) {
							if (!plugin.isEnabled()) {
								getServer().getPluginManager().enablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been enabled.");
							} else {
								sender.sendMessage("Plugin " + plugin.getName() + " is already enabled.");
							}
						} else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("d")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin != null) {
							if (plugin.isEnabled()) {
								getServer().getPluginManager().disablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been disabled.");
							} else {
								sender.sendMessage("Plugin " + plugin.getName() + " is already disabled.");
							}
						} else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("e")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin != null) {
							if (!plugin.isEnabled()) {
								getServer().getPluginManager().enablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been enabled.");
							} else {
								sender.sendMessage("Plugin " + plugin.getName() + " is already enabled.");
							}
						} else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					} else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("r")) {
						Plugin plugin = getServer().getPluginManager().getPlugin(args[1]);
						if (plugin != null) {
							if (plugin.isEnabled()) {
								getServer().getPluginManager().disablePlugin(plugin);
								sender.sendMessage("Plugin " + plugin.getName() + " has been disabled.");
							}
							try {
								getServer().getPluginManager().loadPlugin(new File("plugins/" + plugin.getName() + ".jar"));
								sender.sendMessage("Plugin " + args[1] + " has been loaded.");
							} catch (UnknownDependencyException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : unknown dependency " + e.getMessage());
								e.printStackTrace();
							} catch (InvalidPluginException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid plugin " + e.getMessage());
								e.printStackTrace();
							} catch (InvalidDescriptionException e) {
								sender.sendMessage("Plugin " + args[1] + " can't be loaded : Invalid description " + e.getMessage());
								e.printStackTrace();
							}
							plugin = getServer().getPluginManager().getPlugin(args[1]);
							getServer().getPluginManager().enablePlugin(plugin);
							sender.sendMessage("Plugin " + plugin.getName() + " has been enabled.");
						} else {
							sender.sendMessage("Plugin " + args[1] + " not found.");
						}
						return true;
					}
				} else if (args.length == 1) {
					// /realplugin help
					if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
						String[] help = {
							ChatColor.YELLOW + "/rp dl RealPlugin" + ChatColor.WHITE + " : download last release of RealPlugin.jar",
							"available plugins are RealAdminTools, RealDistantCommands,",
							"  RealJobs, RealPlugin, RealShop2, RealStats, RealTeleporter,",
							"  RealViewDontTouch.",
							ChatColor.YELLOW + "/rp r PluginName" + ChatColor.WHITE + " : reload any plugin",
							ChatColor.YELLOW + "/rp d PluginName" + ChatColor.WHITE + " : disable any plugin",
							ChatColor.YELLOW + "/rp e PluginName" + ChatColor.WHITE + " : enable any plugin",
						};
						for (String h : help) {
							sender.sendMessage(h);
						}
						return true;
					}
				} else {
					StringBuilder list = new StringBuilder("Plugins list: ");
					for (Plugin plugin : getServer().getPluginManager().getPlugins()) {
						if (plugin.isEnabled()) {
							list.append(ChatColor.DARK_GREEN + plugin.getName() + " ");
						} else {
							list.append(ChatColor.RED + plugin.getName() + " ");
						}
					}
					sender.sendMessage(list.toString());
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
	}

}
