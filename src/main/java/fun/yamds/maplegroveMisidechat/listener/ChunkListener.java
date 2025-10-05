package fun.yamds.maplegroveMisidechat.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for(Entity entity : event.getChunk().getEntities()) {
            if(!entity.hasMetadata("ChatTextDisplay"))
                continue;
            entity.remove();
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for(Entity entity : event.getChunk().getEntities()) {
            if(!entity.hasMetadata("ChatTextDisplay"))
                continue;
            entity.remove();
        }
    }
}
