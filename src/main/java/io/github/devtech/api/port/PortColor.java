package io.github.devtech.api.port;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Formatting;

public enum PortColor {
	NONE(0, Formatting.WHITE),
	RED(0xff0000, Formatting.RED),
	YELLOW(0xffff00, Formatting.YELLOW),
	GREEN(0x00ff00, Formatting.YELLOW),
	CYAN(0x00ffff, Formatting.AQUA),
	BLUE(0x0000ff, Formatting.BLUE),
	PURPLE(0xff00ff, Formatting.LIGHT_PURPLE);

	public static final List<PortColor> COLORS = ImmutableList.copyOf(values());
	public static final Map<String, PortColor> COLORS_BY_NAME = Arrays.stream(values())
			                                                            .collect(ImmutableMap.toImmutableMap(Enum::name, Function.identity()));
	public final int color;
	public final Formatting formatting;

	PortColor(int color, Formatting formatting) {
		this.color = color | 0xff000000;
		this.formatting = formatting;
	}

	public static PortColor forName(String color) {
		if (color == null) {
			return NONE;
		}
		return COLORS_BY_NAME.getOrDefault(color, NONE);
	}

	public PortColor next() {
		return COLORS.get((this.ordinal() + 1) % COLORS.size());
	}

	public Tag write() {
		return StringTag.of(this.name());
	}
}
