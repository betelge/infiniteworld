package world.betelge.com.infiniteworld;

import java.nio.ShortBuffer;
import java.util.List;

import tk.betelge.alw3d.renderer.UpdatableGeometry;

/**
 * Created by betelgeuze on 12/01/17.
 */

public class PatchGeometry extends UpdatableGeometry {

    final int maxCount;
    final int resolution;

    PatchGeometry(PrimitiveType primitiveType, ShortBuffer indices,
                  List<Attribute> attributes, int maxCount, int resolution) {
        super(primitiveType, indices, attributes);
        this.maxCount = maxCount;
        this.resolution = resolution;

        this.setCount(maxCount);
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }

    public int getResolution() {
        return resolution;
    }
}
