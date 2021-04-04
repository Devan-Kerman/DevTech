package io.github.devtech.api;

import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.block.HorizontalBlockStateGenerator;
import io.github.devtech.api.port.Port;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public abstract class FacingDevtechMachine extends DevtechMachine {
	public FacingDevtechMachine(Identifier id) {
		super(id);
	}

	public FacingDevtechMachine(String id) {
		super(id);
	}

	public static Direction rotate(Direction target, Direction absolute) {
		if(absolute.getOffsetY() != 0) return absolute;
		if(target.getOffsetY() != 0) return target;
		return realign(target, absolute);
	}

	public static Direction realign(Direction target, Direction absolute) {
		switch (absolute) {
		case NORTH:
			return target;
		case WEST:
			return getClockwise(target).getOpposite();
		case EAST:
			return getClockwise(target);
		case SOUTH:
			return target.getOpposite();
		case UP:
			return getUp(target);
		case DOWN:
			return getUp(target).getOpposite();
		}
		return null;
	}

	public static Direction getClockwise(Direction direction) {
		switch (direction) {
		case UP:
		case NORTH:
			return Direction.EAST;
		case DOWN:
		case SOUTH:
			return Direction.WEST;
		case EAST:
			return Direction.SOUTH;
		case WEST:
			return Direction.NORTH;
		}
		throw new IllegalStateException();
	}

	public static Direction getUp(Direction direction) {
		switch (direction) {
		case DOWN:
			return Direction.NORTH;
		case UP:
			return Direction.SOUTH;
		default:
			return Direction.UP;
		}
	}

	@Override
	public ResourceGenerator[] getGenerators() {
		return ArrayUtils.add(super.getGenerators(), new HorizontalBlockStateGenerator(this.id));
	}

	@Override
	protected Block createBlock(AbstractBlock.Settings settings) {
		return new FacingBlock(settings);
	}

	public Direction relativize(Direction direction, BlockState state) {
		return rotate(direction, state.get(HorizontalFacingBlock.FACING));
	}

	public abstract class FacingBlockEntity extends DefaultBlockEntity {
		@Override
		protected Direction derelativize(Direction relative) {
			return rotate(this.getCachedState().get(HorizontalFacingBlock.FACING), relative);
		}

		@Override
		public Port getPortAbsolute(Direction direction) {
			return this.getPort(FacingDevtechMachine.this.relativize(direction, this.getCachedState()));
		}

	}

	public class FacingBlock extends DefaultBlock {
		public FacingBlock(Settings settings) {
			super(settings);
			this.setDefaultState(this.stateManager.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
		}

		@Nullable
		@Override
		public BlockState getPlacementState(ItemPlacementContext ctx) {
			for (Direction direction : ctx.getPlacementDirections()) {
				if(direction.getOffsetY() == 0) {
					return this.getDefaultState().with(HorizontalFacingBlock.FACING, direction);
				}
			}
			return super.getPlacementState(ctx);
		}

		@Override
		protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
			super.appendProperties(builder.add(HorizontalFacingBlock.FACING));
		}
	}
}
