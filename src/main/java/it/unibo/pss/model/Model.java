package it.unibo.pss.model;

/** Manages the ecosystem simulation model. */
public class Model {

	private final WorldGrid grid;

	public Model(int width, int height) {
		this.grid = WorldGridGenerator.generateGrid(width, height);
	}

	public WorldGrid getGrid() {
		return grid;
	}
}
