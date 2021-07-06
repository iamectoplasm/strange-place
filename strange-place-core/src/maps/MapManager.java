package maps;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import ecs.components.ActiveTag;
import ecs.components.Sprite;
import ecs.systems.CollisionSystem;
import observers.InteractionObserver;
import saves.ProfileManager;
import saves.ProfileObserver;

public class MapManager implements ProfileObserver
{
	private static final String TAG = MapManager.class.getSimpleName();

	private Camera camera;

	private boolean mapChanged = false;
	private Map currentMap = null;
	private Map previousMap = null;

	private Entity player;

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Constructor
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public MapManager()
	{
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Map loader
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public void loadMap(MapFactory.MapType mapType)
	{
		if(currentMap != null)
		{
			previousMap = currentMap;
			
			if(!currentMap.mapEntities.isEmpty())
			{
				clearCurrentMapEntities();
			}
		}
		
		Map map = MapFactory.getMap(mapType);
		if (map == null)
		{
			Gdx.app.debug(TAG, "Map does not exist");
			return;
		}
		
		currentMap = map;
		mapChanged = true;
		//clearCurrentSelectedMapEntity();
		//Gdx.app.debug(TAG,
		//		"Player Start: (" + currentMap.getUnscaledPlayerStart().x + ", " + currentMap.getUnscaledPlayerStart().y + ")");
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Map getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public TiledMap getCurrentTiledMap()
	{
		if (currentMap == null)
		{
			//loadMap(MapFactory.MapType.AREA1_1);
			loadMap(MapFactory.MapType.MARISOL);
			//loadMap(MapFactory.MapType.BUS_STATION);
			// loadMap(MapFactory.MapType.TESTING);
		}

		return currentMap.getCurrentTiledMap();
	}

	//public MapFactory.MapType getCurrentMapType()
	//{
	//	return currentMap.getCurrentMapType();
	//}
	
	public Map getCurrentMap()
	{
		return this.currentMap;
	}
	
	public Map getPreviousMap()
	{
		return this.previousMap;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Layers
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public TiledMapTileLayer getCollisionLayer()
	{
		return currentMap.getCollisionLayer();
	}

	public MapLayer getPortalLayer()
	{
		return currentMap.getPortalLayer();
	}
	
	public MapLayer getSpawnsLayer()
	{
		return currentMap.getSpawnsLayer();
	}

	public MapGroupLayer getBackground()
	{
		return currentMap.getBackground();
	}

	public MapGroupLayer getForeground()
	{
		return currentMap.getForeground();
	}

	public void renderBackground(OrthogonalTiledMapRenderer renderer, Batch batch)
	{
		batch.begin();
		for (int i = 0; i < getBackground().getLayers().getCount(); i++)
		{
			renderer.renderTileLayer((TiledMapTileLayer) getBackground().getLayers().get(i));
		}

		batch.end();
	}

	public void renderForeground(OrthogonalTiledMapRenderer renderer, Batch batch)
	{
		batch.begin();
		for (int i = 0; i < getForeground().getLayers().getCount(); i++) {
			renderer.renderTileLayer((TiledMapTileLayer) getForeground().getLayers().get(i));
		}

		batch.end();
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Player
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Vector2 getPlayerStartUnitScaled() 
	{
		Gdx.app.debug(TAG, "getPlayerStartUnitScaled() called in MapManager");
		return currentMap.getPlayerStart();
	}

//	public void setClosestStartPositionFromScaledUnits(Vector2 position)
//	{
//		Gdx.app.debug(TAG, "setPlayerStartUnitScaled() called with position: (" + position.x + ", " + position.y + ")");
//		currentMap.setClosestStartPosition(position);
//	}

	public Entity getPlayer()
	{
		return player;
	}

	public void setPlayer(Entity entity)
	{
		this.player = entity;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Entities
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public void initializeMapEntities(MapManager mapManager, Batch batch)
	{
		//currentMap.initializeMapEntities(mapManager, batch);
		for (int i = 0; i < currentMap.mapEntities.size; i++)
		{
			currentMap.mapEntities.get(i).getComponent(Sprite.class).batch = batch;
			//currentMap.mapEntities.get(i).getComponent(HomeMap.class).mapManager = mapManager;
			
			currentMap.mapEntities.get(i).getWorld().getSystem(CollisionSystem.class).initBoundingBox(currentMap.mapEntities.get(i).getId());
			
			currentMap.mapEntities.get(i).edit().add(new ActiveTag());
		}
	}
	
	public void addNewMapEntity(MapManager mapManager, Batch batch, Entity entity)
	{
		currentMap.addNewMapEntity(mapManager, batch, entity);
	}

	public final Array<Entity> getCurrentMapEntities()
	{
		return currentMap.getMapEntities();
	}
	
	/**
	 * NOT WORKING AS IT NEEDS TO-- FIGURE THIS OUT
	 */
	public void clearCurrentMapEntities()
	{
		for(Entity e: currentMap.mapEntities)
		{
			//e.edit().remove(e.getComponent(ActiveTag.class));
		}
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Camera
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Camera getCamera()
	{
		return camera;
	}

	public void setCamera(Camera camera)
	{
		this.camera = camera;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Map changes
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public boolean hasMapChanged()
	{
		return mapChanged;
	}

	public void setMapChanged(boolean hasMapChanged)
	{
		this.mapChanged = hasMapChanged;
	}

	@Override
	public void onProfileNotify(ProfileManager profileManager, ProfileEvent event)
	{
		switch (event)
		{
		case PROFILE_LOADED:
			String currentMapString = profileManager.getProperty("currentMapType", String.class);
			MapFactory.MapType mapType;
			if (currentMapString == null || currentMapString.isEmpty())
			{
				mapType = MapFactory.MapType.MARISOL;
				//mapType = MapFactory.MapType.BUS_STATION;
			}
			else
			{
				mapType = MapFactory.MapType.valueOf(currentMapString);
			}
			
			loadMap(mapType);
			break;

		case SAVING_PROFILE:
			//ProfileManager.getInstance().setProperty("currentMapType", currentMap.getCurrentMapType().toString());
			break;
		default:
			break;
		}
	}

	public void registerCurrentMapEntityObservers(InteractionObserver observer)
	{
		if (currentMap != null)
		{
			//Array<Entity> entities = currentMap.getMapEntities();
			//for (Entity entity : entities)
			//{
				//entity.getComponent(Selectable.class).registerObserver(observer);
			//}
		}
	}

	public void unregisterCurrentMapEntityObservers()
	{
		if (currentMap != null)
		{
			//Array<Entity> entities = currentMap.getMapEntities();
			//for (Entity entity : entities)
			//{
				//entity.unregisterObservers();
			//}
		}
	}

	// 12/5/19 additions
	public int getMapWidth()
	{
		return currentMap.getCurrentTiledMap().getProperties().get("width", Integer.class);
	}

	public int getMapHeight()
	{
		return currentMap.getCurrentTiledMap().getProperties().get("height", Integer.class);
	}
	
	// 1/4/21 addition
	public Vector2 getPositionNormalized(Vector2 original)
	{
		Vector2 posWithFlippedY = new Vector2(original.x, this.getMapHeight() - 1 - original.y);
		return posWithFlippedY;
	}
}
