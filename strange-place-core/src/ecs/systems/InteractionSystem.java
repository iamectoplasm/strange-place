package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import configs.CharConfig;
import ecs.components.*;
import ecs.components.GrowthState.GrowthStage;
import ecs.components.player.*;
import inventory.items.InventoryItem.ItemTypeID;
import observers.InteractionObserver;
import observers.InteractionObserver.InteractionEvent;
import observers.InventoryObserver;
import observers.InventoryObserver.InventoryEvent;
import observers.subjects.InteractionSubject;
import observers.subjects.InventorySubject;
import ui.PlayerHUD;

public class InteractionSystem extends IntervalIteratingSystem implements InteractionSubject, InventorySubject
{
	private static final String TAG = InteractionSystem.class.getSimpleName();
	
	protected ComponentMapper<ConfigFile> mConfig;
	protected ComponentMapper<MovementDirection> mDirection;
	//protected ComponentMapper<HomeMap> mLocation;
	protected ComponentMapper<KeyboardInput> mInput;
	protected ComponentMapper<Position> mPosition;
	protected ComponentMapper<BoundingBox> mBoundingBox;
	
	protected ComponentMapper<NPCTag> npcTag;
	protected ComponentMapper<PlantTag> cropTag;
	protected ComponentMapper<GrowthState> growthState;
	protected ComponentMapper<Species> species;
	
	private boolean conversationLoaded = false;
	private boolean sentShowConversationMessage = false;
	
	private boolean wateredCrop = false;
	//private boolean sentCropHarvestedMessage = false;
	
	private boolean entitySelected;
	private Entity currentSelectedEntity;
	
	private Json json = new Json();
	
	private Array<InteractionObserver> interactionObservers = new Array<InteractionObserver>();
	private Array<InventoryObserver> inventoryObservers = new Array<InventoryObserver>();
	
	public InteractionSystem()
	{
		super(Aspect.all(PlayerTag.class,
						 MovementState.class,
						 //HomeMap.class,
						 KeyboardInput.class,
						 Position.class,
						 BoundingBox.class),
				(1/30f));
		
		entitySelected = false;
		currentSelectedEntity = null;
	}
	
	public void addPlayerHUDObserver(PlayerHUD playerHUD)
	{
		this.addInteractionObserver(playerHUD);
		this.addInventoryObserver(playerHUD);
	}
	
	@Override
	protected void process(int entityId)
	{
		BoundingBox box = mBoundingBox.get(entityId);
		
		//if(detectedEntityId != -1)
		if(!entitySelected)
		{
			if(detectEntity(entityId))
			{
				if(npcTag.has(currentSelectedEntity))
				{
					currentSelectedEntity.getComponent(MovementState.class).pauseMovement = true;
				}
			}
		}
		else
		{
			boolean spacePressed = mInput.get(entityId).spacePressed;
			boolean entityIsNpc = npcTag.has(currentSelectedEntity);
			
			if(spacePressed && currentSelectedEntity != null)
			{
				if(entityIsNpc)
				{
					handleNPCInteraction(entityId);
				}
				else
				{
					if(growthState.get(currentSelectedEntity).currentGrowthStage == GrowthStage.HARVESTABLE)
					{
						//sentCropHarvestedMessage = true;
						ItemTypeID item = species.get(currentSelectedEntity).produce;
						notifyInventoryObservers(item, InventoryEvent.RECEIVE_ITEM);
						
						currentSelectedEntity.deleteFromWorld();
						entitySelected = false;
						//sentCropHarvestedMessage = false;
					}
					
					else if(!wateredCrop)
					{
						wateredCrop = true;
						growthState.get(currentSelectedEntity).hasBeenWatered = true;
					}
				}
			}
			
			if(!Intersector.intersectSegmentRectangle(box.selectorPoint, box.selectorPoint, currentSelectedEntity.getComponent(BoundingBox.class).boundingBox))
			{
				if(entityIsNpc)
				{
					currentSelectedEntity.getComponent(MovementState.class).pauseMovement = false;
				}
				
				currentSelectedEntity = null;
				entitySelected = false;
				conversationLoaded = false;
				sentShowConversationMessage = false;
				wateredCrop = false;
				//sentCropHarvestedMessage = false;
			}
		}
	}
	
	private void handleNPCInteraction(int entityId)
	{
		MovementDirection direction = mDirection.get(entityId);
		
		currentSelectedEntity.getComponent(MovementDirection.class).currentDirection = direction.currentDirection.getOpposite();
		if(!conversationLoaded)
		{	/*
			String conversationConfigJson = json.toJson(mConfig.get(currentSelectedEntity).charConfig);
			
			Gdx.app.debug(TAG, "conversationConfigJson: " + conversationConfigJson);
			notifyInteractionObservers(conversationConfigJson, InteractionEvent.LOAD_CONVERSATION);
			Gdx.app.debug(TAG, "Now exiting notifyInteractionObserver in handleNPCInteraction method");
			*/
			InteractionEvent event = InteractionEvent.LOAD_CONVERSATION;
			
			sendConversationCommandForEntity(event, currentSelectedEntity);
			
			conversationLoaded = true;
			sentShowConversationMessage = false;
			
			//showConversation(currentSelectedEntity);
		}
	
		if(!sentShowConversationMessage)
		{
			showConversation(currentSelectedEntity);
		}
	}
	
	public boolean detectEntity(int entityId)
	{
		BoundingBox box = mBoundingBox.get(entityId);
		
		//int [] entities = world.getSystem(CollisionSystem.class).getEntityIds().getData();
		IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(BoundingBox.class)).getEntities();
		
		for(int i = 0; i < entities.size(); i++)
		{
			int currentEntityId = entities.get(i);
			//Gdx.app.debug(TAG, "Entity with boundingBox: " + world.getEntity(entityId).toString());
			if(Intersector.intersectSegmentRectangle(box.selectorPoint, box.selectorPoint, world.getEntity(currentEntityId).getComponent(BoundingBox.class).boundingBox))
			{
				Gdx.app.debug(TAG, "Entity selected: " + world.getEntity(currentEntityId).toString());
				
				entitySelected = true;
				currentSelectedEntity = world.getEntity(currentEntityId);
				return true;
			}
		}
		
		entitySelected = false;
		return false;
	}
	
	public Entity getCurrentSelectedEntity()
	{
		return currentSelectedEntity;
	}
	
	public void showConversation(Entity selectedEntity)
	{
		//String value = "";
		InteractionEvent event;
		
		if(selectedEntity == null)
		{
			Gdx.app.debug(TAG, "The SELECT ENTITY button was pressed, but there was no entity detected.");
			return;
		}
		if(cropTag.has(selectedEntity))
		{
			Gdx.app.debug(TAG, "Can't select a crop atm, if you try to load its conversation everything crashes");
			
			return;
		}
		else
		{
			event = InteractionEvent.SHOW_CONVERSATION;
			sendConversationCommandForEntity(event, selectedEntity);
			
			sentShowConversationMessage = true;
		}
	}
	
	public void hideConversation(Entity selectedEntity)
	{
		InteractionEvent event = InteractionEvent.HIDE_CONVERSATION;
		
		sendConversationCommandForEntity(event, selectedEntity);
		
		sentShowConversationMessage = false;
	}
	
	private void sendConversationCommandForEntity(InteractionEvent event, Entity entity)
	{
		String entityName = mConfig.get(entity).charConfig.getEntityName();
		String conversationConfigPath = mConfig.get(entity).charConfig.getConversationConfigPath();
		
		notifyInteractionObservers(event, entityName, conversationConfigPath);
	}

	@Override
	public void addInteractionObserver(InteractionObserver interactionObserver)
	{
		interactionObservers.add(interactionObserver);
	}

	@Override
	public void removeInteractionObserver(InteractionObserver interactionObserver)
	{
		interactionObservers.removeValue(interactionObserver, true);
	}

	@Override
	public void removeAllInteractionObservers()
	{
		for (InteractionObserver observer : interactionObservers)
		{
			interactionObservers.removeValue(observer, true);
		}
	}

	/*
	@Override
	public void notifyInteractionObservers(String value, InteractionEvent event)
	{
		for (InteractionObserver observer : interactionObservers)
		{
			Gdx.app.debug(TAG, "Notifying observer " + observer.toString() + " of event " + event.toString());
			observer.onInteractionObserverNotify(value, event);
		}
	}
	*/
	@Override
	public void notifyInteractionObservers(InteractionEvent event, String name, String conversationPath)
	{
		for (InteractionObserver observer : interactionObservers)
		{	
			Gdx.app.debug(TAG, "Notifying observer " + observer.toString() + " of event " + event.toString());
			observer.onInteractionObserverNotify(event, name, conversationPath);
		}
	}

	@Override
	public void addInventoryObserver(InventoryObserver inventoryObserver)
	{
		inventoryObservers.add(inventoryObserver);
	}

	@Override
	public void removeInventoryObserver(InventoryObserver inventoryObserver)
	{
		inventoryObservers.removeValue(inventoryObserver, true);
	}

	@Override
	public void notifyInventoryObservers(ItemTypeID item, InventoryEvent event)
	{
		for (InventoryObserver observer : inventoryObservers)
		{
			observer.onInventoryNotify(item, event);
		}
	}

	@Override
	public void removeAllInventoryObservers()
	{
		for (InventoryObserver observer : inventoryObservers)
		{
			inventoryObservers.removeValue(observer, true);
		}
	}

}
