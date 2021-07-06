package inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import inventory.items.InventoryItem;

public class InventorySlotTooltip extends Window
{
	public static final String TAG = InventorySlotTooltip.class.getSimpleName();
	
	//private Skin skin;
	private Label description;

	public InventorySlotTooltip(final Skin skin)
	{
		//super("", skin, "inventory-tooltip");
		super("", skin, "tooltip-window");
		
		//this.description = new Label("", skin, "tooltip-label");
		this.description = new Label("", skin, "inventory-tooltip");
		this.description.setWrap(true);
		this.description.setFillParent(true);
		//this.description.setWrap(true);
		
		this.add(description);
		//this.pad(10f, 10f, 10f, 10f);
		//this.setWidth(100f);
		this.pack();
		
		//this.setVisible(false);
		this.setVisible(true);
	}
	
	public void setVisible(InventorySlot inventorySlot, boolean visible)
	{
		super.setVisible(visible);
		
		if(inventorySlot == null)
		{
			return;
		}
		
		if(!inventorySlot.hasItem())
		{
			super.setVisible(false);
		}
	}
	
	public void updateDescription(InventorySlot inventorySlot)
	{
		if(inventorySlot.hasItem())
		{
			StringBuilder string = new StringBuilder();
			InventoryItem item = inventorySlot.getInventoryItem();
			
			string.append(item.getItemTypeID().name().replace('_', ' '));
			string.append(System.getProperty("line.separator"));
			
			string.append(item.getItemShortDescription());
			
			//string.append(System.getProperty("line.separator"));
			//string.append(String.format("Original Value: %s monies", item.getItemValue()));
			//string.append(System.getProperty("line.separator"));
			
			description.setText(string);
			this.pack();
		}
		else
		{
			description.setText("");
			this.pack();
		}
	}

}
