package maps.locations;

import maps.Map;
import maps.MapFactory;

public class Area1_1Map extends Map
{
	private static String mapPath = "maps/testing/area1-1.tmx";

	public Area1_1Map()
	{
		super(MapFactory.MapType.AREA1_1, mapPath);
	}

}
