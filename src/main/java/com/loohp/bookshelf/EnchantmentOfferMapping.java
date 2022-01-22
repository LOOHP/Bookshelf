package com.loohp.bookshelf;

import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentOfferMapping {

    public static Map<Player, Map<ItemStack, EnchantmentOffer[]>> enchantOffers = new ConcurrentHashMap<>();

}
