package fun.yamds.maplegroveMisidechat.listener;

import fun.yamds.maplegroveMisidechat.MaplegroveMisidechat;
import fun.yamds.maplegroveMisidechat.entity.ChatTextDisplay;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class chatListener implements Listener {
    MaplegroveMisidechat instance =  MaplegroveMisidechat.getInstance();
    FileConfiguration config = instance.getConfig();
    Random random = new Random();

    /**
     * 监听玩家消息
     */
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        TextComponent result = new TextComponent();
        String chatheadStyle = config.getString("chathead-style");

        String message = event.getMessage();
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTask(instance, () -> text_display(player, message));
    }

    /**
     * 总的处理逻辑
     */
    public void text_display(Player player, String message) {
        World world = player.getWorld();
        Location location = player.getLocation();

        // 分段
        List<String> groups = getStrings(message);

        new BukkitRunnable() {
            int groupIndex = 0;
            int charIndex = 0;
            List<ChatTextDisplay> currentGroup = new ArrayList<>();
            boolean waitingExplode = false;
            Location newLoc = location.clone();

            @Override
            public void run() {
                if (groupIndex >= groups.size()) {
                    cancel();
                    return;
                }

                if (waitingExplode) return;

                String currentMessage = groups.get(groupIndex);
                char c = currentMessage.charAt(charIndex++);
                float[] widthInfo = getWidthInfo(currentMessage, charIndex);

                // 传给构造函数
                ChatTextDisplay charTextDisplay = new ChatTextDisplay(
                        widthInfo[0],   // 当前字符偏移
                        widthInfo[1],   // 总宽度
                        c,
                        newLoc,
                        world
                );
                currentGroup.add(charTextDisplay);

                if (charIndex >= currentMessage.length()) {
                    waitingExplode = true;
                    charIndex = 0;

                    Bukkit.getScheduler().runTaskLater(instance, () -> {
                        explodeText(currentGroup);
                        currentGroup.clear();
                        groupIndex++;
                        waitingExplode = false;

                        newLoc.setYaw(location.getYaw() + random.nextInt(45) - 22.5f);
                        newLoc.setY(location.getY() + random.nextFloat(0.3f));
                    }, 40L);
                }
            }
        }.runTaskTimer(instance, 0L, 1L);
    }

    /**
     * 执行掉落物掉落
     */
    void explodeText(List<ChatTextDisplay> activeTexts) {
        for (ChatTextDisplay activeText : activeTexts) {
            activeText.getTextDisplay().setInterpolationDelay(20);
            activeText.moveToTarget(100);
        }
    }


    /**
     * 计算字符串的总宽度和指定下标字符的偏移宽度
     * ASCII(<=127) 宽度 1.2，其他宽度 1.4
     *
     * @param text  要计算的字符串
     * @param index 当前字符下标（0 ~ text.length()）
     * @return float[2] → [当前偏移, 总宽度]
     */
    private static float[] getWidthInfo(String text, int index) {
        float totalWidth = 0f;
        float currentOffset = 0f;

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            float w = (ch <= 127) ? 1.15f : 1.4f;
            totalWidth += w;

            if (i < index) {
                currentOffset += w;
            }
        }

        return new float[]{currentOffset, totalWidth};
    }

    /**
     * 根据标点、空格和文本长度对长文本进行分段
     */
    private static @NotNull List<String> getStrings(String message) {
        String splitChars = " ,.，。;:;";

        // 自然分组
        List<String> groups = new ArrayList<>();
        int start = 0;
        while (start < message.length()) {
            int end = Math.min(start + 25, message.length());

            if (end < message.length()) {
                // 找到离 end 最近的分隔符
                int splitPos = -1;
                for (int i = end; i < message.length(); i++) {
                    if (splitChars.indexOf(message.charAt(i)) >= 0) {
                        splitPos = i + 1;
                        break;
                    }
                }
                if (splitPos != -1) end = splitPos;
            }

            String part = message.substring(start, end);
            if (end < message.length()) part += " - "; // 非最后一组加 -
            groups.add(part);
            start = end;
        }
        return groups;
    }

}
