package fr.crafter.tickleman.realeconomy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealPlugin;
import fr.crafter.tickleman.realplugin.RealVarTools;

//############################################################################## RealEconomyCommand
public class RealEconomyCommand
{

	private final RealPlugin  plugin;
	private final RealEconomy economy;

	//---------------------------------------------------------------------------- RealEconomyCommand
	public RealEconomyCommand(RealPlugin plugin, RealEconomy economy)
	{
		this.plugin = plugin;
		this.economy = economy;
	}

	//-------------------------------------------------------------------------------- executeCommand
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player) {
			Player player = (Player)sender;
			String[] params = {
				(args.length > 0) ? solveAlias1(args[0]) : "",
				(args.length > 1) ? args[1] : "",
				(args.length > 2) ? args[2] : ""
			};
			RealEconomyAction action = new RealEconomyAction(economy);
			if (!params[0].isEmpty() && plugin.hasPermission(player, "realeconomy." + params[0])) {
				switch (params[0]) {
					case "burn":
						action.burn(player, RealVarTools.parseDouble(params[1], 0.0));
						break;
					case "dec":
						action.dec(player, params[1], RealVarTools.parseDouble(params[2], 0.0));
						break;
					case "display":
						action.display(player);
						break;
					case "give":
						action.give(player, params[1], RealVarTools.parseDouble(params[2], 0.0));
						break;
					case "help":
						action.help(player);
						break;
					case "inc":
						action.inc(player, params[1], RealVarTools.parseDouble(params[2], 0.0));
						break;
					case "set":
						action.set(player, params[1], RealVarTools.parseDouble(params[2], 0.0));
						break;
					case "tell":
						action.tell(player, params[1]);
						break;
				}
			}
			else if (
				!params[0].isEmpty() && !params[1].isEmpty()
				&& plugin.hasPermission(player, "realeconomy.give")
			) {
				action.give(player, params[0], RealVarTools.parseDouble(params[1], 0.0));
			}
			else if (!params[0].isEmpty() && plugin.hasPermission(player, "realeconomy.tell")) {
				action.tell(player, params[1]);
			}
			else if (params[0].isEmpty() && plugin.hasPermission(player, "realeconomy.display")) {
				action.display(player);
			}
		}
		return true;
	}

	//----------------------------------------------------------------------------------- solveAlias1
	private String solveAlias1(String param)
	{
		if (param.equals("b")) return "burn";
		if (param.equals("d")) return "dec";
		if (param.equals("g")) return "give";
		if (param.equals("h")) return "help";
		if (param.equals("i")) return "inc";
		if (param.equals("s")) return "set";
		return param;
	}

}
