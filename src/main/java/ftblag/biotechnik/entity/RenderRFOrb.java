package ftblag.biotechnik.entity;

import com.mojang.blaze3d.platform.GLX;
import ftblag.biotechnik.BioTechnik;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderRFOrb extends EntityRenderer<EntityRFOrb> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BioTechnik.MODID, "textures/entity/particles.png");

    public RenderRFOrb(EntityRendererManager manager) {
        super(manager);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
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
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k / 1.0F, l / 1.0F);
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)k, (float)l);
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

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityRFOrb entityRFOrb) {
        return TEXTURE;
    }
}
