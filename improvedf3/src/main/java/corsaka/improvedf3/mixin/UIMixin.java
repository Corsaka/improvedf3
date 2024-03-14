package corsaka.improvedf3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.text.DecimalFormat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.Viewport;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.ui.FontRenderer;
import finalforeach.cosmicreach.RuntimeInfo;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.world.World;
import finalforeach.cosmicreach.world.blocks.BlockState;
import finalforeach.cosmicreach.world.chunks.Chunk;
import finalforeach.cosmicreach.world.chunks.Region;
import finalforeach.cosmicreach.world.chunks.chunkdata.IBlockData;
import finalforeach.cosmicreach.world.chunks.chunkdata.LayeredBlockData;
import finalforeach.cosmicreach.world.entities.Entity;
import finalforeach.cosmicreach.world.entities.Player;

import static finalforeach.cosmicreach.ui.UI.batch;

@Mixin(UI.class)
interface ViewportAccessor {
  @Accessor
  Viewport getUiViewport();
}

@Mixin(UI.class)
public class UIMixin {
  @Unique
  private String improvedF3$dirCompass(double yaw) {
    if(yaw < -135 || yaw > 135) { return "west";
    } else if(yaw < -45 ) { return "south";
    } else if(yaw < 45) { return "east";
    } else { return "north"; }
  }

  /**
   * @author Corsaka
   * @reason Adding colour and precision to the debug text
   */
  @Overwrite
  private void drawDebugText() {
    double fps = (1.0F / Gdx.graphics.getDeltaTime());
    String fpsStr = "%fFPS: %e" + (float)((int)(fps * 100.0)) / 100.0F; //fps
    World world = InGame.world;
    Player player = InGame.getLocalPlayer();
    Entity playerEntity = player.getEntity();
    int bx = (int)Math.floor(playerEntity.position.x);
    int by = (int)Math.floor(playerEntity.position.y);
    int bz = (int)Math.floor(playerEntity.position.z);
    Chunk chunk = world.getChunkAtBlock(bx, by, bz);
    BlockState bs = world.getBlockState(chunk, bx, by, bz);
    DecimalFormat coordFormat = new DecimalFormat("0.000");
    DecimalFormat angleFormat = new DecimalFormat("0.0");
    String posStr = "%cX%aY%bZ%7: %c" + coordFormat.format(playerEntity.position.x) + "%7, %a" + coordFormat.format(playerEntity.position.y) + "%7, %b" + coordFormat.format(playerEntity.position.z);
    double pitch = Math.asin(playerEntity.viewDirection.y) / Math.PI * 180;
    double yaw = -Math.atan2(playerEntity.viewDirection.x, playerEntity.viewDirection.z) / Math.PI * 180;
    String dirPN = (135 > yaw && yaw > -45) ? "+" : "-";
    String dirXZ = (45 < yaw && yaw < 135) || (-45 > yaw && yaw > -135) ? "X" : "Z";
    String dirStr = "%fFacing: %a" + improvedF3$dirCompass(yaw) + " %7(" + dirPN + dirXZ + ") | %6Yaw%f/%5Pitch: %6" + angleFormat.format(yaw) + "%f/%5" + angleFormat.format(pitch);
    String velStr = "%fVelocity: %c" + coordFormat.format(playerEntity.velocity.x) +"%f, %a"+ coordFormat.format(playerEntity.velocity.y) +"%f, %b"+ coordFormat.format(playerEntity.velocity.z);
    String chunkStr = "%fChunk: %e" + chunk;
    String blockStr = "";
    if (bs != null) {
      blockStr = blockStr + "%fBlock: %e" + bs.getBlock().getStringId();
    }

    int blockLight = world.getBlockLight(chunk, bx, by, bz);
    int lightR = (blockLight & 3840) >> 8;
    int lightG = (blockLight & 240) >> 4;
    int lightB = blockLight & 15;
    int skyLight = world.getSkyLight(chunk, bx, by, bz);
    String lightingStr = "%fLighting: (%c" + lightR + "%f, %a" + lightG + "%f, %b" + lightB + "%f), Sky: %e" + skyLight;
    String debugText = "%fCosmic Reach Pre-alpha Version: %e" + RuntimeInfo.version + "\n";
    debugText = debugText + fpsStr + "\n";
    debugText = debugText + "%fJava Heap Memory: %e" + RuntimeInfo.getJavaHeapUseStr() + "\n";
    debugText = debugText + "%fNative Heap Memory: %e" + RuntimeInfo.getNativeHeapUseStr() + "\n";
    debugText = debugText + posStr + "\n" + dirStr + "\n" + velStr + "\n" + chunkStr + "\n" + blockStr + "\n" + lightingStr;
    if (chunk != null && chunk.blockData != null) {
      IBlockData<BlockState> var23 = chunk.blockData;
      if (var23 instanceof LayeredBlockData l) {
        debugText = debugText + "\n%f" + l.getLayer(by - chunk.blockY).getClass().getSimpleName();
      }
    }

    if (chunk != null) {
      IBlockData<BlockState> var33 = chunk.blockData;
      if (var33 instanceof LayeredBlockData layered) {
        debugText = debugText + "\n%fPalette size: %e" + layered.blockStatePalette.size;
        debugText = debugText + "\n%fPaletteIDs size: %e" + layered.blockStatePaletteIds.size;
      }
    }

    int numChunks = 0;

    for(Region r : world.regions.values()) {
      numChunks += r.getNumberOfChunks();
    }

    debugText = debugText + "\n%fRegions loaded: %e" + world.regions.size();
    debugText = debugText + "\n%fChunks loaded: %e" + numChunks;
    if (world.defaultZone != null && world.defaultZone.worldGenerator != null) {
      debugText = debugText + "\n%fWorld Seed: %e" + world.defaultZone.worldGenerator.seed + "%f";
    }

    FontRenderer.fontTexture.bind(0);
    FontRenderer.drawText(batch, ((ViewportAccessor)this).getUiViewport(), debugText, -((ViewportAccessor)this).getUiViewport().getWorldWidth() / 2.0F, -((ViewportAccessor)this).getUiViewport().getWorldHeight() / 2.0F);
  }
}

