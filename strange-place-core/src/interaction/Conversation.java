package interaction;

import com.badlogic.gdx.Gdx;

public class Conversation {
	private static final String TAG = Conversation.class.getSimpleName();

	private String id;
	private String dialog = "";

	public Conversation() {
		Gdx.app.debug(TAG, "\t\t\tConversation object constructed");
	}

	public String getId() {
		// Gdx.app.debug(TAG, "Method getId() called");
		return id;
	}

	public void setId(String id) {
		// Gdx.app.debug(TAG, "Method setId(" + id + ") called");
		this.id = id;
	}

	public String getDialog() {
		// Gdx.app.debug(TAG, "Method getDialog() called");
		return dialog;
	}

	public void setDialog(String dialog) {
		// Gdx.app.debug(TAG, "Method setDialog(" + dialog + ") called");
		this.dialog = dialog;
	}
}
