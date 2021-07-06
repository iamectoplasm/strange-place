package inventory.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import inventory.InventorySlot;
import observers.InventoryObserver;
import observers.InventoryObserver.InventoryEvent;
import observers.subjects.InventorySubject;

public class ItemActionsWindow extends Window implements InventorySubject
{
	public static final String TAG = ItemActionsWindow.class.getSimpleName();
	
	private Skin skin;
	
	private Array<TextButton> actionButtons;
	
	private float maxWidth;
	
	private Array<InventoryObserver> observers;
	
	public ItemActionsWindow(final Skin skin)
	{
		super("", skin);
		
		this.observers = new Array<InventoryObserver>();
		
		this.actionButtons = new Array<TextButton>();
		
		this.skin = skin;
		//this.pad(5f, 5f, 5f, 5f);
		this.pack();
		
		this.setVisible(false);
		this.setModal(false);
	}
	
	public void populateActionList(final InventorySlot inventorySlot)
	{
		this.depopulateActionList();
		
		if(inventorySlot.hasItem())
		{
			InventoryItem item = inventorySlot.getInventoryItem();
			
			//if(item.getItemAttributes() == InventoryItem.ItemAttribute.CONSUMABLE.getValue())
			if(item.isConsumable())
			{
				//TextButton consumeButton = new TextButton("Consume", skin, "item-action-button");
				TextButton consumeButton = new TextButton("Consume", skin, "item-action");
				consumeButton.pad(8, 10, 10, 10);
				this.maxWidth = consumeButton.getWidth();
				
				consumeButton.addListener(new ClickListener()
				{
					@Override
					public void touchUp(InputEvent event, float x, float y, int pointer, int button)
					{
						InventorySlot slot = inventorySlot;
						if (slot.hasItem())
						{
							InventoryItem item = slot.getInventoryItem();
							ItemActionsWindow.this.notifyInventoryObservers(item.getItemTypeID(), InventoryObserver.InventoryEvent.CONSUME_ITEM);
							slot.removeItem(item, 1);
						}
					}
				});
				actionButtons.add(consumeButton);
			}
			
			if(item.isPlantable())
			{
				//TextButton plantButton = new TextButton("Plant", skin, "item-action-button");
				TextButton plantButton = new TextButton("Plant", skin, "item-action");
				plantButton.pad(8, 10, 10, 10);
				plantButton.setWidth(maxWidth);
				
				plantButton.addListener(new ClickListener()
				{
					@Override
					public void touchUp(InputEvent event, float x, float y, int pointer, int button)
					{
						InventorySlot slot = inventorySlot;
						if (slot.hasItem())
						{
							InventoryItem item = slot.getInventoryItem();
							ItemActionsWindow.this.notifyInventoryObservers(item.getItemTypeID(), InventoryObserver.InventoryEvent.PLANT_ITEM);
							slot.removeItem(item, 1);
						}
					}
				});
				actionButtons.add(plantButton);
			}
		}
		else
		{
			TextButton spawnItemButton = new TextButton("Spawn item", skin, "item-action");
			spawnItemButton.pad(8, 10, 10, 10);
			
			spawnItemButton.addListener(new ClickListener()
			{
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button)
				{
					//InventorySlot slot = inventorySlot;
					
					Gdx.app.debug(TAG, "Clicked on spawnItemButton");
				}
			});
			actionButtons.add(spawnItemButton);
		}
		
		if(!actionButtons.isEmpty())
		{
			for(int i = 0; i < actionButtons.size; i++)
			{
				this.add(actionButtons.get(i));
				this.row();
			}
			this.pack();
		}
	}
	
	public void depopulateActionList()
	{
		if(actionButtons.isEmpty())
		{
			return;
		}
		
		for(int i = 0; i < actionButtons.size; i++)
		{
			this.removeActor(actionButtons.get(i));
		}
		
		actionButtons.clear();
		this.setVisible(false);
		
		//this.pack();
	}
	
	public void setVisible(InventorySlot inventorySlot, boolean visible)
	{
		if(!visible)
		{
			//this.addAction(Actions.removeActor());
			this.reset();
			this.pack();
		}
		else
		{
			super.setVisible(visible);
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
