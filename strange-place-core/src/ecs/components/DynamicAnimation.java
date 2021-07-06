package ecs.components;

import java.util.Hashtable;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class DynamicAnimation extends Component
{
	public Hashtable<DynAnimationType, Animation> animations = new Hashtable<DynAnimationType, Animation>();
	//public float animationTimer = 0.0f;
	public boolean playWalkAnim = false;
	
	public int frameWidth = 32;
	public int frameHeight = 32;

	public static enum DynAnimationType
	{
		WALK_LEFT, WALK_RIGHT, WALK_UP, WALK_DOWN
	}
	
	public static class Animation
	{
		private int currentFrameIndex;
		private Array<TextureRegion> frames;

		private float frameDuration;
		private float frameTime;
		
		public Animation(Array<TextureRegion> keyFrames, float velocity)
		{
			frames = keyFrames;
			currentFrameIndex = 0;

			this.frameDuration = velocity / 2;
			frameTime = 0;
		}
		
		public TextureRegion getCurrentFrame()
		{
			return frames.get(currentFrameIndex);
		}

		public float getFrameDuration()
		{
			return frameDuration;
		}

		public int getCurrentFrameIndex()
		{
			return currentFrameIndex;
		}

		public TextureRegion getNextFrame()
		{
			TextureRegion nextFrame = null;

			if (currentFrameIndex == 3)
				currentFrameIndex = 0;
			else
				currentFrameIndex += 1;

			// Gdx.app.debug(TAG, "getNextFrame called: " + currentFrameIndex);
			nextFrame = frames.get(currentFrameIndex);
			return nextFrame;
		}

		public TextureRegion getFrameAtIndex(int index)
		{
			if (index > 3 || index < 0)
			{
				return frames.get(currentFrameIndex);
			}
			currentFrameIndex = index;
			return frames.get(currentFrameIndex);
		}

		public TextureRegion getNextStandingFrame()
		{
			resetFrameTime();

			if (currentFrameIndex == 0)
			{
				return frames.get(0);
			}
			else if (currentFrameIndex == 2)
			{
				return frames.get(2);
			}
			else
			{
				return getNextFrame();
			}
		}

		public TextureRegion getNextWalkingFrame()
		{
			if (currentFrameIndex == 1)
				return frames.get(1);
			else if (currentFrameIndex == 3)
				return frames.get(3);
			else
				return getNextFrame();
		}

		public boolean characterIsStanding()
		{
			if (currentFrameIndex % 2 == 0)
			{
				return true;
			}
			else
				return false;
		}

		public boolean characterIsWalking()
		{
			if (currentFrameIndex % 2 == 1)
			{
				return true;
			}
			
			else
				return false;
		}

		public void resetFrameTime()
		{
			frameTime = 0;
		}

		public float getFrameTime()
		{
			return frameTime;
		}

		public TextureRegion walk(float delta)
		{
			accumulateDelta(delta);

			if (frameTime >= frameDuration)
			{
				resetFrameTime();
				return getNextFrame();
			}

			return getCurrentFrame();
		}

		private void accumulateDelta(float delta)
		{
			frameTime = frameTime + delta;
		}
	}
	
	
}
