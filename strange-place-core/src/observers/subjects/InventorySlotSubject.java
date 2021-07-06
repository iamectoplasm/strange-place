package observers.subjects;

import inventory.InventorySlot;
import observers.InventorySlotObserver;

public interface InventorySlotSubject
{
	public void addInventorySlotObserver(InventorySlotObserver inventorySlotObserver);

	public void removeInventorySlotObserver(InventorySlotObserver inventorySlotObserver);

	public void removeAllInventorySlotObservers();

	public void notifyInventorySlotObservers(final InventorySlot slot, InventorySlotObserver.SlotEvent event);
}
