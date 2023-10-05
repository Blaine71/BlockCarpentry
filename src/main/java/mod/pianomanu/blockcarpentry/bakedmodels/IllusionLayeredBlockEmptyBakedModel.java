package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.LayeredBlock;
import mod.pianomanu.blockcarpentry.block.SixWaySlabFrameBlock;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.block.BlockState;
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
 * @version 1.0 05/23/22
 */
public class IllusionLayeredBlockEmptyBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        BlockState mimic = extraData.getData(FrameBlockTile.MIMIC);
        if (mimic == null) {
            return getMimicQuads(state, side, extraData);
        }

        return Collections.emptyList();
    }

    //supresses "Unboxing of "extraData..." may produce NullPointerException
    @SuppressWarnings("all")
    public List<BakedQuad> getMimicQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull IModelData extraData) {
        if (side == null) {
            return Collections.emptyList();
        }
        if (state != null) {
            int layers = state.get(LayeredBlock.LAYERS);
            boolean renderNorth = side == Direction.NORTH && extraData.getData(FrameBlockTile.NORTH_VISIBLE);
            boolean renderEast = side == Direction.EAST && extraData.getData(FrameBlockTile.EAST_VISIBLE);
            boolean renderSouth = side == Direction.SOUTH && extraData.getData(FrameBlockTile.SOUTH_VISIBLE);
            boolean renderWest = side == Direction.WEST && extraData.getData(FrameBlockTile.WEST_VISIBLE);
            boolean renderUp = side == Direction.UP && extraData.getData(FrameBlockTile.UP_VISIBLE);
            boolean renderDown = side == Direction.DOWN && extraData.getData(FrameBlockTile.DOWN_VISIBLE);
            TextureAtlasSprite textureUp = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/oak_planks"));
            TextureAtlasSprite textureDown = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/oak_planks"));
            TextureAtlasSprite textureNorth = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/spruce_trapdoor"));
            TextureAtlasSprite textureSouth = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/spruce_trapdoor"));
            TextureAtlasSprite textureEast = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/spruce_trapdoor"));
            TextureAtlasSprite textureWest = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/spruce_trapdoor"));
            TextureAtlasSprite slime = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(new ResourceLocation("minecraft", "block/slime_block"));
            List<BakedQuad> quads = new ArrayList<>();
            switch (state.get(SixWaySlabFrameBlock.FACING)) {
                case UP:
                    for (int i = 0; i < layers; i++) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 1f - (i + 1) / 8f, 1 - (i + 0.5f) / 8f, 0f, 1f, -1, textureNorth, textureSouth, textureEast, textureWest, textureUp, textureDown, 0));
                        quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 1f - (i + 0.5f) / 8f, 1 - i / 8f, 1 / 16f, 15 / 16f, slime, -1));
                    }
                    break;
                case DOWN:
                    for (int i = 0; i < layers; i++) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, (i + 0.5f) / 8f, (i + 1) / 8f, 0f, 1f, -1, textureNorth, textureSouth, textureEast, textureWest, textureUp, textureDown, 0));
                        quads.addAll(ModelHelper.createCuboid(1 / 15f, 15 / 16f, i / 8f, (i + 0.5f) / 8f, 1 / 16f, 15 / 16f, slime, -1));
                    }
                    break;
                case WEST:
                    for (int i = 0; i < layers; i++) {
                        quads.addAll(ModelHelper.createSixFaceCuboid((i + 0.5f) / 8f, (i + 1) / 8f, 0f, 1f, 0f, 1f, -1, textureUp, textureDown, textureEast, textureWest, textureNorth, textureSouth, 0));
                        quads.addAll(ModelHelper.createCuboid(i / 8f, (i + 0.5f) / 8f, 1 / 15f, 15 / 16f, 1 / 15f, 15 / 16f, slime, -1));
                    }
                    break;
                case SOUTH:
                    for (int i = 0; i < layers; i++) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 1f, 1f - (i + 1) / 8f, 1 - (i + 0.5f) / 8f, -1, textureNorth, textureSouth, textureUp, textureDown, textureEast, textureWest, 0));
                        quads.addAll(ModelHelper.createCuboid(1 / 15f, 15 / 16f, 1 / 15f, 15 / 16f, 1f - (i + 0.5f) / 8f, 1 - i / 8f, slime, -1));
                    }
                    break;
                case NORTH:
                    for (int i = 0; i < layers; i++) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 1f, (i + 0.5f) / 8f, (i + 1) / 8f, -1, textureNorth, textureSouth, textureUp, textureDown, textureEast, textureWest, 0));
                        quads.addAll(ModelHelper.createCuboid(1 / 15f, 15 / 16f, 1 / 15f, 15 / 16f, i / 8f, (i + 0.5f) / 8f, slime, -1));
                    }
                    break;
                case EAST:
                    for (int i = 0; i < layers; i++) {
                        quads.addAll(ModelHelper.createSixFaceCuboid(1f - (i + 1) / 8f, 1f - (i + 0.5f) / 8f, 0f, 1f, 0f, 1f, -1, textureUp, textureDown, textureEast, textureWest, textureNorth, textureSouth, 0));
                        quads.addAll(ModelHelper.createCuboid(1f - (i + 0.5f) / 8f, 1f - i / 8f, 1 / 15f, 15 / 16f, 1 / 15f, 15 / 16f, slime, -1));
                    }
                    break;
            }
            int overlayIndex_1 = extraData.getData(FrameBlockTile.OVERLAY);
            if (extraData.getData(FrameBlockTile.OVERLAY) != 0) {
                switch (state.get(SixWaySlabFrameBlock.FACING)) {
                    case UP:
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 0.5f, 0f, 1f, overlayIndex_1, renderWest, renderEast, renderSouth, renderNorth, renderUp, renderDown, true));
                        break;
                    case DOWN:
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0.5f, 1f, 0f, 1f, overlayIndex_1, renderWest, renderEast, renderSouth, renderNorth, renderUp, renderDown, true));
                        break;
                    case WEST:
                        quads.addAll(ModelHelper.createOverlay(0.5f, 1f, 0f, 1f, 0f, 1f, overlayIndex_1, renderWest, renderEast, renderSouth, renderNorth, renderUp, renderDown, true));
                        break;
                    case SOUTH:
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1f, 0f, 0.5f, overlayIndex_1, renderWest, renderEast, renderSouth, renderNorth, renderUp, renderDown, true));
                        break;
                    case NORTH:
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1f, 0.5f, 1f, overlayIndex_1, renderWest, renderEast, renderSouth, renderNorth, renderUp, renderDown, true));
                        break;
                    case EAST:
                        quads.addAll(ModelHelper.createOverlay(0f, 0.5f, 0f, 1f, 0f, 1f, overlayIndex_1, renderWest, renderEast, renderSouth, renderNorth, renderUp, renderDown, true));
                        break;
                }
            }
            return quads;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean func_230044_c_() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        return getTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }
}
//========SOLI DEO GLORIA========//