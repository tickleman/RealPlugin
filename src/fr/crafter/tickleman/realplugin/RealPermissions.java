package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;

//########################################################################################### Perms
public class RealPermissions
{

	private final RealPlugin plugin;
	private final String permissionsPluginName;
	boolean permissionsEnabled = false;

	//----------------------------------------------------------------------------------------- Perms
	public RealPermissions(RealPlugin plugin, String permissionsPluginName)
	{
		this.plugin = plugin;
		this.permissionsPluginName = permissionsPluginName.toLowerCase();
	}

	//---------------------------------------------------------------------------- permissionsEnabled
	public void disablePermissionsHandler()
	{
		permissionsEnabled = false;
	}

	//---------------------------------------------------------------------- getPermissionsPluginName
	public String getPermissionsPluginName()
	{
		return permissionsPluginName;
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionString)
	{
		boolean result;
		if (permissionsPluginName.equals("none")) {
			if (permissionString.contains(".")) {
				permissionString = permissionString.replace(
					plugin.getDescription().getName().toLowerCase() + ".", ""
				);
			}
			result = player.isOp()
				? plugin.opHasPermission(permissionString)
				: plugin.playerHasPermission(permissionString);
		}
		else if (permissionsPluginName.equals("bukkit")) {
			result = player.hasPermission(permissionString);
		}
		else {
			result = false;
		}
		// permission universal .* manager
		if (!result && !permissionString.contains(".*")) {
			result = hasPermission(player, permissionString + ".*");
			while (!result && permissionString.contains(".")) {
				permissionString = permissionString.substring(0, permissionString.lastIndexOf("."));
				result = hasPermission(player, permissionString + ".*");
			}
		}
		return result;
	}

}
