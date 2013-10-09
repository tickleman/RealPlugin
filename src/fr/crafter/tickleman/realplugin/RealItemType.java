package fr.crafter.tickleman.realplugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.Item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

//#################################################################################### RealItemType
public class RealItemType
{

	/**
	 * Minecraft type identifier of item
	 */
	private int typeId;

	/**
	 * Variant code of item, for items than can have variants
	 * Equals ItemStack.getDurability() for items that can be damaged
	 * Is null for non-applicable items
	 */
	private short variant;

	//------------------------------------------------------------------------------------- ItemStack
	public RealItemType(ItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getDurability());
	}

	//------------------------------------------------------------------------------------- ItemStack
	public RealItemType(RealItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getDurability());
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(Material material)
	{
		this(material.getId(), (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(Material material, short variant)
	{
		this(material.getId(), variant);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(int typeId)
	{
		this(typeId, (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(int typeId, short variant)
	{
		setTypeIdVariant(typeId, variant);
	}

	//------------------------------------------------------------------------------------- getNameOf
	private static String getNameOf(Object object)
	{
		String name = null;
		if (object != null) {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if ((method.getParameterTypes().length == 0)) {
					if (method.getReturnType().getName().equals("java.lang.String")) {
						try {
							name = (String) method.invoke(object);
							break;
						} catch (Exception e) {
						}
					} else if (
						method.getName().equals("getParent")
						&& (method.getParameterTypes().length == 0)
					) {
						try {
							Object object2 = method.invoke(object);
							name = getNameOf(object2);
							if ((name != null) && (name.length() > 0)) {
								break;
							}
						} catch (Exception e) {
						}
					}
				}
			}
			if ((name == null) || (name.length() == 0)) {
				name = object.getClass().getName();
			}
			while (name.contains(".")) {
				name = name.substring(name.indexOf(".") + 1);
			}
			if (name.length() > 5) {
				if (name.substring(0, 5).equalsIgnoreCase("block")) {
					name = name.substring(5);
				} else if (name.substring(0, 4).equalsIgnoreCase("item")) {
					name = name.substring(4);
				}
			}
		}
		return (name == null) ? "" : name;
	}

	//--------------------------------------------------------------------------------------- getName
	public String getName()
	{
		return getName(typeId);
	}

	//--------------------------------------------------------------------------------------- getName
	public static String getName(int typeId)
	{
		Object object = ((typeId < 256) ? Block.byId[typeId] : Item.byId[typeId]);
		String name = getNameOf(object);
		for (int i = 0; i < name.length(); i ++) {
			if ((name.charAt(i) >= 'A') && (name.charAt(i) <= 'Z')) {
				if (i == 0) {
					name = (char)(name.charAt(i) - 'A' + 'a') + name.substring(i + 1);
				} else if (name.charAt(i - 1) == ' ') {
					name = name.substring(0, i) + (char)(name.charAt(i) - 'A' + 'a')
						+ name.substring(i + 1);
				} else {
					name = name.substring(0, i) + " " + (char)(name.charAt(i) - 'A' + 'a')
						+ name.substring(i + 1);
				}
			}
		}
		return name;
	}

	//------------------------------------------------------------------------------------- getTypeId
	public int getTypeId()
	{
		return typeId;
	}

	//------------------------------------------------------------------------------------ getVariant
	public short getVariant()
	{
		return variant;
	}

	//--------------------------------------------------------------------------------- parseItemType
	public static RealItemType parseItemType(String typeIdVariant)
	{
		if (typeIdVariant.contains(":")) {
			String[] split = typeIdVariant.split(":");
			return new RealItemType(Integer.parseInt(split[0]), Short.parseShort(split[1]));
		} else {
			return new RealItemType(Integer.parseInt(typeIdVariant));
		}
	}

	//------------------------------------------------------------------------- parseItemTypeKeywords
	public static RealItemType parseItemTypeKeywords(String[] keyWords)
	{
		try {
			return RealItemType.parseItemType(keyWords[0]);
		} catch (Exception e) {
			return RealItemType.parseItemType("0");
		}
	}

	//------------------------------------------------------------------------------------- setTypeId
	public void setTypeId(int typeId)
	{
		setTypeIdVariant(typeId, variant);
	}

	//------------------------------------------------------------------------------------ isSameItem
	public boolean isSameItem(RealItemType itemType)
	{
		return (itemType.getTypeId() == getTypeId()) && (itemType.getVariant() == getVariant());
	}

	//------------------------------------------------------------------------------ setTypeIdVariant
	public void setTypeIdVariant(int typeId, short variant)
	{
		this.typeId = typeId;
		setVariant(variant);
	}

	//------------------------------------------------------------------------------------ setVariant
	public void setVariant(short variant)
	{
		if (typeIdHasVariant(typeId)) {
			this.variant = ((variant < 0) ? 0 : variant);
		} else {
			this.variant = 0;
		}
	}

	//--------------------------------------------------------------------------------- toNamedString
	public String toNamedString()
	{
		return getName() + ((getVariant() != 0) ? " : " + getVariant() : "");
	}

	//-------------------------------------------------------------------------------------- toString
	@Override
	public String toString()
	{
		return getTypeId() + ((getVariant() != 0) ? ":" + getVariant() : "");
	}

	//------------------------------------------------------------------------------- typeIdHasDamage
	public static Boolean typeIdHasDamage(int typeId)
	{
		return !typeIdHasVariant(typeId);
	}

	//------------------------------------------------------------------------------ typeIdHasVariant
	public static Boolean typeIdHasVariant(int typeId)
	{
		return false
			// those codes have variant : durability is an item variant instead of damage
			|| (typeId == Material.WOOD.getId()) // 5
			|| (typeId == Material.SAPLING.getId()) // 6
			|| (typeId == Material.LOG.getId()) // 17
			|| (typeId == Material.LEAVES.getId()) // 18
			|| (typeId == Material.LONG_GRASS.getId()) // 31
			|| (typeId == Material.WOOL.getId()) // 35
			|| (typeId == Material.DOUBLE_STEP.getId()) // 43
			|| (typeId == Material.STEP.getId()) // 44
			|| (typeId == Material.JUKEBOX.getId()) // 84
			|| (typeId == Material.SMOOTH_BRICK.getId()) // 98
			|| (typeId == Material.HUGE_MUSHROOM_1.getId()) // 99
			|| (typeId == Material.HUGE_MUSHROOM_2.getId()) // 100
			|| (typeId == Material.WOOD_DOUBLE_STEP.getId()) // 125
			|| (typeId == Material.WOOD_STEP.getId()) // 126
			|| (typeId == Item.COAL.id) // 263
			|| (typeId == Item.INK_SACK.id) // 351
			|| (typeId == Item.MAP.id) // 358
			|| (typeId == Item.POTION.id) // 373
			|| (typeId == Material.MONSTER_EGG.getId()) // 383
			|| (typeId == Item.BOOK_AND_QUILL.id) // 386
		;
	}

	//------------------------------------------------------------------------------- typeIdMaxDamage
	public static short typeIdMaxDamage(int typeId)
	{
		if (typeIdHasVariant(typeId)) {
			return 0;
		} else if (typeId < 256) {
			// this could be easily broken on craftbukkit's next updates,
			// but I mean that blocks are never traded with a damage value, as they are
			// damaged only when you hit them, and are never damaged when in inventories
			try {
				Field strength = Block.class.getField("durability");
				if (!strength.isAccessible()) {
					strength.setAccessible(true);
				}
				return (short) Math.round(strength.getDouble(Block.byId[typeId]));
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			} catch (IllegalAccessException e) {
			} catch (NoSuchFieldException e) {
			}
			System.out.println("default damage 128 for item id " + typeId);
			return 128;
		} else {
			return (short)Item.byId[typeId].getMaxDurability();
		}
	}

	//-------------------------------------------------------------------------------- typeIdVariants
	public static short[] typeIdVariants(RealItemType parseItemType)
	{
		if (
			parseItemType.typeId == Material.LOG.getId()
			|| parseItemType.typeId == Material.LEAVES.getId()
			|| parseItemType.typeId == Material.SAPLING.getId()
		) {
			short[] variants = {0, 1, 2, 3, 4, 5, 6, 7};
			return variants;
		} else if (
			parseItemType.typeId == Material.DOUBLE_STEP.getId()
			|| parseItemType.typeId == Material.STEP.getId()
		) {
			short[] variants = {0, 1, 2, 3, 4, 5, 6};
			return variants;
		} else if (
			parseItemType.typeId == Material.WOOL.getId()
			|| parseItemType.typeId == Material.INK_SACK.getId()
		) {
			short[] variants = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
			return variants;
		} else if (parseItemType.typeId == Material.COAL.getId()) {
			short[] variants = {0, 1};
			return variants;
		} else if (parseItemType.typeId == Material.POTION.getId()) {
			short[] variants = {
				0, 16, 32, 64, 8192, 8193, 8257, 8225, 8194, 8258, 8226, 8195, 8259, 8197, 8229, 8201, 8265,
				8233, 8196, 8260, 8228, 8200, 8264, 8202, 8266, 8204, 8236, 16384, 16385, 16449, 16417,
				16386, 16450, 16418, 16387, 16451, 16389, 16421, 16393, 16457, 16425, 16388, 16452, 16420,
				16392, 16456, 16394, 16458, 16396, 16428
			};
			return variants;
		} else if (parseItemType.typeId == Material.MONSTER_EGG.getId()) {
			short[] variants = {
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
				90, 91, 92, 93, 94, 95, 96, 97, 98,
				120
			};
			return variants;
		} else {
			short[] variants = {};
			return variants;
		}
	}

}
