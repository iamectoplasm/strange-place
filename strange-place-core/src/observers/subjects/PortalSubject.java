package observers.subjects;

import com.badlogic.gdx.utils.Array;

import observers.PortalObserver;

public class PortalSubject
{
	private static final String TAG = PortalSubject.class.getSimpleName();
	
	private Array<PortalObserver> observers;
	
	public PortalSubject()
	{
		//Gdx.app.debug(TAG, "PortalSubject object constructed");
		
		this.observers = new Array<PortalObserver>();
	}
	
	public void addPortalObserver(PortalObserver portalObserver)
	{
		observers.add(portalObserver);
	}
	
	public void removePortalObserver(PortalObserver portalObserver)
	{
		observers.removeValue(portalObserver, true);
	}
	
	public void removeAllPortalObservers()
	{
		for (PortalObserver observer : observers)
		{
			observers.removeValue(observer, true);
		}
	}
	
	public void notifyPortalObservers(final String newMapName, final String spawnName, PortalObserver.PortalEvent event)
	{
		for (PortalObserver observer : observers)
		{
			// Gdx.app.debug(TAG, "Notifying observer " + observer.toString() + " of event "
			// + event.toString());
			observer.onPortalObserverNotify(newMapName, spawnName, event);
		}
	}
}
