package com.github.smallinger.coppergolemlegacy.block.entity;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.block.CopperGolemStatueBlock;
import com.github.smallinger.coppergolemlegacy.entity.CopperGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CopperGolemStatueBlockEntity extends BlockEntity {
    @Nullable
    private Component customName;

    public CopperGolemStatueBlockEntity(BlockPos pos, BlockState state) {
        super(CopperGolemLegacy.COPPER_GOLEM_STATUE_BLOCK_ENTITY.get(), pos, state);
    }

    public void createStatue(CopperGolemEntity golem) {
        this.customName = golem.getCustomName();
        this.setChanged();
    }

    @Nullable
    public CopperGolemEntity removeStatue(BlockState state, ServerLevel level) {
        CopperGolemEntity golem = CopperGolemLegacy.COPPER_GOLEM.get().create(level);
        if (golem != null) {
            BlockPos blockPos = this.getBlockPos();
            golem.moveTo(
                blockPos.getX() + 0.5,
                blockPos.getY(),
                blockPos.getZ() + 0.5,
                state.getValue(CopperGolemStatueBlock.FACING).toYRot(),
                0.0F
            );
            golem.yHeadRot = golem.getYRot();
            golem.yBodyRot = golem.getYRot();
            golem.setCustomName(this.customName);
            golem.setWeatherState(((CopperGolemStatueBlock) state.getBlock()).getWeatheringState());
            return golem;
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.customName != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.customName));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("CustomName")) {
            this.customName = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove("CustomName");
    }
}

