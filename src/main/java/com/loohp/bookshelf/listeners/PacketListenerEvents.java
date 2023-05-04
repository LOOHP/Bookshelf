/*
 * This file is part of Bookshelf.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.bookshelf.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class PacketListenerEvents {

    public PacketListenerEvents() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Bookshelf.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.OPEN_WINDOW) {
            @SuppressWarnings("deprecation")
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                WrappedChatComponent title = packet.getChatComponents().read(0);
                String plain = ChatColor.stripColor(BaseComponent.toLegacyText(ComponentSerializer.parse(title.getJson())));
                if (plain.equals(BookshelfManager.DEFAULT_BOOKSHELF_NAME_TRANSLATABLE_PLACEHOLDER)) {
                    packet.getChatComponents().write(0, WrappedChatComponent.fromJson(BookshelfManager.DEFAULT_BOOKSHELF_NAME_JSON));
                }
            }
        });
    }

}
