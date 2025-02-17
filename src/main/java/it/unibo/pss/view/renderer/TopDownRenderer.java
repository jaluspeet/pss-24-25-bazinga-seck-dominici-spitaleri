package it.unibo.pss.view.renderer;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/** Implements top-down geometry calculations. */
public class TopDownRenderer implements GeometryRenderer {
	private static final double TILE_WIDTH = 64;
	private static final double TILE_HEIGHT = 64;

	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		double left = offsetX + gridX * TILE_WIDTH * scale;
		double top = offsetY + gridY * TILE_HEIGHT * scale;
		double width = TILE_WIDTH * scale;
		double height = TILE_HEIGHT * scale;
		return new Rectangle2D(left, top, width, height);
	}

	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows) {
		double totalWidth = gridCols * TILE_WIDTH;
		double totalHeight = gridRows * TILE_HEIGHT;
		double offsetX = (canvasWidth - totalWidth) / 2.0;
		double offsetY = (canvasHeight - totalHeight) / 2.0;
		return new Point2D(offsetX, offsetY);
	}
}
