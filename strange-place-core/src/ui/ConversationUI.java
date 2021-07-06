package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import configs.CharConfig;
import interaction.Conversation;
import interaction.ConversationChoice;
import interaction.ConversationGraph;
import utility.AssetHandler;

public class ConversationUI extends Window
{
	private static final String TAG = ConversationUI.class.getSimpleName();

	private Label dialogText;
	private List<ConversationChoice> listItems;
	private ScrollPane scrollPane;
	private TextButton closeButton;
	private Label entityNameLabel;
	
	private ConversationGraph graph;
	private String currentEntityName = "";
	private Json json;

	public ConversationUI() {
		super("", AssetHandler.CONVERSATION_SKIN, "dialog-window");

		Gdx.app.debug(TAG, "\t\tConversationUI object constructed");

		json = new Json();
		graph = new ConversationGraph();

		// Create
		dialogText = new Label("No conversation", AssetHandler.CONVERSATION_SKIN);
		dialogText.setWrap(true);
		dialogText.setAlignment(Align.topLeft);
		dialogText.setColor(Color.BLACK);
		dialogText.setWidth(200);
		
		//Gdx.app.debug(TAG, "Current entity name: " + currentEntityName);
		
		entityNameLabel = new Label("ENTITY", AssetHandler.CONVERSATION_SKIN, "conversation-name-label");

		closeButton = new TextButton("X", AssetHandler.CONVERSATION_SKIN, "conversation-exit");

		listItems = new List<ConversationChoice>(AssetHandler.CONVERSATION_SKIN);

		this.scrollPane = new ScrollPane(listItems, AssetHandler.CONVERSATION_SKIN, "conversation-choice-pane");
		scrollPane.setWidth(listItems.getWidth());
		scrollPane.setHeight(listItems.getHeight() * listItems.getItems().size);
		//Gdx.app.debug(TAG, "scroll pane height: " + scrollPane.getHeight());
		
		this.setResizable(true);

		// Layout
		this.add(entityNameLabel);
		this.add();
		this.add(closeButton);
		this.row();

		//this.defaults().center();
		this.defaults().expand().fill();
		this.add(dialogText);
		//this.add(dialogText).pad(10, 10, 10, 10);
		//this.add(dialogText).pad(20, 20, 20, 20);
		this.row();
		//this.add(scrollPane).pad(10, 10, 10, 10);
		this.add(scrollPane).pad(20, 20, 20, 20);

		//this.debug();
		this.pack();

		// Listeners
		listItems.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ConversationChoice choice = listItems.getSelected();
				if (choice == null)
					return;

				graph.notifyConversationGraphObservers(graph, choice.getConversationCommandEvent());

				populateConversationDialog(choice.getDestinationId());
			}
		});
	}

	public TextButton getCloseButton()
	{
		// Gdx.app.debug(TAG, "Method getCloseButton() called");
		return closeButton;
	}

	public String getCurrentEntityName()
	{
		// Gdx.app.debug(TAG, "Method getCurrentEntityId() called");
		return currentEntityName;
	}

	// Utility method I wrote while debugging why the ConversationUI won't show up
	public boolean getIsVisible()
	{
		return this.isVisible();
	}

	// Utility method that handles loading the conversation script file and
	// initializing the ConversationGraph object for this dialog window
	public void loadConversation(CharConfig entityConfig)
	{
		// Gdx.app.debug(TAG, "Method loadConversation(" + entityConfig.toString() + ")
		// called");

		String fullFilenamePath = entityConfig.getConversationConfigPath();
		this.setName("");

		clearDialog();

		if (fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists()) {
			Gdx.app.debug(TAG, "Conversation file does not exist!");
			return;
		}

		currentEntityName = entityConfig.getEntityName();
		//this.setName(entityConfig.getEntityName());

		ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));
		setConversationGraph(graph);
	}
	
	// Utility method that handles loading the conversation script file and
	// initializing the ConversationGraph object for this dialog window
	public void loadConversation(String npcName, String conversationConfigPath)
	{
		String fullFilenamePath = conversationConfigPath;
		this.setName("");

		clearDialog();

		if (fullFilenamePath.isEmpty() || !Gdx.files.internal(fullFilenamePath).exists()) {
			Gdx.app.debug(TAG, "Conversation file does not exist!");
			return;
		}

		currentEntityName = npcName;
		//this.setName(entityConfig.getEntityName());

		ConversationGraph graph = json.fromJson(ConversationGraph.class, Gdx.files.internal(fullFilenamePath));
		setConversationGraph(graph);
	}

	public void setConversationGraph(ConversationGraph newGraph) {
		// Gdx.app.debug(TAG, "Method setConversationGraph(" + newGraph.toString() + ")
		// called");

		if (graph != null)
			graph.removeAllConversationGraphObservers();

		this.graph = newGraph;
		populateConversationDialog(graph.getCurrentConversationId());
	}

	public ConversationGraph getCurrentConversationGraph()
	{
		// Gdx.app.debug(TAG, "Method getCurrentConversationGraph() called");

		return this.graph;
	}

	// Primary driver that manages the business logic for this class.
	// Takes an ID that represents the vertex ID of the Conversation logic.
	private void populateConversationDialog(String conversationId)
	{
		// Gdx.app.debug(TAG, "Method populateConversationDialog(" + conversationId + ")
		// called");

		clearDialog();

		Conversation conversation = graph.getConversationById(conversationId);
		if (conversation == null)
			return;

		graph.setCurrentConversation(conversationId);
		dialogText.setText(conversation.getDialog());
		entityNameLabel.setText(currentEntityName);
		Array<ConversationChoice> choices = graph.getCurrentChoices();

		if (choices == null)
			return;

		listItems.setItems(choices);
		listItems.setSelectedIndex(-1);
		
		//scrollPane.setHeight(listItems.getHeight());
		//this.pack();
	}

	private void clearDialog()
	{
		// Gdx.app.debug(TAG, "Method clearDialog() called");

		dialogText.setText("");
		listItems.clearItems();
	}
}
