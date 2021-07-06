package maps.locations;

import maps.Map;
import maps.MapFactory;

public class MarisolMap extends Map
{
	// private static final String TAG = MarisolMap.class.getSimpleName();

	private static String mapPath = "maps/marisol/marisol-map.tmx";

	public MarisolMap()
	{
		super(MapFactory.MapType.MARISOL, mapPath);
	}
}
