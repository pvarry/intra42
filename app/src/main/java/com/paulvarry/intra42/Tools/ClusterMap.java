package com.paulvarry.intra42.Tools;

public class ClusterMap {

    final private static int CLUSTER_FREMONT_E1_Z1_HEIGHT = 22;
    final private static int CLUSTER_FREMONT_E1_Z1_WIDTH = 33;

    public static LocationItem[][] getParisCluster(String clusterID) {

        final int cluster[][] = {
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}, //r13
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r12
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r11
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r10
                {0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0}, //r9
                {0, 0, 0, 0, 0, 0, 1, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r8
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r7
                {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 0, 0, 0, 0, 0, 0}, //r6
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, //r5
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r4
                {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0}, //r3
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, //r2
                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0}  //r1
        };

        LocationItem map[][] = new LocationItem[cluster.length][cluster[0].length];

        for (int r = 0; r < cluster.length; r++) {
            int realP = 0;

            for (int p = 0; p < cluster[r].length; p++) {

                int locationKind = cluster[r][p];
                String locationName = null;
                if (locationKind == 0) {
                    realP++;
                    locationName = clusterID + "r" + String.valueOf(13 - r) + "p" + String.valueOf(realP);
                }

                map[r][p] = new LocationItem(locationName, locationKind);
            }
        }

        for (int r = 0; r < cluster.length; r++) {
            map[r][7].sizeX = (float) 0.5;
            map[r][17].sizeX = (float) 0.5;
        }

        return map;
    }

    public static LocationItem[][] getFremontCluster(String clusterName) {

        int row = 20;
        int poste = 100;
        String locationName;

        LocationItem map[][] = new LocationItem[row][poste];

        for (int r = 0; r < row; r++) {

            for (int p = 0; p < poste; p++) {

                locationName = clusterName + "r" + String.valueOf(r) + "p" + String.valueOf(p);
                map[r][p] = new LocationItem(locationName, LocationItem.KIND_USER);
            }
        }

        return map;
    }

    public static LocationItem[][] getFremontCluster1Zone1() {

        LocationItem map[][] = new LocationItem[CLUSTER_FREMONT_E1_Z1_HEIGHT][CLUSTER_FREMONT_E1_Z1_WIDTH];

        String clusterID = "e1z1";
        int r = 0;
        int realR = 1;

        createFremont1z1r1(map, clusterID, r, realR);

        r++;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r2(map, clusterID, r, realR);

        r += 2;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r3(map, clusterID, r, realR);

        r += 2;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r4(map, clusterID, r, realR);

        r += 2;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r5(map, clusterID, r, realR);

        r += 2;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r6(map, clusterID, r, realR);

        r += 2;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r7(map, clusterID, r, realR);

        r += 2;
        addEmptyRow(map[r], LocationItem.KIND_CORRIDOR);

        r++;
        realR++;
        createFremont1z1r8(map, clusterID, r, realR);

        return map;
    }

    private static void createFremont1z1r1(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        String locationName;
        int realP = 7;

        for (int p = 0; p < CLUSTER_FREMONT_E1_Z1_WIDTH; p++) {//r1

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 26 && p < 32) {
                locationKind = LocationItem.KIND_USER;
                realP--;
                locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
            } else
                locationName = null;

            map[r][p] = new LocationItem(locationName, locationKind);
        }
    }

    private static void createFremont1z1r2(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r2

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 5 && p < 33) {
                if (p == 31 || p == 17) {
                    map[r][p] = new LocationItem(null, locationKind);
                    map[r + 1][p] = new LocationItem(null, locationKind);
                } else {
                    addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        map[r][12] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
        map[r][13] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
    }

    private static void createFremont1z1r3(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r3

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 1 && p < 33) {
                if (p == 30 || p == 16) {
                    map[r][p] = new LocationItem(null, locationKind);
                    map[r + 1][p] = new LocationItem(null, locationKind);
                } else {
                    addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);

        }
        realP++;
        addDoublePost(map, clusterID, LocationItem.KIND_USER, LocationItem.KIND_CORRIDOR, r, 0, realP, realR);
    }

    private static void createFremont1z1r4(LocationItem[][] map, String clusterID, int r, int realR) {
        int locationKind;
        String locationName;
        int realP = 1;

        locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(null, LocationItem.KIND_WALL);
        map[r + 1][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(locationName, LocationItem.KIND_USER);
        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 2; p >= 0; p--) {//r4

            locationKind = LocationItem.KIND_CORRIDOR;
            if (p >= 4 && p < 33) {
                if (p == 29 || p == 15) {
                    map[r][p] = new LocationItem(null, locationKind);
                    map[r + 1][p] = new LocationItem(null, locationKind);
                } else {
                    if (p > 18)
                        addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    else if (p < 18)
                        addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    else {
                        locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP + 1);
                        map[r][p] = new LocationItem(locationName, LocationItem.KIND_USER);
                        map[r + 1][p] = new LocationItem(null, LocationItem.KIND_WALL);
                        realP--;
                    }
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
    }

    private static void createFremont1z1r5(LocationItem[][] map, String clusterID, int r, int realR) {
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r4

            if (p >= 8 && p < 33) {
                if (p == 28 || p == 14)
                    addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
    }

    private static void createFremont1z1r6(LocationItem[][] map, String clusterID, int r, int realR) {
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r4

            if (p >= 10 && p < 33) {
                if (p == 27 || p == 13)
                    addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        map[r + 1][10] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
    }

    private static void createFremont1z1r7(LocationItem[][] map, String clusterID, int r, int realR) {
        String locationName;
        int realP = 1;

        locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(null, LocationItem.KIND_WALL);
        map[r + 1][CLUSTER_FREMONT_E1_Z1_WIDTH - 1] = new LocationItem(locationName, LocationItem.KIND_USER);
        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 2; p >= 0; p--) {//r4

            if (p >= 13 && p < 33) {
                if (p == 26 || p == 12)
                    addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    addDoublePost(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        map[r + 1][13] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
        map[r + 1][14] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
    }

    private static void createFremont1z1r8(LocationItem[][] map, String clusterID, int r, int realR) {
        int realP = 0;

        for (int p = CLUSTER_FREMONT_E1_Z1_WIDTH - 1; p >= 0; p--) {//r4

            if (p >= 17 && p < 33) {
                if (p == 25 || p == 11)
                    addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
                else {
                    addDoublePostFromDown(map, clusterID, LocationItem.KIND_USER, r, p, realP, realR);
                    realP += 2;
                }
            } else
                addDoublePost(map, clusterID, LocationItem.KIND_CORRIDOR, r, p, realP, realR);
        }
        for (int i = 0; i < 8; i++) {
            map[r + 1][17 + i] = new LocationItem(null, LocationItem.KIND_CORRIDOR);
        }
    }

    private static void addEmptyRow(LocationItem[] map, int KIND_OF_ROW) {
        for (int p = 0; p < CLUSTER_FREMONT_E1_Z1_WIDTH; p++) {
            map[p] = new LocationItem(null, KIND_OF_ROW);
            map[p].sizeY = (float) 0.5;
        }
    }

    private static void addDoublePostFromDown(LocationItem[][] map, String clusterID, int locationKind, int r, int p, int realP, int realR) {
        addDoublePostFromDown(map, clusterID, locationKind, locationKind, r, p, realP, realR);
    }

    private static void addDoublePostFromDown(LocationItem[][] map, String clusterID, int locationKindTop, int locationKindBottom, int r, int p, int realP, int realR) {
        String locationName = null;

        realP++;
        if (locationKindBottom == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r + 1][p] = new LocationItem(locationName, locationKindBottom);

        realP++;
        if (locationKindTop == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][p] = new LocationItem(locationName, locationKindTop);
    }

    private static void addDoublePost(LocationItem[][] map, String clusterID, int locationKind, int r, int p, int realP, int realR) {
        addDoublePost(map, clusterID, locationKind, locationKind, r, p, realP, realR);
    }

    private static void addDoublePost(LocationItem[][] map, String clusterID, int locationKindTop, int locationKindBottom, int r, int p, int realP, int realR) {
        String locationName = null;

        realP++;
        if (locationKindTop == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r][p] = new LocationItem(locationName, locationKindTop);

        realP++;
        if (locationKindBottom == LocationItem.KIND_USER)
            locationName = clusterID + "r" + String.valueOf(realR) + "p" + String.valueOf(realP);
        map[r + 1][p] = new LocationItem(locationName, locationKindBottom);
    }

    public static LocationItem[][] getFremontCluster1Zone2() {
        String clusterID = "e1z2";

        final int cluster[][] = {/*
                 A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y  */
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //1
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //2
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //3
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //4
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //5
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 1}, //6
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 2, 0, 1}, //7
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //8
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //9
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //10
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //11
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //12
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //13
                {1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //14
                {1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1}, //15
                {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //16
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //17
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //18
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1}, //19
                {1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //20
                {1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //21
                {1, 2, 2, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //22
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1}, //23
                {1, 1, 2, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //24
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //25
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //26
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //27
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //28
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //29
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //30
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //31
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //32
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //33
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //34
                {1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //35
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //36
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //37
                {1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //38
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1}, //39
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //40
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //41
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //42
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1}, //43
                {1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //44
                {1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //45
                {1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //46
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1}, //47
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //48
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //49
                {1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //50
                {1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //51
                {1, 1, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //52
                {1, 1, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //53
                {1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //54
                {1, 1, 1, 1, 1, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //55
                {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //56
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //57
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //58
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //59
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //60
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}  //61
        };

        final String clusterLocationName[][] = {/*
                 A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y  */
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p1", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p3", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p13", null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p12", null, "r1p11", "r1p9", "r1p7", null, "r1p4", null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p10", null, null, null, "r1p6", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p14", null, null, null, "r2p6", null, null, null, "r2p4", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p12", null, "r2p8", null, "r2p7", "r2p5", null, null, "r2p2", null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p11", null, null, null, "r2p9", null, null, null, "r2p1", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p34", null, null, null, "r3p30", null, null, null, "r3p2", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p36", null, "r3p35", "r3p33", "r3p31", null, null, null, "r3p4", null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p3", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p39", null, null, null, null, null, null, null, "r3p5", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p41", null, null, null, null, null, null, null, "r3p7", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p40", null, "r3p43", null, null, null, "r3p26", null, "r3p6", null, "r3p9", null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p42", null, null, null, null, null, null, null, "r3p8", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p44", null, null, null, "r3p22", null, null, null, "r3p10", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p48", null, "r3p45", null, null, null, "r3p18", null, "r3p14", null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r3p47", null, null, null, "r3p17", null, null, null, "r3p13", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
        };

        LocationItem map[][] = new LocationItem[cluster.length][cluster[0].length];

        int locationKind;
        for (int r = 0; r < cluster.length; r++) {

            for (int p = 0; p < cluster[r].length; p++) {

                locationKind = cluster[r][p];
                if (locationKind == 1)
                    map[r][p] = new LocationItem(null, locationKind);
                else
                    map[r][p] = new LocationItem(null, locationKind);
                if (locationKind == 0)
                    map[r][p] = new LocationItem(clusterID + clusterLocationName[r][p], locationKind);

                map[r][p].sizeX = (float) 0.7;
                map[r][p].sizeY = (float) 0.7;
            }
        }

        return map;
    }

    public static class LocationItem {

        public final static int KIND_USER = 0;
        public final static int KIND_CORRIDOR = 1;
        public final static int KIND_WALL = 2;
        /**
         * 0: user emplacement;
         * 1: corridor;
         * 2: wall;
         */
        public float kind;
        /**
         * 0 -> 1
         * Default 1
         */
        public float sizeX;
        /**
         * 0 -> 1
         * Default 1
         */
        public float sizeY;
        public String locationName;
        float angle;

        public LocationItem(String locationName, int kind, float angle) {

            this.locationName = locationName;
            this.kind = kind;
            this.angle = angle;
            this.sizeX = 1;
            this.sizeY = 1;
        }

        public LocationItem(String locationName, int kind) {

            this.locationName = locationName;
            this.kind = kind;
            this.sizeX = 1;
            this.sizeY = 1;
        }

        public LocationItem(String locationName, int sizeX, int sizeY, int kind, float angle) {

            this.locationName = locationName;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.kind = kind;
            this.angle = angle;
        }
    }
}
