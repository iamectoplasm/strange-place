package utility;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import configs.CropDeserializer;
import configs.CropConfig.AnimationConfig;
import configs.CropConfig.Crop;
import ecs.components.BoundingBox;
import ecs.components.ConfigFile;
import ecs.components.GrowthState;
import ecs.components.Position;
import ecs.components.Species;
import ecs.components.Sprite;
import ecs.components.StaticAnimation;
import ecs.components.GrowthState.GrowthStage;

public class CropConfigSystem extends IteratingSystem
{
	public static final String TAG = CropConfigSystem.class.getSimpleName();

	protected ComponentMapper<ConfigFile> mConfig;
	protected ComponentMapper<Species> mSpecies;
	protected ComponentMapper<Sprite> mSprite;
	protected ComponentMapper<Position> mPosition;
	protected ComponentMapper<StaticAnimation> mAnimation;
	protected ComponentMapper<GrowthState> mGrowthState;
	protected ComponentMapper<BoundingBox> mBoundingBox;

	public CropConfigSystem()
	{
		super(Aspect.all(ConfigFile.class,
				Species.class,
				StaticAnimation.class,
				Sprite.class,
				Position.class));
		
		Gdx.app.debug(TAG, "CropConfigV2 object constructed");
	}
	
	public void initialize(int entityId, Species.CropSpecies plantType)
	{
		ConfigFile config = mConfig.get(entityId);
		Sprite sprite = mSprite.get(entityId);
		Species species = mSpecies.get(entityId);
		Position position = mPosition.get(entityId);
		StaticAnimation plantAnimation = mAnimation.get(entityId);
		GrowthState growthState = mGrowthState.get(entityId);
		//BoundingBox boundingBox = mBoundingBox.get(entityId);
		
		// First figure out the file path to find the associated file at
		//String configPath = config.fileName;
		//Gdx.app.debug(TAG, "configFilePath of plant " + entityId + ": " + configPath);
		
		config.cropConfig = CropDeserializer.getInstance().getCropConfig();
		
		// De-serialize the config json, store all info into a new POJO BerryTree object
		Crop newCrop = CropDeserializer.getInstance().getCropBySpecies(plantType);
		//BerryTree newTree = CropDeserializer.getInstance().getTreeBySpecies(plantType);
		
		// Set entity's name from config file
		//species.type = plantType;
		
		// Load their animations
		loadAnimations(entityId, newCrop);
		
		// Then initialize their sprite so that its not null
		//sprite.currentFrame = plantAnimation.animations.get(GrowthStage.SAPLING).getKeyFrame(0f);
		sprite.currentFrame = plantAnimation.animations.get(GrowthStage.SEED).getKeyFrame(0f);
		sprite.drawWidth = 1;
		sprite.drawHeight = 2;
		
		//growthState.currentGrowthStage = GrowthStage.SAPLING;
		growthState.currentGrowthStage = GrowthStage.SEED;
		
		position.xOffset = 0;
		position.yOffset = 0;
		
		species.produce = newCrop.getProduce();
	}
	
	private void loadAnimations(int entityId, Crop crop)
	{
		ConfigFile cropConfig = mConfig.get(entityId);
		StaticAnimation animation = mAnimation.get(entityId);
		
		Array<AnimationConfig> universalAnimationConfigs = cropConfig.cropConfig.getUniversalCropAssets().getAnimationConfig();
		
		Gdx.app.debug(TAG, "universalAnimationConfigs count = " + universalAnimationConfigs.size);
		
		Array<AnimationConfig> cropAnimationConfigs = crop.getAnimationConfig();
		Gdx.app.debug(TAG, "cropAnimationConfigs count = " + cropAnimationConfigs.size);

		for (AnimationConfig universalAnimationConfig : universalAnimationConfigs)
		{
			Gdx.app.debug(TAG, "Now in universal animation loading for loop, loading animation for " + universalAnimationConfig.getGrowthStage());
			
			String textureName = cropConfig.cropConfig.getUniversalCropAssets().getAssetSpecification().getTexturePath();
			Array<GridPoint2> points = universalAnimationConfig.getGridPoints();
			GrowthState.GrowthStage stage = universalAnimationConfig.getGrowthStage();
			
			if(stage == GrowthState.GrowthStage.PLANTABLE_SOIL)
			{
				animation.dirt = getSoilSprite(entityId, textureName, points);
			}
			else
			{
				Animation<TextureRegion> currentAnim = null;
			
				currentAnim = loadSingleAnimation(entityId, textureName, points);
				currentAnim.setPlayMode(PlayMode.LOOP);
				
				Gdx.app.debug(TAG, "Now loading universal animation stage: " + stage.name());
			
				animation.animations.put(stage, currentAnim);
			}
		}
		
		for(AnimationConfig animationConfig: cropAnimationConfigs)
		{
			Gdx.app.debug(TAG, "Now in animation loading for loop, loading animation for " + animationConfig.getGrowthStage());
			
			String textureName = cropConfig.cropConfig.getCropAssets().getAssetSpecification().getTexturePath();
			Array<GridPoint2> points = animationConfig.getGridPoints();
			GrowthState.GrowthStage stage = animationConfig.getGrowthStage();

			Animation<TextureRegion> currentAnim = null;
			
			currentAnim = loadSingleAnimation(entityId, textureName, points);
			currentAnim.setPlayMode(PlayMode.LOOP);
				
			Gdx.app.debug(TAG, "Now loading crop animation stage: " + stage.name());
			
			animation.animations.put(stage, currentAnim);
		}
	}
	
	protected Animation<TextureRegion> loadSingleAnimation(int entityId, String textureName, Array<GridPoint2> points)
	{
		StaticAnimation animation = mAnimation.get(entityId);
		
		AssetHandler.loadTextureAsset(textureName);
		Texture texture = AssetHandler.getTextureAsset(textureName);

		TextureRegion[][] textureFrames = TextureRegion.split(texture, animation.frameWidth, animation.frameHeight);
		
		
		//for(int i = 0; i < points.size; i++)
		//{
		//	Gdx.app.debug(TAG, "texture points: (" + points.get(i).x + ", " + points.get(i).y + ")");
		//}
		
		Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(points.size);

		for (GridPoint2 point : points)
		{
			animationKeyFrames.add(textureFrames[point.x][point.y]);
		}

		return new Animation<TextureRegion>(animation.frameDuration, animationKeyFrames);
	}
	
	public TextureRegion getSoilSprite(int entityId, String textureName, Array<GridPoint2> points)
	{
		StaticAnimation animation = mAnimation.get(entityId);
		
		AssetHandler.loadTextureAsset(textureName);
		Texture texture = AssetHandler.getTextureAsset(textureName);

		TextureRegion[][] textureFrames = TextureRegion.split(texture, animation.frameWidth, animation.frameHeight);
		
		Array<TextureRegion> animationKeyFrames = new Array<TextureRegion>(points.size);

		for (GridPoint2 point : points)
		{
			animationKeyFrames.add(textureFrames[point.x][point.y]);
		}

		return animationKeyFrames.first();
	}

	@Override
	protected void process(int entityId)
	{
		Gdx.app.debug(TAG, "processing");
		this.setEnabled(false);
		Gdx.app.debug(TAG, "CropConfigSystemV2 is now disabled");
	}

}
