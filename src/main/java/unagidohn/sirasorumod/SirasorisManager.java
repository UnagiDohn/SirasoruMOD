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
    boolean isGameEnabled = false;
    final int CELL_NUM_X = 10;
    final int CELL_NUM_Y = 20;

    final int ARRAY_NUM_X = 16;
    final int ARRAY_NUM_Y = 26;

    final int CELL_START_X = 3;
    final int CELL_START_Y = 3;

    int[][] cellArray;
    int[][] renderCellArray;

    final int BLOCK_EMPTY = 4;
    final int BLOCK_INVALID = 5;

    int axisXDir = -1;

    int axisYDir = 1;

    BlockPos managerBlockPos;
    BlockPos sirasorisBasePos;
    Level sirasorisExistLevel;
    Player sirasorisCreatedPlayer;

    enum GameState{
        START,
        CALL_MINO,
        FALL_MINO,
        END_MINO,
        END,
        IDLE
    };
    GameState gameState;

    enum Mino{
        Sikaku,
        Tji,
        Lji,
        Jji,
        Bou,
        Num
    }

    Mino fallingMino;
    int fallingMinoX;
    int fallingMinoY;

    int fallingMinoBlockId;
    int nextMinoBlockId;
    public SirasorisManager(){
        cellArray = new int[ARRAY_NUM_Y][];
        renderCellArray = new int[ARRAY_NUM_Y][];
        for(int yIndex = 0; yIndex < ARRAY_NUM_Y; yIndex++ ){
            cellArray[yIndex] = new int[ARRAY_NUM_X];
            renderCellArray[yIndex] = new int[ARRAY_NUM_X];
        }

        for(int yIndex = 0; yIndex < ARRAY_NUM_Y; yIndex++ ){
            for(int xIndex = 0; xIndex < ARRAY_NUM_X; xIndex++ ){
                cellArray[yIndex][xIndex] = BLOCK_INVALID;
            }
        }

        for(int yIndex = CELL_START_Y; yIndex < CELL_START_Y+ CELL_NUM_Y; yIndex++ ){
            for(int xIndex = CELL_START_X; xIndex < CELL_START_X + CELL_NUM_X; xIndex++ ){
                cellArray[yIndex][xIndex] = BLOCK_EMPTY;
            }
        }

        for(int yIndex = CELL_START_Y+ CELL_NUM_Y; yIndex < ARRAY_NUM_Y; yIndex++ ){
            for(int xIndex = CELL_START_X; xIndex < CELL_START_X + CELL_NUM_X; xIndex++ ){
                cellArray[yIndex][xIndex] = BLOCK_EMPTY;
            }
        }
    }
    public InteractionResult Init(UseOnContext useOnContext){
        isGameEnabled = true;
        gameState = GameState.START;

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

        InitMino();

        RefreshCell();

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void RefreshCell(){
        BlockPos createBlockPos = managerBlockPos;
        BlockState createBlockState = null;

        for(int yIndex = 0; yIndex < ARRAY_NUM_Y; yIndex++ ){
            for(int xIndex = 0; xIndex < ARRAY_NUM_X; xIndex++ ){
                renderCellArray[yIndex][xIndex] = cellArray[yIndex][xIndex];
            }
        }

        RenderFallingMino();

        if(true){
            for (int yIndex = 0; yIndex < CELL_NUM_Y; yIndex++) {
                for (int xIndex = 0; xIndex < CELL_NUM_X; xIndex++) {
                    createBlockPos = new BlockPos(sirasorisBasePos.getX() + axisXDir * xIndex, sirasorisBasePos.getY() + axisYDir * yIndex, sirasorisBasePos.getZ());
                    createBlockState = GetSirasoruBlockState(renderCellArray[CELL_START_Y + yIndex][CELL_START_X + xIndex]);

                    sirasorisExistLevel.setBlock(createBlockPos, createBlockState, 11);
                    sirasorisExistLevel.gameEvent(GameEvent.BLOCK_CHANGE, createBlockPos, GameEvent.Context.of(sirasorisCreatedPlayer, createBlockState));
                }
            }
        }else {
            for (int yIndex = 0; yIndex < ARRAY_NUM_Y; yIndex++) {
                for (int xIndex = 0; xIndex < ARRAY_NUM_X; xIndex++) {
                    createBlockPos = new BlockPos(sirasorisBasePos.getX() + axisXDir * xIndex, sirasorisBasePos.getY() + axisYDir * yIndex, sirasorisBasePos.getZ());
                    createBlockState = GetSirasoruBlockState(renderCellArray[yIndex][xIndex]);

                    sirasorisExistLevel.setBlock(createBlockPos, createBlockState, 11);
                    sirasorisExistLevel.gameEvent(GameEvent.BLOCK_CHANGE, createBlockPos, GameEvent.Context.of(sirasorisCreatedPlayer, createBlockState));
                }
            }
        }

    }

    private void RenderFallingMino(){
        for(int yIndex = 0; yIndex < 4; yIndex++){
            for(int xIndex = 0; xIndex < 4; xIndex++) {
                if(minoArray[fallingMino.ordinal()][yIndex][xIndex] == 1){
                    renderCellArray[fallingMinoY + yIndex][fallingMinoX + xIndex] = fallingMinoBlockId;
                }
            }
        }
    }

    public void SetLevel(Level level){
        sirasorisExistLevel = level;
    }

    public void Tick(int gameFrame){
        if(gameFrame % 10 != 0){
            return;
        }

        if(!isGameEnabled){
            return;
        }
        UpdateGame();
        RefreshCell();
    }

    private void UpdateGame(){
        switch(gameState){
            case START:
                gameState = GameState.CALL_MINO;
                break;
            case CALL_MINO:
                fallingMinoX = 7;
                fallingMinoY = 22;
                fallingMinoBlockId = nextMinoBlockId % 4;
                nextMinoBlockId++;
                gameState = GameState.FALL_MINO;
            case FALL_MINO:
            {
                int nextFallingMinoY = fallingMinoY - 1;
                boolean isValid = IsValidMinoPos(fallingMinoX, nextFallingMinoY, fallingMino);

                if(!isValid){
                    WriteCurrentMinoToCell();
                    gameState = GameState.END_MINO;
                }else{
                    fallingMinoY = nextFallingMinoY;
                }
            }
            break;
            case END_MINO:
                gameState = GameState.CALL_MINO;
                break;
            default:
                break;
        }
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
            case 5:
                return SirasoruMOD.SIRASORU_BLOCK_0.get().defaultBlockState();
            case 999:
                return SirasoruMOD.SIRASORIS_CONTROL_MANAGER.get().defaultBlockState();
            default:
                return SirasoruMOD.SIRASORU_BLOCK_0.get().defaultBlockState();
        }
    }

    private boolean IsValidMinoPos(int baseX, int baseY, Mino mino){
        for(int yIndex = 0; yIndex < 4; yIndex++){
            for(int xIndex = 0; xIndex < 4; xIndex++) {
                if(minoArray[mino.ordinal()][yIndex][xIndex] == 1){
                    if(cellArray[baseY + yIndex][baseX + xIndex] != BLOCK_EMPTY){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void WriteCurrentMinoToCell(){
        for(int yIndex = 0; yIndex < 4; yIndex++){
            for(int xIndex = 0; xIndex < 4; xIndex++) {
                if(minoArray[fallingMino.ordinal()][yIndex][xIndex] == 1){
                    cellArray[fallingMinoY + yIndex][fallingMinoX + xIndex] = fallingMinoBlockId;
                }
            }
        }
    }

    int[][][] minoArray;
    private void InitMino(){
        minoArray = new int[Mino.Num.ordinal()][][];
        minoArray[Mino.Sikaku.ordinal()] = new int[][]{
                {1, 1, 0, 0},
                {1, 1, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };

        fallingMinoX = 5;
        fallingMinoY = 10;
        fallingMino = Mino.Sikaku;
        fallingMinoBlockId = 0;
        nextMinoBlockId = 0;
    }
}
