package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;

import ecs.components.ActiveTag;
import ecs.components.DynamicAnimation;
import ecs.components.MovementDirection;
import ecs.components.MovementState;
import ecs.components.Sprite;
import ecs.components.Velocity;
import ecs.components.DynamicAnimation.DynAnimationType;
import ecs.components.MovementDirection.Direction;

public class CharAnimSystem extends IntervalIteratingSystem
{
	ComponentMapper<MovementDirection> mDirection;
	ComponentMapper<MovementState> mMovementState;
	ComponentMapper<DynamicAnimation> mAnimation;
	ComponentMapper<Sprite> mSprite;
	ComponentMapper<Velocity> mVelocity;

	public CharAnimSystem()
	{
		super(Aspect.all(ActiveTag.class,
				MovementDirection.class,
				Velocity.class,
				MovementState.class,
				DynamicAnimation.class,
				Sprite.class),
				(1/60f));
	}

	@Override
	protected void process(int entityId)
	{
		updateAnimations(entityId);
	}

	protected void updateAnimations(int entityId)
	{
		DynamicAnimation movementAnim = mAnimation.get(entityId);
		Sprite sprite = mSprite.create(entityId);
		
		Direction currentDir = mDirection.get(entityId).currentDirection;
		//TextureRegion sprite = mSprite.get(entityId).currentFrame;

		switch (currentDir)
		{
		case DOWN:
			DynamicAnimation.Animation downAnimation = movementAnim.animations.get(DynAnimationType.WALK_DOWN);
			
			if(mMovementState.get(entityId).moveInProgress)
			{
				sprite.currentFrame = downAnimation.walk(world.getDelta());
			}
			else
			{
				sprite.currentFrame = downAnimation.getNextStandingFrame();
			}
			
			break;

		case UP:
			DynamicAnimation.Animation upAnimation = movementAnim.animations.get(DynAnimationType.WALK_UP);
			
			if(mMovementState.get(entityId).moveInProgress)
			{
				sprite.currentFrame = upAnimation.walk(world.getDelta());
			}
			else
			{
				sprite.currentFrame = upAnimation.getNextStandingFrame();
			}
			
			break;

		case LEFT:
			DynamicAnimation.Animation leftAnimation = movementAnim.animations.get(DynAnimationType.WALK_LEFT);
			
			if(mMovementState.get(entityId).moveInProgress)
			{
				sprite.currentFrame = leftAnimation.walk(world.getDelta());
			}
			else
			{
				sprite.currentFrame = leftAnimation.getNextStandingFrame();
			}
			
			break;

		case RIGHT:
			DynamicAnimation.Animation rightAnimation = movementAnim.animations.get(DynAnimationType.WALK_RIGHT);
			
			if(mMovementState.get(entityId).moveInProgress)
			{
				sprite.currentFrame = rightAnimation.walk(world.getDelta());
			}
			else
			{
				sprite.currentFrame = rightAnimation.getNextStandingFrame();
			}
			
			break;

		default:
			mSprite.get(entityId).currentFrame = movementAnim.animations.get(DynAnimationType.WALK_DOWN)
					.getNextStandingFrame();
			break;
		}
	}
}
