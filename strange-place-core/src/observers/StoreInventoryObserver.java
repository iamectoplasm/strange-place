package observers;

public interface StoreInventoryObserver
{
	public static enum StoreInventoryEvent
	{
		PLAYER_MONEY_UPDATED, PLAYER_INVENTORY_UPDATED
	}

	void onStoreInventoryNotify(String value, StoreInventoryEvent event);
}
