package maps.locations;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import game.EntityFactory;
import game.EntityFactory.EntityName;
import maps.Map;
import maps.MapFactory;

public class FarmMap extends Map {
	//private static final String TAG = FarmMap.class.getSimpleName();

	private static String mapPath = "maps/marisol/farm-map.tmx";

	public FarmMap()
	{
		super(MapFactory.MapType.FARM, mapPath);

		this.json = new Json();

		//Array<EntityName> farmResidents = new Array<EntityName>();

		//farmResidents.add(EntityName.ALDER);

		//for (int i = 0; i < farmResidents.size; i++) {
		//	Entity entity = EntityFactory.getInstance().getEntityByName(farmResidents.get(i));
		//	mapEntities.add(entity);
		//}
	}

}
