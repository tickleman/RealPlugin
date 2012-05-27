package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

public class RealPlayer
{

	//-------------------------------------------------------------------------- getEssentialsGodMode
	private static boolean getEssentialsGodMode(Player player)
	{
		Essentials essentials = RealLinks.getEssentials();
		if (essentials == null) {
			return false;
		}
		return essentials.getUser(player).isGodModeEnabled();
	}

	//------------------------------------------------------------------------------------ getGodMode
	public static boolean getGodMode(Player player)
	{
		boolean godMode = getEssentialsGodMode(player);
		return godMode;
	}

}
