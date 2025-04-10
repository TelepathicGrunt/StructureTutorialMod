package com.telepathicgrunt.structuretutorial.utilities;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

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
public class FilterHolderSet<T> implements HolderSet<T> {
    public static <T> MapCodec<FilterHolderSet<T>> codec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
        return RecordCodecBuilder.mapCodec(
                builder -> builder
                        .group(
                                HolderSetCodec.create(registryKey, holderCodec, forceList).fieldOf("base").forGetter(FilterHolderSet::base),
                                HolderSetCodec.create(registryKey, holderCodec, forceList).fieldOf("filter").forGetter(FilterHolderSet::filter))
                        .apply(builder, FilterHolderSet::new));
    }

    private final HolderSet<T> base;
    private final HolderSet<T> filter;

    private Set<Holder<T>> set = null;
    private List<Holder<T>> list = null;

    public HolderSet<T> base() {
        return this.base;
    }
    public HolderSet<T> filter() {
        return this.filter;
    }

    public FilterHolderSet(HolderSet<T> base, HolderSet<T> filter) {
        this.base = base;
        this.filter = filter;
    }

    /**
     * {@return immutable Set of filtered Holders}
     */
    protected Set<Holder<T>> createSet() {
        return this.base
                .stream()
                .filter(holder -> !this.filter.contains(holder))
                .collect(Collectors.toSet());
    }

    public Set<Holder<T>> getSet() {
        Set<Holder<T>> thisSet = this.set;
        if (thisSet == null) {
            Set<Holder<T>> set = this.createSet();
            this.set = set;
            return set;
        } else {
            return thisSet;
        }
    }

    public List<Holder<T>> getList() {
        List<Holder<T>> thisList = this.list;
        if (thisList == null) {
            List<Holder<T>> list = List.copyOf(this.getSet());
            this.list = list;
            return list;
        } else {
            return thisList;
        }
    }

    @Override
    public Stream<Holder<T>> stream() {
        return this.getList().stream();
    }

    @Override
    public int size() {
        return this.getList().size();
    }

    @Override
    public Either<TagKey<T>, List<Holder<T>>> unwrap() {
        return Either.right(this.getList());
    }

    @Override
    public Optional<Holder<T>> getRandomElement(RandomSource rand) {
        List<Holder<T>> list = this.getList();
        int size = list.size();
        return size > 0
                ? Optional.of(list.get(rand.nextInt(size)))
                : Optional.empty();
    }

    @Override
    public Holder<T> get(int i) {
        return this.getList().get(i);
    }

    @Override
    public boolean contains(Holder<T> holder) {
        return this.getSet().contains(holder);
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        return this.base.canSerializeIn(holderOwner) && this.filter.canSerializeIn(holderOwner);
    }

    @Override
    public Optional<TagKey<T>> unwrapKey() {
        return Optional.empty();
    }

    @Override
    public Iterator<Holder<T>> iterator() {
        return this.getList().iterator();
    }
}
