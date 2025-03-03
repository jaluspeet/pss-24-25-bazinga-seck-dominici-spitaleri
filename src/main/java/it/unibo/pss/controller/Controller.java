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
		ModelDTO initialDTO = model.getLatestModelDTO();

		this.view = new View(
				stage,
				SharedConstants.WINDOW_TITLE,
				SharedConstants.WINDOW_WIDTH,
				SharedConstants.WINDOW_HEIGHT,
				initialDTO,
				this
				);
	}

	@Override
	public void onModelUpdated() {
		view.updateModel(model.getLatestModelDTO());
	}

	public ModelDTO getLatestModelDTO() {
		return model.getLatestModelDTO();
	}

	@Override
	public void onViewAction(ViewDTO viewDTO) {
		switch (viewDTO.getCommand().getType()) {
			case SPEED -> {
				ViewDTO.SpeedCommand speedCmd = (ViewDTO.SpeedCommand) viewDTO.getCommand();
				int current = model.getUpdateInterval();
				int newInterval = (speedCmd.getDelta() < 0)
					? Math.max(100, current - 100)
					: Math.min(1500, current + 100);
				model.setUpdateInterval(newInterval);
			}
			case TILE_CLICK -> {
				ViewDTO.EntityTileClickCommand clickCmd = (ViewDTO.EntityTileClickCommand) viewDTO.getCommand();
				String actions = model.getTileActions(clickCmd.getTileX(), clickCmd.getTileY());
				view.setActionText(actions);
			}
			default -> {
			}
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
