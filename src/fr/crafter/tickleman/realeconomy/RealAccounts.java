package fr.crafter.tickleman.realeconomy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import fr.crafter.tickleman.realplugin.RealPlugin;

//################################################################################### RealShopsFile
public class RealAccounts
{

	/** Accounts list : "playerName" => (double)balance */
	private final Map<String, Double> accounts = new HashMap<>();
	private final String              fileName;
	private final RealPlugin          plugin;

	//--------------------------------------------------------------------------------- RealShopsFile
	public RealAccounts(final RealPlugin plugin)
	{
		this.plugin = plugin;
		fileName = plugin.getDataFolder().getPath() + "/accounts.txt";
		load();
	}

	//------------------------------------------------------------------------------------ getBalance
	public Double getBalance(String playerName)
	{
		return accounts.get(playerName);
	}

	//------------------------------------------------------------------------------------------ load
	public void load()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String buffer;
			while ((buffer = reader.readLine()) != null) {
				String[] line = buffer.split(";");
				if (line.length >= 2) {
					//noinspection CatchMayIgnoreException
					try {
						String playerName = line[0].trim();
						Double balance = Double.parseDouble(line[1].trim());
						accounts.put(playerName, balance);
					}
					catch (Exception e) {
					}
				}
			}
			reader.close();
		}
		catch (Exception e) {
			plugin.getLog().warning("Needs " + fileName + " (will auto-create)");
			save();
		}
	}

	//------------------------------------------------------------------------------------------ save
	public void save()
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			for (String playerName : accounts.keySet()) {
				writer.write(playerName + ";" + accounts.get(playerName) + "\n");
			}
			writer.flush();
			writer.close();
		}
		catch (Exception e) {
			plugin.getLog().severe("Could not save " + fileName);
		}
	}

	//------------------------------------------------------------------------------------ getBalance
	public void setBalance(String playerName, Double balance)
	{
		balance = Math.round(balance * 100d) / 100d;
		accounts.put(playerName, balance);
		save();
	}

}
