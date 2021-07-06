package observers;

import inventory.items.InventoryItem;

public interface ItembarObserver
{
	public static enum ItembarEvent
	{
		UPDATE_SELECTED_ITEM, USE_SELECTED_ITEM
	}

	void onToolbarNotify(final InventoryItem.ItemTypeID item, ItembarEvent event);
}
