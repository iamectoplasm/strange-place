package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;

import ecs.components.MovementDirection;
import ecs.components.MovementState;
import ecs.components.Velocity;
import ecs.components.MovementDirection.Direction;
import ecs.components.MovementState.State;
import ecs.components.player.KeyboardInput;
import ecs.components.player.PlayerTag;
import ecs.components.player.KeyboardInput.Keys;

public class PlayerInputSystem extends IntervalIteratingSystem
{
	public static final String TAG = PlayerInputSystem.class.getSimpleName();

	protected ComponentMapper<KeyboardInput> mInput;
	protected ComponentMapper<MovementDirection> mDirection;
	protected ComponentMapper<MovementState> mMovementState;
	protected ComponentMapper<Velocity> mVelocity;
	
	private float refaceTimer = 0f;
	
	public PlayerInputSystem()
	{
		super(Aspect.all(KeyboardInput.class,
						 MovementDirection.class,
						 MovementState.class,
						 PlayerTag.class,
						 Velocity.class),
				(1/60f));
	}

	@Override
	protected void process(int entityId)
	{
		
		KeyboardInput input = mInput.create(entityId);
		MovementDirection direction = mDirection.get(entityId);
		MovementState movementState = mMovementState.get(entityId);
		//Velocity velocity = mVelocity.get(entityId);
		
		if (input.movementKeyPressed)
		{	
			if (!movementState.moveInProgress)
			{
				if (input.keys.get(Keys.LEFT))
				{
					if(direction.currentDirection != Direction.LEFT)
					{
						movementState.currentState = State.REFACE;
						direction.currentDirection = Direction.LEFT;
						runRefaceTimer();
					}
					else if(isRefaceInProgress(movementState.refaceThreshold))
					{
						runRefaceTimer();
					}
					else
					{
						movementState.moveRequested = true;
						resetRefaceTimer();
					}
					//direction.currentDirection = Directions.LEFT;
					// Gdx.app.debug(TAG, "direction now set to LEFT");
				}

				else if (input.keys.get(Keys.RIGHT))
				{
					if(direction.currentDirection != Direction.RIGHT)
					{
						movementState.currentState = State.REFACE;
						direction.currentDirection = Direction.RIGHT;
						runRefaceTimer();
					}
					else if(isRefaceInProgress(movementState.refaceThreshold))
					{
						runRefaceTimer();
					}
					else
					{
						movementState.moveRequested = true;
						resetRefaceTimer();
					}
					
					//direction.currentDirection = Directions.RIGHT;
					// Gdx.app.debug(TAG, "direction now set to RIGHT");
				}

				else if (input.keys.get(Keys.UP))
				{
					if(direction.currentDirection != Direction.UP)
					{
						movementState.currentState = State.REFACE;
						direction.currentDirection = Direction.UP;
						runRefaceTimer();
					}
					else if(isRefaceInProgress(movementState.refaceThreshold))
					{
						runRefaceTimer();
					}
					else
					{
						movementState.moveRequested = true;
						resetRefaceTimer();
					}
					
					//direction.currentDirection = Directions.UP;
					// Gdx.app.debug(TAG, "direction now set to UP");
				}

				else if (input.keys.get(Keys.DOWN))
				{
					if(direction.currentDirection != Direction.DOWN)
					{
						movementState.currentState = State.REFACE;
						direction.currentDirection = Direction.DOWN;
						runRefaceTimer();
					}
					else if(isRefaceInProgress(movementState.refaceThreshold))
					{
						runRefaceTimer();
					}
					else
					{
						movementState.moveRequested = true;
						resetRefaceTimer();
					}
					
					//direction.currentDirection = Directions.DOWN;
					// Gdx.app.debug(TAG, "direction now set to DOWN");
				}

				//movementState.currentState = MovementState.State.WALK;
				//movementState.moveRequested = true;
			}
		}
		else
		{
			//movementState.currentState = MovementState.State.IDLE;
			resetRefaceTimer();
			movementState.moveRequested = false;
			return;
		}
	}

	public void clear(int entityId)
	{
		mInput.get(entityId).keys.put(Keys.LEFT, false);
		mInput.get(entityId).keys.put(Keys.RIGHT, false);
		mInput.get(entityId).keys.put(Keys.UP, false);
		mInput.get(entityId).keys.put(Keys.DOWN, false);
		mInput.get(entityId).keys.put(Keys.SELECT, false);
		mInput.get(entityId).keys.put(Keys.QUIT, false);
	}
	
	private void runRefaceTimer()
	{
		refaceTimer = refaceTimer + world.delta;
	}
	
	private void resetRefaceTimer()
	{
		refaceTimer = 0f;
	}
	
	private boolean isRefaceInProgress(float refaceThreshold)
	{
		if(refaceTimer > 0f && refaceTimer <= refaceThreshold)
		{
			return true;
		}
		else
			return false;
	}
}
