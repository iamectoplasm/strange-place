package ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class BoundingBox extends Component
{
	//private static final String TAG = BoundingBox.class.getSimpleName();
	
	public ShapeRenderer shapeRenderer;
	public Rectangle boundingBox;
	
	public Vector2 boundingBoxCenter;
	public Vector2 selectorPoint;
	
	public boolean entityInSelectionBounds;

	public float xPos;
	public float yPos;

	public float width;
	public float height;

	public BoundingBox()
	{
		this.width = 1;
		this.height = 1;

		this.xPos = 0;
		this.yPos = 0;

		this.boundingBox = new Rectangle(xPos, yPos, width, height);
		this.shapeRenderer = new ShapeRenderer();
		
		this.boundingBoxCenter = new Vector2();
		boundingBox.getCenter(boundingBoxCenter);
		
		this.selectorPoint = new Vector2(boundingBoxCenter.x, boundingBoxCenter.y);
		
		entityInSelectionBounds = false;
	}
}
