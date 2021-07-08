package inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

import inventory.items.InventoryItem;
import observers.InventorySlotObserver;
import observers.subjects.InventorySlotSubject;
import utility.AssetHandler;

public class InventorySlot extends Stack implements InventorySlotSubject
{
	public static final String TAG = InventorySlot.class.getSimpleName();
	
	private String slotName;
	private int slotIndex;
	
	private int filterItemType;
	
	// All slots will have this default background
	private Stack itemBackground;
	
	private Label numItemsLabel;
	private int numItemsVal = 0;

	private Array<InventorySlotObserver> observers;

	public InventorySlot(int indexInTable)
	{
		slotName = "slot at index " + indexInTable;
		slotIndex = indexInTable;
		
		filterItemType = 0;
		
		itemBackground = new Stack();
		observers = new Array<InventorySlotObserver>();
		
		//Image slotBackground = new Image(AssetHandler.INVENTORY_TEXTUREATLAS.createSprite("item-slot"));
		Image slotBackground = new Image(new NinePatch(AssetHandler.INVENTORY_TEXTUREATLAS.createSprite("cream-panel")));
		//slotBackground.setHeight(64);
		//slotBackground.setWidth(64);
		
		itemBackground.add(slotBackground);
		itemBackground.setName("item_slot");
		
		//numItemsLabel = new Label(String.valueOf(numItemsVal), AssetHandler.INVENTORY_SKIN, "inventory-item-count");
		numItemsLabel = new Label(String.valueOf(numItemsVal), AssetHandler.INVENTORY_SKIN, "item-count");
		numItemsLabel.setAlignment(Align.bottomRight);
		numItemsLabel.setName("numItems_label");
		numItemsLabel.setVisible(false);
		//numItemsLabel.setVisible(true);
		
		Table numItemsOverlay = new Table();
		numItemsOverlay.padLeft(48);
		numItemsOverlay.add(numItemsLabel).expand().fillX().bottom().left();

		this.add(itemBackground);
		this.add(numItemsOverlay);
		//this.add(numItemsLabel);
		
		this.addListener(new ClickListener(Input.Buttons.RIGHT)
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				//if()
				Gdx.app.debug(TAG, "InventorySlot " + slotName + " has been right clicked.");
            }
		});
	}

	private void incrementItemCount(boolean sendAddNotification)
	{
		numItemsVal++;
		numItemsLabel.setText(String.valueOf(numItemsVal));

		checkVisibilityOfItemCount();
		
		if(sendAddNotification)
		{
			//Gdx.app.debug(TAG, "In incrementItemCount() method, now sending ADDED_ITEM notify");
			notifyInventorySlotObservers(this, InventorySlotObserver.SlotEvent.ADDED_ITEM);
		}
	}

	private void decrementItemCount(boolean sendRemoveNotification)
	{
		numItemsVal--;
		numItemsLabel.setText(String.valueOf(numItemsVal));

		checkVisibilityOfItemCount();
		
		if(sendRemoveNotification)
		{
			//Gdx.app.debug(TAG, "In decrementItemCount() method, now sending REMOVED_ITEM notify");
			notifyInventorySlotObservers(this, InventorySlotObserver.SlotEvent.REMOVED_ITEM);
		}
	}
	
	public boolean addItem(InventoryItem item, int addCount)
	{
		boolean addSuccessful = false;
		
		if(!this.hasItem())
		{
			super.add(item);
			
			int index = 1;
			while(index <= addCount)
			{
				incrementItemCount(true);
				index++;
			}
			
			addSuccessful = true;
		}
		else if(getInventoryItem().getItemTypeID().equals(item.getItemTypeID()))
		{
			int index = 1;
			while(index <= addCount)
			{
				incrementItemCount(true);
				index++;
			}
			
			addSuccessful = true;
		}
		
		return addSuccessful;
	}
	
	public boolean removeItem(InventoryItem item, int removeCount)
	{
		boolean removeSuccessful = false;
		
		if(this.hasItem())
		{
			while(removeCount > 0)
			{
				decrementItemCount(true);
				removeCount--;
			}
		
			if(numItemsVal < 1)
			{
				super.removeActor(item);
			}
			
			removeSuccessful = true;
		}
		
		return removeSuccessful;
	}

	public void add(Array<Actor> array)
	{
		//Gdx.app.debug(TAG, "Now in method add(Array<Actor> array) for " + slotName);
		
		for (Actor actor : array)
		{
			if(!this.hasItem())
			{
				super.add(actor);
				incrementItemCount(true);
			}
			else
			{
				incrementItemCount(true);
			}
		}
	}

	public void updateAllInventoryItemNames(String name)
	{
		if (hasItem())
		{
			SnapshotArray<Actor> arrayChildren = this.getChildren();
			
			// Skip the first two elements
			for (int i = arrayChildren.size - 1; i > 1; i--)
			{
				arrayChildren.get(i).setName(name);
			}
		}
	}

	public void removeAllInventoryItemsWithName(String name)
	{
		if (hasItem())
		{
			SnapshotArray<Actor> arrayChildren = this.getChildren();
			// Skip the first two elements
			for (int i = arrayChildren.size - 1; i > 1; i--)
			{
				String itemName = arrayChildren.get(i).getName();
				if (itemName.equalsIgnoreCase(name))
				{
					decrementItemCount(true);
					arrayChildren.removeIndex(i);
				}
			}
		}
	}

	public void clearAllInventoryItems(boolean sendRemoveNotifications)
	{
		if(hasItem())
		{
			if(sendRemoveNotifications)
			{
				while(numItemsVal > 0)
				{
					decrementItemCount(true);
				}
			}
			
			this.resetSlot();
		}
	}

	private void checkVisibilityOfItemCount()
	{
		if (this.hasItem() && this.getNumItems() >= 1)
		{
			numItemsLabel.setVisible(true);
		}
		else
		{
			numItemsLabel.setVisible(false);
		}
	}

	public boolean hasItem()
	{
		//Gdx.app.debug(TAG, "Now in hasItem() method for " + this.toString());
		if (hasChildren())
		{
			SnapshotArray<Actor> items = this.getChildren();
			if (items.size > 2)
			{
				//Gdx.app.debug(TAG, "\t\tReturning true");
				return true;
			}
		}
		//Gdx.app.debug(TAG, "\t\tReturning false");
		return false;
	}

	public int getNumItems()
	{
		return numItemsVal;
	}
	
	public void setNumItems(int numItems)
	{
		this.numItemsVal = numItems;
		this.numItemsLabel.setText(numItems);
	}

	public int getNumItems(String name)
	{
		if (hasItem())
		{
			SnapshotArray<Actor> items = this.getChildren();
			int totalFilteredSize = 0;
			for (Actor actor : items)
			{
				if (actor.getName().equalsIgnoreCase(name))
				{
					totalFilteredSize += getNumItems();
				}
			}
			return totalFilteredSize;
		}
		return 0;
	}

	public boolean doesAcceptItemUseType(int itemUseType)
	{
		if (filterItemType == 0)
		{
			return true;
		}
		else
		{
			return ((filterItemType & itemUseType) == itemUseType);
		}
	}

	public InventoryItem getInventoryItem()
	{
		//Gdx.app.debug(TAG, "Now in method getTopInventoryItem() for " + slotName);
		
		InventoryItem item = null;
		if (hasItem())
		{
			SnapshotArray<Actor> items = this.getChildren();
			
			if (items.size > 2)
			{
				item = (InventoryItem) items.peek();
				//Gdx.app.debug(TAG, "\t\tTop inventory item is " + actor.getItemTypeID());
			}
		}
		return item;
	}

	public static void swapSlots(InventorySlot inventorySlotSource, InventorySlot inventorySlotTarget, InventoryItem sourceItem)
	{
		//Gdx.app.debug(TAG, "Now in method swapSlots, with source as " + inventorySlotSource.getName() + " and target as " + inventorySlotTarget.getName());
		
		int sourceCount = inventorySlotSource.getNumItems();
		int targetCount = inventorySlotTarget.getNumItems();
		
		inventorySlotSource.removeActor(sourceItem);
		inventorySlotSource.add(inventorySlotTarget.getInventoryItem());
		inventorySlotSource.setNumItems(targetCount);
		inventorySlotSource.checkVisibilityOfItemCount();
		
		inventorySlotTarget.removeActor(inventorySlotTarget.getInventoryItem());
		inventorySlotTarget.add(sourceItem);
		inventorySlotTarget.setNumItems(sourceCount);
		inventorySlotTarget.checkVisibilityOfItemCount();
		
	}

	@Override
	public void addInventorySlotObserver(InventorySlotObserver slotObserver)
	{
		observers.add(slotObserver);
	}

	@Override
	public void removeInventorySlotObserver(InventorySlotObserver slotObserver)
	{
		observers.removeValue(slotObserver, true);
	}

	@Override
	public void removeAllInventorySlotObservers()
	{
		for (InventorySlotObserver observer : observers)
		{
			observers.removeValue(observer, true);
		}
	}

	@Override
	public void notifyInventorySlotObservers(final InventorySlot slot, InventorySlotObserver.SlotEvent event)
	{
		for (InventorySlotObserver observer : observers)
		{
			observer.onInventorySlotNotify(slot, event);
		}
	}
	
	public void resetSlot()
	{
		if(this.hasItem())
		{
			super.removeActor(getInventoryItem());
		}
		this.numItemsVal = 0;
		this.numItemsLabel.setText(numItemsVal);
		checkVisibilityOfItemCount();
	}
	
	public int getSlotIndex()
	{
		return slotIndex;
	}
	
	public String toString()
	{
		return slotName;
	}
}
