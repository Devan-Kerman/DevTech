package io.github.devtech.client.model;

import java.util.EnumMap;
import java.util.Map;

import io.github.devtech.api.FacingDevtechMachine;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class CubeData {
	public final Map<Direction, SpriteIdentifier> identifiers = new EnumMap<>(Direction.class);

	public CubeData() {
	}

	/**
	 * makes north this direction
	 */
	public CubeData rotate(Direction direction) {
		CubeData data = new CubeData();
		data.identifiers.put(direction, this.identifiers.get(Direction.NORTH));
		Direction clockwise = FacingDevtechMachine.getClockwise(direction);
		data.identifiers.put(clockwise, this.identifiers.get(Direction.EAST));
		data.identifiers.put(clockwise.getOpposite(), this.identifiers.get(Direction.WEST));
		data.identifiers.put(direction.getOpposite(), this.identifiers.get(Direction.SOUTH));
		Direction up = FacingDevtechMachine.getUp(direction);
		data.identifiers.put(up, this.identifiers.get(Direction.UP));
		data.identifiers.put(up.getOpposite(), this.identifiers.get(Direction.DOWN));
		return data;
	}


	public CubeData withBlock(Direction direction, Identifier identifier) {
		return this.with(direction, new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier));
	}

	public CubeData with(Direction direction, SpriteIdentifier identifier) {
		CubeData cubeData = new CubeData();
		cubeData.identifiers.putAll(this.identifiers);
		cubeData.identifiers.put(direction, identifier);
		return cubeData;
	}

	public static CubeData withAll(Identifier identifier) {
		CubeData data = new CubeData();
		for (int i = 0; i < 6; i++) {
			data.identifiers.put(Direction.byId(i), new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier));
		}
		return data;
	}

	public CubeData withNorthBlock(Identifier identifier) {
		return this.withBlock(Direction.NORTH, identifier);
	}

	public CubeData withNorth(SpriteIdentifier identifier) {
		return this.with(Direction.NORTH, identifier);
	}

	public CubeData withSouthBlock(Identifier identifier) {
		return this.withBlock(Direction.SOUTH, identifier);
	}

	public CubeData withSouth(SpriteIdentifier identifier) {
		return this.with(Direction.SOUTH, identifier);
	}

	public CubeData withWestBlock(Identifier identifier) {
		return this.withBlock(Direction.WEST, identifier);
	}

	public CubeData withWest(SpriteIdentifier identifier) {
		return this.with(Direction.WEST, identifier);
	}

	public CubeData withEastBlock(Identifier identifier) {
		return this.withBlock(Direction.EAST, identifier);
	}

	public CubeData withEast(SpriteIdentifier identifier) {
		return this.with(Direction.EAST, identifier);
	}

	public CubeData withUpBlock(Identifier identifier) {
		return this.withBlock(Direction.UP, identifier);
	}

	public CubeData withUp(SpriteIdentifier identifier) {
		return this.with(Direction.UP, identifier);
	}

	public CubeData withDownBlock(Identifier identifier) {
		return this.withBlock(Direction.DOWN, identifier);
	}

	public CubeData withDown(SpriteIdentifier identifier) {
		return this.with(Direction.DOWN, identifier);
	}

	public SpriteIdentifier get(Direction direction) {
		return this.identifiers.get(direction);
	}
}
