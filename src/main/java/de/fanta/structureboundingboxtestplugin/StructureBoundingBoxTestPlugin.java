package de.fanta.structureboundingboxtestplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;

public final class StructureBoundingBoxTestPlugin extends JavaPlugin implements Listener {

    private final Collection<Vector> debugList = new ArrayList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        for (GeneratedStructure generatedStructure : player.getChunk().getStructures()) {
            BoundingBox boundingBox = generatedStructure.getBoundingBox();
            Vector center = boundingBox.getCenter();
            if (!debugList.contains(center)) {
                debugList.add(center);
                getLogger().info("Found Structure: " + generatedStructure.getStructure().key().asMinimalString());
                replaceBlocksInBoundingBoxFrameWithSigns(player.getWorld(), boundingBox, generatedStructure.getStructure().key().asMinimalString());
            }
        }
    }


    public void replaceBlocksInBoundingBoxFrameWithSigns(World world, BoundingBox box, String structure) {
        int minX = (int) box.getMinX();
        int minY = (int) box.getMinY();
        int minZ = (int) box.getMinZ();
        int maxX = (int) box.getMaxX();
        int maxY = (int) box.getMaxY();
        int maxZ = (int) box.getMaxZ();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (x == minX || x == maxX || z == minZ || z == maxZ) {
                    Block blockBottom = world.getBlockAt(x, minY, z);
                    blockBottom.setType(Material.DIAMOND_BLOCK);

                    Block blockTop = world.getBlockAt(x, maxY, z);
                    blockTop.setType(Material.DIAMOND_BLOCK);
                }
            }
        }

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (x == minX || x == maxX) {
                    Block blockNorth = world.getBlockAt(x, y, minZ);
                    blockNorth.setType(Material.DIAMOND_BLOCK);

                    Block blockSouth = world.getBlockAt(x, y, maxZ);
                    blockSouth.setType(Material.DIAMOND_BLOCK);
                }
            }
            for (int z = minZ; z <= maxZ; z++) {
                if (z == minZ || z == maxZ) {
                    Block blockWest = world.getBlockAt(minX, y, z);
                    blockWest.setType(Material.DIAMOND_BLOCK);

                    Block blockEast = world.getBlockAt(maxX, y, z);
                    blockEast.setType(Material.DIAMOND_BLOCK);
                }
            }
        }

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (x == minX || x == maxX || z == minZ || z == maxZ) {
                    Block block = world.getBlockAt(x, maxY + 1, z);
                    block.setType(Material.OAK_SIGN);
                    if (block.getState() instanceof Sign sign) {
                        sign.getSide(Side.FRONT).line(2, Component.text(structure));
                        sign.update();
                    }
                }
            }
        }
    }
}
