package it.unibo.bazinga.view.handlers;

/**
 * Utility class for handling culling operations.
 */
public final class CullingHandler {
	private CullingHandler() {}

	/**
	 * Checks if a rectangle is visible on the canvas.
	 *
	 * @param left        the left coordinate of the rectangle
	 * @param top         the top coordinate of the rectangle
	 * @param width       the width of the rectangle
	 * @param height      the height of the rectangle
	 * @param canvasWidth the width of the canvas
	 * @param canvasHeight the height of the canvas
	 * @return true if the rectangle is visible, false otherwise
	 */
	public static boolean isRectVisible(double left, double top, double width, double height, double canvasWidth, double canvasHeight) {
		return left + width >= 0 && left <= canvasWidth && top + height >= 0 && top <= canvasHeight;
	}

	/**
	 * Computes the visible region of the grid on the canvas.
	 *
	 * @param panX       the x coordinate of the pan
	 * @param panY       the y coordinate of the pan
	 * @param canvasWidth the width of the canvas
	 * @param canvasHeight the height of the canvas
	 * @param scale      the scale of the grid
	 * @param gridCols   the number of columns of the grid
	 * @param gridRows   the number of rows of the grid
	 * @param tileWidth  the width of a tile
	 * @return an array containing the minRow, maxRow, minCol and maxCol of the visible region
	 */
	public static int[] computeVisibleGridRegion(double panX, double panY, double canvasWidth, double canvasHeight, double scale, int gridCols, int gridRows, double tileWidth) {
		int minCol = Math.max(0, (int) Math.floor(panX / tileWidth));
		int minRow = Math.max(0, (int) Math.floor(panY / tileWidth));
		int maxCol = Math.min(gridCols, (int) Math.ceil((panX + canvasWidth / scale) / tileWidth));
		int maxRow = Math.min(gridRows, (int) Math.ceil((panY + canvasHeight / scale) / tileWidth));
		return new int[]{minRow, maxRow, minCol, maxCol};
	}
}
