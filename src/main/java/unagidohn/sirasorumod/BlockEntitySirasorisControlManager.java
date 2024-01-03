package unagidohn.sirasorumod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntitySirasorisControlManager extends BlockEntity {
    int gameFrame;

    public BlockEntitySirasorisControlManager(BlockPos pos, BlockState state) {
        super(SirasoruMOD.SIRASORIS_CONTROL_MANAGER_ENTITY.get(), pos, state);

        gameFrame = 0;
    }

    public void tick() {
        System.out.println("Game Frame " + gameFrame++);

        SirasoruMOD.sirasorisManager.Tick(gameFrame);

        gameFrame = gameFrame % 1000;
    }

    public void SetLevel(Level level){
        SirasoruMOD.sirasorisManager.SetLevel(level);
    }
}
