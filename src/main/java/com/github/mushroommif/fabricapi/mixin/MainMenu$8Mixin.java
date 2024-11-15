package com.github.mushroommif.fabricapi.mixin;

import aoh.kingdoms.history.mainGame.CFG;
import aoh.kingdoms.history.mainGame.Game;
import aoh.kingdoms.history.menu.Colors;
import aoh.kingdoms.history.menu_element.menuElementHover.MenuElement_Hover;
import aoh.kingdoms.history.menu_element.menuElementHover.MenuElement_HoverElement;
import aoh.kingdoms.history.menu_element.menuElementHover.MenuElement_HoverElement_Type;
import aoh.kingdoms.history.menu_element.menuElementHover.MenuElement_HoverElement_Type_Button_TextBonus;
import com.github.mushroommif.fabricapi.internal.FabricApiImages;
import com.github.mushroommif.fabricapi.internal.InternalUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(targets = "aoh.kingdoms.history.menus.MainMenu$8")
public class MainMenu$8Mixin {

    @WrapOperation(
            method = "buildElementHover",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;size()I",
                    ordinal = 0
            )
    )
    private int alwaysBuildHover(List<?> instance, Operation<Integer> original) {
        return 1;
    }

    @WrapOperation(
            method = "buildElementHover",
            at = @At(
                    value = "NEW",
                    target = "aoh/kingdoms/history/menu_element/menuElementHover/MenuElement_Hover"
            )
    )
    private MenuElement_Hover addFabricModsElement(
            List<MenuElement_HoverElement> nElements,
            Operation<MenuElement_Hover> original,
            @Local(ordinal = 1) List<MenuElement_HoverElement_Type> nData
    ) {
        nData.add(
                new MenuElement_HoverElement_Type_Button_TextBonus(
                        "Fabric, " + Game.lang.get("InstalledMods") + ": ",
                        Long.toString(InternalUtils.getModCount()),
                        FabricApiImages.fabricLogo,
                        CFG.FONT_REGULAR_SMALL,
                        CFG.FONT_BOLD_SMALL,
                        Colors.HOVER_LEFT,
                        Colors.HOVER_GOLD
                )
        );

        nElements.add(new MenuElement_HoverElement(nData));
        nData.clear();

        return original.call(nElements);
    }
}
