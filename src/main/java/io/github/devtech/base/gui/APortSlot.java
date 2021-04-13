package io.github.devtech.base.gui;

import java.util.Objects;

import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.astrarre.rendering.v0.api.Graphics3d;
import io.github.astrarre.util.v0.api.Id;
import io.github.devtech.api.DevtechMachine;
import io.github.devtech.api.port.Port;
import io.github.devtech.api.port.PortColor;
import io.github.devtech.api.registry.DDrawables;

import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class APortSlot extends ASlot {
	public final DevtechMachine.DefaultBlockEntity entity;
	public final int port;
	private Port actualPort;
	public APortSlot(DevtechMachine.DefaultBlockEntity entity, int port) {
		super(DDrawables.PORT_SLOT, (Inventory) entity.sortedPorts.get(port), 0);
		this.entity = entity;
		this.port = port;
	}

	public APortSlot(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
		this.entity = null;
		this.port = -1;
	}

	@Override
	protected void renderBackground(Graphics3d graphics, float tickDelta) {
		super.renderBackground(graphics, tickDelta);
		if(this.actualPort != null && this.actualPort.getColor() != PortColor.NONE) {
			int rgb = this.actualPort.getColor().color & 0x44ffffff;
			graphics.fillRect(17, 0, 1, 18, rgb);
			graphics.fillRect(0, 17, 17, 1, rgb);
		}
	}

	@Override
	protected Inventory readInventoryData(NBTagView input) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world != null && Objects.equals(client.world.getRegistryKey().getValue(), Serializer.ID.read(input, "world"))) {
			World world = client.world;
			BlockPos pos = FabricSerializers.BLOCK_POS.read(input, "pos");
			Port port = ((DevtechMachine.DefaultBlockEntity)world.getBlockEntity(pos)).sortedPorts.get(input.getInt("port"));
			this.actualPort = port;
			return (Inventory) port;
		}
		return null;
	}

	@Override
	protected void writeInventoryData(NBTagView.Builder output, Inventory inventory) {
		Serializer.ID.save(output, "world", Id.of(this.entity.getWorld().getRegistryKey().getValue()));
		FabricSerializers.BLOCK_POS.save(output, "pos", this.entity.getPos());
		output.putInt("port", this.port);
	}
}
