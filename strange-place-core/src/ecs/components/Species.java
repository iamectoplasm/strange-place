package ecs.components;

import com.artemis.Component;

import configs.CropConfig.Crop;
import inventory.items.InventoryItem.ItemTypeID;

public class Species extends Component
{
	public String species;
	public Crop crop;
	//public BerryTree berryTree;
	public ItemTypeID produce;
	
	public CropSpecies type;
	
	public enum CropSpecies
	{
		cheri_tree,
		chesto_tree,
		pecha_tree,
		rawst_tree,
		aspear_tree,
		leppa_tree,
		oran_tree,
		persim_tree,
		lum_tree,
		sitrus_tree
	}
	
	public Species()
	{
		species = "";
		//berryTree = new BerryTree();
		crop = new Crop();
		produce = ItemTypeID.none;
		species = null;
	}
}
