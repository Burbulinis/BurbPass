package me.burb.burbpass.gui.util;

public class SlotUtil {
    public static int nextNonBorderSlot(int slot, int size) {
        while (isBorderSlot(++slot, size)) if (slot > size) return size;
        return slot;
    }

    public static boolean isBorderSlot(int slot, int size) {
        int mod = slot % 9;
        return slot < 9 || slot > size - 9 || mod == 8 || mod == 0;
    }

    public static int getSlot(int row, int col) {
        return ((row - 1) * 9) + (col - 1);
    }
}
