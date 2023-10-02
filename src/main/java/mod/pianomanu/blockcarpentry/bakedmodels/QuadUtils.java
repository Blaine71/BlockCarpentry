package mod.pianomanu.blockcarpentry.bakedmodels;

import mod.pianomanu.blockcarpentry.util.TextureHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * Contains helper methods to ease the quad population process
 * See {@link mod.pianomanu.blockcarpentry.util.ModelHelper} for more information
 *
 * @author PianoManu
 * @version 1.1 09/21/23
 */
public class QuadUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    public static TextureAtlasSprite getTexture(BakedModel model, @Nonnull Random rand, @Nonnull IModelData extraData, ModelProperty<Integer> modelPropertyTexture) {

        List<TextureAtlasSprite> textureList = TextureHelper.getTextureFromModel(model, extraData, rand);
        TextureAtlasSprite texture;
        Integer tex = extraData.getData(modelPropertyTexture);
        if (tex == null) {
            LOGGER.error("Cannot determine model texture for model " + model);
        }
        if (textureList.size() <= tex) {
            extraData.setData(modelPropertyTexture, 0);
            tex = 0;
        }
        if (textureList.size() == 0) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(new TranslatableComponent("message.blockcarpentry.block_not_available"), true);
            }
            for (int i = 0; i < 6; i++) {
                textureList.add(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(new ResourceLocation("missing")));
            }
        }
        texture = textureList.get(tex);
        return texture;
    }
}
//========SOLI DEO GLORIA========//