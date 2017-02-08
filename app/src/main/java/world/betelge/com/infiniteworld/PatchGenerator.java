package world.betelge.com.infiniteworld;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import tk.betelge.alw3d.math.Vector3f;
import tk.betelge.alw3d.procedurals.Procedural;
import tk.betelge.alw3d.renderer.Geometry;
import tk.betelge.alw3d.renderer.UpdatableGeometry;

/**
 * Created by betelgeuze on 12/01/17.
 */

public class PatchGenerator {
    final static String POS_ATT_NAME = "position";
    final static String NORMAL_ATT_NAME = "normal";

    public static PatchGeometry generate(Procedural proc, int resolution) {

        int indexCount = 2 * resolution * (resolution - 1) + 2 * (resolution - 1);
        int vertexCount = resolution * resolution;

        ShortBuffer indices = ShortBuffer.allocate(indexCount);

        List<Geometry.Attribute> atList = new ArrayList<Geometry.Attribute>();

        Geometry.Attribute position = new Geometry.Attribute();
        position.name = POS_ATT_NAME;
        position.size = 3;
        position.type = Geometry.Type.FLOAT;
        position.buffer = FloatBuffer.allocate(3 * vertexCount);
        atList.add(position);

        Geometry.Attribute normal = new Geometry.Attribute();
        normal.name = NORMAL_ATT_NAME;
        normal.size = 3;
        normal.type = Geometry.Type.FLOAT;
        normal.buffer = FloatBuffer.allocate(3 * vertexCount);
        atList.add(normal);

        PatchGeometry geo = new PatchGeometry(Geometry.PrimitiveType.TRIANGLE_STRIP,
                indices, atList, indexCount, resolution);

        return geo;
    }

    public static void update(Patch patch, Procedural proc, Vector3f pos, float scale) {
        TerrainAsyncTask oldTask = patch.getTerrainAsyncTask();
        if(oldTask != null)
            oldTask.cancel(true); // Cancel thread even if running

        TerrainAsyncTask newTask = new TerrainAsyncTask(patch, proc, pos, scale);
        newTask.execute();
        patch.setTerrainAsyncTask(newTask);
    }

    /*public static void update(PatchGeometry geometry, Procedural proc, Vector3f pos, float scale) {
        update(geometry, proc, pos, scale, true);
    }

    public static void update(PatchGeometry geometry, Procedural proc, Vector3f pos, float scale,
                              boolean isAsync) {

        TerrainAsyncTask task = new TerrainAsyncTask(geometry, proc, pos, scale);

        if(isAsync)
            task.execute();
        else
            task.doInBackground(null); // Execute on this thread
    }*/
}
