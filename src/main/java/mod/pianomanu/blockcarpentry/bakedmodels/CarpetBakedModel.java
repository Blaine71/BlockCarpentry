package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import mod.pianomanu.blockcarpentry.util.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
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
 * @version 1.3 09/20/23
 */
public class CarpetBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        BlockState mimic = extraData.getData(FrameBlockTile.MIMIC);
        Integer design = extraData.getData(FrameBlockTile.DESIGN);
        if (side != null) {
            return Collections.emptyList();
        }
        if (mimic != null) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (state != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                TextureAtlasSprite texture = QuadUtils.getTexture(model, rand, extraData, FrameBlockTile.TEXTURE);
                TextureAtlasSprite glass = TextureHelper.getGlassTextures().get(extraData.getData(FrameBlockTile.GLASS_COLOR));
                int woolInt = extraData.getData(FrameBlockTile.GLASS_COLOR) - 1;
                if (woolInt < 0)
                    woolInt = 0;
                TextureAtlasSprite wool = TextureHelper.getWoolTextures().get(woolInt);


                boolean renderNorth = extraData.getData(FrameBlockTile.NORTH_VISIBLE);
                boolean renderEast = extraData.getData(FrameBlockTile.EAST_VISIBLE);
                boolean renderSouth = extraData.getData(FrameBlockTile.SOUTH_VISIBLE);
                boolean renderWest = extraData.getData(FrameBlockTile.WEST_VISIBLE);
                int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
                List<BakedQuad> quads = new ArrayList<>();
                if (design == 0) {
                    quads.addAll(ModelHelper.createCuboid(0f, 1f, 0f, 1 / 16f, 0f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                } else if (design == 1) {
                    quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 0f, 1 / 16f, 0f, 15 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(1 / 16f, 1, 0f, 1 / 16f, 0f, 1 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 0f, 1 / 16f, 1 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(0f, 15 / 16f, 0f, 1 / 16f, 15 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));

                    quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 0f, 1 / 16f, 1 / 16f, 15 / 16f, glass, -1, renderNorth, renderSouth, renderEast, renderWest, true, true));
                } else if (design == 2) {
                    quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 0f, 1 / 16f, 0f, 14 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(2 / 16f, 1, 0f, 1 / 16f, 0f, 2 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 0f, 1 / 16f, 2 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 0f, 1 / 16f, 14 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));

                    quads.addAll(ModelHelper.createCuboid(2 / 16f, 14 / 16f, 0f, 1 / 16f, 2 / 16f, 14 / 16f, glass, -1, renderNorth, renderSouth, renderEast, renderWest, true, true));
                } else if (design == 3) {
                    quads.addAll(ModelHelper.createCuboid(0f, 1 / 16f, 0f, 1 / 16f, 0f, 15 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(1 / 16f, 1, 0f, 1 / 16f, 0f, 1 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(15 / 16f, 1f, 0f, 1 / 16f, 1 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(0f, 15 / 16f, 0f, 1 / 16f, 15 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));

                    quads.addAll(ModelHelper.createCuboid(1 / 16f, 15 / 16f, 0f, 1 / 16f, 1 / 16f, 15 / 16f, wool, -1, renderNorth, renderSouth, renderEast, renderWest, true, true));
                } else if (design == 4) {
                    quads.addAll(ModelHelper.createCuboid(0f, 2 / 16f, 0f, 1 / 16f, 0f, 14 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(2 / 16f, 1, 0f, 1 / 16f, 0f, 2 / 16f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(14 / 16f, 1f, 0f, 1 / 16f, 2 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));
                    quads.addAll(ModelHelper.createCuboid(0f, 14 / 16f, 0f, 1 / 16f, 14 / 16f, 1f, texture, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true));

                    quads.addAll(ModelHelper.createCuboid(2 / 16f, 14 / 16f, 0f, 1 / 16f, 2 / 16f, 14 / 16f, wool, -1, renderNorth, renderSouth, renderEast, renderWest, true, true));
                }
                int overlayIndex = extraData.getData(FrameBlockTile.OVERLAY);
                if (overlayIndex != 0) {
                    quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 1 / 16f, 0f, 1f, overlayIndex, true, true, true, true, true, true, true));
                }
                return quads;
            }
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