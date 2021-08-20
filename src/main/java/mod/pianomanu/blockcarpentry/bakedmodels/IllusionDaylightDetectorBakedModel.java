package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.block.FrameBlock;
import mod.pianomanu.blockcarpentry.tileentity.DaylightDetectorFrameTileEntity;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.ModelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
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
 * @version 1.0 08/20/21
 */
public class IllusionDaylightDetectorBakedModel implements IDynamicBakedModel {
    public static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "block/oak_planks");

    private TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(TEXTURE);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        BlockState mimic = extraData.getData(DaylightDetectorFrameTileEntity.MIMIC);
        if (mimic != null && !(mimic.getBlock() instanceof FrameBlock)) {
            ModelResourceLocation location = BlockModelShapes.getModelLocation(mimic);
            if (location != null) {
                IBakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                if (model != null) {
                    int tintIndex = BlockAppearanceHelper.setTintIndex(mimic);
                    boolean renderNorth = side == Direction.NORTH && extraData.getData(DaylightDetectorFrameTileEntity.NORTH_VISIBLE);
                    boolean renderEast = side == Direction.EAST && extraData.getData(DaylightDetectorFrameTileEntity.EAST_VISIBLE);
                    boolean renderSouth = side == Direction.SOUTH && extraData.getData(DaylightDetectorFrameTileEntity.SOUTH_VISIBLE);
                    boolean renderWest = side == Direction.WEST && extraData.getData(DaylightDetectorFrameTileEntity.WEST_VISIBLE);
                    int rotation = extraData.getData(DaylightDetectorFrameTileEntity.ROTATION);
                    List<BakedQuad> quads = new ArrayList<>(ModelHelper.createSixFaceCuboid(0f, 1f, 0f, 6 / 16f, 0f, 1f, mimic, model, extraData, rand, tintIndex, renderNorth, renderSouth, renderEast, renderWest, true, true, rotation));
                    int overlayIndex = extraData.getData(DaylightDetectorFrameTileEntity.OVERLAY);
                    if (overlayIndex != 0) {
                        quads.addAll(ModelHelper.createOverlay(0f, 1f, 0f, 6 / 16f, 0f, 1f, overlayIndex, true, true, true, true, true, true, false));
                    }
                    return quads;
                }
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