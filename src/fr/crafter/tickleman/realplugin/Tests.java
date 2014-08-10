package fr.crafter.tickleman.realplugin;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class Tests
{

	private static CommandSender sender;

	//----------------------------------------------------------------------------------------- names
	private static void names()
	{
		out("material.name | Block.getName");
		for (Material material : Material.values()) {
			String name = material.name().toLowerCase().replace('_', ' ');
			out(name);
		}
	}

	//--------------------------------------------------------------------------------------- recipes
	private static void recipes()
	{
		out("recipes");
		RealRecipe.dumpAllRecipes();
	}

	//------------------------------------------------------------------------------------------- run
	public static void run(String test, CommandSender sender)
	{
		Tests.sender = sender;
		if (test.equals("all") || test.equals("names"))   names();
		if (test.equals("all") || test.equals("recipes")) recipes();
	}

	//------------------------------------------------------------------------------------------- out
	private static void out(String message)
	{
		System.out.println(message);
		sender.sendMessage(message);
	}

}
