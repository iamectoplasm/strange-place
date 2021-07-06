package inventory.items;

import java.util.ArrayList;
import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import inventory.items.InventoryItem.ItemTypeID;
import utility.AssetHandler;

public class InventoryItemFactory
{
	public static final String TAG = InventoryItemFactory.class.getSimpleName();
	
	private Json json = new Json();
	private final String inventoryItemPath = "skins/items.json";
	private static InventoryItemFactory instance = null;
	private Hashtable<ItemTypeID, InventoryItem> inventoryItemList;

	public static InventoryItemFactory getInstance()
	{
		if (instance == null)
		{
			instance = new InventoryItemFactory();
		}

		return instance;
	}

	private InventoryItemFactory()
	{
		@SuppressWarnings("unchecked")
		ArrayList<JsonValue> list = json.fromJson(ArrayList.class, Gdx.files.internal(inventoryItemPath));

		inventoryItemList = new Hashtable<ItemTypeID, InventoryItem>();

		for (JsonValue jsonVal : list)
		{
			InventoryItem inventoryItem = json.readValue(InventoryItem.class, jsonVal);
			inventoryItemList.put(inventoryItem.getItemTypeID(), inventoryItem);
		}
	}

	public InventoryItem getInventoryItem(ItemTypeID inventoryItemType)
	{
		InventoryItem item = new InventoryItem(inventoryItemList.get(inventoryItemType));
		item.setDrawable(new TextureRegionDrawable(AssetHandler.ITEMS_TEXTUREATLAS.findRegion(item.getItemTypeID().toString())));
		//item.setScaling(Scaling.none);

		return item;
	}
}
