package interaction;

import java.util.Hashtable;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import observers.subjects.ConversationGraphSubject;

/*
 * ConversationGraph extends ConversationGraphSubject so that we can hook in ConversationGraphObservers
 * in order to get notifications from the current graph.
 */
public class ConversationGraph extends ConversationGraphSubject {
	private static final String TAG = ConversationGraph.class.getSimpleName();

	// Has a String type for a key, which represents the unique ID of the vertex
	// (represented by the Conversation class).
	// Each key should have access to a Conversation object with the dialog for its
	// respective vertex.
	private Hashtable<String, Conversation> conversations;

	// Has a String type for a key, which represents the unique ID of the edge
	// (represented by the ConversationChoice class).
	// AssociatedChoices contains an ArrayList of the ConversationChoice objects.
	// Represents the vertices connected by an edge to each vertex.
	private Hashtable<String, Array<ConversationChoice>> associatedChoices;

	// currentConversationId represents the current Conversation vertex for easier
	// bookkeeping
	private String currentConversationId;

	// If the default constructor is called, we then have to use setter methods to
	// populate the graph.
	// The setter method automatically get called due to reflection, as the
	// information is stored in JSON files
	public ConversationGraph() {
		Gdx.app.debug(TAG, "\t\tConversationGraph object constructed");
	}

	// This constructor populates the ConversationGraph by passing in the Hashtable
	// that already
	// contains all the Conversation based vertices.
	public ConversationGraph(Hashtable<String, Conversation> conversations, String rootId) {
		// Gdx.app.debug(TAG, "Constructor ConversationGraph(" +
		// conversations.toString() + ", " + rootId + ") called");
		setConversations(conversations);
		setCurrentConversation(rootId);
	}

	public void setConversations(Hashtable<String, Conversation> conversations) {
		// Gdx.app.debug(TAG, "Method setConversations(" + conversations.toString() + ")
		// called");

		if (conversations.size() < 0) {
			throw new IllegalArgumentException(
					"Can't have a negative amount of conversations, that doesn't make sense!");
		}

		this.conversations = conversations;
		this.associatedChoices = new Hashtable<String, Array<ConversationChoice>>(conversations.size());

		for (Conversation conversation : conversations.values()) {
			associatedChoices.put(conversation.getId(), new Array<ConversationChoice>());
		}

		this.conversations = conversations;
	}

	public Array<ConversationChoice> getCurrentChoices() {
		// Gdx.app.debug(TAG, "Method getCurrentChoices() called");

		return associatedChoices.get(currentConversationId);
	}

	public String getCurrentConversationId() {
		// Gdx.app.debug(TAG, "Method getCurrentConversationId() called");
		return this.currentConversationId;
	}

	// isValid() will take the ID passed in as a key for the conversations
	// Hashtable.
	// If the Conversation vertex exists, return true. Otherwise return false.
	public boolean isValid(String conversationId) {
		// Gdx.app.debug(TAG, "Method isValid(" + conversationId + ") called");

		Conversation conversation = conversations.get(conversationId);
		if (conversation == null)
			return false;

		return true;
	}

	// isReachable() takes two vertex IDs.
	// Then tests whether there is a valid edge connection from the source vertex to
	// the destination vertex.
	// If there is, return true. Otherwise, the destination vertex is not reachable
	// so return false.
	public boolean isReachable(String sourceId, String sinkId) {
		// Gdx.app.debug(TAG, "Method isReachable(" + sourceId + ", " + sinkId + ")
		// called");

		if (!isValid(sourceId) || !isValid(sinkId))
			return false;

		if (conversations.get(sourceId) == null)
			return false;

		// First get edges/choices from the source
		Array<ConversationChoice> list = associatedChoices.get(sourceId);

		if (list == null)
			return false;

		for (ConversationChoice choice : list) {
			if (choice.getSourceId().equalsIgnoreCase(sourceId) && choice.getDestinationId().equalsIgnoreCase(sinkId)) {
				return true;
			}
		}

		return false;
	}

	public Conversation getConversationById(String id) {
		// Gdx.app.debug(TAG, "Method getConversationById(" + id + ") called");

		if (!isValid(id)) {
			System.out.println("Id " + id + " is not valid!");
			return null;
		}

		return conversations.get(id);
	}

	public void setCurrentConversation(String id) {
		// Gdx.app.debug(TAG, "Method setCurrentConversations(" + id + ") called");

		Conversation conversation = getConversationById(id);
		if (conversation == null)
			return;
		// Can we reach a new conversation from the current one?

		// Make sure we check the case where the current node is checked against itself
		if (currentConversationId == null || currentConversationId.equalsIgnoreCase(id)
				|| isReachable(currentConversationId, id)) {
			currentConversationId = id;
		} else {
			System.out.println("New conversation node [" + id + "] is not reachable from current node ["
					+ currentConversationId + "]");
		}
	}

	public void addChoice(ConversationChoice conversationChoice) {
		// Gdx.app.debug(TAG, "Method addChoice(" + conversationChoice.toString() + ")
		// called");

		Array<ConversationChoice> list = associatedChoices.get(conversationChoice.getSourceId());

		if (list == null)
			return;

		list.add(conversationChoice);
	}

	public String displayCurrentConversation() {
		// Gdx.app.debug(TAG, "Method displayCurrentConversation() called");

		return conversations.get(currentConversationId).getDialog();
	}

	@Override
	public String toString() {
		StringBuilder outputString = new StringBuilder();
		int numTotalChoices = 0;

		Set<String> keys = associatedChoices.keySet();
		for (String id : keys) {
			outputString.append(String.format("[%s]: ", id));

			for (ConversationChoice choice : associatedChoices.get(id)) {
				numTotalChoices++;
				outputString.append(String.format("%s ", choice.getDestinationId()));
			}

			outputString.append(System.getProperty("line.separator"));
		}

		outputString.append(String.format("Number conversations: %d", conversations.size()));
		outputString.append(String.format(", Number of choices: %d", numTotalChoices));
		outputString.append(System.getProperty("line.separator"));

		return outputString.toString();
	}

	public String toJson() {
		// Gdx.app.debug(TAG, "Method toJson() called");

		Json json = new Json();

		return json.prettyPrint(this);
	}
}
