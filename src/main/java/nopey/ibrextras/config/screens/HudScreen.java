package nopey.ibrextras.config.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import nopey.ibrextras.config.Config;
import nopey.ibrextras.config.Data;
import nopey.ibrextras.objects.HudElement;

public class HudScreen extends Screen {
   private final Screen parent;
   public MinecraftClient client;
   public ButtonWidget btnAdd;
   public ButtonWidget btnAddSpecial;

   protected HudScreen(Screen parent) {
      super(Text.translatable("nopey.ibrextras.config.hudscreen.title"));
      this.parent = parent;
      this.client = MinecraftClient.getInstance();
   }

   protected void init() {
      this.btnAdd = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.addcustom"), (button) -> {
         this.client.setScreen(new ElementScreen(this));
      }).dimensions(this.width / 2 - 100, this.height / 2 - 15, 200, 20).build();
      this.btnAddSpecial = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.addspecial"), (button) -> {
         this.client.setScreen(new SpecialElementScreen(this));
      }).dimensions(this.width / 2 - 100, this.height / 2 + 15, 200, 20).build();
      this.addDrawableChild(this.btnAdd);
      this.addDrawableChild(this.btnAddSpecial);

      for(int i = 0; i < Data.hudElements.size(); ++i) {
         this.addDrawableChild((HudElement)Data.hudElements.get(i));
         this.addDrawableChild(((HudElement)Data.hudElements.get(i)).btnDelete);
      }

   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      super.render(context, mouseX, mouseY, delta);
      context.drawCenteredTextWithShadow (this.client.textRenderer, this.title , this.width / 2, 20, -1);
   }

   public void close() {
      this.client.setScreen(this.parent);
      Config.save();
   }

   public void addChild(HudElement hudElement) {
      this.addDrawableChild(hudElement);
   }
}
