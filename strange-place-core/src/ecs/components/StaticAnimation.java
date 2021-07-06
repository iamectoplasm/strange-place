package ecs.components;

import java.util.Hashtable;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ecs.components.GrowthState.GrowthStage;

public class StaticAnimation extends Component
{
	public Hashtable<GrowthState.GrowthStage, Animation<TextureRegion>> animations;
	public Animation<TextureRegion> animation;
	public TextureRegion dirt;
	
	public float frameDuration = .8f;
	public float currentFrameTime = 0f;
	
	public int frameWidth = 16;
	public int frameHeight = 32;
	
	public StaticAnimation()
	{
		animations = new Hashtable<GrowthState.GrowthStage, Animation<TextureRegion>>();
		dirt = new TextureRegion();
	}
}
