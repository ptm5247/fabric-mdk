package cmd5247.gui.components;

import java.util.List;

import com.google.common.base.Strings;

import cmd5247.CMD;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Environment(value=EnvType.CLIENT)
public class TaskManagerOverlay implements HudRenderCallback {

  @Override
  public void onHudRender(GuiGraphics guiGraphics, float partialTick) {
    if (CMD.getInstance().taskManager.isBusy()) {
      Minecraft.getInstance().getProfiler().push(getClass().getSimpleName());
      var task = CMD.getInstance().taskManager.getAactiveTask();
      renderLines(guiGraphics, task.getScript(), true);
      Minecraft.getInstance().getProfiler().pop();
    }
  }

  public static record DebugLine(String text, int indentation, boolean active) {
    
    public static DebugLine simple(String text) {
      return new DebugLine(text, 0, true);
    }
    
  }

  /** {@link DebugScreenOverlay#renderLines} */
  @SuppressWarnings("resource")
  private void renderLines(GuiGraphics guiGraphics, List<DebugLine> lines, boolean leftSide) {
    int indentWidth = Minecraft.getInstance().font.width("--");

    for(int i = 0; i < lines.size(); ++i) {
       DebugLine line = lines.get(i);
       if (!Strings.isNullOrEmpty(line.text)) {
          int w = Minecraft.getInstance().font.width(line.text);
          int h = Minecraft.getInstance().font.lineHeight;
          int x = leftSide ? (2 + line.indentation * indentWidth) : (guiGraphics.guiWidth() - 2 - w - line.indentation * indentWidth);
          int y = 2 + h * i;
          guiGraphics.fill(x - 1, y - 1, x + w + 1, y + h - 1, 0x90505050);
          guiGraphics.drawString(Minecraft.getInstance().font, line.text, x, y, line.active ? 0xE0E0E0 : 0xB0B0B0, false);
       }
    }
  }

}
