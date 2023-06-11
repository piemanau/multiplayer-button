package com.limeshulkerbox.multiplayerbutton.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameMenuScreen.class, priority = 9999)
public class AddButtonMixin extends Screen {
	protected AddButtonMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("TAIL"), method = "initWidgets")
	private void init(CallbackInfo info) {
		assert this.client != null;
		boolean bl = this.client.isMultiplayerEnabled();
		int lowestY3 = 0;
		int lowestY2 = 0;
		int lowestY1 = 0;
		ButtonWidget lowestButton2 = null;
		ButtonWidget lowestButton1 = null;
		for (int i = 0; i < children().size(); i++) {
			ButtonWidget button = (ButtonWidget) children().get(i);
			if (lowestY1 < button.getY()) {
				lowestY3 = lowestY2;
				lowestY2 = lowestY1;
				lowestY1 = button.getY();
				lowestButton2 = lowestButton1;
				lowestButton1 = button;
			} else if (lowestY2 < button.getY()) {
				lowestY3 = lowestY2;
				lowestY2 = button.getY();
				lowestButton2 = button;
			} else if (lowestY3 < button.getY()) {
				lowestY3 = button.getY();
			}
		}
		assert lowestButton1 != null;
		assert lowestButton2 != null;
		ButtonWidget buttonWidget = ButtonWidget.builder(Text.translatable("menu.multiplayer"), new ButtonWidget.PressAction() {
			private MinecraftClient client;

			@Override
			public void onPress(ButtonWidget button) {
				if (client == null) client = MinecraftClient.getInstance();
				if (client != null) {
					boolean bl1 = this.client.isInSingleplayer();
					boolean bl2 = this.client.isConnectedToRealms();
					button.active = false;
					assert this.client.world != null;
					this.client.world.disconnect();
					if (bl1) {
						this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
						this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
					} else {
						this.client.disconnect();
						this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
					}
				}
			}
		}).build();
		this.addDrawableChild(buttonWidget);
		lowestButton1.setY(lowestY1 * 2 - lowestY2);
	}
}