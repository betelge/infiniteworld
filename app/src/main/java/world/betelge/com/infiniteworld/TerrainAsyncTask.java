package world.betelge.com.infiniteworld;

import android.os.AsyncTask;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import tk.betelge.alw3d.math.Vector3f;
import tk.betelge.alw3d.procedurals.Procedural;
import tk.betelge.alw3d.renderer.Geometry;

/**
 * Created by betelgeuze on 8/02/17.
 */

public class TerrainAsyncTask extends AsyncTask {
    Patch patch = null;
    final PatchGeometry geometry;
    final Procedural proc;
    final Vector3f pos;
    final float scale;

    TerrainAsyncTask(Patch patch, Procedural proc, Vector3f pos, float scale) {
        this((PatchGeometry) patch.getGeometry(), proc, pos, scale);
        this.patch = patch;
        synchronized (patch) {
            patch.setVisible(false);
        }
    }

    private TerrainAsyncTask(PatchGeometry geometry, Procedural proc, Vector3f pos, float scale) {
        this.geometry = geometry;
        this.proc = proc;
        this.pos = pos;
        this.scale = scale;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        synchronized (geometry) {
            geometry.setCount(0);
        }

        int resolution = geometry.getResolution();

        ShortBuffer indices = geometry.getIndices();
        FloatBuffer vertices = null;
        FloatBuffer normals = null;
        for (Geometry.Attribute att : geometry.getAttributes()) {
            if (att.name.equals(PatchGenerator.POS_ATT_NAME)) {
                vertices = (FloatBuffer) att.buffer;
            } else if (att.name.equals(PatchGenerator.NORMAL_ATT_NAME)) {
                normals = (FloatBuffer) att.buffer;
            }
        }

        vertices.clear();
        normals.clear();
        Vector3f vertPos = new Vector3f();
        Vector3f realPos = new Vector3f();
        Vector3f normal = new Vector3f();
        for (int j = 0; j < resolution; j++) {
            for (int i = 0; i < resolution; i++) {
                vertPos.set(-.5f + i / (float) (resolution - 1), -.5f + j / (float) (resolution - 1), 0.f);
                realPos.set(vertPos);
                realPos.multThis(scale);
                realPos.addThis(pos);

                double h = proc.getValueNormal(realPos.x, realPos.y, realPos.z,
                        scale / resolution, normal);

                vertices.put(vertPos.x);
                vertices.put(vertPos.y);
                vertices.put((float) h);

                normals.put(normal.x);
                normals.put(normal.y);
                normals.put(normal.z);
            }
        }
        vertices.flip();
        normals.flip();

        indices.clear();
        for (short j = 0; j < resolution - 1; j++) {
            for (short i = 0; i < resolution; i++) {
                indices.put((short) (j * resolution + i));
                indices.put((short) ((j + 1) * resolution + i));
            }
            // Degenerated triangle
            indices.put((short) ((j + 2) * resolution - 1));
            indices.put((short) ((j + 1) * resolution));
        }
        indices.flip();

        geometry.setCount(indices.capacity());
        geometry.needsUpdate = true;

        if(patch != null) {
            synchronized (patch) {
                patch.setVisible(true);
            }
        }

        return null;
    }
}
