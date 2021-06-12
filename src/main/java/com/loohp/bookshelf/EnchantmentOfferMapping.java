package com.loohp.bookshelf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantmentOfferMapping {
	
	public static ConcurrentHashMap<Player, Map<ItemStack, EnchantmentOffer[]>> enchantOffers = new ConcurrentHashMap<>();

}
