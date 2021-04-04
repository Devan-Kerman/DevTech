package io.github.devtech.api.port;

import java.util.Objects;

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

	public boolean isValid = true;
	private PortColor color;
	private Port.Type type;

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

	public void invalidate() {this.isValid = false;}

	public abstract Entry getEntry();

	@Environment (EnvType.CLIENT)
	public abstract SpriteIdentifier getTexture();

	@Environment (EnvType.CLIENT)
	public abstract SpriteIdentifier getBarTexture();

	public Port.Type getType() {
		if(this.type == null) this.type = new Port.Type(this.getEntry(), this.getColor());
		return this.type;
	}

	public PortColor getColor() {
		return this.color;
	}

	public Port setColor(PortColor color) {
		this.color = color;
		this.type = null;
		return this;
	}

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

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (!(o instanceof Type)) {
				return false;
			}

			Type type = (Type) o;

			if (!Objects.equals(this.entry, type.entry)) {
				return false;
			}
			return this.color == type.color;
		}

		@Override
		public int hashCode() {
			int result = this.entry != null ? this.entry.hashCode() : 0;
			result = 31 * result + (this.color != null ? this.color.hashCode() : 0);
			return result;
		}
	}
}
