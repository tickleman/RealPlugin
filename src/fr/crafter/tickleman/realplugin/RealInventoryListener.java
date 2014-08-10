package fr.crafter.tickleman.realplugin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event.Result;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

//########################################################################### RealInventoryListener
public class RealInventoryListener implements Listener
{

	/** Which inventory object has clicked the player ? himself, a chest, a furnace, ... ? */
	private Map <HumanEntity, Inventory> playerInventory = new HashMap <HumanEntity, Inventory>();

	//--------------------------------------------------------------------------------- availableRoom
	/**
	 * Return available quantity for the item stack into the inventory
	 *
	 * @param inventory the inventory to put the item stack into
	 * @param itemStack the item stack we want to put into the inventory
	 * @return integer available quantity, 0 if no room, itemStack.getAmount() if there is enough room
	 */
	public int availableRoom(Inventory inventory, ItemStack itemStack)
	{
		int toStore = itemStack.getAmount();
		if (inventory.firstEmpty() > -1) {
			return itemStack.getAmount();
		} else {
			for (ItemStack slot : inventory.getContents()) {
				if (	
					(slot.getType().name().equals(itemStack.getType().name()))
					&& (slot.getType().getMaxStackSize() > slot.getAmount())
				) {
					toStore -= (slot.getType().getMaxStackSize() - slot.getAmount());
					if (toStore <= 0) {
						return itemStack.getAmount();
					}
				}
			}
			return itemStack.getAmount() - toStore;
		}
	}

	//------------------------------------------------------------------------------ clickedInventory
	public Inventory clickedInventory(InventoryClickEvent event)
	{
		return (event.getRawSlot() >= 0) && (event.getRawSlot() < (event.getInventory().getSize()))
			? event.getView().getTopInventory()
			: event.getView().getBottomInventory();
	}

	//------------------------------------------------------------------------ doWhatWillReallyBeDone
	/**
	 * Change the event : do only the calculated action
	 * (not tested, is this really usefull ?)
	 *
	 * @param event
	 */
	public RealInventoryMove doWhatWillReallyBeDone(InventoryClickEvent event)
	{
		RealInventoryMove inventoryMove = whatWillReallyBeDone(event);
		event.setResult(Result.ALLOW);
		event.getView().setCursor(inventoryMove.getCursor());
		event.setCurrentItem(inventoryMove.getItem());
		event.setCancelled(true);
		return inventoryMove;
	}

	//------------------------------------------------------------------------------- insideInventory
	/**
	 * Which inventory is opened on the player's view ?
	 *
	 * @param Player player
	 * @return Inventory
	 */
	public Inventory insideInventory(HumanEntity player)
	{
		return playerInventory.get(player);
	}

	//------------------------------------------------------------------------------ onInventoryClose
  public void onInventoryClose(InventoryCloseEvent event)
  {
		if (event.getInventory() != event.getPlayer().getInventory()) {
			playerInventory.remove(event.getPlayer());
		}
  }

	//------------------------------------------------------------------------------- onInventoryOpen
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if (event.getInventory() != event.getPlayer().getInventory()) {
			playerInventory.put(event.getPlayer(), event.getInventory());
		}
	}

	//----------------------------------------------------------------------------- rightClickOnlyOne
	/**
	 * If right-click on a stack containing items and cursor is empty :
	 * take only one item on inventory, instead of 50%
	 * All other configurations : standard behavior
	 *
	 * @param event
	 */
	public void rightClickOnlyOne(InventoryClickEvent event)
	{
		if (
			event.getCursor().getType().equals(Material.AIR)
			&& !event.getCurrentItem().getType().equals(Material.AIR)
			&& !event.isLeftClick() && !event.isShiftClick()
		) {
			event.setResult(Result.ALLOW);
			event.getView().setCursor(event.getCurrentItem());
			event.getCursor().setAmount(1);
			if (event.getCurrentItem().getAmount() == 1) {
				event.setCurrentItem(new ItemStack(Material.AIR));
			} else {
				event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
			}
			event.setCancelled(true);
		}
	}

	//-------------------------------------------------------------------------- whatWillReallyBeDone
	/**
	 * Return the real quantities that will be moved when the event will be done (item exchanges)
	 * 
	 * Calculates the quantity into the cursor that will be released (null if none)
	 * and the quantity from the slot (item) that will be taken
	 * 
	 * This tries to respect Minecraft game's rules to tell the developer the really moving quantities
	 *
	 * @param InventoryClickEvent event
	 * @return ItemStack[2] {cursor, item}
	 */
	public RealInventoryMove whatWillReallyBeDone(InventoryClickEvent event)
	{
		ItemStack cursor = event.getCursor().clone();
		ItemStack item   = event.getCurrentItem().clone();
		if (!event.getSlotType().equals(SlotType.OUTSIDE)) {
			if (event.isShiftClick()) {
				//System.out.println("shift click");
				// shift click : check if there is enough room into the destination inventory
				if (!item.getType().equals(Material.AIR)) {
					//System.out.println("shift click : check if there is enough room into the destination inventory");
					//System.out.println("- item is not null");
					Inventory checkInventory = event.getInventory().getName().equals("Inventory")
						? insideInventory(event.getWhoClicked())
						: event.getWhoClicked().getInventory();
					int room = availableRoom(checkInventory, item);
					if (room < item.getAmount()) {
						//System.out.println("-- room < item.getAmount()");
						if (room > 0) {
							//System.out.println("--- ok");
							item.setAmount(room);
						} else {
							//System.out.println("--- cant");
							item = new ItemStack(Material.AIR);
						}
					}
					cursor = new ItemStack(Material.AIR);
				}
			} else if (event.isLeftClick()) {
				//System.out.println("left click");
				// left click into the same item : check if there is enough room into the destination slot
				if (
					!item.getType().equals(Material.AIR)
					&& !cursor.getType().equals(Material.AIR)
					&& (item.getType().equals(cursor.getType()))
				) {
					//System.out.println("left click into the same item : check if there is enough room into the destination slot");
					int room = Math.min(cursor.getAmount(), item.getType().getMaxStackSize() - item.getAmount());
					if (room > 0) {
						//System.out.println("-- room > 0 => ok");
						cursor.setAmount(room);
					} else {
						//System.out.println("-- room == 0 => cant");
						cursor = new ItemStack(Material.AIR);
					}
					item = new ItemStack(Material.AIR);
				}
			} else if (item.getType().equals(Material.AIR) && !cursor.getType().equals(Material.AIR)) {
				//System.out.println("right click on an empty slot : cursor 1");
				// right click on an empty slot : cursor 1
				cursor.setAmount(1);
			} else if (cursor.getType().equals(Material.AIR) && !item.getType().equals(Material.AIR)) {
				//System.out.println("right click to item from slot : item 50%");
				// right click to item from slot : item 50%
				item.setAmount((int)Math.ceil(item.getAmount() / 2.0));
			} else if (
				!item.getType().equals(Material.AIR)
				&& !cursor.getType().equals(Material.AIR)
				&& (item.getType().equals(cursor.getType()))
			) {
				//System.out.println("right click into the same item : check if there is enough room into the destination slot to cursor 1");
				// right click into the same item : check if there is enough room into the destination slot to cursor 1
				if (item.getType().getMaxStackSize() > item.getAmount()) {
					//System.out.println("- getmaxstacksize > item.getamount => setamount(1)");
					cursor.setAmount(1);
				} else {
					//System.out.println("- getmaxstacksize <= item.getamount => cant");
					cursor = new ItemStack(Material.AIR);
				}
				item = new ItemStack(Material.AIR);
			}
		}
		return new RealInventoryMove(cursor, item);
	}

}
