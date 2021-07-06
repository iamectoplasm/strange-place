package observers;

import interaction.ConversationGraph;

/*
 * The ConversationGraphObserver interface sends notifications for ConversationCommandEvents.
 * Examples: When the conversation has ended, or the player wants to see a vendor's wares based on their conversation.
 */
public interface ConversationGraphObserver
{
	public static enum ConversationCommandEvent
	{
		LOAD_STORE_INVENTORY, EXIT_CONVERSATION, ACCEPT_QUEST, ADD_ENTITY_TO_INVENTORY, RETURN_QUEST, NONE
	}

	void onConversationGraphNotify(final ConversationGraph graph, ConversationCommandEvent event);
	
	String toString();
}
