package corsaka.improvedf3;

import net.fabricmc.api.ModInitializer;
import java.util.logging.Logger;

public class ImprovedF3 implements ModInitializer {
  public static final String MOD_ID = "improvedf3";
  public static final Logger LOGGER = Logger.getLogger(MOD_ID);
  @Override
  public void onInitialize() {
    LOGGER.info("ImprovedF3 initialized!");
  }
}
