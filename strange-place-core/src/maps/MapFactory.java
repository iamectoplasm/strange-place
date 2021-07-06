package maps;

import java.util.Hashtable;

import maps.locations.Area1_1Map;
import maps.locations.Area2_1Map;
import maps.locations.BusStationMap;
import maps.locations.CollisionsMap;
//import maps.locations.CollisionsMap;
import maps.locations.FarmMap;
import maps.locations.MarisolMap;
import maps.locations.SkyBluePlains;
import maps.locations.WaterfallLake;
import maps.locations.WildPlains;

public class MapFactory
{
	// All maps for the game
	private static Hashtable<MapType, Map> mapTable = new Hashtable<MapType, Map>();

	public static enum MapType
	{
		AREA1_1("map configs/area1-1.json"),
		AREA2_1("map configs/area2-1.json"),
		BUS_STATION("map configs/bus-station.json"),
		FARM("map configs/farm.json"),
		MARISOL("map configs/marisol.json"),
		WATERFALL_LAKE("map configs/waterfall-lake.json"),
		WILD_PLAINS("map configs/wild-plains.json"),
		SKY_BLUE_PLAINS("map configs/sky-blue-plains.json"),
		TESTING("map configs/testing.json");
		
		private String mapJsonPath;
		
		private MapType(String mapJsonPath)
		{
			this.mapJsonPath = mapJsonPath;
		}
		
		public String getMapJsonPath()
		{
			return mapJsonPath;
		}
	}

	static public Map getMap(MapType mapType)
	{
		Map map = null;

		switch (mapType)
		{
		case AREA1_1:
			map = mapTable.get(MapType.AREA1_1);
			if(map == null)
			{
				map = new Area1_1Map();
				mapTable.put(MapType.AREA1_1, map);
			}
			break;
		case AREA2_1:
			map = mapTable.get(MapType.AREA2_1);
			if(map == null)
			{
				map = new Area2_1Map();
				mapTable.put(MapType.AREA2_1, map);
			}
			break;
		case BUS_STATION:
			map = mapTable.get(MapType.BUS_STATION);
			if(map == null)
			{
				map = new BusStationMap();
				mapTable.put(MapType.BUS_STATION, map);
			}
			break;
		case FARM:
			map = mapTable.get(MapType.FARM);
			if (map == null)
			{
				map = new FarmMap();
				mapTable.put(MapType.FARM, map);
			}
			break;

		case MARISOL:
			map = mapTable.get(MapType.MARISOL);
			if (map == null)
			{
				map = new MarisolMap();
				mapTable.put(MapType.MARISOL, map);
			}
			break;

		case WATERFALL_LAKE:
			map = mapTable.get(MapType.WATERFALL_LAKE);
			if (map == null)
			{
				map = new WaterfallLake();
				mapTable.put(MapType.WATERFALL_LAKE, map);
			}
			break;
			
		case WILD_PLAINS:
			map = mapTable.get(MapType.WILD_PLAINS);
			if (map == null)
			{
				map = new WildPlains();
				mapTable.put(MapType.WILD_PLAINS, map);
			}
			break;

		case SKY_BLUE_PLAINS:
			map = mapTable.get(MapType.SKY_BLUE_PLAINS);
			if (map == null)
			{
				map = new SkyBluePlains();
				mapTable.put(MapType.SKY_BLUE_PLAINS, map);
			}
			break;
		case TESTING:
			map = mapTable.get(MapType.TESTING);
			if(map == null)
			{
				map = new CollisionsMap();
				mapTable.put(MapType.TESTING, map);
			}
			break;
		default:
			break;
		}

		return map;
	}
}
