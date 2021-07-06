package ecs.components;

import com.artemis.Component;

public class GrowthState extends Component
{
	public GrowthStage currentGrowthStage;
	
	public boolean hasBeenWatered = false;
	
	public static enum GrowthStage
	{
		PLANTABLE_SOIL, SEED, SPROUT, SAPLING, FLOWERING, HARVESTABLE;
		
		public GrowthStage getNext()
		{
			switch(this)
			{
			case SEED:
				return SPROUT;
			case SPROUT:
				return SAPLING;
			case SAPLING:
				return FLOWERING;
			case FLOWERING:
				return HARVESTABLE;
			default:
				return HARVESTABLE;
			}
		}
	}
	
	public GrowthState()
	{
		this.currentGrowthStage = GrowthStage.SEED;
	}
	
	public void iterateGrowthStage()
	{
		switch(currentGrowthStage)
		{
		case SEED:
			currentGrowthStage = GrowthStage.SPROUT;
			break;
		case SPROUT:
			currentGrowthStage = GrowthStage.SAPLING;
			break;
		case SAPLING:
			currentGrowthStage = GrowthStage.FLOWERING;
			break;
		case FLOWERING:
			currentGrowthStage = GrowthStage.HARVESTABLE;
			break;
		default:
			currentGrowthStage =  GrowthStage.HARVESTABLE;
			break;
		}
	}
}
