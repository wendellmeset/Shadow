package me.x150.sipprivate.feature.command.impl;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.command.Command;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Locate extends Command {

    private final DynamicRegistryManager jigsawRegistry = DynamicRegistryManager.create();
    private List<ChunkPos> strongholds = Lists.newArrayList();
    private BiomeSource biomeSource;
    private StructuresConfig structuresConfig;
    private long worldSeed;
    private ChunkGenerator chunkGenerator;
    private StructureManager structureManager;
    private Registry<Biome> biomeRegistry;

    public Locate() {
        super("Locate", "locates structures", "locate");
    }

    private static boolean canPlaceStrongholdInBiome(Biome biome) {
        Biome.Category category = biome.getCategory();
        return category != Biome.Category.OCEAN && category != Biome.Category.RIVER && category != Biome.Category.BEACH && category != Biome.Category.SWAMP && category != Biome.Category.NETHER && category != Biome.Category.THEEND;
    }

    @Override
    public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            String[] structures = new String[StructureFeature.STRUCTURES.size()];
            StructureFeature.STRUCTURES.keySet().toArray(structures);
            return structures;
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length != 2) {
            message("syntax is .locate <structure> <seed>");
            return;
        }
        StructureFeature<?> structureFeature = StructureFeature.STRUCTURES.get(args[0]);
        if (structureFeature == null) {
            message("invalid structure");
            return;
        }
        try {
            this.worldSeed = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            this.worldSeed = args[1].hashCode();
        }

        DynamicRegistryManager registryManager;
        try {
            assert CoffeeClientMain.client.world != null;
            registryManager = CoffeeClientMain.client.world.getRegistryManager();
            this.biomeRegistry = registryManager.get(Registry.BIOME_KEY);
            this.biomeSource = switch (CoffeeClientMain.client.world.getDimension().getEffects().getPath()) {
                case "the_end" -> new TheEndBiomeSource(biomeRegistry, worldSeed);
                case "the_nether" -> MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(biomeRegistry);
                default -> MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(biomeRegistry);
            };

            Supplier<ChunkGeneratorSettings> supplier = ChunkGeneratorSettings::getInstance;

            this.chunkGenerator = new NoiseChunkGenerator(BuiltinRegistries.NOISE_PARAMETERS, biomeSource, worldSeed, supplier);
            this.structuresConfig = chunkGenerator.getStructuresConfig();
            System.out.println(registryManager.get(Registry.STRUCTURE_POOL_KEY).toString());


        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        try {
            LevelStorage.Session session = LevelStorage.create(FabricLoader.getInstance().getConfigDir()).createSession("awdwa");
            structureManager = new StructureManager(CoffeeClientMain.client.getResourceManager(), session, null);
            session.close();
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        BlockPos res = null;
        try {
            assert CoffeeClientMain.client.player != null;
            res = locateStructure(structureFeature, CoffeeClientMain.client.player.getBlockPos(), 100);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        if (res == null) {
            message("couldnt find structure");
            return;
        }
        message("found structure at: " + res.getX() + " " + res.getZ());
        this.strongholds = Lists.newArrayList();
        this.biomeSource = null;
        this.structuresConfig = null;
        this.chunkGenerator = null;
        this.structureManager = null;
        this.biomeRegistry = null;
    }

    @Nullable
    public BlockPos locateStructure(StructureFeature<?> structureFeature, BlockPos center, int radius) {
        if (structureFeature == StructureFeature.STRONGHOLD) {
            BlockPos blockPos = null;
            double d = Double.MAX_VALUE;
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            generateStrongholdPositions();
            for (ChunkPos chunkPos : this.strongholds) {
                mutable.set(ChunkSectionPos.getOffsetPos(chunkPos.x, 8), 32, ChunkSectionPos.getOffsetPos(chunkPos.z, 8));
                double e = mutable.getSquaredDistance(center);
                if (blockPos == null) {
                    blockPos = new BlockPos(mutable);
                    d = e;
                    continue;
                }
                if (!(e < d)) continue;
                blockPos = new BlockPos(mutable);
                d = e;
            }
            return blockPos;
        }
        StructureConfig structureConfig = structuresConfig.getForType(structureFeature);
        ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> d = chunkGenerator.getStructuresConfig().getConfiguredStructureFeature(structureFeature);
        if (structureConfig == null || d.isEmpty()) {
            return null;
        }
        Set<RegistryKey<Biome>> mutable = biomeSource.getBiomes().stream().flatMap(biome -> biomeRegistry.getKey(biome).stream()).collect(Collectors.toSet());
        if (d.values().stream().noneMatch(mutable::contains)) {
            return null;
        }
        return locateStructure2(center, radius, structureConfig, structureFeature);
    }

    @Nullable
    private BlockPos locateStructure2(BlockPos searchStartPos, int searchRadius, StructureConfig config, StructureFeature<?> structureFeature) {
        int i = config.getSpacing();
        int j = ChunkSectionPos.getSectionCoord(searchStartPos.getX());
        int k = ChunkSectionPos.getSectionCoord(searchStartPos.getZ());
        block0:
        for (int l = 0; l <= searchRadius; ++l) {
            for (int m = -l; m <= l; ++m) {
                boolean bl = m == -l || m == l;
                for (int n = -l; n <= l; ++n) {
                    ChunkPos chunkPos;
                    boolean bl2 = n == -l || n == l;
                    boolean structurePresence = getStructurePresence(chunkPos = structureFeature.getStartChunk(config, worldSeed, j + i * m, k + i * n), structureFeature);
                    if (!bl && !bl2 || !structurePresence) continue;
                    return structureFeature.getLocatedPos(chunkPos);
                }
                if (l == 0) continue block0;
            }
        }
        return null;
    }

    private <F extends StructureFeature<?>> boolean getStructurePresence(ChunkPos pos2, F feature2) {
        ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> multimap = structuresConfig.getConfiguredStructureFeature(feature2);
        for (Map.Entry<ConfiguredStructureFeature<?, ?>, Collection<RegistryKey<Biome>>> entry : multimap.asMap().entrySet()) {
            if (!this.isGenerationPossible(pos2, entry.getKey(), entry.getValue())) continue;
            return true;
        }
        return false;
    }

    private <FC extends FeatureConfig, F extends StructureFeature<FC>> boolean isGenerationPossible(ChunkPos pos, ConfiguredStructureFeature<FC, F> feature, Collection<RegistryKey<Biome>> allowedBiomes) {
        Predicate<Biome> predicate = biome -> this.biomeRegistry.getKey(biome).filter(allowedBiomes::contains).isPresent();
        return feature.feature.canGenerate(jigsawRegistry, chunkGenerator, biomeSource, structureManager, worldSeed, pos, feature.config, CoffeeClientMain.client.world, predicate);
    }

    private void generateStrongholdPositions() {
        if (!this.strongholds.isEmpty()) {
            return;
        }
        StrongholdConfig strongholdConfig = this.structuresConfig.getStronghold();
        if (strongholdConfig == null || strongholdConfig.getCount() == 0) {
            return;
        }
        ArrayList<Biome> list = Lists.newArrayList();
        for (Biome biome : this.biomeSource.getBiomes()) {
            if (!canPlaceStrongholdInBiome(biome)) continue;
            list.add(biome);
        }
        int i = strongholdConfig.getDistance();
        int biome = strongholdConfig.getCount();
        int j = strongholdConfig.getSpread();
        Random random = new Random();
        random.setSeed(this.worldSeed);
        double d = random.nextDouble() * Math.PI * 2.0;
        int k = 0;
        int l = 0;
        for (int m = 0; m < biome; ++m) {
            double e = (double) (4 * i + i * l * 6) + (random.nextDouble() - 0.5) * ((double) i * 2.5);
            int n = (int) Math.round(Math.cos(d) * e);
            int o = (int) Math.round(Math.sin(d) * e);
            BlockPos blockPos = biomeSource.locateBiome(ChunkSectionPos.getOffsetPos(n, 8), 0, ChunkSectionPos.getOffsetPos(o, 8), 112, list::contains, random, chunkGenerator.getMultiNoiseSampler());
            if (blockPos != null) {
                n = ChunkSectionPos.getSectionCoord(blockPos.getX());
                o = ChunkSectionPos.getSectionCoord(blockPos.getZ());
            }
            this.strongholds.add(new ChunkPos(n, o));
            d += Math.PI * 2 / (double) j;
            if (++k != j) continue;
            k = 0;
            j += 2 * j / (++l + 1);
            j = Math.min(j, biome - m);
            d += random.nextDouble() * Math.PI * 2.0;
        }
    }
}
