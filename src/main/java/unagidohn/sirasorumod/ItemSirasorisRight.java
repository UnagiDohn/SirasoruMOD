package unagidohn.sirasorumod;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ItemSirasorisRight  extends Item {
    public ItemSirasorisRight(Properties properties)
    {
        super(properties);
    }

    /*@Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide()) {
            return InteractionResult.PASS;
        }
        SirasoruMOD.sirasorisManager.MoveRight();

        return InteractionResult.sidedSuccess(level.isClientSide);
    }*/

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if(level.isClientSide()) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }
        SirasoruMOD.sirasorisManager.MoveRight();

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), level.isClientSide());
    }
}
