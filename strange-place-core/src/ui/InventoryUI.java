package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;

import inventory.*;
import inventory.items.*;
import inventory.items.InventoryItem.ItemTypeID;
import observers.InventoryObserver;
import observers.InventoryObserver.InventoryEvent;
import observers.InventorySlotObserver;
import observers.subjects.InventorySubject;
import utility.AssetHandler;

public class InventoryUI extends Window implements InventorySubject, InventorySlotObserver
{
	public static final String TAG = InventoryUI.class.getSimpleName();
	
	public static final String PLAYER_INVENTORY = "Player_Inventory";
	public static final String STORE_INVENTORY = "Store_Inventory";
	
	public static final int NUM_SLOTS = 30;
	//private final int SLOT_WIDTH = 32;
	//private final int SLOT_HEIGHT = 32;
	private final int SLOT_WIDTH = 64;
	private final int SLOT_HEIGHT = 64;

	private int lengthSlotRow = 10;

	private Table inventorySlotTable;

	private static DragAndDrop dragAndDrop;

	private Array<Actor> inventoryActors;
	private Array<InventorySlot> inventorySlots;
	private InventorySlotTooltip inventorySlotTooltip;

	private Array<InventoryObserver> observers;
	
	private ItemActionsWindow itemActionsWindow;

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryUI constructor
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public InventoryUI()
	{
		//super("", AssetHandler.INVENTORY_SKIN, "inventory");
		super("", AssetHandler.INVENTORY_SKIN, "inventory-window");

		this.observers = new Array<InventoryObserver>();
		this.inventoryActors = new Array<Actor>();
		this.inventorySlots = new Array<InventorySlot>();
		
		this.inventorySlotTooltip = new InventorySlotTooltip(AssetHandler.INVENTORY_SKIN);
		inventoryActors.add(inventorySlotTooltip);
		
		this.itemActionsWindow = new ItemActionsWindow(AssetHandler.INVENTORY_SKIN);
		inventoryActors.add(itemActionsWindow);
		
		InventoryUI.dragAndDrop = new DragAndDrop();

		// Create
		this.inventorySlotTable = new Table();
		
		inventorySlotTable.setName("Inventory Slot Table");
		inventorySlotTable.setTouchable(Touchable.enabled);
		
		// Layout
		for (int i = 1; i <= NUM_SLOTS; i++)
		{
			InventorySlot inventorySlot = new InventorySlot(i);
			
			inventorySlots.add(inventorySlot);
			
			inventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
			inventorySlot.addListener(new ItemActionsWindowListener(itemActionsWindow));
			
			dragAndDrop.addTarget(new InventorySlotTarget(inventorySlot));
			
			inventorySlotTable.add(inventorySlot).size(SLOT_WIDTH, SLOT_HEIGHT);
			if (i % lengthSlotRow == 0)
			{
				inventorySlotTable.row();
			}
		}

		this.add(inventorySlotTable).row();
		//this.setScale(2f);
		//this.setScale(1.5f);
		this.setMovable(true);
		this.pack();
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryUI initialization
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	/**
	 * The PopulateInventory() method is used when loading inventory items from a
	 * save game profile
	 * 
	 * @param targetTable
	 *            The Table widget used for storing the InventoryItem objects
	 * @param inventoryItems
	 *            The Array of InventoryItem objects, re-wrapped into a POJO class
	 *            InventoryItemLocation, that go into the table
	 * @param draganddrop
	 *            The DragAndDrop class that these will use
	 */
	@SuppressWarnings("rawtypes")
	public static void populateInventory(Table targetTable,
			Array<InventoryItemLocation> inventoryItems,
			DragAndDrop draganddrop,
			String defaultName,
			boolean disableNonDefaultItems)
	{
		clearInventoryItems(targetTable);
		
		//Gdx.app.debug(TAG, "Now in populateInventory() method");

		Array<Cell> cells = targetTable.getCells();
		for (int i = 0; i < inventoryItems.size; i++)
		{
			InventoryItemLocation itemLocation = inventoryItems.get(i);
			ItemTypeID itemTypeID = ItemTypeID.valueOf(itemLocation.getItemTypeAtLocation());
			InventorySlot inventorySlot = ((InventorySlot) cells.get(itemLocation.getLocationIndex()).getActor());
			
			InventoryItem item = InventoryItemFactory.getInstance().getInventoryItem(itemTypeID);
			
			//item.setName(item.getItemTypeID().name());
			String itemName =  itemLocation.getItemNameProperty();
			if(itemName == null || itemName.isEmpty())
			{
				//Gdx.app.debug(TAG, "\tSetting defaultName for item " + item.getItemTypeID() + ": " + defaultName);
				item.setName(defaultName);
			}
			else
			{
				//Gdx.app.debug(TAG, "\tSetting itemName for item " + item.getItemTypeID() + ": " + itemName);
				item.setName(itemName);
			}
			
			inventorySlot.addItem(item, itemLocation.getNumberItemsAtLocation());
			draganddrop.addSource(new InventorySlotSource(inventorySlot, draganddrop));
			
			//if(itemLocation.getNumberItemsAtLocation() > 1)
			//{
			//	for(int index = 1; index < itemLocation.getNumberItemsAtLocation(); index++)
			//	{
					//Gdx.app.debug(TAG, "Now incrementing item count at " + inventorySlot);
			//		inventorySlot.incrementItemCount(false);
			//	}
			//}
		}
		
		//Gdx.app.debug(TAG, "Now exiting populateInventory() method");
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryUI getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Table getInventorySlotTable()
	{
		return inventorySlotTable;
	}

	public DragAndDrop getDragAndDrop()
	{
		return dragAndDrop;
	}

	public Array<Actor> getInventoryActors()
	{
		return inventoryActors;
	}
	
	public InventorySlot getFirstEmptySlot()
	{
		for(int i = 0; i < inventorySlots.size; i++)
		{
			if(!inventorySlots.get(i).hasItem())
			{
				return inventorySlots.get(i);
			}
		}
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Array<InventoryItemLocation> getInventory(Table targetTable)
	{
		Array<Cell> cells = targetTable.getCells();
		Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();

		for (int i = 0; i < cells.size; i++)
		{
			InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());

			if (inventorySlot == null)
				continue;

			int numItems = inventorySlot.getNumItems();
			
			if (numItems > 0)
			{
				String itemTypeIDString = inventorySlot.getInventoryItem().getItemTypeID().toString();
				String itemName = inventorySlot.getInventoryItem().getName();
				items.add(new InventoryItemLocation(i, itemTypeIDString, numItems, itemName));
			}
		}
		return items;
	}

	@SuppressWarnings("rawtypes")
	public static Array<InventoryItemLocation> getInventory(Table targetTable, String name)
	{
		Array<Cell> cells = targetTable.getCells();
		Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();

		for (int i = 0; i < cells.size; i++)
		{
			InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
			if (inventorySlot == null)
				continue;
			
			int numItems = inventorySlot.getNumItems(name);
			
			if (numItems > 0)
			{
				// System.out.println("[i] " + i + " itemtype: " +
				// inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems "
				// + numItems);
				items.add(new InventoryItemLocation(i,
						inventorySlot.getInventoryItem().getItemTypeID().toString(),
						numItems,
						name));
			}
		}
		return items;
	}

	@SuppressWarnings("rawtypes")
	public static Array<InventoryItemLocation> getInventoryFiltered(Table targetTable, String filterOutName)
	{
		Array<Cell> cells = targetTable.getCells();
		Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();

		for (int i = 0; i < cells.size; i++)
		{
			InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
			
			if (inventorySlot == null)
				continue;
			
			int numItems = inventorySlot.getNumItems();
			
			if (numItems > 0)
			{
				String topItemName = inventorySlot.getInventoryItem().getName();
				if (topItemName.equalsIgnoreCase(filterOutName))
					continue;
				
				// System.out.println("[i] " + i + " itemtype: " +
				// inventorySlot.getTopInventoryItem().getItemTypeID().toString() + " numItems "
				// + numItems);
				items.add(new InventoryItemLocation(i, inventorySlot.getInventoryItem().getItemTypeID().toString(),
						numItems, inventorySlot.getInventoryItem().getName()));
			}
		}
		return items;
	}

	@SuppressWarnings("rawtypes")
	public static Array<InventoryItemLocation> getInventoryFiltered(Table sourceTable,
			Table targetTable,
			String filterOutName)
	{
		Array<InventoryItemLocation> items = getInventoryFiltered(targetTable, filterOutName);
		Array<Cell> sourceCells = sourceTable.getCells();

		for (InventoryItemLocation item : items)
		{
			int index = 0;
			
			for (; index < sourceCells.size; index++)
			{
				InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
				
				if (inventorySlot == null)
					continue;
				
				int numItems = inventorySlot.getNumItems();
				
				if (numItems == 0)
				{
					item.setLocationIndex(index);
					// System.out.println("[index] " + index + " itemtype: " +
					// item.getItemTypeAtLocation() + " numItems " + numItems);
					index++;
					break;
				}
			}
			
			if (index == sourceCells.size)
			{
				// System.out.println("[index] " + index + " itemtype: " +
				// item.getItemTypeAtLocation() + " numItems " +
				// item.getNumberItemsAtLocation());
				item.setLocationIndex(index - 1);
			}
		}
		return items;
	}
	
	public ItemActionsWindow getItemsActionWindow()
	{
		return itemActionsWindow;
	}
	
	public Array<InventorySlot> getTopRowSlots()
	{
		Array<InventorySlot> topRowSlots = new Array<InventorySlot>();
		
		for(int i = 0; i < 10; i++)
		{
			topRowSlots.add(inventorySlots.get(i));
		}
		
		return topRowSlots;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Setters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */

	@SuppressWarnings("rawtypes")
	public static void setInventoryItemNames(Table targetTable, String name)
	{
		Array<Cell> cells = targetTable.getCells();
		for (int i = 0; i < cells.size; i++)
		{
			InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
			
			if (inventorySlot == null)
				continue;
			
			inventorySlot.updateAllInventoryItemNames(name);
		}
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryUI updates
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public void addItemToInventory(ItemTypeID itemID, String itemName)
	{
		//Array<Cell> sourceCells = inventorySlotTable.getCells();
		
		boolean itemAdded = false;
		
		for(int i = 0; i < inventorySlots.size; i++)
		{
			InventorySlot inventorySlot = inventorySlots.get(i);
			
			//Gdx.app.debug(TAG, "Going through addItemToInventory for loop, looking at inventorySlot " + inventorySlot);
			
			if(inventorySlot.hasItem() && inventorySlot.getInventoryItem().getItemTypeID() == itemID)
			{
				Gdx.app.debug(TAG, "Adding item " + itemID + " by incrementing item count");
				
				InventoryItem inventoryItem = InventoryItemFactory.getInstance().getInventoryItem(itemID);
				inventoryItem.setName(itemName);
				inventorySlots.get(i).addItem(inventoryItem, 1);
				itemAdded = true;
				break;
			}
		}
		
		if(!itemAdded)
		{
			Gdx.app.debug(TAG, "Adding item to first empty slot");
			
			InventorySlot firstEmptySlot = getFirstEmptySlot();
			
			InventoryItem inventoryItem = InventoryItemFactory.getInstance().getInventoryItem(itemID);
			inventoryItem.setName(itemName);
			firstEmptySlot.addItem(inventoryItem, 1);
			dragAndDrop.addSource(new InventorySlotSource(firstEmptySlot, dragAndDrop));
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean doesInventoryHaveSpace()
	{
		Array<Cell> sourceCells = inventorySlotTable.getCells();

		for (int index = 0; index < sourceCells.size; index++)
		{
			InventorySlot inventorySlot = ((InventorySlot) sourceCells.get(index).getActor());
			if (inventorySlot == null)
				continue;
			int numItems = inventorySlot.getNumItems();
			if (numItems == 0)
			{
				return true;
			}
			else
			{
				index++;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static Array<InventoryItemLocation> removeInventoryItems(String name, Table inventoryTable)
	{
		Array<Cell> cells = inventoryTable.getCells();
		Array<InventoryItemLocation> items = new Array<InventoryItemLocation>();
		for (int i = 0; i < cells.size; i++)
		{
			InventorySlot inventorySlot = ((InventorySlot) cells.get(i).getActor());
			if (inventorySlot == null)
				continue;
			inventorySlot.removeAllInventoryItemsWithName(name);
		}
		return items;
	}

	@SuppressWarnings("rawtypes")
	public static void clearInventoryItems(Table targetTable)
	{
		Array<Cell> cells = targetTable.getCells();
		for (int i = 0; i < cells.size; i++)
		{
			InventorySlot inventorySlot = (InventorySlot) cells.get(i).getActor();
			if (inventorySlot == null)
				continue;
			inventorySlot.clearAllInventoryItems(false);
		}
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryUI observer pattern overrides
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public void onInventorySlotNotify(InventorySlot slot, SlotEvent event)
	{
		switch (event)
		{
		case ADDED_ITEM:
			Gdx.app.debug(TAG, "ADDED_ITEM event triggered for " + slot.getName());
			
			InventoryItem addItem = slot.getInventoryItem();
			
			if (addItem == null)
				return;
			break;
			
		case REMOVED_ITEM:
			Gdx.app.debug(TAG, "REMOVED_ITEM event triggered for " + slot.getName());
			
			InventoryItem removeItem = slot.getInventoryItem();
			if (removeItem == null)
				return;
			
			break;
			
		default:
			break;
		}
	}

	@Override
	public void addInventoryObserver(InventoryObserver inventoryObserver)
	{
		observers.add(inventoryObserver);
	}

	@Override
	public void removeInventoryObserver(InventoryObserver inventoryObserver)
	{
		observers.removeValue(inventoryObserver, true);
	}

	@Override
	public void removeAllInventoryObservers()
	{
		for (InventoryObserver observer : observers)
		{
			observers.removeValue(observer, true);
		}
	}

	@Override
	public void notifyInventoryObservers(InventoryItem.ItemTypeID item, InventoryEvent event)
	{
		for (InventoryObserver observer : observers)
		{
			observer.onInventoryNotify(item, event);
		}
	}
}
