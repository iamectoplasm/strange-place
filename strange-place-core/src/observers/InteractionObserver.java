package observers;

public interface InteractionObserver
{
	public static enum InteractionEvent
	{
		//ENTITY_SELECTED, ENTITY_DESELECTED,
		LOAD_CONVERSATION, SHOW_CONVERSATION, HIDE_CONVERSATION
	}

	//void onInteractionObserverNotify(final String value, InteractionEvent event);
	void onInteractionObserverNotify(InteractionEvent event, final String name, final String conversationPath);
	
}
