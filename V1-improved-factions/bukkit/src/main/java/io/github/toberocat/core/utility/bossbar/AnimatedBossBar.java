package io.github.toberocat.core.utility.bossbar;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.async.AsyncTask;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static io.github.toberocat.core.utility.Utility.clamp;
import static io.github.toberocat.core.utility.Utility.lerp;

public class AnimatedBossBar extends SimpleBar {

    public static final long ANIMATION_TIME = 1;
    public static final int ANIMATION_SPEED = 10000;
    public static final int ANIMATION_MS_FADE = 700;
    public static final double EPS = 0.001;
    private final Ease ease;
    private int taskId = -1;
    private Runnable finishCallback;

    public AnimatedBossBar(String title, BarColor color, double min, double max) {
        super(title, color, min, max);
        this.ease = time -> time;
    }

    public AnimatedBossBar(String title, BarColor color, double min, double max, Ease ease) {
        super(title, color, min, max);
        this.ease = ease;
    }

    public void fadeInstantly(double value, Player... players) {
        if (players == null) return;

        for (Player player : players) addPlayer(player);

        finishCallback = () -> {
            for (Player player : players) removePlayer(player);
        };

        setValueAnimated(value);
    }

    public void fade(double value, Player... players) {
        if (players == null) return;

        for (Player player : players) addPlayer(player);

        finishCallback = () -> AsyncTask.runLaterSync(ANIMATION_MS_FADE, () -> {
            for (Player player : players) removePlayer(player);
        });
        AsyncTask.runLaterSync(ANIMATION_MS_FADE, () -> setValueAnimated(value));
    }

    public void fade(double value, List<Player> players) {
        if (players == null) return;

        for (Player player : players) addPlayer(player);

        finishCallback = () -> AsyncTask.runLaterSync(ANIMATION_MS_FADE, () -> {
            for (Player player : players) removePlayer(player);
        });
        AsyncTask.runLaterSync(ANIMATION_MS_FADE, () -> setValueAnimated(value));
    }

    public Ease getEase() {
        return ease;
    }

    public void setValueAnimated(double value) {
        Bukkit.getScheduler().cancelTask(taskId);

        if (bossBar.getProgress() < (value - min) / (max - min)) animateRising(value);
        else animateDropping(value);
    }

    private void animateDropping(double value) {
        double start = bossBar.getProgress();
        double end = clamp((value - min) / (max - min), min, max);

        double lastMs = System.currentTimeMillis();

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double elapsed = ease.evaluate((now - lastMs) / ANIMATION_SPEED / ANIMATION_TIME);
                double update = Math.max(bossBar.getProgress() - lerp(end, start, elapsed), 0);

                bossBar.setProgress(update);

                if ((update - end) < EPS) {
                    if (finishCallback != null) finishCallback.run();
                    cancel();
                }
            }
        }.runTaskTimer(MainIF.getIF(), 0, ANIMATION_TIME).getTaskId();
    }

    private void animateRising(double value) {
        double start = bossBar.getProgress();
        double end = clamp((value - min) / (max - min), min, max);

        final double lastMs = System.currentTimeMillis();

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double elapsed = ease.evaluate((now - lastMs) / ANIMATION_SPEED / ANIMATION_TIME);
                double update = Math.min(bossBar.getProgress() + lerp(start, end, elapsed), 1);

                bossBar.setProgress(update);

                if ((end - update) < EPS) {
                    if (finishCallback != null) finishCallback.run();
                    cancel();
                }
            }
        }.runTaskTimer(MainIF.getIF(), 0, ANIMATION_TIME).getTaskId();
    }
}
