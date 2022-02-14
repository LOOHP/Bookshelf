package com.loohp.bookshelf.objectholders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BookshelfHolder implements InventoryHolder {

    private BlockPosition position;
    private String title;
    private Inventory inventory;

    private Unsafe unsafe;

    public BookshelfHolder(BlockPosition position, String title, Inventory inventory) {
        this.position = position;
        this.title = title;
        this.inventory = inventory;
        this.unsafe = null;
    }

    public BlockPosition getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @SuppressWarnings({"DeprecatedIsStillUsed", "deprecation"})
    @Deprecated
    public Unsafe getUnsafe() {
        if (unsafe != null) {
            return unsafe;
        }
        BookshelfHolder thisRef = this;
        return unsafe = new Unsafe() {
            @Deprecated
            public void setPosition(BlockPosition position) {
                thisRef.position = position;
            }

            @Deprecated
            public void setTitle(String title) {
                thisRef.title = title;
            }

            @Deprecated
            public void setInventory(Inventory inventory) {
                thisRef.inventory = inventory;
            }
        };
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public interface Unsafe {

        /**
         * <b>Dangerous, non-deterministic behavior if not used correctly</b>
         */
        @Deprecated
        void setPosition(BlockPosition position);

        /**
         * <b>Dangerous, non-deterministic behavior if not used correctly</b>
         */
        @Deprecated
        void setTitle(String title);

        /**
         * <b>Dangerous, non-deterministic behavior if not used correctly</b>
         */
        @Deprecated
        void setInventory(Inventory inventory);

    }

}
