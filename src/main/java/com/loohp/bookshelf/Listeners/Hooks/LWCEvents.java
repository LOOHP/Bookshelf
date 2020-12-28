package com.loohp.bookshelf.Listeners.Hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Permission.Access;
import com.griefcraft.model.Protection;
import com.griefcraft.model.Protection.Type;
import com.griefcraft.scripting.Module;
import com.griefcraft.scripting.event.LWCAccessEvent;
import com.griefcraft.scripting.event.LWCBlockInteractEvent;
import com.griefcraft.scripting.event.LWCCommandEvent;
import com.griefcraft.scripting.event.LWCDropItemEvent;
import com.griefcraft.scripting.event.LWCEntityInteractEvent;
import com.griefcraft.scripting.event.LWCMagnetPullEvent;
import com.griefcraft.scripting.event.LWCProtectionDestroyEvent;
import com.griefcraft.scripting.event.LWCProtectionInteractEntityEvent;
import com.griefcraft.scripting.event.LWCProtectionInteractEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEntityEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.griefcraft.scripting.event.LWCProtectionRegistrationPostEvent;
import com.griefcraft.scripting.event.LWCProtectionRemovePostEvent;
import com.griefcraft.scripting.event.LWCRedstoneEvent;
import com.griefcraft.scripting.event.LWCReloadEvent;
import com.griefcraft.scripting.event.LWCSendLocaleEvent;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.API.Events.PlayerOpenBookshelfEvent;
import com.loohp.bookshelf.ObjectHolders.LWCRequestOpenData;

public class LWCEvents implements Module {
	
	public static void hookLWC() {
		LWC.getInstance().getModuleLoader().registerModule(Bookshelf.plugin, new LWCEvents());
	}
	
	@Override
	public void onReload(LWCReloadEvent event) {
		return;
	}
	
	@Override
	public void load(LWC event) {
		return;
	}

	@Override
	public void onAccessRequest(LWCAccessEvent event) {
		Bukkit.getConsoleSender().sendMessage("000");
		if (!event.getPlayer().hasPermission("bookshelf.use")) {
			return;
		}
		Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> {
			Player player = event.getPlayer();
			if (!Bookshelf.requestOpen.containsKey(player)) {
				return;
			}
			LWCRequestOpenData data = Bookshelf.requestOpen.get(player);
			String loc = data.getKey();
			Protection protection = event.getProtection();
			if (LWC.getInstance().getPlugin().getLWC().canAccessProtection(player, protection) == true || !event.getAccess().equals(Access.NONE)) {
				if (event.getProtection().getType().equals(Type.DONATION)) {
					if (!Bookshelf.isDonationView.contains(player.getUniqueId())) {
						Bookshelf.isDonationView.add(player.getUniqueId());
					}
				}
				
				PlayerOpenBookshelfEvent pobe = new PlayerOpenBookshelfEvent(player, loc, data.getBlockFace(), data.isCancelled());
				Bukkit.getPluginManager().callEvent(pobe);
				
				if (!pobe.isCancelled()) {
					Inventory inv = Bookshelf.keyToContentMapping.get(loc);
					Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(inv));
					if (!Bookshelf.bookshelfSavePending.contains(loc)) {
						Bookshelf.bookshelfSavePending.add(loc);
					}
				}
			}
			Bookshelf.requestOpen.remove(player);
		}, 1);
	}

	@Override
	public void onBlockInteract(LWCBlockInteractEvent event) {
		return;
	}

	@Override
	public void onCommand(LWCCommandEvent event) {
		return;
	}

	@Override
	public void onDestroyProtection(LWCProtectionDestroyEvent event) {
		return;
	}

	@Override
	public void onDropItem(LWCDropItemEvent event) {
		return;
	}

	@Override
	public void onEntityInteract(LWCEntityInteractEvent event) {
		return;
	}

	@Override
	public void onEntityInteractProtection(LWCProtectionInteractEntityEvent event) {
		return;
	}

	@Override
	public void onMagnetPull(LWCMagnetPullEvent event) {
		return;
	}

	@Override
	public void onPostRegistration(LWCProtectionRegistrationPostEvent event) {
		return;
	}

	@Override
	public void onPostRemoval(LWCProtectionRemovePostEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Bookshelf.lwcCancelOpen.add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> Bookshelf.lwcCancelOpen.remove(player.getUniqueId()), 5);
	}

	@Override
	public void onProtectionInteract(LWCProtectionInteractEvent event) {
		Player player = event.getPlayer();
		if (!event.getResult().equals(Result.CANCEL)) {
			return;
		}
		if (player == null) {
			return;
		}
		Bookshelf.lwcCancelOpen.add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> Bookshelf.lwcCancelOpen.remove(player.getUniqueId()), 5);
	}

	@Override
	public void onRedstone(LWCRedstoneEvent event) {
		return;
	}

	@Override
	public void onRegisterEntity(LWCProtectionRegisterEntityEvent event) {
		return;
	}

	@Override
	public void onRegisterProtection(LWCProtectionRegisterEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return;
		}
		Bookshelf.lwcCancelOpen.add(player.getUniqueId());
		Bukkit.getScheduler().runTaskLater(Bookshelf.plugin, () -> Bookshelf.lwcCancelOpen.remove(player.getUniqueId()), 5);
	}

	@Override
	public void onSendLocale(LWCSendLocaleEvent event) {
		return;
	}
}
