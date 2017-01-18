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

    public static PatchGeometry generate(Procedural proc, int resolution, Vector3f pos, float scale) {

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

        update(geo, proc, pos, scale);

        return geo;
    }

    public static void update(PatchGeometry geometry, Procedural proc, Vector3f pos, float scale) {

        synchronized (geometry) {
            int resolution = geometry.getResolution();

            ShortBuffer indices = geometry.getIndices();
            FloatBuffer vertices = null;
            FloatBuffer normals = null;
            for (Geometry.Attribute att : geometry.getAttributes()) {
                if (att.name.equals(POS_ATT_NAME)) {
                    vertices = (FloatBuffer) att.buffer;
                } else if (att.name.equals(NORMAL_ATT_NAME)) {
                    normals = (FloatBuffer) att.buffer;
                }
            }

            vertices.clear();
            normals.clear();
            Vector3f vertPos = new Vector3f();
            Vector3f realPos = new Vector3f();
            Vector3f gradient = new Vector3f();
            for (int j = 0; j < resolution; j++) {
                for (int i = 0; i < resolution; i++) {
                    vertPos.set(-.5f + i / (float) (resolution - 1), -.5f + j / (float) (resolution - 1), 0.f);
                    realPos.set(vertPos);
                    realPos.multThis(scale);
                    realPos.addThis(pos);

                    double h = proc.getValueNormal(realPos.x, realPos.y, realPos.z,
                            scale / resolution, gradient);

                    vertices.put(vertPos.x);
                    vertices.put(vertPos.y);
                    vertices.put((float) h);

                    normals.put(gradient.x);
                    normals.put(gradient.y);
                    normals.put(gradient.z);
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

        }
    }
}
