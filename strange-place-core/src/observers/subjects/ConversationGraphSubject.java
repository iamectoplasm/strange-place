package observers.subjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import interaction.ConversationGraph;
import observers.ConversationGraphObserver;
import observers.ConversationGraphObserver.ConversationCommandEvent;

/*
 * The ConversationGraphSubject sends notifications to ConversationGraphObserver
 * 		as the player moves through the conversation graph by selecting different options
 */
public class ConversationGraphSubject
{
	private static final String TAG = ConversationGraphSubject.class.getSimpleName();

	private Array<ConversationGraphObserver> observers;

	public ConversationGraphSubject()
	{
		// Gdx.app.debug(TAG, "Constructor ConversationGraphSubject() called");
		observers = new Array<ConversationGraphObserver>();
	}

	public void addConversationGraphObserver(ConversationGraphObserver graphObserver)
	{
		// Gdx.app.debug(TAG, "Method addObserver(" + graphObserver.toString() + ")
		// called");
		observers.add(graphObserver);
	}

	public void removeConversationGraphObserver(ConversationGraphObserver graphObserver)
	{
		// Gdx.app.debug(TAG, "Method removeObserver(" + graphObserver.toString() + ")
		// called");
		observers.removeValue(graphObserver, true);
	}

	public void removeAllConversationGraphObservers()
	{
		// Gdx.app.debug(TAG, "Method removeAllObservers() called");
		for (ConversationGraphObserver observer : observers)
		{
			observers.removeValue(observer, true);
		}
	}

	public void notifyConversationGraphObservers(ConversationGraph graph, ConversationCommandEvent event)
	{
		for (ConversationGraphObserver observer : observers)
		{
			Gdx.app.debug(TAG, "Notifying observer " + observer.toString() + " of event " + event.toString());
			observer.onConversationGraphNotify(graph, event);
		}
	}
}
