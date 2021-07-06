package maps.locations;

import maps.Map;
import maps.MapFactory;

public class SkyBluePlains extends Map
{
	// private static final String TAG = WaterfallLake.class.getSimpleName();

	private static String mapPath = "maps/pmd/sky-blue-plains.tmx";

	public SkyBluePlains()
	{
		super(MapFactory.MapType.SKY_BLUE_PLAINS, mapPath);

		//json = new Json();

		/*
		Array<EntityName> residents = new Array<EntityName>();

		for(int i = 0; i < residents.size; i++)
		{
		Entity entity =
		EntityFactory.getInstance().createNPCEntityByName(residents.get(i));
		mapEntities.add(entity);
		}
		*/
	}
}
