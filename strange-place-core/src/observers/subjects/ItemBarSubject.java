package observers.subjects;

import inventory.InventorySlot;
import observers.ItemBarObserver;

public interface ItemBarSubject
{
	public void addItemBarObserver(ItemBarObserver itemBarObserver);

	public void removeItemBarObserver(ItemBarObserver itemBarObserver);

	public void removeAllItemBarObservers();

	public void notifyItemBarObservers(final InventorySlot slot, ItemBarObserver.ItemBarEvent event);
}
