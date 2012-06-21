package com.ezest.javafx.components.tokeninput;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Callback;

public class TokenInputSearchControl<ItemType> extends VBox{

	private  TextField searchField;
	private Popup popup;
    private ListView<ItemType> availableList;
    private ObservableList<ItemType> chosenItems;
    private ObservableList<Cell> chosenItemCells;
    private NodeFactory<ItemType> nodeFactory;
    private EventHandler<ActionEvent> searchEvent;
    
    private TokenInputSearchControl<ItemType> content ;
    private VBox itemsVBox ;
    
    
	public TokenInputSearchControl(){
		super();
		this.content = this;
		setPadding(new Insets(3));
		setSpacing(3);
		getStyleClass().add("tokenInput");
		itemsVBox = new VBox();
		getChildren().add(itemsVBox);
		configureSearchField();
	}

	private void configureSearchField() {
		HBox hb = new HBox();
		Image searchImg = new Image(getClass().getResourceAsStream("/images/tokeninput/search.png"));
		ImageView imgView = new ImageView(searchImg);
		imgView.setFitHeight(30);
		imgView.setFitWidth(30);
		imgView.setSmooth(true);
		
		chosenItems = FXCollections.observableArrayList();
        chosenItemCells = FXCollections.observableArrayList();
        
		searchField = new TextField();
		searchField.getStyleClass().add("tokenInput-text-field");
		searchField.setPrefHeight(30);
		searchField.setPromptText("Select Search Query..");
		searchField.textProperty().addListener(new ChangeListener<String>()	{
			public void changed(ObservableValue<? extends String> source, String oldValue, String newValue)
			{
				if (newValue != null && newValue.trim().length() > 0 && !popup.isShowing())
				{
					showPopup();
				}
			}
		});
		        
		hb.getChildren().addAll(imgView, searchField);
		HBox.setHgrow(searchField, Priority.ALWAYS);
		getChildren().add(hb);
		
		createPopup();
	}
	
	public void setCellFactory(Callback<ListView<ItemType>, ListCell<ItemType>> callback)
	{
		availableList.setCellFactory(callback);
	}

	public ObservableList<ItemType> getAvailableItems()
	{
		return availableList.getItems();
	}

	public ObservableList<ItemType> getChosenItems()
	{
		return chosenItems;
	}

	public MultipleSelectionModel<ItemType> getAvailableItemsSelectionModel()
	{
		return availableList.getSelectionModel();
	}

	public void setNodeFactory(NodeFactory<ItemType> nodeFactory)
	{
		this.nodeFactory = nodeFactory;
	}

	public Property<String> inputText()
	{
		return searchField.textProperty();
	}
	
	
	
	private void showPopup() {
		Parent parent = getParent();
		Bounds childBounds = getBoundsInParent();
		Bounds parentBounds = parent.localToScene(parent.getBoundsInLocal());
		double layoutX = childBounds.getMinX() + parentBounds.getMinX() + parent.getScene().getX() + parent.getScene().getWindow().getX();
		double layoutY = childBounds.getMaxY() + parentBounds.getMinY() + parent.getScene().getY() + parent.getScene().getWindow().getY();
		popup.show(this, layoutX, layoutY);
	}
	
    protected void listItemChosen()
    {
        ObservableList<ItemType> selectedItems = availableList.getSelectionModel().getSelectedItems();
        if (selectedItems != null && selectedItems.size() > 0 && selectedItems.get(0) != null)
        {
            ItemType selected = selectedItems.get(0);
            Cell cell = new Cell(selected);
            chosenItemCells.add(cell);
            chosenItems.add(selected);
            //content.getChildren().add(content.getChildren().size() - 1, cell);
            itemsVBox.getChildren().add(cell);
            layout();
            popup.hide();
            if(searchEvent!=null){
            	searchEvent.handle(null);
            }
        }
    }

    protected void cancelCurrent()
    {
        popup.hide();
    }

    
    public EventHandler<ActionEvent> getSearchEvent() {
		return searchEvent;
	}

	public void setSearchEvent(EventHandler<ActionEvent> searchEvent) {
		this.searchEvent = searchEvent;
	}

	protected void createPopup()
    {
        popup = new Popup();
        availableList = new ListView<ItemType>();
        availableList.getStyleClass().add("drop-shadow");
        availableList.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent mouseEvent)
            {
                listItemChosen();
            }
        });

        availableList.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            public void handle(KeyEvent keyEvent)
            {
                if (keyEvent.getCode().equals(KeyCode.ENTER))
                {
                    listItemChosen();
                }
                else if (keyEvent.getCode().equals(KeyCode.ESCAPE))
                {
                    cancelCurrent();
                }
            }
        });

        popup.showingProperty().addListener(new ChangeListener<Boolean>()
        {
            public void changed(ObservableValue<? extends Boolean> source, Boolean oldValue, Boolean newValue)
            {
                if (!newValue)
                {
                    searchField.setText("");
                }
            }
        });

        popup.getContent().add(availableList);
        popup.setAutoHide(true);
    }


	private class Cell extends HBox
	{
	    private ImageView deleteButton;

	    private Cell(final ItemType item)
	    {
	        setSpacing(6);
	        setMinHeight(24);
	        setPrefHeight(24);
	        StringBuilder builder = new StringBuilder()
	                .append("-fx-border-color: #CCD5E4;")
	                .append("-fx-border-radius: 5;")
	                .append("-fx-padding: 2 5 2 5;")
	                .append("-fx-background-radius: 5;")
	                .append("-fx-background-color: #EFF2F7;)");
	        setStyle(builder.toString());

	        Node node;
	        if (nodeFactory != null)
	        {
	            node = nodeFactory.createNode(item);
	        }
	        else
	        {
	            node = new Label(String.valueOf(item));
	        }
	        StackPane sp = new StackPane();
	        sp.getChildren().add(node);
	        sp.setAlignment(Pos.CENTER_LEFT);
	        
	        getChildren().add(sp);
	        HBox.setHgrow(sp, Priority.ALWAYS);

	        deleteButton = new ImageView(new Image(getClass().getResourceAsStream("/images/tokeninput/cross-script.png")));
	        deleteButton.setCursor(Cursor.HAND);
	        deleteButton.setStyle("-fx-padding: 0; -fx-text-fill: blue");
	        Tooltip.install(deleteButton, new Tooltip("Remove"));
	        deleteButton.setOnMouseClicked(new EventHandler<MouseEvent>()
	        {
	            public void handle(MouseEvent event)
	            {
	                chosenItemCells.remove(Cell.this);
	                chosenItems.remove(item);
	                //content.getChildren().remove(Cell.this);
	                itemsVBox.getChildren().remove(Cell.this);
	                layout();
	                if(searchEvent!=null){
	                	searchEvent.handle(null);
	                }
	            }
	        });
	        getChildren().add(deleteButton);
	    }
	}

}




interface NodeFactory<DataType>
{
Node createNode(DataType data);
}