package it.unibo.pss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.view.geometry.IsometricRenderer;
import it.unibo.pss.view.geometry.TopDownRenderer;
import it.unibo.pss.view.handlers.CullingHandler;
import it.unibo.pss.view.handlers.PanZoomHandler;
import it.unibo.pss.view.handlers.ViewControlsHandler;
import it.unibo.pss.view.sprites.SpritePathResolver;
import it.unibo.pss.view.views.StackView;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;

public class ViewLayerTest {

	static {
		new JFXPanel();
	}

	// SpritePathResolver sets and returns the proper sprite prefix.
	@Test
	public void testSpritePathResolverMode() {
		SpritePathResolver.setMode("isometric");
		assertEquals("/sprites/isometric", SpritePathResolver.getPrefix(), "Mode should be isometric");
		SpritePathResolver.setMode("topdown");
		assertEquals("/sprites/topdown", SpritePathResolver.getPrefix(), "Mode should be topdown");
	}

	// rectangle fully inside the canvas is reported as visible.
	@Test
	public void testCullingVisibleRect() {
		boolean visible = CullingHandler.isRectVisible(50, 50, 100, 100, 500, 500);
		assertTrue(visible, "Rectangle within canvas should be visible");
	}

	// rectangle completely outside the canvas is not visible.
	@Test
	public void testCullingNonVisibleRect() {
		boolean visible = CullingHandler.isRectVisible(-200, -200, 50, 50, 500, 500);
		assertFalse(visible, "Rectangle outside canvas should not be visible");
	}


	// TopDownRenderer.computeTileRect computes the expected rectangle.
	@Test
	public void testTopDownRendererComputeTileRect() {
		TopDownRenderer renderer = new TopDownRenderer();
		double offsetX = 10;
		double offsetY = 20;
		double scale = 1.0;
		int gridX = 2;
		int gridY = 3;
		double tileWidth = SharedConstants.TILE_WIDTH;
		double tileHeight = SharedConstants.TILE_HEIGHT;
		
		// x = offsetX + gridX*tileWidth, y = offsetY + gridY*(tileHeight*2)
		Rectangle2D expected = new Rectangle2D(
				offsetX + gridX * tileWidth * scale,
				offsetY + gridY * tileHeight * 2 * scale,
				tileWidth * scale,
				tileHeight * 2 * scale);
		Rectangle2D actual = renderer.computeTileRect(gridX, gridY, offsetX, offsetY, scale);
		assertEquals(expected.getMinX(), actual.getMinX(), 0.0001, "MinX should match expected value");
		assertEquals(expected.getMinY(), actual.getMinY(), 0.0001, "MinY should match expected value");
		assertEquals(expected.getWidth(), actual.getWidth(), 0.0001, "Width should match expected value");
		assertEquals(expected.getHeight(), actual.getHeight(), 0.0001, "Height should match expected value");
	}

	// TopDownRenderer.computeCenterOffset returns the proper centered offset.
	@Test
	public void testTopDownRendererComputeCenterOffset() {
		TopDownRenderer renderer = new TopDownRenderer();
		double canvasWidth = 800;
		double canvasHeight = 600;
		int gridCols = 10;
		int gridRows = 10;
		double scale = 1.0;
		double tileWidth = SharedConstants.TILE_WIDTH;
		double tileHeight = SharedConstants.TILE_HEIGHT;
		double gridWidth = gridCols * tileWidth * scale;
		double gridHeight = gridRows * tileHeight * 2 * scale;
		Point2D expected = new Point2D((canvasWidth - gridWidth) / 2.0, (canvasHeight - gridHeight) / 2.0);
		Point2D actual = renderer.computeCenterOffset(canvasWidth, canvasHeight, gridCols, gridRows, scale);
		assertEquals(expected.getX(), actual.getX(), 0.0001, "Center offset X should match");
		assertEquals(expected.getY(), actual.getY(), 0.0001, "Center offset Y should match");
	}

	// Verify that TopDownRenderer.screenToGrid converts a screen point to the correct grid coordinates.
	@Test
	public void testTopDownRendererScreenToGrid() {
		TopDownRenderer renderer = new TopDownRenderer();
		double scale = 1.0;
		
		// Assume camera offset is (0,0) for simplicity.
		Point2D cameraOffset = new Point2D(0, 0);
		double tileWidth = SharedConstants.TILE_WIDTH;
		double tileHeight = SharedConstants.TILE_HEIGHT;
		
		// Pick a point inside the tile at grid (2,3)
		double screenX = 2 * tileWidth * scale + 5;
		double screenY = 3 * (tileHeight * 2 * scale) + 10;
		Point2D screenPoint = new Point2D(screenX, screenY);
		Point2D gridPoint = renderer.screenToGrid(screenPoint, scale, cameraOffset);
		assertEquals(2, gridPoint.getX(), "Screen point should map to grid X coordinate 2");
		assertEquals(3, gridPoint.getY(), "Screen point should map to grid Y coordinate 3");
	}

	// IsometricRenderer.computeTileRect returns a valid, non-null rectangle with positive dimensions.
	@Test
	public void testIsometricRendererComputeTileRect() {
		IsometricRenderer renderer = new IsometricRenderer();
		double offsetX = 50;
		double offsetY = 50;
		double scale = 1.0;
		int gridX = 3;
		int gridY = 4;
		Rectangle2D rect = renderer.computeTileRect(gridX, gridY, offsetX, offsetY, scale);
		assertNotNull(rect, "Isometric computeTileRect should not return null");
		assertTrue(rect.getWidth() > 0 && rect.getHeight() > 0, "Computed rectangle dimensions must be positive");
	}

	// IsometricRenderer.computeCenterOffset returns a non-null offset.
	@Test
	public void testIsometricRendererComputeCenterOffset() {
		IsometricRenderer renderer = new IsometricRenderer();
		double canvasWidth = 800;
		double canvasHeight = 600;
		int gridCols = 10;
		int gridRows = 10;
		double scale = 1.0;
		Point2D offset = renderer.computeCenterOffset(canvasWidth, canvasHeight, gridCols, gridRows, scale);
		assertNotNull(offset, "Center offset computed by IsometricRenderer should not be null");
	}

	// IsometricRenderer.screenToGrid returns plausible grid coordinates.
	@Test
	public void testIsometricRendererScreenToGrid() {
		IsometricRenderer renderer = new IsometricRenderer();
		double scale = 1.0;
		
		// Assume no camera offset.
		Point2D cameraOffset = new Point2D(0, 0);
		Point2D screenPoint = new Point2D(200, 150);
		Point2D gridPoint = renderer.screenToGrid(screenPoint, scale, cameraOffset);
		assertTrue(gridPoint.getX() >= 0, "Grid X coordinate should be non-negative");
		assertTrue(gridPoint.getY() >= 0, "Grid Y coordinate should be non-negative");
	}

	// initial state of PanZoomHandler and its applyCamera method.
	@Test
	public void testPanZoomHandlerInitialState() {
		Canvas canvas = new Canvas(400, 300);
		
		// Create PanZoomHandler with a dummy update callback.
		PanZoomHandler handler = new PanZoomHandler(canvas, () -> {});
		
		// Default values should be scale=1 and pan offsets=0.
		assertEquals(1.0, handler.getScale(), 0.0001, "Initial scale should be 1.0");
		assertEquals(0.0, handler.getPanX(), 0.0001, "Initial panX should be 0.0");
		assertEquals(0.0, handler.getPanY(), 0.0001, "Initial panY should be 0.0");
		
		// Applying camera transformation with no pan should return the original offset.
		Point2D baseOffset = new Point2D(100, 100);
		Point2D transformed = handler.applyCamera(baseOffset);
		assertEquals(baseOffset.getX(), transformed.getX(), 0.0001, "Camera-applied X should equal base offset X when no pan is applied");
		assertEquals(baseOffset.getY(), transformed.getY(), 0.0001, "Camera-applied Y should equal base offset Y when no pan is applied");
	}

	// Validate toggle view mode in ViewControlsHandler.
	@Test
	public void testToggleViewMode() {
		
		// Create a dummy StackView with an initial isometric renderer.
		StackView<StackView.Renderable> stackView = new StackView<>(800, 600, true);
		
		// Ensure the initial renderer is isometric.
		stackView.setGeometryRenderer(new IsometricRenderer());
		ViewControlsHandler controlsHandler = new ViewControlsHandler(stackView);
		
		// Initially, the geometry renderer should be an instance of IsometricRenderer.
		assertTrue(stackView.getGeometryRenderer() instanceof IsometricRenderer, "Initial geometry renderer should be IsometricRenderer");
		
		// Toggle view mode: IsometricRenderer --> TopDownRenderer.
		controlsHandler.toggleViewMode();
		assertTrue(stackView.getGeometryRenderer() instanceof it.unibo.pss.view.geometry.TopDownRenderer, "After toggling, geometry renderer should be TopDownRenderer");
		
		// TopDownRenderer --> IsometricRenderer
		controlsHandler.toggleViewMode();
		assertTrue(stackView.getGeometryRenderer() instanceof IsometricRenderer, "After second toggle, geometry renderer should be IsometricRenderer again");
	}
}

