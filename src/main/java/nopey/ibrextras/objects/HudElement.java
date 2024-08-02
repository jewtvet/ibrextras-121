package nopey.ibrextras.objects;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nopey.ibrextras.config.Config;
import nopey.ibrextras.config.Data;
import nopey.ibrextras.config.screens.HudScreen;
import nopey.ibrextras.mixin.InGameHudMixin;
import org.joml.Matrix4f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HudElement extends ClickableWidget {
   public static final Identifier WIDGETS_TEXTURE = Identifier.tryParse("ibrextras", "textures/widgets.png");
   public static final Identifier SPEED_GAUGE_TEXTURE = Identifier.tryParse("ibrextras", "textures/speed_gauge.png");
   public static final Identifier SPEED_GAUGE_BG_TEXTURE = Identifier.tryParse("ibrextras", "textures/speed_gauge_bg.png");
   public static final Identifier SPEED_NEEDLE_TEXTURE = Identifier.tryParse("ibrextras", "textures/speed_needle.png");
   private boolean timerMatch;
   public int color;
   public final float scale;
   public final ElementType type;
   public int initialWidth;
   public int initialHeight;
   public MinecraftClient client = MinecraftClient.getInstance();
   public int lastMouseX;
   public int lastMouseY;
   public int lastX;
   public int lastY;
   public boolean enabled;
   public boolean isDragging;
   public ButtonWidget btnDelete;
   public KeyBinding keyBind;

   public HudElement(Text message, int x, int y, int color, float scale) {
      super(x, y, 0, (int)(8.0F * scale), message);
      this.setWidth((int)((float)this.client.textRenderer.getWidth(this.getFilteredText()) * scale));
      this.enabled = true;
      this.color = color;
      this.scale = scale;
      this.type = ElementType.TEXT;
      this.initButton();
   }

   public HudElement(int x, int y, int width, int height, float scale, ElementType type) {
      super(x, y, (int)((float)width * scale), (int)((float)height * scale), Text.literal("NONE"));
      this.enabled = true;
      this.color = -1;
      this.scale = scale;
      this.type = type;
      this.initialWidth = width;
      this.initialHeight = height;
      this.initButton();
   }

   @Override
   public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
      int w = this.client.getWindow().getScaledWidth() / 2;
      int h = this.client.getWindow().getScaledHeight();
      if (this.client.currentScreen instanceof HudScreen) {
         context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 587137024);
         if (this.type == ElementType.TIMER && !this.timerMatch) {
            this.setWidth(this.client.textRenderer.getWidth("00.000 -00.00"));
            context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1426063360);
            context.drawTextWithShadow(this.client.textRenderer, "00.000 -00.00", this.getX() + 2, this.getY() + 2, -1);
         }

         if (this.isDragging) {
            context.drawTextWithShadow(this.client.textRenderer, String.valueOf(this.getX()), 0, this.getY() + 1, -1);
            context.drawTextWithShadow(this.client.textRenderer, String.valueOf(this.getY()), w + 1, h - 8, -1);
            context.drawVerticalLine(w, h, this.getY(), 1157562368);
            context.drawHorizontalLine(0, w * 2, this.getY(), 1157562368);
         }
      }
   }

   public void render(DrawContext context, float tickDelta) {
      this.btnDelete.setPosition(this.getX() + this.getWidth() - 4, this.getY() - 4);
      if (this.enabled) {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         if (this.type == ElementType.SPEEDOMETER) {
            this.startRotationAndScaling(context, 0.0F);
            if (!Data.speedometerBackground) {
               context.drawTexture(SPEED_GAUGE_TEXTURE, this.getX(), this.getY(), 0, 0, 145, 145);
            } else {
               context.drawTexture(SPEED_GAUGE_BG_TEXTURE, this.getX(), this.getY(), 0, 0, 145, 145);
            }

            this.stopScale(context);
            this.startRotationAndScaling(context, 45.0F + (float)Data.displayedSpeedVal * 3.1F);
            context.drawTexture(SPEED_NEEDLE_TEXTURE, this.getX(), this.getY(), 0, 0, 145, 145);
            this.stopScale(context);
         }

         if (this.type == ElementType.DPAD) {
            this.startScale(context);
            context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY() + 17, 0, this.client.options.leftKey.isPressed() ? 46 : 30, 16, 16);
            context.drawTexture(WIDGETS_TEXTURE, this.getX() + 34, this.getY() + 17, 16, this.client.options.rightKey.isPressed() ? 46 : 30, 16, 16);
            context.drawTexture(WIDGETS_TEXTURE, this.getX() + 17, this.getY(), 32, this.client.options.forwardKey.isPressed() ? 46 : 30, 16, 16);
            context.drawTexture(WIDGETS_TEXTURE, this.getX() + 17, this.getY() + 17, 48, this.client.options.backKey.isPressed() ? 46 : 30, 16, 16);
            this.stopScale(context);
         }

         if (this.type == ElementType.PINGICON) {
            byte offset;
            if (Data.pingVal < 0) {
               offset = 40;
            } else if (Data.pingVal < 150) {
               offset = 0;
            } else if (Data.pingVal < 300) {
               offset = 8;
            } else if (Data.pingVal < 600) {
               offset = 16;
            } else if (Data.pingVal < 1000) {
               offset = 24;
            } else {
               offset = 32;
            }

            this.startScale(context);
            context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 246, offset, 10, 8);
            this.stopScale(context);
         }

         if (this.type == ElementType.TIMER) {
            Text msg = ((InGameHudMixin) MinecraftClient.getInstance().inGameHud).getOverlayMessage();
            if (msg == null) {
               return;
            }

            String s = msg.getString();
            if (s.matches("(\\d+:)?(\\d+).(\\d+)( \\+?\\-?(\\d+:)?(\\d+).(\\d+))?")) {
               this.timerMatch = true;
               String[] split = s.split(" ");
               int timeWidth = this.client.textRenderer.getWidth(split[0]);
               this.setWidth(timeWidth + 4);
               context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1426063360);
               context.drawTextWithShadow(this.client.textRenderer, split[0], this.getX() + 2, this.getY() + 2, -1);
               if (split.length > 1) {
                  int splitWidth = this.client.textRenderer.getWidth(split[1]);
                  this.setWidth(timeWidth + splitWidth + 8);
                  context.fill(this.getX() + timeWidth + 4, this.getY(), this.getX() + timeWidth + splitWidth + 8, this.getY() + this.getHeight(), split[1].startsWith("+") ? 1442775040 : 1426128640);
                  context.drawTextWithShadow(this.client.textRenderer, split[1], this.getX() + timeWidth + 6, this.getY() + 2, -1);
               }

               ((InGameHudMixin) MinecraftClient.getInstance().inGameHud).setOverlayRemaining(0);
            }

            this.timerMatch = false;
         }

         if (this.type == ElementType.TEXT) {
            this.setWidth((int)((float)this.client.textRenderer.getWidth(this.getFilteredText()) * this.scale));
            this.startScale(context);
            context.drawTextWithShadow(this.client.textRenderer, this.getFilteredText(), this.getX(), this.getY(), this.color);
            this.stopScale(context);
         }

         RenderSystem.disableBlend();
      }
   }

   public void onClick(double mouseX, double mouseY) {
      this.lastMouseX = (int)mouseX;
      this.lastMouseY = (int)mouseY;
      this.lastX = this.getX();
      this.lastY = this.getY();
      this.isDragging = true;
   }

   public void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
      int distanceX = (int)mouseX - this.lastMouseX;
      int distanceY = (int)mouseY - this.lastMouseY;
      this.setX(this.lastX + distanceX);
      this.setY(this.lastY + distanceY);
      super.onDrag(mouseX, mouseY, deltaX, deltaY);
   }

   public void onRelease(double mouseX, double mouseY) {
      this.isDragging = false;
   }

   public void startScale(DrawContext context) {
      context.getMatrices().push();
      Matrix4f mx4f = context.getMatrices().peek().getPositionMatrix();
      mx4f.scaleAround((float)Math.sqrt((double)this.scale), (float)this.getX(), (float)this.getY(), 0.0F);
      context.getMatrices().multiplyPositionMatrix(mx4f);
   }

   public void startRotationAndScaling(DrawContext context, float angle) {
      float anchorX = (float)this.getX() + (float)this.initialWidth / 2.0F;
      float anchorY = (float)this.getY() + (float)this.initialHeight / 2.0F;
      context.getMatrices().push();
      Matrix4f mx4f = context.getMatrices().peek().getPositionMatrix();
      mx4f.scaleAround((float)Math.sqrt((double)this.scale), anchorX, anchorY, 0.0F);
      mx4f.rotateAround(RotationAxis.POSITIVE_Z.rotationDegrees(angle / 2.0F), anchorX, anchorY, 0.0F);
      context.getMatrices().multiplyPositionMatrix(mx4f);
   }

   public void stopScale(DrawContext context) {
      context.getMatrices().pop();
   }

   public Text getFilteredText() {
      return Text.literal(this.getMessage().getString().replace("$name", this.client.player != null ? this.client.player.getName().getString() : "ERROR").replace("$speed:m/s", String.format("%03.0f", Data.displayedSpeedVal)).replace("$speed:km/h", String.format("%03.0f", Data.displayedSpeedVal * 3.6D)).replace("$speed:mph", String.format("%03.0f", Data.displayedSpeedVal * 2.236936D)).replace("$speed:kt", String.format("%03.0f", Data.displayedSpeedVal * 1.943844D)).replace("$speed", String.format("%03.0f", Data.displayedSpeedVal * 3.6D)).replace("$ping", String.format("%d", Data.pingVal)).replace("$angle", String.format("%03.0f", Data.angleVal)).replace("$g", String.format("%+.1f", Data.gVal)).replace("$fps", String.format("%d", Data.fpsVal)).replace("$inertia", String.format("%03.0f", Math.abs(Data.displayedInertiaVal))));
   }

   public void initButton() {
      this.btnDelete = ButtonWidget.builder(Text.translatable("x"), (button) -> {
         Data.hudElements.remove(this);
         this.client.setScreen(this.client.currentScreen);
         Config.save();
      }).dimensions(this.getX() + this.getWidth() - 4, this.getY() - 4, 10, 10).build();
   }

   public void initKeybind() {
      this.keyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(this.getMessage().getString(), InputUtil.Type.KEYSYM, 0, "nopey.ibrextras.category"));
   }

   protected void appendClickableNarrations(NarrationMessageBuilder builder) {
   }
}
