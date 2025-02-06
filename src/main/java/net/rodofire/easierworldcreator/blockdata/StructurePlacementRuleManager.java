package net.rodofire.easierworldcreator.blockdata;

import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class to manage if blocks can be placed or not
 */
@SuppressWarnings("unused")
public class StructurePlacementRuleManager {
    /**
     * Define if the blocks can be forced. Ex: determine if it can replace a stone block. If true, the stone block will be replaced, else not.
     */
    private boolean force = false;

    /**
     * Define which blocks can deviate from the rule defined by {@code force}
     * <p>
     * meaning that if a block is not supposed to be replaced,
     * but it is present in the Set, it will be replaced anyway.
     * <p>
     * Work the other way around: if a block is supposed to be replaced because {@code force = true},
     * but it is present in the Set, it will not be replaced.
     */
    private Set<Block> overriddenBlocks = new HashSet<>();
    /**
     * Define which blocks can deviate from the rule defined by {@code force}
     * <p>
     * meaning that if a block is not supposed to be replaced,
     * but it is present in the BlockTag, it will be replaced anyway.
     * <p>
     * Work the other way around: if a block is supposed to be replaced because {@code force = true},
     * but it is present in the BlockTag, it will not be replaced.
     */
    private Set<TagKey<Block>> overriddenTags = new HashSet<>();

    public StructurePlacementRuleManager() {
    }

    public StructurePlacementRuleManager(Set<Block> overridenBlocks) {
        this.overriddenBlocks = overridenBlocks;
    }

    public StructurePlacementRuleManager(Set<Block> overriddenBlocks, Set<TagKey<Block>> overriddenTags) {
        this.overriddenBlocks = overriddenBlocks;
        this.overriddenTags = overriddenTags;
    }

    public StructurePlacementRuleManager(boolean force) {
        this.force = force;
    }

    public StructurePlacementRuleManager(boolean force, Set<Block> overriddenBlocks) {
        this.force = force;
        this.overriddenBlocks = overriddenBlocks;
    }

    public StructurePlacementRuleManager(boolean force, Set<Block> overriddenBlocks, Set<TagKey<Block>> overriddenTags) {
        this.force = force;
        this.overriddenBlocks = overriddenBlocks;
        this.overriddenTags = overriddenTags;
    }

    public Set<Block> getOverriddenBlocks() {
        return overriddenBlocks;
    }

    public void setOverriddenBlocks(Set<Block> overriddenBlocks) {
        this.overriddenBlocks = overriddenBlocks;
    }

    public Set<TagKey<Block>> getOverriddenTags() {
        return overriddenTags;
    }

    public void setOverriddenTags(Set<TagKey<Block>> overriddenTags) {
        this.overriddenTags = overriddenTags;
    }

    public void addOverrideBlock(Block block) {
        overriddenBlocks.add(block);
    }

    public void addTagKey(TagKey<Block> tagKey) {
        overriddenTags.add(tagKey);
    }

    public void addOverrideBlocks(Set<Block> overriddenBlocks) {
        this.overriddenBlocks.addAll(overriddenBlocks);
    }

    public void addTagKeys(Set<TagKey<Block>> tagKeys) {
        this.overriddenTags.addAll(tagKeys);
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }


    @Override
    public String toString() {
        return "StructurePlacementRuleManager{" +
                "force=" + force +
                ", overriddenBlocks=" + overriddenBlocks.stream()
                .map(Block::toString)
                .collect(Collectors.joining(", ", "[", "]")) +
                ", tagKeys=" + overriddenTags.stream()
                .map(TagKey::toString)
                .collect(Collectors.joining(", ", "[", "]")) +
                '}';
    }
}
