package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.utils.Array;

import ecs.components.ActiveTag;
import ecs.components.GrowthState;
import ecs.components.PlantTag;
import ecs.components.Species;
import ecs.components.Sprite;
import ecs.components.StaticAnimation;
import inventory.items.InventoryItem.ItemTypeID;
import observers.InventoryObserver;
import observers.InventoryObserver.InventoryEvent;
import observers.subjects.InventorySubject;

public class CropGrowthSystem extends IntervalIteratingSystem implements InventorySubject
{
	protected ComponentMapper<PlantTag> cropTag;
	protected ComponentMapper<GrowthState> growthState;
	protected ComponentMapper<Species> species;
	
	private Array<InventoryObserver> inventoryObservers = new Array<InventoryObserver>();

	public CropGrowthSystem()
	{
		super(Aspect.all(ActiveTag.class,
				PlantTag.class,
				StaticAnimation.class,
				GrowthState.class,
				Sprite.class),
				(1/60f));
	}

	@Override
	protected void process(int entityId)
	{
		//if(growthState.get(entityId).hasBeenWatered && growthState.get(entityId).currentGrowthStage == GrowthStage.HARVESTABLE)
		//{
			//ItemTypeID item = species.get(entityId).produce;
			//notifyInventoryObservers(item, InventoryEvent.RECEIVE_ITEM);
			
			//world.getEntity(entityId).deleteFromWorld();
		//}
		if(growthState.get(entityId).hasBeenWatered)
		{
			growthState.get(entityId).iterateGrowthStage();
			growthState.get(entityId).hasBeenWatered = false;
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
	public void removeAllInventoryObservers()
	{
		for (InventoryObserver observer : inventoryObservers)
		{
			inventoryObservers.removeValue(observer, true);
		}
	}

	@Override
	public void notifyInventoryObservers(ItemTypeID item, InventoryEvent event)
	{
		for (InventoryObserver observer : inventoryObservers)
		{
			observer.onInventoryNotify(item, event);
		}
	}
	
}
