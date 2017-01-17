package world.betelge.com.infiniteworld;

import java.nio.ShortBuffer;
import java.util.List;

import tk.betelge.alw3d.renderer.UpdatableGeometry;

/**
 * Created by betelgeuze on 12/01/17.
 */

public class PatchGeometry extends UpdatableGeometry {

    final int maxCount;

    PatchGeometry(PrimitiveType primitiveType, ShortBuffer indices,
                  List<Attribute> attributes, int maxVertexCount) {
        super(primitiveType, indices, attributes);
        this.maxCount = maxVertexCount;
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }
}
