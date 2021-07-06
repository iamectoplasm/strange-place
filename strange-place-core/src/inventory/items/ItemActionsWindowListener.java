package inventory.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;

import inventory.InventorySlot;

public class ItemActionsWindowListener extends InputListener
{
	public static final String TAG = ItemActionsWindowListener.class.getSimpleName();
	
	private ItemActionsWindow itemActions;
	//private boolean isInside = false;
	
	private Vector2 currentCoords;
	//private Vector2 offset;
	
	public ItemActionsWindowListener(ItemActionsWindow inventorySlotItemActions)
	{
		this.itemActions = inventorySlotItemActions;
		this.currentCoords = new Vector2(0, 0);
		//this.offset = new Vector2(20, 10);
		//this.offset = new Vector2(0, 0);
	}
	
	@Override
	public void touchDragged (InputEvent event, float x, float y, int pointer)
	{
		itemActions.setVisible(false);
	}
	
	@Override
	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
	{
		InventorySlot inventorySlot = (InventorySlot) event.getListenerActor();
		
		boolean rightClickOnEmptySlot = (button == Input.Buttons.RIGHT) && (!inventorySlot.hasItem());
		boolean leftClickOnItem = (button == Input.Buttons.LEFT) && (inventorySlot.hasItem());
		
		if(rightClickOnEmptySlot || leftClickOnItem)
		{
			float displayX = inventorySlot.getOriginX();
			//float displayY = inventorySlot.getOriginY() - 24;
			float displayY = inventorySlot.getOriginY();
			currentCoords.set(displayX, displayY);
			inventorySlot.localToStageCoordinates(currentCoords);
		
			itemActions.populateActionList(inventorySlot);
			itemActions.setPosition(currentCoords.x, currentCoords.y, Align.topLeft);
			//itemActions.setPosition(currentCoords.x, currentCoords.y);
			itemActions.toFront();
			itemActions.setVisible(inventorySlot, true);
			
			//Gdx.app.debug(TAG, "In touchDown() method, setting itemActions window to visible"); 
		
			return true;
		}
		else
		{
			Gdx.app.debug(TAG, "In touchDown() method, adding hide() action");
			//itemActions.setVisible(false);
			itemActions.setVisible(inventorySlot, false);
			return false;
		}
	}
	
	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor)
	{
		//isInside = true;
	}
	
	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor)
	{
		//isInside = false;
	}
}
