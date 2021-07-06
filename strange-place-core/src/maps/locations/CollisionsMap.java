package maps.locations;

import com.artemis.Entity;
import com.badlogic.gdx.utils.Json;

import ecs.components.Species.CropSpecies;
import game.EntityFactory;
import game.EntityFactory.EntityName;
import maps.Map;
import maps.MapFactory;

public class CollisionsMap extends Map
{
	private static final String TAG = CollisionsMap.class.getSimpleName();

	private static String mapPath = "maps/testing/collisions-test.tmx";
	private Json json;

	public CollisionsMap()
	{
		super(MapFactory.MapType.TESTING, mapPath);
	
		this.json = new Json();
		
		EntityFactory.getInstance();
		Entity treeTest = EntityFactory.getInstance().createPlantEntity(CropSpecies.leppa_tree);
		//Gdx.app.debug(TAG, "Entity treeTest: " + treeTest.getId());
		mapEntities.add(treeTest);
		
		//Entity kenneth = EntityFactory.getInstance().createNPCEntityByName(EntityName.KENNETH);
		//mapEntities.add(kenneth);
	
		//Array<EntityName> residents = new Array<EntityName>();
		//residents.add(EntityName.PUNK);
	
		//for(int i = 0; i < residents.size; i++)
		//{
		//	Entity entity = EntityFactory.getInstance().getEntityByName(residents.get(i));
		//	Vector2 entityStartPosition = npcStartPositions.get(i);
		//	entity.getComponent(Position.class).setCurrentPosition(entityStartPosition);
		//	mapEntities.add(entity);
		//}
	}
}
