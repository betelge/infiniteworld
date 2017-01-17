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
    final static String POS_ATT_NAME = "position";//"a_pos";

    public static PatchGeometry generate(Procedural proc, int resolution, Vector3f pos, float scale) {

        ShortBuffer indices = ShortBuffer.allocate(2 * resolution * (resolution - 1) + 2 * (resolution - 1));

        Geometry.Attribute position = new Geometry.Attribute();
        position.name = POS_ATT_NAME;
        position.size = 3;
        position.type = Geometry.Type.FLOAT;
        position.buffer = FloatBuffer.allocate(3 * resolution * resolution);
        List<Geometry.Attribute> atList = new ArrayList<Geometry.Attribute>();
        atList.add(position);

        PatchGeometry geo = new PatchGeometry(Geometry.PrimitiveType.TRIANGLE_STRIP,
                indices, atList, resolution * resolution);

        update(geo, proc, pos, scale);

        return geo;
    }

    public static void update(UpdatableGeometry geometry, Procedural proc, Vector3f pos, float scale) {
        int resolution = (int) Math.sqrt(geometry.getMaxCount());

        ShortBuffer indices = geometry.getIndices();
        FloatBuffer vertices = null;
        for(Geometry.Attribute att : geometry.getAttributes()) {
            if(att.name.equals(POS_ATT_NAME)) {
                vertices = (FloatBuffer) att.buffer;
                break;
            }
        }

        vertices.clear();
        Vector3f vertPos = new Vector3f();
        Vector3f realPos = new Vector3f();
        for(int j = 0; j < resolution; j++) {
            for(int i = 0; i < resolution; i++) {
                vertPos.set(-1.f + 2.f*i/(resolution - 1), -1.f + 2.f*j/(resolution - 1), 0.f);
                realPos.set(vertPos);
                realPos.multThis(scale);
                realPos.addThis(pos);

                double h = proc.getValue(realPos.x, realPos.y, realPos.z,
                        scale / resolution);

                vertices.put(vertPos.x);
                vertices.put(vertPos.y);
                vertices.put((float) h);
            }
        }
        vertices.flip();

        indices.clear();
        for(short j = 0; j < resolution - 1; j++) {
            for(short i = 0; i < resolution; i++) {
                indices.put((short) (j*resolution + i));
                indices.put((short) ((j+1)*resolution + i));
            }
            // Degenerated triangle
            indices.put((short) ((j+2)*resolution - 1 ));
            indices.put((short) ((j+1)*resolution));
        }
        indices.flip();

        geometry.setCount(indices.capacity());
    }
}
