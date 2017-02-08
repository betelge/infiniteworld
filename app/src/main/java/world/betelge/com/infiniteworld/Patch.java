package world.betelge.com.infiniteworld;

import tk.betelge.alw3d.renderer.Geometry;
import tk.betelge.alw3d.renderer.GeometryNode;
import tk.betelge.alw3d.renderer.Material;
import tk.betelge.alw3d.renderer.UpdatableGeometry;

/**
 * Created by betelgeuze on 12/01/17.
 */

public class Patch extends GeometryNode {

    int gridx, gridy, gridz;
    int level;

    TerrainAsyncTask task = null;

    Patch(Geometry geometry, Material material) {
        super(geometry, material);
        setVisible(false); // Set to true when updated with generated terrain
    }

    public TerrainAsyncTask getTerrainAsyncTask() {
        return task;
    }

    public void setTerrainAsyncTask(TerrainAsyncTask task) {
        this.task = task;
    }

    public int getGridx() {
        return gridx;
    }

    public void setGridx(int gridx) {
        this.gridx = gridx;
    }

    public int getGridy() {
        return gridy;
    }

    public void setGridy(int gridy) {
        this.gridy = gridy;
    }

    public int getGridz() {
        return gridz;
    }

    public void setGridz(int gridz) {
        this.gridz = gridz;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
