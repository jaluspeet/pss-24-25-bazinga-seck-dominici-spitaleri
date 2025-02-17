package it.unibo.pss.view.sprites;

import it.unibo.pss.model.world.World;
import javafx.scene.paint.Color;

/** Provides methods to load world tile sprites. For now, returns colors. */
public class WorldSpriteLoader {
	public Color getTileColor(World.Tile tile) {
		return tile.getType() == World.Tile.TileType.LAND ? Color.GREEN : Color.BLUE;
	}
}
