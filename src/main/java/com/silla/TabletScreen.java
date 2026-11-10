package com.silla;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Barra de texto igual a la del chat. Lo escrito NO se manda al chat: solo va a la tablet,
 * que lo convierte en voz. Enter = hablar, Esc = cancelar.
 */
public class TabletScreen extends Screen {
    private TextFieldWidget campo;

    public TabletScreen() {
        super(Text.translatable("screen.silla.tablet"));
    }

    @Override
    protected void init() {
        campo = new TextFieldWidget(this.textRenderer, 4, this.height - 14, this.width - 8, 12,
                Text.translatable("screen.silla.tablet"));
        campo.setMaxLength(SillaConfigClient.MAX_CHARS);
        campo.setDrawsBackground(false);
        campo.setPlaceholder(Text.translatable("screen.silla.tablet.hint"));
        this.addDrawableChild(campo);
        this.setInitialFocus(campo);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            String texto = campo.getText().trim();
            if (!texto.isEmpty() && ClientPlayNetworking.canSend(SpeakC2S.ID)) {
                ClientPlayNetworking.send(new SpeakC2S(texto));
            }
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // sin fondo borroso: se ve como el chat normal
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(2, this.height - 16, this.width - 2, this.height - 2, 0x80000000);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
