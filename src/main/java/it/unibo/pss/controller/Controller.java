package it.unibo.pss.controller;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.controller.observer.ModelObserver;
import it.unibo.pss.controller.observer.ViewObserver;
import it.unibo.pss.model.Model;
import it.unibo.pss.controller.observer.ViewDTO;
import it.unibo.pss.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class Controller extends Application implements ModelObserver, ViewObserver {
	private Model model;
	private View view;

	@Override
	public void start(Stage stage) {
		this.model = new Model(SharedConstants.WORLD_WIDTH, SharedConstants.WORLD_HEIGHT);
		this.model.addObserver(this);
		this.view = new View(stage, SharedConstants.WINDOW_TITLE, SharedConstants.WINDOW_WIDTH, SharedConstants.WINDOW_HEIGHT, new ModelDTO(model.getGrid()), this);
	}

	@Override
	public void onModelUpdated() {
		view.updateModel(new ModelDTO(model.getGrid()));
	}

	public ModelDTO getLatestModelDTO() {
		return new ModelDTO(model.getGrid());
	}

	@Override
	public void onViewAction(ViewDTO viewDTO) {
		switch(viewDTO.getCommand().getType()) {
			case SPEED:
				ViewDTO.SpeedCommand speedCmd = (ViewDTO.SpeedCommand) viewDTO.getCommand();
				int current = model.getUpdateInterval();
				int newInterval = (speedCmd.getDelta() < 0)
					? Math.max(100, current - 100)
					: Math.min(1500, current + 100);
				model.setUpdateInterval(newInterval);
				break;
			default:
				break;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}
}
