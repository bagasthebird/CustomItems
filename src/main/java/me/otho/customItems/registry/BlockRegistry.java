package me.otho.customItems.registry;

import java.util.ArrayList;
import java.util.HashMap;

import me.otho.customItems.compability.Integration;
import me.otho.customItems.compability.NEICustomItemsConfig;
import me.otho.customItems.configuration.jsonReaders.blocks.Cfg_block;
import me.otho.customItems.configuration.jsonReaders.blocks.Cfg_blockDrop;
import me.otho.customItems.configuration.jsonReaders.blocks.Cfg_crop;
import me.otho.customItems.configuration.jsonReaders.blocks.Cfg_fluid;
import me.otho.customItems.configuration.jsonReaders.common.Cfg_drop;
import me.otho.customItems.configuration.jsonReaders.entities.Cfg_entityDrop;
import me.otho.customItems.mod.blocks.CustomBlock;
import me.otho.customItems.mod.blocks.CustomButtonBlock;
import me.otho.customItems.mod.blocks.CustomCarpetBlock;
import me.otho.customItems.mod.blocks.CustomCrop;
import me.otho.customItems.mod.blocks.CustomDoorBlock;
import me.otho.customItems.mod.blocks.CustomCrossedBlock;
import me.otho.customItems.mod.blocks.CustomFallingBlock;
import me.otho.customItems.mod.blocks.CustomFenceBlock;
import me.otho.customItems.mod.blocks.CustomFenceBlock;
import me.otho.customItems.mod.blocks.CustomFlowerBlock;
import me.otho.customItems.mod.blocks.CustomFluidBlock;
import me.otho.customItems.mod.blocks.CustomGateBlock;
import me.otho.customItems.mod.blocks.CustomLadderBlock;
import me.otho.customItems.mod.blocks.CustomPaneBlock;
import me.otho.customItems.mod.blocks.CustomPressurePlateBlock;
import me.otho.customItems.mod.blocks.CustomRotatedPillar;
import me.otho.customItems.mod.blocks.CustomSlabBlock;
import me.otho.customItems.mod.blocks.CustomStairsBlock;
import me.otho.customItems.mod.blocks.CustomTorch;
import me.otho.customItems.mod.blocks.CustomTrapDoorBlock;
import me.otho.customItems.mod.blocks.CustomWallBlock;
import me.otho.customItems.mod.blocks.IMMBlock;
import me.otho.customItems.mod.handler.BucketHandler;
import me.otho.customItems.mod.items.CustomBucket;
import me.otho.customItems.mod.items.CustomSeed;
import me.otho.customItems.mod.items.CustomSlabItem;
import me.otho.customItems.mod.materials.CI_Material;
import me.otho.customItems.utility.LogHelper;
import me.otho.customItems.utility.Util;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockRegistry {

    public static HashMap<String, Cfg_blockDrop> drops = new HashMap<String, Cfg_blockDrop>();

    public static boolean registerBlockDrop(Cfg_blockDrop data) {

        String[] parser = data.id.split(":");
        if (parser.length < 3)
            data.id = data.id.concat(":0");

        if (drops.containsKey(data.id)) {

            Cfg_blockDrop drop = drops.get(data.id);

            drop.drops = ArrayUtils.addAll(drop.drops, data.drops);

			drops.put(data.id, drop);			
		}else{
            drops.put(data.id, data);
        }

        return true;
    }

    public static boolean registerBlockDrop(Cfg_blockDrop[] data) {
        int i;

        for (i = 0; i < data.length; i++) {
            boolean registered = registerBlockDrop(data[i]);

            if (!registered) {
                LogHelper.error("Failed to register: Entity drop " + i);
                return false;
            }
        }

        return true;
    }

    public static boolean registerBlock(Cfg_block data) {
        LogHelper.log(Level.INFO, "Register Block: " + data.name, 1);

        data.toolClass = Util.validateToolClass(data.toolClass);

        if (Util.validateType(data.type)) {
            Util.BlockType blockType = Util.BlockType.valueOf(data.type.toUpperCase());
            switch (blockType) {
            // 1.0.10
            case FLOWER:
                registerFlowerBlock(data);
                break;
            case CARPET:
                registerCarpetBlock(data);
                break;
            case TORCH:
                registerTorchBlock(data);
                break;
            case GATE:
                registerGateBlock(data);
                break;
            case DOOR:
                registerDoorBlock(data);
                break;
            case TRAPDOOR:
                registerTrapDoorBlock(data);
                break;
            case LADDER:
                registerLadderBlock(data);
                break;
            case BUTTON:
                registerButtonBlock(data);
                break;
            case LEVER:
                registerLeverBlock(data);
                break;
            case BED:
                registerBedBlock(data);
                break;
            case PRESSUREPLATE:
                registerPressurePlateBlock(data);
                break;
            //pre 1.0.10
            case FENCE:
                registerFenceBlock(data);
                break;
            case LOG:
            case PILLARS:
                registerLogBlock(data);
                break;
            case PANE:
                registerPaneBlock(data);
                break;
            case SLAB:
                registerSlabBlock(data);
                break;
            case STAIRS:
                registerStairsBlock(data);
                break;
            case WALL:
                registerWallBlock(data);
                break;
            case FALLING:
                registerFallingBlock(data);
                break;
            case CROSSED:
                registerCrossedBlock(data);
                break;
            case NORMAL:
            default:
                registerNormalBlock(data);
                break;
            }
        }

        return true;
    }

    public static boolean registerBlock(Cfg_block[] data) {
        int i;

        for (i = 0; i < data.length; i++) {
            boolean registered = registerBlock(data[i]);

            if (!registered) {
                LogHelper.error("Failed to register: Block " + i);
                return false;
            }
        }

        return true;
    }

    public static boolean registerCrop(Cfg_crop data) {
        LogHelper.log(Level.INFO, data.name, 1);

        String registerName = Util.parseRegisterName(data.name);

        int cropRender;

        if (data.renderType.equals("crops")) {
            cropRender = 6;
        } else if (data.renderType.equals("flower")) {
            cropRender = 1;
        } else {
            cropRender = 6;
        }

        CustomCrop crop = new CustomCrop(data.fruitName, cropRender);
        CustomSeed seed = new CustomSeed(crop);
        crop.setSeed(seed);

        crop.setAcceptBoneMeal(data.acceptBoneMeal);
        crop.setDropSeedWhenMature(data.dropSeedWhenMature);
        crop.setEachExtraFruitDropChance(data.eachExtraFruitDropChance);
        crop.setEachExtraSeedDropChance(data.eachExtraSeedDropChance);
        crop.setFruitQuantityDropRange(data.minFruitDrop, data.maxFruitDrop);
        crop.setSeedQuantityDropRange(data.minSeedDrop, data.maxSeedDrop);
        crop.setFruitItemDamage(data.dropFruitDamage);

        crop.setBlockTextureName(data.textureName);

        seed.setTextureName(data.textureName + "_seed");

        GameRegistry.registerBlock(crop, registerName + "_crop");
        crop.setBlockName(Registry.mod_id.toLowerCase() + ":" + registerName + "_crop");
        LanguageRegistry.instance().addStringLocalization(crop.getUnlocalizedName() + ".name", "en_US", data.name);

        GameRegistry.registerItem(seed, registerName + "_seed");
        seed.setUnlocalizedName(Registry.mod_id.toLowerCase() + ":" + registerName + "_seed");
        LanguageRegistry.instance().addStringLocalization(seed.getUnlocalizedName() + ".name", "en_US",
                data.name + " Seeds");

        Registry.itemsList.add(seed);
        Registry.itemsList.add(data.creativeTab);

        if (data.dropFromGrassChance > 0) {
            MinecraftForge.addGrassSeed(new ItemStack(seed), data.dropFromGrassChance);
        }

        if (Integration.isNEI()) {
            NEICustomItemsConfig.addItemToHide(Registry.mod_id + ":" + registerName);
        }
        return true;
    }

    public static boolean registerCrop(Cfg_crop[] data) {
        int i;

        for (i = 0; i < data.length; i++) {
            boolean registered = registerCrop(data[i]);

            if (!registered) {
                LogHelper.error("Failed to register: Crop " + i);
                return false;
            }
        }

        return true;
    }

    public static boolean registerFluid(Cfg_fluid data) {
        LogHelper.log(Level.INFO, data.name, 1);

        String registerName = Util.parseRegisterName(data.name);
        data.luminosity = Util.range(data.luminosity, 0, 15);

        Fluid fluid = new Fluid(registerName);

        fluid.setLuminosity(data.luminosity);
        fluid.setDensity(data.density);
        fluid.setTemperature(data.temperature);
        fluid.setViscosity(data.viscosity);
        fluid.setGaseous(data.isGas);

        FluidRegistry.registerFluid(fluid);

        Material material;
        if (data.material.equals("lava")) {
            material = Material.lava;
        } else {
            material = Material.water;
        }

        CustomFluidBlock fluidBlock = new CustomFluidBlock(fluid, material);

        fluidBlock.setQuantaPerBlock(data.flowLength);

        fluidBlock.setBlockTextureName(data.textureName);

        Registry.blocksList.add(fluidBlock);
        Registry.blocksList.add(data.creativeTab);

        fluidBlock.setBlockName(Registry.mod_id.toLowerCase() + ":" + data.name);
        GameRegistry.registerBlock(fluidBlock, registerName);

        fluid.setUnlocalizedName(
                fluidBlock.getUnlocalizedName().substring(fluidBlock.getUnlocalizedName().indexOf(":") + 1));
        LanguageRegistry.instance().addStringLocalization(fluidBlock.getUnlocalizedName() + ".name", "en_US",
                data.name);
        LanguageRegistry.instance().addStringLocalization(fluid.getUnlocalizedName(), "en_US", data.name);
        fluid.setBlock(fluidBlock);

        if (data.bucket.name == null)
            data.bucket.name = data.name + " Bucket";

        String BucketRegisterName = Util.parseRegisterName(data.bucket.name);

        if (data.bucket.creativeTab == null)
            data.bucket.creativeTab = data.creativeTab;

        if (data.bucket.textureName == null)
            data.bucket.textureName = data.textureName + "_bucket";

        CustomBucket bucket = new CustomBucket(fluidBlock, data.bucket.textureName);

        bucket.setUnlocalizedName(Registry.mod_id.toLowerCase() + ":" + data.bucket.name);
        bucket.setContainerItem(Items.bucket);

        Registry.itemsList.add(bucket);
        Registry.itemsList.add(data.bucket.creativeTab);

        bucket.setTextureName(data.bucket.textureName);
        GameRegistry.registerItem(bucket, BucketRegisterName);

        FluidContainerRegistry.registerFluidContainer(
                FluidRegistry.getFluidStack(fluid.getName(), FluidContainerRegistry.BUCKET_VOLUME),
                new ItemStack(bucket), new ItemStack(Items.bucket));
        LanguageRegistry.instance().addStringLocalization(bucket.getUnlocalizedName() + ".name", "en_US",
                data.bucket.name);
        BucketHandler.INSTANCE.buckets.put(fluidBlock, bucket);
        MinecraftForge.EVENT_BUS.register(BucketHandler.INSTANCE);

        return true;
    }

    public static boolean registerFluid(Cfg_fluid[] data) {
        int i;

        for (i = 0; i < data.length; i++) {
            boolean registered = registerFluid(data[i]);

            if (!registered) {
                LogHelper.error("Failed to register: Fluid " + i);
                return false;
            }
        }

        return true;
    }

    public static void registerPressurePlateBlock(Cfg_block data) {
        CustomPressurePlateBlock block = new CustomPressurePlateBlock(CI_Material.getMaterial(data.material));
        
        block.tickRate  = data.tickRate;

        genericBlockSetup(data, block, true);
    }

    public static void registerSlabBlock(Cfg_block data) {
        String registerName = Util.parseRegisterName(data.name);

        CustomSlabBlock slabBlock = new CustomSlabBlock(false, CI_Material.getMaterial(data.material), registerName);
        CustomSlabBlock doubleBlock = new CustomSlabBlock(true, CI_Material.getMaterial(data.material), registerName);

        genericBlockSetup(data, slabBlock, false);
        genericBlockSetup(data, doubleBlock, false);
        
        //Register slab block
        GameRegistry.registerBlock(slabBlock, CustomSlabItem.class, registerName, slabBlock, doubleBlock, false);
        slabBlock.setBlockName(Registry.mod_id.toLowerCase() + ":" + registerName);
        LanguageRegistry.instance().addStringLocalization(slabBlock.getUnlocalizedName() + ".name", "en_US", data.name);

        //Register double slab block
        GameRegistry.registerBlock(doubleBlock, CustomSlabItem.class, "double_" + registerName, slabBlock, doubleBlock,
                true);
        doubleBlock.setBlockName(Registry.mod_id.toLowerCase() + ":" + "double_" + registerName);
        LanguageRegistry.instance().addStringLocalization(doubleBlock.getUnlocalizedName() + ".name", "en_US",
                data.name);        
        
        //Nei Integration: Hide double slab block
        if (Integration.isNEI()) {
            NEICustomItemsConfig.addItemToHide(Registry.mod_id + ":double_" + registerName);
        }
    }

    public static void registerBedBlock(Cfg_block data) {
        // TODO Auto-generated method stub

    }

    public static void registerLeverBlock(Cfg_block data) {
        // TODO Auto-generated method stub

    }

    public static void registerButtonBlock(Cfg_block data) {
        CustomButtonBlock block = new CustomButtonBlock();
        
        block.tickRate  = data.tickRate;

        genericBlockSetup(data, block, true);
    }

    public static void registerLadderBlock(Cfg_block data) {
        CustomLadderBlock block = new CustomLadderBlock();

        genericBlockSetup(data, block, true);
    }

    public static void registerTrapDoorBlock(Cfg_block data) {
        CustomTrapDoorBlock block = new CustomTrapDoorBlock(CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerDoorBlock(Cfg_block data) {
        CustomDoorBlock block = new CustomDoorBlock(CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerGateBlock(Cfg_block data) {
        CustomGateBlock block = new CustomGateBlock();

        genericBlockSetup(data, block, true);
    }

    public static void registerWallBlock(Cfg_block data) {
        CustomWallBlock block = new CustomWallBlock(new CustomBlock(CI_Material.getMaterial(data.material)));

        genericBlockSetup(data, block, true);
    }

    public static void registerStairsBlock(Cfg_block data) {
        CustomStairsBlock block = new CustomStairsBlock(new CustomBlock(CI_Material.getMaterial(data.material)), 0);

        genericBlockSetup(data, block, true);
    }

    public static void registerPaneBlock(Cfg_block data) {
        CustomPaneBlock block = new CustomPaneBlock("side", "top", Material.glass, true);

        genericBlockSetup(data, block, true);
    }

    public static void registerFallingBlock(Cfg_block data) {
        CustomFallingBlock block = new CustomFallingBlock(CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerNormalBlock(Cfg_block data) {
        CustomBlock block = new CustomBlock(CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerCrossedBlock(Cfg_block data) {
        CustomCrossedBlock block = new CustomCrossedBlock(CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerLogBlock(Cfg_block data) {
        CustomRotatedPillar block = new CustomRotatedPillar(CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerFenceBlock(Cfg_block data) {
        CustomFenceBlock block = new CustomFenceBlock(data.textureName, CI_Material.getMaterial(data.material));

        genericBlockSetup(data, block, true);
    }

    public static void registerTorchBlock(Cfg_block data) {
        CustomTorch block = new CustomTorch();

        genericBlockSetup(data, block, true);
    }

    public static void registerFlowerBlock(Cfg_block data) {
        CustomFlowerBlock block = new CustomFlowerBlock();

        genericBlockSetup(data, block, true);
    }

    public static void registerCarpetBlock(Cfg_block data) {
        CustomCarpetBlock block = new CustomCarpetBlock();

        genericBlockSetup(data, block, true);
    }

    public static void genericBlockSetup(Cfg_block data, IMMBlock block, boolean register) {
        Block mineBlock = (Block) block;

        String registerName = Util.parseRegisterName(data.name);

        block.setHardness(data.hardness);
        block.setResistance(data.resistance);
        block.setBreaks(data.dropsItSelf);
        block.setCanSilkHarvest(data.canSilkHarvest);
        block.setCollides(data.isCollidable);
        data.lightLevel = Util.range(data.lightLevel, 0, 1);

        block.setLightLevel(data.lightLevel);
        if (data.toolClass != null)
            block.setHarvestLevel(data.toolClass, data.harvestLevel);
        if (data.multipleTextures == null) {
            block.setBlockTextureName(data.textureName);
        } else {
            String[] textureNames = new String[6];
            textureNames[0] = data.multipleTextures.yneg;
            textureNames[1] = data.multipleTextures.ypos;
            textureNames[2] = data.multipleTextures.zneg;
            textureNames[3] = data.multipleTextures.zpos;
            textureNames[4] = data.multipleTextures.xneg;
            textureNames[5] = data.multipleTextures.xpos;
            block.registerBlockTextures(textureNames);
        }

        block.setOpaque(data.isOpaque);
        block.setStepSound(Util.parseSoundType(data.stepSound));

        if (data.dropItemName != null) {
            block.setDropItem(data.dropItemName);
            block.setMaxItemDrop(data.maxItemDrop);
            block.setMinItemDrop(data.minItemDrop);
            block.setEachExtraItemDropChance(data.eachExtraItemDropChance);
        }

        Registry.blocksList.add(block);
        Registry.blocksList.add(data.creativeTab);

        // Register Block
        if( register ) {
            GameRegistry.registerBlock(mineBlock, registerName);
            block.setBlockName(Registry.mod_id.toLowerCase() + ":" + registerName);
            LanguageRegistry.instance().addStringLocalization(block.getUnlocalizedName() + ".name", "en_US", data.name);
        }

        mineBlock.slipperiness = data.slipperiness;

        Item itemBlock = Item.getItemFromBlock(mineBlock);        
        if( itemBlock != null) {
            int size = Util.range(data.maxStackSize, 1, 64);
            itemBlock.setMaxStackSize(size);
        }
    }
}
