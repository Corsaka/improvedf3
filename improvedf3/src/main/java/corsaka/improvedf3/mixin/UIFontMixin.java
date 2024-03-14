package corsaka.improvedf3.mixin;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import finalforeach.cosmicreach.ui.FontRenderer;
import finalforeach.cosmicreach.ui.UI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Dictionary;
import java.util.Hashtable;

@Mixin(UI.class)
public class UIFontMixin {
  @Unique
  private final Dictionary<String,float[]> colorDict = new Hashtable<>();
  private float x;
  private float y;

  @Unique
  private void initColorDict() {
    colorDict.put("0",new float[]{0,0,0}); colorDict.put("1",new float[]{0,0,0.667f}); colorDict.put("2",new float[]{0,0.667f,0}); colorDict.put("3",new float[]{0,0.667f,0.667f}); //black, darkblue, darkgreen, darkaqua
    colorDict.put("4",new float[]{0.667f,0,0}); colorDict.put("5",new float[]{0.667f,0,0.667f}); colorDict.put("6",new float[]{1,0.667f,0}); colorDict.put("7",new float[]{0.667f,0.667f,0.667f}); //red, purple, gold, grey
    colorDict.put("8",new float[]{0.333f,0.333f,0.333f}); colorDict.put("9",new float[]{0.333f,0.333f,1}); colorDict.put("a",new float[]{0.333f,1,0.333f}); colorDict.put("b",new float[]{0.333f,1,1}); //darkgrey, blue, green, aqua
    colorDict.put("c",new float[]{1,0.333f,0.333f}); colorDict.put("d",new float[]{1,0.333f,1}); colorDict.put("e",new float[]{1,1,0.333f}); colorDict.put("f",new float[]{1,1,1}); //red, pink, yellow, white
  }

  @Unique
  private void drawTheText(SpriteBatch batch, String word, float xStart, float[] color) {
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      TextureRegion texReg = FontRenderer.fontTextureRegions[c];
      x -= FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth();
      switch (c) {
        case '\n':
          y += (float) texReg.getRegionHeight();
          x = xStart;
          break;
        case ' ':
          x += FontRenderer.fontCharSizes[c].x / 4.0F;
          break;
        default:
          batch.setColor(color[0], color[1], color[2], 1);
          batch.draw(texReg, x, y);
          x += FontRenderer.fontCharSizes[c].x + FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth() + 2.0F;
      }
    }
  }

  @Redirect(method = "drawDebugText()V", at = @At(value = "INVOKE",
                  target = "Lfinalforeach/cosmicreach/ui/FontRenderer;drawText(Lcom/badlogic/gdx/graphics/g2d/SpriteBatch;Lcom/badlogic/gdx/utils/viewport/Viewport;Ljava/lang/String;FF)V"))
  public void drawColoredText(SpriteBatch batch, Viewport uiViewport, String text, float xStart, float yStart) {
    initColorDict();
    x = xStart;
    y = yStart;
    float[] color = colorDict.get("f");;

    if(text.indexOf('¦') == -1) {
      drawTheText(batch, text, xStart, color);
    } else {
      String[] splitText = text.split("¦");
      boolean first = true;
      for(String word : splitText) {
        if(first) {
          first = false;
        } else {
          color = colorDict.get(word.substring(0,1));;
          word = word.substring(1);
        }
        drawTheText(batch, word, xStart, color);
      }
    }
  }
}
