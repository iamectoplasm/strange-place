package utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public final class AssetHandler
{
	private static final String TAG = AssetHandler.class.getSimpleName();
	
	public final static AssetManager assetManager = new AssetManager();
	private static InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();
	
	public final static String INVENTORY_TEXTURE_ATLAS_PATH = "skins/inventoryui.atlas";
	public final static String INVENTORY_SKIN_PATH = "skins/inventoryui.json";
	//public final static String INVENTORY_TEXTURE_ATLAS_PATH = "skins/inventoryui2.atlas";
	//public final static String INVENTORY_SKIN_PATH = "skins/inventoryui2.json";
	
	public final static String CONVERSATION_TEXTURE_ATLAS_PATH = "skins/conversationui.atlas";
	public final static String CONVERSATION_SKIN_PATH = "skins/conversationui.json";
	
	public final static String ITEMBAR_TEXTURE_ATLAS_PATH = "skins/itembarui.atlas";
	public final static String ITEMBAR_SKIN_PATH = "skins/itembarui.json";
	
	public final static String ITEMS_TEXTURE_ATLAS_PATH = "skins/items.atlas";
	public final static String ITEMS_SKIN_PATH = "skins/items.json";
	
	public final static String INTRO_SCREEN_TEXTURE_ATLAS_PATH = "skins/introscreenui.atlas";
	public final static String INTRO_SCREEN_SKIN_PATH = "skins/introscreenui.json";

	public static TextureAtlas INVENTORY_TEXTUREATLAS = new TextureAtlas(INVENTORY_TEXTURE_ATLAS_PATH);
	public static TextureAtlas CONVERSATION_TEXTUREATLAS = new TextureAtlas(CONVERSATION_TEXTURE_ATLAS_PATH);
	public static TextureAtlas ITEMBAR_TEXTUREATLAS = new TextureAtlas(ITEMBAR_TEXTURE_ATLAS_PATH);
	
	public static TextureAtlas ITEMS_TEXTUREATLAS = new TextureAtlas(ITEMS_TEXTURE_ATLAS_PATH);
	public static TextureAtlas INTRO_SCREEN_TEXTUREATLAS = new TextureAtlas(INTRO_SCREEN_TEXTURE_ATLAS_PATH);

	public static Skin INVENTORY_SKIN = new Skin(Gdx.files.internal(INVENTORY_SKIN_PATH), INVENTORY_TEXTUREATLAS);
	public static Skin CONVERSATION_SKIN = new Skin(Gdx.files.internal(CONVERSATION_SKIN_PATH), CONVERSATION_TEXTUREATLAS);
	public static Skin ITEMBAR_SKIN = new Skin(Gdx.files.internal(ITEMBAR_SKIN_PATH), ITEMBAR_TEXTUREATLAS);
	
	public static Skin INTRO_SCREEN_SKIN = new Skin(Gdx.files.internal(INTRO_SCREEN_SKIN_PATH), INTRO_SCREEN_TEXTUREATLAS);

	public static Vector2 roundVector(Vector2 vector)
	{
		float roundedX = (float) (Math.round(vector.x * Math.pow(10, 2)) / Math.pow(10, 2));
		float roundedY = (float) (Math.round(vector.y * Math.pow(10, 2)) / Math.pow(10, 2));

		return new Vector2(roundedX, roundedY);
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Map Assets
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	
	public static void loadMapAsset(String mapFilenamePath)
	{
		if (mapFilenamePath == null || mapFilenamePath.isEmpty())
		{
			return;
		}
		
		if(assetManager.isLoaded(mapFilenamePath))
		{
			return;
		}

		Gdx.app.debug(TAG, "Resolved path: " + filePathResolver.resolve(mapFilenamePath));
		boolean mapExists = filePathResolver.resolve(mapFilenamePath).exists();
		if (mapExists)
		{
			TmxMapLoader mapLoader = new TmxMapLoader(filePathResolver);
			
			assetManager.setLoader(TiledMap.class, mapLoader);

			assetManager.load(mapFilenamePath, TiledMap.class);

			// Until we add in a loading screen, block until we load map
			assetManager.finishLoadingAsset(mapFilenamePath);
			Gdx.app.debug(TAG, "Map loaded: " + mapFilenamePath);
		}
		else
		{
			Gdx.app.debug(TAG, "Map does not exist: " + mapFilenamePath);
		}
	}

	public static TiledMap getMapAsset(String mapFilenamePath)
	{
		TiledMap map = null;

		// Once the asset manager is done loading
		boolean mapIsLoaded = assetManager.isLoaded(mapFilenamePath);
		if(mapIsLoaded)
		{
			map = assetManager.get(mapFilenamePath, TiledMap.class);
		}
		
		else
		{
			Gdx.app.debug(TAG, "Map is not loaded: " + mapFilenamePath);
		}

		return map;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - Texture Assets
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public static void loadTextureAsset(String textureFilenamePath)
	{
		if (textureFilenamePath == null || textureFilenamePath.isEmpty())
		{
			return;
		}

		// Load asset
		boolean textureExists = filePathResolver.resolve(textureFilenamePath).exists();
		if(textureExists)
		{
			assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));

			assetManager.load(textureFilenamePath, Texture.class);

			// Until we add a loading screen, block until we load the map
			assetManager.finishLoadingAsset(textureFilenamePath);
		}
		else
		{
			Gdx.app.debug(TAG, "Texture does not exist: " + textureFilenamePath);
		}
	}

	public static Texture getTextureAsset(String textureFilenamePath)
	{
		Texture texture = null;

		// Once the asset manager is done loading
		boolean textureIsLoaded = assetManager.isLoaded(textureFilenamePath);
		if(textureIsLoaded)
		{
			texture = assetManager.get(textureFilenamePath, Texture.class);
		}
		else
		{
			Gdx.app.debug(TAG, "Texture is not loaded: " + textureFilenamePath);
		}

		return texture;
	}

	/*
	 * = = = = = = = = = = = = = = = = = = = =
	 * 
	 * - General asset loading methods
	 * 
	 * = = = = = = = = = = = = = = = = = = = =
	 */
	public static int numberAssetsQueued()
	{
		return assetManager.getQueuedAssets();
	}

	public static boolean updateAssetLoading()
	{
		return assetManager.update();
	}

	public static float loadCompleted()
	{
		return assetManager.getProgress();
	}

	public static boolean isAssetLoaded(String fileName)
	{
		return assetManager.isLoaded(fileName);
	}

	public static void unloadAsset(String assetFilenamePath)
	{
		boolean assetManagerIsLoaded = assetManager.isLoaded(assetFilenamePath);
		// Once the asset manager is done loading
		if (assetManagerIsLoaded)
		{
			assetManager.unload(assetFilenamePath);
		}
		else
		{
			Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload at " + assetFilenamePath);
		}
	}
	
	public static Texture combineTextures(Texture texture1, Texture texture2)
	{
		texture1.getTextureData().prepare();
		Pixmap pixmap1 = texture1.getTextureData().consumePixmap();

	    texture2.getTextureData().prepare();
	    Pixmap pixmap2 = texture2.getTextureData().consumePixmap();

	    pixmap1.drawPixmap(pixmap2, 0, 0);
	    Texture textureResult = new Texture(pixmap1);

	    pixmap1.dispose();
	    pixmap2.dispose();

	    return textureResult;
	}
}
