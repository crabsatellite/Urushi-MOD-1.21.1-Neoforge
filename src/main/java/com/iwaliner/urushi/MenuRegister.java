package com.iwaliner.urushi;


import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.iwaliner.urushi.ModCoreUrushi;
import com.iwaliner.urushi.blockentity.menu.*;
import com.iwaliner.urushi.item.menu.DrawstringBagMenu;

public class MenuRegister {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ModCoreUrushi.ModID);



    public static final DeferredHolder<MenuType<?>, MenuType<FryerMenu>> FryerMenu = MENUS.register("fryer", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new FryerMenu(windowId, inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<DoubledWoodenCabinetryMenu>> DoubledWoodenCabinetryMenu = MENUS.register("doubled_wooden_cabinetry", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new DoubledWoodenCabinetryMenu(windowId, inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<com.iwaliner.urushi.blockentity.menu.UrushiHopperMenu>> UrushiHopperMenu = MENUS.register("urushi_hopper", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new UrushiHopperMenu(windowId, inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<AutoCraftingTableMenu>> AutoCraftingTableMenu = MENUS.register("auto_crafting_table", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new AutoCraftingTableMenu(windowId, inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<SilkwormFarmMenu>> SilkwormFarmMenu = MENUS.register("silkworm_farm", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new SilkwormFarmMenu(windowId, inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<KettleMenu>> KettleMenu = MENUS.register("kettle", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new KettleMenu(windowId, inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<TranslatableBookMenu>> TranslatableBookMenu = MENUS.register("translatable_book", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new TranslatableBookMenu(windowId);}));
    public static final DeferredHolder<MenuType<?>, MenuType<FillerMenu>> FillerMenu = MENUS.register("filler", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new FillerMenu(windowId,inv);}));
    public static final DeferredHolder<MenuType<?>, MenuType<DrawstringBagMenu>> DrawstringBagMenu = MENUS.register("drawstring_bag", () -> IMenuTypeExtension.create((windowId, inv, data) -> {return new DrawstringBagMenu(windowId, inv);}));




    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
