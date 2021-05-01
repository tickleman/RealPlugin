package fr.crafter.tickleman.realplugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

//###################################################################################### RealConfig
public class RealConfig
{

	private final String fileName;

	public  boolean          debug = false;
	public  String           language = "en";
	public  String           permissionsPlugin = "bukkit";
	private final JavaPlugin plugin;
	public  boolean          pluginLog = false;
	private final Set<Field> volatileFields = new HashSet<>();

	//---------------------------------------------------------------------------------------- Config
	public RealConfig(final JavaPlugin plugin)
	{
		this(plugin, "config");
	}

	//---------------------------------------------------------------------------------------- Config
	public RealConfig(final JavaPlugin plugin, String fileName)
	{
		this.plugin = plugin;
		this.fileName = getPlugin().getDataFolder().getPath() + "/" + fileName + ".txt";
	}

	//---------------------------------------------------------------------------------------- Config
	public RealConfig(final JavaPlugin plugin, String fileName, RealConfig mainConfig)
	{
		this(plugin, fileName);
		copyFrom(mainConfig);
		setVolatileFields(mainConfig.getClass());
	}

	//----------------------------------------------------------------------------------------- clone
	private void copyFrom(RealConfig config)
	{
		for (Field field : getClass().getFields()) {
			//noinspection CatchMayIgnoreException
			try {
				field.set(this, field.get(config));
			}
			catch (Exception exception) {
			}
		}
	}

	//------------------------------------------------------------------------------------- getPlugin
	protected JavaPlugin getPlugin()
	{
		return plugin;
	}

	//------------------------------------------------------------------------------------------ load
	public RealConfig load()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				buffer = buffer.replace("\r", "");
				if (buffer.charAt(0) != '#') {
					String[] line = buffer.split("=");
					if (line.length >= 2) {
						String key = line[0].trim();
						String value = line[1].trim();
						Field field = getClass().getField(key);
						if (volatileFields.contains(field)) {
							getPlugin().getServer().getLogger().log(
								Level.WARNING, "[" + getPlugin().getDescription().getName() + "] "
								+ " ignore configuration option " + key
								+ " in " + fileName + " (unknown keyword)"
							);
						}
						else {
							String fieldClass = field.getType().getName();
							try {
								switch (fieldClass) {
									case "boolean":
									case "java.lang.Boolean":
										field.set(this, RealVarTools.parseBoolean(value));
										break;
									case "double":
									case "java.lang.Double":
										field.set(this, Double.parseDouble(value));
										break;
									case "int":
									case "java.lang.Integer":
										field.set(this, Integer.parseInt(value));
										break;
									default:
										field.set(this, value);
										break;
								}
							}
							catch (Exception exception) {
								getPlugin().getServer().getLogger().log(
									Level.SEVERE, "[" + getPlugin().getDescription().getName() + "] "
									+ " ignore configuration option " + key
									+ " in " + fileName + " (" + exception.getMessage() + ")"
								);
							}
						}
					}
				}
			}
			reader.close();
		}
		catch (Exception exception) {
			getPlugin().getServer().getLogger().log(
				Level.WARNING, "[" + getPlugin().getDescription().getName() + "] "
				+ " auto-create default " + fileName
			);
			save();
		}
		return this;
	}

	//------------------------------------------------------------------------------------------ save
	public void save()
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			for (Field field : getClass().getFields()) {
				if (!volatileFields.contains(field)) {
					String value = ((field.get(this) == null) ? "null" : field.get(this).toString());
					writer.write(field.getName() + "=" + value + "\n");
				}
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			getPlugin().getServer().getLogger().log(
				Level.SEVERE, "[" + getPlugin().getDescription().getName() + "]"
				+ " file save error " + fileName + " (" + e.getMessage() + ")"
			);
		}
	}

	//----------------------------------------------------------------------------- setVolatileFields
	private void setVolatileFields(Class<?> applyClass)
	{
		volatileFields.clear();
		Collections.addAll(volatileFields, applyClass.getFields());
	}

}
