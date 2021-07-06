package inventory.items;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class InventoryItem extends Image
{
	public static final String TAG = InventoryItem.class.getSimpleName();
	
	public enum ItemTypeID
	{
		cherry,
		lunar_wing,
		old_charm,
		magma_stone,
		icy_rock,
		smooth_rock,
		heat_rock,
		damp_rock,
		cheri_berry,
		chesto_berry,
		pecha_berry,
		rawst_berry,
		aspear_berry,
		leppa_berry,
		oran_berry,
		persim_berry,
		lum_berry,
		sitrus_berry,
		none;
	}

	public enum ItemAttribute
	{
		STACKABLE(1), CONSUMABLE(2), PLANTABLE(4), EQUIPPABLE(8);

		private int attribute;

		ItemAttribute(int attribute)
		{
			this.attribute = attribute;
		}

		public int getValue()
		{
			return attribute;
		}
	}

//	public enum ItemUseType
//	{
//		ITEM_RESTORE_ENERGY(1);
//
//		private int itemUseType;
//
//		ItemUseType(int itemUseType)
//		{
//			this.itemUseType = itemUseType;
//		}
//
//		public int getValue()
//		{
//			return itemUseType;
//		}
//	}

	private int itemAttributes;
//	private int itemUseType;
//	private int itemUseTypeValue;

	private ItemTypeID itemTypeID;
	private String itemShortDescription;

	private int itemValue;

	public InventoryItem()
	{
		super();
	}

	public InventoryItem(InventoryItem inventoryItem)
	{
		super();
		this.itemTypeID = inventoryItem.getItemTypeID();
		this.itemAttributes = inventoryItem.getItemAttributes();
//		this.itemUseType = inventoryItem.getItemUseType();
//		this.itemUseTypeValue = inventoryItem.getItemUseTypeValue();
		this.itemShortDescription = inventoryItem.getItemShortDescription();
		this.itemValue = inventoryItem.getItemValue();
		
		// Setting bounds & touchable
		//this.setBounds(inventoryItem.getImageX(), inventoryItem.getImageY(), inventoryItem.getImageWidth(), inventoryItem.getImageHeight());
		//this.setTouchable(Touchable.enabled);
	}

	public InventoryItem(TextureRegion textureRegion,
						 ItemTypeID itemTypeID,
						 int itemAttributes,
						 int itemUseType,
						 int itemUseTypeValue,
						 int itemValue)
	{
		super(textureRegion);
		this.itemTypeID = itemTypeID;
		this.itemAttributes = itemAttributes;
//		this.itemUseType = itemUseType;
//		this.itemUseTypeValue = itemUseTypeValue;
		this.itemValue = itemValue;
		
		// Setting bounds & touchable
		//this.setBounds(textureRegion.getRegionX(), textureRegion.getRegionY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
		//this.setTouchable(Touchable.enabled);

	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryItem getters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public ItemTypeID getItemTypeID()
	{
		return itemTypeID;
	}

	public int getItemAttributes()
	{
		return itemAttributes;
	}

//	public int getItemUseType()
//	{
//		return itemUseType;
//	}

//	public int getItemUseTypeValue()
//	{
//		return itemUseTypeValue;
//	}

	public int getItemValue()
	{
		return itemValue;
	}

	public String getItemShortDescription()
	{
		return itemShortDescription;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryItem setters
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public void setItemTypeID(ItemTypeID itemTypeID)
	{
		this.itemTypeID = itemTypeID;
	}

	public void setItemAttributes(int itemAttributes)
	{
		this.itemAttributes = itemAttributes;
	}

//	public void setItemUseType(int itemUseType)
//	{
//		this.itemUseType = itemUseType;
//	}

//	public void setItemUseTypeValue(int itemUseTypeValue)
//	{
//		this.itemUseTypeValue = itemUseTypeValue;
//	}

	public void setItemValue(int itemValue)
	{
		this.itemValue = itemValue;
	}

	public void setItemShortDescription(String itemShortDescription)
	{
		this.itemShortDescription = itemShortDescription;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - InventoryItem queries
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public boolean isStackable()
	{
		return ((itemAttributes & ItemAttribute.STACKABLE.getValue()) == ItemAttribute.STACKABLE.getValue());
	}

	public boolean isConsumable()
	{
		//Gdx.app.debug(TAG, "Now checking if " + itemTypeID + " is consumable");
		//Gdx.app.debug(TAG, "\titemattributes: " + itemAttributes);
		//Gdx.app.debug(TAG, "\titem's value: " + ItemAttribute.CONSUMABLE.getValue());
		//Gdx.app.debug(TAG, "\tNow ANDing them together: " + (itemAttributes & ItemAttribute.CONSUMABLE.getValue()));
		return ((itemAttributes & ItemAttribute.CONSUMABLE.getValue()) == ItemAttribute.CONSUMABLE.getValue());
	}
	
	public boolean isPlantable()
	{
		//Gdx.app.debug(TAG, "Now checking if item " + itemTypeID + " is plantable");
		//Gdx.app.debug(TAG, "\titemattributes: " + itemAttributes);
		//Gdx.app.debug(TAG, "\titem's value: " + ItemAttribute.PLANTABLE.getValue());
		//Gdx.app.debug(TAG, "\tNow ANDing them together: " + (itemAttributes & ItemAttribute.PLANTABLE.getValue()));
		return ((itemAttributes & ItemAttribute.PLANTABLE.getValue()) == ItemAttribute.PLANTABLE.getValue());
	}

	public boolean isSameItemType(InventoryItem candidateInventoryItem)
	{
		return (itemTypeID == candidateInventoryItem.getItemTypeID());
	}

//	public static boolean doesRestoreEnergy(int itemUseType)
//	{
//		return ((itemUseType & ItemUseType.ITEM_RESTORE_ENERGY.getValue()) == ItemUseType.ITEM_RESTORE_ENERGY.getValue());
//	}
}
