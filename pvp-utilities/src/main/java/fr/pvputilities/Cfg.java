package fr.pvputilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Cfg {
    public enum Lang { AUTO, FR, EN }
    public enum Mode { SKIN, ARMOR, BOTH }
    public enum Target { PLAYERS, ALL }
    public enum Unit { NUMBER, BLOCKS, METERS }
    public enum Side { LEFT, RIGHT }
    public enum Sky { CLEAR, RAIN, STORM }

    public enum Preset {
        DAY(1000), NOON(6000), SUNSET(12000), NIGHT(13000), MIDNIGHT(18000), MOON(16000), SUNRISE(23000);

        public final long ticks;

        Preset(long ticks) {
            this.ticks = ticks;
        }
    }

    public static final class Tint {
        public boolean enabled = true;
        public int color = 0xFFFF0000;
        public int opacity = 179;
        public Mode mode = Mode.BOTH;
        public Target target = Target.PLAYERS;
        public boolean self = true;
    }

    public static final class Hit {
        public boolean enabled = true;
        public boolean eye = true;
        public boolean look = true;
        public boolean reach = true;
        public boolean self = true;
        public boolean players = true;
        public boolean mobs = true;
        public boolean projectiles = true;
        public boolean items = true;
        public boolean others = true;
        public int width = 2;
        public int distance = 64;
        public int base = 0xFFFFFFFF;
        public int eyeColor = 0xFFFF0000;
        public int lookColor = 0xFF0000FF;
        public int target = 0xFFFF0000;
        public int hurt = 0xFFFFFF00;
        public int reachColor = 0xFFFF8000;
    }

    public static final class Reach {
        public boolean enabled = true;
        public boolean shadow = true;
        public boolean background = false;
        public int scale = 100;
        public double x = 0.5;
        public double y = 0.56;
        public int mainColor = 0xFFFFFFFF;
        public int shadowColor = 0xFF000000;
        public int backgroundColor = 0x80000000;
        public int decimals = 2;
        public Unit unit = Unit.BLOCKS;
        public boolean playersOnly = true;
        public boolean keepLast = false;
        public int hideSeconds = 3;
    }

    public static final class Saturation {
        public boolean enabled = true;
    }

    public static final class Cross {
        public boolean enabled = true;
        public boolean playersOnly = true;
        public boolean critNeedsCharge = true;
        public int hitColor = 0xFFFF0000;
        public int critColor = 0xFF0000FF;
    }

    public static final class Momentum {
        public boolean enabled = true;
        public boolean text = false;
        public boolean smooth = true;
        public boolean objective = false;
        public boolean total = false;
        public boolean relative = true;
        public int hideTicks = 25;
        public int textX = 0;
        public int textY = 0;
        public double[] forward = {1.3, 4.32, 5.61, 9.6};
        public double[] backward = {1.3, 4.32, 5.61, 9.6};
    }

    public static final class Swing {
        public boolean enabled = false;
        public int depth = 25;
        public int angle = 20;
    }

    public static final class Totem {
        public boolean enabled = true;
        public Side side = Side.RIGHT;
    }

    public static final class Tab {
        public boolean enabled = true;
        public int maxPlayers = 80;
        public boolean removeHeader = false;
        public boolean removeFooter = false;
        public int scale = 100;
        public int x = 0;
        public int y = 0;
    }

    public static final class Board {
        public boolean enabled = true;
        public int scale = 100;
        public int x = 0;
        public int y = 0;
    }

    public static final class Nametag {
        public boolean enabled = true;
        public boolean remove = false;
        public boolean own = false;
        public boolean hideEntities = false;
        public boolean hidePlayers = false;
        public boolean hideStands = false;
        public int height = 0;
        public int scale = 100;
    }

    public static final class Clock {
        public boolean enabled = false;
        public Preset preset = Preset.DAY;
    }

    public static final class Climate {
        public boolean enabled = false;
        public Sky type = Sky.CLEAR;
    }

    public static final class Scales {
        public boolean enabled = true;
        public double hotbar = 1.0;
        public double actionbar = 1.0;
        public double crosshair = 1.0;
        public double health = 1.0;
        public double armor = 1.0;
        public double food = 1.0;
        public double air = 1.0;
        public double xpbar = 1.0;
        public double xplevel = 1.0;
        public double item = 1.0;
        public double effects = 1.0;
        public double boss = 1.0;
        public double title = 1.0;
        public double inventory = 1.0;
        public double pause = 1.0;
        public double settings = 1.0;

        public double get(String name) {
            return switch (name) {
                case "hotbar" -> hotbar;
                case "actionbar" -> actionbar;
                case "crosshair" -> crosshair;
                case "health" -> health;
                case "armor" -> armor;
                case "food" -> food;
                case "air" -> air;
                case "xpbar" -> xpbar;
                case "xplevel" -> xplevel;
                case "item" -> item;
                case "effects" -> effects;
                case "boss" -> boss;
                case "title" -> title;
                case "inventory" -> inventory;
                case "pause" -> pause;
                case "settings" -> settings;
                default -> 1.0;
            };
        }

        public void set(String name, double v) {
            switch (name) {
                case "hotbar" -> hotbar = v;
                case "actionbar" -> actionbar = v;
                case "crosshair" -> crosshair = v;
                case "health" -> health = v;
                case "armor" -> armor = v;
                case "food" -> food = v;
                case "air" -> air = v;
                case "xpbar" -> xpbar = v;
                case "xplevel" -> xplevel = v;
                case "item" -> item = v;
                case "effects" -> effects = v;
                case "boss" -> boss = v;
                case "title" -> title = v;
                case "inventory" -> inventory = v;
                case "pause" -> pause = v;
                case "settings" -> settings = v;
                default -> {
                }
            }
        }
    }

    public static final class Bright {
        public boolean enabled = false;
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Cfg inst = new Cfg();

    public Lang lang = Lang.AUTO;
    public Tint tint = new Tint();
    public Hit hit = new Hit();
    public Reach reach = new Reach();
    public Saturation saturation = new Saturation();
    public Cross cross = new Cross();
    public Momentum momentum = new Momentum();
    public Bright bright = new Bright();
    public Swing swing = new Swing();
    public Totem totem = new Totem();
    public Tab tab = new Tab();
    public Board board = new Board();
    public Nametag nametag = new Nametag();
    public Clock time = new Clock();
    public Climate weather = new Climate();
    public Scales scales = new Scales();

    public static Cfg get() {
        return inst;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("pvp-utilities.json");
    }

    public static void load() {
        try (Reader r = Files.newBufferedReader(path())) {
            Cfg c = GSON.fromJson(r, Cfg.class);
            if (c != null) {
                if (c.tint == null) c.tint = new Tint();
                if (c.hit == null) c.hit = new Hit();
                if (c.reach == null) c.reach = new Reach();
                if (c.saturation == null) c.saturation = new Saturation();
                if (c.cross == null) c.cross = new Cross();
                if (c.momentum == null) c.momentum = new Momentum();
                if (c.bright == null) c.bright = new Bright();
                if (c.swing == null) c.swing = new Swing();
                if (c.totem == null) c.totem = new Totem();
                if (c.tab == null) c.tab = new Tab();
                if (c.board == null) c.board = new Board();
                if (c.nametag == null) c.nametag = new Nametag();
                if (c.time == null) c.time = new Clock();
                if (c.weather == null) c.weather = new Climate();
                if (c.scales == null) c.scales = new Scales();
                if (c.totem.side == null) c.totem.side = Side.RIGHT;
                if (c.time.preset == null) c.time.preset = Preset.DAY;
                if (c.weather.type == null) c.weather.type = Sky.CLEAR;
                if (c.lang == null) c.lang = Lang.AUTO;
                if (c.reach.unit == null) c.reach.unit = Unit.BLOCKS;
                if (c.tint.mode == null) c.tint.mode = Mode.BOTH;
                if (c.tint.target == null) c.tint.target = Target.PLAYERS;
                if (c.momentum.forward == null || c.momentum.forward.length != 4) c.momentum.forward = new Momentum().forward;
                if (c.momentum.backward == null || c.momentum.backward.length != 4) c.momentum.backward = new Momentum().backward;
                inst = c;
            }
        } catch (Exception e) {
            inst = new Cfg();
            save();
        }
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(path())) {
            GSON.toJson(inst, w);
        } catch (Exception ignored) {
        }
    }

    public static void saveAndApply() {
        save();
        T.reset();
        TintApply.apply();
    }
}
