package fr.crafter.tickleman.realplugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

//############################################################################# class RealItemStack
public class RealItemStack extends RealItemType
{

	/**
	 * Amount of stored item (can be negative, greater than 64, no special limitation)
	 */
	private int amount;

	/**
	 * Damage code for item : 0 to 255
	 * Equals ItemStack.getDurability() for items that can be damaged
	 * Is 0 for non damaged on non-applicable items
	 */
	private short damage;

	private Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

	//######################################################################################## PUBLIC

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(ItemStack itemStack)
	{
		this(
			itemStack.getTypeId(), itemStack.getAmount(),
			itemStack.getDurability(), itemStack.getEnchantments()
		);
	}

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(net.minecraft.server.v1_6_R2.ItemStack itemStack)
	{
		this(itemStack.id, itemStack.count, (short) itemStack.getData());
	}

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(int itemTypeId)
	{
		super(itemTypeId);
		setAmount(1);
	}

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(int typeId, int amount, short durability_variant)
	{
		this(typeId, amount, durability_variant, null);
	}

	//--------------------------------------------------------------------------------- RealItemStack
	public RealItemStack(
		int typeId, int amount, short durability_variant, Map<Enchantment, Integer> enchantments
	) {
		super(typeId, durability_variant);
		setAmount(amount);
		setDamage(durability_variant);
		if (enchantments != null) {
			for (Enchantment enchantment : enchantments.keySet()) {
				this.enchantments.put(enchantment, enchantments.get(enchantment));
			}
		}
	}

	//-------------------------------------------------------------------------------- cloneItemStack
	public static ItemStack cloneItem(ItemStack itemStack)
	{
		ItemStack newItemStack = new ItemStack(
			itemStack.getType(), itemStack.getAmount(), itemStack.getDurability(),
			itemStack.getData().getData()
		);
		if (newItemStack.getEnchantments() != null) {
			newItemStack.addEnchantments(newItemStack.getEnchantments());
		}
		return newItemStack;
	}

	//---------------------------------------------------------------------------------------- create
	public static RealItemStack create(ItemStack itemStack)
	{
		return (itemStack == null) ? new RealItemStack(0) : new RealItemStack(itemStack);
	}

	//------------------------------------------------------------------------------------- getAmount
	public int getAmount()
	{
		return amount;
	}

	//------------------------------------------------------------------------------------- getDamage
	public short getDamage()
	{
		return damage;
	}

	//--------------------------------------------------------------------------------- getDurability
	public short getDurability()
	{
		return typeIdHasDamage(getTypeId()) ? getDamage() : getVariant();
	}

	//--------------------------------------------------------------------------- getEnchantmentLevel
	public Integer getEnchantmentLevel(Enchantment enchantment)
	{
		return enchantments.get(enchantment);
	}

	//------------------------------------------------------------------------------- getEnchantments
	public Set<Enchantment> getEnchantments()
	{
		return enchantments.keySet();
	}

	//----------------------------------------------------------------------------------- getItemType
	public RealItemType getItemType()
	{
		return new RealItemType(getTypeId(), getVariant());
	}

	//------------------------------------------------------------------------------------- setAmount
	public void setAmount(int amount)
	{
		this.amount = amount;
	}

	//------------------------------------------------------------------------------------- setDamage
	public void setDamage(short damage)
	{
		if (typeIdHasDamage(getTypeId())) {
			this.damage = damage;
		}
	}

	//------------------------------------------------------------------------------------- setTypeId
	@Override
	public void setTypeId(int typeId)
	{
		super.setTypeId(typeId);
		if (!typeIdHasDamage(typeId)) {
			setDamage((short)0);
		}
	}

	//----------------------------------------------------------------------------------- toItemStack
	public ItemStack toItemStack()
	{
		return new ItemStack(getTypeId(), getAmount(), getDamage());
	}

	//--------------------------------------------------------------------------------- toNamedString
	public String toNamedString()
	{
		return super.toNamedString() + " x " + getAmount()
				+ ((getDamage() > 0) ? " (" + getDamage() + ")" : "");
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		return super.toString() + "x" + getAmount()
			+ ((getDamage() > 0) ? "(" + getDamage() + ")" : "");
	}

}
