package game;

import java.util.Hashtable;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import configs.CharConfig;
import configs.StrangePlaceConfigs.ConfigPath;
import ecs.components.*;
import ecs.components.DynamicAnimation.Animation;
import ecs.components.player.*;
import utility.AssetHandler;
import utility.CharConfigSystem;
import utility.CropConfigSystem;

/**
 * The EntityFactory class that instantiates and returns the Entity objects
 * specified by the EntityType enum passed into the static factory method
 * getEntity().
 */
public class EntityFactory
{
	//private static final String TAG = EntityFactory.class.getSimpleName();
	
	protected ComponentMapper<ConfigFile> mConfig;
	protected ComponentMapper<Sprite> mSprite;
	protected ComponentMapper<Position> mPosition;
	protected ComponentMapper<DynamicAnimation> mAnimation;

	public static World world;

	private static EntityFactory instance = null;
	private Hashtable<String, String> entities;
	
	public static EntityFactory getInstance()
	{
		if (instance == null)
		{
			instance = new EntityFactory();
		}
		return instance;
	}

	public static enum EntityType
	{
		PLAYER(new ArchetypeBuilder()
				.add(ActiveTag.class)
				.add(BoundingBox.class)
				.add(ConfigFile.class)
				.add(MovementDirection.class)
				//.add(HomeMap.class)
				.add(KeyboardInput.class)
				.add(DynamicAnimation.class)
				.add(MovementState.class)
				.add(PlayerCamera.class)
				.add(PlayerTag.class)
				.add(Position.class)
				.add(Sprite.class)
				.add(Velocity.class)
				.build(world)),

		NPC(new ArchetypeBuilder()
				.add(BoundingBox.class)
				.add(ConfigFile.class)
				.add(MovementDirection.class)
				.add(NPCTag.class)
				.add(KeyboardInput.class)
				.add(Position.class)
				.add(Velocity.class)
				.add(MovementState.class)
				.add(Sprite.class)
				.add(DynamicAnimation.class)
				//.add(HomeMap.class)
				.build(world)),
		
		PLANT(new ArchetypeBuilder()
				.add(PlantTag.class)
				.add(Sprite.class)
				.add(ConfigFile.class)
				.add(Position.class)
				.add(BoundingBox.class)
				.add(GrowthState.class)
				.add(StaticAnimation.class)
				.add(Species.class)
				//.add(HomeMap.class)
				.build(world));

		private Archetype archetype;

		private EntityType(Archetype setArchetype)
		{
			this.archetype = setArchetype;
		}
	}

	public static enum EntityName
	{
		SYLVANA, BIANCA, KENNETH, PUNK, RUBY, ALDER, JOI
	}

	public static String PLAYER_CONFIG = "scripts/player.json";
	public static String BIANCA_CONFIG = "scripts/bianca.json";
	public static String KENNETH_CONFIG = "scripts/kenneth.json";
	public static String PUNK_CONFIG = "scripts/punk.json";
	public static String RUBY_CONFIG = "scripts/ruby.json";
	public static String ALDER_CONFIG = "scripts/alder.json";
	public static String JOI_CONFIG = "scripts/joi.json";
	
	//public static String BERRY_TREE_CONFIG = "scripts/berry_trees.json";

	private EntityFactory()
	{
		entities = new Hashtable<String, String>();

		entities.put(EntityName.SYLVANA.name(), PLAYER_CONFIG);
		entities.put(EntityName.ALDER.name(), ALDER_CONFIG);
		entities.put(EntityName.BIANCA.name(), BIANCA_CONFIG);
		entities.put(EntityName.KENNETH.name(), KENNETH_CONFIG);
		entities.put(EntityName.PUNK.name(), PUNK_CONFIG);
		entities.put(EntityName.RUBY.name(), RUBY_CONFIG);
		entities.put(EntityName.JOI.name(), JOI_CONFIG);
	}

	public static Entity getEntityByType(EntityType entityType)
	{
		Entity entity = null;

		switch (entityType)
		{
		case PLAYER:
			entity = world.createEntity(EntityType.PLAYER.archetype);
			entity.getComponent(ConfigFile.class).fileName = ConfigPath.PLAYER_CONFIG;
			//CharacterInitializer.getInstance().initialize(entity.getId());
			world.getSystem(CharConfigSystem.class).initialize(entity.getId());
			return entity;

		case NPC:
			entity = world.createEntity(EntityType.NPC.archetype);
			return entity;
			
		case PLANT:
			entity = world.createEntity(EntityType.PLANT.archetype);
			entity.getComponent(ConfigFile.class).fileName = ConfigPath.BERRY_TREES_CONFIG;
			return entity;

		default:
			return null;
		}
	}

	public static void setWorld(World setWorld)
	{
		world = setWorld;
	}

	public Entity createNPCEntityByName(EntityName entityName)
	{
		String config = entities.get(entityName.toString());
		Entity entity = world.createEntity(EntityType.NPC.archetype);
		
		entity.getComponent(ConfigFile.class).fileName = config;
		//CharacterInitializer.getInstance().initialize(entity.getId());
		
		world.getSystem(CharConfigSystem.class).initialize(entity.getId());
		return entity;
	}
	
	public Entity createPlantEntity(Species.CropSpecies type)
	{
		Entity entity = world.createEntity(EntityType.PLANT.archetype);
		//world.getSystem(CropConfigSystem.class).initialize(entity.getId(), type);
		world.getSystem(CropConfigSystem.class).initialize(entity.getId(), type);
		return entity;
	}
	
	public CharConfig deserializeEntityConfig(String configFilePath)
	{
		Json tempJson = new Json();
		return tempJson.fromJson(CharConfig.class, Gdx.files.internal(configFilePath).read());
	}
	
	/*
	private void loadAnimations(int entityId, CharConfig entityConfig)
	{
		MovementAnim animation = mAnimation.get(entityId);
		
		Array<AnimationConfig> animationConfigs = entityConfig.getAnimationConfig();

		for (AnimationConfig animationConfig : animationConfigs)
		{
			Array<String> textureNames = animationConfig.getTexturePaths();
			Array<GridPoint2> points = animationConfig.getGridPoints();
			AnimationType animationType = animationConfig.getAnimationType();

			Animation currentAnim = null;

			if (textureNames.size == 1)
			{
				// animation = loadAnimation(textureNames.get(0), points, frameDuration);
				currentAnim = loadSingleAnimation(entityId, textureNames.get(0), points);
			}
			
			animation.animations.put(animationType, currentAnim);
		}
	}
	*/

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
}
