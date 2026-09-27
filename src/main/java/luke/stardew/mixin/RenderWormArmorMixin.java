package luke.stardew.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import luke.stardew.items.StardewItems;
import net.minecraft.client.render.entity.MobRendererBiped;
import net.minecraft.client.render.entity.MobRendererBipedArmored;
import net.minecraft.core.entity.IArmorWearing;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.useless.dragonfly.models.entity.StaticEntityModel;

@Mixin(MobRendererBipedArmored.class)
public abstract class RenderWormArmorMixin<T extends Mob & IArmorWearing<HumanArmorShape>> extends MobRendererBiped<T> {
    private RenderWormArmorMixin(float shadowSize) {
        super(shadowSize);
    }


    @WrapMethod(method = "getAndSetupModelForLayer")
    public @Nullable StaticEntityModel renderWorms(@NotNull T entity, float brightness, float partialTick, int layer, Operation<StaticEntityModel> original) {
        if (layer == 3 && this.bindTextures(entity)) {
            return this.setupAnimations(entity, this.getModel("armor.leggings"), partialTick, layer);
        }
        return original.call(entity, brightness, partialTick, layer);
    }

    @Unique
    private boolean bindTextures(@NotNull T entity) {
        ItemStack itemstack = entity.getItemInArmorSlot(HumanArmorShape.LEGS);
        if (itemstack == null) {
            return false;
        }
        Item item = itemstack.getItem();
        if (item.equals(StardewItems.ARMOR_CAN_OF_WORMS)) {
            this.bindTexture("/assets/stardew/textures/armor/bait.png");
            return true;
        }
        if (item.equals(StardewItems.ARMOR_CAN_OF_WORMS_GOLDEN)) {
            this.bindTexture("/assets/stardew/textures/armor/bait_golden.png");
            return true;
        }
        return false;
    }
}
