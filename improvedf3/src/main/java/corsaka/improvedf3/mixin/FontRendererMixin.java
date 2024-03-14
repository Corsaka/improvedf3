package corsaka.improvedf3.mixin;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import finalforeach.cosmicreach.ui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Dictionary;
import java.util.Hashtable;

@Mixin(FontRenderer.class)
public class FontRendererMixin {

  /**
   * @author Corsaka
   * @reason To add colour interpretation
   */
  @Overwrite
  public static void drawText(SpriteBatch batch, Viewport uiViewport, String text, float xStart, float yStart) {
    float x = xStart;
    float y = yStart;

    Dictionary<String,float[]> colorDict = new Hashtable<>();
    colorDict.put("0",new float[]{0,0,0}); colorDict.put("1",new float[]{0,0,0.667f}); colorDict.put("2",new float[]{0,0.667f,0}); colorDict.put("3",new float[]{0,0.667f,0.667f}); //black, darkblue, darkgreen, darkaqua
    colorDict.put("4",new float[]{0.667f,0,0}); colorDict.put("5",new float[]{0.667f,0,0.667f}); colorDict.put("6",new float[]{1,0.667f,0}); colorDict.put("7",new float[]{0.667f,0.667f,0.667f}); //red, purple, gold, grey
    colorDict.put("8",new float[]{0.333f,0.333f,0.333f}); colorDict.put("9",new float[]{0.333f,0.333f,1}); colorDict.put("a",new float[]{0.333f,1,0.333f}); colorDict.put("b",new float[]{0.333f,1,1}); //darkgrey, blue, green, aqua
    colorDict.put("c",new float[]{1,0.333f,0.333f}); colorDict.put("d",new float[]{1,0.333f,1}); colorDict.put("e",new float[]{1,1,0.333f}); colorDict.put("f",new float[]{1,1,1}); //red, pink, yellow, white

    if(text.indexOf('%') != -1) {
      String[] splitText = text.split("%");
      boolean first = true;
      for (String word : splitText) {
        if (first) {
          first = false;
          //actually draw the text
          for (int i = 0; i < word.length(); ++i) { //iterate over characters
            char c = word.charAt(i); //get character at location in string
            TextureRegion texReg = FontRenderer.fontTextureRegions[c]; //calculate its texture region
            x -= FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth(); //subtract x by size of letter
            switch (c) { //depending on character
              case '\n':
                y += (float) texReg.getRegionHeight(); //increase gap by whatever size the texture is
                x = xStart; //return x to start
                break;
              case ' ':
                x += FontRenderer.fontCharSizes[c].x / 4.0F; //reduce the font size by 4x for spaces
                break;
              default: //after everything else
                batch.setColor(1, 1, 1, 1); //set to white
                batch.draw(texReg, x, y); //draw that character at pos x/y
                x += FontRenderer.fontCharSizes[c].x + FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth() + 2.0F; //increase x by size of texture + 2
            }
          }
        } else if(word.substring(0,1).matches("[0-9a-f]")) {
          float[] color = colorDict.get(word.substring(0,1));
          word = word.substring(1);
          //actually draw the text
          for (int i = 0; i < word.length(); ++i) { //iterate over characters
            char c = word.charAt(i); //get character at location in string
            TextureRegion texReg = FontRenderer.fontTextureRegions[c]; //calculate its texture region
            x -= FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth(); //subtract x by size of letter
            switch (c) { //depending on character
              case '\n':
                y += (float) texReg.getRegionHeight(); //increase gap by whatever size the texture is
                x = xStart; //return x to start
                break;
              case ' ':
                x += FontRenderer.fontCharSizes[c].x / 4.0F; //reduce the font size by 4x for spaces
                break;
              default: //after everything else
                batch.setColor(color[0], color[1], color[2], 1); //set to colour
                batch.draw(texReg, x, y); //draw that character at pos x/y
                x += FontRenderer.fontCharSizes[c].x + FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth() + 2.0F; //increase x by size of texture + 2
            }
          }
        } else {
          System.out.println(word + " is not a valid colour code!");
          word = "%" + word;
          //actually draw the text
          for (int i = 0; i < word.length(); ++i) { //iterate over characters
            char c = word.charAt(i); //get character at location in string
            TextureRegion texReg = FontRenderer.fontTextureRegions[c]; //calculate its texture region
            x -= FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth(); //subtract x by size of letter
            switch (c) { //depending on character
              case '\n':
                y += (float) texReg.getRegionHeight(); //increase gap by whatever size the texture is
                x = xStart; //return x to start
                break;
              case ' ':
                x += FontRenderer.fontCharSizes[c].x / 4.0F; //reduce the font size by 4x for spaces
                break;
              default: //after everything else
                batch.setColor(1, 1, 1, 1); //set to white
                batch.draw(texReg, x, y); //draw that character at pos x/y
                x += FontRenderer.fontCharSizes[c].x + FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth() + 2.0F; //increase x by size of texture + 2
            }
          }
        }
      }
    } else {
      //actually draw the text
      for (int i = 0; i < text.length(); ++i) { //iterate over characters
        char c = text.charAt(i); //get character at location in string
        TextureRegion texReg = FontRenderer.fontTextureRegions[c]; //calculate its texture region
        x -= FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth(); //subtract x by size of letter
        switch (c) { //depending on character
          case '\n':
            y += (float) texReg.getRegionHeight(); //increase gap by whatever size the texture is
            x = xStart; //return x to start
            break;
          case ' ':
            x += FontRenderer.fontCharSizes[c].x / 4.0F; //reduce the font size by 4x for spaces
            break;
          default: //after everything else
            batch.setColor(1,1,1,1); //set to white
            batch.draw(texReg, x, y); //draw that character at pos x/y
            x += FontRenderer.fontCharSizes[c].x + FontRenderer.fontCharStartPos[c].x % (float) texReg.getRegionWidth() + 2.0F; //increase x by size of texture + 2
        }
      }
    }
  }
}
