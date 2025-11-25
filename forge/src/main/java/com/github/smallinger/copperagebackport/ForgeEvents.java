package com.github.smallinger.copperagebackport;

import com.github.smallinger.copperagebackport.event.CopperGolemSpawnLogic;
import com.github.smallinger.copperagebackport.event.PlayerJoinHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgeEvents {
    
    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        Level level = (Level) event.getLevel();
        Direction direction = Direction.NORTH;
        if (event.getEntity() != null) {
            direction = Direction.fromYRot(event.getEntity().getYRot());
        }
        CopperGolemSpawnLogic.handleBlockPlaced(level, event.getPos(), event.getPlacedBlock(), direction);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            PlayerJoinHandler.onPlayerJoin(serverPlayer);
        }
    }
}
