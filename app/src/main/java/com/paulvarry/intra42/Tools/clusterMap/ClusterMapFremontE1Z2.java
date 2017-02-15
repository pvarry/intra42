package com.paulvarry.intra42.Tools.clusterMap;

public class ClusterMapFremontE1Z2 {

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

}
