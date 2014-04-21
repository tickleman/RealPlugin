package fr.crafter.tickleman.realplugin;

import org.bukkit.enchantments.Enchantment;

//################################################################################# RealEnchantment
public class RealEnchantment
{

	//-------------------------------------------------------------------------- getEnchantmentWeight
	public static int getEnchantmentWeight(Enchantment enchantment)
	{
		return net.minecraft.server.v1_7_R1.Enchantment.byId[enchantment.getId()].getRandomWeight();
	}

}
