package observers;

import inventory.InventorySlot;

public interface InventorySlotObserver
{
	public static enum SlotEvent
	{
		ADDED_ITEM, REMOVED_ITEM, CLEAR_SLOT
	}

	void onInventorySlotNotify(final InventorySlot slot, SlotEvent event);
}
