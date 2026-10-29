package fr.pvputilities;

public final class Features {
    public static final String[] NAMES = {
            "hitboxes", "damage-tint", "crosshair", "reach", "saturation", "momentum", "fullbright", "swing",
            "totems", "tabtweaks", "scoreboardtweaks", "nametags", "timechanger", "weatherchanger", "hudscale"
    };

    public static boolean get(String name) {
        Cfg c = Cfg.get();
        return switch (name) {
            case "hitboxes" -> c.hit.enabled;
            case "damage-tint" -> c.tint.enabled;
            case "crosshair" -> c.cross.enabled;
            case "reach" -> c.reach.enabled;
            case "saturation" -> c.saturation.enabled;
            case "momentum" -> c.momentum.enabled;
            case "fullbright" -> c.bright.enabled;
            case "swing" -> c.swing.enabled;
            case "totems" -> c.totem.enabled;
            case "tabtweaks" -> c.tab.enabled;
            case "scoreboardtweaks" -> c.board.enabled;
            case "nametags" -> c.nametag.enabled;
            case "timechanger" -> c.time.enabled;
            case "weatherchanger" -> c.weather.enabled;
            case "hudscale" -> c.scales.enabled;
            default -> false;
        };
    }

    public static void set(String name, boolean value) {
        Cfg c = Cfg.get();
        switch (name) {
            case "hitboxes" -> c.hit.enabled = value;
            case "damage-tint" -> c.tint.enabled = value;
            case "crosshair" -> c.cross.enabled = value;
            case "reach" -> c.reach.enabled = value;
            case "saturation" -> c.saturation.enabled = value;
            case "momentum" -> c.momentum.enabled = value;
            case "fullbright" -> c.bright.enabled = value;
            case "swing" -> c.swing.enabled = value;
            case "totems" -> c.totem.enabled = value;
            case "tabtweaks" -> c.tab.enabled = value;
            case "scoreboardtweaks" -> c.board.enabled = value;
            case "nametags" -> c.nametag.enabled = value;
            case "timechanger" -> c.time.enabled = value;
            case "weatherchanger" -> c.weather.enabled = value;
            case "hudscale" -> c.scales.enabled = value;
            default -> {
            }
        }
        Cfg.save();
    }
}
