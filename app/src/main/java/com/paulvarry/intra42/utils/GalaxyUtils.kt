package com.paulvarry.intra42.utils

import android.content.Context
import android.util.SparseArray
import com.google.gson.reflect.TypeToken
import com.paulvarry.intra42.api.ServiceGenerator
import com.paulvarry.intra42.api.model.ProjectsUsers
import com.paulvarry.intra42.api.model.Users
import com.paulvarry.intra42.ui.galaxy.model.CircleHolder
import com.paulvarry.intra42.ui.galaxy.model.ProjectDataIntra

class GalaxyUtils {
    companion object {
        private fun getResName(cursus: Int, campus: Int): String {
            return "project_data_cursus_" + cursus + "_campus_" + campus
        }

        private fun getResId(context: Context?, resName: String): Int? {
            if (context == null) return null
            val resources = context.resources ?: return null
            return resources.getIdentifier(resName, "raw", context.packageName)
        }

        fun getDataFromApp(context: Context, cursus: Int, campus: Int, user: Users?): List<ProjectDataIntra>? {
            val gson = ServiceGenerator.getGson()
            val list: List<ProjectDataIntra>
            val listType = object : TypeToken<ArrayList<ProjectDataIntra?>?>() {}.type
            val resId: Int = getResId(context, getResName(cursus, campus)) ?: return null
            if (resId == 0) return null
            val ins = context.resources.openRawResource(resId)
            val data = Tools.readTextFile(ins)
            list = gson.fromJson(data, listType)

            if (user != null) {
                val project = SparseArray<ProjectsUsers>()
                for (p in user.projectsUsers) {
                    project.put(p.project.id, p)
                }
                for (p in list) {
                    val projectsUsers = project[p.projectId]
                    if (projectsUsers == null) {
                        p.state = null
                        p.finalMark = null
                        continue
                    }
                    p.finalMark = projectsUsers.finalMark
                    if (projectsUsers.status == ProjectsUsers.Status.FINISHED) {
                        if (projectsUsers.validated != null && projectsUsers.validated!!) p.state = ProjectDataIntra.State.DONE else p.state = ProjectDataIntra.State.FAIL
                    } else if (projectsUsers.status == ProjectsUsers.Status.CREATING_GROUP || projectsUsers.status == ProjectsUsers.Status.IN_PROGRESS || projectsUsers.status == ProjectsUsers.Status.SEARCHING_A_GROUP || projectsUsers.status == ProjectsUsers.Status.WAITING_FOR_CORRECTION || projectsUsers.status == ProjectsUsers.Status.WAITING_TO_START) p.state = ProjectDataIntra.State.IN_PROGRESS else p.state = ProjectDataIntra.State.IN_PROGRESS
                }
            }
            return list
        }

        fun getHack21(): CircleHolder {
            return ServiceGenerator.getGson().fromJson(
                    "{\"radius\":1000,\"circles\":[{\"projects\":[{\"kind\":\"inner_solo\",\"id\":1314}],\"angle\":0},{\"projects\":[{\"kind\":\"inner_solo\",\"id\":1327},{\"kind\":\"inner_solo\",\"id\":1316},{\"kind\":\"inner_solo\",\"id\":1318}],\"angle\":0},{\"projects\":[{\"kind\":\"inner_linked\",\"ids\":[1315,1326]},{\"kind\":\"inner_solo\",\"id\":1328},{\"kind\":\"inner_exam\",\"id\":1320}],\"angle\":75},{\"projects\":[{\"kind\":\"inner_solo\",\"id\":1329},{\"kind\":\"inner_solo\",\"id\":1331},{\"kind\":\"inner_solo\",\"id\":1330},{\"kind\":\"inner_exam\",\"id\":1321}],\"angle\":-65},{\"projects\":[{\"kind\":\"inner_planet\",\"id\":1346,\"satellites\":[1338,1339,1340,1341,1342,1343,1344,1345]},{\"kind\":\"inner_solo\",\"id\":1334},{\"kind\":\"inner_exam\",\"id\":1322}],\"angle\":150},{\"projects\":[{\"kind\":\"inner_linked\",\"ids\":[1336,1332]},{\"kind\":\"inner_solo\",\"id\":1335},{\"kind\":\"inner_exam\",\"id\":1323}],\"angle\":-150},{\"projects\":[{\"kind\":\"inner_big\",\"id\":1337},{\"kind\":\"inner_exam\",\"id\":1324}],\"angle\":50}],\"projects\":{}}",
                    CircleHolder::class.java)
        }
    }
}
