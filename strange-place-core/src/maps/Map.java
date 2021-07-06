package maps;

import com.artemis.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import configs.MapConfig;
import configs.MapConfig.MapEntity;
import ecs.components.ActiveTag;
import ecs.components.BoundingBox;
import ecs.components.Position;
import ecs.components.Sprite;
import ecs.systems.CollisionSystem;
import game.EntityFactory;
import game.EntityFactory.EntityName;
import maps.MapFactory.MapType;
import utility.AssetHandler;

public abstract class Map
{
	private static final String TAG = Map.class.getSimpleName();
	public static final float UNIT_SCALE = 1 / 16f;

	// Map layers
	protected final static String COLLISION_LAYER = "collisions";
	protected final static String SPAWNS_LAYER = "spawns";
	protected final static String PORTAL_LAYER = "portals";

	protected final static String BACKGROUND_LAYERS = "background";
	protected final static String FOREGROUND_LAYERS = "foreground";

	/*
	// Start locations
	protected final static String PLAYER_SPAWN = "spawn";
	protected final static String NPC_SPAWN = "npc-spawn";
	protected Array<Vector2> npcStartPositions;
	protected Hashtable<String, Vector2> specialNPCStartPositions;
	*/

	protected Json json;
	protected MapConfig mapConfig;

	// Class variables
	private TiledMap currentMap = null;
	
	private Vector2 playerStart;

	private MapLayer collisionLayer = null;
	private MapLayer portalLayer = null;
	private MapLayer spawnsLayer = null;

	// private Array<MapLayer> backgroundLayers;
	// private Array<MapLayer> foregoundLayers;
	private MapGroupLayer backgroundLayers;
	private MapGroupLayer foregroundLayers;

	private MapFactory.MapType currentMapType;

	protected Array<Entity> mapEntities;

	private Array<Portal> portals;
	
	private int mapWidth;
	private int mapHeight;

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Constructor
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Map(MapFactory.MapType mapType, String fullMapPath)
	{
		this.currentMapType = mapType;

		this.json = new Json();
		this.mapEntities = new Array<Entity>(20);

		//this.unscaledStart = new Vector2(0, 0);
		this.playerStart = new Vector2(0, 0);
		//this.playerStartPositionRect = new Vector2(0, 0);
		//this.closestPlayerStartPosition = new Vector2(0, 0);

		if (fullMapPath == null || fullMapPath.isEmpty())
		{
			Gdx.app.debug(TAG, "Map is invalid");
			return;
		}

		AssetHandler.loadMapAsset(fullMapPath);
		if (AssetHandler.isAssetLoaded(fullMapPath))
		{
			currentMap = AssetHandler.getMapAsset(fullMapPath);
		}
		else
		{
			Gdx.app.debug(TAG, "Map not loaded: " + fullMapPath);
			return;
		}

		collisionLayer = currentMap.getLayers().get(COLLISION_LAYER);
		if (collisionLayer == null)
		{
			Gdx.app.debug(TAG, "No collision layer");
		}

		portalLayer = currentMap.getLayers().get(PORTAL_LAYER);
		if (portalLayer == null)
		{
			Gdx.app.debug(TAG, "No portal layer");
		}
		else
		{
			this.portals = new Array<Portal>();

			MapObjects mapObjects = portalLayer.getObjects();
			for (int i = 0; i < mapObjects.getCount(); i++)
			{
				Portal portal = new Portal(mapObjects.get(i));
				portals.add(portal);
			}
		}
		spawnsLayer = currentMap.getLayers().get(SPAWNS_LAYER);
		if (spawnsLayer == null)
		{
			Gdx.app.debug(TAG, "No spawns layer");
		}

		backgroundLayers = (MapGroupLayer) currentMap.getLayers().get(BACKGROUND_LAYERS);
		if (backgroundLayers == null)
		{
			Gdx.app.debug(TAG, "Background layers not found");
		}

		foregroundLayers = (MapGroupLayer) currentMap.getLayers().get(FOREGROUND_LAYERS);
		if (foregroundLayers == null)
		{
			Gdx.app.debug(TAG, "Foreground layers not found");
		}
		
		this.mapWidth = currentMap.getProperties().get("width", Integer.class);
		this.mapHeight = currentMap.getProperties().get("height", Integer.class);
		
		Gdx.app.debug(TAG, "mapWidth: " + mapWidth);
		Gdx.app.debug(TAG, "mapHeight: " + mapHeight);

		//this.npcStartPositions = getNPCStartPositions();
		
		Gdx.app.debug(TAG, "Now deserializing mapConfig: " + mapType.getMapJsonPath());
		
		this.mapConfig = json.fromJson(MapConfig.class, Gdx.files.internal(mapType.getMapJsonPath()).read());
		
		Gdx.app.debug(TAG, "\t\tgeneral player start in config: (" +
				mapConfig.getGeneralPlayerStart().x +
				", " +
				mapConfig.getGeneralPlayerStart().y + ")");
		
		initializePlayerLocation(getPositionNormalized(mapConfig.getGeneralPlayerStart()));
		
		Array<MapEntity> npcs = mapConfig.getNpcs();
		for(int i = 0; i < npcs.size; i++)
		{
			EntityName name = npcs.get(i).getNpc();
			
			Gdx.app.debug(TAG, "now creating entity " + name);
			
			Entity e = EntityFactory.getInstance().createNPCEntityByName(name);
			
			Vector2 npcStart = mapConfig.getNpcs().get(i).getNpcStart();
			Vector2 normalizedStart = getPositionNormalized(npcStart);
			e.getComponent(Position.class).startingPosition = normalizedStart;
			e.getComponent(Position.class).resetAllToStarting();
			
			mapEntities.add(e);
		}
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Updates
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	protected void initializeMapEntities(MapManager mapManager, Batch batch)
	{
		for (int i = 0; i < mapEntities.size; i++)
		{
			mapEntities.get(i).getComponent(Sprite.class).batch = batch;
			//mapEntities.get(i).getComponent(HomeMap.class).mapManager = mapManager;
			
			mapEntities.get(i).getWorld().getSystem(CollisionSystem.class).initBoundingBox(mapEntities.get(i).getId());
			
			mapEntities.get(i).edit().add(new ActiveTag());
		}
	}
	
	public void initializePlayerLocation(Vector2 startLocation)
	{
		//TiledMapTileLayer layer = getCollisionLayer();
		//Cell startCell = layer.getCell((int) startLocation.x, (int) startLocation.y);
		
		//Vector2 adjustedStart = new Vector2(startLocation.x, (mapHeight - startLocation.y));
		//playerStart = adjustedStart;
		
		playerStart = startLocation;
	}
	
	protected void addNewMapEntity(MapManager mapManager, Batch batch, Entity entity)
	{
		entity.getComponent(Sprite.class).batch = batch;
		//entity.getComponent(HomeMap.class).mapManager = mapManager;
		
		entity.getWorld().getSystem(CollisionSystem.class).initBoundingBox(entity.getId());
		
		entity.edit().add(new ActiveTag());
		
		Gdx.app.debug(TAG, "Map entity added at point: (" + entity.getComponent(Position.class).cellX + ", " + entity.getComponent(Position.class).cellY + ")");
		Gdx.app.debug(TAG, "Map entity boundingBox at: (" + entity.getComponent(BoundingBox.class).xPos + ", " + entity.getComponent(BoundingBox.class).yPos + ")");
		
		mapEntities.add(entity);
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Map & layer getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public TiledMap getCurrentTiledMap()
	{
		return currentMap;
	}

	public MapType getCurrentMapType()
	{
		return currentMapType;
	}

	public MapLayer getPortalLayer()
	{
		return portalLayer;
	}
	
	public MapLayer getSpawnsLayer()
	{
		MapLayer tileSpawnsLayer = (MapLayer) spawnsLayer;
		return tileSpawnsLayer;
	}

	public TiledMapTileLayer getCollisionLayer()
	{
		TiledMapTileLayer tileCollisionLayer = (TiledMapTileLayer) collisionLayer;
		return tileCollisionLayer;
	}

	public MapGroupLayer getBackground()
	{
		return backgroundLayers;
	}

	public MapGroupLayer getForeground()
	{
		return foregroundLayers;
	}

	public Array<Portal> getPortals()
	{
		return this.portals;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Player-specific getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Vector2 getPlayerStart()
	{
		//Vector2 scaledPlayerStart = playerStart.cpy();
		//Vector2 scaledPlayerStart = calculate
		//playerStart.set(playerStart.x * UNIT_SCALE, playerStart.y * UNIT_SCALE);
		
		Gdx.app.debug(TAG, "getPlayerStart() called, currently returning: (" + playerStart.x + ", " + playerStart.y + ")");
		//Gdx.app.debug(TAG, "Scaled player start: (" + playerStart.x + ", " + playerStart.y + ")");
		return playerStart;
	}
	
	public Vector2 getPositionNormalized(Vector2 original)
	{
		Vector2 posWithFlippedY = new Vector2(original.x, this.mapHeight - 1 - original.y);
		return posWithFlippedY;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - NPC-specific getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public Array<Entity> getMapEntities()
	{
		return mapEntities;
	}
}
