package configs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import configs.CropConfig.Crop;
import ecs.components.Species;
import inventory.items.InventoryItem;

public class CropDeserializer
{
	//private static final String TAG = CropDeserializer.class.getSimpleName();
	
		private static CropDeserializer instance = null;
		private CropConfig config;
		
		public static String CROP_CONFIG = "scripts/crops.json";
		
		private CropDeserializer()
		{
			Json tempJson = new Json();
			config = tempJson.fromJson(CropConfig.class, Gdx.files.internal(CROP_CONFIG).read());
		}
		
		public static CropDeserializer getInstance()
		{
			if (instance == null)
			{
				instance = new CropDeserializer();
			}

			return instance;
		}
		
		public CropConfig getCropConfig()
		{
			return config;
		}
		
		public Crop getCropBySpecies(Species.CropSpecies species)
		{
			Array<Crop> crops = config.getAllCrops();
			
			for(int i = 0; i < crops.size; i++)
			{
				if(crops.get(i).getSpecies().equals(species))
				{
					return crops.get(i);
				}
			}
			
			return null;
		}
		
		public Crop getCropByProduce(InventoryItem.ItemTypeID produce)
		{
			Array<Crop> crops = config.getAllCrops();
			for(int i = 0; i < crops.size; i++)
			{
				if(crops.get(i).getProduce().equals(produce))
				{
					return crops.get(i);
				}
			}
			
			return null;
		}
}
