package mcjty.rftoolsdim.modules.dimlets.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class DimletCycleRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DigitCycleRecipe> {

    private final ShapedRecipe.Serializer serializer = new ShapedRecipe.Serializer();

    @Override
    public DigitCycleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ShapedRecipe recipe = serializer.fromJson(recipeId, json);
        String inputString = json.getAsJsonPrimitive("input").getAsString();
        String outputString = json.getAsJsonPrimitive("output").getAsString();
        return new DigitCycleRecipe(recipe, inputString, outputString);
    }

    @Override
    public DigitCycleRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
        ShapedRecipe recipe = serializer.fromNetwork(recipeId, buffer);
        String inputString = buffer.readUtf(32767);
        String outputString = buffer.readUtf(32767);
        return new DigitCycleRecipe(recipe, inputString, outputString);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, DigitCycleRecipe recipe) {
        serializer.toNetwork(buffer, recipe.getRecipe());
        buffer.writeUtf(recipe.getInput());
        buffer.writeUtf(recipe.getOutput());
    }
}
