package com.github.smallinger.coppergolemlegacy.block;

import com.github.smallinger.coppergolemlegacy.block.entity.CopperGolemStatueBlockEntity;
import com.github.smallinger.coppergolemlegacy.entity.CopperGolemEntity;
import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

public class CopperGolemStatueBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Pose> POSE = EnumProperty.create("pose", Pose.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 14.0, 13.0);
    
    private final WeatheringCopper.WeatherState weatheringState;

    public CopperGolemStatueBlock(WeatheringCopper.WeatherState weatheringState, Properties properties) {
        super(properties);
        this.weatheringState = weatheringState;
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(POSE, Pose.STANDING)
                .setValue(WATERLOGGED, false)
        );
    }

    public WeatheringCopper.WeatherState getWeatheringState() {
        return this.weatheringState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSE, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Empty hand interaction - change pose
        if (stack.isEmpty()) {
            if (!level.isClientSide()) {
                Pose currentPose = state.getValue(POSE);
                Pose nextPose = currentPose.getNextPose();
                level.setBlock(pos, state.setValue(POSE, nextPose), Block.UPDATE_ALL);
                level.playSound(null, pos, ModSounds.COPPER_STATUE_HIT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return InteractionResult.SUCCESS;
        }
        
        // Axe interaction - restore golem
        if (stack.is(ItemTags.AXES) && !level.isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) level;
            
            if (level.getBlockEntity(pos) instanceof CopperGolemStatueBlockEntity statueEntity) {
                CopperGolemEntity golem = statueEntity.removeStatue(state, serverLevel);
                if (golem != null) {
                    level.removeBlock(pos, false);
                    serverLevel.addFreshEntity(golem);
                    level.playSound(null, pos, ModSounds.COPPER_STATUE_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
                    stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                    return InteractionResult.SUCCESS;
                }
            }
        }
        
        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CopperGolemStatueBlockEntity(pos, state);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public enum Pose implements StringRepresentable {
        STANDING("standing"),
        RUNNING("running"),
        SITTING("sitting"),
        STAR("star");

        private final String name;

        Pose(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public Pose getNextPose() {
            Pose[] poses = values();
            return poses[(this.ordinal() + 1) % poses.length];
        }
    }
}

