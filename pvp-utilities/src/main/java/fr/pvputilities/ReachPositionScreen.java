package fr.pvputilities;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class ReachPositionScreen extends Screen {
    private static final int KEY_RIGHT = 262, KEY_LEFT = 263, KEY_DOWN = 264, KEY_UP = 265;
    private static final int BUTTON_WIDTH = 80, BUTTON_GAP = 6;

    private final Screen parent;
    private final double startX;
    private final double startY;
    private boolean dragging;
    private double grabX, grabY;
    private int[] bounds = {0, 0, 0, 0};

    public ReachPositionScreen(Screen parent) {
        super(Component.literal(T.get("pos.title")));
        this.parent = parent;
        this.startX = Cfg.get().reach.x;
        this.startY = Cfg.get().reach.y;
    }

    @Override
    protected void init() {
        int y = this.height - 50;
        int x = this.width / 2 - (BUTTON_WIDTH * 3 + BUTTON_GAP * 2) / 2;
        addRenderableWidget(Button.builder(T.text("pos.reset"), b -> {
            Cfg.get().reach.x = 0.5;
            Cfg.get().reach.y = 0.56;
        }).bounds(x, y, BUTTON_WIDTH, 20).build());
        addRenderableWidget(Button.builder(T.text("pos.cancel"), b -> cancel())
                .bounds(x + BUTTON_WIDTH + BUTTON_GAP, y, BUTTON_WIDTH, 20).build());
        addRenderableWidget(Button.builder(T.text("pos.apply"), b -> apply())
                .bounds(x + (BUTTON_WIDTH + BUTTON_GAP) * 2, y, BUTTON_WIDTH, 20).build());
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        Cfg.Reach r = Cfg.get().reach;
        String text = ReachHud.format(ReachTracker.preview(), r);
        bounds = ReachHud.draw(g, this.font, text, r);
        boolean hover = mouseX >= bounds[0] && mouseX <= bounds[2] && mouseY >= bounds[1] && mouseY <= bounds[3];
        int c = dragging || hover ? 0xFFFFFF55 : 0xAAFFFFFF;
        g.fill(bounds[0] - 2, bounds[1] - 2, bounds[2] + 2, bounds[1] - 1, c);
        g.fill(bounds[0] - 2, bounds[3] + 1, bounds[2] + 2, bounds[3] + 2, c);
        g.fill(bounds[0] - 2, bounds[1] - 2, bounds[0] - 1, bounds[3] + 2, c);
        g.fill(bounds[2] + 1, bounds[1] - 2, bounds[2] + 2, bounds[3] + 2, c);
        g.drawCenteredString(this.font, T.get("pos.hint"), this.width / 2, 12, 0xFFFFFFFF);
        super.render(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;
        if (event.button() == 0 && event.x() >= bounds[0] - 2 && event.x() <= bounds[2] + 2
                && event.y() >= bounds[1] - 2 && event.y() <= bounds[3] + 2) {
            dragging = true;
            grabX = event.x() - (bounds[0] + bounds[2]) / 2.0;
            grabY = event.y() - (bounds[1] + bounds[3]) / 2.0;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            Cfg.Reach r = Cfg.get().reach;
            r.x = clamp((event.x() - grabX) / this.width);
            r.y = clamp((event.y() - grabY) / this.height);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        Cfg.Reach r = Cfg.get().reach;
        switch (event.key()) {
            case KEY_LEFT -> r.x = clamp(r.x - 1.0 / this.width);
            case KEY_RIGHT -> r.x = clamp(r.x + 1.0 / this.width);
            case KEY_UP -> r.y = clamp(r.y - 1.0 / this.height);
            case KEY_DOWN -> r.y = clamp(r.y + 1.0 / this.height);
            default -> {
                return super.keyPressed(event);
            }
        }
        return true;
    }

    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    private void cancel() {
        Cfg.get().reach.x = startX;
        Cfg.get().reach.y = startY;
        this.minecraft.setScreen(parent);
    }

    private void apply() {
        Cfg.save();
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        cancel();
    }
}
