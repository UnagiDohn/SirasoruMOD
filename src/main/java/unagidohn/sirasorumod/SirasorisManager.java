package unagidohn.sirasorumod;

import mcp.client.Start;
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
import java.util.Random;
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

    final int ROTATION_NUM = 4;

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
        Sji,
        Sji_Gyaku,
        Num
    }

    Mino fallingMino;
    int fallingMinoX;
    int fallingMinoY;
    int fallingMinoRotation;
    int fallingMinoBlockId;
    int nextMinoBlockId;
    int lineCompleteNum;
    int gameFrame;

    public SirasorisManager(){
        cellArray = new int[ARRAY_NUM_Y][];
        renderCellArray = new int[ARRAY_NUM_Y][];
        for(int yIndex = 0; yIndex < ARRAY_NUM_Y; yIndex++ ){
            cellArray[yIndex] = new int[ARRAY_NUM_X];
            renderCellArray[yIndex] = new int[ARRAY_NUM_X];
        }

        ClearBlock();
    }

    private void ClearBlock(){
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
        gameState = GameState.IDLE;

        Level level = useOnContext.getLevel();
        BlockPos blockpos = useOnContext.getClickedPos();
        Player player = useOnContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        ItemStack itemstack = useOnContext.getItemInHand();

        if(level.isClientSide()) {
           return InteractionResult.PASS;
        }

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

        // しらそりすロゴを書く.
        WriteSirasorisLogo();
        lineCompleteNum = 0;
        gameFrame = 0;

        RefreshCell();

        return InteractionResult.sidedSuccess(level.isClientSide);
    }



    private void WriteSirasorisLogo(){
        ClearBlock();

        cellArray[CELL_START_Y + 2][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 2][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 2][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 2][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 2][CELL_START_X + 7] = 0;
        cellArray[CELL_START_Y + 3][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 3][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 3][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 3][CELL_START_X + 8] = 0;
        cellArray[CELL_START_Y + 4][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 4][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 4][CELL_START_X + 7] = 0;
        cellArray[CELL_START_Y + 5][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 5][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 5][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 5][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X + 7] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X + 8] = 0;

        cellArray[CELL_START_Y + 8][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 8][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 8][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 8][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 8][CELL_START_X + 8] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 5] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 7] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 9] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 8] = 0;
        cellArray[CELL_START_Y + 11][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 11][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 11][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 12][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 12][CELL_START_X + 5] = 0;

        cellArray[CELL_START_Y + 14][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 8] = 0;
        cellArray[CELL_START_Y + 15][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 15][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 15][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 15][CELL_START_X + 8] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 7] = 0;
        cellArray[CELL_START_Y + 17][CELL_START_X] = 0;
        cellArray[CELL_START_Y + 17][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 17][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 17][CELL_START_X + 8] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 6] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 7] = 0;
    }

    private void WriteGameOver(){
        ClearBlock();

        cellArray[CELL_START_Y + 2][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 2][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 2][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 3][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 3][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 4][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 4][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 5][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 5][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 6][CELL_START_X + 3] = 0;

        cellArray[CELL_START_Y + 8][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 8][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 9][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 10][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 11][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 11][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 11][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 12][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 12][CELL_START_X + 4] = 0;

        cellArray[CELL_START_Y + 14][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 14][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 15][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 16][CELL_START_X + 4] = 0;
        cellArray[CELL_START_Y + 17][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 1] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 2] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 3] = 0;
        cellArray[CELL_START_Y + 18][CELL_START_X + 4] = 0;

        WriteNumber(CELL_START_X + 6, CELL_START_Y + 14, (lineCompleteNum / 100) % 10);
        WriteNumber(CELL_START_X + 6, CELL_START_Y + 8, (lineCompleteNum / 10) % 10);
        WriteNumber(CELL_START_X + 6, CELL_START_Y + 2, lineCompleteNum % 10);
    }

    private void WriteNumber(int baseX, int baseY, int number){
        switch (number){
            case 1:
                cellArray[baseY + 0][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 2:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = 1;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 3:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 4:
                cellArray[baseY + 0][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 5:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 6:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = 1;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 7:
                cellArray[baseY + 0][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 8:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = 1;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 9:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = 1;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            case 0:
                cellArray[baseY + 0][baseX + 0] = 1;
                cellArray[baseY + 0][baseX + 1] = 1;
                cellArray[baseY + 0][baseX + 2] = 1;
                cellArray[baseY + 1][baseX + 0] = 1;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = 1;
                cellArray[baseY + 2][baseX + 0] = 1;
                cellArray[baseY + 2][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 2] = 1;
                cellArray[baseY + 3][baseX + 0] = 1;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = 1;
                cellArray[baseY + 4][baseX + 0] = 1;
                cellArray[baseY + 4][baseX + 1] = 1;
                cellArray[baseY + 4][baseX + 2] = 1;
                break;
            default:
                cellArray[baseY + 0][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 0][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 1][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 2][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 3][baseX + 2] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 0] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 1] = BLOCK_EMPTY;
                cellArray[baseY + 4][baseX + 2] = BLOCK_EMPTY;
                break;
        }
    }

    public void StartGame(){
        isGameEnabled = true;
        lineCompleteNum = 0;

        ClearBlock();
        RefreshCell();

        gameState = GameState.START;
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
        if(gameState == GameState.IDLE || gameState == GameState.START || gameState == GameState.CALL_MINO){
            return;
        }
        for(int yIndex = 0; yIndex < 4; yIndex++){
            for(int xIndex = 0; xIndex < 4; xIndex++) {
                if(minoArray[fallingMino.ordinal()][fallingMinoRotation][yIndex][xIndex] == 1){
                    renderCellArray[fallingMinoY + yIndex][fallingMinoX + xIndex] = fallingMinoBlockId;
                }
            }
        }
    }

    public void SetLevel(Level level){
        sirasorisExistLevel = level;
    }

    public void Tick(int currentGameFrame){
        if(currentGameFrame % 10 != 0){
            return;
        }
        gameFrame = currentGameFrame;

        if(!isGameEnabled){
            return;
        }
        UpdateGame();
        RefreshCell();
    }

    private void UpdateGame(){
        switch(gameState){
            case IDLE:

                break;
            case START:
                gameState = GameState.CALL_MINO;
                break;
            case CALL_MINO:
                fallingMinoX = 7;
                fallingMinoY = 22;
                fallingMinoRotation = 0;
                fallingMinoBlockId = nextMinoBlockId % 4;
                nextMinoBlockId++;
                Mino[] values = Mino.values();
                fallingMino = values[gameFrame % Mino.Num.ordinal()];
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
                if(CheckGameOver()){
                    gameState = gameState.END;
                }else{
                    CheckLine();
                    gameState = GameState.CALL_MINO;
                }
                break;
            case END:
                WriteGameOver();
                gameState = gameState.IDLE;
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
                if(minoArray[mino.ordinal()][fallingMinoRotation][yIndex][xIndex] == 1){
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
                if(minoArray[fallingMino.ordinal()][fallingMinoRotation][yIndex][xIndex] == 1){
                    cellArray[fallingMinoY + yIndex][fallingMinoX + xIndex] = fallingMinoBlockId;
                }
            }
        }
    }

    private void CheckLine(){
        int yIndex = 0;
        int lineCheckMax = CELL_NUM_Y * 3;

        for (int lineCheckCounter = 0; lineCheckCounter < lineCheckMax; lineCheckCounter++) {
            boolean isLineComplete = true;
            for (int xIndex = 0; xIndex < CELL_NUM_X; xIndex++) {
                if (cellArray[CELL_START_Y + yIndex][CELL_START_X + xIndex] == BLOCK_EMPTY) {
                    isLineComplete = false;
                    break;
                }
            }

            if(isLineComplete){
                for(int yIndexMover = yIndex; yIndexMover < CELL_NUM_Y; yIndexMover++){
                    for (int xIndex = 0; xIndex < CELL_NUM_X; xIndex++) {
                        cellArray[CELL_START_Y + yIndexMover][CELL_START_X + xIndex] = cellArray[CELL_START_Y + yIndexMover + 1][CELL_START_X + xIndex];
                    }
                }
                lineCompleteNum++;
                if(lineCompleteNum > 999){
                    lineCompleteNum = 999;
                }
            }else{
                yIndex++;
                if(yIndex >= CELL_NUM_Y){
                    break;
                }
            }
        }
    }

    private boolean CheckGameOver(){
        boolean isGameOver = false;
        for(int yIndex = CELL_START_Y + CELL_NUM_Y; yIndex < ARRAY_NUM_Y; yIndex++){
            for (int xIndex = 0; xIndex < CELL_NUM_X; xIndex++) {
                if(cellArray[yIndex][CELL_START_X + xIndex] != BLOCK_EMPTY){
                    isGameOver = true;
                }
            }
        }
        return isGameOver;
    }

    public void MoveLeft(){
        if(gameState == GameState.IDLE){
            StartGame();
            return;
        }

        int nextFallingMinoX = fallingMinoX - 1;
        boolean isValid = IsValidMinoPos(nextFallingMinoX, fallingMinoY, fallingMino);
        if(isValid){
            fallingMinoX = nextFallingMinoX;
        }
    }
    public void MoveRight(){
        if(gameState == GameState.IDLE){
            StartGame();
            return;
        }

        int nextFallingMinoX = fallingMinoX + 1;
        boolean isValid = IsValidMinoPos(nextFallingMinoX, fallingMinoY, fallingMino);
        if(isValid){
            fallingMinoX = nextFallingMinoX;
        }
    }
    public void Rotation(){
        if(gameState == GameState.IDLE){
            StartGame();
            return;
        }

        fallingMinoRotation = (fallingMinoRotation + 1) % ROTATION_NUM;

        //FallBlock();
    }

    public void FallBlock(){
        for(int fallY = fallingMinoY; fallY > 0; fallY --){
            int nextFallingMinoY = fallingMinoY - 1;
            boolean isValid = IsValidMinoPos(fallingMinoX, nextFallingMinoY, fallingMino);
            if(isValid){
                fallingMinoY = nextFallingMinoY;
            }else{
                break;
            }
        }
    }

    int[][][][] minoArray;
    private void InitMino(){
        minoArray = new int[Mino.Num.ordinal()][][][];
        minoArray[Mino.Sikaku.ordinal()] = new int [][][]{
                {
                        {1, 1, 0, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {1, 1, 0, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {1, 1, 0, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {1, 1, 0, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                }
        };

        minoArray[Mino.Lji.ordinal()] = new int[][][]{
                {
                        {0, 0, 1, 0},
                        {1, 1, 1, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 1, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 0, 0},
                        {1, 1, 1, 0},
                        {1, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {1, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                }

        };
        minoArray[Mino.Jji.ordinal()] = new int[][][]{
                {
                        {1, 0, 0, 0},
                        {1, 1, 1, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 1, 0},
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 0, 0},
                        {1, 1, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0}
                }
        };
        minoArray[Mino.Tji.ordinal()] = new int[][][]{
                {
                        {0, 1, 0, 0},
                        {1, 1, 1, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {0, 1, 1, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 0, 0},
                        {1, 1, 1, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {1, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                }
        };
        minoArray[Mino.Bou.ordinal()] = new int[][][]{
                {
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 1, 0}
                },
                {
                        {0, 0, 0, 0},
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 1, 0, 0}
                }
        };
        minoArray[Mino.Sji.ordinal()] = new int[][][]{
                {
                        {0, 1, 1, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {0, 1, 1, 0},
                        {0, 0, 1, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 0, 0},
                        {0, 1, 1, 0},
                        {1, 1, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {1, 0, 0, 0},
                        {1, 1, 0, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                }
        };
        minoArray[Mino.Sji_Gyaku.ordinal()] = new int[][][]{
                {
                        {1, 1, 0, 0},
                        {0, 1, 1, 0},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 1, 0},
                        {0, 1, 1, 0},
                        {0, 1, 0, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 0, 0, 0},
                        {1, 1, 0, 0},
                        {0, 1, 1, 0},
                        {0, 0, 0, 0}
                },
                {
                        {0, 1, 0, 0},
                        {1, 1, 0, 0},
                        {1, 0, 0, 0},
                        {0, 0, 0, 0}
                }
        };
        fallingMinoX = 5;
        fallingMinoY = 10;
        fallingMino = Mino.Sikaku;
        fallingMinoBlockId = 0;
        nextMinoBlockId = 0;
    }
}
