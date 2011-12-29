package fr.crafter.tickleman.realplugin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
		String url = "http://plugins.crafter.fr/depot/tickleman/" + plugin
			+ "/" + version + "/" + plugin + ".jar";
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setReadTimeout(1000);
			connection.setUseCaches(false);
			InputStream input = connection.getInputStream();
			try {
				DataOutputStream output = new DataOutputStream(
					new FileOutputStream(new File("plugins/" + plugin + ".jar.tmp"))
				);
				try {
					byte[] buffer = new byte[10240];
					int size;
					while ((size = input.read(buffer)) > 0) {
						output.write(buffer, 0, size);
					}
					output.close();
					RealFileTools.deleteFile("plugins/" + plugin + ".jar");
					RealFileTools.renameFile("plugins/" + plugin + ".jar.tmp", "plugins/" + plugin + ".jar");
					return true;
				} catch (Exception e) {
					output.close();
					RealFileTools.deleteFile("plugins/" + plugin + ".jar.tmp");
				}
			} catch (Exception e) {
			}
			input.close();
		} catch (Exception e) {
		}
		return false;
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
								"plugin " + args[1] + " version " + args[2] + " has been downloaded."
								+ " /stop or /reload your server to load it."
							);
							return true;
						} else {
							sender.sendMessage("plugin " + args[1] + " could not be downloaded or installed.");
						}
					}
				}
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("download") || args[0].equalsIgnoreCase("dl")) {
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
					}
				} else if (args.length == 1) {
					// /realplugin help
					if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("h")) {
						String[] help = {
							"/rp dl RealPlugin : download last release of RealPlugin.jar",
							"available plugins are RealAdminTools, RealDistantCommands,",
							"    RealPlugin, RealShop2, RealTeleporter, RealViewDontTouch"
						};
						for (String h : help) {
							sender.sendMessage(h);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		System.out.println(
			"[RealPlugin] version [" + getDescription().getVersion() + "] ("
			+ getDescription().getAuthors().toString().replace("[", "").replace("]", "") + ") unloaded"
		);
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		System.out.println(
			"[RealPlugin] version [" + getDescription().getVersion() + "] ("
			+ getDescription().getAuthors().toString().replace("[", "").replace("]", "") + ") loaded"
		);
	}

}
