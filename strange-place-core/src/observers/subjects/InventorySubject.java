package observers.subjects;

import inventory.items.InventoryItem;
import observers.InventoryObserver;

public interface InventorySubject
{
	public void addInventoryObserver(InventoryObserver inventoryObserver);

	public void removeInventoryObserver(InventoryObserver inventoryObserver);

	public void removeAllInventoryObservers();

	public void notifyInventoryObservers(final InventoryItem.ItemTypeID item, InventoryObserver.InventoryEvent event);
}
