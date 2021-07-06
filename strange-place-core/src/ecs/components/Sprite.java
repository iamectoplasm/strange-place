package ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sprite extends Component
{
	public TextureRegion currentFrame = new TextureRegion();
	
	public int drawWidth;
	public int drawHeight;

	public Batch batch;

	public Sprite(Batch batch)
	{
		this.batch = batch;
	}

	public Sprite()
	{

	}
}
