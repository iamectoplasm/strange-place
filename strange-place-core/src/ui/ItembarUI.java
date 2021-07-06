package ui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;

import inventory.items.InventoryItem.ItemTypeID;
import observers.ItembarObserver;
import observers.ItembarObserver.ItembarEvent;
import observers.subjects.ItembarSubject;
import utility.AssetHandler;

public class ItembarUI extends Window implements ItembarSubject
{

	public ItembarUI()
	{
		super("toolbar?", AssetHandler.ITEMBAR_SKIN, "toolbar");
		
		
	}

	@Override
	public void addToolbarObserver(ItembarObserver toolbarObserver)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeToolbarObserver(ItembarObserver toolbarObserver)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllToolbarObservers()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyToolbarObservers(ItemTypeID item, ItembarEvent event)
	{
		// TODO Auto-generated method stub
		
	}

}
