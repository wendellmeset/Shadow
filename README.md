# Shadow
reuploaded shadow client source code

# Differences between this and Saturn's source code release
## Removal of the Backdoor feature
Only in Shadow-Actual/src/main/java/net/shadow/client/feature/gui: backdoor  
diff Shadow-Actual/src/main/java/net/shadow/client/feature/module/impl/grief/ControlPanel.java Shadow-SourceRelease/src/main/java/net/shadow/client/feature/module/impl/grief/ControlPanel.java  
```diff
8d7
< import net.shadow.client.feature.gui.backdoor.BackdoorScreen;
25c24
<         client.setScreen(new BackdoorScreen());
---
>         //removed!
```
## Rework on ClickTP
diff Shadow-Actual/src/main/java/net/shadow/client/feature/module/impl/movement/ClickTP.java Shadow-SourceRelease/src/main/java/net/shadow/client/feature/module/impl/movement/ClickTP.java  
```diff
16a17
> import net.shadow.client.feature.gui.notifications.Notification;
21a23
> import net.shadow.client.helper.event.events.PacketEvent;
26a29,33
> 
>     boolean rddthreadsuspender = false;
>     BlockPos originblock = new BlockPos(0,0,0);
>     Vec3d movings = new Vec3d(0,0,0);
> 
40a48,52
>     private static int lengthToN(BlockPos p, Vec3d origin) {
>         Vec3d v = new Vec3d(p.getX(), p.getY(), p.getZ());
>         return (int) roundToN(v.distanceTo(origin), 0);
>     }
> 
65a78,87
>     @EventListener(type=EventType.PACKET_SEND)
>     void onSendPacket(PacketEvent pe){
>         if(pe.getPacket() instanceof PlayerMoveC2SPacket p){
>             Vec3d origin = new Vec3d(p.getX(0), p.getY(0), p.getZ(0));
>             if(lengthToN(originblock, origin) < 3){
>                 pe.cancel();
>             }
>         }
>     }
> 
101a124
>                     originblock = client.player.getBlockPos();
122,138c145,170
<                 case Experimental -> new Thread(() -> {
<                     int rdd = lengthTo(ray.getBlockPos());
<                     BlockPos destt = new BlockPos(blockHitResult.getBlockPos());
<                     client.player.jump();
<                     ClientPlayerEntity player = client.player;
<                     Vec3d playerpos = player.getPos();
<                     double xn = destt.getX() - playerpos.x;
<                     double yn = destt.getY() - playerpos.y;
<                     double zn = destt.getZ() - playerpos.z;
<                     double x = xn / rdd;
<                     double y = yn / rdd;
<                     double z = zn / rdd;
<                     for (int i = 0; i < rdd; i++) {
<                         client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX() + x, player.getY() + y, player.getZ() + z, true));
<                         try {
<                             Thread.sleep(10);
<                         } catch (Exception ignored) {
---
>                 case Experimental -> {
>                     new Thread(() -> {
>                         originblock = client.player.getBlockPos();
>                         BlockHitResult ray3 = (BlockHitResult) client.player.raycast(200, client.getTickDelta(), true);
>                         int rd3 = lengthTo(ray3.getBlockPos());
>                         int raycastdistance3 = rd3 / 7;
>                         BlockHitResult blockHitResult3 = (BlockHitResult) client.player.raycast(200, client.getTickDelta(), true);
>                         BlockPos d3 = new BlockPos(blockHitResult3.getBlockPos());
>                         BlockPos dest3 = new BlockPos(d3.getX() + 0.5, d3.getY(), d3.getZ() + 0.5);
>                         dest3 = dest3.offset(Direction.UP, 1);
>                         ClientPlayerEntity player = client.player;
>                         Vec3d playerpos = player.getPos();
>                         double xn = dest3.getX() - playerpos.x;
>                         double yn = dest3.getY() - playerpos.y;
>                         double zn = dest3.getZ() - playerpos.z;
>                         double x = xn / raycastdistance3;
>                         double y = yn / raycastdistance3;
>                         double z = zn / raycastdistance3;
>                         for (int i = 0; i < raycastdistance3; i++) {
>                             client.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(player.getX() + x, player.getY() + y, player.getZ() + z, true));
>                             try {
>                                 Thread.sleep(100);
>                             } catch (Exception ignored) {
>                             }
>                             client.player.setVelocity(0, 0, 0);
>                             movings = new Vec3d(player.getX() + x, player.getY() + y, player.getZ() + z);
140,142c172,173
<                         client.player.setVelocity(0, 0, 0);
<                     }
<                 }).start();
---
>                     }).start();
>                 }
150a182,185
>         if(mode.getValue() == Mode.Experimental){
>             Renderer.R3D.renderFilled(new Vec3d(originblock.getX(), originblock.getY(), originblock.getZ()), new Vec3d(1, 1, 1), Utils.getCurrentRGB(), matrices);
>             Renderer.R3D.renderFilled(new Vec3d(movings.getX(), movings.getY(), movings.getZ()), new Vec3d(1, 1, 1), Utils.getCurrentRGB(), matrices);
>         }
```
