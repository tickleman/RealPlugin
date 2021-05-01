package fr.crafter.tickleman.realplugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

//#################################################################################### RealItemType
public class RealItemType
{

	/**
	 * Minecraft type identifier of item
	 */
	private String typeId;

	/**
	 * Variant code of item, for items than can have variants
	 * Equals ItemStack.getDurability() for items that can be damaged
	 * Is null for non-applicable items
	 */
	private short variant;

	//------------------------------------------------------------------------------------- ItemStack
	public RealItemType(ItemStack itemStack)
	{
		this(itemStack.getType().name(), itemStack.getDurability());
	}

	//------------------------------------------------------------------------------------- ItemStack
	public RealItemType(RealItemStack itemStack)
	{
		this(itemStack.getTypeId(), itemStack.getDurability());
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(Material material)
	{
		this(material.name(), (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(Material material, short variant)
	{
		this(material.name(), variant);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(String typeId)
	{
		this(typeId, (short)0);
	}

	//-------------------------------------------------------------------------------------- ItemType
	public RealItemType(String typeId, short variant)
	{
		setTypeIdVariant(typeId, variant);
	}

	//----------------------------------------------------------------------------------- getMaterial
	public Material getMaterial()
	{
		return Material.valueOf(this.typeId);
	}

	//------------------------------------------------------------------------------------- getNameOf
	private static String getNameOf(Object object)
	{
		String name = null;
		if (object != null) {
			for (Method method : object.getClass().getDeclaredMethods()) {
				if ((method.getParameterTypes().length == 0)) {
					if (method.getReturnType().getName().equals("java.lang.String")) {
						//noinspection CatchMayIgnoreException
						try {
							name = (String) method.invoke(object);
							break;
						}
						catch (Exception e) {
						}
					}
					else if (
						method.getName().equals("getParent")
						&& (method.getParameterTypes().length == 0)
					) {
						//noinspection CatchMayIgnoreException
						try {
							Object object2 = method.invoke(object);
							name = getNameOf(object2);
							if (name.length() > 0) {
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
				}
				else if (name.substring(0, 4).equalsIgnoreCase("item")) {
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
	public static String getName(String typeId)
	{
		System.out.print("i Change " + typeId);
		Object object = Material.valueOf(typeId).toString();
		String name = getNameOf(object);
		for (int i = 0; i < name.length(); i ++) {
			if ((name.charAt(i) >= 'A') && (name.charAt(i) <= 'Z')) {
				if (i == 0) {
					name = (char)(name.charAt(i) - 'A' + 'a') + name.substring(i + 1);
				}
				else if (name.charAt(i - 1) == ' ') {
					name = name.substring(0, i) + (char)(name.charAt(i) - 'A' + 'a')
						+ name.substring(i + 1);
				}
				else {
					name = name.substring(0, i) + " " + (char)(name.charAt(i) - 'A' + 'a')
						+ name.substring(i + 1);
				}
			}
		}
		System.out.println(" into " + name);
		return name;
	}

	//------------------------------------------------------------------------------------- getTypeId
	public String getTypeId()
	{
		return typeId;
	}

	//------------------------------------------------------------------------------------ getVariant
	public short getVariant()
	{
		return variant;
	}

	//--------------------------------------------------------------------------------------- isBlock
	public static boolean isBlock(String typeId)
	{
		return Material.valueOf(typeId).isBlock();
	}

	//--------------------------------------------------------------------------------- parseItemType
	public static RealItemType parseItemType(String typeIdVariant)
	{
		if (typeIdVariant.contains(":")) {
			String[] split = typeIdVariant.split(":");
			return new RealItemType(split[0], Short.parseShort(split[1]));
		} else {
			return new RealItemType(typeIdVariant);
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
	public void setTypeId(String typeId)
	{
		setTypeIdVariant(typeId, variant);
	}

	//------------------------------------------------------------------------------------ isSameItem
	public boolean isSameItem(RealItemType itemType)
	{
		return (itemType.getTypeId().equals(getTypeId())) && (itemType.getVariant() == getVariant());
	}

	//------------------------------------------------------------------------------ setTypeIdVariant
	public void setTypeIdVariant(String typeId, short variant)
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
	public static Boolean typeIdHasDamage(String typeId)
	{
		return !typeIdHasVariant(typeId);
	}

	//------------------------------------------------------------------------------ typeIdHasVariant
	public static Boolean typeIdHasVariant(String typeId)
	{
		return
			// those codes have variant : durability is an item variant instead of damage
			// - blocks
			   (typeId.equals(Material.LEGACY_WOOD.name())) // 5
			|| (typeId.equals(Material.LEGACY_SAPLING.name())) // 6
			|| (typeId.equals(Material.LEGACY_LOG.name())) // 17
			|| (typeId.equals(Material.LEGACY_LEAVES.name())) // 18
			|| (typeId.equals(Material.LEGACY_LONG_GRASS.name())) // 31
			|| (typeId.equals(Material.LEGACY_WOOL.name())) // 35
			|| (typeId.equals(Material.LEGACY_DOUBLE_STEP.name())) // 43
			|| (typeId.equals(Material.LEGACY_STEP.name())) // 44
			|| (typeId.equals(Material.JUKEBOX.name())) // 84
			|| (typeId.equals(Material.LEGACY_SMOOTH_BRICK.name())) // 98
			|| (typeId.equals(Material.LEGACY_HUGE_MUSHROOM_1.name())) // 99
			|| (typeId.equals(Material.LEGACY_HUGE_MUSHROOM_2.name())) // 100
			|| (typeId.equals(Material.LEGACY_WOOD_DOUBLE_STEP.name())) // 125
			|| (typeId.equals(Material.LEGACY_WOOD_STEP.name())) // 126
			|| (typeId.equals(Material.LEGACY_COBBLE_WALL.name())) // 139
			|| (typeId.equals(Material.LEGACY_SKULL.name())) // 144
			|| (typeId.equals(Material.QUARTZ_BLOCK.name())) // 155
			|| (typeId.equals(Material.LEGACY_HARD_CLAY.name())) // 159
			|| (typeId.equals(Material.LEGACY_LEAVES_2.name())) // 161
			|| (typeId.equals(Material.LEGACY_LOG_2.name())) // 162
			|| (typeId.equals(Material.LEGACY_CARPET.name())) // 171
			// - items
			|| (typeId.equals(Material.COAL.name())) // 263
			|| (typeId.equals(Material.GOLDEN_APPLE.name())) // 322
			|| (typeId.equals(Material.LEGACY_RAW_FISH.name())) // 349
			|| (typeId.equals(Material.LEGACY_COOKED_FISH.name())) // 350
			|| (typeId.equals(Material.LEGACY_INK_SACK.name())) // 351
			|| (typeId.equals(Material.MAP.name())) // 358
			|| (typeId.equals(Material.POTION.name())) // 373
			|| (typeId.equals(Material.LEGACY_MONSTER_EGG.name())) // 383
			|| (typeId.equals(Material.LEGACY_BOOK_AND_QUILL.name())) // 386
			|| (typeId.equals(Material.LEGACY_SKULL_ITEM.name())) // 397
		;
	}

	//------------------------------------------------------------------------------- typeIdMaxDamage
	public static short typeIdMaxDamage(String typeId)
	{
		if (typeIdHasVariant(typeId)) {
			return 0;
		}
		else if (isBlock(typeId)) {
			/*
			// this could be easily broken on bukkit next updates,
			// but I mean that blocks are never traded with a damage value, as they are
			// damaged only when you hit them, and are never damaged when in inventories
			//noinspection CatchMayIgnoreException,TryWithIdenticalCatches
			try {
				Field strength = Block.class.getField("durability");
				if (!strength.isAccessible()) {
					strength.setAccessible(true);
				}
				// TODO Check if e() is the right replacement for getById[]
				return (short) Math.round(strength.getDouble(Block.b(typeId)));
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			} catch (IllegalAccessException e) {
			} catch (NoSuchFieldException e) {
			}
			System.out.println("default damage 128 for item id " + typeId);
			*/
			return 128;
		}
		else {
			Material material = Material.getMaterial(typeId);
			return (material == null) ? 0 : material.getMaxDurability();
		}
	}

	//-------------------------------------------------------------------------------- typeIdVariants
	public static short[] typeIdVariants(RealItemType parseItemType)
	{
		if (
			parseItemType.typeId.equals(Material.LEGACY_LOG.name())
			|| parseItemType.typeId.equals(Material.LEGACY_LEAVES.name())
			|| parseItemType.typeId.equals(Material.LEGACY_SAPLING.name())
		) {
			return new short[]{0, 1, 2, 3, 4, 5, 6, 7};
		}
		else if (
			parseItemType.typeId.equals(Material.LEGACY_DOUBLE_STEP.name())
			|| parseItemType.typeId.equals(Material.LEGACY_STEP.name())
		) {
			return new short[]{0, 1, 2, 3, 4, 5, 6};
		}
		else if (
			parseItemType.typeId.equals(Material.LEGACY_WOOL.name())
			|| parseItemType.typeId.equals(Material.LEGACY_INK_SACK.name())
		)
		{
			return new short[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		}
		else if (parseItemType.typeId.equals(Material.COAL.name())) {
			return new short[]{0, 1};
		}
		else if (parseItemType.typeId.equals(Material.POTION.name())) {
			return new short[]{
				0, 16, 32, 64, 8192, 8193, 8257, 8225, 8194, 8258, 8226, 8195, 8259, 8197, 8229, 8201, 8265,
				8233, 8196, 8260, 8228, 8200, 8264, 8202, 8266, 8204, 8236, 16384, 16385, 16449, 16417,
				16386, 16450, 16418, 16387, 16451, 16389, 16421, 16393, 16457, 16425, 16388, 16452, 16420,
				16392, 16456, 16394, 16458, 16396, 16428
			};
		}
		else if (parseItemType.typeId.equals(Material.LEGACY_MONSTER_EGG.name())) {
			return new short[]{
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
				90, 91, 92, 93, 94, 95, 96, 97, 98,
				120
			};
		}
		else {
			return new short[]{};
		}
	}

}
