package game;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Vector2;

import ecs.components.Position;
import ecs.components.Sprite;
import ecs.components.player.KeyboardInput;
import ecs.components.player.PlayerCamera;
import ecs.systems.CharAnimSystem;
import ecs.systems.CameraSystem;
import ecs.systems.CollisionSystem;
import ecs.systems.InteractionSystem;
import ecs.systems.MovementSystem;
import ecs.systems.EntitySortSystem;
import ecs.systems.NPCInputSystem;
import ecs.systems.CropAnimSystem;
import ecs.systems.CropGrowthSystem;
import ecs.systems.PlayerInputSystem;
import ecs.systems.PortalSystem;
import ecs.systems.RenderSystem;
import game.EntityFactory.EntityType;
import maps.Map;
import saves.ProfileManager;
import maps.MapManager;
import ui.PlayerHUD;
import utility.CharConfigSystem;
import utility.CropConfigSystem;

public class MainGameScreen implements Screen
{
	private static final String TAG = MainGameScreen.class.getSimpleName();

	private static class VIEWPORT
	{
		static float viewportWidth;
		static float viewportHeight;
		static float virtualWidth;
		static float virtualHeight;
		static float physicalWidth;
		static float physicalHeight;
		static float aspectRatio;
	}

	public static enum GameState
	{
		SAVING, LOADING, RUNNING, PAUSED, GAME_OVER
	}

	private StrangePlace game;
	private static GameState gameState;
	
	private World world;

	private OrthogonalTiledMapRenderer mapRenderer = null;
	private MapManager mapManager;
	private OrthographicCamera camera = null;
	private OrthographicCamera hudCamera = null;

	private InputMultiplexer multiplexer;

	private Entity player;
	private static PlayerHUD playerHUD;

	public MainGameScreen(StrangePlace currentGame)
	{
		this.game = currentGame;
		mapManager = new MapManager();

		// camera setup
		//setupViewport(16, 9);
		setupViewport(4, 3);
		this.camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);
		camera.zoom = 6f;
		
		establishWorld();
		player.getComponent(PlayerCamera.class).playerCamera = this.camera;

		mapManager.setPlayer(player);
		mapManager.setCamera(camera);

		hudCamera = new OrthographicCamera();
		hudCamera.setToOrtho(false, VIEWPORT.physicalWidth, VIEWPORT.physicalHeight);
		hudCamera.zoom = 1f;
		//hudCamera.zoom = 0.5f;
		
		playerHUD = new PlayerHUD(hudCamera, player, mapManager);

		InputProcessor playerInput = world.getEntity(player.getId()).getComponent(KeyboardInput.class);

		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(playerHUD.getStage());

		multiplexer.addProcessor(playerInput);

		Gdx.input.setInputProcessor(multiplexer);
		
		ProfileManager.getInstance().addObserver(mapManager);
	}
	
	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(multiplexer);
		
		ProfileManager.getInstance().addObserver(mapManager);
		ProfileManager.getInstance().addObserver(playerHUD);
		
		setGameState(GameState.LOADING);
		
		world.getSystem(InteractionSystem.class).addPlayerHUDObserver(playerHUD);
		
		mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getCurrentTiledMap(), Map.UNIT_SCALE);
		Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());
		
		Vector2 playerStart = mapManager.getCurrentMap().getPlayerStart();

		player.getComponent(Position.class).setCurrentPosition(playerStart);
		player.getComponent(Sprite.class).batch = mapRenderer.getBatch();

		mapManager.initializeMapEntities(mapManager, mapRenderer.getBatch());
		
		world.getSystem(CameraSystem.class).updateMapConstraints(player.getId());
	}

	@Override
	public void render(float delta)
	{
		if(gameState == GameState.GAME_OVER)
		{
			//game.setScreen(game.getScreenType(StrangePlace.ScreenType.GameOver));
		}
		
		if(gameState == GameState.PAUSED)
		{
			//player.updateInput(delta);
			playerHUD.render(delta);
			return;
		}
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapRenderer.setView(camera);

		mapRenderer.getBatch().setProjectionMatrix(camera.combined);
		
		AnimatedTiledMapTile.updateAnimationBaseTime();
		mapManager.renderBackground(mapRenderer, mapRenderer.getBatch());
		
		// 12/2/19-- THIS WAS THE BLOCK OF CODE THAT I NEEDED TO BE ABLE TO SHOW THE
		// CONVERSATIONUI!!!!!!
		// The EntityObservers weren't updating without it, and that was where the
		// problem was!!!
		if (mapManager.hasMapChanged())
		{
			mapRenderer.setMap(mapManager.getCurrentTiledMap());
			mapManager.initializeMapEntities(mapManager, mapRenderer.getBatch());
			
			world.getSystem(CameraSystem.class).updateMapConstraints(player.getId());

			playerHUD.updateEntityObservers();

			mapManager.setMapChanged(false);
		}
		
		world.setDelta(delta);
		world.process();

		mapManager.renderForeground(mapRenderer, mapRenderer.getBatch());

		playerHUD.render(delta);
	}

	@Override
	public void hide()
	{
		if(gameState != GameState.GAME_OVER )
		{
			setGameState(GameState.SAVING);
		}
		
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
		setGameState(GameState.SAVING);
		playerHUD.pause();
	}

	@Override
	public void resume()
	{
		setGameState(GameState.LOADING);
		playerHUD.resume();
	}

	@Override
	public void dispose()
	{
		if (player != null)
		{
			player.deleteFromWorld();
		}
		if (mapRenderer != null)
			mapRenderer.dispose();
	}
	
	public static void setGameState(GameState gameState)
	{
		switch(gameState)
		{
		case RUNNING:
			gameState = GameState.RUNNING;
			break;
			
		case LOADING:
			ProfileManager.getInstance().loadProfile();
			gameState = GameState.RUNNING;
			break;
			
		case SAVING:
			ProfileManager.getInstance().saveProfile();
			gameState = GameState.PAUSED;
			break;
			
		case PAUSED:
			if(gameState == GameState.PAUSED)
			{
				gameState = GameState.RUNNING;
			}
			else if(gameState == GameState.RUNNING)
			{
				gameState = GameState.PAUSED;
			}
			break;
			
		case GAME_OVER:
			gameState = GameState.GAME_OVER;
			break;
			
		default:
			gameState = GameState.RUNNING;
			break;
		}

	}

	private void setupViewport(int width, int height)
	{
		// Make the viewport a percentage of the total display area
		VIEWPORT.virtualWidth = width;
		VIEWPORT.virtualHeight = height;

		// Current viewport dimensions
		VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
		VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

		// Pixel dimensions of display
		VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
		VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

		// Aspect ratio for current viewport
		VIEWPORT.aspectRatio = (VIEWPORT.virtualWidth / VIEWPORT.virtualHeight);

		// Update viewport if there could be skewing??
		if (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight >= VIEWPORT.aspectRatio) {
			// Letterbox left and right
			VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth / VIEWPORT.physicalHeight);
			VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
		} else {
			// Letterbox above and below
			VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
			VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight / VIEWPORT.physicalWidth);
		}

		Gdx.app.debug(TAG, "WorldRenderer, virtual: (" + VIEWPORT.virtualWidth + ", " + VIEWPORT.virtualHeight + ")");
		Gdx.app.debug(TAG,
				"WorldRenderer, viewport: (" + VIEWPORT.viewportWidth + ", " + VIEWPORT.viewportHeight + ")");
		Gdx.app.debug(TAG,
				"WorldRenderer, physical: (" + VIEWPORT.physicalWidth + ", " + VIEWPORT.physicalHeight + ")");
	}

	private void establishWorld()
	{
		// 1/26/20 Artemis addition
		WorldConfiguration config = new WorldConfiguration();
		
		config.setSystem(new CharConfigSystem());
		config.setSystem(new CropConfigSystem());
		config.setSystem(new PlayerInputSystem());
		config.setSystem(new NPCInputSystem());
		config.setSystem(new CharAnimSystem());
		config.setSystem(new CropAnimSystem());
		config.setSystem(new CameraSystem(mapManager));
		config.setSystem(new MovementSystem());
		config.setSystem(new CollisionSystem(mapManager));
		config.setSystem(new InteractionSystem());
		config.setSystem(new PortalSystem(mapManager));
		config.setSystem(new EntitySortSystem());
		config.setSystem(new RenderSystem(camera));
		config.setSystem(new CropGrowthSystem());

		this.world = new World(config);
		EntityFactory.setWorld(world);

		this.player = EntityFactory.getEntityByType(EntityType.PLAYER);
	}
	
	public World getWorld()
	{
		return this.world;
	}

}
