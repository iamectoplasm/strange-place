package ui;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import configs.CharConfig;
import configs.CharConfig.InventoryConfig;
import configs.CropDeserializer;
import configs.CropConfig.Crop;
import ecs.components.BoundingBox;
import ecs.components.ConfigFile;
import ecs.components.MovementDirection;
import ecs.components.Position;
import ecs.components.Sprite;
import ecs.systems.InteractionSystem;
import game.EntityFactory;
import interaction.ConversationGraph;
import inventory.items.InventoryItem;
import inventory.items.InventoryItem.ItemTypeID;
import inventory.items.InventoryItemLocation;
import maps.MapManager;
import observers.InteractionObserver;
import observers.ConversationGraphObserver;
import observers.InventoryObserver;
import observers.StoreInventoryObserver;
import observers.ToolbarObserver;
import saves.ProfileManager;
import saves.ProfileObserver;
import utility.CropConfigSystem;

/*
 * The PlayerHUD class acts as a hub for ALL of the game UI.
 * Its purpose is to implement the various observer interfaces, so that it can relay
 * 		the appropriate notifications to the correct destinations
 */
/**
 * Acts as a hub for all of the game UI-- implements various observer interfaces
 * so that it can relay appropriate notifications to the correct destinations
 * 
 * @author Zoe
 *
 */
public class PlayerHUD implements Screen,
								  ProfileObserver,
								  ConversationGraphObserver,
								  InteractionObserver,
								  ToolbarObserver,
								  InventoryObserver,
								  StoreInventoryObserver
		{
	public static final String TAG = PlayerHUD.class.getSimpleName();

	/*
	 * = = = = = = = = = = = = = = = Class variables = = = = = = = = = = = = = = =
	 */
	private Stage stage;
	private Viewport viewport;
	private Camera camera;
	private Entity player;

	//private ToolbarUI optionsUI;
	private InventoryUI playerInventoryUI;
	private StoreInventoryUI storeInventoryUI;
	private ConversationUI conversationUI;

	private Json json;
	private MapManager mapManager;

	//private static final String INVENTORY_FULL = "Inventory is full!";

	// - - - - - - - - - - - - - - -
	// End of class variables
	// - - - - - - - - - - - - - - -

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Constructor
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public PlayerHUD(Camera camera, Entity player, MapManager mapMgr)
	{
		this.camera = camera;
		this.player = player;
		this.mapManager = mapMgr;
		this.viewport = new ScreenViewport(this.camera);
		this.stage = new Stage(viewport);

		this.json = new Json();

		this.playerInventoryUI = new InventoryUI();
		playerInventoryUI.setVisible(false);
		playerInventoryUI.setPosition((viewport.getWorldWidth() / 2) - (playerInventoryUI.getWidth() / 2f), viewport.getWorldHeight() - (playerInventoryUI.getHeight() * 2f));
		playerInventoryUI.setKeepWithinStage(false);
		playerInventoryUI.setMovable(true);

		this.storeInventoryUI = new StoreInventoryUI();
		storeInventoryUI.setVisible(false);
		storeInventoryUI.setPosition(0, 0);
		storeInventoryUI.setKeepWithinStage(false);
		storeInventoryUI.setWidth(400f);
		storeInventoryUI.setPlayerMoney(100);

		this.conversationUI = new ConversationUI();
		conversationUI.setVisible(false);
		conversationUI.setMovable(true);
		conversationUI.setPosition(0, 0);
		conversationUI.setWidth(400f);
		conversationUI.setHeight(conversationUI.getHeight());

		//stage.addActor(optionsUI);
		stage.addActor(playerInventoryUI);
		stage.addActor(storeInventoryUI);
		stage.addActor(conversationUI);

		//optionsUI.validate();
		playerInventoryUI.validate();
		storeInventoryUI.validate();
		conversationUI.validate();

		// Add tooltips
		Array<Actor> actors = playerInventoryUI.getInventoryActors();
		for (Actor actor : actors)
		{
			stage.addActor(actor);
		}
		
		Array<Actor> storeActors = storeInventoryUI.getInventoryActors();
		for(Actor actor: storeActors)
		{
			stage.addActor(actor);
		}

		// Observers
		this.playerInventoryUI.addInventoryObserver(this);
		this.storeInventoryUI.addObserver(this);
		this.playerInventoryUI.getItemsActionWindow().addInventoryObserver(this);

		// Listeners
		stage.addListener(new InputListener()
		{
			@Override
			public boolean keyDown (InputEvent event, int keycode)
			{
				if(keycode == Input.Keys.E)
				{
					playerInventoryUI.setVisible(playerInventoryUI.isVisible() ? false : true);
					playerInventoryUI.getItemsActionWindow().setVisible(playerInventoryUI.getItemsActionWindow().isVisible() ? false : true);
				}
				return false;
			}
		});

		conversationUI.getCloseButton().addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				conversationUI.setVisible(false);
			}
		});
		
		storeInventoryUI.getCloseButton().addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				storeInventoryUI.savePlayerInventory();
				storeInventoryUI.cleanupStoreInventory();
				storeInventoryUI.setVisible(false);
            }
        });
	}
	
	private Array<InventoryItemLocation> generateDefaultInventory()
	{
		Array<InventoryConfig> inventoryConfigs = player.getComponent(ConfigFile.class).charConfig.getInventoryConfigs();
		
		Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
		//for(int i = 0; i < items.size; i++)
		for(int i = 0; i < inventoryConfigs.size; i++)
		{
			//itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
			ItemTypeID item = inventoryConfigs.get(i).getItemID();
			int count = inventoryConfigs.get(i).getItemCount();
			
			Gdx.app.debug(TAG, "Creating new InventoryItemLocation with item " + item.name() + " and count " + count);
			
			itemLocations.add(new InventoryItemLocation(i, item.name(), count, InventoryUI.PLAYER_INVENTORY));
		}
		
		return itemLocations;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Stage getStage()
	{
		return stage;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Stage Rendering overrides
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public void render(float delta)
	{
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}

	@Override
	public void show()
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}

	@Override
	public void hide()
	{
	}
	// - - - - - - - - - - - - - - -
	// End of rendering overrides
	// - - - - - - - - - - - - - - -

	public void updateEntityObservers()
	{
		mapManager.unregisterCurrentMapEntityObservers();
		mapManager.registerCurrentMapEntityObservers(this);
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Notifications
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	/*
	 * = = = = = = = = = = = = = = = ProfileManager = = = = = = = = = = = = = = =
	 */
	@Override
	public void onProfileNotify(ProfileManager profileManager, ProfileEvent event)
	{
		switch (event)
		{
		case PROFILE_LOADED:
			boolean firstTime = profileManager.getIsNewProfile();

			if (firstTime)
			{
				InventoryUI.clearInventoryItems(playerInventoryUI.getInventorySlotTable());
				
				// Add default items if first time
				/*
				Array<ItemTypeID> items = player.getComponent(ConfigFile.class).charConfig.getInventoryItems();
				
				Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
				for(int i = 0; i < items.size; i++)
				{
					itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.PLAYER_INVENTORY));
				}
				*/
				Array<InventoryItemLocation> itemLocations = generateDefaultInventory();
				
				InventoryUI.populateInventory(playerInventoryUI.getInventorySlotTable(),
						itemLocations,
						playerInventoryUI.getDragAndDrop(),
						InventoryUI.PLAYER_INVENTORY,
						false);
				
				
				profileManager.setProperty("playerInventory", InventoryUI.getInventory(playerInventoryUI.getInventorySlotTable()));
				
				// Start player with some money
				//optionsUI.setPlayerMoney(100);
				
			}
			else
			{
				//int playerMoney = profileManager.getProperty("currentPlayerMoney", Integer.class);
				
				@SuppressWarnings("unchecked")
				Array<InventoryItemLocation> inventory = profileManager.getProperty("playerInventory", Array.class);
				InventoryUI.populateInventory(playerInventoryUI.getInventorySlotTable(),
											  inventory,
											  playerInventoryUI.getDragAndDrop(),
											  InventoryUI.PLAYER_INVENTORY,
											  false);
			}
			break;
			
		case SAVING_PROFILE:
			profileManager.setProperty("playerInventory", InventoryUI.getInventory(playerInventoryUI.getInventorySlotTable()));
			break;
			
		case CLEAR_CURRENT_PROFILE:
			break;
			
		default:
			break;
		}
	}

	/*
	 * = = = = = = = = = = = = = = = ConversationGraph = = = = = = = = = = = = = = =
	 */
	@Override
	public void onConversationGraphNotify(ConversationGraph graph, ConversationCommandEvent event)
	{
		switch (event)
		{
		case LOAD_STORE_INVENTORY:
			Gdx.app.debug(TAG, "LOAD_STORE_INVENTORY event sent to ConversationGraph " + graph.toString());
			
			Entity selectedEntity = player.getWorld().getSystem(InteractionSystem.class).getCurrentSelectedEntity();
			if(selectedEntity == null)
			{
				break;
			}
			
			Array<InventoryItemLocation> inventory = InventoryUI.getInventory(playerInventoryUI.getInventorySlotTable());
			storeInventoryUI.loadPlayerInventory(inventory);
			
			//Array<InventoryItem.ItemTypeID> items = selectedEntity.getComponent(ConfigFile.class).charConfig.getInventoryConfigs();
			Array<InventoryItem.ItemTypeID> items = selectedEntity.getComponent(ConfigFile.class).charConfig.getInventoryItems();
			Array<InventoryItemLocation> itemLocations = new Array<InventoryItemLocation>();
			for(int i = 0; i < items.size; i++)
			{
				itemLocations.add(new InventoryItemLocation(i, items.get(i).toString(), 1, InventoryUI.STORE_INVENTORY));
			}
			
			storeInventoryUI.loadStoreInventory(itemLocations);
			
			conversationUI.setVisible(false);
			storeInventoryUI.toFront();
			//playerInventoryUI.setVisible(true);
			storeInventoryUI.setVisible(true);
			break;
		case EXIT_CONVERSATION:
			conversationUI.setVisible(false);
			//mapManager.clearCurrentSelectedMapEntity();
			//mapManager.getPlayer().getComponent(SelectionBox.class).currentSelectedEntity = null;
			break;
		case NONE:
			break;
		default:
			break;
		}
	}

	/*
	 * = = = = = = = = = = = = = = = Component notifications = = = = = = = = = = = =
	 * = = =
	 */
	/*
	@Override
	public void onInteractionObserverNotify(String value, InteractionEvent event)
	{
		switch (event)
		{
		case LOAD_CONVERSATION:
			Gdx.app.debug(TAG, "\tNow in LOAD_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			
			CharConfig config = json.fromJson(CharConfig.class, value);

			//conversationUI.loadConversation(config);
			conversationUI.loadConversation(config.getEntityName(), config.getConversationConfigPath());
			conversationUI.getCurrentConversationGraph().addConversationGraphObserver(this);
			
			Gdx.app.debug(TAG, "\tNow exiting LOAD_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			break;
			
		case SHOW_CONVERSATION:
			Gdx.app.debug(TAG, "\tNow in SHOW_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			
			CharConfig configShow = json.fromJson(CharConfig.class, value);

			if (configShow.getEntityName().equalsIgnoreCase(conversationUI.getCurrentEntityName()))
			{
				conversationUI.setVisible(true);
			}
			
			Gdx.app.debug(TAG, "\tNow exiting SHOW_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			break;
			
		case HIDE_CONVERSATION:
			Gdx.app.debug(TAG, "\tNow in HIDE_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			
			CharConfig configHide = json.fromJson(CharConfig.class, value);
			if (configHide.getEntityName().equalsIgnoreCase(conversationUI.getCurrentEntityName()))
			{
				conversationUI.setVisible(false);
			}
			
			Gdx.app.debug(TAG, "\tNow in HIDE_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			break;

		default:
			break;
		}
	}
	*/
	@Override
	public void onInteractionObserverNotify(InteractionEvent event, String name, String conversationPath)
	{
		switch (event)
		{
		case LOAD_CONVERSATION:
			Gdx.app.debug(TAG, "\tNow in LOAD_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			
			/*
			CharConfig config = json.fromJson(CharConfig.class, value);

			//conversationUI.loadConversation(config);
			conversationUI.loadConversation(config.getEntityName(), config.getConversationConfigPath());
			*/
			conversationUI.loadConversation(name, conversationPath);
			
			conversationUI.getCurrentConversationGraph().addConversationGraphObserver(this);
			
			Gdx.app.debug(TAG, "\tNow exiting LOAD_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			break;
			
		case SHOW_CONVERSATION:
			Gdx.app.debug(TAG, "\tNow in SHOW_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			
			//CharConfig configShow = json.fromJson(CharConfig.class, value);

			//if (configShow.getEntityName().equalsIgnoreCase(conversationUI.getCurrentEntityName()))
			if (name.equalsIgnoreCase(conversationUI.getCurrentEntityName()))
			{
				conversationUI.setVisible(true);
			}
			
			Gdx.app.debug(TAG, "\tNow exiting SHOW_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			break;
			
		case HIDE_CONVERSATION:
			Gdx.app.debug(TAG, "\tNow in HIDE_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			
			//CharConfig configHide = json.fromJson(CharConfig.class, value);
			//if (configHide.getEntityName().equalsIgnoreCase(conversationUI.getCurrentEntityName()))
			if (name.equalsIgnoreCase(conversationUI.getCurrentEntityName()))
			{
				conversationUI.setVisible(false);
			}
			
			Gdx.app.debug(TAG, "\tNow in HIDE_CONVERSATION in PlayerHUD's onInteractionObserverNotify");
			break;

		default:
			break;
		}
	}

	/*
	 * = = = = = = = = = = = = = = = Store inventory event = = = = = = = = = = = = =
	 * = =
	 */
	@Override
	public void onStoreInventoryNotify(String value, StoreInventoryEvent event)
	{
		switch(event)
		{
		case PLAYER_MONEY_UPDATED:
			Gdx.app.debug(TAG, "In onStoreInventoryNotify method, PLAYER_MONEY_UPDATED");
			
			//int val = Integer.valueOf(value);
			
			break;
		case PLAYER_INVENTORY_UPDATED:
			@SuppressWarnings("unchecked") Array<InventoryItemLocation> items = json.fromJson(Array.class, value);
			InventoryUI.populateInventory(playerInventoryUI.getInventorySlotTable(), items, playerInventoryUI.getDragAndDrop(), InventoryUI.PLAYER_INVENTORY, false);
			break;
		default:
			break;
		}
	}

	/*
	 * = = = = = = = = = = = = = = = Player inventory event = = = = = = = = = = = =
	 * = = =
	 */
	@Override
	public void onInventoryNotify(InventoryItem.ItemTypeID item, InventoryEvent event)
	{
		switch (event)
		{
		case CONSUME_ITEM:
			Gdx.app.debug(TAG, "Thing consumed, which was " + item.toString());
			break;
			
		case PLANT_ITEM:
			Crop newCrop = CropDeserializer.getInstance().getCropByProduce(item);
			Entity cropEntity = EntityFactory.getInstance().createPlantEntity(newCrop.getSpecies());
			
			EntityFactory.world.getSystem(CropConfigSystem.class).initialize(cropEntity.getId(), newCrop.getSpecies());
			
			float plantSpawnX = player.getComponent(BoundingBox.class).boundingBox.x + player.getComponent(MovementDirection.class).currentDirection.getDX();
			float plantSpawnY = player.getComponent(BoundingBox.class).boundingBox.y + player.getComponent(MovementDirection.class).currentDirection.getDY();
			
			Vector2 plantSpawnPoint = new Vector2(plantSpawnX, plantSpawnY);
			cropEntity.getComponent(Position.class).setCurrentPosition(plantSpawnPoint);
			
			Gdx.app.debug(TAG, "New crop entity spawned at point: (" + plantSpawnPoint.x + ", " + plantSpawnPoint.y + ")");
			
			mapManager.addNewMapEntity(mapManager, player.getComponent(Sprite.class).batch, cropEntity);
			
			break;
			
		case RECEIVE_ITEM:
			Gdx.app.debug(TAG, "RECEIVE_ITEM message received by PlayerHUD " + item.toString());
			
			playerInventoryUI.addItemToInventory(item, InventoryUI.PLAYER_INVENTORY);
			break;

		default:
			break;
		}
	}

	/*
	 * = = = = = = = = = = = = = = = Status event = = = = = = = = = = = = = = =
	 */
	@Override
	public void onToolbarBarNotify(int value, StatusEvent event)
	{
		// TODO Auto-generated method stub
	}
	// - - - - - - - - - - - - - - -
	// End of notifications
	// - - - - - - - - - - - - - - -
}
