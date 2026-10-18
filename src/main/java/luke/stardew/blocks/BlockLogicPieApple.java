package luke.stardew.blocks;

import luke.stardew.items.StardewItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicEdible;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.BoundingVolume;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.Arrays;

public class BlockLogicPieApple extends BlockLogicEdible {
    private static final float HEIGHT = 0.375F;
    private static final float[] COL = {1F / 16F, 6F / 16F, 10F / 16F, 15F / 16F};
    private static final float[] ROW = {1F / 16F, 8F / 16F, 15F / 16F};
    private static final BoundingVolume[] VOLUMES = new BoundingVolume[6];
    private static final AABBdc[] SLICES = new AABBdc[6];

    public BlockLogicPieApple(Block<?> block) {
        super(block, 6, () -> StardewItems.FOOD_APPLE_PIE);
        BlockLogicPieApple.bakeSelectionBox();
    }

    public static void bakeSelectionBox() {
        for (int i = 0; i < 6; i++) {
            int c = i / 2;
            int r = i % 2;
            SLICES[i] = new AABBd(COL[c], 0.0, ROW[r], COL[c + 1], HEIGHT, ROW[r + 1]);
        }

        for (int meta = 0; meta < 6; meta++) {
            VOLUMES[meta] = BoundingVolume.bakeFromBlockSpaceAabbs(Arrays.copyOfRange(SLICES, meta, 6));
        }
    }

    private void addSlice(World world, TilePosc tilePos, Player entityplayer) {
        if (entityplayer.isSneaking()) {
            entityplayer.inventory.insertItem(new ItemStack(StardewItems.FOOD_APPLE_PIE_SLICE), true);
        } else {
            if (entityplayer.getHealth() < entityplayer.getMaxHealth()) {
                entityplayer.heal(this.getHealAmount(world, tilePos));
            } else {
                return;
            }
        }
        int data = world.getBlockData(tilePos) + 1;
        if (data >= this.maxBites) {
            world.setBlockTypeNotify(tilePos, Blocks.AIR);
        } else {
            world.setBlockDataNotify(tilePos, data);
            world.markBlockDirty(tilePos);
        }
    }

    @Override
    public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side, double xHit, double yHit) {
        this.addSlice(world, tilePos, player);
        return true;
    }

    @Override
    public @NotNull AABBdc getBoundsFromState(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
        AABBd bounds = new AABBd();
        for(AABBdc aabbdc: BlockLogicPieApple.getShape(source.getBlockData(tilePos))){
            bounds.union(aabbdc);
        }
        return bounds;
    }

    public static @NotNull AABBdc[] getShape(int meta){
        return Arrays.copyOfRange(SLICES, Math.max(0, Math.min(5, meta)), 6);
    }


    @Override
    public int getHealAmount(@NotNull World world, @NotNull TilePosc tilePosc) {
        return 3;
    }

    @Override
    public @Nullable BoundingVolume getBoundingVolume(@NotNull World world, @NotNull TilePosc tilePos) {
        int meta = world.getBlockData(tilePos);
        return VOLUMES[Math.max(0, Math.min(5, meta))];
    }
}
