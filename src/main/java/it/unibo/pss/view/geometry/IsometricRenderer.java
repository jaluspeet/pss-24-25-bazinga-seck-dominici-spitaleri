package it.unibo.pss.view.geometry;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import it.unibo.pss.common.SharedConstants;

/**
 * Implementation of the GeometryRenderer interface that renders in isometric perspective.
 */
public class IsometricRenderer implements GeometryRenderer {

	/**
	 * Returns the isometric coordinates of the given grid coordinates.
	 *
	 * @param gridX The x-coordinate of the grid.
	 * @param gridY The y-coordinate of the grid.
	 * @param offsetX The x-offset of the grid.
	 * @param offsetY The y-offset of the grid.
	 * @return The isometric coordinates of the given grid coordinates.
	 */
	private Point2D getIsometricCoordinates(int gridX, int gridY, double offsetX, double offsetY) {
		return new Point2D((gridX - gridY) * (SharedConstants.TILE_WIDTH / 2.0) + offsetX, (gridX + gridY) * (SharedConstants.TILE_HEIGHT / 2.0) + offsetY - SharedConstants.TILE_HEIGHT);
	}

	/**
	 * Returns the grid coordinates of the given isometric coordinates.
	 *
	 * @param isoX The x-coordinate of the isometric coordinates.
	 * @param isoY The y-coordinate of the isometric coordinates.
	 * @param offsetX The x-offset of the grid.
	 * @param offsetY The y-offset of the grid.
	 * @return The grid coordinates of the given isometric coordinates.
	 */
	@Override
	public Rectangle2D computeTileRect(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Point2D isoCoords = getIsometricCoordinates(gridX, gridY, offsetX, offsetY);
		double width = SharedConstants.SPRITE_WIDTH * scale;
		double height = SharedConstants.SPRITE_HEIGHT * scale;
		return new Rectangle2D(isoCoords.getX() * scale - width / 2, isoCoords.getY() * scale - height + (SharedConstants.SPRITE_HEIGHT / 2 * scale), width, height);
	}

	/**
	 * Returns the center offset for the given canvas dimensions and grid dimensions.
	 *
	 * @param canvasWidth The width of the canvas.
	 * @param canvasHeight The height of the canvas.
	 * @param gridCols The number of columns in the grid.
	 * @param gridRows The number of rows in the grid.
	 * @param scale The scale of the grid.
	 * @return The center offset for the given canvas dimensions and grid dimensions.
	 */
	@Override
	public Point2D computeCenterOffset(double canvasWidth, double canvasHeight, int gridCols, int gridRows, double scale) {
		double centerTileX = (gridCols - 1) / 2.0;
		double centerTileY = (gridRows - 1) / 2.0;
		return new Point2D(canvasWidth / 2.0 - (centerTileX - centerTileY) * (SharedConstants.TILE_WIDTH / 2.0) * scale, canvasHeight / 2.0 - (centerTileX + centerTileY) * (SharedConstants.TILE_HEIGHT / 2.0) * scale);
	}

	/**
	 * Returns the grid coordinates of the given screen coordinates.
	 *
	 * @param screenPoint The screen coordinates.
	 * @param scale The scale of the grid.
	 * @param cameraOffset The offset of the camera.
	 * @return The grid coordinates of the given screen coordinates.
	 */
	@Override
	public Point2D screenToGrid(Point2D screenPoint, double scale, Point2D cameraOffset) {
		double A = SharedConstants.TILE_WIDTH / 2.0;
		double B = SharedConstants.TILE_HEIGHT / 2.0;
		double Xprime = (screenPoint.getX() / scale) - cameraOffset.getX();
		double Yprime = (screenPoint.getY() / scale) - cameraOffset.getY() + SharedConstants.TILE_HEIGHT;
		int gridX = (int) Math.floor(((Xprime / A) + (Yprime / B)) / 2);
		int gridY = (int) Math.floor(((Yprime / B) - (Xprime / A)) / 2);
		return new Point2D(gridX, gridY);
	}

	@Override
	public double[][] computeTileOutline(int gridX, int gridY, double offsetX, double offsetY, double scale) {
		Point2D iso = getIsometricCoordinates(gridX, gridY, offsetX, offsetY);
		double x = iso.getX() * scale;
		double y = iso.getY() * scale;
		double halfWidth = (SharedConstants.TILE_WIDTH / 2.0) * scale;
		double halfHeight = (SharedConstants.TILE_HEIGHT / 2.0) * scale;
		double[] xs = { x, x + halfWidth, x, x - halfWidth };
		double[] ys = { y, y + halfHeight, y + SharedConstants.TILE_HEIGHT * scale, y + halfHeight };
		return new double[][] { xs, ys };
	}
}
