package inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import inventory.items.InventoryItem;

public class InventorySlotSource extends Source
{
	public static final String TAG = InventorySlotSource.class.getSimpleName();
	
	//private DragAndDrop dragAndDrop;
	private InventorySlot sourceSlot;

	public InventorySlotSource(InventorySlot source, DragAndDrop dragAndDrop)
	{
		super(source.getInventoryItem());
		this.sourceSlot = source;
		//this.dragAndDrop = dragAndDrop;
		
		//Gdx.app.debug(TAG, "New InventorySlotSource constructed for item: " + source.getTopInventoryItem().getItemTypeID());
	}

	@Override
	public Payload dragStart(InputEvent event, float x, float y, int pointer)
	{
		//Gdx.app.debug(TAG, "Now in method dragStart()");
		//Gdx.app.debug(TAG, "\tParameters are: event = " + event + ", x = " + x + ", y = " + y + ", pointer = " + pointer);
		
		Payload payload = new Payload();
		
		InventoryItem item = (InventoryItem) getActor();
		int countRemoved = 0;
		
		if (item == null)
		{
			return null;
		}
		
		InventorySlot sourceSlot = (InventorySlot) item.getParent();
		
		if (sourceSlot == null)
		{
			//Gdx.app.debug(TAG, "Creating an InventorySource through actor.getParent() returned null");
			return null;
		}
		else
		{
			this.sourceSlot = sourceSlot;
		}
		
		Image dragActorImage = (Image) getActor();
		
		Actor dragActor = new Image(dragActorImage.getDrawable());
		
		//dragActor.setScale(1.5f);
		dragActor.setScale(2f);

		payload.setDragActor(dragActor);
		
		//payload.setObject(getActor());
		payload.setObject(item);
		
		//payload.setValidDragActor(dragActor);
		//payload.setInvalidDragActor(dragActor);
		
		//payload.setValidDragActor(new Label("Valid", Utility.TOOLBARUI_SKIN, "inventory-item-count"));
		//payload.setInvalidDragActor(new Label("Invalid", Utility.TOOLBARUI_SKIN, "inventory-item-count"));
		//dragAndDrop.setDragActorPosition(x,y);

		return payload;
	}
	
	@Override
	public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target)
	{
		if (target == null)
		{
			//InventoryItem payloadItem = (InventoryItem) payload.getObject();
			//sourceSlot.add(payloadItem);
			//sourceSlot.addItem(payloadItem, sourceSlot.getNumItems());
		}
	}

	public InventorySlot getSourceSlot()
	{
		//Gdx.app.debug(TAG, "Now in method getSourceSlot()");
		return this.sourceSlot;
	}
	
	public String toString()
	{
		String retString = "This InventorySlotSource is the slot at index" + sourceSlot.getSlotIndex();
		
		return retString;
	}
}
