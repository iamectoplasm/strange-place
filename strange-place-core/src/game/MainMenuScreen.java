package game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import game.MainGameScreen.GameState;
import game.StrangePlace.ScreenType;
import saves.ProfileManager;
import utility.AssetHandler;

public class MainMenuScreen implements Screen
{
	private Stage stage;
	//private StrangePlace game;

	public MainMenuScreen(final StrangePlace currentGame)
	{
		//this.game = currentGame;

		//creation
		this.stage = new Stage();
		Table table = new Table(AssetHandler.INTRO_SCREEN_SKIN);
		table.setFillParent(true);
		table.setBackground("main_menu_background");

		//Image title = new Image(Utility.STATUSUI_TEXTUREATLAS.findRegion("Strange Place"));
		TextButton newGameButton = new TextButton("New Game", AssetHandler.INTRO_SCREEN_SKIN);
		TextButton loadGameButton = new TextButton("Load Game", AssetHandler.INTRO_SCREEN_SKIN);
		TextButton creditsButton = new TextButton("Credits", AssetHandler.INTRO_SCREEN_SKIN);
		TextButton exitButton = new TextButton("Exit",AssetHandler.INTRO_SCREEN_SKIN);


		//Layout
		//table.add(title).spaceBottom(75).row();
		table.add(newGameButton).spaceBottom(10).row();
		table.add(loadGameButton).spaceBottom(10).row();
		table.add(creditsButton).spaceBottom(10).row();
		table.add(exitButton).spaceBottom(10).row();

		this.stage.addActor(table);

		//Listeners
		newGameButton.addListener(new ClickListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				ProfileManager.getInstance().setIsNewProfile(true);
				currentGame.setScreen(currentGame.getScreenType(ScreenType.MainGame));
				//hide();
				//game.getScreen().show();
			}
		});

		loadGameButton.addListener(new ClickListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				MainGameScreen.setGameState(GameState.LOADING);
				currentGame.setScreen(currentGame.getScreenType(ScreenType.MainGame));
			}
		});

		exitButton.addListener(new ClickListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button)
			{
				Gdx.app.exit();
			}
		});

		creditsButton.addListener(new ClickListener()
		{
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
			{
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button)
			{
				//game.setScreen(game.getScreenType(ScreenType.Credits));
			}
		});
	}
	
	@Override
	public void render(float delta)
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().setScreenSize(width, height);
	}

	@Override
	public void show()
	{
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide()
	{
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause()
	{
		
	}

	@Override
	public void resume()
	{
		
	}

	@Override
	public void dispose()
	{
		stage.dispose();
	}

}