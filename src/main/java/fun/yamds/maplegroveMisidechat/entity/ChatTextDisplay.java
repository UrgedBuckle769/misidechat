package fun.yamds.maplegroveMisidechat.entity;

import fun.yamds.maplegroveMisidechat.MaplegroveMisidechat;

import java.util.Objects;
import java.util.Random;

import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ChatTextDisplay {

    private final Random random = new Random();
    private final MaplegroveMisidechat instance =  MaplegroveMisidechat.getInstance();
    private final TextDisplay textDisplay; // 本体


    public TextDisplay getTextDisplay() {
        return textDisplay;
    }

    public ChatTextDisplay(Float index, Float length, char c, Location location, World world) {
        Location newLoc = getTargetLocation(index, length, location.clone(), 5).clone();
        newLoc.setY(location.getY() + 1.3 + random.nextFloat(0.05f));
        this.textDisplay = (TextDisplay) world.spawnEntity(newLoc, EntityType.TEXT_DISPLAY);
        this.textDisplay.setMetadata("ChatTextDisplay", new FixedMetadataValue(instance, "ChatTextDisplay"));
        TextComponent text = new TextComponent(String.valueOf(c));
        text.setColor(ChatColor.of("#ffff55"));
        // text.setUnderlined(true);
        // text.setShadowColor(new java.awt.Color(255, 255, 255, 255)); // 阴影无用，好像得用nms才能修改阴影颜色

        this.textDisplay.setInterpolationDuration(10);
        this.textDisplay.setTextOpacity((byte) -127);
        this.textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        this.textDisplay.setShadowed(true);
        this.textDisplay.setGlowing(true);
        this.textDisplay.setBrightness(new Display.Brightness(15, 15));
        // this.textDisplay.set
        this.textDisplay.setText(text.toLegacyText());

        world.playSound(newLoc, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1.2f);

        // 创建初始化
        charTransformationInit(newLoc);
    }

    /**
     * 根据玩家坐标和距离，计算出目标位置，朝向玩家
     *
     * @param playerLoc
     * @param distance
     */
    public Location getTargetLocation(Float index, Float length, Location playerLoc, double distance) {
        float offsetX = (length / 2.0f - index) * -0.175f;
        Vector direction = playerLoc.getDirection().normalize();

        // 目标点坐标
        Vector tarVec = playerLoc.toVector().add(direction.multiply(distance));
        Location tarLoc = tarVec.toLocation(Objects.requireNonNull(playerLoc.getWorld()));

        // 使用玩家朝向的 yaw 来计算左右偏移
        double rad = Math.toRadians(playerLoc.getYaw());
        float rightX = (float) -Math.sin(rad);  // 右侧向量的 X 分量
        float rightZ = (float) Math.cos(rad);   // 右侧向量的 Z 分量
        
        Location newLoc = new Location(tarLoc.getWorld(), 
                tarLoc.getX() + rightX * offsetX, 
                tarLoc.getY(), 
                tarLoc.getZ() + rightZ * offsetX);
        
        // 设置文本显示的朝向，使其始终面向玩家（使用 setDirection 直接传入反向方向向量）
        Vector toPlayer = playerLoc.toVector().subtract(newLoc.toVector()).normalize();
        newLoc.setDirection(toPlayer);
        
        return newLoc;
    }

    /**
     * 根据 Location 计算字符的 Transformation
     * 不改变 base 坐标，仅仅修改渲染位置
     * 这里的trans有两个，是为了刚生成字符时的过度动画
     */
    public void charTransformationInit(Location location) {
        // yaw/pitch 转四元数
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Quaternionf rotation = new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch));

        Quaternionf oldRotation = new Quaternionf()
                .rotateY((float) Math.toRadians(-yaw + random.nextInt(90)-45))
                .rotateX((float) Math.toRadians(pitch + random.nextInt(90)-45));

        Transformation trans = new Transformation(
                new Vector3f(),
                rotation,
                new Vector3f(1.2f, 1.2f, 1.2f),
                new Quaternionf().identity()
        );

        Transformation oldTrans = new Transformation(
                new Vector3f(0f, random.nextFloat(0.25f)-.125f, 0f),   // 偏移 (相对朝向)
                oldRotation,
                new Vector3f(1.8f, 1.8f, 1.8f),
                new Quaternionf().identity()
        );

        this.textDisplay.setTransformation(oldTrans);

        Bukkit.getScheduler().runTaskLater(instance, () -> {
            // 应用变换
            this.textDisplay.setInterpolationDelay(0);
            this.textDisplay.setTransformation(trans);
        }, 1L);

    }

    /**
     * 平滑移动到目标位置（tick单位）
     * 让文本实体骑着物品下坠 (自己写的下坠着实没有骑乘掉落物看着真)
     */
    public void moveToTarget(int totalTicks) {
        if(this.textDisplay.isDead())
            return;
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey key = new NamespacedKey("minecraft", "air");
        if (itemMeta != null) {
            itemMeta.setItemModel(key);
            itemMeta.setMaxStackSize(1);
            itemMeta.setItemName("");
        }
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(1);
        Item item = this.textDisplay.getWorld().dropItemNaturally(this.textDisplay.getLocation(), itemStack);
        item.setPickupDelay(32767);
        item.setGlowing(false);

        item.setMetadata("ChatTextDisplay", new FixedMetadataValue(instance, "ChatTextDisplay"));
        item.addPassenger(this.textDisplay);    // 骑乘

        Vector3f translation = new Vector3f(0f, -0.15f, 0f);

        Transformation transformation = textDisplay.getTransformation();

        // 获取旋转部分（Quaternionf）
        Quaternionf original  = transformation.getLeftRotation();
        // 下落时随机旋转，这个随机角度大多数都对着玩家，由于字符只有单面显示，反面看不到太多字符
        Quaternionf randomRotation = new Quaternionf().rotationXYZ(
                (float) Math.toRadians(270 + random.nextInt(90)),
                (float) Math.toRadians(random.nextInt(90) - 45),
                (float) Math.toRadians(random.nextInt(90) - 45)
        );

        // 叠加旋转（四元数乘法）
        Quaternionf newRotation = new Quaternionf(original).mul(randomRotation);

        // 重新构造 Transformation
        Transformation trans = new Transformation(
                translation,
                newRotation,
                new Vector3f(1.7f, 1.7f, 1.7f),
                new Quaternionf()
        );

        this.textDisplay.setTransformation(trans);
        this.textDisplay.setInterpolationDelay(0);

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                // 超过时间强行kill
                if (tick > totalTicks) {
                    textDisplay.remove();
                    item.remove();
                    cancel();
                    return;
                }

                // 渐隐
                if (tick > totalTicks - 40 || textDisplay.getTicksLived() > totalTicks + 100) {
                    textDisplay.setTextOpacity((byte) (textDisplay.getTextOpacity() - 6));
                    if (!item.isDead() && tick == totalTicks - 39) {
                        item.removePassenger(textDisplay);
                        item.setGravity(false);
                        item.setVelocity(new Vector(0, .08, 0));
                    }
                    if (textDisplay.getTextOpacity() <= 0) {
                        textDisplay.remove();
                        item.remove();
                        cancel();
                        return;
                    }
                }
                tick++;
            }
        }.runTaskTimer(instance, 0, 1L);
    }
}
