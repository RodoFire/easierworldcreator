package net.rodofire.easierworldcreator.tag;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Util class related to tags
 */
public class TagUtil {
    public static class BlockTags {
        public static Set<Block> convertBlockTagToBlockSet(TagKey<Block> blockTag) {
            Set<Block> blocks = new HashSet<>();
            Registries.BLOCK.iterateEntries(blockTag).forEach(block -> {
                blocks.add(block.value());
            });
            return blocks;
        }

        public static Set<Block> convertBlockTagToBlockSet(List<TagKey<Block>> blockTags) {
            Set<Block> blocks = new HashSet<>();
            for (TagKey<Block> blockTag : blockTags) {
                blocks.addAll(convertBlockTagToBlockSet(blockTag));
            }
            return blocks;
        }

        /**
         * convert a blockTag to an array
         * @param blockTags the converted blockTag
         * @return the converted array
         */
        public static Block[] convertBlockTagToBlockArray(TagKey<Block> blockTags) {
            return convertBlockTagToBlockSet(blockTags).toArray(new Block[0]);
        }

        /**
         * convert a list of blockTag to a matrices of array
         */
        public static Block[][] convertBlockTagToBlockArray(List<TagKey<Block>> blockTags) {
            Block[][] result = new Block[blockTags.size()][];

            for (int i = 0; i < blockTags.size(); i++) {
                TagKey<Block> blockTag = blockTags.get(i);
                List<Block> blocks = new ArrayList<>();
                Registries.BLOCK.iterateEntries(blockTag).forEach(block -> blocks.add(block.value()));
                result[i] = blocks.toArray(new Block[0]);
            }

            return result;
        }
    }

    public static class ItemTags {
        public static Set<Item> convertItemTagToItemSet(TagKey<Item> blockTag) {
            Set<Item> blocks = new HashSet<>();
            Registries.ITEM.iterateEntries(blockTag).forEach(block -> {
                blocks.add(block.value());
            });
            return blocks;
        }

        public static Set<Item> convertItemTagToItemSet(List<TagKey<Item>> blockTags) {
            Set<Item> blocks = new HashSet<>();
            for (TagKey<Item> blockTag : blockTags) {
                blocks.addAll(convertItemTagToItemSet(blockTag));
            }
            return blocks;
        }

        /**
         * convert an ItemTag to an array
         * @param blockTags the converted ItemTag
         * @return the converted array
         */
        public static Item[] convertItemTagToItemArray(TagKey<Item> blockTags) {
            return convertItemTagToItemSet(blockTags).toArray(new Item[0]);
        }

        /**
         * convert a list of ItemTag to a matrices of array
         */
        public static Item[][] convertItemTagToItemArray(List<TagKey<Item>> blockTags) {
            Item[][] result = new Item[blockTags.size()][];

            for (int i = 0; i < blockTags.size(); i++) {
                TagKey<Item> blockTag = blockTags.get(i);
                List<Item> blocks = new ArrayList<>();
                Registries.ITEM.iterateEntries(blockTag).forEach(block -> blocks.add(block.value()));
                result[i] = blocks.toArray(new Item[0]);
            }

            return result;
        }
    }
}
