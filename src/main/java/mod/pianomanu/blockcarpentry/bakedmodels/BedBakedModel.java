package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.BedFrameBlock;
import mod.pianomanu.blockcarpentry.tileentity.BedFrameTile;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import mod.pianomanu.blockcarpentry.util.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Contains all information for the block model
 * See {@link ModelHelper} for more information
 *
 * @author PianoManu
 * @version 1.3 09/20/23
 */
public class BedBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        BlockState mimic = extraData.getData(BedFrameTile.MIMIC);
        if (mimic != null) {
            ModelResourceLocation location = BlockModelShaper.stateToModelLocation(mimic);
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
            return getMimicQuads(state, side, rand, extraData, model);
        }

        return Collections.emptyList();
    }

    public List<BakedQuad> getMimicQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData, BakedModel model) {
        if (side != null) {
            return Collections.emptyList();
        }
        BlockState mimic = extraData.getData(BedFrameTile.MIMIC);
        if (mimic != null && state != null) {// && extraData.getData(BedFrameTile.PILLOW)!=null && extraData.getData(BedFrameTile.BLANKET)!=null) {
            TextureAtlasSprite texture = TextureHelper.getTexture(model, rand, extraData, BedFrameTile.TEXTURE);
            int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
            List<BakedQuad> quads = new ArrayList<>(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 5 / 16f, 0f, 1f, texture, tintIndex));
            TextureAtlasSprite pillow = TextureHelper.getWoolTextures().get(extraData.getData(BedFrameTile.PILLOW));
            TextureAtlasSprite blanket = TextureHelper.getWoolTextures().get(extraData.getData(BedFrameTile.BLANKET));
            Integer design = extraData.getData(BedFrameTile.DESIGN);
            if (design == null) {
                return quads;
            }
            List<TextureAtlasSprite> planksList = TextureHelper.getPlanksTextures();
            TextureAtlasSprite planks;
            Integer desTex = extraData.getData(BedFrameTile.DESIGN_TEXTURE);
            if (desTex == null || desTex < 0 || desTex > 7) {
                return quads;
            } else {
                planks = planksList.get(desTex);
            }
            //four bed support cubes (bed feet)
            if (state.getValue(BedFrameBlock.PART) == BedPart.FOOT) {
                switch (state.getValue(BedBlock.FACING)) {
                    case NORTH -> {
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                    }
                    case EAST -> {
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                    }
                    case SOUTH -> {
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                    }
                    case WEST -> {
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                    }
                }

            }
            if (state.getValue(BedFrameBlock.PART) == BedPart.HEAD) {
                switch (state.getValue(BedBlock.FACING)) {
                    case SOUTH -> {
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                    }
                    case WEST -> {
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                    }
                    case NORTH -> {
                        quads.addAll(ModelHelper.createCuboid(0f, 3 / 16f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                    }
                    case EAST -> {
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 0f, 3 / 16f, texture, tintIndex));
                        quads.addAll(ModelHelper.createCuboid(13 / 16f, 1f, 0f, 3 / 16f, 13 / 16f, 1f, texture, tintIndex));
                    }
                }
            }
            if (design == 0 || design == 1) {
                if (state.getValue(BedFrameBlock.PART) == BedPart.FOOT) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, -8 / 16f, 0f, blanket, -1));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 24 / 16f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                        }
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, 1f, 24 / 16f, blanket, -1));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-8 / 16f, 0f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                        }
                    }
                }
                if (state.getValue(BedFrameBlock.PART) == BedPart.HEAD) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case SOUTH -> quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 8 / 16f, 1f, pillow, -1));
                        case WEST -> quads.addAll(ModelHelper.createCuboid(0f, 8 / 16f, 5 / 16f, 10 / 16f, 0f, 1f, pillow, -1));
                        case NORTH -> quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 0f, 8 / 16f, pillow, -1));
                        case EAST -> quads.addAll(ModelHelper.createCuboid(8 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 1f, pillow, -1));
                    }
                }
            }
            if (design == 1) {
                if (state.getValue(BedFrameBlock.PART) == BedPart.FOOT) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, texture, tintIndex));
                        }
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, texture, tintIndex));
                        }
                    }

                }
                if (state.getValue(BedFrameBlock.PART) == BedPart.HEAD) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, texture, tintIndex));
                        }
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, texture, tintIndex));
                        }
                    }
                }
            }
            if (design == 2) {
                if (state.getValue(BedFrameBlock.PART) == BedPart.FOOT) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, 0f, 14 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, -8 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 14 / 16f, 1f, texture, tintIndex));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 24 / 16f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 5 / 16f, 12 / 16f, 0f, 1f, texture, tintIndex));
                        }
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, 2 / 16f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, 1f, 24 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 0f, 2 / 16f, texture, tintIndex));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-8 / 16f, 0f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 5 / 16f, 12 / 16f, 0f, 1f, texture, tintIndex));
                        }
                    }
                }
                if (state.getValue(BedFrameBlock.PART) == BedPart.HEAD) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 10 / 16f, 8 / 16f, 14 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 14 / 16f, 1f, texture, tintIndex));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 8 / 16f, 5 / 16f, 10 / 16f, 1 / 16f, 15 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 5 / 16f, 12 / 16f, 0f, 1f, texture, tintIndex));
                        }
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 10 / 16f, 2 / 16f, 8 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 0f, 2 / 16f, texture, tintIndex));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(8 / 16f, 14 / 16f, 5 / 16f, 10 / 16f, 1 / 16f, 15 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, texture, tintIndex));
                            quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 5 / 16f, 12 / 16f, 0f, 1f, texture, tintIndex));
                        }
                    }
                }
            }
            if (design == 3) {
                if (state.getValue(BedFrameBlock.PART) == BedPart.FOOT) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, 0f, 14 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, -8 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 14 / 16f, 1f, planks, -1));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 24 / 16f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 5 / 16f, 12 / 16f, 0f, 1f, planks, -1));
                        }
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, 2 / 16f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 9 / 16f, 1f, 24 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 0f, 2 / 16f, planks, -1));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-8 / 16f, 0f, 5 / 16f, 9 / 16f, 1 / 16f, 15 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 5 / 16f, 12 / 16f, 0f, 1f, planks, -1));
                        }
                    }
                }
                if (state.getValue(BedFrameBlock.PART) == BedPart.HEAD) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 10 / 16f, 8 / 16f, 14 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 14 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 14 / 16f, 1f, planks, -1));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 8 / 16f, 5 / 16f, 10 / 16f, 1 / 16f, 15 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(2 / 16f, 1f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 5 / 16f, 12 / 16f, 0f, 1f, planks, -1));
                        }
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 5 / 16f, 10 / 16f, 2 / 16f, 8 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 5 / 16f, 10 / 16f, 2 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 12 / 16f, 0f, 2 / 16f, planks, -1));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(8 / 16f, 14 / 16f, 5 / 16f, 10 / 16f, 1 / 16f, 15 / 16f, pillow, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 0f, 1 / 16f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 5 / 16f, 10 / 16f, 15 / 16f, 1f, planks, -1));
                            quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 5 / 16f, 12 / 16f, 0f, 1f, planks, -1));
                        }
                    }
                }
            }
            if (design == 4) {
                if (state.getValue(BedFrameBlock.PART) == BedPart.FOOT) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case NORTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, -8 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, -8 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, -8 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, -9 / 16f, -8 / 16f, blanket, -1));
                        }
                        case EAST -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 24 / 16f, 5 / 16f, 10 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 24 / 16f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 24 / 16f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(24 / 16f, 25 / 16f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                        }
                        case SOUTH -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 1f, 24 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-1 / 16f, 0f, 3 / 16f, 9 / 16f, 1f, 24 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 1f, 24 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 9 / 16f, 24 / 16f, 25 / 16f, blanket, -1));
                        }
                        case WEST -> {
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-8 / 16f, 0f, 5 / 16f, 10 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(1f, 17 / 16f, 3 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(0f, 1f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-8 / 16f, 0f, 3 / 16f, 9 / 16f, -1 / 16f, 0f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-8 / 16f, 0f, 3 / 16f, 9 / 16f, 1f, 17 / 16f, blanket, -1));
                            quads.addAll(ModelHelper.createCuboid(-9 / 16f, -8 / 16f, 5 / 16f, 9 / 16f, 0f, 1f, blanket, -1));
                        }
                    }
                }
                if (state.getValue(BedFrameBlock.PART) == BedPart.HEAD) {
                    switch (state.getValue(BedBlock.FACING)) {
                        case SOUTH -> quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 9 / 16f, 1f, pillow, -1));
                        case WEST -> quads.addAll(ModelHelper.createCuboid(0f, 7 / 16f, 5 / 16f, 10 / 16f, 0f, 1f, pillow, -1));
                        case NORTH -> quads.addAll(ModelHelper.createCuboid(0f, 1f, 5 / 16f, 10 / 16f, 0f, 7 / 16f, pillow, -1));
                        case EAST -> quads.addAll(ModelHelper.createCuboid(9 / 16f, 1f, 5 / 16f, 10 / 16f, 0f, 1f, pillow, -1));
                    }
                }
            }

            return quads;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleIcon() {
        return getTexture();
    }

    @Override
    @Nonnull
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
//========SOLI DEO GLORIA========//