package nopey.ibrextras.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import nopey.ibrextras.config.Data;
import nopey.ibrextras.objects.HudElement;

public class HudRenderer {
   public void render(DrawContext context, float tickDelta) {
      Data.displayedSpeedVal = MathHelper.lerp((double)tickDelta, Data.displayedSpeedVal, Data.speedVal);
      Data.displayedInertiaVal = MathHelper.lerp((double)tickDelta, Data.displayedInertiaVal, Data.oldInertiaVal - Data.inertiaVal);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      if (Data.isRidingBoat && Data.enabled) {
         for(int i = 0; i < Data.hudElements.size(); ++i) {
            ((HudElement)Data.hudElements.get(i)).render(context, tickDelta);
         }
      }

      RenderSystem.disableBlend();
   }

   public void update(MinecraftClient client, BoatEntity boat) {
      Vec3d velocity = boat.getVelocity().multiply(1.0D, 0.0D, 1.0D);
      Data.oldSpeedVal = Data.speedVal;
      Data.speedVal = velocity.length() * 20.0D;
      Data.gVal = (Data.speedVal - Data.oldSpeedVal) * 2.040816327D;
      Data.angleVal = Math.toDegrees(Math.acos(velocity.dotProduct(boat.getRotationVector()) / velocity.length() * boat.getRotationVector().length()));
      if (Double.isNaN(Data.angleVal)) {
         Data.angleVal = 0.0D;
      }

      Data.pingVal = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid()).getLatency();
      Data.fpsVal = client.getCurrentFps();
      Data.oldInertiaVal = Data.inertiaVal;
      Data.inertiaVal = (double)boat.getRotationClient().length() * 20.0D;
   }
}
