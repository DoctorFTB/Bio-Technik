package ftblag.biotechnik.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ftblag.biotechnik.BioTechnik;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRFOrb extends EntityRenderer<EntityRFOrb> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BioTechnik.MODID, "textures/entity/particles.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(TEXTURE);

    public RenderRFOrb(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    protected int getBlockLightLevel(EntityRFOrb orb, BlockPos pos) {
        return Mth.clamp(super.getBlockLightLevel(orb, pos) + 7, 0, 15);
    }

    @Override
    public void render(EntityRFOrb orb, float p_225623_2_, float p_225623_3_, PoseStack stack, MultiBufferSource buffer, int lightmapUV) {
        stack.pushPose();
        int i = (int) (System.nanoTime() / 25000000L % 16L);

        float f2 = i / 16.0F;
        float f3 = (i + 1) / 16.0F;
        float f4 = 0.5F;
        float f5 = 0.5625F;

        stack.translate(0.0D, 0.1F, 0.0D);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        float f11 = 0.1F + 0.3F * ((float) (orb.orbMaxAge - orb.orbAge) / (float) orb.orbMaxAge);
        stack.scale(f11, f11, f11);
        VertexConsumer builder = buffer.getBuffer(RENDER_TYPE);
        PoseStack.Pose pose = stack.last();
        vertex(builder, pose, -0.5F, -0.25F, 255, 0, 0, f2, f5, lightmapUV);
        vertex(builder, pose, 0.5F, -0.25F, 255, 0, 0, f3, f5, lightmapUV);
        vertex(builder, pose, 0.5F, 0.75F, 255, 0, 0, f3, f4, lightmapUV);
        vertex(builder, pose, -0.5F, 0.75F, 255, 0, 0, f2, f4, lightmapUV);
        stack.popPose();
        super.render(orb, p_225623_2_, p_225623_3_, stack, buffer, lightmapUV);
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, int r, int g, int b, float u, float v, int lightmapUV) {
        builder
                .vertex(pose, x, y, 0.0F)
                .color(r, g, b, 128)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(lightmapUV)
                .normal(pose, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRFOrb entityRFOrb) {
        return TEXTURE;
    }
}
