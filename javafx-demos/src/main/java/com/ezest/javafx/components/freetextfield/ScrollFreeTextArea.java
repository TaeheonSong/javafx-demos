package com.ezest.javafx.components.freetextfield;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.GroupBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.StackPaneBuilder;

public class ScrollFreeTextArea extends StackPane{

	private Label label;
	private StackPane lblContainer ;
	private TextArea textArea;
	
	private Character NEW_LINE_CHAR = new Character((char)10);
	private final double NEW_LINE_HEIGHT = 18D;
	private final double TOP_PADDING = 3D;
	private final double BOTTOM_PADDING = 6D;
	
	public ScrollFreeTextArea(){
		super();
		configure();
	}
	
	public ScrollFreeTextArea(String text){
		super();
		configure();
		textArea.setText(text);
	}
	
	private void configure(){
		setAlignment(Pos.TOP_LEFT);
		
		this.textArea =new TextArea();
		this.textArea.setWrapText(true);
		this.textArea.getStyleClass().add("scroll-free-text-area");
		
		
		this.label =new Label();
		this.label.setWrapText(true);
		this.label.prefWidthProperty().bind(this.textArea.widthProperty());
		this.label.textProperty().bind(this.textArea.textProperty());
		 
		this.lblContainer = StackPaneBuilder.create()
										  .alignment(Pos.TOP_LEFT)
										  .padding(new Insets(4,7,7,7))
										  .children(label)
										  .build();
		// Binding the container width to the TextArea width.
		lblContainer.maxWidthProperty().bind(textArea.widthProperty());
		
		textArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> paramObservableValue,	String paramT1, String value) {
				layoutForNewLine(textArea.getText());
			}
		});
		
		label.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> paramObservableValue,	Number paramT1, Number paramT2) {
				layoutForNewLine(textArea.getText());
			}
		});
		
		getChildren().addAll(GroupBuilder.create().children(lblContainer).build(),textArea);
	}
	
	private void layoutForNewLine(String text){
		if(text!=null && text.length()>0 && 
					((Character)text.charAt(text.length()-1)).equals(NEW_LINE_CHAR)){ 
			textArea.setPrefHeight(label.getHeight() + NEW_LINE_HEIGHT + TOP_PADDING + BOTTOM_PADDING);
			textArea.setMinHeight(label.getHeight() + NEW_LINE_HEIGHT + TOP_PADDING + BOTTOM_PADDING);
		}else{
			textArea.setPrefHeight(label.getHeight() + TOP_PADDING + BOTTOM_PADDING);
			textArea.setMinHeight(label.getHeight() + TOP_PADDING + BOTTOM_PADDING);
		}
	}
	
}

