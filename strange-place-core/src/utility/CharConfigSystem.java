package utility;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import configs.CharConfig;
import configs.CharConfig.AnimationConfig;
import ecs.components.*;
import ecs.components.DynamicAnimation.Animation;
import ecs.components.DynamicAnimation.DynAnimationType;

public class CharConfigSystem extends IteratingSystem
{
	public static final String TAG = CharConfigSystem.class.getSimpleName();

	protected ComponentMapper<ConfigFile> mConfig;
	protected ComponentMapper<Sprite> mSprite;
	protected ComponentMapper<Position> mPosition;
	protected ComponentMapper<DynamicAnimation> mAnimation;

	public CharConfigSystem()
	{
		super(Aspect.all(ConfigFile.class,
						 DynamicAnimation.class,
						 Sprite.class,
						 Position.class));
		
	}

	public void initialize(int entityId)
	{
		ConfigFile config = mConfig.get(entityId);
		Sprite sprite = mSprite.get(entityId);
		DynamicAnimation animation = mAnimation.get(entityId);
		Position position = mPosition.get(entityId);
		
		// First figure out the file path to find the associated file at
		String configPath = config.fileName;
		//Gdx.app.debug(TAG, "configFilePath of entity " + entityId + ": " + configPath);
			
		// De-serialize the config json, store all info in the EntityConfig component
		config.charConfig = new CharConfig(deserializeEntityConfig(configPath));
		
		// Load their animations
		loadAnimations(entityId, config.charConfig);
		// Then initialize their sprite so that its not null
		sprite.currentFrame = animation.animations.get(DynAnimationType.WALK_DOWN).getFrameAtIndex(0);
		sprite.drawWidth = 2;
		sprite.drawHeight = 2;
			
		//TiledMapTileLayer spawnLayer = map.mapManager.getSpawnsLayer();
		//Vector2 startPosition = config.entityConfig.getStartPosition();
		//Cell destinationCell = spawnLayer.getCell((int) startPosition.x, (int) startPosition.y);
		
		position.xOffset = -(1/2f);
		position.yOffset = (1/4f);
		position.resetAllToStarting();
		//Gdx.app.debug(TAG, "startPosition of entity " + entityId + ": (" + position.cellX + ", " + position.cellY + ")");
	}

	@Override
	protected void begin()
	{ }

	@Override
	protected void process(int entityId)
	{
		Gdx.app.debug(TAG, "processing");
		this.setEnabled(false);
	}

	@Override
	protected void end()
	{ }

	private void loadAnimations(int entityId, CharConfig entityConfig)
	{
		DynamicAnimation animation = mAnimation.get(entityId);
		
		Array<AnimationConfig> animationConfigs = entityConfig.getAnimationConfig();

		for (AnimationConfig animationConfig : animationConfigs)
		{
			Array<String> textureNames = animationConfig.getTexturePaths();
			Array<GridPoint2> points = animationConfig.getGridPoints();
			DynAnimationType animationType = animationConfig.getAnimationType();

			Animation currentAnim = null;

			if (textureNames.size == 1)
			{
				// animation = loadAnimation(textureNames.get(0), points, frameDuration);
				currentAnim = loadSingleAnimation(entityId, textureNames.get(0), points);
			}
			
			animation.animations.put(animationType, currentAnim);
		}
	}

	protected Animation loadSingleAnimation(int entityId, String textureName, Array<GridPoint2> points)
	{
		DynamicAnimation animation = mAnimation.get(entityId);
		
		AssetHandler.loadTextureAsset(textureName);
		Texture texture = AssetHandler.getTextureAsset(textureName);

		TextureRegion[][] textureFrames = TextureRegion.split(texture, animation.frameWidth, animation.frameHeight);
	
		Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(points.size);

		for (GridPoint2 point : points)
		{
			animationKeyFrames.add(textureFrames[point.x][point.y]);
		}

		return new DynamicAnimation.Animation(animationKeyFrames, world.getEntity(entityId).getComponent(Velocity.class).velocity);
	}

	public CharConfig deserializeEntityConfig(String configFilePath)
	{
		Json tempJson = new Json();
		return tempJson.fromJson(CharConfig.class, Gdx.files.internal(configFilePath).read());
	}

}
