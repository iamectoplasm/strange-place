package configs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import ecs.components.GrowthState;
import ecs.components.Species;
import inventory.items.InventoryItem;

public class CropConfig
{
	private static final String TAG = CropConfig.class.getSimpleName();
	
	private UniversalCropAssets universalCropAssets;
	private CropAssets cropAssets;
	
	public CropConfig()
	{
		Gdx.app.debug(TAG, "\t\tCropConfigV2 object constructed");
		
		this.universalCropAssets = new UniversalCropAssets();
		this.cropAssets = new CropAssets();
	}
	
	public CropConfig(CropConfig cropConfig)
	{
		this.universalCropAssets = cropConfig.getUniversalCropAssets();
		this.cropAssets = cropConfig.getCropAssets();
	}
	
	public UniversalCropAssets getUniversalCropAssets()
	{
		return universalCropAssets;
	}
	
	public CropAssets getCropAssets()
	{
		return cropAssets;
	}
	
	public Array<Crop> getAllCrops()
	{
		return cropAssets.getCrops();
	}
	
	public static class UniversalCropAssets
	{
		private AssetSpecification assetSpecification;
		private Array<AnimationConfig> animationConfig;
		
		public UniversalCropAssets()
		{
			this.assetSpecification = new AssetSpecification();
			this.animationConfig = new Array<AnimationConfig>();
		}
		
		public AssetSpecification getAssetSpecification()
		{
			return assetSpecification;
		}

		public Array<AnimationConfig> getAnimationConfig()
		{
			return animationConfig;
		}
	}
	
	public static class CropAssets
	{
		private AssetSpecification assetSpecification;
		private Array<Crop> crops;
		
		public CropAssets()
		{
			this.crops = new Array<Crop>();
		}
		
		public AssetSpecification getAssetSpecification()
		{
			return assetSpecification;
		}
		
		public Array<Crop> getCrops()
		{
			return crops;
		}
	}
	
	public static class AssetSpecification
	{
		private String texturePath;
		private int spriteWidth;
		private int spriteHeight;
		
		public AssetSpecification()
		{
			texturePath = "";
			spriteWidth = 0;
			spriteHeight = 0;
		}
		
		public String getTexturePath()
		{
			return texturePath;
		}

		public int getSpriteWidth()
		{
			return spriteWidth;
		}

		public int getSpriteHeight()
		{
			return spriteHeight;
		}
	}
	
	public static class AnimationConfig
	{
		private GrowthState.GrowthStage growthStage;
		private Array<GridPoint2> gridPoints;
		
		public AnimationConfig()
		{
			this.growthStage = GrowthState.GrowthStage.PLANTABLE_SOIL;
			this.gridPoints = new Array<GridPoint2>();
		}
		
		public GrowthState.GrowthStage getGrowthStage()
		{
			return growthStage;
		}
		
		public Array<GridPoint2> getGridPoints()
		{
			return gridPoints;
		}
	}
	
	public static class Crop
	{
		private Species.CropSpecies species;
		private InventoryItem.ItemTypeID produce;
		private Array<AnimationConfig> animationConfig;
		
		public Crop()
		{
			this.species = Species.CropSpecies.cheri_tree;
			this.produce = InventoryItem.ItemTypeID.none;
			this.animationConfig = new Array<AnimationConfig>();
		}

		public Species.CropSpecies getSpecies()
		{
			return species;
		}
		
		public InventoryItem.ItemTypeID getProduce()
		{
			return produce;
		}
		
		public Array<AnimationConfig> getAnimationConfig()
		{
			return animationConfig;
		}
	}
}
