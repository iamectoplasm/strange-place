package maps;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;

import ecs.systems.PortalSystem;
import game.StrangePlace;
import observers.PortalObserver.PortalEvent;
import observers.subjects.PortalSubject;

public class Portal extends PortalSubject
{
	public static final String TAG = Portal.class.getSimpleName();
	
	PortalSystem portalSystem;
	
	Rectangle portalBounds;
	
	float x;
	float y;
	float width;
	float height;
	
	String destinationMap;
	String associatedSpawn;
	boolean portalTriggered = false;
	
	public Portal(MapObject portal)
	{
		this.portalSystem = StrangePlace.gameScreen.getWorld().getSystem(PortalSystem.class);
		this.addPortalObserver(portalSystem);
		
		this.destinationMap = portal.getName();
		this.associatedSpawn = portal.getProperties().get("spawnID", String.class);
		
		this.x = portal.getProperties().get("x", Float.class) * Map.UNIT_SCALE;
		this.y = portal.getProperties().get("y", Float.class) * Map.UNIT_SCALE;
		this.width = portal.getProperties().get("width", Float.class) * Map.UNIT_SCALE;
		this.height = portal.getProperties().get("height", Float.class) * Map.UNIT_SCALE;

		this.portalBounds = new Rectangle(x, y, width, height);
		//Gdx.app.debug(TAG, "New Portal object created with name: " + destinationMap + " and values: " + portalBounds.toString());
		//Gdx.app.debug(TAG, "\t\tThe spawn associated with this portal is " + associatedSpawn);
	}
	
	public Rectangle getPortalBounds()
	{
		return this.portalBounds;
	}
	
	public String getDestinationMap()
	{
		return this.destinationMap;
	}
	
	public void checkPortalActivation(Rectangle playerBoundingBox)
	{
		//Gdx.app.debug(TAG, "Portal: " + portalBounds.toString());
		//Gdx.app.debug(TAG, "Player bounding box: " + playerBoundingBox.toString());
		//Gdx.app.debug(TAG, "Overlap? " + portalBounds.overlaps(playerBoundingBox));
		
		if(portalBounds.overlaps(playerBoundingBox))
		{
			notifyPortalObservers(destinationMap, associatedSpawn, PortalEvent.PORTAL_TRIGGERED);
		}
	}

}
