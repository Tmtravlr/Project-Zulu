package projectzulu.common.core;

import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import projectzulu.common.api.CustomMobData;
import projectzulu.common.blocks.StringHelper;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;

public class ConfigHelper {
	public static void configDropToMobData(Configuration config, String category, CustomMobData customMobData, Item item, int meta, int weightChance){
		configItemStackToMobData(config, category, customMobData, new ItemStack(item, 1, meta), weightChance);
	}
	
	public static void configDropToMobData(Configuration config, String category, CustomMobData customMobData, Block block, int meta, int weightChance){
		configItemStackToMobData(config, category, customMobData, new ItemStack(block, 1, meta), weightChance);
	}
	
	public static void configDropToMobData(Configuration config, String category, CustomMobData customMobData, Optional<? extends Item> item, int meta, int weightChance){
		if(item.isPresent()){
			configItemStackToMobData(config, category, customMobData, new ItemStack(item.get(), 1, meta), weightChance);
		}
	}
	
	private static void configItemStackToMobData(Configuration config, String category, CustomMobData customMobData, ItemStack itemStack, int weightChance){
		int stackSize = config.get(category,"Item "+itemStack.getItemName()+" Quantity:", 1).getInt();
		itemStack.stackSize = stackSize;
		int weight = config.get(category, "Item "+itemStack.getItemName()+" Weight:", weightChance).getInt();
		if(weight > 0){
			customMobData.addLootToMob(itemStack, weight);
		}
	}
	
	public static void userItemConfigRangeToMobData(Configuration config, String category, CustomMobData customMobData){
		Property property = config.get(category,"Item User Custom Drop", "0-0:0:3:4, 0:0:1:2");
		String[] itemStringEntries = property.value.split(",");
		for (String stringEntry : itemStringEntries){
			String[] entryParts = stringEntry.split(":");
			if(entryParts.length == 4){
				// <itemRange [Inclusive]>:<meta/damage>:<Weight>:<Quantity>
				int meta = StringHelper.parseInteger(entryParts[1], "0123456789");
				int weight = StringHelper.parseInteger(entryParts[2], "0123456789");
				int quantity = StringHelper.parseInteger(entryParts[3], "0123456789");
				
				int minItemID;
				int maxItemID;
				String[] itemRangeParts = entryParts[0].split("-");
				if(itemRangeParts.length == 1){
					minItemID = StringHelper.parseInteger(itemRangeParts[0], "0123456789");
					maxItemID = minItemID;
				}else{
					minItemID = StringHelper.parseInteger(itemRangeParts[0], "0123456789");
					maxItemID = StringHelper.parseInteger(itemRangeParts[1], "0123456789");
				}
				
				for (int curID = minItemID; curID <= maxItemID; curID++){
					if(Item.itemsList[curID] != null && weight > 0){
						ItemStack itemStack = new ItemStack(curID, quantity, meta);
						customMobData.addLootToMob(itemStack, weight);
					}
				}
			}
		}
	}
}