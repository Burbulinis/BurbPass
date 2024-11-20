package me.burb.burbpass.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class PaginatedGui extends SimpleGui {
    private int maxPage;
    private int page = 0;

    protected PaginatedGui(Player player, Inventory inventory) {
        super(player, inventory);
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    protected void registerPreviousPage(int slot) {
        registerHandler(slot, event -> {
            if (page > 1) page--;
            render();
        });
    }

    protected void registerNextPage(int slot) {
        registerHandler(slot, event -> {
            if (page < maxPage) page++;
            render();
        });
    }

    protected boolean hasPreviousPage() {
        return page > 1;
    }

    protected boolean hasNextPage() {
        return page < maxPage;
    }

    protected void setPage(int page) {
        this.page = page;
        render();
    }

    protected int getPage() {
        return page;
    }
}
