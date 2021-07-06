package ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;

public class MovementState extends Component
{
	public State currentState = State.IDLE;
	public boolean moveRequested = false;
	public boolean moveInProgress = false;
	
	public float refaceThreshold = 0.1f;

	public float stateTime = 0.0f;
	
	public boolean pauseMovement = false;

	public void resetStateTime()
	{
		stateTime = 0;
	}

	public static enum State
	{
		IDLE,
		WALK,
		REFACE,
		IMMOBILE; // IMMOBILE should always be last
		
		//public float walkTime = 0.25f;
		//public float walkTime = 0.2f;

		static public State getRandomNext()
		{
			// Ignore IMMOBILE which should be last state
			return State.values()[MathUtils.random(State.values().length - 3)];
		}
	}
}
