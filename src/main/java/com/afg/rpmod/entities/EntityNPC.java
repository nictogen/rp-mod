package com.afg.rpmod.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class EntityNPC extends EntityLiving{

	double posXF = 0, posYF = 0, posZF = 0;

	public EntityNPC(World worldIn) {
		super(worldIn);
	}

	public void onEntityUpdate()
	{
		this.world.profiler.startSection("entityBaseTick");

		if (this.isRiding())
		{
			this.dismountRidingEntity();
		}

		if(this.posXF == 0 && this.posYF == 0 && this.posZF == 0){
			this.posXF = this.posX;
			this.posYF = this.posY;
			this.posZF = this.posZ;
		}

		this.posX = posXF;
		this.posY = posYF;
		this.posZ = posZF;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotationYaw = this.rotationYaw;

		this.extinguish();

		if (this.posY < -64.0D)
		{
			this.setDead();
		}

		if (!this.world.isRemote)
		{
			this.setFlag(0, false);
		}

		this.firstUpdate = false;
		this.world.profiler.endSection();
		this.world.profiler.startSection("livingEntityBaseTick");


		boolean flag1 = true;

		if (this.getHealth() <= 0.0F)
		{
			this.onDeathUpdate();
		}
		this.prevRenderYawOffset = this.renderYawOffset;
		this.prevRotationYawHead = this.rotationYawHead;
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
		this.world.profiler.endSection();
		super.onEntityUpdate();
	}

	public abstract boolean interact(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand);


	protected boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
	{
		if(stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.COMMAND_BLOCK))
			this.setDead();
		return this.interact(player, stack, hand);
	}

	@Override
	public boolean canBeCollidedWith()
	{
		return true;
	}


	@Override
	public void onLivingUpdate()
	{
//		super.onLivingUpdate();
	}


	@Override
	protected void damageEntity(DamageSource damageSrc, float damageAmount)
	{
//		super.damageEntity(damageSrc, damageAmount);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		return false;
//		return super.attackEntityFrom(source, amount);
	}

	@Override
	public void knockBack(Entity entityIn, float strenght, double xRatio, double zRatio)
	{
//		super.knockBack(entityIn, strenght, xRatio, zRatio);
	}
	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return new ArrayList<ItemStack>();
	}

	@Override
	@Nullable
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return null;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn,
			@Nullable ItemStack stack) {
	}

	@Override
	public EnumHandSide getPrimaryHand() {
		return EnumHandSide.RIGHT;
	}

}
