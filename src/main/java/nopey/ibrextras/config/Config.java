package nopey.ibrextras.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import nopey.ibrextras.objects.ElementType;
import nopey.ibrextras.objects.HudElement;

public class Config {
   private static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "ibrextras.cfg");

   public static void load() {
      ElementType type = ElementType.TEXT;
      boolean enabled = true;
      Text text = Text.empty();
      int color = -1;
      int x = 0;
      int y = 0;
      int width = 0;
      int height = 0;
      float scale = 1.0F;

      try {
         BufferedReader br = new BufferedReader(new FileReader(file));
         String s = br.readLine();

         do {
            if (s.matches("^\\d:$")) {
               type = ElementType.values()[Integer.parseInt(s.substring(0, 1))];
               s = br.readLine();
               if (s.startsWith("enabled=")) {
                  enabled = Boolean.parseBoolean(s.split("=")[1]);
               }

               s = br.readLine();
               if (s.startsWith("text=")) {
                  text = Text.literal(s.split("=")[1]);
               }

               s = br.readLine();
               if (s.startsWith("color")) {
                  color = (int)Long.parseLong(s.split("=")[1], 16);
               }

               s = br.readLine();
               if (s.startsWith("x=")) {
                  x = Integer.parseInt(s.split("=")[1]);
               }

               s = br.readLine();
               if (s.startsWith("y=")) {
                  y = Integer.parseInt(s.split("=")[1]);
               }

               s = br.readLine();
               if (s.startsWith("width=")) {
                  width = Integer.parseInt(s.split("=")[1]);
               }

               s = br.readLine();
               if (s.startsWith("height=")) {
                  height = Integer.parseInt(s.split("=")[1]);
               }

               s = br.readLine();
               if (s.startsWith("scale=")) {
                  scale = Float.parseFloat(s.split("=")[1]);
               }

               s = br.readLine();
            }

            HudElement hud;
            if (type == ElementType.TEXT) {
               hud = new HudElement(text, x, y, color, scale);
               hud.enabled = enabled;
               Data.hudElements.add(hud);
            } else {
               hud = new HudElement(x, y, width, height, scale, type);
               hud.enabled = enabled;
               Data.hudElements.add(hud);
            }
         } while(s != null);
      } catch (Exception var12) {
         System.out.println(var12.getLocalizedMessage());
      }

   }

   public static void save() {
      try {
         FileWriter wr = new FileWriter(file);

         for(int i = 0; i < Data.hudElements.size(); ++i) {
            HudElement hud = (HudElement) Data.hudElements.get(i);
            wr.write(hud.type.ordinal() + ":\n");
            wr.write("enabled=" + hud.enabled + "\n");
            wr.write("text=" + hud.getMessage().getString() + "\n");
            wr.write("color=" + Integer.toHexString(hud.color) + "\n");
            wr.write("x=" + hud.getX() + "\n");
            wr.write("y=" + hud.getY() + "\n");
            wr.write("width=" + hud.initialWidth + "\n");
            wr.write("height=" + hud.initialHeight + "\n");
            wr.write("scale=" + hud.scale + "\n");
         }

         wr.close();
      } catch (Exception var3) {
         System.out.println(var3.getLocalizedMessage());
      }

   }
}
