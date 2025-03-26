package it.unibo.bazinga.controller;

import it.unibo.bazinga.common.SharedConstants;
import it.unibo.bazinga.controller.observer.ModelDTO;
import it.unibo.bazinga.controller.observer.ModelObserver;
import it.unibo.bazinga.controller.observer.ViewObserver;
import it.unibo.bazinga.model.Model;
import it.unibo.bazinga.controller.observer.ViewDTO;
import it.unibo.bazinga.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Controller class that manages the communication between the Model and the View
 * in order to keep strict MVC separation.
 *
 * The Controller class is responsible for:
 * - creating the Model and the View
 * - updating the View when the Model changes
 * - updating the Model when the View sends commands
 * - starting the application
 */
public class Controller extends Application implements ModelObserver, ViewObserver {
	private Model model;
	private View view;

	/**
	 * Start the application by creating the Model and the View.
	 *
	 * @param stage the primary stage for the application
	 */
	@Override
	public void start(Stage stage) {
		this.model = new Model(SharedConstants.WORLD_WIDTH, SharedConstants.WORLD_HEIGHT);
		this.model.addObserver(this);
		this.view = new View(stage, SharedConstants.WINDOW_TITLE, SharedConstants.WINDOW_WIDTH, SharedConstants.WINDOW_HEIGHT, this );
	}

	/**
	 * Update the View when the Model changes.
	 */
	@Override
	public void onModelUpdated() {
		ModelDTO latestDTO = model.getLatestModelDTO();
		view.updateModel(latestDTO);
	}

	public ModelDTO getLatestModelDTO() { return model.getLatestModelDTO(); }

	/**
	 * Update the Model when the View sends commands.
	 *
	 * @param viewDTO the command sent by the View
	 */
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

	public Model getModel() { return model; }
	public void setModel(Model model) { this.model = model; }
	public View getView() { return view; }
	public void setView(View view) { this.view = view; }

	/**
	 * Start the application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) { launch(args); }

}
