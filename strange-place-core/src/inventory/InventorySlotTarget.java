package inventory;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import inventory.items.InventoryItem;

public class InventorySlotTarget extends Target
{
	public static final String TAG = InventorySlotTarget.class.getSimpleName();
	
	private InventorySlot targetSlot;

	public InventorySlotTarget(InventorySlot target)
	{
		super(target);
		this.targetSlot = target;
		
		//Gdx.app.debug(TAG, "New InventorySlotTarget() constructed for " + target);
	}

	@Override
	public boolean drag(Source source, Payload payload, float x, float y, int pointer)
	{
		if(payload == null)
		{
			//Gdx.app.debug(TAG, "\t\tdrag() PAYLOAD IS NULL, meaning that dragStart() returned null");
			//Gdx.app.debug(TAG, "\tParameters are: source = " + source.toString() + ", payload = NULL" + ", x = " + x + ", y = " + y + ", pointer = " + pointer);
			return false;
		}
		else if(source == null)
		{
			//Gdx.app.debug(TAG, "\t\tdrag() SOURCE IS NULL");
			//Gdx.app.debug(TAG, "\tParameters are: source = NULL" + ", payload = " + payload.toString() + ", x = " + x + ", y = " + y + ", pointer = " + pointer);
		}
		else
		{
			//Gdx.app.debug(TAG, "\tParameters are: source = " + source.getActor().getName() + ", payload = " + payload.getObject().getClass() + ", x = " + x + ", y = " + y + ", pointer = " + pointer);
		}
		
		//payload.getDragActor().setPosition(x, y);
		
		return true;
	}

	@Override
	public void drop(Source source, Payload payload, float x, float y, int pointer)
	{
		//Gdx.app.debug(TAG, "Now in method drop() for " + targetSlot);
		//Gdx.app.debug(TAG, "\tParameters are: source = " + source.toString() +
		//		", payload = " + payload.toString() + ", x = " + x + ", y = " + y + ", pointer = " + pointer);
		
		InventorySlot sourceSlot = ((InventorySlotSource) source).getSourceSlot();
		InventoryItem sourceItem = (InventoryItem) payload.getObject();
		//InventoryItem sourceActor = (InventoryItem) source.getActor();
		
		if (sourceItem == null)
		{
			//Gdx.app.debug(TAG, "\tsourceItem is null! now returning...");
			return;
		}
		
		//Gdx.app.debug(TAG, "\tsourceActor info: " + sourceItem.getItemTypeID());
		
		if(!targetSlot.hasItem())
		{
			//Gdx.app.debug(TAG, "\tTargetSlot is empty, sourceActor will be added to it");
			targetSlot.addItem(sourceItem, sourceSlot.getNumItems());
			sourceSlot.resetSlot();
			
			//Gdx.app.debug(TAG, "\tnow returning from drop() method"); 
			return;
		}
		
		InventoryItem targetItem = targetSlot.getInventoryItem();
		
		//Gdx.app.debug(TAG, "\ttargetActor info: " + targetItem.getItemTypeID());
		
		//Gdx.app.debug(TAG, "\ttargetSlot is slot at index: " + targetSlot.getSlotIndex());

		if(sourceItem.isSameItemType(targetItem) && sourceItem.isStackable())
		{
			targetSlot.addItem(sourceItem, sourceSlot.getNumItems());
			sourceSlot.resetSlot();
		}
		else
		{
			// If they aren't the same items or do not stack, then swap
			InventorySlot.swapSlots(sourceSlot, targetSlot, sourceItem);
		}
	}

	public void reset(Source source, Payload payload, float x, float y, int pointer)
	{ }
}
