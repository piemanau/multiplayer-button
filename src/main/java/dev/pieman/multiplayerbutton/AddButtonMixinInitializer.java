package dev.pieman.multiplayerbutton;

import net.fabricmc.api.ModInitializer;

public class AddButtonMixinInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Multiplayer Button added!");
    }
}
