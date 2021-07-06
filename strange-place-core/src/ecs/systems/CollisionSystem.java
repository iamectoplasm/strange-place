package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import ecs.components.ActiveTag;
import ecs.components.BoundingBox;
import ecs.components.MovementDirection;
import ecs.components.Position;
import maps.MapManager;

public class CollisionSystem extends IntervalIteratingSystem
{
	public static final String TAG = CollisionSystem.class.getSimpleName();

	protected ComponentMapper<Position> mPosition;
	protected ComponentMapper<MovementDirection> mDirection;
	protected ComponentMapper<BoundingBox> mBox;
	
	MapManager worldMapManager;

	public CollisionSystem(MapManager mapManager)
	{
		super(Aspect.all(ActiveTag.class,
						 Position.class,
						 MovementDirection.class,
						 BoundingBox.class), (1/60f));
		
		this.worldMapManager = mapManager;
	}

	public void initBoundingBox(int entityId)
	{
		BoundingBox box = mBox.get(entityId);
		Position position = mPosition.get(entityId);
		
		box.shapeRenderer = new ShapeRenderer();

		box.xPos = position.currentPosition.x;
		box.yPos = position.currentPosition.y;

		box.boundingBox = new Rectangle(box.xPos, box.yPos, 1, 1);
		
		box.boundingBoxCenter = box.boundingBox.getCenter(new Vector2());
		box.selectorPoint = box.boundingBoxCenter;
	}
	
	@Override
	protected void process(int entityId)
	{
		updateBoundingBoxPosition(entityId);
	}

	protected void updateBoundingBoxPosition(int entityId)
	{
		BoundingBox box = mBox.get(entityId);
		Position position = mPosition.get(entityId);
		MovementDirection direction = mDirection.get(entityId);
		
		box.xPos = position.currentPosition.x;
		box.yPos = position.currentPosition.y;
		box.boundingBox.set(box.xPos, box.yPos, 1, 1);
		box.boundingBoxCenter = box.boundingBox.getCenter(new Vector2());
		
		box.selectorPoint.set(box.boundingBoxCenter.x + direction.currentDirection.getDX(), box.boundingBoxCenter.y + direction.currentDirection.getDY());
	}

	public boolean checkForMapCollision(int entityId)
	{
		BoundingBox box = mBox.get(entityId);
		Position position = mPosition.get(entityId);
		
		float xDest = position.destinationPosition.x;
		float yDest = position.destinationPosition.y;
		
		TiledMapTileLayer collisions = worldMapManager.getCollisionLayer();
		Cell destinationCell = collisions.getCell((int) xDest, (int) yDest);

		if (destinationCell != null)
		{
			box.xPos = position.startingPosition.x;
			box.yPos = position.startingPosition.y;
			box.boundingBox.setPosition(box.xPos, box.yPos);
			return true;
		}
		else
			return false;
	}
	
	public boolean checkForEntityCollision(int entityId)
	{	
		BoundingBox box = mBox.get(entityId);
		
		IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(BoundingBox.class)).getEntities();
		
		for(int i = 0; i < entities.size(); i++)
		{
			int currentEntityID = entities.get(i);
			if(Intersector.intersectSegmentRectangle(box.selectorPoint, box.selectorPoint, mBox.get(currentEntityID).boundingBox))
			{
				return true;
			}
		}
		
		//for(int i = 0; i < worldMapManager.getCurrentMapEntities().size; i++)
		//{
		//	Entity currentEntity = worldMapManager.getCurrentMapEntities().get(i);
		//	if(Intersector.intersectSegmentRectangle(box.selectorPoint, box.selectorPoint, currentEntity.getComponent(BoundingBox.class).boundingBox))
		//	{
		//		return true;
		//	}
		//}
		
		return false;
	}
}
