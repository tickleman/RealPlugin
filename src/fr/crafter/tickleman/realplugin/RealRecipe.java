package fr.crafter.tickleman.realplugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.server.CraftingManager;
import net.minecraft.server.CraftingRecipe;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

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
	/**
	 * Generate a easily usable recipe, based on Minecraft's crafting recipe
	 */
	public RealRecipe(CraftingRecipe recipe, RealItemStack resultItem)
	{
		this.resultItem = resultItem;
		// recipeField
		Field recipeField = null;
		for (Field field : recipe.getClass().getDeclaredFields()) {
			if (
				field.getType().getCanonicalName().contains(".ItemStack[]")
				|| field.getType().getCanonicalName().contains(".List")
			) {
				recipeField = field;
				break;
			}
		}
		recipeField.setAccessible(true);
		try {
			if (recipeField.getType().getCanonicalName().contains(".ItemStack[]")) {
				// ItemStack[]
				for (ItemStack itemStack : (ItemStack[])recipeField.get(recipe)) {
					if (itemStack != null) {
						recipeItems.add(new RealItemStack(
							itemStack.id, itemStack.count, (short)itemStack.getData()
						));
					}
				}
			} else {
				// List
				@SuppressWarnings("unchecked")
				List<ItemStack> itemStackList = (List<ItemStack>)recipeField.get(recipe);
				for (int i = 0; i < itemStackList.size(); i ++) {
					ItemStack itemStack = itemStackList.get(i);
					if (itemStack != null) {
						recipeItems.add(new RealItemStack(
							itemStack.id, itemStack.count, (short)itemStack.getData()
						));
					}
				}
			}
		} catch (Exception e) {
			System.out.println(
				"[ERROR] on " + resultItem.toString() + " recipe " + recipe.getClass()
				+ " field " + recipeField.getType().getCanonicalName()
			);
			e.printStackTrace();
		}
		if (recipeItems.size() == 9) {
			int isStrange = 0;
			for (RealItemStack recipeItem : recipeItems) {
				if (recipeItem.getAmount() == 9) isStrange ++;
			}
			if (isStrange == 9) {
				for (RealItemStack recipeItem : recipeItems) {
					recipeItem.setAmount(1);
				}
			}
		}
	}

	//------------------------------------------------------------------------------------ RealRecipe
	/**
	 * Generate a easily usable recipe, based on Minecraft's item-to-item recipe
	 */
	public RealRecipe(RealItemStack recipeItemStack, RealItemStack resultItem)
	{
		this.resultItem = resultItem;
		this.recipeItems.add(new RealItemStack(Item.COAL.id));
		this.recipeItems.add(recipeItemStack);
	}

	//-------------------------------------------------------------------------------- dumpAllRecipes
	public static void dumpAllRecipes()
	{
		for (int i = 1; i <= 2266; i++) {
			if (Item.byId[i] != null) {
				Item item = Item.byId[i];
				for (RealRecipe recipe : getItemRecipes(new RealItemType(item.id))) {
					System.out.println("RECIPE " + i + " : " + recipe.toNamedString());
				}
			}
			if (i == 121) i = 255;
			if (i == 383) i = 2255;
		}
	}

	//-------------------------------------------------------------------------------- getItemRecipes
	/**
	 * Return a set of possible recipes for given item type
	 */
	public static Set<RealRecipe> getItemRecipes(RealItemType realItemType)
	{
		// code for 1.2
		Set<RealRecipe> itemRecipes = new HashSet<RealRecipe>();
		for (Object recipe : CraftingManager.getInstance().getRecipies()) {
			ItemStack itemStack = ((CraftingRecipe)recipe).b();
			RealItemStack resultItemStack = new RealItemStack(
				itemStack.id, itemStack.count, (short)itemStack.getData()
			);	
			if (realItemType.isSameItem(resultItemStack)) {
				itemRecipes.add(new RealRecipe((CraftingRecipe)recipe, resultItemStack));
			}
		}
		for (Object itemTypeId : FurnaceRecipes.getInstance().getRecipies().keySet()) {
			ItemStack itemStack = (ItemStack)FurnaceRecipes.getInstance().getRecipies().get(itemTypeId);
			RealItemStack recipeItemStack = new RealItemStack((Integer)itemTypeId);
			RealItemStack resultItemStack = new RealItemStack(
				itemStack.id, itemStack.count, (short)itemStack.getData()
			);
			if (realItemType.isSameItem(resultItemStack)) {
				itemRecipes.add(new RealRecipe(recipeItemStack, resultItemStack));
			}
		}
		// code for 1.1
		/*
		Set<RealRecipe> itemRecipes = new HashSet<RealRecipe>();
		for (Object recipe : CraftingManager.getInstance().b()) {
			RealItemStack resultItemStack = new RealItemStack(((CraftingRecipe)recipe).b());
			if (realItemType.isSameItem(resultItemStack)) {
				itemRecipes.add(new RealRecipe((CraftingRecipe)recipe, resultItemStack));
			}
		}
		for (Object itemTypeId : FurnaceRecipes.getInstance().b().keySet()) {
			RealItemStack recipeItemStack = new RealItemStack((Integer)itemTypeId);
			RealItemStack resultItemStack = new RealItemStack(
				(ItemStack)FurnaceRecipes.getInstance().b().get(itemTypeId)
			);
			if (realItemType.isSameItem(resultItemStack)) {
				itemRecipes.add(new RealRecipe(recipeItemStack, resultItemStack));
			}
		}
		*/
		// TODO : here potions recipes here (must find a way)
		/*
		if (itemRecipes.isEmpty() && (realItemType.getTypeId() == Material.POTION.getId())) {
			ItemPotion itemPotion = (ItemPotion)Item.byId[Material.POTION.getId()];
			if (itemPotion == null) System.out.println("POTION IS NULL");
			else {
				System.out.println("POTION IS " + realItemType.toString());
				for (Object objectPotion : itemPotion.b(realItemType.getVariant())) {
					ItemStack itemStack = (ItemStack)objectPotion;
					System.out.println(new RealItemStack(itemStack).toString());
				}
			}
		}
		*/
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
