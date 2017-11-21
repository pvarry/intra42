package com.paulvarry.intra42.utils.clusterMap;

public class ClusterMapFremontE1Z2 {

    public static LocationItem[][] getFremontCluster1Zone2() {
        String clusterID = "e1z2";

        final int cluster[][] = {/*
                 A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y  */
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //1
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //2
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //3
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 1}, //4
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 2, 0, 1}, //5
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //6
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //7
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //8
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //9
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //10
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //11
                {1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //12
                {1, 1, 1, 1, 1, 1, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1}, //13
                {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //14
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //15
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //16
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1}, //17
                {1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //18
                {1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //19
                {1, 2, 2, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //20
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1}, //21
                {1, 1, 2, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //22
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //23
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //24
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //25
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //26
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //27
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //28
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //29
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //30
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //31
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //32
                {1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //33
                {1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //34
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //35
                {1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //36
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1}, //37
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //38
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //39
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1}, //40
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 1}, //41
                {1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //42
                {1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //43
                {1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 1}, //44
                {1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 1}, //45
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //46
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //47
                {1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //48
                {1, 0, 2, 0, 0, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //49
                {1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //50
                {1, 2, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1}, //51
                {1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 2, 1}, //52
                {1, 1, 1, 1, 1, 0, 2, 0, 1, 0, 2, 0, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //53
                {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //54
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //55
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1}, //56
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 2, 0, 1}, //57
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 2, 2, 0, 1, 1, 1, 0, 1, 1}, //58
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, //59
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1}, //60
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 0, 1}, //61
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1}, //62
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}  //63
        };

        final String clusterLocationName[][] = {/*
                 A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y  */
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}, //1
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p1", null, null}, //2
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p3", null, null}, //3
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p13", null, null, null, "r1p5", null, null}, //4
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p12", null, "r1p11", "r1p9", "r1p7", null, "r1p4", null}, //5
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r1p10", null, null, null, "r1p6", null, null}, //6
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p16", null, null, null, null, null, null, null, null, null, null}, //7
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p14", null, null, null, "r2p6", null, null, null, "r2p4", null, null}, //8
                {null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p13", null, "r2p12", "r2p10", "r2p8", null, "r2p7", "r2p5", "r2p3", null, "r2p2", null}, //9
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r2p11", null, null, null, "r2p9", null, null, null, "r2p1", null, null}, //10
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}, //11
                {null, null, null, null, null, null, null, null, null, null, "r4p6", null, null, null, "r3p34", null, null, null, "r3p30", null, null, null, "r3p2", null, null}, //12
                {null, null, null, null, null, null, null, "r4p3", "r4p5", "r4p7", null, "r4p8", null, "r3p36", null, "r3p35", "r3p33", "r3p31", null, "r3p28", null, "r3p4", null, "r3p1", null}, //13
                {null, null, null, null, null, null, "r4p1", null, null, null, "r4p9", null, null, null, "r3p37", null, null, null, "r3p29", null, null, null, "r3p3", null, null}, //14
                {null, null, null, null, null, null, null, null, null, null, "r4p11", null, null, null, "r3p39", null, null, null, "r3p27", null, null, null, "r3p5", null, null}, //15
                {null, null, "r4p25", null, null, null, "r4p21", null, null, null, "r4p13", null, null, null, "r3p41", null, null, null, "r3p25", null, null, null, "r3p7", null, null}, //16
                {null, "r4p27", null, "r4p26", "r4p24", "r4p22", null, "r4p19", "r4p17", "r4p15", null, "r4p12", null, "r3p40", null, "r3p43", null, "r3p23", null, "r3p26", null, "r3p6", null, "r3p9", null}, //17
                {null, null, "r4p28", null, null, null, "r4p20", null, null, null, "r4p14", null, null, null, "r3p42", null, null, null, "r3p24", null, null, null, "r3p8", null, null}, //18
                {null, null, "r4p30", null, null, null, null, null, null, null, null, null, null, null, "r3p44", null, null, null, "r3p22", null, null, null, "r3p10", null, null}, //19
                {null, null, null, null, null, null, "r4p38", null, null, null, null, null, null, null, "r3p46", null, null, null, "r3p20", null, null, null, "r3p12", null, null}, //20
                {null, "r4p31", null, "r4p32", "r4p34", "r4p36", null, "p4p37", "r4p39", null, null, "r4p40", null, "r3p48", null, "r3p45", null, "r3p19", null, "r3p18", "r3p16", "r3p14", null, "r3p11", null}, //21
                {null, null, null, null, null, null, "r4p35", null, null, null, "TBD", null, null, null, "r3p47", null, null, null, "r3p17", null, null, null, "r3p13", null, null}, //22
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}, //23
                {null, null, null, null, null, null, null, null, null, null, "r6p2", null, null, null, "r4p48", null, null, null, "r4p40", null, null, null, "r5p36", null, null}, //24
                {null, null, null, null, null, null, null, null, null, "r6p4", null, "r6p1", null, "r5p47", null, "r4p46", "r4p44", "r5p42", null, "r5p41", "r5p39", "r5p37", null, "r5p34", null}, //25
                {null, null, null, null, null, null, null, null, null, null, "r6p3", null, null, null, "r5p45", null, null, null, "r5p43", null, null, null, "r5p35", null, null}, //26
                {null, null, null, null, null, null, null, null, null, null, "r6p5", null, null, null, null, null, null, null, null, null, null, null, "r5p33", null, null}, //27
                {null, null, null, null, null, null, null, null, null, null, "r6p7", null, null, null, "r5p19", null, null, null, "r5p23", null, null, null, "r5p31", null, null}, //28
                {null, null, null, null, null, null, null, null, null, "r6p6", null, "r6p9", null, "r5p17", null, "r5p20", "r5p22", "r5p24", null, "r5p25", "r5p27", "r5p29", null, "r5p30", null}, //29
                {null, null, null, null, null, null, null, null, null, null, "r6p8", null, null, null, "r5p18", null, null, null, "r5p26", null, null, null, "r5p28", null, null}, //30
                {null, null, null, null, null, null, null, null, null, null, "r6p10", null, null, null, "r5p16", null, null, null, null, null, null, null, null, null, null}, //31
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r5p14", null, null, null, "r5p6", null, null, null, "r5p4", null, null}, //32
                {null, null, null, null, null, null, null, null, "r6p14", null, null, "r6p11", null, "r5p13", null, "r5p12", "r5p10", "r5p8", null, "r5p7", "r5p5", "r5p3", null, "r5p2", null}, //33
                {null, null, null, null, null, null, null, null, null, null, "r6p13", null, null, null, "r5p11", null, null, null, "r5p9", null, null, null, "r5p1", null, null}, //34
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}, //35
                {null, null, null, null, null, null, null, null, null, null, "r8p37", null, null, null, "r7p34", null, null, null, "r7p30", null, null, null, "r7p2", null, null}, //36
                {null, null, null, null, null, null, null, null, "r8p40", "r8p38", null, "r8p35", null, "r7p36", null, "r7p35", "r7p33", "r7p31", null, "r7p28", null, "r7p4", null, "r7p1", null}, //37
                {null, null, null, null, null, null, null, null, null, null, "r8p36", null, null, null, "r7p37", null, null, null, "r7p29", null, null, null, "r7p3", null, null}, //38
                {null, null, null, null, null, null, null, null, null, null, "r8p38", null, null, null, "r7p39", null, null, null, "r7p27", null, null, null, "r7p5", null, null}, //39
                {null, null, "r8p20", null, null, null, "r8p24", null, null, null, "r8p32", null, null, null, "r7p41", null, null, null, "r7p25", null, null, null, "r7p7", null, null}, //40
                {null, "r8p18", null, "r8p21", "r8p23", "r8p25", null, "r8p26", "r8p28", "p8p30", null, "r8p31", null, "r7p40", null, "r7p43", null, "r7p23", null, "r7p26", null, "r7p6", null, "r7p9", null}, //41
                {null, null, "r8p19", null, null, null, "r8p27", null, null, null, "r8p29", null, null, null, "r7p42", null, null, null, "r7p24", null, null, null, "r7p8", null, null}, //42
                {null, null, "r8p17", null, null, null, null, null, null, null, null, null, null, null, "r7p44", null, null, null, "rp22", null, null, null, "r7p10", null, null}, //43
                {null, null, "r8p15", null, null, null, "r8p7", null, null, null, null, null, null, null, "r7p46", null, null, null, "r7p20", null, null, null, "r7p12", null, null}, //44
                {null, "r8p14", null, "r8p13", "r8p11", "r8p9", null, "r8p8", "r8p6", "r8p4", null, "TBD", null, "r7p48", null, "r7p45", null, "r7p19", null, "r7p18", "r7p16", "r7p14", null, "r7p11", null}, //45
                {null, null, "r8p12", null, null, null, "r8p10", null, null, null, "r8p2", null, null, null, "r7p47", null, null, null, "r7p17", null, null, null, "r7p13", null, null}, //46
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}, //47
                {null, null, "TBD", null, null, null, "TBD", null, null, null, "r10p2", null, null, null, "r9p48", null, null, null, "r9p40", null, null, null, "r9p36", null, null}, //48
                {null, "TBD", null, "TBD", "TBD", "TBD", null, "TBD", null, "r10p4", null, "r10p1", null, "r9p47", null, "r9p46", "r9p44", "r9p42", null, "r9p41", "r9p29", "r9p37", null, "r9p34", null}, //49
                {null, null, "TBD", null, null, null, "TBD", null, null, null, "r10p3", null, null, null, "r9p45", null, null, null, "r9p43", null, null, null, "r9p35", null, null}, //50
                {null, null, "TBD", null, null, null, "TBD", null, null, null, "r10p5", null, null, null, null, null, null, null, null, null, null, null, "r9p33", null, null}, //51
                {null, null, null, null, null, null, "TBD", null, null, null, "r10p7", null, null, null, "r9p19", null, null, null, "r9p23", null, null, null, "r9p31", null, null}, //52
                {null, null, null, null, null, "TBD", null, "TBD", null, "r10p6", null, "r10p9", null, "r9p17", null, "r9p20", "r9p22", "r9p24", null, "r9p25", "r9p27", "r9p29", null, "r9p30", null}, //53
                {null, null, null, null, null, null, "TBD", null, null, null, "r10p8", null, null, null, "r9p18", null, null, null, "r9p26", null, null, null, "r9p28", null, null}, //54
                {null, null, null, null, null, null, null, null, null, null, "r10p10", null, null, null, "r9p16", null, null, null, null, null, null, null, null, null, null}, //55
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r9p14", null, null, null, "r9p6", null, null, null, "r9p4", null, null}, //56
                {null, null, null, null, null, null, null, null, null, null, null, null, null, "r9p13", null, "r9p12", "r9p10", "r9p8", null, "r9p7", "r9p5", "r9p3", null, "r9p2", null}, //57
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "r9p11", null, null, null, "r9p9", null, null, null, "r9p1", null, null}, //58
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}, //59
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "TBD", null, null}, //60
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "TBD", null, "TBD", null}, //61
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "TBD", null, null}, //62
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}  //63
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
