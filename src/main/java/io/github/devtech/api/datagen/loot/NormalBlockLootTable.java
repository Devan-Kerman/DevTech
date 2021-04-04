package io.github.devtech.api.datagen.loot;

import io.github.devtech.api.datagen.ResourceGenerator;
import io.github.devtech.api.datagen.block.NormalBlockGenerator;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.loot.JLootTable;

import net.minecraft.util.Identifier;

public class NormalBlockLootTable implements ResourceGenerator {
	private final Identifier identifier;

	public NormalBlockLootTable(Identifier identifier) {this.identifier = identifier;}

	@Override
	public void generate(RuntimeResourcePack pack) {
		pack.addLootTable(
				NormalBlockGenerator.fix(this.identifier, "blocks"),
				JLootTable.loot("minecraft:block")
						.pool(JLootTable.pool().rolls(1).entry(JLootTable.entry().type("minecraft:item").name(this.identifier + ""))
								      .condition(JLootTable.condition("minecraft:survives_explosion"))));
	}
}
