package ecs.systems;

import java.math.BigDecimal;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import ecs.components.ActiveTag;
import ecs.components.MovementDirection;
import ecs.components.MovementState;
import ecs.components.player.KeyboardInput;
import ecs.components.Position;
import ecs.components.Velocity;
import ecs.components.MovementDirection.Direction;
import ecs.components.MovementState.State;

public class MovementSystem extends IntervalIteratingSystem
{
	//private final static String TAG = MovementSystem.class.getSimpleName();
	
	ComponentMapper<ActiveTag> activeTag;
	
	ComponentMapper<MovementDirection> mDirection;
	ComponentMapper<Position> mPosition;
	ComponentMapper<Velocity> mVelocity;
	ComponentMapper<MovementState> mMovementState;
	ComponentMapper<KeyboardInput> mInput;

	public MovementSystem()
	{
		super(Aspect.all(ActiveTag.class,
						 MovementDirection.class,
						 Position.class,
						 Velocity.class,
						 MovementState.class,
						 KeyboardInput.class),
				(1/60f));
	}

	protected void calculateNextPosition(int entityId)
	{
		MovementDirection direction = mDirection.get(entityId);
		Position position = mPosition.get(entityId);
		MovementState movementState = mMovementState.get(entityId);
		
		position.setStartPosition();

		float destX = position.cellX + direction.currentDirection.getDX();
		float destY = position.cellY + direction.currentDirection.getDY();

		position.setDestPosition(new Vector2(destX, destY));

		movementState.moveInProgress = true;
		// position.get(entityId).destinationPosition.set(destX, destY);
		// updateBoundingBoxPosition();
	}

	protected void move(int entityId)
	{
		MovementDirection direction = mDirection.get(entityId);
		Position position = mPosition.get(entityId);
		MovementState movementState = mMovementState.get(entityId);
		
		Direction currentDirection = direction.currentDirection;
		Vector2 currentPositionLocal = position.currentPosition;
		Vector2 nextPosition = position.destinationPosition;
		Vector2 startPosition = position.startingPosition;
		
		movementState.stateTime += world.getDelta();

		float alpha = calculateLerpAlpha(movementState.stateTime, entityId);
		// Gdx.app.debug(TAG, "Current lerp alpha: " + alpha);

		currentPositionLocal.set(position.startingPosition.cpy().lerp(position.destinationPosition, alpha));

		switch (currentDirection)
		{
		case RIGHT:
			MathUtils.clamp(currentPositionLocal.x, startPosition.x, nextPosition.x);
			
			position.setCurrentPosition(currentPositionLocal);

			if (currentPositionLocal.x >= nextPosition.x)
			{
				completeMove(entityId);
			}
			break;

		case LEFT:
			//if (currentPositionLocal.x >= nextPosition.x)
			//{
				// MathUtils.clamp(currentPositionLocal.x, nextPosition.x, startPosition.x);
			position.setCurrentPosition(currentPositionLocal);
			//}
			// if(currentPositionLocal.x <= nextPosition.x)
			//else
			//{
			//	completeMove(entityId);
			//}
			
			if(currentPositionLocal.x <= nextPosition.x)
			{
				completeMove(entityId);
			}
			break;

		case UP:
			MathUtils.clamp(currentPositionLocal.y, startPosition.y, nextPosition.y);
			position.setCurrentPosition(currentPositionLocal);
			
			if (currentPositionLocal.y >= nextPosition.y)
			{
				completeMove(entityId);
			}
			break;

		case DOWN:
			//if (currentPositionLocal.y >= nextPosition.y)
			//{
			//	MathUtils.clamp(currentPositionLocal.y, nextPosition.y, startPosition.y);
				position.setCurrentPosition(currentPositionLocal);
			//}
			// if(currentPositionLocal.y <= nextPosition.y)
			//else
			//{
			//	completeMove(entityId);
			//}
			if(currentPositionLocal.y <= nextPosition.y)
			{
				completeMove(entityId);
			}
			break;
		default:
			break;
		}
	}

	private void completeMove(int entityId)
	{
		Position position = mPosition.get(entityId);
		MovementState movementState = mMovementState.get(entityId);

		position.cellX = (int) position.destinationPosition.x;
		position.cellY = (int) position.destinationPosition.y;
		
		position.snapToCurrentToCell();

		movementState.resetStateTime();
		movementState.moveInProgress = false;
	}

	@Override
	protected void process(int entityId)
	{
		MovementState movementState = mMovementState.get(entityId);
		
		if(movementState.moveRequested && !movementState.moveInProgress)
		{
			calculateNextPosition(entityId);
			if(world.getSystem(CollisionSystem.class).checkForMapCollision(entityId) ||
				world.getSystem(CollisionSystem.class).checkForEntityCollision(entityId))
			{
				cancelMove(entityId);
			}
		}
		else if(movementState.moveInProgress)
		{
			movementState.currentState = State.WALK;
			move(entityId);
		}
	}
	
	public void cancelMove(int entityId)
	{
		Position position = mPosition.get(entityId);
		MovementState state = mMovementState.get(entityId);
		
		state.currentState = MovementState.State.IDLE;
		state.moveRequested = false;
		state.moveInProgress = false;

		position.resetAllToStarting();
	}

	private float calculateLerpAlpha(float currentTime, int entityId)
	{
		Velocity velocity = mVelocity.get(entityId);
		
		float alpha = (currentTime/ velocity.velocity);
		Float roundedAlpha = new BigDecimal(alpha).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
		return roundedAlpha;
	}

}
