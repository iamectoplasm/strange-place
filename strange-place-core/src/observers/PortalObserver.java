package observers;

public interface PortalObserver
{
	public static enum PortalEvent
	{
		PORTAL_TRIGGERED
	}

	void onPortalObserverNotify(final String newMap, final String spawnName, PortalEvent event);
}
