package observers;

import inventory.InventorySlot;

public interface ItemBarObserver
{
	public static enum ItemBarEvent
	{
		UPDATE_BAR, UPDATE_SELECTED_ITEM, USE_SELECTED_ITEM
	}

	//void onToolbarNotify(final InventoryItem.ItemTypeID item, ItembarEvent event);
	void onItemBarNotify(final InventorySlot slot, ItemBarEvent event);
}
