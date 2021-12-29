/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.x150.sipprivate.feature.gui.notifications;

import me.x150.sipprivate.CoffeeClientMain;
import me.x150.sipprivate.feature.gui.clickgui.ClickGUI;
import me.x150.sipprivate.helper.font.FontRenderers;
import me.x150.sipprivate.helper.render.Renderer;
import me.x150.sipprivate.helper.util.Transitions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NotificationRenderer {

    public static final List<Notification> notifications       = new ArrayList<>();
    public static final List<Notification> topBarNotifications = new ArrayList<>();

    public static void render() {
        renderSide();
        renderTop();
    }

    public static void onFastTick() {
        for (Notification notification : new ArrayList<>(notifications)) {
            notification.renderPosX = Transitions.transition(notification.renderPosX, notification.posX, 10);
            notification.renderPosY = Transitions.transition(notification.renderPosY, notification.posY, 10);
            notification.animationProgress = Transitions.transition(notification.animationProgress, notification.animationGoal, 10, 0.0001);
        }
        for (Notification notification : new ArrayList<>(topBarNotifications)) {
            notification.renderPosX = Transitions.transition(notification.renderPosX, notification.posX, 10);
            notification.renderPosY = Transitions.transition(notification.renderPosY, notification.posY, 10);
            if (notification.shouldDoAnimation) {
                notification.animationProgress = Transitions.transition(notification.animationProgress, notification.animationGoal, 10, 0.0001);
            }
        }
    }

    public static void renderTop() {
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        int baseX = CoffeeClientMain.client.getWindow().getScaledWidth() / 2;
        int height = 16;
        int baseY = -height - 5;
        int currentYOffset = 5;
        float minWidth = 50;
        long c = System.currentTimeMillis();
        ArrayList<Notification> nf = new ArrayList<>(topBarNotifications);
        nf.sort(Comparator.comparingDouble(value -> -FontRenderers.getNormal().getStringWidth(String.join(" ", value.contents))));
        for (Notification notification : nf) {
            double timeRemaining = Math.abs(c - notification.creationDate - notification.duration) / (double) notification.duration;
            timeRemaining = MathHelper.clamp(timeRemaining, 0, 1);
            boolean notificationExpired = notification.creationDate + notification.duration < c;
            if (notification.duration < 0) {
                timeRemaining = 0;
                notificationExpired = false;
            }
            notification.posX = notification.renderPosX = baseX;
            if (notification.renderPosY == -69 || notification.posY == -69) {
                notification.renderPosY = baseY;
            }
            if (!notificationExpired) {
                notification.posY = currentYOffset;
                if (Math.abs(notification.posY - notification.renderPosY) < 3) {
                    notification.animationGoal = 1;
                }
            } else {
                notification.animationGoal = 0;
                if (notification.animationProgress < 0.01) {
                    notification.posY = baseY - 5;
                    if (notification.renderPosY < baseY + 5) {
                        topBarNotifications.remove(notification);
                    }
                }
            }
            notification.shouldDoAnimation = notification.animationGoal != notification.animationProgress;
            String contents = String.join(" ", notification.contents);
            float width = FontRenderers.getNormal().getStringWidth(contents) + 5;
            width = width / 2f;
            width = Math.max(minWidth, width);
            Renderer.R2D.scissor(Renderer.R3D.getEmptyMatrixStack(),notification.renderPosX-width*notification.animationProgress,notification.renderPosY,notification.renderPosX+width*notification.animationProgress,notification.renderPosY + height + 1);
            Renderer.R2D.fill(ms, new Color(28, 28, 28, 200), notification.renderPosX - width, notification.renderPosY, notification.renderPosX + width, notification.renderPosY + height);
            FontRenderers.getNormal().drawCenteredString(ms, contents, notification.renderPosX, notification.renderPosY + height / 2f - FontRenderers.getNormal().getFontHeight() / 2f, 0xFFFFFF);
            double timeRemainingInv = 1 - timeRemaining;
            if (!notification.shouldDoAnimation && notification.animationProgress == 0 && notificationExpired) {
                timeRemainingInv = 1;
            }
            Renderer.R2D.fill(ms,ClickGUI.theme.getActive(),notification.renderPosX - width, notification.renderPosY + height - 1, notification.renderPosX + width, notification.renderPosY + height);
            Renderer.R2D.fill(ms, ClickGUI.theme.getAccent(), notification.renderPosX - width, notification.renderPosY + height - 1, notification.renderPosX - width + (width * 2 * timeRemainingInv), notification.renderPosY + height);
            Renderer.R2D.unscissor();
            currentYOffset += height + 3;
        }
    }

    public static void renderSide() {
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        int currentYOffset = 0;
        int baseX = CoffeeClientMain.client.getWindow().getScaledWidth();
        int baseY = CoffeeClientMain.client.getWindow().getScaledHeight() - 10;
        long c = System.currentTimeMillis();
        for (Notification notification : new ArrayList<>(notifications)) {
            double timeRemaining = Math.abs(c - notification.creationDate - notification.duration) / (double) notification.duration;
            timeRemaining = MathHelper.clamp(timeRemaining, 0, 1);
            boolean notificationExpired = notification.creationDate + notification.duration < c;
            int notifHeight = (int) (2 + ((notification.contents.length + (notification.title.isEmpty() ? 0 : 1)) * FontRenderers.getNormal().getFontHeight()));
            double descWidth = 0;
            for (String content : notification.contents) {
                descWidth = Math.max(FontRenderers.getNormal().getStringWidth(content), descWidth);
            }
            double notifWidth = Math.max(4+Math.max(descWidth,FontRenderers.getNormal().getStringWidth(notification.title)),100);
            notification.posY = baseY - currentYOffset - notifHeight;
            currentYOffset += notifHeight + 2;
            if (!notificationExpired) {
                notification.posX = baseX-notifWidth-10;
            } else {
                notification.posX = baseX + 10;
                if (notification.renderPosX > baseX + 5) {
                    notifications.remove(notification);
                    continue;
                }
            }
            if (notification.renderPosY == 0) {
                notification.renderPosY = notification.posY;
            }
            if (notification.renderPosX == 0) {
                notification.renderPosX = baseX+4;
            }
            Renderer.R2D.fill(new Color(28, 28, 28), notification.renderPosX, notification.renderPosY, notification.renderPosX + notifWidth, notification.renderPosY + notifHeight);
            Renderer.R2D.fill(ClickGUI.theme.getActive(),notification.renderPosX-1,notification.renderPosY,notification.renderPosX,notification.renderPosY+notifHeight);
            Renderer.R2D.fill(ClickGUI.theme.getAccent(),notification.renderPosX-1,notification.renderPosY,notification.renderPosX,notification.renderPosY+(notifHeight*(1-timeRemaining)));
            int currentYOffsetText = (int) (1 + FontRenderers.getNormal().getFontHeight());
            FontRenderers.getNormal().drawString(ms, notification.title, notification.renderPosX + 2, notification.renderPosY + 1, 0xFFFFFF);
            for (String content : notification.contents) {
                FontRenderers.getNormal().drawString(ms, content, notification.renderPosX + 2, notification.renderPosY + currentYOffsetText, 0xFFFFFF);
                currentYOffsetText += FontRenderers.getNormal().getFontHeight();
            }
        }
    }
}
