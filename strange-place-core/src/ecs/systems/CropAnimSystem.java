package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ecs.components.ActiveTag;
import ecs.components.GrowthState;
import ecs.components.Sprite;
import ecs.components.StaticAnimation;

public class CropAnimSystem extends IntervalIteratingSystem
{
	//private static final String TAG = CropAnimSystem.class.getSimpleName();
	
	float delta = 0;
	
	ComponentMapper<Sprite> mSprite;
	ComponentMapper<StaticAnimation> mAnimation;
	ComponentMapper<GrowthState> mGrowthState;
	
	public CropAnimSystem()
	{
		super(Aspect.all(ActiveTag.class,
				StaticAnimation.class,
				GrowthState.class,
				Sprite.class),
				(1/60f));
	}

	@Override
	protected void process(int entityId)
	{
		Sprite sprite = mSprite.get(entityId);
		StaticAnimation animation = mAnimation.get(entityId);
		GrowthState growthState = mGrowthState.get(entityId);
		
		Animation<TextureRegion> currentAnimation = animation.animations.get(growthState.currentGrowthStage);
		
		//Gdx.app.debug(TAG, "number of frames in animation: " + frames);
		
		animation.currentFrameTime += world.getDelta();
		
		sprite.currentFrame = currentAnimation.getKeyFrame(animation.currentFrameTime, true);
		
		if(animation.currentFrameTime >= animation.frameDuration * 2)
		{
			animation.currentFrameTime = 0;
		}
	}
}
