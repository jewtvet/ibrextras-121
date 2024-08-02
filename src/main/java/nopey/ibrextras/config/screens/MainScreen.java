package nopey.ibrextras.config.screens;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import nopey.ibrextras.config.Config;
import nopey.ibrextras.config.Data;

@Environment(EnvType.CLIENT)
public class MainScreen extends Screen {
   private final Screen parent;
   public MinecraftClient client = MinecraftClient.getInstance();
   public ButtonWidget btnEnabled;
   public ButtonWidget btnPositioning;

   public MainScreen(Screen parent) {
      super(Text.translatable("nopey.ibrextras.config.mainscreen.title"));
      this.parent = parent;
   }

   protected void init() {
      this.btnEnabled = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.enabled").append(Data.enabled ? Text.translatable("nopey.ibrextras.on").formatted (Formatting .UNDERLINE) : Text.translatable("nopey.ibrextras.off").formatted (Formatting .RESET)), (button) -> {
         Data.enabled = !Data.enabled;
         button.setMessage (Text.translatable("nopey.ibrextras.button.enabled").append (Data.enabled ? Text.translatable("nopey.ibrextras.on").formatted (Formatting .UNDERLINE) : Text.translatable("nopey.ibrextras.off").formatted (Formatting .RESET)));
         Config.save();
      }).dimensions(this.width / 2 - 205, 50, 200, 20).tooltip (Tooltip .of (Text.translatable("nopey.ibrextras.tooltip.enabled"))).build();
      this.btnPositioning = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.positioning"), (button) -> {
         Screen screen = new HudScreen(this);
         if (this.client.world  != null) {
            this.client.setScreen(screen);
         }

      }).dimensions(this.width / 2 + 5, 50, 200, 20).tooltip (Tooltip .of (Text.translatable("nopey.ibrextras.tooltip.positioning"))).build();
      this.addDrawableChild(this.btnEnabled);
      this.addDrawableChild(this.btnPositioning);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.renderBackground(context, mouseX, mouseY, delta);
      super.render(context, mouseX, mouseY, delta);
      context.drawCenteredTextWithShadow (this.client.textRenderer, this.title , this.width / 2, 20, -1);
   }

   public void close() {
      this.client.setScreen(this.parent);
   }
}
