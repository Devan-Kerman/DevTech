package io.github.devtech.base.item;

import java.util.List;
import java.util.Random;

import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketItem;
import io.github.devtech.api.registry.DLang;
import io.github.devtech.mixin.LivingEntityAccess;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class AirJumpItem extends TrinketItem {
	public static final DustParticleEffect DUST_PARTICLE_EFFECT = new DustParticleEffect(1, 1, 1, 1);
	@Environment (EnvType.CLIENT) private int clientCooldown;

	public AirJumpItem() {
		super(new Settings().rarity(Rarity.UNCOMMON));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(DLang.AIR_JUMP_ITEM_DESC);
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public boolean canWearInSlot(String group, String slot) {
		return group.equals(SlotGroups.FEET) && slot.equals(Slots.AGLET);
	}

	@Override
	public void tick(PlayerEntity player, ItemStack stack) {
		if(player.noClip) return;
		boolean isJumping = ((LivingEntityAccess) player).isJumping();
		if (isJumping && player.world.isClient && this.clientCooldown <= 0) {
			Random random = player.world.random;
			for (int i = 0; i < 100; i++) {
				float x = random.nextInt() % 100, y = random.nextInt() % 100;
				float normalSquared = MathHelper.fastInverseSqrt(x * x + y * y);
				x *= normalSquared;
				y *= normalSquared;
				player.world.addImportantParticle(DUST_PARTICLE_EFFECT, player.getX() + x, player.getY() - .1f, player.getZ() + y, 0, 0, 0);
			}
			player.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 2f, 1f);
			this.clientCooldown = 20;
		}
		this.clientCooldown--;
		player.setOnGround(true);
	}
}
