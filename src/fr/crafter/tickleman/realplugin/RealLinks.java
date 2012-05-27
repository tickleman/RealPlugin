package fr.crafter.tickleman.realplugin;

import com.earth2me.essentials.Essentials;

public class RealLinks
{

	private static Essentials essentials = null;
	private static Boolean hasEssentials = null;

	//--------------------------------------------------------------------------------- getEssentials
	public static Essentials getEssentials()
	{
		if (hasEssentials == null) {
			hasEssentials = init("com.earth2me.essentials.Essentials");
		}
		return essentials;
	}

	//------------------------------------------------------------------------------------------ init
	private static boolean init(String className)
	{
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			return false;
		}
		essentials = (Essentials) RealPlugin.getRealServer().getPluginManager().getPlugin("Essentials");
		if (essentials == null) {
			return false;
		}
		return true;
	}

}
