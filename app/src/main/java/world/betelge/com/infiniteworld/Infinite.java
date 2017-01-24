package world.betelge.com.infiniteworld;

import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Random;

import tk.betelge.alw3d.Alw3dModel;
import tk.betelge.alw3d.Alw3dView;
import tk.betelge.alw3d.math.Noise;
import tk.betelge.alw3d.math.Transform;
import tk.betelge.alw3d.math.Vector3f;
import tk.betelge.alw3d.procedurals.Procedural;
import tk.betelge.alw3d.procedurals.fBm;
import tk.betelge.alw3d.renderer.CameraNode;
import tk.betelge.alw3d.renderer.Material;
import tk.betelge.alw3d.renderer.Node;
import tk.betelge.alw3d.renderer.ShaderProgram;
import tk.betelge.alw3d.renderer.UpdatableGeometry;
import tk.betelge.alw3d.renderer.passes.ClearPass;
import tk.betelge.alw3d.renderer.passes.RenderPass;
import tk.betelge.alw3d.renderer.passes.SceneRenderPass;
import utils.ShaderLoader;
import utils.StringLoader;

public class Infinite extends AppCompatActivity implements View.OnTouchListener {

    Alw3dModel model;
    Alw3dView view;
    CameraNode sceneCam;

    Procedural proc;
    Patch[] patches;
    final int PATCH_SIZE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        model = new Alw3dModel();
        view = new Alw3dView(this, model);
        StringLoader.setContext(this);

        setContentView(view);

        ShaderProgram shaderProgram = ShaderLoader.loadShaderProgram(R.raw.terrain_v, R.raw.terrain_f);
        Material material = new Material(shaderProgram);

        proc = new /*fBm*/Noise(42l);
        Node rootNode = new Node();

        patches = new Patch[PATCH_SIZE*PATCH_SIZE];

        // Initialize patches
        for(int j = 0; j < PATCH_SIZE; j++) {
            for (int i = 0; i < PATCH_SIZE; i++) {
                Vector3f pos = new Vector3f(-(PATCH_SIZE-1) / 2 + i, -(PATCH_SIZE-1) / 2 + j, 0);
                PatchGeometry geo = PatchGenerator.generate(proc, 32, pos, 1);
                Patch patch = new Patch(geo, material);
                patches[PATCH_SIZE*j+i] = patch;
                patch.getTransform().getPosition().set(pos);
                rootNode.attach(patch);
            }
        }

        sceneCam = new CameraNode(60, 0, 0.1f, 1000);
        rootNode.attach(sceneCam);
        sceneCam.getTransform().getPosition().set(2, -2, 2);
        sceneCam.getTransform().getRotation().lookAt(new Vector3f(-1,1,-1), new Vector3f(0, 0, -1));
        SceneRenderPass pass = new SceneRenderPass(rootNode, sceneCam);

        List<RenderPass> renderPasses = model.getRenderPasses();

        renderPasses.add(new ClearPass(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT));
        renderPasses.add(pass);

        view.setOnTouchListener(this);
    }

    Random rnd = new Random();
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Vector3f pos = new Vector3f(100*rnd.nextFloat(), 100*rnd.nextFloat(), 0.f);
        //PatchGenerator.update((PatchGeometry) patches[4].getGeometry(), proc, pos, 1.f);

        if(motionEvent.getHistorySize() > 0) {
            float dx = motionEvent.getX() - motionEvent.getHistoricalX(0);
            float dy = motionEvent.getY() - motionEvent.getHistoricalY(0);
            sceneCam.getTransform().getPosition().addThis(dx/1000f, dy/1000f, 0);

            arrangePatches();
        }

        return true;
    }

    private void arrangePatches() {
        Transform camTrans = sceneCam.getAbsoluteTransform();
        Vector3f lookDir = camTrans.getRotation().mult(Vector3f.UNIT_Z);
        Vector3f camPos = camTrans.getPosition();

        lookDir.multThis(-camPos.z/lookDir.z);
        Vector3f rayHit = camPos.add(lookDir);

        int gridX = Math.round(rayHit.x);
        int gridY = Math.round(rayHit.y);

        int centerOffset = (PATCH_SIZE - 1) / 2;
        Vector3f currentPos = patches[PATCH_SIZE*centerOffset+centerOffset].getAbsoluteTransform()
                .getPosition();
        int dx = gridX - Math.round(currentPos.x);
        int dy = gridY - Math.round(currentPos.y);
        float errorX = Math.abs(rayHit.x - (gridX - .5f * dx));
        float errorY = Math.abs(rayHit.y - (gridY - .5f * dy));

        if(dx == 0 && dy == 0 || (errorX < .1 && errorY < .1))
            return; // We haven't moved enough

        Patch[] newPatches = new Patch[PATCH_SIZE*PATCH_SIZE];

        for(int j = 0; j < PATCH_SIZE; j++){
            for(int i = 0; i < PATCH_SIZE; i++) {

                Patch patch = patches[PATCH_SIZE*j+i];

                int newI = (i - dx) % PATCH_SIZE;
                int newJ = (j - dy) % PATCH_SIZE;
                if(newI < 0) newI += PATCH_SIZE;
                if(newJ < 0) newJ += PATCH_SIZE;

                newPatches[PATCH_SIZE*newJ+newI] = patch;

                if( newI != i - dx || newJ != j - dy) {
                    // This patch has looped
                    Vector3f patchPos = patch.getTransform().getPosition();
                    patchPos.set(gridX + newI - centerOffset,
                            gridY + newJ - centerOffset, 0);

                    PatchGenerator.update((PatchGeometry) patch.getGeometry(), proc, patchPos, 1f);
                }
            }
        }

        for(int i = 0; i < PATCH_SIZE*PATCH_SIZE; i++) {
            patches[i] = newPatches[i];
        }
    }
}
