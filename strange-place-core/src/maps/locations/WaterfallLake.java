package maps.locations;

import com.badlogic.gdx.utils.Json;

import maps.Map;
import maps.MapFactory;

public class WaterfallLake extends Map {
	// private static final String TAG = WaterfallLake.class.getSimpleName();

	private static String mapPath = "maps/pmd/waterfall-lake.tmx";

	public WaterfallLake()
	{
		super(MapFactory.MapType.WATERFALL_LAKE, mapPath);

		json = new Json();

		// Array<EntityName> residents = new Array<EntityName>();

		// for(int i = 0; i < residents.size; i++)
		// {
		// Entity entity =
		// EntityFactory.getInstance().getEntityByName(residents.get(i));
		// mapEntities.add(entity);
		// }
	}

}
