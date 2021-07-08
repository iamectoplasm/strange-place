package ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;

import inventory.InventorySlot;
import inventory.items.InventoryItem.ItemTypeID;
import observers.InventoryObserver;
import observers.ItemBarObserver;
import observers.ItemBarObserver.ItemBarEvent;
import observers.subjects.ItemBarSubject;
import utility.AssetHandler;

public class ItembarUI extends Window implements ItemBarSubject
{
	private Array<ItemBarObserver> observers;
	
	private Table layoutTable;
	private Array<InventorySlot> inventorySlots;
	
	private int selectedIndex;
	private Image selectedOverlay;
	
	private final int SLOT_WIDTH = 32;
	private final int SLOT_HEIGHT = 32;

	public ItembarUI()
	{
		super("", AssetHandler.ITEMBAR_SKIN);
		
		this.observers = new Array<ItemBarObserver>();
		
		inventorySlots = new Array<InventorySlot>();
		layoutTable = new Table();
		layoutTable.padLeft(10);
		
		for(int i = 0; i < 10; i++)
		{
			InventorySlot newSlot = new InventorySlot(i);
			inventorySlots.add(newSlot);
			layoutTable.add(newSlot).size(SLOT_WIDTH, SLOT_HEIGHT).padRight(10);
		}
		
		selectedIndex = 0;
		selectedOverlay = new Image(AssetHandler.ITEMBAR_TEXTUREATLAS.createSprite("toolbar-selection-overlay"));
		inventorySlots.get(selectedIndex).add(selectedOverlay);
		
		this.add(layoutTable).row();
		
		this.pack();
	}
	
	public void setInventorySlots(Array<InventorySlot> newSlots)
	{
		for(int i = 0; i < inventorySlots.size; i++)
		{
			InventorySlot itemBarSlot = inventorySlots.get(i);
			InventorySlot newSlot = newSlots.get(i);
			inventorySlots.get(i).addItem(newSlot.getInventoryItem(), newSlot.getNumItems());
		}
	}
	
	public void moveSelectedRight()
	{
		if(selectedIndex < 9)
		{
			inventorySlots.get(selectedIndex).removeActor(selectedOverlay);
			selectedIndex += 1;
			inventorySlots.get(selectedIndex).add(selectedOverlay);
		}
	}
	
	public void moveSelectedLeft()
	{
		if(selectedIndex > 0)
		{
			inventorySlots.get(selectedIndex).removeActor(selectedOverlay);
			selectedIndex -= 1;
			inventorySlots.get(selectedIndex).add(selectedOverlay);
		}
	}

	@Override
	public void addItemBarObserver(ItemBarObserver itemBarObserver)
	{
		observers.add(itemBarObserver);
	}

	@Override
	public void removeItemBarObserver(ItemBarObserver itemBarObserver)
	{
		observers.removeValue(itemBarObserver, true);
	}

	@Override
	public void removeAllItemBarObservers()
	{
		for (ItemBarObserver observer : observers)
		{
			observers.removeValue(observer, true);
		}
	}

	@Override
	public void notifyItemBarObservers(InventorySlot slot, ItemBarEvent event)
	{
		for (ItemBarObserver observer : observers)
		{
			observer.onItemBarNotify(slot, event);
		}
	}

}
