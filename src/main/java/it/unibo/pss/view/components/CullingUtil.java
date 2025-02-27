package it.unibo.pss.view.components;

// methods for computing the visible grid region
public final class CullingUtil {
	private CullingUtil() { }

	// check if a rectangle is visible
	public static boolean isRectVisible(double left, double top, double width, double height, double canvasWidth, double canvasHeight) {
		return !(left + width < 0 || left > canvasWidth || top + height < 0 || top > canvasHeight);
	}

	// compute the visible grid region
	public static int[] computeVisibleGridRegion(double panX, double panY, double canvasWidth, double canvasHeight, double scale, int gridCols, int gridRows, int tileSize) {
		double worldWidth = canvasWidth / scale;
		double worldHeight = canvasHeight / scale;
		int minCol = (int) Math.floor(panX / tileSize);
		int minRow = (int) Math.floor(panY / tileSize);
		int maxCol = (int) Math.ceil((panX + worldWidth) / tileSize);
		int maxRow = (int) Math.ceil((panY + worldHeight) / tileSize);
		minCol = Math.max(0, minCol);
		minRow = Math.max(0, minRow);
		maxCol = Math.min(gridCols, maxCol);
		maxRow = Math.min(gridRows, maxRow);
		return new int[]{minRow, maxRow, minCol, maxCol};
	}
}
