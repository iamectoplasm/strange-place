package interaction;

import com.badlogic.gdx.Gdx;

import observers.ConversationGraphObserver.ConversationCommandEvent;

/*
 * ConversationChoice is a POJO.
 * 
 * 		Purpose is to connect two vertices (source and destination) with a particular choice to be displayed to the player,
 * 		and to manage any events (ConversationCommandEvent) that occur from a particular choice. 
 */
public class ConversationChoice {
	private static final String TAG = ConversationChoice.class.getSimpleName();

	// Source and destination IDs represent the direction of the connection for the
	// directed graph--
	// Will direct the flow of the conversation as the player makes choices at each
	// Conversation vertex of the graph.
	private String sourceId;
	private String destinationId;

	private String choicePhrase;

	private ConversationCommandEvent conversationCommandEvent;

	public ConversationChoice() {
		Gdx.app.debug(TAG, "\t\tConversationChoice object constructed");
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public String getChoicePhrase() {
		return choicePhrase;
	}

	public void setChoicePhrase(String choicePhrase) {
		this.choicePhrase = choicePhrase;
	}

	public ConversationCommandEvent getConversationCommandEvent() {
		return conversationCommandEvent;
	}

	public void setConversationCommandEvent(ConversationCommandEvent choiceCommand) {
		this.conversationCommandEvent = choiceCommand;
	}

	@Override
	public String toString() {
		return choicePhrase;
	}
}
