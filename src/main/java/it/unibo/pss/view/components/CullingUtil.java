package it.unibo.pss.view.components;

public final class CullingUtil {
	private CullingUtil() {}

	public static boolean isRectVisible(double left, double top, double width, double height, double canvasWidth, double canvasHeight) {
		return left + width >= 0 && left <= canvasWidth && top + height >= 0 && top <= canvasHeight;
	}

	public static int[] computeVisibleGridRegion(double panX, double panY, double canvasWidth, double canvasHeight, double scale, int gridCols, int gridRows, int tileSize) {
		int minCol = Math.max(0, (int) Math.floor(panX / tileSize));
		int minRow = Math.max(0, (int) Math.floor(panY / tileSize));
		int maxCol = Math.min(gridCols, (int) Math.ceil((panX + canvasWidth / scale) / tileSize));
		int maxRow = Math.min(gridRows, (int) Math.ceil((panY + canvasHeight / scale) / tileSize));
		return new int[]{minRow, maxRow, minCol, maxCol};
	}
}
