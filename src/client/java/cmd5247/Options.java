package cmd5247;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(value=EnvType.CLIENT)
public class Options {

  public final KeyMapping keyCommand = new KeyMapping("Open Mod Command Terminal", GLFW.GLFW_KEY_RIGHT_ALT, "CMD");

}
