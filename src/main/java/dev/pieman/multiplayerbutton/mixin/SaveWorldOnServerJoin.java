package dev.pieman.multiplayerbutton.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class SaveWorldOnServerJoin extends Screen {

    @Shadow
    @Final
    private Screen parent;

    protected SaveWorldOnServerJoin(Text title) {
        super(title);
    }

    @Inject(at = @At(value = "HEAD"), method = "method_19912(Lnet/minecraft/client/gui/widget/ButtonWidget;)V", cancellable = true)
    private void modifyCancelButton(CallbackInfo ci) {
        System.out.println("Mixed in correctly");
        assert client != null;
        if (client.isInSingleplayer() || client.isConnectedToRealms()) {
            ci.cancel();
            client.setScreen(parent);
        } else {
            System.out.println("b");
            if (parent instanceof GameMenuScreen) {
                ci.cancel();
                client.disconnect();
                client.setScreen(new TitleScreen());
            }
        }
    }

    @Inject(method = "connect()V", at = @At(value = "HEAD"))
    private void injected(CallbackInfo ci) {
        //copied and modified from return to title screen
        boolean bl1 = MinecraftClient.getInstance().isInSingleplayer();
        assert client != null;
        if (client.world != null) {
            assert MinecraftClient.getInstance().world != null;
            MinecraftClient.getInstance().world.disconnect();
            if (bl1) MinecraftClient.getInstance().disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
        }
    }
}
