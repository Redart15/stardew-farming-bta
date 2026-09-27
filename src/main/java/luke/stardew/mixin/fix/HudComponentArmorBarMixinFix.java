package luke.stardew.mixin.fix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.hud.component.HudComponentArmorBar;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HudComponentArmorBar.ArmorState.class)
public class HudComponentArmorBarMixinFix {

    @WrapOperation(method = "calculate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/item/ItemStack;getItemDamageForDisplay()I"))
    private static int addOne(ItemStack instance, Operation<Integer> original){
        return original.call(instance) - 1;
    }
}
