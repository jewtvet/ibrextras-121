package nopey.ibrextras.objects;

public enum ElementType {
   TEXT,
   SPEEDOMETER,
   DPAD,
   PINGICON,
   TIMER;

   // $FF: synthetic method
   private static ElementType[] $values() {
      return new ElementType[]{TEXT, SPEEDOMETER, DPAD, PINGICON, TIMER};
   }
}
