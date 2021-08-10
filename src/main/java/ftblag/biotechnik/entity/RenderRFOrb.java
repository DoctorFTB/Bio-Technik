package ftblag.biotechnik.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ftblag.biotechnik.BioTechnik;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRFOrb extends EntityRenderer<EntityRFOrb> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(BioTechnik.MODID, "textures/entity/particles.png");
    private static final RenderType RENDER_TYPE = RenderType.itemEntityTranslucentCull(TEXTURE);

    public RenderRFOrb(EntityRendererManager manager) {
        super(manager);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    protected int getBlockLightLevel(EntityRFOrb orb, BlockPos pos) {
        return MathHelper.clamp(super.getBlockLightLevel(orb, pos) + 7, 0, 15);
    }

    @Override
    public void render(EntityRFOrb orb, float p_225623_2_, float p_225623_3_, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_) {
        stack.pushPose();
        int i = (int) (System.nanoTime() / 25000000L % 16L);

        float f2 = i / 16.0F;
        float f3 = (i + 1) / 16.0F;
        float f4 = 0.5F;
        float f5 = 0.5625F;

        stack.translate(0.0D, 0.1F, 0.0D);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        float f11 = 0.1F + 0.3F * ((float) (orb.orbMaxAge - orb.orbAge) / (float) orb.orbMaxAge);
        stack.scale(f11, f11, f11);
        IVertexBuilder ivertexbuilder = buffer.getBuffer(RENDER_TYPE);
        MatrixStack.Entry matrixstack$entry = stack.last();
        Matrix4f matrix4f = matrixstack$entry.pose();
        Matrix3f matrix3f = matrixstack$entry.normal();
        vertex(ivertexbuilder, matrix4f, matrix3f, -0.5F, -0.25F, 255, 0, 0, f2, f5, p_225623_6_);
        vertex(ivertexbuilder, matrix4f, matrix3f, 0.5F, -0.25F, 255, 0, 0, f3, f5, p_225623_6_);
        vertex(ivertexbuilder, matrix4f, matrix3f, 0.5F, 0.75F, 255, 0, 0, f3, f4, p_225623_6_);
        vertex(ivertexbuilder, matrix4f, matrix3f, -0.5F, 0.75F, 255, 0, 0, f2, f4, p_225623_6_);
        stack.popPose();
        super.render(orb, p_225623_2_, p_225623_3_, stack, buffer, p_225623_6_);
    }

    private static void vertex(IVertexBuilder builder, Matrix4f matrix4f, Matrix3f matrix3f, float x, float y, int r, int g, int b, float u, float v, int uv2) {
        builder
                .vertex(matrix4f, x, y, 0.0F)
                .color(r, g, b, 128)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(uv2)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityRFOrb entityRFOrb) {
        return TEXTURE;
    }
}
