package fr.crafter.tickleman.realplugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

//####################################################################################### FileTools
public class RealFileTools
{

	//------------------------------------------------------------------------------------ deleteFile
	public static void deleteFile(String fileName)
	{
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	//---------------------------------------------------------------------------- extractDefaultFile
	public static void extractDefaultFile(JavaPlugin plugin, String filePath)
	{
		String[] split = filePath.split("/");
		String fileName = split[split.length - 1];
		File actual = new File(filePath);
		if (!actual.exists()) {
			InputStream input = plugin.getClass().getResourceAsStream("/default/" + fileName);
			if (input != null) {
				plugin.getServer().getLogger().log(
					Level.INFO, "Create default file " + fileName + " for " + filePath
				);
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					plugin.getServer().getLogger().log(Level.INFO, "Default file written " + filePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try { input.close();  } catch (Exception e) {}
				try { output.close(); } catch (Exception e) {}
			}
		}
	}

	//------------------------------------------------------------------------------------ fileExists
	public static boolean fileExists(String fileName)
	{
		return (new File(fileName)).exists();
	}

	//----------------------------------------------------------------------------------------- mkDir
	public static void mkDir(String dirName)
	{
		File dir = new File(dirName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	//------------------------------------------------------------------------------ readFullTextFile
	public static String readFullTextFile(String fileName) throws IOException
	{
		StringBuffer sb = new StringBuffer(1024);
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		char[] chars = new char[1024];
		while (reader.read(chars) > -1) {
			sb.append(String.valueOf(chars));	
		}
		reader.close();
		return sb.toString();
	}

	//------------------------------------------------------------------------------------ renameFile
	public static void renameFile(String fromFile, String toFile)
	{
		File from = new File(fromFile);
		File to = new File(toFile);
		if (from.exists() && !to.exists()) {
			from.renameTo(to);
		}
	}

}
