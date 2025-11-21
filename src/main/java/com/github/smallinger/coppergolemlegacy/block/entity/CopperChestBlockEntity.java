package com.github.smallinger.coppergolemlegacy.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

import com.github.smallinger.coppergolemlegacy.CopperGolemLegacy;
import com.github.smallinger.coppergolemlegacy.ModSounds;

public class CopperChestBlockEntity extends ChestBlockEntity {
    private final ContainerOpenersCounter openersCounter;
    
    public CopperChestBlockEntity(BlockPos pos, BlockState state) {
        super(CopperGolemLegacy.COPPER_CHEST_BLOCK_ENTITY.get(), pos, state);
        
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen(Level level, BlockPos blockPos, BlockState blockState) {
                // Sound wird in openerCountChanged gespielt
            }

            @Override
            protected void onClose(Level level, BlockPos blockPos, BlockState blockState) {
                // Sound wird in openerCountChanged gespielt
            }

            @Override
            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
                // Play sound only when first opening (oldCount == 0, newCount > 0)
                // or last closing (newCount == 0, oldCount > 0)
                if (oldCount == 0 && newCount > 0) {
                    // Opening
                    level.playSound(
                        null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ModSounds.COPPER_CHEST_OPEN.get(),
                        SoundSource.BLOCKS,
                        0.5f,
                        level.random.nextFloat() * 0.1f + 0.9f
                    );
                } else if (newCount == 0 && oldCount > 0) {
                    // Closing
                    level.playSound(
                        null,
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        ModSounds.COPPER_CHEST_CLOSE.get(),
                        SoundSource.BLOCKS,
                        0.5f,
                        level.random.nextFloat() * 0.1f + 0.9f
                    );
                }
                level.blockEvent(pos, state.getBlock(), 1, newCount);
            }

            @Override
            protected boolean isOwnContainer(Player player) {
                if (!(player.containerMenu instanceof ChestMenu)) {
                    return false;
                } else {
                    Container container = ((ChestMenu) player.containerMenu).getContainer();
                    return container == CopperChestBlockEntity.this || 
                           container instanceof CompoundContainer && 
                           ((CompoundContainer) container).contains(CopperChestBlockEntity.this);
                }
            }
        };
    }
    
    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.getLevel() != null) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator() && this.getLevel() != null) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    public void recheckOpen() {
        if (!this.remove && this.getLevel() != null) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    
    /**
     * Check if the chest is currently open
     * @return true if the chest has viewers
     */
    public boolean isChestOpen() {
        return this.openersCounter.getOpenerCount() > 0;
    }
}
