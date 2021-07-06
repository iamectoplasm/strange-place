package observers;

import com.artemis.Entity;

public interface EntitySelectionObserver
{
	public static enum SelectionEvent
	{
		ENTITY_SELECTED, ENTITY_DESELECTED
	}
	
	void onEntitySelectionNotify(final Entity entity, SelectionEvent event);
}
