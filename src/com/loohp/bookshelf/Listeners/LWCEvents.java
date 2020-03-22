package com.loohp.bookshelf.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

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
		if (!event.getPlayer().hasPermission("bookshelf.use")) {
			return;
		}
		new BukkitRunnable() {
			public void run() {
				Player player = event.getPlayer();
				if (!Bookshelf.requestOpen.containsKey(player)) {
					return;
				}
				String loc = Bookshelf.requestOpen.get(player);
				Protection protection = event.getProtection();
				if (LWC.getInstance().getPlugin().getLWC().canAccessProtection(player, protection) == true || !event.getAccess().equals(Access.NONE)) {
					if (event.getProtection().getType().equals(Type.DONATION)) {
						if (!Bookshelf.isDonationView.contains(player)) {
							Bookshelf.isDonationView.add(player);
						}
					}
					Inventory inv = Bookshelf.bookshelfContent.get(loc);
					Bukkit.getScheduler().runTask(Bookshelf.plugin, () -> player.openInventory(inv));
					if (!Bookshelf.bookshelfSavePending.contains(loc)) {
						Bookshelf.bookshelfSavePending.add(loc);
					}
				}
				Bookshelf.requestOpen.remove(player);
			}
		}.runTaskLater(Bookshelf.plugin, 1);
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
		Bookshelf.cancelOpen.add(event.getPlayer());
		new BukkitRunnable() {
			public void run() {
				while (Bookshelf.cancelOpen.contains(event.getPlayer())) {
					Bookshelf.cancelOpen.remove(event.getPlayer());
				}
			}
		}.runTaskLater(Bookshelf.plugin, 5);
	}

	@Override
	public void onProtectionInteract(LWCProtectionInteractEvent event) {
		if (!event.getResult().equals(Result.CANCEL)) {
			return;
		}
		Bookshelf.cancelOpen.add(event.getPlayer());
		new BukkitRunnable() {
			public void run() {
				while (Bookshelf.cancelOpen.contains(event.getPlayer())) {
					Bookshelf.cancelOpen.remove(event.getPlayer());
				}
			}
		}.runTaskLater(Bookshelf.plugin, 5);
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
		Bookshelf.cancelOpen.add(event.getPlayer());
		new BukkitRunnable() {
			public void run() {
				while (Bookshelf.cancelOpen.contains(event.getPlayer())) {
					Bookshelf.cancelOpen.remove(event.getPlayer());
				}
			}
		}.runTaskLater(Bookshelf.plugin, 5);
	}

	@Override
	public void onSendLocale(LWCSendLocaleEvent event) {
		return;
	}
}
