package mod.pianomanu.blockcarpentry.tileentity;

import mod.pianomanu.blockcarpentry.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static mod.pianomanu.blockcarpentry.setup.Registration.FRAMEBLOCK_TILE;

/**
 * BlockEntity for {@link mod.pianomanu.blockcarpentry.block.FrameBlock} and all sorts of frame blocks
 * Contains all information about the block and the mimicked block
 *
 * @author PianoManu
 * @version 1.4 09/24/23
 */
public class FrameBlockTile extends BlockEntity implements IForgeBlockEntity {
    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();
    public static final ModelProperty<Integer> TEXTURE = new ModelProperty<>();
    public static final ModelProperty<Integer> DESIGN = new ModelProperty<>();
    public static final ModelProperty<Integer> DESIGN_TEXTURE = new ModelProperty<>();
    //currently only for doors and trapdoors
    public static final ModelProperty<Integer> GLASS_COLOR = new ModelProperty<>();
    public static final ModelProperty<Integer> OVERLAY = new ModelProperty<>();
    public static final ModelProperty<Integer> ROTATION = new ModelProperty<>();
    public static final ModelProperty<Boolean> NORTH_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> EAST_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> SOUTH_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> WEST_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> UP_VISIBLE = new ModelProperty<>();
    public static final ModelProperty<Boolean> DOWN_VISIBLE = new ModelProperty<>();

    public final int maxTextures = 8;
    public final int maxDesignTextures = 4;
    public final int maxDesigns = 4;

    public static final List<TagPacket<?>> TAG_PACKETS = initTagPackets();

    private BlockState mimic;
    private Integer texture = 0;
    private Integer design = 0;
    private Integer designTexture = 0;
    private Integer glassColor = 0;
    private Integer overlay = 0;
    private Integer rotation = 0;
    private Float friction = Registration.FRAMEBLOCK.get().getFriction();
    private Float explosionResistance = Registration.FRAMEBLOCK.get().getExplosionResistance();
    private Boolean canSustainPlant = false;
    private Integer enchantPowerBonus = 0;

    private Boolean northVisible = true;
    private Boolean eastVisible = true;
    private Boolean southVisible = true;
    private Boolean westVisible = true;
    private Boolean upVisible = true;
    private Boolean downVisible = true;

    private static final Logger LOGGER = LogManager.getLogger();

    private static List<TagPacket<?>> initTagPackets() {
        List<TagPacket<?>> packets = new ArrayList<>();
        packets.add(new TagPacket<>("mimic", BlockState.class, Blocks.AIR.defaultBlockState()));
        packets.add(new TagPacket<>("texture", Integer.class, 0));
        packets.add(new TagPacket<>("design", Integer.class, 0));
        packets.add(new TagPacket<>("designTexture", Integer.class, 0));
        packets.add(new TagPacket<>("glassColor", Integer.class, 0));
        packets.add(new TagPacket<>("overlay", Integer.class, 0));
        packets.add(new TagPacket<>("rotation", Integer.class, 0));
        packets.add(new TagPacket<>("friction", Float.class, Registration.FRAMEBLOCK.get().getFriction()));
        packets.add(new TagPacket<>("explosionResistance", Float.class, Registration.FRAMEBLOCK.get().getExplosionResistance()));
        packets.add(new TagPacket<>("canSustainPlant", Boolean.class, false));
        packets.add(new TagPacket<>("enchantPowerBonus", Integer.class, 0));
        return packets;
    }

    public FrameBlockTile(BlockPos pos, BlockState state) {
        super(FRAMEBLOCK_TILE.get(), pos, state);
    }

    public FrameBlockTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public BlockState getMimic() {
        return this.mimic;
    }

    public Integer getDesign() {
        return this.design;
    }

    public <V> V set(V newValue) {
        setChanged();
        level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS);
        return newValue;
    }

    public Integer getDesignTexture() {
        return this.designTexture;
    }

    public void setMimic(BlockState mimic) {
        this.mimic = set(mimic);
    }

    public Integer getTexture() {
        return this.texture;
    }

    public void setDesign(Integer design) {
        this.design = set(design);
    }

    public Integer getGlassColor() {
        return this.glassColor;
    }

    public void setDesignTexture(Integer designTexture) {
        this.designTexture = set(designTexture);
    }

    public Integer getRotation() {
        return rotation;
    }

    public void setTexture(Integer texture) {
        this.texture = set(texture);
    }

    public void setVisibleSides(Direction dir, boolean isVisible) {
        switch (dir) {
            case DOWN:
                downVisible = isVisible;
                break;
            case UP:
                upVisible = isVisible;
                break;
            case NORTH:
                northVisible = isVisible;
                break;
            case WEST:
                westVisible = isVisible;
                break;
            case SOUTH:
                southVisible = isVisible;
                break;
            case EAST:
                eastVisible = isVisible;
                break;
            default:
                break;
        }
    }

    public List<Direction> getVisibleSides() {
        List<Direction> dir = new ArrayList<>();
        if (northVisible)
            dir.add(Direction.NORTH);
        if (eastVisible)
            dir.add(Direction.EAST);
        if (southVisible)
            dir.add(Direction.SOUTH);
        if (westVisible)
            dir.add(Direction.WEST);
        if (upVisible)
            dir.add(Direction.UP);
        if (downVisible)
            dir.add(Direction.DOWN);
        return dir;
    }

    public void setGlassColor(Integer colorNumber) {
        this.glassColor = set(colorNumber);
    }

    public Integer getOverlay() {
        return this.overlay;
    }

    public void setRotation(Integer rotation) {
        this.rotation = set(rotation);
    }

    public void setOverlay(Integer overlay) {
        this.overlay = set(overlay);
    }

    public Float getFriction() {
        return friction;
    }

    public void setFriction(Float friction) {
        this.friction = set(friction);
    }

    public Float getExplosionResistance() {
        return explosionResistance;
    }

    public void setExplosionResistance(Float explosionResistance) {
        this.explosionResistance = set(explosionResistance);
    }

    public Boolean getCanSustainPlant() {
        return canSustainPlant;
    }

    public void setCanSustainPlant(Boolean canSustainPlant) {
        this.canSustainPlant = set(canSustainPlant);
    }

    public Integer getEnchantPowerBonus() {
        return enchantPowerBonus;
    }

    public void setEnchantPowerBonus(Integer enchantPowerBonus) {
        this.enchantPowerBonus = set(enchantPowerBonus);
    }

    private static <V> V readDataType(CompoundTag tag, String tagElement, Class<V> classType, V defaultValue) {
        if (classType == BlockState.class) {
            return (V) NbtUtils.readBlockState(tag.getCompound(tagElement));
        }
        if (classType == Integer.class) {
            if (readInteger(tag) != 0)
                return (V) readInteger(tag);
            return (V) (Integer) tag.getInt(tagElement);
        }
        if (classType == Float.class) {
            return (V) (Float) tag.getFloat(tagElement);
        }
        if (classType == Boolean.class) {
            return (V) (Boolean) tag.getBoolean(tagElement);
        }
        return defaultValue;
    }

    //TODO LEGACY METHOD -> remove in 1.20
    private static Integer readInteger(CompoundTag tag) {
        if (!tag.contains("number", 8)) {
            return 0;
        } else {
            try {
                return Integer.parseInt(tag.getString("number"));
            } catch (NumberFormatException e) {
                LOGGER.error("Not a valid Number Format: " + tag.getString("number"));
                return 0;
            }
        }
    }

    private <V> V read(CompoundTag tag, String tagElement, Class<V> classType, V defaultValue) {
        if (!tag.contains(tagElement)) {
            return defaultValue;
        } else {
            try {
                return readDataType(tag, tagElement, classType, defaultValue);
            } catch (Exception e) {
                LOGGER.error("Not a valid " + tagElement + " Format: " + tag.getString(tagElement));
            }
        }
        return defaultValue;
    }

    private <V> V read(CompoundTag tag, TagPacket<V> tagPacket) {
        return read(tag, tagPacket.TAG_ELEMENT, tagPacket.CLASS_TYPE, tagPacket.DEFAULT);
    }

    private <V> void write(CompoundTag tag, String tagElement, V newElement) {
        if (newElement != null) {
            if (newElement.getClass() == Integer.class)
                tag.putInt(tagElement, (int) newElement);
            if (newElement.getClass() == Float.class)
                tag.putFloat(tagElement, (float) newElement);
            if (newElement.getClass() == Boolean.class)
                tag.putBoolean(tagElement, (boolean) newElement);
            if (newElement.getClass() == BlockState.class)
                tag.put(tagElement, NbtUtils.writeBlockState((BlockState) newElement));
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        onDataPacket(pkt);
    }

    private <V> void onDataPacket(ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        Class cls = FrameBlockTile.class;
        for (TagPacket<?> tagPacket : TAG_PACKETS) {
            Field[] fs = cls.getDeclaredFields();
            for (Field f : fs) {
                if (f.getName().equals(tagPacket.TAG_ELEMENT)) {
                    try {
                        V oldValue = (V) f.get(this);
                        V newValue = update(tag, tagPacket.TAG_ELEMENT, oldValue, (Class<V>) tagPacket.CLASS_TYPE, (V) tagPacket.DEFAULT);
                        f.set(this, newValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private <V> V update(CompoundTag tag, String tagElement, V oldValue, Class<V> classType, V defaultValue) {
        if (tag.contains(tagElement)) {
            V newValue = read(tag, tagElement, classType, defaultValue);
            if (!Objects.equals(oldValue, newValue)) {
                this.requestModelDataUpdate();
                level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS);
            }
            return newValue;
        }
        return null;
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(MIMIC, mimic)
                .with(TEXTURE, texture)
                .with(DESIGN, design)
                .with(DESIGN_TEXTURE, designTexture)
                .with(GLASS_COLOR, glassColor)
                .with(OVERLAY, overlay)
                .with(ROTATION, rotation)
                .with(NORTH_VISIBLE, northVisible)
                .with(EAST_VISIBLE, eastVisible)
                .with(SOUTH_VISIBLE, southVisible)
                .with(WEST_VISIBLE, westVisible)
                .with(UP_VISIBLE, upVisible)
                .with(DOWN_VISIBLE, downVisible)
                .build();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        Class cls = FrameBlockTile.class;
        for (TagPacket<?> tagPacket : TAG_PACKETS) {
            Field[] fs = cls.getDeclaredFields();
            for (Field f : fs) {
                if (f.getName().equals(tagPacket.TAG_ELEMENT)) {
                    try {
                        write(tag, tagPacket.TAG_ELEMENT, f.get(this));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        Class cls = FrameBlockTile.class;
        for (TagPacket<?> tagPacket : TAG_PACKETS) {
            Field[] fs = cls.getDeclaredFields();
            for (Field f : fs) {
                if (f.getName().equals(tagPacket.TAG_ELEMENT)) {
                    try {
                        f.set(this, read(tag, tagPacket));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        Class cls = FrameBlockTile.class;
        for (TagPacket<?> tagPacket : TAG_PACKETS) {
            Field[] fs = cls.getDeclaredFields();
            for (Field f : fs) {
                if (f.getName().equals(tagPacket.TAG_ELEMENT)) {
                    try {
                        write(tag, tagPacket.TAG_ELEMENT, f.get(this));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void clear() {
        this.setMimic(null);
    }

    public static class TagPacket<V> {
        public final String TAG_ELEMENT;
        public final Class<V> CLASS_TYPE;
        public final V DEFAULT;

        public TagPacket(String tagElement, Class<V> classType, V defaultValue) {
            this.TAG_ELEMENT = tagElement;
            this.CLASS_TYPE = classType;
            this.DEFAULT = defaultValue;
        }
    }
}
//========SOLI DEO GLORIA========//