package com.paulvarry.intra42.api.cluster_map_contribute;

import android.util.SparseArray;

import com.paulvarry.intra42.api.cluster_map.Location;

import androidx.annotation.NonNull;

public class MapStore extends SparseArray<SparseArray<Location>> {

    @NonNull
    public SparseArray<Location> require(int x, Cluster cluster) {
        if (x < 0)
            throw new ArrayIndexOutOfBoundsException("x= " + String.valueOf(x));

        SparseArray<Location> col = super.get(x);
        if (col == null) {
            col = new SparseArray<>(cluster.height);
            super.append(x, col);
        }
        return col;
    }

    public Location get(int x, int y) {
        if (x < 0 || y < 0)
            return null;
        SparseArray<Location> col = super.get(x);
        if (col == null)
            return null;
        return col.get(y);
    }

    @NonNull
    public Location require(int x, int y, Cluster cluster) {
        if (x < 0 || y < 0)
            throw new ArrayIndexOutOfBoundsException("x= " + String.valueOf(x) + " ; y= " + String.valueOf(y));

        SparseArray<Location> col = super.get(x);
        if (col == null) {
            col = new SparseArray<>(cluster.height);
            super.append(x, col);
            col.append(y, new Location());
            return col.get(y);
        }

        Location cel = col.get(y);
        if (cel == null) {
            cel = new Location();
            col.append(y, cel);
        }
        return cel;
    }

    public void replace(int x, int y, Location item, Cluster cluster) {
        if (x < 0 || y < 0)
            throw new ArrayIndexOutOfBoundsException("x= " + String.valueOf(x) + " ; y= " + String.valueOf(y));

        SparseArray<Location> col = super.get(x);
        if (col == null) {
            col = new SparseArray<>(cluster.height);
            super.append(x, col);
            col.append(y, item);
        } else
            col.append(y, item);
    }
}
