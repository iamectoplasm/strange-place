package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

import ecs.components.ActiveTag;
import ecs.components.BoundingBox;
import ecs.components.Position;
import ecs.components.Sprite;

public class RenderSystem extends EntitySystem
{
	private static final String TAG = RenderSystem.class.getSimpleName();
	
	ComponentMapper<Sprite> mSprite;
	ComponentMapper<Position> mPosition;

	// For collisions debugging
	//ComponentMapper<HomeMap> mLocation;
	ComponentMapper<BoundingBox> mBoundingBox;
	
	//Array<Entity> sortedEntities = new Array<Entity>();
	
	Camera camera;
	
	public RenderSystem(Camera camera)
	{
		super(Aspect.all(ActiveTag.class,
				 Sprite.class,
				 Position.class,
				 BoundingBox.class));
		
		Gdx.app.debug(TAG, "\t\tRenderSystem object constructed");

		this.camera = camera;
	}

	@Override
	protected void begin()
	{
	}
	
	@Override
	protected void processSystem()
	{
		Bag<Entity> sortedEntities = world.getSystem(EntitySortSystem.class).getSortedEntities();
		
		for(Entity e: sortedEntities)
		{
			Sprite sprite = mSprite.get(e);
			Position position = mPosition.get(e);
		
			//Gdx.app.debug(TAG, "Current entity being rendered: " + e.getComponent(Name.class).entityName);
			//Gdx.app.debug(TAG, "\tLocated at: (" + position.currentPosition.x + ", " + position.currentPosition.y + ")");
			
			//debugBoundingBox(e.getId());

			Batch batch = sprite.batch;

			batch.begin();
			batch.draw(sprite.currentFrame,
					position.currentPosition.x + position.xOffset,
					position.currentPosition.y + position.yOffset,
					sprite.drawWidth,
					sprite.drawHeight);
			batch.end();
		}
	}
	
	protected void debugBoundingBox(int entityId)
	{
		//HomeMap map = mLocation.get(entityId);
		BoundingBox box = mBoundingBox.get(entityId);
		
		//Camera camera = map.mapManager.getCamera();
		
		// Used to graphically debug boundingboxes
		box.shapeRenderer.setProjectionMatrix(camera.combined);
		
		box.shapeRenderer.begin(ShapeType.Filled);
		box.shapeRenderer.setColor(Color.CYAN);
		
		box.shapeRenderer.rect(box.boundingBox.x,
							   box.boundingBox.y,
							   box.boundingBox.width,
							   box.boundingBox.height);
		box.shapeRenderer.end();
		
		box.shapeRenderer.begin(ShapeType.Filled);
		box.shapeRenderer.setColor(Color.CORAL);
		box.shapeRenderer.rectLine(box.boundingBox.getCenter(new Vector2()), box.selectorPoint, (1/16f));
		box.shapeRenderer.end();
	}

}
