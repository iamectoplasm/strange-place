package ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.MathUtils;

import ecs.components.ActiveTag;
import ecs.components.MovementDirection;
import ecs.components.MovementState;
import ecs.components.NPCTag;
import ecs.components.MovementDirection.Direction;

public class NPCInputSystem extends IntervalIteratingSystem
{
	public static final String TAG = NPCInputSystem.class.getSimpleName();
	
	protected ComponentMapper<MovementDirection> mDirection;
	protected ComponentMapper<MovementState> mState;

	protected float deltaTime;

	public NPCInputSystem()
	{
		super(Aspect.all(ActiveTag.class,
						 NPCTag.class,
						 MovementDirection.class,
						 MovementState.class),
						 (1/10f));
	}

	@Override
	protected void process(int entityId)
	{
		MovementState state = mState.get(entityId);
		MovementDirection direction = mDirection.get(entityId);
		
		if(state.currentState != MovementState.State.IMMOBILE && !state.pauseMovement)
		{
			deltaTime += world.getDelta();

			// Change direction after so many seconds
			if (deltaTime > MathUtils.random(1, 50))
			{
				state.currentState = MovementState.State.getRandomNext();
				
				if (!state.moveInProgress)
				{
					direction.currentDirection = Direction.getRandomNext();
					state.moveRequested = true;
				}
				else
				{
					state.currentState = MovementState.State.IDLE;
				}
				deltaTime = 0.0f;

				// Gdx.app.debug(TAG, "currentDirection for NPC entity " + entityId + " changed
				// to " + direction.get(entityId).currentDirection);
			}
			else
			{
				state.moveRequested = false;
			}
		}
	}
}
