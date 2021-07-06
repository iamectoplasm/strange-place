package observers;

import inventory.items.InventoryItem;

public interface InventoryObserver
{
	public static enum InventoryEvent
	{
		PLANT_ITEM, CONSUME_ITEM, RECEIVE_ITEM, NONE
	}

	void onInventoryNotify(final InventoryItem.ItemTypeID item, InventoryEvent event);
}
