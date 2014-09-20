package com.Otho.customItems.mod.items.tools;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import com.Otho.customItems.mod.creativeTab.customItemsTab;
import com.Otho.customItems.util.logHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CustomSword extends ItemSword {
    public CustomSword(ToolMaterial mat) {
        super(mat);
        this.setCreativeTab(customItemsTab.customItemsTab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".")+1));
    }
    
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
    {
    	if(stack.getItemDamage() == stack.getMaxDamage())
    	{
    		return true;
    	}else
    	{
    		return false;
    	}
        
    }
    
}
