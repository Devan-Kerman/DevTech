package io.github.devtech.api.port;

import io.github.astrarre.itemview.v0.fabric.FabricViews;
import io.github.devtech.api.DevtechMachine;
import io.github.devtech.api.access.PortAccess;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PortItem extends Item {
	public PortItem() {
		super(new Settings());
	}

	@Override
	public Text getName(ItemStack stack) {
		Port port = Port.SERIALIZER.read(FabricViews.view(stack.getTag()));
		return new TranslatableText(this.getTranslationKey(stack)).formatted(port.getColor().formatting);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		// todo open gui
		return super.use(world, user, hand);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockEntity entity = world.getBlockEntity(pos);
		if(entity instanceof PortAccess) {
			ItemStack stack = context.getStack();
			Port port = Port.SERIALIZER.read(FabricViews.view(stack.getTag()));
			if(port == null) return ActionResult.FAIL;
			PortAccess access = (PortAccess) entity;
			Direction face = context.getSide();
			Port old = access.getPortAbsolute(face);
			if(access.setPort(face, port)) {
				if (old != null) {
					ItemStack oldPortStack = new ItemStack(this);
					CompoundTag tag = (CompoundTag) Port.SERIALIZER.save(old);
					oldPortStack.setTag(tag);
					context.getPlayer().inventory.offerOrDrop(world, oldPortStack);
				}
				stack.decrement(1);
				return ActionResult.CONSUME;
			}
		}
		return super.useOnBlock(context);
	}
}
