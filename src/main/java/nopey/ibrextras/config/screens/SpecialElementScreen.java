package nopey.ibrextras.config.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import nopey.ibrextras.config.Config;
import nopey.ibrextras.config.Data;
import nopey.ibrextras.objects.ElementType;
import nopey.ibrextras.objects.HudElement;

public class SpecialElementScreen extends Screen {
   private final HudScreen parent;
   private MinecraftClient client;
   public CheckboxWidget chkSpeedometer;
   public CheckboxWidget chkDpad;
   public CheckboxWidget chkPingicon;
   public CheckboxWidget chkTimer;
   public TextFieldWidget inputScale;
   public TextWidget txtScale;
   public TextWidget txtError;
   public ButtonWidget btnAdd;
   public ButtonWidget btnStop;

   protected SpecialElementScreen(HudScreen parent) {
      super(Text.translatable("nopey.ibrextras.config.elementscreen.title"));
      this.parent = parent;
      this.client = MinecraftClient.getInstance();
   }

   protected void init() {
      // Create chkSpeedometer using builder
      this.chkSpeedometer = CheckboxWidget.builder(Text.literal("Add Speedometer"), this.textRenderer).build();
      this.chkSpeedometer.setX(this.width / 2 - 100);
      this.chkSpeedometer.setY(this.height / 2 - 50);
      this.chkSpeedometer.setWidth(95);
      this.chkSpeedometer.setHeight(20);

      // Create chkDpad using builder
      this.chkDpad = CheckboxWidget.builder(Text.literal("Add D-pad"), this.textRenderer).build();
      this.chkDpad.setX(this.width / 2 + 5);
      this.chkDpad.setY(this.height / 2 - 50);
      this.chkDpad.setWidth(95);
      this.chkDpad.setHeight(20);

      // Create chkPingicon using builder
      this.chkPingicon = CheckboxWidget.builder(Text.literal("Add Pingicon"), this.textRenderer).build();
      this.chkPingicon.setX(this.width / 2 - 100);
      this.chkPingicon.setY(this.height / 2 - 25);
      this.chkPingicon.setWidth(95);
      this.chkPingicon.setHeight(20);

      // Create chkTimer using builder
      this.chkTimer = CheckboxWidget.builder(Text.literal("Add Timer"), this.textRenderer).build();
      this.chkTimer.setX(this.width / 2 + 5);
      this.chkTimer.setY(this.height / 2 - 25);
      this.chkTimer.setWidth(95);
      this.chkTimer.setHeight(20);

      this.txtError = new TextWidget(this.width / 2 - 100, this.height / 2 + 75, 200, 20, Text.empty(), this.client.textRenderer);
      this.txtError.alignCenter();
      this.txtError.setTextColor(-65536);
      this.inputScale = new TextFieldWidget(this.client.textRenderer, this.width / 2 - 55, this.height / 2 + 25, 40, 20, Text.empty());
      this.inputScale.setText("1.0");
      this.txtScale = new TextWidget(this.width / 2 - 100, this.height / 2 + 25, 40, 20, Text.translatable("Scale"), this.client.textRenderer);
      this.txtScale.alignCenter();
      this.btnAdd = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.add"), (button) -> {
         if (!this.isManyChecked()) {
            HudElement hud = this.getTickedElement();
            Data.hudElements.add(hud);
            this.parent.addChild(hud);
            this.close();
            Config.save();
         }

      }).dimensions(this.width / 2 - 10, this.height / 2 + 25, 110, 20).build();
      this.btnStop = ButtonWidget.builder(Text.translatable("nopey.ibrextras.button.stop"), (button) -> {
         this.close();
      }).dimensions(this.width / 2 - 100, this.height / 2 + 50, 200, 20).build();
      this.addDrawableChild(this.chkSpeedometer);
      this.addDrawableChild(this.chkDpad);
      this.addDrawableChild(this.chkPingicon);
      this.addDrawableChild(this.chkTimer);
      this.addDrawableChild(this.txtError);
      this.addDrawableChild(this.inputScale);
      this.addDrawableChild(this.txtScale);
      this.addDrawableChild(this.btnAdd);
      this.addDrawableChild(this.btnStop);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float delta) {
      this.renderInGameBackground(context);
      super.render(context, mouseX, mouseY, delta);
      context.drawCenteredTextWithShadow (this.client.textRenderer, this.title, this.width / 2, 20, -1);
      if (this.isManyChecked()) {
         this.txtError.setMessage (Text.translatable("Please check exactly one HUD Element!"));
      } else if (!this.inputScale.getText().matches("-?\\d+(\\.\\d+)?")) {
         this.txtError.setMessage (Text.translatable("Scale has to be a number!"));
      } else {
         this.txtError.setMessage (Text.empty());
      }

   }

   public void close() {
      this.client.setScreen(this.parent);
   }

   public float getScale() {
      try {
         return Float.parseFloat(this.inputScale.getText ());
      } catch (Exception var2) {
         return 1.0F;
      }
   }

   public HudElement getTickedElement() {
      if (this.chkSpeedometer.isChecked ()) {
         return new HudElement(50, 50, 145, 145, this.getScale(), ElementType.SPEEDOMETER);
      } else if (this.chkDpad.isChecked ()) {
         return new HudElement(50, 50, 50, 33, this.getScale(), ElementType.DPAD);
      } else if (this.chkPingicon.isChecked ()) {
         return new HudElement(50, 50, 10, 8, this.getScale(), ElementType.PINGICON);
      } else {
         return this.chkTimer.isChecked () ? new HudElement(50, 50, 40, 12, this.getScale(), ElementType.TIMER) : null;
      }
   }

   public boolean isManyChecked() {
      boolean s = this.chkSpeedometer.isChecked ();
      boolean d = this.chkDpad.isChecked ();
      boolean p = this.chkPingicon.isChecked ();
      boolean t = this.chkTimer.isChecked ();
      int result = (s ? 1 : 0) + (d ? 1 : 0) + (p ? 1 : 0) + (t ? 1 : 0);
      return result != 1;
   }
}
