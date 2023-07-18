package dev.pieman.multiplayerbutton.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
        assert client != null;
        if (client.isInSingleplayer() || client.isConnectedToRealms()) {
            ci.cancel();
            client.setScreen(parent);
        } else {
            if (parent instanceof GameMenuScreen) {
                ci.cancel();
                client.disconnect();
                client.setScreen(new TitleScreen());
            }
        }
    }

    @ModifyVariable(at = @At(value = "STORE"), method = "connect()V")
    private MultiplayerServerListWidget.Entry addMultiplayerButtonSinglePlayer(MultiplayerServerListWidget.Entry entry) {
        // Only applies if the entry is something join able.
        if (entry instanceof MultiplayerServerListWidget.ServerEntry || entry instanceof MultiplayerServerListWidget.LanServerEntry) {
            boolean bl1 = MinecraftClient.getInstance().isInSingleplayer();
            assert client != null;
            if (client.world != null) {
                assert MinecraftClient.getInstance().world != null;
                MinecraftClient.getInstance().world.disconnect();
                if (bl1)
                    MinecraftClient.getInstance().disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
            }
        }
        return entry;
    }
}
