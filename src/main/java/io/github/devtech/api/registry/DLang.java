package io.github.devtech.api.registry;

import java.util.HashMap;
import java.util.Map;

import io.github.astrarre.gui.v0.api.DrawableRegistry;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public interface DLang {
	Map<String, String> EN_US = new HashMap<>();
	Text FILLER = Text.of("");
	Text HOLD_SHIFT_FOR_MORE = translate("tooltip.devtech.hold_shift_for_more_info", "Hold shift for more information")
			                           .formatted(Formatting.DARK_GRAY, Formatting.ITALIC);
	Text HOLD_SHIFT_AND_CTRL_FOR_TIPS = translate("tooltip.devtech.hold_shift_ctrl_for_tips", "Hold shift+ctrl for tips")
			                                    .formatted(Formatting.DARK_GRAY, Formatting.ITALIC);
	Text TORQUE = translate("tooltip.devtech.torque", "Current Torque");
	Text TORQUE_PIN = translate("tooltip.devtech.torque_pin", "Required Torque");
	Text TORQUE_EXCESS_HELPER = translate("tooltip.devtech.torque_excess_helper", "(use a gearbox to convert to RPM for faster processing)")
			.formatted(Formatting.DARK_GRAY, Formatting.ITALIC);
	Text TORQUE_EXCESS = translate("tooltip.devtech.torque_excess", "Excess torque ").append(TORQUE_EXCESS_HELPER);
	Text INTEGRATED_PORTS = translate("tooltip.devtech.integrated_ports", "Integrated Ports:").formatted(Formatting.GRAY);
	Text MECHANICAL_LOOM_DESC = translate("tooltip.devtech.mechanical_loom_description", "Turn base fabric into items").formatted(Formatting.GRAY);
	Text BASIC_ALLOY_KILN_DESC = translate("tooltip.devtech.basic_alloy_kiln_description", "Melts together multiple ingots into one");
	Text INPUT_ROTATIONAL_ENERGY_PORT = translate("port.devtech.rotational_energy.input", "Input Rotating Port");
	Text INPUT_ITEM_PORT = translate("port.devtech.item.input", "Input Item Port");
	Text OUTPUT_ITEM_PORT = translate("port.devtech.item.output", "Output Item Port");
	@Deprecated
	Text RPT_HELPER = translate("tooltip.devtech.rpm.helper", "(rotations per tick)")
			.formatted(Formatting.DARK_GRAY, Formatting.ITALIC);
	@Deprecated
	Text RPT = translate("tooltip.devtech.rpt", "%3.3f RPT ").append(RPT_HELPER);
	Text BASIC_ALLOY_KILN = new TranslatableText(DMachines.PAT.block.getTranslationKey());
	Text SOLAR_COOKER = new TranslatableText(DBlocks.SOLAR_COOKER.getTranslationKey());
	Text SOLAR_COOKER_DESC = translate("tooltip.devtech.solar_cooker_description", "A solar powered furnace. Higher skylight level = Faster Cooking. Peek speed is equal to furnace");
	Text ELDRICH_PHANTOM_CORE_DESC = translate("tooltip.devtech.epc_desc", "When activated (r-click), grants the player:").formatted(Formatting.GRAY);
	Text ELDRICH_PHANTOM_CORE_DESC_0 = translate("tooltip.devtech.epc_desc_0", "   + creative-like flight").formatted(Formatting.GRAY);
	Text ELDRICH_PHANTOM_CORE_DESC_1 = translate("tooltip.devtech.epc_desc_1", "   + noClip (phase through blocks like spectator mode)").formatted(Formatting.GRAY);
	Text AIR_JUMP_ITEM_DESC = translate("tooltip.aji_desc", "Allows the player to jump in air").formatted(Formatting.GRAY);

	static Text rpt(float rpm) {
		return new TranslatableText("tooltip.devtech.rpt", rpm).append(RPT_HELPER);
	}

	static MutableText translate(String key, String en_us) {
		EN_US.put(key, en_us);
		return new TranslatableText(key);
	}
}
