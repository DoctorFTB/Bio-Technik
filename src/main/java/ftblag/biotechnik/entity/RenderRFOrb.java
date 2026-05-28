package ftblag.biotechnik.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ftblag.biotechnik.BioTechnik;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class RenderRFOrb extends EntityRenderer<EntityRFOrb, RFOrbRenderState> {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(BioTechnik.MODID, "textures/entity/particles.png");
    private static final RenderType RENDER_TYPE = RenderTypes.entityTranslucentCullItemTarget(TEXTURE);

    public RenderRFOrb(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    protected int getBlockLightLevel(EntityRFOrb orb, BlockPos pos) {
        return Mth.clamp(super.getBlockLightLevel(orb, pos) + 7, 0, 15);
    }

    @Override
    public void submit(RFOrbRenderState state, PoseStack stack, SubmitNodeCollector nodeCollector, CameraRenderState cameraRenderState) {
        stack.pushPose();
        int i = (int) (System.nanoTime() / 25000000L % 16L);

        float f2 = i / 16.0F;
        float f3 = (i + 1) / 16.0F;
        float f4 = 0.5F;
        float f5 = 0.5625F;

        stack.translate(0.0D, 0.1F, 0.0D);
        stack.mulPose(cameraRenderState.orientation);
//        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        float f11 = 0.1F + 0.3F * state.lifeAge;
        stack.scale(f11, f11, f11);
        nodeCollector.submitCustomGeometry(stack, RENDER_TYPE, (pose, builder) -> {
            vertex(builder, pose, -0.5F, -0.25F, 255, 0, 0, f2, f5, state.lightCoords);
            vertex(builder, pose, 0.5F, -0.25F, 255, 0, 0, f3, f5, state.lightCoords);
            vertex(builder, pose, 0.5F, 0.75F, 255, 0, 0, f3, f4, state.lightCoords);
            vertex(builder, pose, -0.5F, 0.75F, 255, 0, 0, f2, f4, state.lightCoords);
        });
        stack.popPose();
        super.submit(state, stack, nodeCollector, cameraRenderState);
    }

    private static void vertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, int r, int g, int b, float u, float v, int lightmapUV) {
        builder
                .addVertex(pose, x, y, 0.0F)
                .setColor(r, g, b, 128)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(lightmapUV)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public RFOrbRenderState createRenderState() {
        return new RFOrbRenderState();
    }

    @Override
    public void extractRenderState(EntityRFOrb orb, RFOrbRenderState state, float partialTick) {
        super.extractRenderState(orb, state, partialTick);
        state.lifeAge = ((float) (orb.orbMaxAge - orb.orbAge) / (float) orb.orbMaxAge);
    }
}
