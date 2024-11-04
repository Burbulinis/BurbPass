package me.burb.burbpass;

import me.burb.burbpass.utils.GUI;
import org.bukkit.plugin.java.JavaPlugin;

public class BurbPass extends JavaPlugin {

    @Override
    public void onEnable() {
        GUI.setPlugin(this);
    }
}