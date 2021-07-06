package observers;

public interface ToolbarObserver
{
	public static enum StatusEvent
	{
		UPDATE_MONEY
	}

	void onToolbarBarNotify(final int value, StatusEvent event);
}
