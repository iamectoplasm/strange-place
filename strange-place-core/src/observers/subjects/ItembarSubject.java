package observers.subjects;

import inventory.items.InventoryItem;
import observers.ItembarObserver;

public interface ItembarSubject
{
	public void addToolbarObserver(ItembarObserver toolbarObserver);

	public void removeToolbarObserver(ItembarObserver toolbarObserver);

	public void removeAllToolbarObservers();

	public void notifyToolbarObservers(final InventoryItem.ItemTypeID item, ItembarObserver.ItembarEvent event);
}
