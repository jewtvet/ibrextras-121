package nopey.ibrextras.config.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import nopey.ibrextras.config.Config;
import nopey.ibrextras.config.Data;
import nopey.ibrextras.objects.HudElement;

public class ElementScreen extends Screen {
   private final HudScreen parent;
   private MinecraftClient client;
   public TextFieldWidget inputName;
   public TextFieldWidget inputColorA;
   public TextFieldWidget inputColorR;
   public TextFieldWidget inputColorG;
   public TextFieldWidget inputColorB;
   public TextFieldWidget inputScale;
   public TextWidget txtScale;
   public TextWidget txtError;
   public ButtonWidget btnTextInfo;
   public ButtonWidget btnAdd;

   protected ElementScreen(HudScreen parent) {
      super(Text.translatable("nopey.ibrextras.config.elementscreen.title"));
      this.parent = parent;
      this.client = MinecraftClient.getInstance();
   }

   protected void init() {
      this.inputName = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, this.height / 2 - 25, 175, 20, Text.empty());
      this.inputName.setText("Custom Text");
      this.inputColorA = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 100, this.height / 2, 40, 20, Text.empty());
      this.inputColorA.setEditableColor (-5592406);
      this.inputColorA.setText("255");
      this.inputColorR = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 55, this.height / 2, 40, 20, Text.empty());
      this.inputColorR.setEditableColor (-65536);
      this.inputColorR.setText("255");
      this.inputColorG = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 10, this.height / 2, 40, 20, Text.empty());
      this.inputColorG.setEditableColor (-16711936);
      this.inputColorG.setText("255");
      this.inputColorB = new TextFieldWidget(this.client.textRenderer, this.width / 2 + 35, this.height / 2, 40, 20, Text.empty());
      this.inputColorB.setEditableColor (-16776961);
      this.inputColorB.setText("255");
      this.inputScale = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 55, this.height / 2 + 25, 40, 20, Text.empty());
      this.inputScale.setText("1.0");
      this.txtScale = new TextWidget(this.width / 2 - 100, this.height / 2 + 25, 40, 20, Text.translatable("Scale"), this.client.textRenderer);
      this.txtScale.alignCenter();
      this.txtError = new TextWidget(this.width / 2 - 100, this.height / 2 + 50, 200, 20, Text.empty(), this.client.textRenderer);
      this.txtError.alignCenter();
      this.txtError.setTextColor(-65536);
      this.btnTextInfo = ButtonWidget.builder(Text.translatable("?"), (button) -> {
      }).tooltip (Tooltip .of (Text.translatable("nopey.ibrextras.tooltip.textinfo"))).dimensions(this.width / 2 + 80, this.height / 2 - 25, 20, 20).build();
      this.btnAdd = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.add"), (button) -> {
         if (this.isNumeric()) {
            HudElement hud = new HudElement(Text.literal(this.inputName.getText ()), 50, 50, this.getColor(), this.getScale());
            Data.hudElements.add(hud);
            this.parent.addChild(hud);
            this.close();
            Config.save();
         }

      }).dimensions(this.width / 2 - 10, this.height / 2 + 25, 110, 20).build();
      this.addDrawableChild(this.inputName);
      this.addDrawableChild(this.inputColorA);
      this.addDrawableChild(this.inputColorR);
      this.addDrawableChild(this.inputColorG);
      this.addDrawableChild(this.inputColorB);
      this.addDrawableChild(this.inputScale);
      this.addDrawableChild(this.txtScale);
      this.addDrawableChild(this.txtError);
      this.addDrawableChild(this.btnTextInfo);
      this.addDrawableChild(this.btnAdd);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.renderInGameBackground(context);
      super.render(context, mouseX, mouseY, delta);
      context.drawCenteredTextWithShadow (this.client.textRenderer, this.title , this.width / 2, 20, -1);

      try {
         context.fill(this.width / 2 + 80, this.height / 2, this.width / 2 + 100, this.height / 2 + 20, this.getColor());
      } catch (NumberFormatException var6) {
         context.fill(this.width / 2 + 80, this.height / 2, this.width / 2 + 100, this.height / 2 + 20, -16777216);
      }

      context.drawHorizontalLine(this.width / 2 + 79, this.width / 2 + 100, this.height / 2 - 1, -1);
      context.drawVerticalLine(this.width / 2 + 79, this.height / 2 - 1, this.height / 2 + 20, -1);
      context.drawHorizontalLine(this.width / 2 + 79, this.width / 2 + 100, this.height / 2 + 20, -1);
      context.drawVerticalLine(this.width / 2 + 100, this.height / 2 - 1, this.height / 2 + 20, -1);
      if (!this.isNumeric()) {
         this.txtError.setMessage (Text.translatable("The colors and scale have to be numbers!"));
      } else {
         this.txtError.setMessage (Text.empty());
      }

   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public int getColor() {
      try {
         int alpha = Integer.parseInt(this.inputColorA.getText ());
         int red = Integer.parseInt(this.inputColorR.getText ());
         int green = Integer.parseInt(this.inputColorG.getText ());
         int blue = Integer.parseInt(this.inputColorB.getText ());
         return (int)Long.parseLong(String.format("%02X%02X%02X%02X", alpha, red, green, blue), 16);
      } catch (Exception var5) {
         return 0;
      }
   }

   public float getScale() {
      try {
         return Float.parseFloat(this.inputScale.getText ());
      } catch (Exception var2) {
         return 1.0F;
      }
   }

   public boolean isNumeric() {
      boolean a = this.inputColorA.getText ().matches("-?\\d+(\\.\\d+)?");
      boolean r = this.inputColorR.getText ().matches("-?\\d+(\\.\\d+)?");
      boolean g = this.inputColorG.getText ().matches("-?\\d+(\\.\\d+)?");
      boolean b = this.inputColorB.getText ().matches("-?\\d+(\\.\\d+)?");
      boolean s = this.inputScale.getText ().matches("-?\\d+(\\.\\d+)?");
      return a && r && g && b && s;
   }
}
