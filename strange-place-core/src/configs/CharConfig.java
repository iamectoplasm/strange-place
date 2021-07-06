package configs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import ecs.components.DynamicAnimation;
import ecs.components.MovementDirection;
import ecs.components.MovementState;
import ecs.components.DynamicAnimation.DynAnimationType;
import inventory.items.InventoryItem.ItemTypeID;

/**
 * The EntityConfig class is a POJO class that deserializes an entity's JSON
 * file
 * 
 * @Updated 12/1/19
 */
public class CharConfig
{
	private static final String TAG = CharConfig.class.getSimpleName();
	
	private Array<AnimationConfig> animationConfig;
	private Array<InventoryConfig> inventory;
	//private Array<ItemTypeID> inventory;
	//private Array<Integer> itemCounts;
	
	private MovementState.State state;
	private MovementDirection.Direction direction;
	
	private String name;
	private String conversationConfigPath;
	private int money;

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - EntityConfig constructors
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	/**
	 * Creates an EntityConfig object
	 * 
	 * @Updated 12/1/19
	 */
	public CharConfig()
	{
		Gdx.app.debug(TAG, "\t\tCharConfig object constructed");
		
		animationConfig = new Array<AnimationConfig>();
		inventory = new Array<InventoryConfig>();
		//inventory = new Array<ItemTypeID>();
		//itemCounts = new Array<Integer>();
	}

	/**
	 * Creates an EntityConfig object
	 * 
	 * @param config
	 */
	public CharConfig(CharConfig config)
	{
		state = config.getState();
		direction = config.getDirection();
		name = config.getEntityName();
		
		conversationConfigPath = config.getConversationConfigPath();

		animationConfig = new Array<AnimationConfig>();
		animationConfig.addAll(config.getAnimationConfig());

		//inventory = new Array<ItemTypeID>();
		//inventory.addAll(config.getInventory());
		
		//itemCounts = new Array<Integer>();
		//itemCounts.addAll(config.getItemCounts());
		inventory = new Array<InventoryConfig>();
		inventory.addAll(config.getInventoryConfigs());
		
		money = config.getMoney();

		// Gdx.app.debug(TAG, "EntityID: " + entityID.toString());
		// Gdx.app.debug(TAG, "ConversationConfigPath: " +
		// conversationConfigPath.toString());
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - AnimationConfig nested class
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	static public class AnimationConfig
	{
		private DynamicAnimation.DynAnimationType animationType;
		private Array<String> texturePath;
		private Array<GridPoint2> gridPoints;

		/*
		 * = = = = = = = = = = = = = = = = = = = =
		 * 
		 * - AnimationConfig constructor
		 * 
		 * = = = = = = = = = = = = = = = = = = = =
		 */
		public AnimationConfig()
		{
			animationType = DynAnimationType.WALK_DOWN;
			texturePath = new Array<String>();
			gridPoints = new Array<GridPoint2>();
		}

		/*
		 * = = = = = = = = = = = = = = = = = = = =
		 * 
		 * - AnimationConfig getters
		 * 
		 * = = = = = = = = = = = = = = = = = = = =
		 */
		public Array<String> getTexturePaths()
		{
			return texturePath;
		}

		public Array<GridPoint2> getGridPoints()
		{
			return gridPoints;
		}

		public DynamicAnimation.DynAnimationType getAnimationType()
		{
			return animationType;
		}

		/*
		 * = = = = = = = = = = = = = = = = = = = =
		 * 
		 * - AnimationConfig setters
		 * 
		 * = = = = = = = = = = = = = = = = = = = =
		 */
		public void setTexturePaths(Array<String> texturePaths)
		{
			this.texturePath = texturePaths;
		}

		public void setGridPoints(Array<GridPoint2> gridPoints)
		{
			this.gridPoints = gridPoints;
		}

		public void setAnimationType(DynAnimationType animationType)
		{
			this.animationType = animationType;
		}
	}
	
	static public class InventoryConfig
	{
		ItemTypeID itemID;
		int itemCount;
		
		public InventoryConfig()
		{
			itemID = ItemTypeID.none;
			itemCount = 0;
		}

		public ItemTypeID getItemID() {
			return itemID;
		}

		public int getItemCount() {
			return itemCount;
		}
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - EntityConfig getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public String getEntityName()
	{
		return name;
	}

	public MovementDirection.Direction getDirection()
	{
		return direction;
	}

	public MovementState.State getState()
	{
		return state;
	}

	public Array<AnimationConfig> getAnimationConfig()
	{
		return animationConfig;
	}

	public String getConversationConfigPath()
	{
		return conversationConfigPath;
	}

	public Array<InventoryConfig> getInventoryConfigs()
	{
		return inventory;
	}
	
	public Array<ItemTypeID> getInventoryItems()
	{
		Array<ItemTypeID> items = new Array<ItemTypeID>();
		for(int i = 0; i < inventory.size; i++)
		{
			items.add(inventory.get(i).getItemID());
		}
		
		return items;
	}
	
	public int getMoney()
	{
		return money;
	}
	
	public void addAnimationConfig(AnimationConfig animationConfig)
	{
		this.animationConfig.add(animationConfig);
	}
}
