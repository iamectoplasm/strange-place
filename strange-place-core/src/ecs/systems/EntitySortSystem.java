package ecs.systems;

import java.util.Comparator;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;

import ecs.components.ActiveTag;
import ecs.components.Position;

public class EntitySortSystem extends EntitySystem
{
	private static final String TAG = EntitySortSystem.class.getSimpleName();
	
	private YComparator comparator;
	
	public EntitySortSystem()
	{
		super(Aspect.all(ActiveTag.class,
				 Position.class));
		
		Gdx.app.debug(TAG, "\t\tEntitySortSystem object contructed");
		
		this.comparator = new YComparator();
	}
	
	@Override
	protected void processSystem()
	{
		//getEntities().sort(comparator);
	}
	
	public Bag<Entity> getSortedEntities()
	{	
		getEntities().sort(comparator);
		
		//Gdx.app.debug(TAG, "Sorted entities: ");
		//for(Entity e: getEntities())
		//{
		//	Gdx.app.debug(TAG, "\tEntity: " + e.getComponent(Name.class).entityName);
		//}
		
		return getEntities();
	}
	
	public class YComparator implements Comparator<Entity>
	{
		@Override
		public int compare(Entity entity1, Entity entity2)
		{
			//return (int) (entity1.getComponent(Position.class).cellY - entity2.getComponent(Position.class).cellY);
			return (entity1.getComponent(Position.class).currentPosition.y - entity2.getComponent(Position.class).currentPosition.y) < 0 ? 1 : -1;
		}
	}
	
}