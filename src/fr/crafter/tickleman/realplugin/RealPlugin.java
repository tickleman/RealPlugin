package fr.crafter.tickleman.realplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

//###################################################################################### RealPlugin
public class RealPlugin extends JavaPlugin
{

	protected RealConfig      config = null;
	private   RealTranslation lang   = null;
	private   RealLog         log    = null;
	private   RealPermissions perms  = null;

	//--------------------------------------------------------------------------------- getRealConfig
	/**
	 * Plugin developer must override this to get it's own configuration object
	 */
	public RealConfig getRealConfig()
	{
		if (config == null) {
			loadConfig();
		}
		return config;
	}

	//---------------------------------------------------------------------------------------- getLog
	public RealLog getLog()
	{
		return log;
	}

	//-------------------------------------------------------------------------------- getPermissions
	public RealPermissions getPermissions()
	{
		return perms;
	}

	//--------------------------------------------------------------------------------- hasPermission
	public boolean hasPermission(Player player, String permissionString)
	{
		return perms.hasPermission(player, permissionString.toLowerCase());
	}

	//------------------------------------------------------------------------------------ loadConfig
	/**
	 * Plugin developer must override this to load it's own configuration object
	 */
	protected void loadConfig()
	{
		config = new RealConfig(this).load();
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		// disabled
		if (log != null) {
			getLog().info(
				"version [" + getDescription().getVersion() + "] ("
				+ getDescription().getAuthors().toString().replace("[", "").replace("]", "")
				+ ") un-loaded",
				true
			);
		}
		// disable associated objects
		config = null;
		log    = null;
		perms  = null;
		lang   = null;
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		getDataFolder().mkdirs();
		reload();
		getLog().info(
			"version [" + getDescription().getVersion() + "] ("
			+ getDescription().getAuthors().toString().replace("[", "").replace("]", "")
			+ ") loaded",
			true
		);
	}

	//------------------------------------------------------------------------------------- opHasPerm
	/**
	 * Plugin developer can override this to set default op permissions
	 * if permission system is "none"
	 */
	public boolean opHasPermission(String permissionString)
	{
		return true;
	}

	//------------------------------------------------------------------------------------- opHasPerm
	/**
	 * Plugin developer can override this to set default non-op players some permissions
	 * if permission system is "none"
	 */
	public boolean playerHasPermission(String permissionString)
	{
		return false;
	}

	//---------------------------------------------------------------------------------------- reload
	public void reload()
	{
		loadConfig();
		log   = new RealLog(this, getRealConfig().debug, getRealConfig().pluginLog);
		perms = new RealPermissions(this, getRealConfig().permissionsPlugin);
		lang  = new RealTranslation(this, getRealConfig().language).load();
	}

	//-------------------------------------------------------------------------------------------- tr
	/**
	 * Call this to translate text using chosen language file
	 */
	public String tr(String text)
	{
		return lang.tr(text);
	}

	//------------------------------------------------------------------------------------ trItemName
	/**
	 * Get item name from translation file entry looking like 35:14=Red wool
	 * If there is no translation file entry, then try to generate item name
	 * from realItemType.getName() translation
	 *
	 * @param RealItemType itemType
	 * @return String
	 */
	public String trItemName(RealItemType itemType)
	{
		return trItemName(itemType.toString());
	}

	//------------------------------------------------------------------------------------ trItemName
	/**
	 * Get item name from translation file entry looking like 35:14=Red wool
	 * If there is no translation file entry, then try to generate item name
	 * from realItemType.getName() translation
	 *
	 * @param String typeIdVariant
	 * @return String
	 */
	public String trItemName(String typeIdVariant)
	{
		String itemName = tr(typeIdVariant);
		if (itemName.equals(typeIdVariant)) {
			itemName = tr(RealItemType.parseItemType(typeIdVariant).getName());
		}
		return itemName;
	}

}
