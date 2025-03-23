package it.unibo.pss.view.handlers;

import it.unibo.pss.controller.observer.ModelDTO;
import it.unibo.pss.controller.observer.ViewDTO;
import it.unibo.pss.controller.observer.ViewObserver;
import it.unibo.pss.view.views.StackView;
import it.unibo.pss.view.views.WorldView;
import javafx.geometry.Point2D;

public class MouseHandler {
	private final StackView<?> viewport;
	private final WorldView worldView;
	private final ViewObserver viewObserver;
	private ModelDTO modelDTO;

	public MouseHandler(StackView<?> viewport, WorldView worldView, ViewObserver viewObserver) {
		this.viewport = viewport;
		this.worldView = worldView;
		this.viewObserver = viewObserver;
		attachMouseHandler();
	}

	/**
	 * Updates the current model so that mouse events use the latest grid information.
	 */
	public void setModelDTO(ModelDTO modelDTO) {
		this.modelDTO = modelDTO;
	}

	/**
	 * Attaches a mouse-click event to the viewport.
	 */
	private void attachMouseHandler() {
		viewport.setOnMouseClicked(e -> {
			if (modelDTO == null) return;
			Point2D localPoint = viewport.sceneToLocal(e.getSceneX(), e.getSceneY());
			double scale = viewport.getCamera().getScale();
			Point2D cameraOffset = CameraOffsetHandler.computeCameraOffset(viewport.getGeometryRenderer(), viewport.getCamera(), viewport.getWidth(), viewport.getHeight(), modelDTO.getGrid().getWidth(), modelDTO.getGrid().getHeight());
			Point2D gridCoords = viewport.getGeometryRenderer().screenToGrid(localPoint, scale, cameraOffset);
			int tileX = (int) gridCoords.getX();
			int tileY = (int) gridCoords.getY();

			if (tileX >= 0 && tileY >= 0 && tileX < modelDTO.getGrid().getWidth() && tileY < modelDTO.getGrid().getHeight()) {
				worldView.setHighlightedTile(tileX, tileY);
				viewObserver.onViewAction(new ViewDTO(new ViewDTO.EntityTileClickCommand(tileX, tileY)));
			}
		});
	}
}

