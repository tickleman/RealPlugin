package fr.crafter.tickleman.realplugin;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

//##################################################################################### RealRecipes
public class RealRecipe
{

	private Set<RealItemStack> recipeItems = new HashSet<RealItemStack>();
	private RealItemStack resultItem;

	//--------------------------------------------------------------------------------- getResultItem
	public Set<RealItemStack> getRecipeItems()
	{
		return recipeItems;
	}

	//--------------------------------------------------------------------------------- getResultItem
	public RealItemStack getResultItem()
	{
		return resultItem;
	}

	//------------------------------------------------------------------------------------ RealRecipe
	public RealRecipe(ShapedRecipe recipe)
	{
		resultItem = new RealItemStack(recipe.getResult());
		for (ItemStack itemStack : recipe.getIngredientMap().values()) {
			if (itemStack != null && itemStack.getType() != null) {
				recipeItems.add(new RealItemStack(itemStack));
			}
		}
	}
	
	//------------------------------------------------------------------------------------ RealRecipe
	public RealRecipe(ShapelessRecipe recipe)
	{
		resultItem = new RealItemStack(recipe.getResult());
		for (ItemStack itemStack : recipe.getIngredientList()) {
			recipeItems.add(new RealItemStack(itemStack));
		}
	}

	public RealRecipe(FurnaceRecipe recipe)
	{
		resultItem = new RealItemStack(recipe.getResult());
		recipeItems.add(new RealItemStack(recipe.getInput()));
	}

	//------------------------------------------------------------------------------------ RealRecipe
	/**
	 * Generate a easily usable recipe, based on Minecraft's item-to-item recipe
	 */
	public RealRecipe(RealItemStack recipeItemStack, RealItemStack resultItem)
	{
		resultItem.setAmount(8);
		recipeItemStack.setAmount(8);
		this.resultItem = resultItem;
		// TODO check Material.COAL.getId()
		this.recipeItems.add(new RealItemStack(Material.COAL.name()));
		this.recipeItems.add(recipeItemStack);
	}

	//-------------------------------------------------------------------------------- dumpAllRecipes
	public static void dumpAllRecipes()
	{
		dumpAllRecipes(null);
	}

	//-------------------------------------------------------------------------------- dumpAllRecipes
	public static void dumpAllRecipes(String itemId)
	{
		if (itemId != null) {
			itemId = itemId.toLowerCase();
		}
		for (Material material : Material.values()) {
			if (itemId == null || itemId.equals(material.name().toLowerCase())) {
				System.out.println("Recipes for " + material.name());
				for (RealRecipe recipe : getItemRecipes(new RealItemType(material))) {
					System.out.println("+ " + recipe.toNamedString());
				}
			}
		}
	}

	//-------------------------------------------------------------------------------- getItemRecipes
	/**
	 * Return a set of possible recipes for given item type
	 */
	public static Set<RealRecipe> getItemRecipes(RealItemType realItemType)
	{
		Set<RealRecipe> itemRecipes = new HashSet<RealRecipe>();

		for (Recipe recipe : Bukkit.getRecipesFor(new ItemStack(realItemType.getMaterial()))) {
			if (recipe instanceof ShapedRecipe) {
				itemRecipes.add(new RealRecipe((ShapedRecipe)recipe));
			}
			else if (recipe instanceof CraftShapelessRecipe) {
				itemRecipes.add(new RealRecipe((ShapelessRecipe)recipe));
			}
			else if (recipe instanceof CraftFurnaceRecipe) {
				itemRecipes.add(new RealRecipe((FurnaceRecipe)recipe));
			}
		}

		return itemRecipes;
	}

	//--------------------------------------------------------------------------------- toNamedString
	public String toNamedString()
	{
		String result = "";
		for (RealItemStack itemStack : recipeItems) {
			result += " + " + itemStack.toNamedString();
		}
		return resultItem.toNamedString() + " = " + result.substring(1);
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		String result = "";
		for (RealItemStack itemStack : recipeItems) {
			result += "+" + itemStack.toString();
		}
		return resultItem.toString() + "=" + result.substring(1);
	}

}
