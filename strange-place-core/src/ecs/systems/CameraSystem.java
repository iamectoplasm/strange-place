package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import ecs.components.Position;
import ecs.components.player.PlayerCamera;
import maps.MapManager;

public class CameraSystem extends IntervalIteratingSystem
{
	ComponentMapper<PlayerCamera> camera;
	ComponentMapper<Position> position;
	
	private int mapWidth;
	private int mapHeight;
	
	private float cameraWidth;
	private float cameraHeight;
	
	MapManager worldMapManager;

	public CameraSystem(MapManager mapManager)
	{
		//super(Aspect.all(PlayerCamera.class, Position.class, HomeMap.class), (1/60f));
		super(Aspect.all(PlayerCamera.class, Position.class), (1/60f));
		
		this.worldMapManager = mapManager;
	}

	@Override
	protected void process(int entityId)
	{
		// Move camera after player as normal
		camera.get(entityId).playerCamera.position.set(position.get(entityId).currentPosition, 0f);
		
		 camera.get(entityId).playerCamera.position.x = MathUtils.clamp(camera.get(entityId).playerCamera.position.x,
				 cameraWidth,
				 mapWidth - cameraWidth);
				 //mapWidth - cameraWidth);
		 camera.get(entityId).playerCamera.position.y = MathUtils.clamp(camera.get(entityId).playerCamera.position.y,
				 cameraHeight,
				 mapHeight - cameraHeight);
				 //mapHeight - cameraHeight);

		camera.get(entityId).playerCamera.update();
	}
	
	public void updateMapConstraints(int entityId)
	{
		//this.mapWidth = location.get(entityId).mapManager.getMapWidth();
		//this.mapHeight = location.get(entityId).mapManager.getMapHeight();
		this.mapWidth = worldMapManager.getMapWidth();
		this.mapHeight = worldMapManager.getMapHeight();

		this.cameraWidth = camera.get(entityId).playerCamera.viewportWidth * 3;
		this.cameraHeight = camera.get(entityId).playerCamera.viewportHeight * 3;
	}

}
