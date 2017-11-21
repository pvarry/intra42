package com.paulvarry.intra42.utils.clusterMap;

public class ClusterMapFremontE1Z3 {

    public static LocationItem[][] getFremontCluster1Zone3() {
        String clusterID = "e1z3";

        final int cluster[][] = {/*
                 A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y  */
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //1
                {1, 0, 0, 0, 2, 2, 1, 1, 1, 1, 1, 1, 1}, //2
                {1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1}, //3
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //4
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 2, 2, 1}, //5
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //6
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, //7
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //8
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //9
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //10
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //11
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //12
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, //13
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //14
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //15
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //16
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //17
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //18
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, //19
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //20
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //21
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //22
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //23
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //24
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1}, //25
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //26
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1}, //27
                {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //28
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1}, //29
                {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 2, 1}, //30
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}  //31
        };

        final String clusterLocationName[][] = {/*
                 A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y  */
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, "TBD", "r1p6", "TBD", null, null, null, null, null, null, null, null, null},
                {null, "r1p10", "TBD", "TBD", "TBD", "TBD", null, null, null, null, null, null, null},
                {null, "r1p12", null, null, null, null, null, null, null, null, null, null, null},
                {null, "r1p14", "TBD", "TBD", "TBD", "TBD", null, "r2p1", "TBD", "TBD", null, null, null},
                {null, "TBD", "r1p18", "r1p20", "r1p22", "r1p24", null, "r2p2", "TBD", "r2p6", "r2p8", "TBD", null},
                {null, null, null, null, null, null, null, null, null, null, null, "r2p11", null},
                {null, "TBD", "r3p8", "r3p6", "r3p4", "r3p2", null, "r2p22", "r2p20", "r2p18", "r2p16", "r2p13", null},
                {null, "TBD", "r3p7", "TBD", "r3p3", "r3p1", null, "r2p23", "r2p21", "r2p19", "r2p17", "r2p15", null},
                {null, "TBD", null, null, null, null, null, null, null, null, null, null, null},
                {null, "TBD", "r3p19", "r3p21", "r3p23", "r3p25", null, "TBD", "r4p3", "TBD", "r4p7", "TBD", null},
                {null, "TBD", "TBD", "TBD", "TBD", "r3p26", null, "r4p2", "r4p4", "r4p6", "r4p8", "r4p11", null},
                {null, null, null, null, null, null, null, null, null, null, null, "r4p13", null},
                {null, "r5p10", "r5p8", "TBD", "r5p4", "TBD", null, "TBD", "TBD", "TBD", "r4p18", "r4p15", null},
                {null, "TBD", "r5p7", "r5p5", "r5p3", "r5p1", null, "r4p25", "TBD", "TBD", "TBD", "r4p17", null},
                {null, "TBD", null, null, null, null, null, null, null, null, null, null, null},
                {null, "r5p16", "r5p19", "r5p21", "r5p23", "r5p25", null, "TBD", "TBD", "r6p5", "TBD", "r6p9", null},
                {null, "TBD", "r5p20", "r5p22", "r5p24", "TBD", null, "r6p2", "r6p4", "r6p6", "r6p8", "r6p11", null},
                {null, null, null, null, null, null, null, null, null, null, null, "r6p13", null},
                {null, "TBD", "r7p8", "TBD", "r7p4", "TBD", null, "r6p24", "r6p22", "r6p20", "r6p18", "TBD", null},
                {null, "TBD", "TBD", "TBD", "TBD", "TBD", null, "TBD", "TBD", "TBD", "TBD", "TBD", null},
                {null, "TBD", null, null, null, null, null, null, null, null, null, null, null},
                {null, "TBD", "TBD", "TBD", "TBD", "TBD", null, "TBD", "r8p4", "r8p6", "TBD", "r8p10", null},
                {null, "r7p17", "TBD", "r7p21", "r7p23", "TBD", null, "TBD", "TBD", "r8p5", "TBD", "TBD", null},
                {null, null, null, null, null, null, null, null, null, null, null, "r8p14", null},
                {null, "r9p9", "TBD", "TBD", "TBD", "TBD", null, "TBD", "r8p23", "TBD", "TBD", "r8p16", null},
                {null, "TBD", "TBD", "TBD", "TBD", "TBD", null, "TBD", "TBD", "TBD", "TBD", "r8p18", null},
                {null, "TBD", null, null, null, null, null, null, null, null, null, null, null},
                {null, "TBD", "TBD", "TBD", "TBD", "TBD", null, "TBD", "TBD", "TBD", "TBD", null, null},
                {null, "r9p17", "r9p19", "TBD", "r9p23", "r9p25", null, "TBD", "TBD", "TBD", "TBD", null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
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

                map[r][p].sizeX = 1f;
                map[r][p].sizeY = 1f;
            }
        }

        return map;
    }
}
