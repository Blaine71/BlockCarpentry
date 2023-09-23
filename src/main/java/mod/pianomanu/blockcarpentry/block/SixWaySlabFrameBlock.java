package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.item.BaseFrameItem;
import mod.pianomanu.blockcarpentry.item.BaseIllusionItem;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.TwoBlocksFrameBlockTile;
import mod.pianomanu.blockcarpentry.util.BCBlockStateProperties;
import mod.pianomanu.blockcarpentry.util.BlockSavingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Main class for frame "slabs", they can be placed in six different ways (that's the reason for this class name) - all important block info can be found here
 * Visit {@link FrameBlock} for a better documentation
 *
 * @author PianoManu
 * @version 1.5 09/23/23
 */
@SuppressWarnings("deprecation")
public class SixWaySlabFrameBlock extends AbstractSixWayFrameBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty CONTAINS_BLOCK = BCBlockStateProperties.CONTAINS_BLOCK;
    public static final BooleanProperty CONTAINS_2ND_BLOCK = BCBlockStateProperties.CONTAINS_2ND_BLOCK;
    public static final IntegerProperty LIGHT_LEVEL = BCBlockStateProperties.LIGHT_LEVEL;
    public static final BooleanProperty DOUBLE_SLAB = BCBlockStateProperties.DOUBLE;
    //everything is inverted because when placing, we would need to take the opposite - I figured it out when I completed my work and I don't want to change everything again
    protected static final VoxelShape BOTTOM = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape TOP = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape EAST = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape WEST = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape CUBE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public SixWaySlabFrameBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(CONTAINS_BLOCK, Boolean.FALSE).setValue(LIGHT_LEVEL, 0).setValue(WATERLOGGED, false).setValue(DOUBLE_SLAB, false).setValue(CONTAINS_2ND_BLOCK, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, DOUBLE_SLAB, CONTAINS_2ND_BLOCK);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(DOUBLE_SLAB))
            return CUBE;
        return switch (state.getValue(FACING)) {
            case EAST -> EAST;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case UP -> TOP;
            default -> BOTTOM;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        BlockState blockState = context.getLevel().getBlockState(blockpos);
        if (blockState.is(this)) {
            return blockState.setValue(DOUBLE_SLAB, true).setValue(WATERLOGGED, false);
        }
        if (Objects.requireNonNull(context.getPlayer()).isCrouching() && BCModConfig.SNEAK_FOR_VERTICAL_SLABS.get() || !Objects.requireNonNull(context.getPlayer()).isCrouching() && !BCModConfig.SNEAK_FOR_VERTICAL_SLABS.get()) {
            if (fluidstate.getType() == Fluids.WATER) {
                return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.isSource());
            } else {
                return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
            }
        } else {
            BlockState blockstate1 = this.defaultBlockState().setValue(FACING, Direction.UP).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
            Direction direction = context.getClickedFace();
            return direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double) blockpos.getY() > 0.5D)) ? blockstate1 : blockstate1.setValue(FACING, Direction.DOWN);
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return getShape(state, getter, pos, context);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack itemstack = useContext.getItemInHand();
        boolean slabType = state.getValue(DOUBLE_SLAB);
        if (!slabType && itemstack.is(this.asItem())) {
            if (useContext.replacingClickedOnBlock()) {
                Direction direction = useContext.getClickedFace();
                return switch (state.getValue(FACING)) {
                    case EAST -> direction == Direction.EAST;
                    case SOUTH -> direction == Direction.SOUTH;
                    case WEST -> direction == Direction.WEST;
                    case NORTH -> direction == Direction.NORTH;
                    case UP -> direction == Direction.UP;
                    default -> direction == Direction.DOWN;
                };
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TwoBlocksFrameBlockTile(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitresult) {
        return frameUse(state, level, pos, player, hand, hitresult);
    }

    @Override
    public boolean changeMimic(BlockState state, Level level, BlockPos pos, Player player, ItemStack itemStack) { //TODO fix removing and replacing for double slabs -> somehow saves old mimic in model...
        if (state.getValue(BCBlockStateProperties.CONTAINS_2ND_BLOCK) || itemStack.getItem() instanceof BaseFrameItem || itemStack.getItem() instanceof BaseIllusionItem) {
            return false;
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        int count = itemStack.getCount();
        Block heldBlock = ((BlockItem) itemStack.getItem()).getBlock();
        if (tileEntity instanceof TwoBlocksFrameBlockTile && !itemStack.isEmpty() && BlockSavingHelper.isValidBlock(heldBlock) && !state.getValue(CONTAINS_2ND_BLOCK)) {
            BlockState handBlockState = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            insertBlock(level, pos, state, handBlockState);
            if (!player.isCreative())
                itemStack.setCount(count - 1);
        }
        return true;
    }

    public boolean removeBlock(Level level, BlockPos pos, BlockState state, ItemStack itemStack, Player player) {
        if (itemStack.getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isCrouching())) {
            if (!player.isCreative())
                this.dropContainedBlock(level, pos);
            else {
                this.clearTile(level, pos);
            }
            state = state.setValue(CONTAINS_BLOCK, Boolean.FALSE).setValue(CONTAINS_2ND_BLOCK, Boolean.FALSE);
            level.setBlock(pos, state, 2);
            return true;
        }
        return false;
    }

    public void clearTile(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof TwoBlocksFrameBlockTile frameBlockEntity) {
                frameBlockEntity.clear();
            }
        }
    }

    public void dropContainedBlock(Level levelIn, BlockPos pos) {
        if (!levelIn.isClientSide) {
            BlockEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof TwoBlocksFrameBlockTile frameBlockEntity) {
                BlockState blockState = frameBlockEntity.getMimic_1();
                if (!(blockState == null)) {
                    dropItemStackInWorld(levelIn, pos, blockState);
                }
                blockState = frameBlockEntity.getMimic_2();
                if (!(blockState == null)) {
                    dropItemStackInWorld(levelIn, pos, blockState);
                }
                frameBlockEntity.clear();
            }
        }
    }

    public void insertBlock(Level levelIn, BlockPos pos, BlockState state, BlockState handBlock) {
        BlockEntity tileentity = levelIn.getBlockEntity(pos);
        if (tileentity instanceof TwoBlocksFrameBlockTile) {
            if (!state.getValue(CONTAINS_BLOCK)) {
                TwoBlocksFrameBlockTile frameBlockEntity = (TwoBlocksFrameBlockTile) tileentity;
                frameBlockEntity.clear();
                frameBlockEntity.setMimic_1(handBlock);
                levelIn.setBlock(pos, state.setValue(CONTAINS_BLOCK, Boolean.TRUE), 2);
            } else if (state.getValue(DOUBLE_SLAB)) {
                TwoBlocksFrameBlockTile frameBlockEntity = (TwoBlocksFrameBlockTile) tileentity;
                frameBlockEntity.setMimic_2(handBlock);
                levelIn.setBlock(pos, state.setValue(CONTAINS_2ND_BLOCK, Boolean.TRUE), 2);
            }
        }
    }

    @Override
    public void onRemove(BlockState state, @Nonnull Level levelIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            dropContainedBlock(levelIn, pos);

            super.onRemove(state, levelIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(LIGHT_LEVEL) > 15) {
            return 15;
        }
        return state.getValue(LIGHT_LEVEL);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @Nonnull
    public BlockState updateShape(BlockState stateIn, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull LevelAccessor levelIn, @Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }
}
//========SOLI DEO GLORIA========//