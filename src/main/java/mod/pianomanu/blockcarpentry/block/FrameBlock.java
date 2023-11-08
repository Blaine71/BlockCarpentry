package mod.pianomanu.blockcarpentry.block;

import mod.pianomanu.blockcarpentry.item.ChiselItem;
import mod.pianomanu.blockcarpentry.item.PaintbrushItem;
import mod.pianomanu.blockcarpentry.setup.Registration;
import mod.pianomanu.blockcarpentry.setup.config.BCModConfig;
import mod.pianomanu.blockcarpentry.tileentity.FrameBlockTile;
import mod.pianomanu.blockcarpentry.util.CornerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.extensions.IForgeBlockState;

import javax.annotation.Nullable;


/**
 * Main class for frameblocks - all important block info can be found here
 * This class is the most basic one for all frame blocks, so you can find most of the documentation here
 *
 * @author PianoManu
 * @version 1.6 11/08/23
 */
@SuppressWarnings("deprecation")
public class FrameBlock extends AbstractFrameBlock implements IForgeBlockState, SimpleWaterloggedBlock, IFrameBlock {

    /**
     * classic constructor, all default values are set
     *
     * @param properties determined when registering the block (see {@link Registration}
     */
    public FrameBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CONTAINS_BLOCK, Boolean.FALSE).setValue(LIGHT_LEVEL, 0).setValue(WATERLOGGED, false));
    }

    /**
     * Assign needed blockstates to frame block - we need "contains_block" and "light_level", both because we have to check for blockstate changes
     */
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, CONTAINS_BLOCK, LIGHT_LEVEL);
    }

    /**
     * When placed, this method is called and a new FrameBlockTile is created
     * This is needed to store a block inside the frame, change its light value etc.
     *
     * @param pos   regardless of the position, we always create the BlockEntity
     * @param state regardless of its state, we always create the BlockEntity
     * @return the new empty FrameBlock-BlockEntity
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FrameBlockTile(pos, state);
    }

    /**
     * This method is called, whenever the state of the block changes (e.g. the block is harvested)
     *
     * @param state    old blockstate
     * @param levelIn  level of the block
     * @param pos      block position
     * @param newState new blockstate
     * @param isMoving whether the block has some sort of motion (should never be moving - false)
     */
    @Override
    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            dropContainedBlock(levelIn, pos);

            super.onRemove(state, levelIn, pos, newState, isMoving);
        }
        if (state.getValue(WATERLOGGED)) {
            levelIn.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitresult) {
        if (player.getItemInHand(hand).getItem() instanceof ChiselItem chiselItem)
            CornerUtils.changeBoxSize(state, level, pos, player, hitresult.getLocation(), hitresult.getDirection(), chiselItem.shouldShrink());
        if (!level.isClientSide && !player.isCrouching() && player.getItemInHand(hand).getItem() instanceof PaintbrushItem && PaintbrushItem.setRectangleCustom(level, player, pos))
            return InteractionResult.SUCCESS;
        updateShape(state, level, pos);
        return super.use(state, level, pos, player, hand, hitresult);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        FluidState fluidstate = context.getLevel().getFluidState(blockpos);
        if (fluidstate.getType() == Fluids.WATER) {
            return this.defaultBlockState().setValue(WATERLOGGED, fluidstate.isSource());
        } else {
            return this.defaultBlockState();
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (state.getValue(CONTAINS_BLOCK) && !context.isHoldingItem(Registration.CHISEL.get())) {
            return this.getShape(state, getter, pos);
        }
        return Shapes.block();
    }


    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor levelIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            levelIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelIn));
        }
        updateShape(stateIn, levelIn, currentPos);

        return super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        if (context.isHoldingItem(Registration.CHISEL.get()))
            return Shapes.block();
        return this.getShape(state, getter, pos);
    }

    private VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos) {
        if (state.getValue(CONTAINS_BLOCK) && BCModConfig.SHOW_COMPLEX_BOUNDING_BOX.get()) {
            BlockEntity be = getter.getBlockEntity(pos);
            if (be instanceof FrameBlockTile fte) {
                return fte.getShape();
            }
        }
        return Shapes.block();
    }

    private void updateShape(BlockState state, LevelAccessor level, BlockPos pos) {
        if (state.getValue(CONTAINS_BLOCK) && BCModConfig.SHOW_COMPLEX_BOUNDING_BOX.get()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FrameBlockTile fte) {
                fte.updateShape();
            }
        }
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return this.getShape(state, blockGetter, pos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return this.getShape(state, blockGetter, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return this.getShape(state, blockGetter, pos);
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }


}
//========SOLI DEO GLORIA========//