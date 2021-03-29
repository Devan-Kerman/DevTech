package io.github.devtech.api.port;

import io.github.astrarre.itemview.v0.api.Serializer;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.itemview.v0.fabric.FabricSerializers;
import io.github.devtech.Devtech;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public abstract class Port {
	public static final Registry<Entry> REGISTRY = FabricRegistryBuilder.createSimple(Entry.class, Devtech.id("port")).buildAndRegister();
	public static final Serializer<Port> SERIALIZER = Serializer.of((t) -> REGISTRY.get(FabricSerializers.IDENTIFIER.read(t.asTag(), "portId"))
			                                                                       .from(t.asTag()), (port) -> {
		NBTagView.Builder builder = NBTagView.builder();
		FabricSerializers.IDENTIFIER.save(builder, "portId", REGISTRY.getId(port.getEntry()));
		port.write(builder);
		return builder;
	});

	public PortColor color;

	public Port(PortColor color) {
		this.color = color;
	}

	/**
	 * deserialize the port from the data
	 */
	public Port(NBTagView tag) {
		this.color = PortColor.forName(tag.getString("color"));
	}

	/**
	 * serialize the port to the tag the key portId stores the id of the port, so do not write to that
	 */
	public void write(NBTagView.Builder tag) {
		tag.putString("color", this.color.name());
	}

	public void tick(BlockEntity entity, Direction face) {}

	public abstract Entry getEntry();

	@Environment (EnvType.CLIENT)
	public abstract SpriteIdentifier getTexture();

	@Environment (EnvType.CLIENT)
	public abstract SpriteIdentifier getBarTexture();

	public interface Entry {
		Port from(NBTagView tag);
	}

	public static class Type {
		public final Entry entry;
		public final PortColor color;

		public Type(Entry entry, PortColor color) {
			this.entry = entry;
			this.color = color;
		}

		public boolean is(Port port) {
			return port.color == this.color && port.getEntry() == this.entry;
		}
	}
}
