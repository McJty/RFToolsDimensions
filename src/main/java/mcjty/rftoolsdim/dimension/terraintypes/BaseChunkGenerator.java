package mcjty.rftoolsdim.dimension.terraintypes;

import mcjty.rftoolsdim.dimension.DimensionSettings;
import mcjty.rftoolsdim.dimension.biomes.RFTBiomeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public abstract class BaseChunkGenerator extends ChunkGenerator {

    protected final DimensionSettings settings;
    private final INoiseGenerator surfaceDepthNoise;
    protected final SharedSeedRandom randomSeed;

    protected List<BlockState> defaultBlocks = new ArrayList<>();
    protected BlockState defaultFluid = Blocks.WATER.getDefaultState();

    public BaseChunkGenerator(Registry<Biome> registry, DimensionSettings settings) {
        super(new RFTBiomeProvider(registry, settings), new DimensionStructuresSettings(false));
        this.settings = settings;
        this.randomSeed = new SharedSeedRandom(0);  // @todo 1.16 SEED
//        this.surfaceDepthNoise = (INoiseGenerator)(noisesettings.func_236178_i_() ? new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
        this.surfaceDepthNoise = new PerlinNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0));  //) : new OctavesNoiseGenerator(this.randomSeed, IntStream.rangeClosed(-3, 0)));
        for (ResourceLocation id : settings.getCompiledDescriptor().getBaseBlocks()) {
            Block block = ForgeRegistries.BLOCKS.getValue(id);
            if (block == null) {
                throw new RuntimeException("Could not find block with id '" + id + "'!");
            }
            defaultBlocks.add(block.getDefaultState());
        }
        if (defaultBlocks.isEmpty()) {
            defaultBlocks.add(Blocks.STONE.getDefaultState());
        }
    }

    // Get a (possibly random) default block
    public BlockState getDefaultBlock() {
        if (defaultBlocks.size() == 1) {
            return defaultBlocks.get(0);
        } else {
            int idx = randomSeed.nextInt(defaultBlocks.size());
            return defaultBlocks.get(idx);
        }
    }

    public DimensionSettings getSettings() {
        return settings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((RFTBiomeProvider)biomeProvider).getBiomeRegistry();
    }

    @Override
    public void generateSurface(WorldGenRegion region, IChunk chunk) {
        ChunkPos chunkpos = chunk.getPos();
        int cx = chunkpos.x;
        int cz = chunkpos.z;
        SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
        sharedseedrandom.setBaseChunkSeed(cx, cz);
        ChunkPos chunkpos1 = chunk.getPos();
        int xStart = chunkpos1.getXStart();
        int zStart = chunkpos1.getZStart();
        double d0 = 0.0625D;
        BlockPos.Mutable mpos = new BlockPos.Mutable();

        for(int x = 0; x < 16; ++x) {
            for(int z = 0; z < 16; ++z) {
                int xx = xStart + x;
                int zz = zStart + z;
                int yy = chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z) + 1;
                double d1 = this.surfaceDepthNoise.noiseAt((double)xx * 0.0625D, (double)zz * 0.0625D, 0.0625D, (double)x * 0.0625D) * 15.0D;
                region.getBiome(mpos.setPos(xStart + x, yy, zStart + z))
                        .buildSurface(sharedseedrandom, chunk, xx, zz, yy, d1, defaultBlocks.get(0), this.defaultFluid, this.getSeaLevel(), region.getSeed());
            }
        }
        this.makeBedrock(chunk);
    }

    protected void makeBedrock(IChunk chunk) {

    }
}
