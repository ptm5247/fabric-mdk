package cmd5247;

import cmd5247.commands.Commands;
import cmd5247.gui.screens.CommandScreen;
import cmd5247.task.TaskManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

@Environment(value=EnvType.CLIENT)
public class CMD implements ClientModInitializer, ClientTickEvents.EndTick {

  private static CMD instance;

  public static CMD getInstance() {
    return instance;
  }

  public final Options options = new Options();
  public final Commands commands = new Commands();
  public final TaskManager taskManager = new TaskManager();

  public CMD() {
    instance = this;
  }

	@Override
	public void onInitializeClient() {
    KeyBindingHelper.registerKeyBinding(options.keyCommand);
    ClientTickEvents.END_CLIENT_TICK.register(this);
		this.taskManager.registerHudRenderCallbacks(HudRenderCallback.EVENT);
	}

  @Override
  public void onEndTick(Minecraft client) {
    // opens command screen - see Minecraft.handleKeybinds
    if (client.screen == null && client.getOverlay() == null && this.options.keyCommand.consumeClick())
      if (client.getChatStatus().isChatAllowed(client.isLocalServer()))
        client.setScreen(new CommandScreen("$"));

    taskManager.tick();
  }



}
