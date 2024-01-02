package unagidohn.sirasorumod;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class SirasorisManager {
    final int CELL_NUM_X = 10;
    final int CELL_NUM_Y = 20;

    int[][] cellArray;

    final int BLOCK_EMPTY = 4;

    int axisXDir = -1;

    int axisYDir = 1;

    BlockPos managerBlockPos;
    BlockPos sirasorisBasePos;
    Level sirasorisExistLevel;
    Player sirasorisCreatedPlayer;

    public SirasorisManager(){
        cellArray = new int[CELL_NUM_Y][];
        for(int yIndex = 0; yIndex < CELL_NUM_Y; yIndex++ ){
            cellArray[yIndex] = new int[CELL_NUM_X];
        }

        for(int yIndex = 0; yIndex < CELL_NUM_Y; yIndex++ ){
            for(int xIndex = 0; xIndex < CELL_NUM_X; xIndex++ ){
                cellArray[yIndex][xIndex] = BLOCK_EMPTY;
            }
        }
    }
    public InteractionResult Init(UseOnContext useOnContext){
        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        ItemStack itemstack = useOnContext.getItemInHand();

        if(level.isClientSide()) {
           return InteractionResult.PASS;
        }


        System.out.println("item_use_player is " + player.getName());
        System.out.println("click_pos is " + blockpos.getX() + "," + blockpos.getY() + "," + blockpos.getZ());
        System.out.println("click_pos_block is " + blockstate.getBlock().getName());

        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
        }

        sirasorisExistLevel = level;
        managerBlockPos = blockpos;
        sirasorisBasePos = new BlockPos(managerBlockPos.getX(), managerBlockPos.getY() + 1, managerBlockPos.getZ());
        sirasorisCreatedPlayer = player;

        BlockPos createBlockPos = managerBlockPos;
        BlockState createBlockState = null;

        createBlockPos = new BlockPos(managerBlockPos.getX(), managerBlockPos.getY(), managerBlockPos.getZ());
        createBlockState = GetSirasoruBlockState(999);

        level.setBlock(createBlockPos, createBlockState, 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, createBlockPos, GameEvent.Context.of(player, createBlockState));

        RefreshCell();

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void RefreshCell(){
        BlockPos createBlockPos = managerBlockPos;
        BlockState createBlockState = null;

        for(int yIndex = 0; yIndex < CELL_NUM_Y; yIndex++ ){
            for(int xIndex = 0; xIndex < CELL_NUM_X; xIndex++ ){
                createBlockPos = new BlockPos(sirasorisBasePos.getX() + axisXDir * xIndex, sirasorisBasePos.getY() + axisYDir * yIndex, sirasorisBasePos.getZ());
                createBlockState = GetSirasoruBlockState(cellArray[yIndex][xIndex]);

                sirasorisExistLevel.setBlock(createBlockPos, createBlockState, 11);
                sirasorisExistLevel.gameEvent(GameEvent.BLOCK_CHANGE, createBlockPos, GameEvent.Context.of(sirasorisCreatedPlayer, createBlockState));
            }
        }
    }

    public void SetLevel(Level level){
        sirasorisExistLevel = level;
    }

    public void Tick(){

    }

    private BlockState GetSirasoruBlockState(int id){
        switch(id){
            case 0:
                return SirasoruMOD.SIRASORU_BLOCK_0.get().defaultBlockState();
            case 1:
                return SirasoruMOD.SIRASORU_BLOCK_1.get().defaultBlockState();
            case 2:
                return SirasoruMOD.SIRASORU_BLOCK_2.get().defaultBlockState();
            case 3:
                return SirasoruMOD.SIRASORU_BLOCK_3.get().defaultBlockState();
            case 4:
                return SirasoruMOD.BLOCK_MUJI.get().defaultBlockState();
            case 999:
                return SirasoruMOD.SIRASORIS_CONTROL_MANAGER.get().defaultBlockState();
            default:
                return SirasoruMOD.SIRASORU_BLOCK_0.get().defaultBlockState();
        }
    }
}
