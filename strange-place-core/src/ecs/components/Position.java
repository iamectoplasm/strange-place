package ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

import utility.AssetHandler;

public class Position extends Component {
	// private int cellX = 0;
	// private int cellY = 0;
	public int cellX;
	public int cellY;

	public Vector2 startingPosition = new Vector2(0, 0);
	public Vector2 currentPosition = new Vector2(0, 0);
	public Vector2 destinationPosition = new Vector2(0, 0);
	
	public float xOffset;
	public float yOffset;

	public void setStartPosition()
	{
		this.startingPosition = new Vector2(cellX, cellY);
	}

	public void setCurrentPosition(Vector2 current)
	{
		cellX = (int) current.x;
		cellY = (int) current.y;
		this.currentPosition = AssetHandler.roundVector(current);
		// cellX = (int) newPosition.x;
		// cellY = (int) newPosition.y;
	}

	public void resetAllToStarting()
	{
		currentPosition.set(startingPosition);
		destinationPosition.set(startingPosition);
		cellX = (int) startingPosition.x;
		cellY = (int) startingPosition.y;
	}

	public void snapToCurrentToCell()
	{
		this.currentPosition = new Vector2(cellX, cellY);
		startingPosition.set(currentPosition);
		destinationPosition.set(currentPosition);
	}

	public void setDestPosition(Vector2 dest)
	{
		this.destinationPosition = AssetHandler.roundVector(dest);
	}

	// public int getCellX()
	// {
	// return this.cellX;
	// }

	// public int getCellY()
	// {
	// return this.cellY;
	// }
}
