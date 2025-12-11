package com.telepathicgrunt.structure_tutorial.utilities;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A special HolderSet that holds one HolderSet as the base entries and uses a second HolderSet as a filter on those entries.
 * You can copy and paste this to your project. You do not need to make any changes here.
 * Simply use this in your structure's codec to replace the biomes HolderSet field. See OceanStructures.java for an example of usage.
 */
public class FilterHolderSet<T> implements RegistryEntryList<T> {
    public static <T> MapCodec<FilterHolderSet<T>> codec(RegistryKey<? extends Registry<T>> registryKey, Codec<RegistryEntry<T>> holderCodec, boolean forceList) {
        return RecordCodecBuilder.mapCodec(
                builder -> builder
                        .group(
                                RegistryEntryListCodec.create(registryKey, holderCodec, forceList).fieldOf("base").forGetter(FilterHolderSet::base),
                                RegistryEntryListCodec.create(registryKey, holderCodec, forceList).fieldOf("filter").forGetter(FilterHolderSet::filter))
                        .apply(builder, FilterHolderSet::new));
    }

    private final RegistryEntryList<T> base;
    private final RegistryEntryList<T> filter;

    private Set<RegistryEntry<T>> set = null;
    private List<RegistryEntry<T>> list = null;

    public RegistryEntryList<T> base() {
        return this.base;
    }
    public RegistryEntryList<T> filter() {
        return this.filter;
    }

    public FilterHolderSet(RegistryEntryList<T> base, RegistryEntryList<T> filter) {
        this.base = base;
        this.filter = filter;
    }

    /**
     * {@return immutable Set of filtered Holders}
     */
    protected Set<RegistryEntry<T>> createSet() {
        return this.base
                .stream()
                .filter(holder -> !this.filter.contains(holder))
                .collect(Collectors.toSet());
    }

    public Set<RegistryEntry<T>> getSet() {
        Set<RegistryEntry<T>> thisSet = this.set;
        if (thisSet == null) {
            Set<RegistryEntry<T>> set = this.createSet();
            this.set = set;
            return set;
        } else {
            return thisSet;
        }
    }

    public List<RegistryEntry<T>> getList() {
        List<RegistryEntry<T>> thisList = this.list;
        if (thisList == null) {
            List<RegistryEntry<T>> list = List.copyOf(this.getSet());
            this.list = list;
            return list;
        } else {
            return thisList;
        }
    }

    @Override
    public Stream<RegistryEntry<T>> stream() {
        return this.getList().stream();
    }

    @Override
    public int size() {
        return this.getList().size();
    }

    @Override
    public boolean isBound() {
        return this.set != null;
    }

    @Override
    public Either<TagKey<T>, List<RegistryEntry<T>>> getStorage() {
        return Either.right(this.getList());
    }

    @Override
    public Optional<RegistryEntry<T>> getRandom(Random rand) {
        List<RegistryEntry<T>> list = this.getList();
        int size = list.size();
        return size > 0
                ? Optional.of(list.get(rand.nextInt(size)))
                : Optional.empty();
    }

    @Override
    public RegistryEntry<T> get(int i) {
        return this.getList().get(i);
    }

    @Override
    public boolean contains(RegistryEntry<T> holder) {
        return this.getSet().contains(holder);
    }

    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> holderOwner) {
        return this.base.ownerEquals(holderOwner) && this.filter.ownerEquals(holderOwner);
    }

    @Override
    public Optional<TagKey<T>> getTagKey() {
        return Optional.empty();
    }

    @Override
    public Iterator<RegistryEntry<T>> iterator() {
        return this.getList().iterator();
    }
}
