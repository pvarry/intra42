package com.paulvarry.intra42.api.cluster_map_contribute;

import android.content.Context;
import android.util.SparseArray;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.IBaseItemSmall;
import com.paulvarry.intra42.api.cluster_map.Location;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class Cluster implements IBaseItemSmall, Serializable, Comparable<Cluster> {

    public String name;
    public String nameShort;
    public String slug;
    public String hostPrefix;
    public int campusId;
    public int position;
    /**
     * Size X
     */
    public int width;
    /**
     * Size Y
     */
    public int height;
    public MapStore map;
    public String comment;
    public boolean isReadyToPublish;

    private transient Campus campus;

    public Cluster() {
    }

    public Cluster(int campusId, String name, String hostPrefix) {
        this.campusId = campusId;
        this.name = name;
        this.nameShort = name;
        this.hostPrefix = hostPrefix;
    }

    public void setMap(Object mapTmp) {
        this.map = new MapStore();

        if (mapTmp == null)
            return;

        if (mapTmp instanceof HashMap) {
            HashMap mapData = (HashMap) mapTmp;
            for (Object i : mapData.keySet()) {
                setMapCol(Integer.parseInt(i.toString()), mapData.get(i));
            }
        } else if (mapTmp instanceof ArrayList) {
            ArrayList mapData = (ArrayList) mapTmp;
            for (int i = 0; i < mapData.size(); i++) {
                setMapCol(i, mapData.get(i));
            }
        }
    }

    private void setMapCol(int i, Object colTmp) {
        if (colTmp == null)
            return;
        map.put(i, new SparseArray<Location>());

        if (colTmp instanceof HashMap) {
            HashMap col = (HashMap) colTmp;
            for (Object j : col.keySet()) {
                setMapCel(i, Integer.parseInt(j.toString()), col.get(j));
            }
        } else if (colTmp instanceof ArrayList) {
            ArrayList col = (ArrayList) colTmp;
            for (int j = 0; j < col.size(); j++) {
                setMapCel(i, j, col.get(j));
            }
        }
    }

    private void setMapCel(int i, int j, Object o) {
        if (o == null)
            return;
        map.get(i).put(j, new Location((Map<String, Object>) o));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("nameShort", nameShort);
        map.put("slug", slug);
        map.put("hostPrefix", hostPrefix);
        map.put("campusId", campusId);
        map.put("position", position);
        map.put("width", width);
        map.put("height", height);
        map.put("comment", comment);
        map.put("isReadyToPublish", isReadyToPublish);

        ArrayList<ArrayList<Location>> arrayListExport = new ArrayList<>(width);
        for (int i = 0; i < width; i++) {
            SparseArray<Location> sparseArrayCol = this.map.get(i);
            arrayListExport.add(i, new ArrayList<Location>(height));

            if (sparseArrayCol != null)
                for (int j = 0; j < height; j++) {
                    arrayListExport.get(i).add(j, sparseArrayCol.get(j));
                }
        }
        map.put("map", arrayListExport);

        return map;
    }

    @Override
    public String getName(Context context) {
        if (campus == null) {
            List<Campus> cache = CacheCampus.get(AppClass.instance().cacheSQLiteHelper);
            if (cache != null)
                for (Campus c : cache) {
                    if (c.id == campusId)
                        this.campus = c;
                }
        }
        StringBuilder b = new StringBuilder();
        if (campus != null) {
            b.append("[").append(campus.name).append(" - ").append(campusId).append("]");
        } else if (campusId != 0)
            b.append("[campus_id=").append(campusId).append("]");
        else
            b.append("[??]");
        b.append(" ");
        if (name != null)
            b.append(name);
        if (name != null && nameShort != null)
            b.append(" - ");
        if (nameShort != null)
            b.append(nameShort);

        if (name == null && nameShort == null)
            b.append("[no name]");

        return b.toString();
    }

    @Override
    public String getSub(Context context) {
        return comment;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }

    @Override
    public int compareTo(@NonNull Cluster o) {
        if (campusId != o.campusId) {
            if (campusId > o.campusId)
                return 1;
            else
                return -1;
        }
        if (position != o.position) {
            if (position > o.position)
                return 1;
            else
                return -1;
        }
        return name.compareTo(o.name);

    }

    @Override
    public int getId() {
        return 0;
    }
}
