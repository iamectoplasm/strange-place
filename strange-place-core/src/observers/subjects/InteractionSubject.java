package observers.subjects;

import observers.InteractionObserver;

public interface InteractionSubject
{
	public void addInteractionObserver(InteractionObserver interactionObserver);
	
	public void removeInteractionObserver(InteractionObserver interactionObserver);
	
	public void removeAllInteractionObservers();
	
	//public void notifyInteractionObservers(final String value, InteractionObserver.InteractionEvent event);
	public void notifyInteractionObservers(InteractionObserver.InteractionEvent event, final String name, final String conversationPath);
}
