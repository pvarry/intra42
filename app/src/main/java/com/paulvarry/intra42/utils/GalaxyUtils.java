package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.ProjectDataIntra;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.Users;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class GalaxyUtils {

    private static String getResName(int cursus, int campus) {

        return "project_data_cursus_" + cursus + "_campus_" + campus;
    }

    private static int getResId(Context context, String resName) {

        if (context == null)
            return 0;
        Resources resources = context.getResources();
        if (resources == null)
            return 0;
        return resources.getIdentifier(resName, "raw", context.getPackageName());
    }

    public static List<ProjectDataIntra> getDataFromApp(@NonNull Context context, int cursus, int campus, @Nullable Users user) {
        Gson gson = ServiceGenerator.getGson();
        List<ProjectDataIntra> list;
        Type listType = new TypeToken<ArrayList<ProjectDataIntra>>() {
        }.getType();

        int resId = getResId(context, getResName(cursus, campus));

        if (resId == 0)
            return null;

        InputStream ins = context.getResources().openRawResource(resId);
        String data = readTextFile(ins);
        list = gson.fromJson(data, listType);

        if (user != null) {
            SparseArray<ProjectsUsers> project = new SparseArray<>();
            for (ProjectsUsers p : user.projectsUsers) {
                project.put(p.project.id, p);
            }

            for (ProjectDataIntra p : list) {
                ProjectsUsers projectsUsers = project.get(p.projectId);

                if (projectsUsers == null) {
                    p.state = null;
                    p.finalMark = null;
                    continue;
                }
                p.finalMark = projectsUsers.finalMark;
                if (projectsUsers.status.equals(ProjectUserStatus.FINISHED)) {
                    if (projectsUsers.validated != null && projectsUsers.validated)
                        p.state = ProjectDataIntra.State.DONE;
                    else
                        p.state = ProjectDataIntra.State.FAIL;
                } else if (projectsUsers.status.contentEquals(ProjectUserStatus.CREATING_GROUP) ||
                        projectsUsers.status.contentEquals(ProjectUserStatus.IN_PROGRESS) ||
                        projectsUsers.status.contentEquals(ProjectUserStatus.SEARCHING_A_GROUP) ||
                        projectsUsers.status.contentEquals(ProjectUserStatus.WAITING_FOR_CORRECTION) ||
                        projectsUsers.status.contentEquals(ProjectUserStatus.WAITING_TO_START))
                    p.state = ProjectDataIntra.State.IN_PROGRESS;
                else
                    p.state = ProjectDataIntra.State.IN_PROGRESS;

            }
        }
        return list;
    }

    private static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }
}
