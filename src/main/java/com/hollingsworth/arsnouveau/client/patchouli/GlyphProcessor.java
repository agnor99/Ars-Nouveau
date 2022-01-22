package com.hollingsworth.arsnouveau.client.patchouli;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.common.crafting.recipes.GlyphRecipe;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class GlyphProcessor implements IComponentProcessor {

    GlyphRecipe recipe;
    @Override
    public void setup(IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe").asString();
        try {
            recipe = (GlyphRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
        }catch (Exception e){}
    }

    @Override
    public IVariable process(String s) {
        if(recipe == null)
            return null;

        if(s.equals("tier"))
            return IVariable.wrap(new TranslatableComponent("ars_nouveau.tier").getString() + ": " + new TranslatableComponent("ars_nouveau.spell_tier." + recipe.getSpellPart().getTier().value).getString());
        if(s.equals("schools")) {
            AbstractSpellPart part = ((Glyph) recipe.output.getItem()).spellPart;
            StringBuilder str = new StringBuilder(new TranslatableComponent("ars_nouveau.spell_schools").getString() +": ");
            for(SpellSchool spellSchool : part.getSchools()){
                str.append(spellSchool.getTextComponent().getString()).append(",");
            }
            if(!part.getSchools().isEmpty())
                str = new StringBuilder(str.substring(0, str.length() - 1));
            return IVariable.wrap(str.toString());
        }
        if(s.equals("mana_cost") ){
            if(recipe.output.getItem() instanceof Glyph){
                int cost =  ((Glyph) recipe.output.getItem()).spellPart.getConfigCost();
                String costLang = "";
                if(cost == 0)
                    costLang = new TranslatableComponent("ars_nouveau.mana_cost.none").getString();
                if(cost < 20)
                    costLang = new TranslatableComponent("ars_nouveau.mana_cost.low").getString();
                if(cost < 50)
                    costLang = new TranslatableComponent("ars_nouveau.mana_cost.medium").getString();
                if(cost >= 50)
                    costLang = new TranslatableComponent("ars_nouveau.mana_cost.high").getString();
                return IVariable.wrap(new TranslatableComponent("ars_nouveau.casting_cost").getString() + ": " + costLang);
            }
            return IVariable.wrap("");
        }
        return null;
    }
}
