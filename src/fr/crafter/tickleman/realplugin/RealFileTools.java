package fr.crafter.tickleman.realplugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

//####################################################################################### FileTools
public class RealFileTools
{

	public static int httpTimeout = 1000;

	//------------------------------------------------------------------------------------ deleteFile
	public static void deleteFile(String fileName)
	{
		File file = new File(fileName);
		if (file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}
	}

	//---------------------------------------------------------------------------------- downloadFile
	public static boolean downloadFile(String url, String filename)
	{
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setReadTimeout(httpTimeout);
			connection.setUseCaches(false);
			InputStream input = connection.getInputStream();
			try {
				DataOutputStream output = new DataOutputStream(new FileOutputStream(filename + ".tmp"));
				try {
					byte[] buffer = new byte[10240];
					int size;
					while ((size = input.read(buffer)) > 0) {
						output.write(buffer, 0, size);
					}
					output.close();
					deleteFile(filename);
					renameFile(filename + ".tmp", filename);
					return true;
				} catch (IOException e) {
					output.close();
					deleteFile(filename + ".tmp");
				}
			} catch (IOException e) {
				deleteFile(filename + ".tmp");
			}
			input.close();
		} catch (Exception e) {
			deleteFile(filename + ".tmp");
		}
		return false;
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
				try {
					FileOutputStream output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length;
					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					plugin.getServer().getLogger().log(Level.INFO, "Default file written " + filePath);
					input.close();
					output.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//------------------------------------------------------------------------------- getFileContents
	public static String getFileContents(String filename) throws IOException
	{
		if (filename.startsWith("http") && filename.contains(":/")) {
			if (downloadFile(filename, "download.tmp")) {
				return readFullTextFile("download.tmp");
			} else {
				return "";
			}
		} else {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			StringBuilder buffer = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line).append("\n");
			}
			reader.close();
			return buffer.toString();
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
			//noinspection ResultOfMethodCallIgnored
			dir.mkdirs();
		}
	}

	//------------------------------------------------------------------------------ readFullTextFile
	public static String readFullTextFile(String fileName) throws IOException
	{
		StringBuilder sb = new StringBuilder(10240);
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		char[] chars = new char[10240];
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
			//noinspection ResultOfMethodCallIgnored
			from.renameTo(to);
		}
	}

	//-------------------------------------------------------------------------------- setFileContent
	public static void setFileContent(String fileName, String content) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		writer.write(content);
		writer.close();
	}

}
