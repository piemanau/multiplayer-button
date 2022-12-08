package com.limeshulkerbox.multiplayerbutton.mixin;

import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameMenuScreen.class, priority = 9999)
public class AddButtonMixin extends Screen
{
protected AddButtonMixin(Text title) {
	super(title);
}

@Inject(at = @At("TAIL"), method = "initWidgets")
private void init(CallbackInfo info)
{
	System.out.println("This line is printed by an example mod mixin!");
	assert this.client != null;
	boolean bl = this.client.isMultiplayerEnabled();
	int lowestY3 = 0;
	int lowestY2 = 0;
	int lowestY1 = 0;
	ButtonWidget lowestButton2 = null;
	ButtonWidget lowestButton1 = null;
	for (int i = 0; i < children().size(); i++)
	{
		ButtonWidget button = (ButtonWidget) children().get(i);
		if (lowestY1 < button.y)
		{
			lowestY3 = lowestY2;
			lowestY2 = lowestY1;
			lowestY1 = button.y;
			lowestButton2 = lowestButton1;
			lowestButton1 = button;
		}
		else if (lowestY2 < button.y)
		{
			lowestY3 = lowestY2;
			lowestY2 = button.y;
			lowestButton2 = button;
		}
		else if (lowestY3 < button.y)
		{
			lowestY3 = button.y;
		}
	}
	assert lowestButton1 != null;
	assert lowestButton2 != null;
	this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, lowestY1, 204, lowestButton1.getHeight(), Text.translatable("menu.multiplayer"), (buttonWidget) ->
	{
		//copied from return to title screen
		{
			boolean bl1 = this.client.isInSingleplayer();
			boolean bl2 = this.client.isConnectedToRealms();
			buttonWidget.active = false;
			assert this.client.world != null;
			this.client.world.disconnect();
			if (bl1) this.client.disconnect(new MessageScreen(Text.translatable("menu.savingLevel")));
			else this.client.disconnect();

			/*if (bl1) {
				this.client.openScreen(new TitleScreen());
			} else if (bl2) {
				RealmsBridgeScreen realmsBridgeScreen = new RealmsBridgeScreen();
				realmsBridgeScreen.switchToRealms(new TitleScreen());
			} else {
				this.client.openScreen(new MultiplayerScreen(new TitleScreen()));
			}*/
		}

		Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(new TitleScreen()) : new MultiplayerWarningScreen(new TitleScreen());
		this.client.setScreen(screen);
	}));

	lowestButton1.y = lowestY1 * 2 - lowestY2;
	}
}
