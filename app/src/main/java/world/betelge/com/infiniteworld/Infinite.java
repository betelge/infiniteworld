package world.betelge.com.infiniteworld;

import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import tk.betelge.alw3d.Alw3dModel;
import tk.betelge.alw3d.Alw3dView;
import tk.betelge.alw3d.math.Noise;
import tk.betelge.alw3d.math.Vector3f;
import tk.betelge.alw3d.procedurals.Procedural;
import tk.betelge.alw3d.renderer.CameraNode;
import tk.betelge.alw3d.renderer.Material;
import tk.betelge.alw3d.renderer.Node;
import tk.betelge.alw3d.renderer.ShaderProgram;
import tk.betelge.alw3d.renderer.passes.ClearPass;
import tk.betelge.alw3d.renderer.passes.RenderPass;
import tk.betelge.alw3d.renderer.passes.SceneRenderPass;
import utils.ShaderLoader;
import utils.StringLoader;

public class Infinite extends AppCompatActivity {

    Alw3dModel model;
    Alw3dView view;
    CameraNode sceneCam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new Alw3dModel();
        view = new Alw3dView(this, model);
        StringLoader.setContext(this);

        setContentView(view);

        ShaderProgram shaderProgram = ShaderLoader.loadShaderProgram(R.raw.terrain_v, R.raw.terrain_f);
        Material material = new Material(shaderProgram);

        Procedural proc = new Noise(42l);
        PatchGeometry geo = PatchGenerator.generate(
                proc, 32, new Vector3f(0,0,0), 1);
        Patch patch = new Patch(geo, material);

        Node rootNode = new Node();
        rootNode.attach(patch);
        sceneCam = new CameraNode(60, 0, 0.1f, 1000);
        rootNode.attach(sceneCam);
        sceneCam.getTransform().getPosition().set(3, -3, 3);
        sceneCam.getTransform().getRotation().lookAt(new Vector3f(-1,1,-1), new Vector3f(0, 0, -1));
        SceneRenderPass pass = new SceneRenderPass(rootNode, sceneCam);

        List<RenderPass> renderPasses = model.getRenderPasses();

        renderPasses.add(new ClearPass(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT));
        renderPasses.add(pass);
    }
}
