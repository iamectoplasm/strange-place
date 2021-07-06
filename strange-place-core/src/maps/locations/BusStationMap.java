package maps.locations;

import maps.Map;
import maps.MapFactory;

public class BusStationMap extends Map
{
	private static String mapPath = "maps/testing/bus-station.tmx";

	public BusStationMap()
	{
		super(MapFactory.MapType.BUS_STATION, mapPath);
		// TODO Auto-generated constructor stub
	}

}
