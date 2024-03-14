package corsaka.improvedf3.mixin;

import com.badlogic.gdx.Gdx;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.world.BlockSelection;
import finalforeach.cosmicreach.world.World;
import finalforeach.cosmicreach.world.blocks.BlockState;
import finalforeach.cosmicreach.world.chunks.Chunk;
import finalforeach.cosmicreach.world.entities.Entity;
import finalforeach.cosmicreach.world.entities.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.text.DecimalFormat;

@Mixin(UI.class)
public class UIMixin {
  @ModifyVariable(method = "drawDebugText()V", at = @At("STORE"), ordinal = 0)
  private String adjustFpsTxt(String fpsTxt) {
    return "¦fFPS: ¦e" + Gdx.graphics.getFramesPerSecond();
  }

  @ModifyVariable(method = "drawDebugText()V", at = @At("STORE"), ordinal = 1)
  private String adjustPosStr(String posStr) {
    Entity playerEntity = InGame.getLocalPlayer().getEntity();
    double posX = playerEntity.position.x;
    double posY = playerEntity.position.y;
    double posZ = playerEntity.position.z;
    DecimalFormat posFormat = new DecimalFormat("0.000");
    String pos = "¦cX¦aY¦bZ¦f: ¦c" + posFormat.format(posX) + "¦f, ¦a" + posFormat.format(posY) + "¦f, ¦b" + posFormat.format(posZ) + "¦f";

    double pitch = Math.asin(playerEntity.viewDirection.y) / Math.PI * 180;
    double yaw = -Math.atan2(playerEntity.viewDirection.x, playerEntity.viewDirection.z) / Math.PI * 180;
    String dir = getDir(yaw, pitch);

    return pos + "\n" + dir;
  }

  @Unique
  private static String getDir(double yaw, double pitch) {
    String dirPN = (135 > yaw && yaw > -45) ? "+" : "-";
    String dirXZ = (45 < yaw && yaw < 135) || (-45 > yaw && yaw > -135) ? "X" : "Z";
    String dirCompass;
    if(yaw < -135 || yaw > 135) { dirCompass = "west";
    } else if(yaw < -45 ) { dirCompass = "south";
    } else if(yaw < 45) { dirCompass = "east";
    } else { dirCompass = "north"; }
    DecimalFormat angleFormat = new DecimalFormat("0.0");
    return "¦fFacing: ¦a" + dirCompass + " ¦7(" + dirPN + dirXZ + ") | ¦6Yaw¦f/¦5Pitch: ¦6" + angleFormat.format(yaw) + "¦f/¦5" + angleFormat.format(pitch);
  }

  @ModifyVariable(method = "drawDebugText()V", at = @At("STORE"), ordinal = 2)
  private String adjustChunkStr(String chunkStr) {
    return "¦fChunk:¦e" + chunkStr.split(":")[1];
  }

  @ModifyVariable(method = "drawDebugText()V", at = @At("STORE"), ordinal = 3)
  private String adjustBlockStr(String blockStr) {
    String newBlockStr = "";
    BlockState bs = BlockSelection.getBlockLookingAt();
    if (bs != null) {
      newBlockStr = "¦fLooking at: ¦e" + bs.getSaveKey();
    } else {
      newBlockStr = "";
    }
    return newBlockStr;
  }

  @ModifyVariable(method = "drawDebugText()V", at = @At("STORE"), ordinal = 4)
  private String adjustLightingStr(String posStr) {
    World world = InGame.world;
    Player player = InGame.getLocalPlayer();
    Entity playerEntity = player.getEntity();
    int bx = (int)Math.floor(playerEntity.position.x);
    int by = (int)Math.floor(playerEntity.position.y);
    int bz = (int)Math.floor(playerEntity.position.z);
    Chunk chunk = world.getChunkAtBlock(bx, by, bz);

    int blockLight = world.getBlockLight(chunk, bx, by, bz);
    int lightR = (blockLight & 3840) >> 8;
    int lightG = (blockLight & 240) >> 4;
    int lightB = blockLight & 15;
    int skyLight = world.getSkyLight(chunk, bx, by, bz);
    return "¦fLighting: (¦c" + lightR + "¦f, ¦a" + lightG + "¦f, ¦b" + lightB + "¦f), Sky: ¦e" + skyLight + "¦d";
  }

  @Unique
  private static String splitAndRebuild(String inputText, String inputString) {
    String[] splitText = inputText.split(inputString);
    String returnText;
    try { returnText = splitText[0] + "¦f" + inputString + "¦e" + splitText[1]; }
    catch(java.lang.ArrayIndexOutOfBoundsException e) { returnText = inputText; }
    return returnText;
  }

  @Unique
  private boolean adjusted = false;

  @ModifyVariable(method = "drawDebugText()V", at = @At("STORE"), ordinal = 5)
  private String adjustDebugText(String debugText) {
    if(!debugText.contains("World Seed:")) { adjusted = false; return debugText; }
    if(adjusted) { return debugText; }

    debugText = splitAndRebuild(debugText,"Cosmic Reach Pre-alpha Version:");
    debugText = splitAndRebuild(debugText,"Java Heap Memory:");
    debugText = splitAndRebuild(debugText,"Native Heap Memory:");
    debugText = splitAndRebuild(debugText,"Palette size:");
    debugText = splitAndRebuild(debugText,"Regions loaded:");
    debugText = splitAndRebuild(debugText,"Chunks loaded:");
    debugText = splitAndRebuild(debugText,"World Seed:");
    adjusted = true;
    return debugText;
  }
}
