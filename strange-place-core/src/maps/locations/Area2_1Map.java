package maps.locations;

import maps.Map;
import maps.MapFactory;

public class Area2_1Map extends Map
{
	private static String mapPath = "maps/testing/area2-1.tmx";

	public Area2_1Map()
	{
		super(MapFactory.MapType.AREA2_1, mapPath);
	}
}
