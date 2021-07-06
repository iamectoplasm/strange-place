package observers.subjects;

import observers.ToolbarObserver;

public interface ToolbarSubject
{
	public void addObserver(ToolbarObserver toolbarObserver);

	public void removeObserver(ToolbarObserver toolbarObserver);

	public void removeAllObservers();

	public void notify(final int value, ToolbarObserver.StatusEvent event);
}
