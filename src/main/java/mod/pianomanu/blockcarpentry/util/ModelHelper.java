package mod.pianomanu.blockcarpentry.util;

import mod.pianomanu.blockcarpentry.bakedmodels.ModelInformation;
import mod.pianomanu.blockcarpentry.bakedmodels.QuadUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Util class for building cuboid shapes
 *
 * @author PianoManu
 * @version 1.7 11/08/23
 */
public class ModelHelper {

    public static Float[] checkWithinBounds(Float xl, Float xh, Float yl, Float yh, Float zl, Float zh) {
        if (xh - xl > 1 || yh - yl > 1 || zh - zl > 1) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.blockcarpentry.block_error"), true);
            }
            return null;
        }
        if (xl < 0) {
            xl++;
            xh++;
        }
        if (xh > 1) {
            xh--;
            xl--;
        }
        if (yl < 0) {
            yl++;
            yh++;
        }
        if (yh > 1) {
            yh--;
            yl--;
        }
        if (zl < 0) {
            zl++;
            zh++;
        }
        if (zh > 1) {
            zh--;
            zl--;
        }
        return new Float[]{xl, xh, yl, yh, zl, zh};
    }

    private static Vec3[] convertFloatsToVec(float xl, float xh, float yl, float yh, float zl, float zh) {
        //Eight corners of the block
        Vec3 NWU = v(xl, yh, zl); //North-West-Up
        Vec3 SWU = v(xl, yh, zh); //...
        Vec3 NWD = v(xl, yl, zl);
        Vec3 SWD = v(xl, yl, zh);
        Vec3 NEU = v(xh, yh, zl);
        Vec3 SEU = v(xh, yh, zh);
        Vec3 NED = v(xh, yl, zl);
        Vec3 SED = v(xh, yl, zh); //South-East-Down
        return new Vec3[]{NWU, SWU, NWD, SWD, NEU, SEU, NED, SED};
    }

    /**
     * This method is used to create a cuboid from six faces. Input values determine
     * the dimensions of the cuboid and the texture to apply.
     * Example 1: full block with dirt texture would be:
     * (0f,1f,0f,1f,0f,1f, >path of dirt texture<, 0)
     * I.e. the dimensions of the block are from (0,0,0) to (16,16,16)
     * Example 2: oak fence:
     * (6/16f,10/16f,0f,1f,6/16f,10/16f, >oak planks<, 0)
     * I.e. the dimensions of a fence, being (6,0,6) to (10,16,10)
     *
     * @param xl        low x component of the block
     * @param xh        high x component of the block
     * @param yl        low y component of the block
     * @param yh        high y component of the block
     * @param zl        low z component of the block
     * @param zh        high z component of the block
     * @param texture   TextureAtlasSprite of the block
     * @param tintIndex only needed for tintable blocks like grass
     * @return List of baked quads, i.e. List of six faces
     */
    public static List<BakedQuad> createCuboid(float xl, float xh, float yl, float yh, float zl, float zh, TextureAtlasSprite texture, int tintIndex) {
        return createCuboid(xl, xh, yl, yh, zl, zh, texture, tintIndex, true, true, true, true, true, true);
    }

    public static List<BakedQuad> createCuboid(float xl, float xh, float yl, float yh, float zl, float zh, TextureAtlasSprite texture, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        Vec3[] vecs = convertFloatsToVec(xl, xh, yl, yh, zl, zh);
        return createCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], texture, tintIndex, north, south, east, west, up, down);
    }

    public static List<BakedQuad> createCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, TextureAtlasSprite texture, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, boolean keepDefaultUV016) {
        return createCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, texture, tintIndex, north, south, east, west, up, down, keepDefaultUV016, 0, true);
    }

    public static List<BakedQuad> createCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, TextureAtlasSprite texture, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, boolean keepDefaultUV016, int overlayIndex, boolean doNotMoveOverlay) {
        List<BakedQuad> quads = new ArrayList<>();

        Float[] floats = checkWithinBounds((float) NWD.x, (float) SEU.x, (float) NWD.y, (float) SEU.y, (float) NWD.z, (float) SEU.z);
        if (floats == null)
            return createCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, TextureHelper.getMissingTexture());
        Float xl = floats[0];
        Float xh = floats[1];
        Float yl = floats[2];
        Float yh = floats[3];
        Float zl = floats[4];
        Float zh = floats[5];

        if (keepDefaultUV016) {
            if (up) quads.add(QuadUtils.createQuad(NWU, SWU, SEU, NEU, texture, tintIndex));
            if (down)
                quads.add(QuadUtils.createQuad(NED, SED, SWD, NWD, texture, tintIndex));
            if (west)
                quads.add(QuadUtils.createQuad(NWU, NWD, SWD, SWU, texture, tintIndex));
            if (east)
                quads.add(QuadUtils.createQuad(SEU, SED, NED, NEU, texture, tintIndex));
            if (north)
                quads.add(QuadUtils.createQuad(NEU, NED, NWD, NWU, texture, tintIndex));
            if (south)
                quads.add(QuadUtils.createQuad(SWU, SWD, SED, SEU, texture, tintIndex));
        } else {
            if (up)
                quads.add(QuadUtils.createQuad(NWU, SWU, SEU, NEU, texture, xl * 16, xh * 16, zl * 16, zh * 16, tintIndex));
            if (down)
                quads.add(QuadUtils.createQuad(NED, SED, SWD, NWD, texture, xh * 16, xl * 16, 16 - zl * 16, 16 - zh * 16, tintIndex));
            if (west)
                quads.add(QuadUtils.createQuad(NWU, NWD, SWD, SWU, texture, zl * 16, zh * 16, 16 - yh * 16, 16 - yl * 16, tintIndex));
            if (east)
                quads.add(QuadUtils.createQuad(SEU, SED, NED, NEU, texture, 16 - zh * 16, 16 - zl * 16, 16 - yh * 16, 16 - yl * 16, tintIndex));
            if (north)
                quads.add(QuadUtils.createQuad(NEU, NED, NWD, NWU, texture, 16 - xh * 16, 16 - xl * 16, 16 - yh * 16, 16 - yl * 16, tintIndex));
            if (south)
                quads.add(QuadUtils.createQuad(SWU, SWD, SED, SEU, texture, xl * 16, xh * 16, 16 - yh * 16, 16 - yl * 16, tintIndex));
        }
        if (overlayIndex != 0) {
            quads.addAll(createOverlay(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, overlayIndex));
        }
        return quads;
    }

    public static List<BakedQuad> createCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, TextureAtlasSprite texture) {
        return createCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, texture, -1, true, true, true, true, true, true);
    }

    public static List<BakedQuad> createCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, TextureAtlasSprite texture, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        return createCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, texture, tintIndex, north, south, east, west, up, down, false);
    }

    public static List<BakedQuad> createSixFaceCuboid(float xl, float xh, float yl, float yh, float zl, float zh, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, int rotation) {
        return createSixFaceCuboid(xl, xh, yl, yh, zl, zh, mimic, model, extraData, rand, tintIndex, true, true, true, true, true, true, rotation);
    }

    public static List<BakedQuad> createSixFaceCuboid(float xl, float xh, float yl, float yh, float zl, float zh, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, List<Direction> directions, List<Integer> rotations) {
        return createSixFaceCuboid(xl, xh, yl, yh, zl, zh, mimic, model, extraData, rand, tintIndex, true, true, true, true, true, true, directions, rotations);
    }

    public static List<BakedQuad> createSixFaceCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, List<Direction> directions, List<Integer> rotations, boolean keepUV, boolean invert) {
        Float[] floats = checkWithinBounds((float) NWD.x, (float) SEU.x, (float) NWD.y, (float) SEU.y, (float) NWD.z, (float) SEU.z);
        if (floats == null)
            return createCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, TextureHelper.getMissingTexture());

        TextureAtlasSprite textureNorth = TextureHelper.getTexture(model, extraData, rand, Direction.NORTH);
        TextureAtlasSprite textureEast = TextureHelper.getTexture(model, extraData, rand, Direction.EAST);
        TextureAtlasSprite textureSouth = TextureHelper.getTexture(model, extraData, rand, Direction.SOUTH);
        TextureAtlasSprite textureWest = TextureHelper.getTexture(model, extraData, rand, Direction.WEST);
        TextureAtlasSprite textureUp = TextureHelper.getTexture(model, extraData, rand, Direction.UP);
        TextureAtlasSprite textureDown = TextureHelper.getTexture(model, extraData, rand, Direction.DOWN);

        return createSixFaceCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, textureNorth, textureSouth, textureEast, textureWest, textureUp, textureDown, tintIndex, true, true, true, true, true, true, directions, rotations, keepUV, invert);
    }

    public static List<BakedQuad> createSixFaceCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, TextureAtlasSprite textureNorth, TextureAtlasSprite textureSouth, TextureAtlasSprite textureEast, TextureAtlasSprite textureWest, TextureAtlasSprite textureUp, TextureAtlasSprite textureDown, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, List<Direction> directions, List<Integer> rotations, boolean keepUV, boolean invert) {
        List<BakedQuad> quads = new ArrayList<>();
        Vec3[] rotatedVecs;
        Direction[] rotatedDirs;
        boolean[] rotatedVisibility;
        Direction newUp = Direction.UP;
        Direction newDown = Direction.DOWN;
        Direction newNorth = Direction.NORTH;
        Direction newEast = Direction.EAST;
        Direction newSouth = Direction.SOUTH;
        Direction newWest = Direction.WEST;
        int upRotation = rotations.size() == 6 ? rotations.get(1) : 0;
        int downRotation = rotations.size() == 6 ? rotations.get(0) : 0;
        int westRotation = rotations.size() == 6 ? rotations.get(4) : 0;
        int eastRotation = rotations.size() == 6 ? rotations.get(5) : 0;
        int northRotation = rotations.size() == 6 ? rotations.get(2) : 0;
        int southRotation = rotations.size() == 6 ? rotations.get(3) : 0;
        for (Direction d : directions) {
            rotatedVecs = CornerUtils.rotateVec3s(NWU, NEU, SWU, SEU, NWD, NED, SWD, SED, d);
            rotatedDirs = CornerUtils.getRotatedDirection(newUp, newDown, newNorth, newEast, newSouth, newWest, d);
            rotatedVisibility = CornerUtils.getRotatedVisibility(up, down, north, east, south, west, d);
            NWU = rotatedVecs[0];
            NEU = rotatedVecs[1];
            SWU = rotatedVecs[2];
            SEU = rotatedVecs[3];
            NWD = rotatedVecs[4];
            NED = rotatedVecs[5];
            SWD = rotatedVecs[6];
            SED = rotatedVecs[7];
            newUp = rotatedDirs[0];
            newDown = rotatedDirs[1];
            newNorth = rotatedDirs[2];
            newEast = rotatedDirs[3];
            newSouth = rotatedDirs[4];
            newWest = rotatedDirs[5];
            up = rotatedVisibility[0];
            down = rotatedVisibility[1];
            north = rotatedVisibility[2];
            east = rotatedVisibility[3];
            south = rotatedVisibility[4];
            west = rotatedVisibility[5];
        }
        if (keepUV) {
            if (up)
                quads.add(QuadUtils.createQuad(NWU, SWU, SEU, NEU, textureUp, tintIndex, upRotation));
            if (down)
                quads.add(QuadUtils.createQuad(SWD, NWD, NED, SED, textureDown, tintIndex, downRotation));
            if (west)
                quads.add(QuadUtils.createQuad(NWU, NWD, SWD, SWU, textureWest, tintIndex, westRotation));
            if (east)
                quads.add(QuadUtils.createQuad(SEU, SED, NED, NEU, textureEast, tintIndex, eastRotation));
            if (north)
                quads.add(QuadUtils.createQuad(NEU, NED, NWD, NWU, textureNorth, tintIndex, northRotation));
            if (south)
                quads.add(QuadUtils.createQuad(SWU, SWD, SED, SEU, textureSouth, tintIndex, southRotation));
        } else {
            float[] uvs;
            if (up) {
                uvs = QuadUtils.rotateUVcomplex(NWU, SWU, SEU, NEU, newUp, upRotation);
                quads.add(QuadUtils.createQuad(NWU, SWU, SEU, NEU, textureUp, uvs[0], uvs[1], uvs[2], uvs[3], uvs[4], uvs[5], uvs[6], uvs[7], tintIndex, invert, 0));
            }
            if (down) {
                uvs = QuadUtils.rotateUVcomplex(SWD, NWD, NED, SED, newDown, downRotation);
                quads.add(QuadUtils.createQuad(SWD, NWD, NED, SED, textureDown, uvs[0], uvs[1], uvs[2], uvs[3], uvs[4], uvs[5], uvs[6], uvs[7], tintIndex, invert, 0));
            }
            if (west) {
                uvs = QuadUtils.rotateUVcomplex(NWU, NWD, SWD, SWU, newWest, westRotation);
                quads.add(QuadUtils.createQuad(NWU, NWD, SWD, SWU, textureWest, uvs[0], uvs[1], uvs[2], uvs[3], uvs[4], uvs[5], uvs[6], uvs[7], tintIndex, invert, 0));
            }
            if (east) {
                uvs = QuadUtils.rotateUVcomplex(SEU, SED, NED, NEU, newEast, eastRotation);
                quads.add(QuadUtils.createQuad(SEU, SED, NED, NEU, textureEast, uvs[0], uvs[1], uvs[2], uvs[3], uvs[4], uvs[5], uvs[6], uvs[7], tintIndex, invert, 0));
            }
            if (north) {
                uvs = QuadUtils.rotateUVcomplex(NEU, NED, NWD, NWU, newNorth, northRotation);
                quads.add(QuadUtils.createQuad(NEU, NED, NWD, NWU, textureNorth, uvs[0], uvs[1], uvs[2], uvs[3], uvs[4], uvs[5], uvs[6], uvs[7], tintIndex, invert, 0));
            }
            if (south) {
                uvs = QuadUtils.rotateUVcomplex(SWU, SWD, SED, SEU, newSouth, southRotation);
                quads.add(QuadUtils.createQuad(SWU, SWD, SED, SEU, textureSouth, uvs[0], uvs[1], uvs[2], uvs[3], uvs[4], uvs[5], uvs[6], uvs[7], tintIndex, invert, 0));
            }
        }
        return quads;
    }

    public static List<BakedQuad> createSixFaceCuboid(float xl, float xh, float yl, float yh, float zl, float zh, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, List<Direction> directions, List<Integer> rotations) {
        Float[] floats = checkWithinBounds(xl, xh, yl, yh, zl, zh);
        if (floats == null)
            return createCuboid(xl, xh, yl, yh, zl, zh, TextureHelper.getMissingTexture(), -1);
        xl = floats[0];
        xh = floats[1];
        yl = floats[2];
        yh = floats[3];
        zl = floats[4];
        zh = floats[5];

        Vec3[] vecs = convertFloatsToVec(xl, xh, yl, yh, zl, zh);
        return createSixFaceCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], mimic, model, extraData, rand, tintIndex, north, south, east, west, up, down, directions, rotations, false, false);
    }

    public static List<BakedQuad> createSixFaceCuboid(float xl, float xh, float yl, float yh, float zl, float zh, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, int rotation) {
        Float[] floats = checkWithinBounds(xl, xh, yl, yh, zl, zh);
        if (floats == null)
            return createCuboid(xl, xh, yl, yh, zl, zh, TextureHelper.getMissingTexture(), -1);
        xl = floats[0];
        xh = floats[1];
        yl = floats[2];
        yh = floats[3];
        zl = floats[4];
        zh = floats[5];

        TextureAtlasSprite textureNorth = TextureHelper.getTexture(model, extraData, rand, Direction.NORTH);
        TextureAtlasSprite textureEast = TextureHelper.getTexture(model, extraData, rand, Direction.EAST);
        TextureAtlasSprite textureSouth = TextureHelper.getTexture(model, extraData, rand, Direction.SOUTH);
        TextureAtlasSprite textureWest = TextureHelper.getTexture(model, extraData, rand, Direction.WEST);
        TextureAtlasSprite textureUp = TextureHelper.getTexture(model, extraData, rand, Direction.UP);
        TextureAtlasSprite textureDown = TextureHelper.getTexture(model, extraData, rand, Direction.DOWN);

        List<Direction> directions = CornerUtils.legacyRotation(rotation);
        Vec3[] vecs = convertFloatsToVec(xl, xh, yl, yh, zl, zh);
        return createSixFaceCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], textureNorth, textureSouth, textureEast, textureWest, textureUp, textureDown, tintIndex, north, south, east, west, up, down, directions, Collections.emptyList(), false, false);
    }

    public static List<BakedQuad> createSixFaceVoxel(float x, float y, float z, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, int rotation) {
        return createSixFaceCuboid(x / 16f, (x + 1) / 16f, y / 16f, (y + 1) / 16f, z / 16f, (z + 1) / 16f, mimic, model, extraData, rand, tintIndex, north, south, east, west, up, down, rotation);
    }

    public static List<BakedQuad> createSixFaceVoxel(float x, float y, float z, BlockState mimic, BakedModel model, ModelData extraData, RandomSource rand, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, List<Direction> direction, List<Integer> rotations) {
        return createSixFaceCuboid(x / 16f, (x + 1) / 16f, y / 16f, (y + 1) / 16f, z / 16f, (z + 1) / 16f, mimic, model, extraData, rand, tintIndex, north, south, east, west, up, down, direction, rotations);
    }

    public static List<BakedQuad> createSixFaceCuboid(float xl, float xh, float yl, float yh, float zl, float zh, int tintIndex, TextureAtlasSprite textureNorth, TextureAtlasSprite textureSouth, TextureAtlasSprite textureEast, TextureAtlasSprite textureWest, TextureAtlasSprite textureUp, TextureAtlasSprite textureDown, int rotation) {
        Float[] floats = checkWithinBounds(xl, xh, yl, yh, zl, zh);
        if (floats == null)
            return createCuboid(xl, xh, yl, yh, zl, zh, TextureHelper.getMissingTexture(), -1);
        xl = floats[0];
        xh = floats[1];
        yl = floats[2];
        yh = floats[3];
        zl = floats[4];
        zh = floats[5];

        Vec3[] vecs = convertFloatsToVec(xl, xh, yl, yh, zl, zh);
        return createSixFaceCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], textureNorth, textureSouth, textureEast, textureWest, textureUp, textureDown, tintIndex, true, true, true, true, true, true, CornerUtils.legacyRotation(rotation), Collections.emptyList(), true, false);
    }

    public static List<BakedQuad> createSixFaceCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, ModelInformation m, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, boolean moveOverlay, boolean keepUV) {
        List<BakedQuad> quads = new ArrayList<>();
        TextureAtlasSprite textureUp = m.upTexture;
        TextureAtlasSprite textureDown = m.downTexture;
        TextureAtlasSprite textureNorth = m.northTexture;
        TextureAtlasSprite textureEast = m.eastTexture;
        TextureAtlasSprite textureSouth = m.southTexture;
        TextureAtlasSprite textureWest = m.westTexture;
        if (up && textureUp != null)
            quads.add(QuadUtils.createQuad(NWU, SWU, SEU, NEU, textureUp, Direction.UP, m.tintIndex, 0, keepUV, false));
        if (down && textureDown != null)
            quads.add(QuadUtils.createQuad(NED, SED, SWD, NWD, textureDown, Direction.DOWN, m.tintIndex, 0, keepUV, false));
        if (west && textureWest != null)
            quads.add(QuadUtils.createQuad(NWU, NWD, SWD, SWU, textureWest, Direction.WEST, m.tintIndex, 0, keepUV, false, false));
        if (east && textureEast != null)
            quads.add(QuadUtils.createQuad(SEU, SED, NED, NEU, textureEast, Direction.EAST, m.tintIndex, 0, keepUV, false, false));
        if (north && textureNorth != null)
            quads.add(QuadUtils.createQuad(NEU, NED, NWD, NWU, textureNorth, Direction.NORTH, m.tintIndex, 0, keepUV, false, false));
        if (south && textureSouth != null)
            quads.add(QuadUtils.createQuad(SWU, SWD, SED, SEU, textureSouth, Direction.SOUTH, m.tintIndex, 0, keepUV, false, false));
        return quads;
    }

    public static List<BakedQuad> createSixFaceCuboid(float xl, float xh, float yl, float yh, float zl, float zh, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, ModelInformation m, Boolean moveOverlay, int rotation) {
        Float[] floats = checkWithinBounds(xl, xh, yl, yh, zl, zh);
        if (floats == null)
            return createCuboid(xl, xh, yl, yh, zl, zh, TextureHelper.getMissingTexture(), -1);

        Vec3[] vecs = convertFloatsToVec(floats[0], floats[1], floats[2], floats[3], floats[4], floats[5]);
        return createSixFaceCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], m, north, south, east, west, up, down, moveOverlay, true);
    }

    public static List<BakedQuad> createCuboid(float xl, float xh, float yl, float yh, float zl, float zh, TextureAtlasSprite texture, int tintIndex, int[] ulow, int[] uhigh, int[] vlow, int[] vhigh) {
        Vec3[] vecs = convertFloatsToVec(xl, xh, yl, yh, zl, zh);
        return createCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], texture, tintIndex, true, true, true, true, true, true, ulow, uhigh, vlow, vhigh);
    }

    public static List<BakedQuad> createCuboid(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, TextureAtlasSprite texture, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, int[] ulow, int[] uhigh, int[] vlow, int[] vhigh) {
        if (ulow.length != 6 || uhigh.length != 6 || vlow.length != 6 || vhigh.length != 6) {
            return Collections.emptyList();
        }
        List<BakedQuad> quads = new ArrayList<>();
        if (up)
            quads.add(QuadUtils.createQuad(NWU, NEU, SEU, SWU, texture, ulow[0], uhigh[0], vlow[0], vhigh[0], tintIndex));
        if (down)
            quads.add(QuadUtils.createQuad(NED, NWD, SWD, SED, texture, ulow[1], uhigh[1], vlow[1], vhigh[1], tintIndex));
        if (north)
            quads.add(QuadUtils.createQuad(NWU, NWD, NED, NEU, texture, ulow[2], uhigh[2], vlow[2], vhigh[2], tintIndex));
        if (east)
            quads.add(QuadUtils.createQuad(NEU, NED, SED, SEU, texture, ulow[3], uhigh[3], vlow[3], vhigh[3], tintIndex));
        if (south)
            quads.add(QuadUtils.createQuad(SEU, SED, SWD, SWU, texture, ulow[4], uhigh[4], vlow[4], vhigh[4], tintIndex));
        if (west)
            quads.add(QuadUtils.createQuad(SWU, SWD, NWD, NWU, texture, ulow[5], uhigh[5], vlow[5], vhigh[5], tintIndex));
        return quads;
    }

    /**
     * Creates a single voxel starting at {x, y, z} to {x+1, y+1, z+1}
     *
     * @param x         start value in x-direction
     * @param y         start value in y-direction
     * @param z         start value in z-direction
     * @param texture   texture for the voxel
     * @param tintIndex set to value > 0, if you want to enable tinting
     * @param north     whether the north face of the voxel is visible
     * @param south     whether the south face of the voxel is visible
     * @param east      whether the east face of the voxel is visible
     * @param west      whether the west face of the voxel is visible
     * @param up        whether the top face of the voxel is visible
     * @param down      whether the bottom face of the voxel is visible
     * @return list of {@link BakedQuad} representing the voxel
     */
    public static List<BakedQuad> createVoxel(int x, int y, int z, TextureAtlasSprite texture, int tintIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down) {
        return createCuboid(x / 16f, (x + 1) / 16f, y / 16f, (y + 1) / 16f, z / 16f, (z + 1) / 16f, texture, tintIndex, north, south, east, west, up, down);
    }

    /**
     * This just builds vectors and is useful for clean code
     *
     * @param x x component
     * @param y y component
     * @param z z component
     * @return 3D-Vector with input values. Why am I writing this...
     */
    private static Vec3 v(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static List<BakedQuad> createOverlayVoxel(int x, int y, int z, int overlayIndex) {
        return createOverlay(x / 16f, (x + 1) / 16f, y / 16f, (y + 1) / 16f, z / 16f, (z + 1) / 16f, overlayIndex);
    }

    public static List<BakedQuad> createOverlayVoxel(int x, int y, int z, int overlayIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, Boolean doNotMoveOverlay) {
        return createOverlay(x / 16f, (x + 1) / 16f, y / 16f, (y + 1) / 16f, z / 16f, (z + 1) / 16f, overlayIndex, north, south, east, west, up, down, doNotMoveOverlay);
    }

    public static List<BakedQuad> createOverlay(float xl, float xh, float yl, float yh, float zl, float zh, int overlayIndex) {
        return createOverlay(xl, xh, yl, yh, zl, zh, overlayIndex, true, true, true, true, true, true, true);
    }

    public static List<BakedQuad> createOverlay(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, int overlayIndex) {
        return createOverlay(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, overlayIndex, true, true, true, true, true, true, true);
    }

    public static List<BakedQuad> createOverlay(Vec3 NWU, Vec3 SWU, Vec3 NWD, Vec3 SWD, Vec3 NEU, Vec3 SEU, Vec3 NED, Vec3 SED, int overlayIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, Boolean doNotMoveOverlay) {
        ModelInformation m = TextureHelper.getOverlayModelInformation(overlayIndex);
        return ModelHelper.createSixFaceCuboid(NWU, SWU, NWD, SWD, NEU, SEU, NED, SED, m, north, south, east, west, up, down, !doNotMoveOverlay, false);
    }

    public static List<BakedQuad> createOverlay(float xl, float xh, float yl, float yh, float zl, float zh, int overlayIndex, boolean north, boolean south, boolean east, boolean west, boolean up, boolean down, Boolean doNotMoveOverlay) {
        ModelInformation m = TextureHelper.getOverlayModelInformation(overlayIndex);
        Vec3[] vecs = convertFloatsToVec(xl, xh, yl, yh, zl, zh);
        return ModelHelper.createSixFaceCuboid(vecs[0], vecs[1], vecs[2], vecs[3], vecs[4], vecs[5], vecs[6], vecs[7], m, north, south, east, west, up, down, !doNotMoveOverlay, false);
    }

    private static float meanUV(double uv1, double uv2) {
        return (float) (uv1 + uv2) * 16 / 2;
    }
}
//========SOLI DEO GLORIA========//