package dev.pieman.multiplayerbutton.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GameMenuScreen.class, priority = 9999)
public abstract class AddButtonMixin extends Screen {
	protected AddButtonMixin(Text title) {
		super(title);
	}

	@ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;DISCONNECT_TEXT:Lnet/minecraft/text/Text;"), method = "initWidgets")
	private GridWidget.Adder addMultiplayerButtonMultiplayer(GridWidget.Adder adder) {
		addMultiplayerButton(adder);
		return adder;
	}

	@ModifyVariable(at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/GameMenuScreen;RETURN_TO_MENU_TEXT:Lnet/minecraft/text/Text;"), method = "initWidgets")
	private GridWidget.Adder addMultiplayerButtonSinglePlayer(GridWidget.Adder adder) {
		addMultiplayerButton(adder);
		return adder;
	}

	@Unique
	private void addMultiplayerButton(GridWidget.Adder adder) {
		ButtonWidget multiplayerButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), (button) -> {
			assert this.client != null;
			if (this.client.options.skipMultiplayerWarning) {
				client.setScreen(new MultiplayerScreen(this));
			} else {
				client.setScreen(new MultiplayerWarningScreen(this));
			}
		}).width(204).build());

		adder.add(multiplayerButton, 2);
	}
}
