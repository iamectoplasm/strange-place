package ui;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import inventory.InventorySlot;
import inventory.InventorySlotTarget;
import inventory.InventorySlotTooltip;
import inventory.InventorySlotTooltipListener;
import inventory.items.InventoryItem;
import inventory.items.InventoryItemLocation;
import observers.InventorySlotObserver;
import observers.StoreInventoryObserver;
import observers.StoreInventoryObserver.StoreInventoryEvent;
import observers.subjects.StoreInventorySubject;
import utility.AssetHandler;

public class StoreInventoryUI extends Window implements InventorySlotObserver, StoreInventorySubject
{
	public static final String TAG = StoreInventoryUI.class.getSimpleName();
	
	private int numStoreInventorySlots = 20;
	private int lengthSlotRow = 10;
	
	private NinePatch tableBackground;

	private Table storeInventorySlotTable;
	private Table playerInventorySlotTable;
	private static DragAndDrop dragAndDrop;
	private Array<Actor> inventoryActors;

	//private final int slotWidth = 32;
	//private final int slotHeight = 32;
	private final int SLOT_WIDTH = 64;
	private final int SLOT_HEIGHT = 64;

	private InventorySlotTooltip inventorySlotTooltip;

	private Label sellTotalLabel;
	private Label buyTotalLabel;
	private Label playerTotalLabel;

	private int sellPrice = 0;
	private int buyPrice = 0;
	private int playerTotal = 0;

	private Button sellButton;
	private Button buyButton;
	public TextButton closeButton;

	private Table buttonsTable;
	private Table totalLabelsTable;

	private Array<StoreInventoryObserver> observers;

	private Json json;

	private static String SELL = "Sell";
	private static String BUY = "Buy";
	private static String MONEY = "Money";
	private static String PLAYER_TOTAL = "Player Total";

	public StoreInventoryUI()
	{
		super("Store Inventory", AssetHandler.INVENTORY_SKIN);

		observers = new Array<StoreInventoryObserver>();
		json = new Json();

		//this.setFillParent(true);

		// Create
		StoreInventoryUI.dragAndDrop = new DragAndDrop();
		inventoryActors = new Array<Actor>();
		
		//tableBackground = AssetHandler.INVENTORY_SKIN.getPatch("inv-slot-background");
		tableBackground = AssetHandler.INVENTORY_SKIN.getPatch("bordered-slate-panel");
		tableBackground.setMiddleWidth(this.getWidth());
		tableBackground.setMiddleHeight(32);
		
		storeInventorySlotTable = new Table();
		storeInventorySlotTable.setBackground(new NinePatchDrawable(tableBackground));
		storeInventorySlotTable.setName(InventoryUI.STORE_INVENTORY);
		storeInventorySlotTable.setTouchable(Touchable.enabled);

		playerInventorySlotTable = new Table();
		playerInventorySlotTable.setBackground(new NinePatchDrawable(tableBackground));
		playerInventorySlotTable.setName(InventoryUI.PLAYER_INVENTORY);
		playerInventorySlotTable.setTouchable(Touchable.enabled);
		
		inventorySlotTooltip = new InventorySlotTooltip(AssetHandler.INVENTORY_SKIN);

		//sellButton = new TextButton(SELL, AssetHandler.INVENTORY_SKIN, "item-action-button");
		sellButton = new TextButton(SELL, AssetHandler.INVENTORY_SKIN, "item-action");
		sellButton.pad(8, 10, 10, 10);
		//sellButton.setScale(.5f);
		//disableButton(sellButton, true);
		
		//buyButton = new TextButton(BUY, AssetHandler.INVENTORY_SKIN, "item-action-button");
		buyButton = new TextButton(BUY, AssetHandler.INVENTORY_SKIN, "item-action");
		buyButton.pad(8, 10, 10, 10);
		//buyButton.setScale(.5f);
		//disableButton(buyButton, true);

		sellTotalLabel = new Label(SELL + " : " + sellPrice + " " + MONEY, AssetHandler.INVENTORY_SKIN);
		sellTotalLabel.setAlignment(Align.left);
		//sellTotalLabel.setScale(1f);

		buyTotalLabel = new Label(BUY + " : " + buyPrice + " " + MONEY, AssetHandler.INVENTORY_SKIN);
		buyTotalLabel.setAlignment(Align.right);
		//buyTotalLabel.setScale(1f);

		playerTotalLabel = new Label(PLAYER_TOTAL + " : " + playerTotal + " " + MONEY, AssetHandler.INVENTORY_SKIN);

		closeButton = new TextButton("X", AssetHandler.CONVERSATION_SKIN, "conversation-exit");

		buttonsTable = new Table();
		buttonsTable.defaults().expand().fill();
		buttonsTable.add(sellButton).padLeft(10).padRight(10);
		buttonsTable.add(buyButton).padLeft(10).padRight(10);

		totalLabelsTable = new Table();
		//totalLabels.defaults().expand().fill();
		//totalLabels.add(sellTotalLabel).padLeft(40);
		totalLabelsTable.add(sellTotalLabel).padRight(40);
		//totalLabels.add();
		//totalLabels.add(buyTotalLabel).padRight(40);
		totalLabelsTable.add(buyTotalLabel).padLeft(40);

		// Layout
		for (int i = 1; i <= numStoreInventorySlots; i++)
		{
			InventorySlot storeInventorySlot = new InventorySlot(i);
			storeInventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
			
			storeInventorySlot.setName(InventoryUI.STORE_INVENTORY);

			dragAndDrop.addTarget(new InventorySlotTarget(storeInventorySlot));
			storeInventorySlot.addInventorySlotObserver(this);

			storeInventorySlotTable.add(storeInventorySlot).size(SLOT_WIDTH, SLOT_HEIGHT);

			if (i % lengthSlotRow == 0)
			{
				storeInventorySlotTable.row();
			}
		}

		for (int i = 1; i <= InventoryUI.NUM_SLOTS; i++)
		{
			InventorySlot playerInventorySlot = new InventorySlot(i);
			playerInventorySlot.addListener(new InventorySlotTooltipListener(inventorySlotTooltip));
			
			playerInventorySlot.setName(InventoryUI.PLAYER_INVENTORY);

			dragAndDrop.addTarget(new InventorySlotTarget(playerInventorySlot));
			playerInventorySlot.addInventorySlotObserver(this);

			playerInventorySlotTable.add(playerInventorySlot).size(SLOT_WIDTH, SLOT_HEIGHT);

			if (i % lengthSlotRow == 0)
			{
				playerInventorySlotTable.row();
			}
		}

		inventoryActors.add(inventorySlotTooltip);

		//this.add();
		this.add(closeButton);
		this.row();

		//this.defaults().expand().fill();
		//this.add(storeInventorySlotTable).pad(10, 10, 10, 10).row();
		//this.add(storeInventorySlotTable).pad(5, 5, 5, 5).row();
		this.add(storeInventorySlotTable).row();
		this.add(buttonsTable).row();
		this.add(totalLabelsTable).row();
		//this.add(playerInventorySlotTable).pad(10, 10, 10, 10).row();
		//this.add(playerInventorySlotTable).pad(5, 5, 5, 5).row();
		this.add(playerInventorySlotTable).row();
		this.add(playerTotalLabel);
		
		//this.setScale(1.5f);
		//this.setScale(2f);
		
		//storeInventorySlotTable.setTouchable(Touchable.enabled);
		//playerInventorySlotTable.setTouchable(Touchable.enabled);
		//this.setTouchable(Touchable.enabled);
		
		this.pack();

		// Listeners
		buyButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				if (buyPrice > 0 && playerTotal >= buyPrice)
				{
					playerTotal -= buyPrice;
					StoreInventoryUI.this.notify(Integer.toString(playerTotal),
							StoreInventoryEvent.PLAYER_MONEY_UPDATED);

					buyPrice = 0;
					buyTotalLabel.setText(BUY + " : " + buyPrice + MONEY);
					checkButtonStates();

					InventoryUI.setInventoryItemNames(playerInventorySlotTable, InventoryUI.PLAYER_INVENTORY);

					savePlayerInventory();
				}
			}
		});

		sellButton.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				if (sellPrice > 0)
				{
					playerTotal += sellPrice;
					StoreInventoryUI.this.notify(Integer.toString(playerTotal),
							StoreInventoryEvent.PLAYER_MONEY_UPDATED);

					sellPrice = 0;

					sellTotalLabel.setText(SELL + " : " + sellPrice + MONEY);

					checkButtonStates();

					@SuppressWarnings("rawtypes")
					Array<Cell> cells = storeInventorySlotTable.getCells();

					for (int i = 0; i < cells.size; i++)
					{
						InventorySlot inventorySlot = (InventorySlot) cells.get(i).getActor();
						if (inventorySlot == null)
							continue;
						if (inventorySlot.hasItem() && inventorySlot.getInventoryItem().getName()
								.equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY))
						{
							inventorySlot.clearAllInventoryItems(false);
						}
					}
					savePlayerInventory();
				}
			}
		});
	}

	public TextButton getCloseButton()
	{
		return closeButton;
	}

	public Table getInventorySlotTable()
	{
		return storeInventorySlotTable;
	}

	public Array<Actor> getInventoryActors()
	{
		return inventoryActors;
	}

	public void loadPlayerInventory(Array<InventoryItemLocation> playerInventoryItems)
	{
		//InventoryUI.populateInventory(playerInventorySlotTable, playerInventoryItems,
		//		InventoryUI.PLAYER_INVENTORY, true);
		InventoryUI.populateInventory(playerInventorySlotTable, playerInventoryItems, dragAndDrop, InventoryUI.PLAYER_INVENTORY, true);
	}

	public void loadStoreInventory(Array<InventoryItemLocation> storeInventoryItems)
	{
		//InventoryUI.populateInventory(playerInventorySlotTable, storeInventoryItems,
		//		InventoryUI.STORE_INVENTORY, false);
		InventoryUI.populateInventory(storeInventorySlotTable, storeInventoryItems, dragAndDrop, InventoryUI.STORE_INVENTORY, false);
	}

	public void savePlayerInventory()
	{
		Array<InventoryItemLocation> playerItemsInPlayerInventory = InventoryUI
				.getInventoryFiltered(playerInventorySlotTable, InventoryUI.STORE_INVENTORY);
		Array<InventoryItemLocation> playerItemsInStoreInventory = InventoryUI
				.getInventoryFiltered(storeInventorySlotTable, storeInventorySlotTable, InventoryUI.STORE_INVENTORY);

		playerItemsInPlayerInventory.addAll(playerItemsInStoreInventory);

		StoreInventoryUI.this.notify(json.toJson(playerItemsInPlayerInventory),
				StoreInventoryEvent.PLAYER_INVENTORY_UPDATED);
	}

	public void cleanupStoreInventory()
	{
		InventoryUI.removeInventoryItems(InventoryUI.STORE_INVENTORY, playerInventorySlotTable);
		InventoryUI.removeInventoryItems(InventoryUI.PLAYER_INVENTORY, storeInventorySlotTable);
	}

	@Override
	public void onInventorySlotNotify(InventorySlot slot, SlotEvent event)
	{
		InventoryItem itemInSlot = slot.getInventoryItem();
		String slotName = slot.getName();
		
		//Gdx.app.debug(TAG, "In onInventorySlotNotify() method, " + event.name() + " for slot " + slotName);
		//Gdx.app.debug(TAG, "\titem in slot: " + itemInSlot.getItemTypeID());
		//Gdx.app.debug(TAG, "\tslot name: " + slotName);
		
		switch (event)
		{
		case ADDED_ITEM:
			//if (slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)
			//		&& slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY))
			if (itemInSlot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)
					&& slotName.equalsIgnoreCase(InventoryUI.STORE_INVENTORY))
			{
				//Gdx.app.debug(TAG, "\tIn sell item if condition");
				
				sellPrice += slot.getInventoryItem().getItemValue();
				sellTotalLabel.setText(SELL + " : " + sellPrice + MONEY);
			}
			//if (slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY)
			//		&& slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY))
			if (itemInSlot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY)
					&& slotName.equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY))
			{
				//Gdx.app.debug(TAG, "\tIn buy item if condition");
				buyPrice += slot.getInventoryItem().getItemValue();
				buyTotalLabel.setText(BUY + " : " + buyPrice + " " + MONEY);
			}
			break;

		case REMOVED_ITEM:
			//if (slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)
			//		&& slot.getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY))
			if (itemInSlot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)
					&& slotName.equalsIgnoreCase(InventoryUI.STORE_INVENTORY))
			{
				//Gdx.app.debug(TAG, "\tIn sell item if condition");
				sellPrice -= slot.getInventoryItem().getItemValue();
				sellTotalLabel.setText(SELL + " : " + sellPrice + MONEY);
			}
			//if (slot.getTopInventoryItem().getName().equalsIgnoreCase(InventoryUI.STORE_INVENTORY)
			//		&& slot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY))
			if (itemInSlot.getName().equalsIgnoreCase(InventoryUI.PLAYER_INVENTORY)
					&& slotName.equalsIgnoreCase(InventoryUI.STORE_INVENTORY))
			{
				//Gdx.app.debug(TAG, "\tIn buy item if condition");
				buyPrice -= slot.getInventoryItem().getItemValue();
				buyTotalLabel.setText(BUY + " : " + buyPrice + " " + MONEY);
			}
			break;
		}
		
		checkButtonStates();
	}

	public void checkButtonStates()
	{
		if (sellPrice <= 0)
		{
			disableButton(sellButton, true);
		}
		else
		{
			disableButton(sellButton, false);
		}

		if (buyPrice <= 0 || playerTotal < buyPrice)
		{
			disableButton(buyButton, true);
		}
		else
		{
			disableButton(buyButton, false);
		}
	}

	private void disableButton(Button button, boolean disable)
	{
		if (disable)
		{
			button.setDisabled(true);
			button.setTouchable(Touchable.disabled);
		}
		else
		{
			button.setDisabled(false);
			button.setTouchable(Touchable.enabled);
		}
	}

	public void setPlayerMoney(int value)
	{
		playerTotal = value;
		playerTotalLabel.setText(PLAYER_TOTAL + " : " + playerTotal + " " + MONEY);
	}

	@Override
	public void addObserver(StoreInventoryObserver storeObserver)
	{
		observers.add(storeObserver);
	}

	@Override
	public void removeObserver(StoreInventoryObserver storeObserver)
	{
		observers.removeValue(storeObserver, true);
	}

	@Override
	public void removeAllObservers()
	{
		for(StoreInventoryObserver observer: observers)
		{
			observers.removeValue(observer, true);
		}
	}

	@Override
	public void notify(String value, StoreInventoryObserver.StoreInventoryEvent event)
	{
		for(StoreInventoryObserver observer: observers)
		{
			observer.onStoreInventoryNotify(value, event);
		}
	}
}
