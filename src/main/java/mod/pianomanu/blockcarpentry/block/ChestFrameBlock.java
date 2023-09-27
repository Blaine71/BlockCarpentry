package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.item.BaseFrameItem;
import mod.pianomanu.blockcarpentry.item.BaseIllusionItem;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.ChestFrameBlockEntity;
import mod.pianomanu.blockcarpentry.util.BlockAppearanceHelper;
import mod.pianomanu.blockcarpentry.util.BlockModificationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Main class for frame chests - all important block info can be found here
 * Visit {@link FrameBlock} for a better documentation
 *
 * @author PianoManu
 * @version 1.6 09/27/23
 */
public class ChestFrameBlock extends FrameBlock implements SimpleWaterloggedBlock {
    private static final VoxelShape INNER_CUBE = Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);
    private static final VoxelShape BOTTOM_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 2.0);
    private static final VoxelShape BOTTOM_EAST = Block.box(14.0, 0.0, 2.0, 16.0, 2.0, 14.0);
    private static final VoxelShape BOTTOM_SOUTH = Block.box(0.0, 0.0, 14.0, 16.0, 2.0, 16.0);
    private static final VoxelShape BOTTOM_WEST = Block.box(0.0, 0.0, 2.0, 2.0, 2.0, 14.0);
    private static final VoxelShape TOP_NORTH = Block.box(0.0, 14.0, 0.0, 16.0, 16.0, 2.0);
    private static final VoxelShape TOP_EAST = Block.box(14.0, 14.0, 2.0, 16.0, 16.0, 14.0);
    private static final VoxelShape TOP_SOUTH = Block.box(0.0, 14.0, 14.0, 16.0, 16.0, 16.0);
    private static final VoxelShape TOP_WEST = Block.box(0.0, 14.0, 2.0, 2.0, 16.0, 14.0);
    private static final VoxelShape NW_PILLAR = Block.box(0.0, 2.0, 0.0, 2.0, 14.0, 2.0);
    private static final VoxelShape SW_PILLAR = Block.box(0.0, 2.0, 14.0, 2.0, 14.0, 16.0);
    private static final VoxelShape NE_PILLAR = Block.box(14.0, 2.0, 0.0, 16.0, 14.0, 2.0);
    private static final VoxelShape SE_PILLAR = Block.box(14.0, 2.0, 14.0, 16.0, 14.0, 16.0);
    private static final VoxelShape CHEST = Shapes.or(INNER_CUBE, BOTTOM_EAST, BOTTOM_SOUTH, BOTTOM_WEST, BOTTOM_NORTH, TOP_EAST, TOP_SOUTH, TOP_WEST, TOP_NORTH, NW_PILLAR, SW_PILLAR, NE_PILLAR, SE_PILLAR);

    public ChestFrameBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CONTAINS_BLOCK, false).setValue(LIGHT_LEVEL, 0).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, CONTAINS_BLOCK, LIGHT_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        if (fluidstate.getType() == Fluids.WATER) {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, fluidstate.isSource());
        } else {
            return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChestFrameBlockEntity(Registration.CHEST_FRAME_TILE.get(), pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitresult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND) {
            if (!level.isClientSide) {
                return frameUseServer(state, level, pos, player, itemStack, hitresult);
            }
            return frameUseClient(state, level, pos, player, itemStack, hitresult);
        }
        return InteractionResult.FAIL;
    }

    public InteractionResult frameUseServer(BlockState state, Level level, BlockPos pos, Player player, ItemStack itemStack, BlockHitResult hitresult) {
        if (removeBlock(level, pos, state, itemStack, player))
            return InteractionResult.SUCCESS;
        if (state.getValue(CONTAINS_BLOCK)) {
            if (BlockAppearanceHelper.setAll(itemStack, state, level, pos, player) || BlockModificationHelper.setAll(itemStack, (ChestFrameBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos)), player))
                return InteractionResult.CONSUME;
        }
        if (itemStack.getItem() instanceof BlockItem) {
            if (changeMimic(state, level, pos, player, itemStack))
                return InteractionResult.SUCCESS;
        }
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof ChestFrameBlockEntity && state.getValue(CONTAINS_BLOCK)) {
            return chestBehavior(state, level, pos, player, itemStack);
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult chestBehavior(BlockState state, Level level, BlockPos pos, Player player, ItemStack itemStack) {
        MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
        if (menuprovider != null) {
            player.openMenu(menuprovider);
            player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
            PiglinAi.angerNearbyPiglins(player, true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult frameUseClient(BlockState state, Level level, BlockPos pos, Player player, ItemStack itemStack, BlockHitResult hitresult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChestFrameBlockEntity && state.getValue(CONTAINS_BLOCK)) {
            if (!(itemStack.getItem() instanceof BaseFrameItem || itemStack.getItem() instanceof BaseIllusionItem)) {
                MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
                if (menuprovider != null) {
                    player.openMenu(menuprovider);
                    player.awardStat(Stats.CUSTOM.get(Stats.OPEN_CHEST));
                    PiglinAi.angerNearbyPiglins(player, true);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.CONSUME;
            }
        }
        return super.frameUseClient(state, level, pos, player, itemStack, hitresult);
    }

    public boolean removeBlock(Level level, BlockPos pos, BlockState state, ItemStack itemStack, Player player) {
        if (itemStack.getItem() == Registration.HAMMER.get() || (!BCModConfig.HAMMER_NEEDED.get() && player.isCrouching())) {
            if (!player.isCreative())
                this.dropContainedBlock(level, pos);
            state = state.setValue(CONTAINS_BLOCK, Boolean.FALSE);
            level.setBlock(pos, state, 2);
            return true;
        }
        return false;
    }

    @Override
    public void dropContainedBlock(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof ChestFrameBlockEntity) {
                ChestFrameBlockEntity frameBlockEntity = (ChestFrameBlockEntity) tileentity;
                BlockState blockState = frameBlockEntity.getMimic();
                if (!(blockState == null)) {
                    level.levelEvent(1010, pos, 0);
                    frameBlockEntity.clear();
                    float f = 0.7F;
                    double d0 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
                    double d1 = (level.random.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
                    double d2 = (double) (level.random.nextFloat() * 0.7F) + (double) 0.15F;
                    ItemStack itemstack1 = new ItemStack(blockState.getBlock());
                    ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
                    itementity.setDefaultPickUpDelay();
                    level.addFreshEntity(itementity);
                    frameBlockEntity.clear();
                }
            }
        }
    }

    @Override
    public void insertBlock(Level levelIn, BlockPos pos, BlockState state, BlockState handBlock) {
        BlockEntity tileentity = levelIn.getBlockEntity(pos);
        if (tileentity instanceof ChestFrameBlockEntity) {
            ChestFrameBlockEntity frameBlockEntity = (ChestFrameBlockEntity) tileentity;
            frameBlockEntity.clear();
            frameBlockEntity.setMimic(handBlock);
            levelIn.setBlock(pos, state.setValue(CONTAINS_BLOCK, Boolean.TRUE), 2);
        }
    }

    @Override
    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity te = levelIn.getBlockEntity(pos);
            if (te instanceof ChestFrameBlockEntity) {
                dropContainedBlock(levelIn, pos);
                Containers.dropContents(levelIn, pos, ((ChestFrameBlockEntity) te).getItems());
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return CHEST;
    }

    @Override
    public boolean isCorrectTileInstance(BlockEntity blockEntity) {
        return blockEntity instanceof ChestFrameBlockEntity;
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return super.getFriction(state, level, pos, entity);
    }

    @Override
    public boolean executeModifications(BlockState state, Level level, BlockPos pos, Player player, ItemStack itemStack) {
        return BlockAppearanceHelper.setAll(itemStack, state, level, pos, player) || getTile(level, pos) != null && BlockModificationHelper.setAll(itemStack, getTile(level, pos), player, true, false);
    }
}
//========SOLI DEO GLORIA========//