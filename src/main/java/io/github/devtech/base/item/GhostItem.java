package io.github.devtech.base.item;

import java.util.List;

import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.networking.v0.api.ModPacketHandler;
import io.github.astrarre.util.v0.api.Id;
import io.github.devtech.Devtech;
import io.github.devtech.api.registry.DLang;
import io.github.ladysnake.pal.AbilitySource;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.PlayerAbility;
import io.github.ladysnake.pal.SimpleAbilityTracker;
import io.github.ladysnake.pal.VanillaAbilities;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GhostItem extends Item {
	public static final Id NOCLIP_CHANNEL_ID = Devtech.id2("noclip");
	public static final AbilitySource GHOST_ITEM = Pal.getAbilitySource(Devtech.id("ghost_item"));
	public static final PlayerAbility NO_CLIP = Pal.registerAbility("minecraft", "no_clip", NoClipAbilityTracker::new);

	static {
		ModPacketHandler.INSTANCE.registerSynchronizedClient(NOCLIP_CHANNEL_ID, (id, tag) -> {
			PlayerEntity entity = MinecraftClient.getInstance().player;
			if (entity != null) {
				boolean state = tag.getBool("state");
				entity.noClip = state;
				entity.setOnGround(!state);
			}
		});

		ModPacketHandler.INSTANCE.registerSynchronizedServer(NOCLIP_CHANNEL_ID, (member, id, tag) -> {
			ServerPlayerEntity entity = member.to();
			if(entity.isCreativeLevelTwoOp()) {
				boolean state = tag.getBool("state");
				entity.noClip = state;
				entity.setOnGround(!state);
			}
		});
	}

	public GhostItem() {
		super(new Settings().rarity(Rarity.EPIC).maxCount(1));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(DLang.ELDRICH_PHANTOM_CORE_DESC);
		tooltip.add(DLang.ELDRICH_PHANTOM_CORE_DESC_0);
		tooltip.add(DLang.ELDRICH_PHANTOM_CORE_DESC_1);
		super.appendTooltip(stack, world, tooltip, context);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag != null && tag.getBoolean("active");
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (!world.isClient && stack.getItem() == this) {
			stack = stack.copy();
			if (GHOST_ITEM.grants(user, VanillaAbilities.ALLOW_FLYING)) {
				GHOST_ITEM.revokeFrom(user, VanillaAbilities.ALLOW_FLYING);
				GHOST_ITEM.revokeFrom(user, VanillaAbilities.FLYING);
				GHOST_ITEM.revokeFrom(user, NO_CLIP);
				stack.getOrCreateTag().putBoolean("active", false);
			} else {
				GHOST_ITEM.grantTo(user, VanillaAbilities.ALLOW_FLYING);
				GHOST_ITEM.grantTo(user, VanillaAbilities.FLYING);
				GHOST_ITEM.grantTo(user, NO_CLIP);
				user.teleport(user.getX(), user.getY() + .25, user.getZ());
				stack.getOrCreateTag().putBoolean("active", true);
			}
			world.playSound(user, user.getBlockPos(), SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.MASTER, 1f, 1f);
		}
		return TypedActionResult.consume(stack);
	}

	public static final class NoClipAbilityTracker extends SimpleAbilityTracker {
		public NoClipAbilityTracker(PlayerAbility ability, PlayerEntity player) {
			super(ability, player);
		}

		public void checkConflict() {
			boolean enabled = this.isEnabled();
			this.updateBacking(this.shouldBeEnabled()); // avoid false positives from gamemode changes
			boolean expected = this.isEnabled();
			if (enabled != expected) {
				// Attempt to satisfy both compliant and rogue mods
				// If the external state and the Pal state are conflicting, one of them tries to make this ability enabled
				this.updateState(true);
				Devtech.LOGGER.warn("Player ability {} was updated externally (expected {}, was {}).",
						this.ability.getId(),
						expected ? "enabled" : "disabled",
						enabled ? "enabled" : "disabled",
						new RuntimeException("stacktrace"));
			}
		}

		protected void updateBacking(boolean enabled) {
			this.player.noClip = enabled;
		}

		@Override
		protected void updateState(boolean enabled) {
			super.updateState(enabled);
			this.updateBacking(enabled);
		}

		@Override
		protected void sync() {
			if (this.player.world.isClient) {
				ModPacketHandler.INSTANCE.sendToServer(NOCLIP_CHANNEL_ID, NBTagView.builder().putBool("state", this.isEnabled()).build());
			} else if (this.player instanceof ServerPlayerEntity && ((ServerPlayerEntity)this.player).networkHandler != null) {
				ModPacketHandler.INSTANCE.sendToClient((ServerPlayerEntity) this.player,
						NOCLIP_CHANNEL_ID,
						NBTagView.builder().putBool("state", this.isEnabled()).build());
			}
		}

		@Override
		public boolean isEnabled() {
			return this.player.noClip;
		}
	}

}
