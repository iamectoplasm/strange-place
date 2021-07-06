package saves;

import java.util.Enumeration;
import java.util.Hashtable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class ProfileManager extends ProfileSubject
{
	private static final String TAG = ProfileManager.class.getSimpleName();
	
	private Json json;
	private static ProfileManager profileManager;
	private Hashtable<String, FileHandle> profiles = null;
	private ObjectMap<String, Object> profileProperties = new ObjectMap<String, Object>();
	private String profileName;
	private boolean isNewProfile = false;

	private static final String SAVEGAME_SUFFIX = ".sav";
	public static final String DEFAULT_PROFILE = "default";

	private ProfileManager()
	{
		this.json = new Json();
		this.profiles = new Hashtable<String, FileHandle>();
		this.profileName = DEFAULT_PROFILE;

		profiles.clear();
		storeAllProfiles();
	}

	public static final ProfileManager getInstance()
	{
		if (profileManager == null)
		{
			profileManager = new ProfileManager();
		}

		return profileManager;
	}

	public Array<String> getProfileList()
	{
		Array<String> profileStrings = new Array<String>();
		for (Enumeration<String> e = profiles.keys(); e.hasMoreElements();)
		{
			profileStrings.add(e.nextElement());
		}

		return profileStrings;
	}

	public FileHandle getProfileFile(String profile)
	{
		if (!doesProfileExist(profile))
		{
			return null;
		}

		return profiles.get(profile);
	}

	public void storeAllProfiles()
	{
		if (Gdx.files.isLocalStorageAvailable())
		{
			FileHandle[] files = Gdx.files.local(".").list(SAVEGAME_SUFFIX);
			for (FileHandle file : files)
			{
				profiles.put(file.nameWithoutExtension(), file);
			}
		}
		
		else
			return;
	}

	public boolean doesProfileExist(String profileName)
	{
		return profiles.containsKey(profileName);
	}

	public void writeProfileToStorage(String profileName, String fileData, boolean overwrite)
	{
		String fullFileName = profileName + SAVEGAME_SUFFIX;

		boolean localFileExists = Gdx.files.internal(fullFileName).exists();

		// If we cannot overwrite and the file exists, exit
		if (localFileExists && !overwrite)
		{
			return;
		}

		FileHandle file = null;

		if (Gdx.files.isLocalStorageAvailable())
		{
			file = Gdx.files.local(fullFileName);
			file.writeString(fileData, !overwrite);
		}
		
		profiles.put(profileName, file);
	}

	public void setProperty(String key, Object object)
	{
		profileProperties.put(key, object);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T getProperty(String key, Class<T> type)
	{
		T property = null;
		if (!profileProperties.containsKey(key))
		{
			return property;
		}

		property = (T) profileProperties.get(key);
		return property;
	}

	public void saveProfile()
	{
		notify(this, ProfileObserver.ProfileEvent.SAVING_PROFILE);
		String text = json.prettyPrint(json.toJson(profileProperties));
		writeProfileToStorage(profileName, text, true);
	}

	@SuppressWarnings("unchecked")
	public void loadProfile()
	{
		if (isNewProfile)
		{
			notify(this, ProfileObserver.ProfileEvent.CLEAR_CURRENT_PROFILE);
			ProfileManager.getInstance().saveProfile();
		}

		String fullProfileName = profileName + SAVEGAME_SUFFIX;
		boolean doesProfileExist = Gdx.files.internal(fullProfileName).exists();

		if (!doesProfileExist)
		{
			Gdx.app.debug(TAG, "File doesn't exist!");
			return;
		}

		profileProperties = json.fromJson(ObjectMap.class, profiles.get(profileName));
		notify(this, ProfileObserver.ProfileEvent.PROFILE_LOADED);
		isNewProfile = false;
	}

	public void setCurrentProfile(String profileToSet)
	{
		if (doesProfileExist(profileToSet))
		{
			profileName = profileToSet;
		}
		else
			profileName = DEFAULT_PROFILE;
	}

	public void setIsNewProfile(boolean isNewProfile)
	{
		this.isNewProfile = isNewProfile;
	}

	public boolean getIsNewProfile()
	{
		return this.isNewProfile;
	}
}
