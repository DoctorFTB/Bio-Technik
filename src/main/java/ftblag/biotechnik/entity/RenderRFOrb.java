package ftblag.biotechnik.entity;

import org.lwjgl.opengl.GL11;

import ftblag.biotechnik.BioTechnik;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FTB_lag.
 */
@SideOnly(Side.CLIENT)
public class RenderRFOrb extends Render<EntityRFOrb> {

    public RenderRFOrb() {
        super(Minecraft.getMinecraft().getRenderManager());
        shadowSize = 0.1F;
        shadowOpaque = 0.5F;
    }

    @Override
    public void doRender(EntityRFOrb orb, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        bindEntityTexture(orb);
        int i = (int) (System.nanoTime() / 25000000L % 16L);
        Tessellator tessellator = Tessellator.getInstance();
        float f2 = i / 16.0F;
        float f3 = (i + 1) / 16.0F;
        float f4 = 0.5F;
        float f5 = 0.5625F;
        int j = orb.getBrightnessForRender();
        int k = j % 65536;
        int l = j / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, l / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float f11 = 0.1F + 0.3F * ((float) (orb.orbMaxAge - orb.orbAge) / (float) orb.orbMaxAge);
        GL11.glScalef(f11, f11, f11);
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        buffer.pos(-0.5D, -0.25D, 0.0D).tex(f2, f5).color(255, 0, 0, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5D, -0.25D, 0.0D).tex(f3, f5).color(255, 0, 0, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(0.5D, 0.75D, 0.0D).tex(f3, f4).color(255, 0, 0, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(-0.5D, 0.75D, 0.0D).tex(f2, f4).color(255, 0, 0, 255).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityRFOrb entity) {
        return new ResourceLocation(BioTechnik.MODID, "textures/entity/particles.png");
    }
}