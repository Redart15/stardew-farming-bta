package luke.stardew.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.client.render.item.model.ItemModelDispatcher.*;

@Environment(EnvType.CLIENT)
public class ItemModelTieredFishingRod extends ItemModelStandard {
    protected final String material;
    protected IconCoordinate rodCast;

    public ItemModelTieredFishingRod(Item item, String material) {
        super(item);
        this.material = material;
        this.rodCast = TextureRegistry.getTexture("stardew:item/tool_fishingrod_" + material + "_cast");
        this.setDisplayPos("firstperson_righthand", HANDHELD_ROD_FIRST_PERSON_RIGHT_HAND);
        this.setDisplayPos("firstperson_lefthand", HANDHELD_ROD_FIRST_PERSON_LEFT_HAND);
        this.setDisplayPos("thirdperson_righthand", HANDHELD_ROD_THIRD_PERSON_RIGHT_HAND);
        this.setDisplayPos("thirdperson_lefthand", HANDHELD_ROD_THIRD_PERSON_LEFT_HAND);
    }

    @Override
    public @NotNull IconCoordinate getIcon(@Nullable Entity entity, @NotNull ItemStack itemStack) {
        if (entity instanceof Player player) {
            if (itemStack == player.getHeldItem() && player.bobberEntity != null) {
                return this.rodCast;
            }
        }
        return super.getIcon(entity, itemStack);
    }
}
