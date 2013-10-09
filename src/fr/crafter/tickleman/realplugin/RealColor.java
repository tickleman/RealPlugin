package fr.crafter.tickleman.realplugin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Color;

//####################################################################################### RealColor
public abstract class RealColor
{

	public static String cancel   = ChatColor.RED.toString();
	public static String command  = ChatColor.GRAY.toString();
	public static String doc      = ChatColor.GRAY.toString();
	public static String item     = ChatColor.GREEN.toString();
	public static String message  = ChatColor.AQUA.toString();
	public static String player   = ChatColor.GOLD.toString();
	public static String price    = ChatColor.YELLOW.toString();
	public static String quantity = ChatColor.WHITE.toString();
	public static String shop     = ChatColor.DARK_PURPLE.toString();
	public static String text     = ChatColor.GRAY.toString();

	//-------------------------------------------------------------------------------------- colorMap
	private static Map<String, Color> colorMap;

	//------------------------------------------------------------------------------------- stringMap
	private static Map<Color, String> stringMap;

	//----------------------------------------------------------------------------------- parseString
	public static Color parseString(String color)
	{
		if (colorMap == null) {
			colorMap = new HashMap<String, Color>();
			colorMap.put("aqua", Color.AQUA);
			colorMap.put("black", Color.BLACK);
			colorMap.put("blue", Color.BLUE);
			colorMap.put("fuchsia", Color.FUCHSIA);
			colorMap.put("gray", Color.GRAY);
			colorMap.put("green", Color.GREEN);
			colorMap.put("lime", Color.LIME);
			colorMap.put("maroon", Color.MAROON);
			colorMap.put("navy", Color.NAVY);
			colorMap.put("olive", Color.OLIVE);
			colorMap.put("orange", Color.ORANGE);
			colorMap.put("purple", Color.PURPLE);
			colorMap.put("red", Color.RED);
			colorMap.put("silver", Color.SILVER);
			colorMap.put("teal", Color.TEAL);
			colorMap.put("yellow", Color.YELLOW);
		}
		return colorMap.get(color);
	}

	//-------------------------------------------------------------------------------------- toString
	public static String toString(Color color)
	{
		if (stringMap == null) {
			stringMap = new HashMap<Color, String>();
			stringMap.put(Color.AQUA, "aqua");
			stringMap.put(Color.BLACK, "black");
			stringMap.put(Color.BLUE, "blue");
			stringMap.put(Color.FUCHSIA, "fuchsia");
			stringMap.put(Color.GRAY, "gray");
			stringMap.put(Color.GREEN, "green");
			stringMap.put(Color.LIME, "lime");
			stringMap.put(Color.MAROON, "maroon");
			stringMap.put(Color.NAVY, "navy");
			stringMap.put(Color.OLIVE, "olive");
			stringMap.put(Color.ORANGE, "orange");
			stringMap.put(Color.PURPLE, "purple");
			stringMap.put(Color.RED, "red");
			stringMap.put(Color.SILVER, "silver");
			stringMap.put(Color.TEAL, "teal");
			stringMap.put(Color.YELLOW, "yellow");
		}
		return stringMap.get(color);
	}

}
