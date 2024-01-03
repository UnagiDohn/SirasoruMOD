package unagidohn.sirasorumod;
import unagidohn.sirasorumod.BlockEntitySirasorisControlManager;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SirasoruMOD.MODID)
public class SirasoruMOD
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sirasorumod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final RegistryObject<Block> SIRASORU_BLOCK_0 = BLOCKS.register("sirasoru_block_0", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> SIRASORU_BLOCK_0_ITEM = ITEMS.register("sirasoru_block_0", () -> new BlockItem(SIRASORU_BLOCK_0.get(), new Item.Properties()));

    public static final RegistryObject<Block> SIRASORU_BLOCK_1 = BLOCKS.register("sirasoru_block_1", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> SIRASORU_BLOCK_1_ITEM = ITEMS.register("sirasoru_block_1", () -> new BlockItem(SIRASORU_BLOCK_1.get(), new Item.Properties()));
    public static final RegistryObject<Block> SIRASORU_BLOCK_2 = BLOCKS.register("sirasoru_block_2", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> SIRASORU_BLOCK_2_ITEM = ITEMS.register("sirasoru_block_2", () -> new BlockItem(SIRASORU_BLOCK_2.get(), new Item.Properties()));
    public static final RegistryObject<Block> SIRASORU_BLOCK_3 = BLOCKS.register("sirasoru_block_3", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> SIRASORU_BLOCK_3_ITEM = ITEMS.register("sirasoru_block_3", () -> new BlockItem(SIRASORU_BLOCK_3.get(), new Item.Properties()));


    public static final RegistryObject<Item> SIRASORIS_ROD = ITEMS.register("sirasoris_rod", () -> new ItemSirasorisRod(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SIRASORIS_LEFT = ITEMS.register("sirasoris_left", () -> new ItemSirasorisLeft(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SIRASORIS_RIGHT = ITEMS.register("sirasoris_right", () -> new ItemSirasorisRight(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SIRASORIS_ROTATION = ITEMS.register("sirasoris_rotation", () -> new ItemSirasorisRotation(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SIRASORIS_FALLDOWN = ITEMS.register("sirasoris_falldown", () -> new ItemSirasorisFallDown(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Block> BLOCK_MUJI = BLOCKS.register("block_muji", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> BLOCK_MUJI_ITEM = ITEMS.register("block_muji", () -> new BlockItem(BLOCK_MUJI.get(), new Item.Properties()));

    public static final RegistryObject<BlockSirasorisControlManager> SIRASORIS_CONTROL_MANAGER = BLOCKS.register("sirasoris_control_manager", () -> new BlockSirasorisControlManager(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<Item> SIRASORIS_CONTROL_MANAGER_ITEM = ITEMS.register("sirasoris_control_manager", () -> new BlockItem(SIRASORIS_CONTROL_MANAGER.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<BlockEntitySirasorisControlManager>> SIRASORIS_CONTROL_MANAGER_ENTITY = BLOCK_ENTITIES.register("sirasoris_control_manager",
            () -> BlockEntityType.Builder.of(BlockEntitySirasorisControlManager::new, SIRASORIS_CONTROL_MANAGER.get()).build(null));
    public static SirasorisManager sirasorisManager;

    public SirasoruMOD()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        sirasorisManager = new SirasorisManager();
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        if (event.getTab() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(SIRASORU_BLOCK_0_ITEM);
            event.accept(SIRASORU_BLOCK_1_ITEM);
            event.accept(SIRASORU_BLOCK_2_ITEM);
            event.accept(SIRASORU_BLOCK_3_ITEM);
            event.accept(BLOCK_MUJI_ITEM);
            event.accept(SIRASORIS_CONTROL_MANAGER_ITEM);
        }else if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(SIRASORIS_ROD);
            event.accept(SIRASORIS_LEFT);
            event.accept(SIRASORIS_RIGHT);
            event.accept(SIRASORIS_ROTATION);
            event.accept(SIRASORIS_FALLDOWN);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
