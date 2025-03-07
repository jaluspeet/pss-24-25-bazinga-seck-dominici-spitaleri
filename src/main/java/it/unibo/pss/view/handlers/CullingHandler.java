package it.unibo.pss.view.handlers;

public final class CullingHandler {
	private CullingHandler() {}

	public static boolean isRectVisible(double left, double top, double width, double height, double canvasWidth, double canvasHeight) {
		return left + width >= 0 && left <= canvasWidth && top + height >= 0 && top <= canvasHeight;
	}

	public static int[] computeVisibleGridRegion(double panX, double panY, double canvasWidth, double canvasHeight, double scale, int gridCols, int gridRows, double tileWidth) {
		int minCol = Math.max(0, (int) Math.floor(panX / tileWidth));
		int minRow = Math.max(0, (int) Math.floor(panY / tileWidth));
		int maxCol = Math.min(gridCols, (int) Math.ceil((panX + canvasWidth / scale) / tileWidth));
		int maxRow = Math.min(gridRows, (int) Math.ceil((panY + canvasHeight / scale) / tileWidth));
		return new int[]{minRow, maxRow, minCol, maxCol};
	}
}
