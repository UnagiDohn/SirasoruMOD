package unagidohn.sirasorumod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntitySirasorisControlManager extends BlockEntity {
    int gameFrame;
    SirasorisManager sirasorisManager;

    public BlockEntitySirasorisControlManager(BlockPos pos, BlockState state) {
        super(SirasoruMOD.SIRASORIS_CONTROL_MANAGER_ENTITY.get(), pos, state);

        sirasorisManager = new SirasorisManager();
        gameFrame = 0;

        sirasorisManager.Init(pos, state);
    }

    public void tick() {
        System.out.println("Game Frame " + gameFrame++);
    }

    public void SetLevel(Level level){

    }
}
