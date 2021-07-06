package game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class StrangePlace extends Game
{
	public static MainMenuScreen menuScreen;
	public static MainGameScreen gameScreen;
	
	public static enum ScreenType
	{
		MainMenu,
		MainGame,
		LoadGame,
		NewGame,
		Credits
	}
	
	public Screen getScreenType(ScreenType screenType)
	{
		switch(screenType)
		{
		case MainMenu:
				return menuScreen;
			case MainGame:
				return gameScreen;
			//case LoadGame:
			//	return loadGameScreen;
			//case NewGame:
			//	return newGameScreen;
			//case Credits:
			//	return creditScreen;
			default:
				return menuScreen;
		}

	}


	@Override
	public void create()
	{
		gameScreen = new MainGameScreen(this);
		menuScreen = new MainMenuScreen(this);
		
		//setScreen(gameScreen);
		setScreen(menuScreen);
	}

	@Override
	public void dispose()
	{
		gameScreen.dispose();
		menuScreen.dispose();
	}
}