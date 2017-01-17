package world.betelge.com.infiniteworld;

import tk.betelge.alw3d.math.Vector3f;
import tk.betelge.alw3d.procedurals.Procedural;

/**
 * Created by betelgeuze on 12/01/17.
 */

public class DummyProcedural implements Procedural {
    @Override
    public double getValue(double x, double y, double z, double resolution) {
        return 0;
    }

    @Override
    public double getValueNormal(double x, double y, double z, double resolution, Vector3f gradient) {
        gradient.set(0, 0, 0);
        return 0;
    }
}
