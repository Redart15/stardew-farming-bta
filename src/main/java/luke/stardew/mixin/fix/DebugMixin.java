package luke.stardew.mixin.fix;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.support.ISupportable;
import net.minecraft.core.item.IBonemealable;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.debug.ChunkDecoratorDebug;
import net.minecraft.core.world.pos.ChunkTilePos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ChunkDecoratorDebug.class)
public class DebugMixin {

    @WrapMethod(method = "decorate")
    public void decorate(Chunk chunk, Operation<Void> original) {
        if(!FabricLoader.getInstance().isDevelopmentEnvironment()){
            original.call(chunk);
            return;
        }
        int chunkX = chunk.pos.x;
        int chunkZ = chunk.pos.z;

        int chunkBlockOffset = chunkX * 63 * 8;
        int chunkMetaOffset = chunkZ * 8;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 255; y > 0; y--) {
                    if (x % 2 != 0 || z % 2 != 0 || y % 4 != 0) {
                        continue;
                    }
                    int yStep = y / 4;
                    int blockBlockOffset = (x / 2) * 63;
                    int blockId = chunkBlockOffset + blockBlockOffset + yStep;
                    int meta = chunkMetaOffset + z / 2;
                    ChunkTilePos chunkTilePos = new ChunkTilePos(x, y, z);
                    if (blockId < 0 || blockId >= Blocks.blocksList.length || Blocks.blocksList[blockId] == null || blockId == Blocks.WIRE_REDSTONE.id()) {
                        continue;
                    }
                    Block<?> block = Blocks.blocksList[blockId];
                    assert block != null;
                    BlockLogic blockLogic = block.getLogic();
                    chunk.setBlockIdData(chunkTilePos, blockId, meta);
                    if(blockLogic instanceof ISupportable){ // catch blocks requiring support!
                        chunk.setBlockIdData(new ChunkTilePos(x, y - 1, z), Blocks.STONE.id(), 0);
                    }
                    if(block.getMaterial().isLiquid()){
                        // contain liquid
                        chunk.setBlockIdData(new ChunkTilePos(x, y - 1, z), Blocks.GLASS.id(), 0); // under
                        chunk.setBlockIdData(new ChunkTilePos(x, y, z + 1), Blocks.GLASS.id(), 0); // left
                        chunk.setBlockIdData(new ChunkTilePos(x, y, z - 1), Blocks.GLASS.id(), 0); // right
                        chunk.setBlockIdData(new ChunkTilePos(x + 1, y, z), Blocks.GLASS.id(), 0); // forward
                        chunk.setBlockIdData(new ChunkTilePos(x - 1, y, z), Blocks.GLASS.id(), 0); // backward
                    }
                    if(blockLogic instanceof IBonemealable){ // catch most if not all crops
                        chunk.setBlockIdData(new ChunkTilePos(x, y - 1, z), Blocks.FARMLAND_DIRT.id(), 3);
                    }
                }
            }
        }


    }
}
