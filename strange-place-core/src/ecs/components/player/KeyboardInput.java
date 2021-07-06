package ecs.components.player;

import java.util.HashMap;
import java.util.Map;

import com.artemis.Component;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

public class KeyboardInput extends Component implements InputProcessor
{
	public static final String TAG = KeyboardInput.class.getSimpleName();

	public boolean movementKeyPressed = false;
	public Vector3 lastMouseCoordinates = new Vector3(0, 0, 0);
	
	public boolean shiftPressed;
	public boolean spacePressed;

	public KeyboardInput()
	{
		initializeHashmap();
	}

	public enum Keys
	{
		LEFT, RIGHT, UP, DOWN, SHIFT, SELECT, QUIT, PAUSE
	}

	public enum Mouse
	{
		SELECT, DOACTION
	}

	public Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
	public Map<Mouse, Boolean> mouseButtons = new HashMap<Mouse, Boolean>();

	// Initialize hashmap for movements
	public void initializeHashmap()
	{
		keys.put(Keys.LEFT, false);
		keys.put(Keys.RIGHT, false);
		keys.put(Keys.UP, false);
		keys.put(Keys.DOWN, false);
		keys.put(Keys.SELECT, false);
		keys.put(Keys.QUIT, false);
		
		keys.put(Keys.SHIFT, false);

		mouseButtons.put(Mouse.SELECT, false);
		mouseButtons.put(Mouse.DOACTION, false);
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Keys pressed
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public boolean keyDown(int keycode)
	{		
		if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
		{
			this.leftPressed();
		}
		
		if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
		{
			this.rightPressed();
		}
		if (keycode == Input.Keys.UP || keycode == Input.Keys.W)
		{
			this.upPressed();
		}
		if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
		{
			this.downPressed();
		}
		if(keycode == Input.Keys.SHIFT_LEFT)
		{
			this.shiftPressed();
		}
		if (keycode == Input.Keys.Q)
		{
			this.quitPressed();
		}
		if (keycode == Input.Keys.P)
		{
			this.pausePressed();
		}
		if (keycode == Input.Keys.SPACE)
		{
			this.spacePressed();
		}

		return true;
	}

	public void leftPressed()
	{
		keys.put(Keys.LEFT, true);
		movementKeyPressed = true;
	}

	public void rightPressed()
	{
		keys.put(Keys.RIGHT, true);
		movementKeyPressed = true;
	}

	public void upPressed()
	{
		keys.put(Keys.UP, true);
		movementKeyPressed = true;
	}

	public void downPressed()
	{
		keys.put(Keys.DOWN, true);
		movementKeyPressed = true;
	}
	
	public void shiftPressed()
	{
		keys.put(Keys.SHIFT, true);
		shiftPressed = true;
	}

	public void quitPressed()
	{
		keys.put(Keys.QUIT, true);
	}

	public void pausePressed()
	{
		keys.put(Keys.PAUSE, true);
	}

	public void spacePressed()
	{
		keys.put(Keys.SELECT, true);
		spacePressed = true;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Keys released
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public boolean keyUp(int keycode)
	{
		if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A)
		{
			this.leftReleased();
		}
		if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D)
		{
			this.rightReleased();
		}
		if (keycode == Input.Keys.UP || keycode == Input.Keys.W)
		{
			this.upReleased();
		}
		if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S)
		{
			this.downReleased();
		}
		if (keycode == Input.Keys.SHIFT_LEFT)
		{
			this.shiftReleased();
		}
		if (keycode == Input.Keys.Q)
		{
			this.quitReleased();
		}
		if (keycode == Input.Keys.P)
		{
			this.pauseReleased();
		}
		if (keycode == Input.Keys.SPACE)
		{
			this.spaceReleased();
		}
		return true;
	}

	public void leftReleased()
	{
		keys.put(Keys.LEFT, false);
		movementKeyPressed = false;
	}

	public void rightReleased()
	{
		keys.put(Keys.RIGHT, false);
		movementKeyPressed = false;
	}

	public void upReleased()
	{
		keys.put(Keys.UP, false);
		movementKeyPressed = false;
	}

	public void downReleased()
	{
		keys.put(Keys.DOWN, false);
		movementKeyPressed = false;
	}
	
	public void shiftReleased()
	{
		keys.put(Keys.SHIFT, false);
		shiftPressed = false;
	}

	public void quitReleased()
	{
		keys.put(Keys.QUIT, false);
	}

	public void pauseReleased()
	{
		keys.put(Keys.PAUSE, false);
	}

	public void spaceReleased()
	{
		keys.put(Keys.SELECT, false);
		spacePressed = false;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Mouse pressed
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		// Gdx.app.debug(TAG, "GameScreen: MOUSE DOWN........: (" + screenX + "," +
		// screenY + ")" );

		if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT)
		{
			this.setClickedMouseCoordinates(screenX, screenY);
		}

		// left is selection, right is context menu
		if (button == Input.Buttons.LEFT)
		{
			this.selectMouseButtonPressed(screenX, screenY);
		}

		if (button == Input.Buttons.RIGHT)
		{
			this.doActionMouseButtonPressed(screenX, screenY);
		}
		return true;
	}

	public void setClickedMouseCoordinates(int x, int y)
	{
		lastMouseCoordinates.set(x, y, 0);
	}

	public void selectMouseButtonPressed(int x, int y)
	{
		mouseButtons.put(Mouse.SELECT, true);
	}

	public void doActionMouseButtonPressed(int x, int y)
	{
		mouseButtons.put(Mouse.DOACTION, true);
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Mouse released
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// left is selection, right is context menu
		if (button == Input.Buttons.LEFT)
		{
			this.selectMouseButtonReleased(screenX, screenY);
		}
		if (button == Input.Buttons.RIGHT)
		{
			this.doActionMouseButtonReleased(screenX, screenY);
		}
		return true;
	}

	public void selectMouseButtonReleased(int x, int y)
	{
		mouseButtons.put(Mouse.SELECT, false);
	}

	public void doActionMouseButtonReleased(int x, int y)
	{
		mouseButtons.put(Mouse.DOACTION, false);
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Unused overrides
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

}
