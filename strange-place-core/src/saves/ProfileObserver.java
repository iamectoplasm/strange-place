package saves;

public interface ProfileObserver
{
	public static enum ProfileEvent
	{
		PROFILE_LOADED, SAVING_PROFILE, CLEAR_CURRENT_PROFILE
	}

	void onProfileNotify(final ProfileManager profileManager, ProfileEvent event);
}
