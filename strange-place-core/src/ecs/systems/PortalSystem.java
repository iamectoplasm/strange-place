package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import ecs.components.BoundingBox;
import ecs.components.MovementState;
import ecs.components.Position;
import ecs.components.player.PlayerTag;
import maps.Map;
import maps.MapFactory;
import maps.MapManager;
import maps.Portal;
import observers.PortalObserver;

public class PortalSystem extends IntervalIteratingSystem implements PortalObserver
{
	public static final String TAG = PortalSystem.class.getSimpleName();

	protected ComponentMapper<PlayerTag> playerTag;
	
	protected ComponentMapper<Position> position;
	protected ComponentMapper<MovementState> state;
	protected ComponentMapper<BoundingBox> box;
	//protected ComponentMapper<HomeMap> location;
	
	private boolean portalActivated = false;
	private String newMapName = "";
	private String newMapSpawnName = "";
	
	MapManager worldMapManager;
	
	public PortalSystem(MapManager mapManager)
	{
		super(Aspect.all(Position.class,
						 MovementState.class,
						 BoundingBox.class,
						 //HomeMap.class,
						 PlayerTag.class),
				(1/20f));
		
		this.worldMapManager = mapManager;
	}
	
	public void registerPortals()
	{
		
	}
	
	@Override
	protected void process(int entityId)
	{
		if(portalActivated)
		{
			//MapManager mapManager = location.get(entityId).mapManager;
			
			Gdx.app.debug(TAG, "Map being loaded: " + newMapName);
			Gdx.app.debug(TAG, "Should be spawning at spawn point: " + newMapSpawnName);

			//mapManager.loadMap(MapFactory.MapType.valueOf(currentActivePortal));
			worldMapManager.loadMap(MapFactory.MapType.valueOf(newMapName));
	
			RectangleMapObject spawnPoint = (RectangleMapObject) worldMapManager.getSpawnsLayer().getObjects().get(newMapSpawnName);
			
			Gdx.app.debug(TAG, "spawnPoint x: " + spawnPoint.getRectangle().getX() * Map.UNIT_SCALE + " spawnPoint y: " + spawnPoint.getRectangle().getY() * Map.UNIT_SCALE);
			
			// Get center of rectangle
			int x = (int) ((int) spawnPoint.getRectangle().getX() * Map.UNIT_SCALE);
			int y = (int) ((int) spawnPoint.getRectangle().getY() * Map.UNIT_SCALE);
			
			position.get(entityId).startingPosition = new Vector2(x, y);
			position.get(entityId).resetAllToStarting();
			
			//Gdx.app.debug(TAG, "PortalSystem() finished calling mapManager.setClosestStartPositionFromScaledUnits()");
			portalActivated = false;
		}
		else
		{
			//MapManager mapManager = location.get(entityId).mapManager;
			//Array<Portal> portals = mapManager.getCurrentMap().getPortals();
			Array<Portal> portals = worldMapManager.getCurrentMap().getPortals();

			for (int i = 0; i < portals.size; i++)
			{
				Portal portal = portals.get(i);
				portal.checkPortalActivation(box.get(entityId).boundingBox);
			}
		}
	}

	@Override
	public void onPortalObserverNotify(String newMap, String spawnName, PortalEvent event)
	{
		if(event == PortalEvent.PORTAL_TRIGGERED)
		{
			Gdx.app.debug(TAG, "Message sent from Portal: PORTAL_TRIGGERED");
			
			portalActivated = true;
			newMapName = newMap;
			newMapSpawnName = spawnName;
		}
	}
}
