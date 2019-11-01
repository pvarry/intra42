package com.paulvarry.intra42.ui.galaxy

import android.graphics.Point
import android.util.SparseArray
import com.paulvarry.intra42.ui.galaxy.model.CircleHolder
import com.paulvarry.intra42.ui.galaxy.model.CircleToDraw
import com.paulvarry.intra42.ui.galaxy.model.GalaxyCircle
import com.paulvarry.intra42.ui.galaxy.model.ProjectDataIntra
import com.paulvarry.intra42.utils.GalaxyUtils
import kotlin.math.*

object Hack {

    private val hack: SparseArray<CircleHolder> = SparseArray<CircleHolder>()
            .apply {
                put(21, GalaxyUtils.getHack21())
            }

    fun computeData(projects: List<ProjectDataIntra>?, cursusId: Int): Pair<List<ProjectDataIntra>, MutableList<CircleToDraw>>? {
        if (projects == null)
            return null
        val hack: CircleHolder? = hack.get(cursusId, null)

        val computedCircles = mutableListOf<CircleToDraw>()
        val computedProjects = SparseArray<ComputationData>()
        hack?.circles?.forEachIndexed { circleIndex, galaxyCircle ->
            val circleToDraw = CircleToDraw(galaxyCircle)
            computedCircles.add(circleToDraw)

            circleToDraw.radius = hack.radius * circleIndex / (hack.circles.size - 1)
            computeCircleLayer(galaxyCircle, circleToDraw) { index: Int, kind: ProjectDataIntra.Kind, location: Point ->
                computedProjects.put(index, ComputationData(kind, location, circleIndex))
            }
        }

        // Post compute
        val lastCircle = computedCircles.lastOrNull()
        projects.forEach { project ->
            postComputeProject(project, computedProjects, computedCircles)
            lastCircle?.let { circle -> project.by = postComputeProjectBy(circle, project) }
        }

        return Pair(projects, computedCircles)
    }

    /**
     * Compute all project in one circle
     */
    private fun computeCircleLayer(galaxyCircle: GalaxyCircle, circleToDraw: CircleToDraw, function: (index: Int, kind: ProjectDataIntra.Kind, Point) -> Unit) {
        galaxyCircle.projects.forEachIndexed { projectsIndex, project ->
            val position = computePositionSingle(Galaxy.centerPoint, circleToDraw.radius, galaxyCircle.angle, galaxyCircle.projects.size, projectsIndex)
            when (project.kind) {
                ProjectDataIntra.Kind.INNER_PLANET -> {
                    project.satellites.forEachIndexed { index, i ->
                        val c = computePositionSingle(position, 70, 0, project.satellites.size, index)
                        function.invoke(i, ProjectDataIntra.Kind.INNER_SATELLITE, c)
                    }
                    function.invoke(project.id, project.kind, position)
                }
                ProjectDataIntra.Kind.INNER_LINKED -> {
                    val groupPositions = computePositionGroup(position, project.ids.size, 50, Galaxy.centerPoint, circleToDraw.radius)
                    if (groupPositions.isNotEmpty())
                        groupPositions.forEachIndexed { index, point ->
                            function.invoke(project.ids[index], project.kind, point)
                        }
                }
                else -> function.invoke(project.id, project.kind, position)
            }
        }
    }

    private fun postComputeProject(project: ProjectDataIntra, computedProjects: SparseArray<ComputationData>, computedCircles: MutableList<CircleToDraw>) {
        computedProjects.get(project.projectId)?.let {
            project.x = it.position.x
            project.y = it.position.y
            project.by = null
            project.kind = it.kind

            computedCircles.getOrNull(it.circleIndex)?.let { draw ->
                if (draw.state?.overridedBy(project.state!!) != false)
                    draw.state = project.state
            }
        }
    }

    private fun postComputeProjectBy(circle: CircleToDraw, project: ProjectDataIntra): List<ProjectDataIntra.By>? {
        return project.by?.map { by ->
            if (by.parentId == 1324) {
                val target = by.points?.find {
                    if (it[0] > 2990 && it[0] < 3010 && it[1] > 2990 && it[1] < 3010)
                        return@find false
                    return@find true
                }

                if (target != null) {
                    val vX = target[0] - 3000f // 3000 is center
                    val vY = target[1] - 3000f
                    val magV = sqrt(vX * vX + vY * vY)
                    val aX = 3000f + vX / magV * circle.radius
                    val aY = 3000f + vY / magV * circle.radius
                    by.points = listOf(target, listOf(aX, aY))
                }
            }
            by
        }
    }

    /**
     * This function will compute the real position of a point in the graph, this is used to equally
     * place point on a circle.
     *
     * @param center Center position of the circle.
     * @param radius Radius of the circle.
     * @param angleShift Angle in degree.
     * @param length Total number of item on the circle.
     * @param position Position of this item on the total.
     * @return The position of this point at the specified position on the graph.
     */
    private fun computePositionSingle(center: Point, radius: Int, angleShift: Int, length: Int, position: Int): Point {
        val shift = angleShift * Math.PI / 180
        val pos = 2 * Math.PI * position / length
        val x = sin(shift + pos) * radius + center.x
        val y = cos(shift + pos) * radius + center.y
        return Point(x.roundToInt(), y.roundToInt())
    }

    /**
     * This function will compute the real position of group of points in the graph, this will fill
     * a curve with a predetermined number of points, all the point are side by side ans will be
     * around the target position.
     *
     * @param targetLocation Target location of this group of point.
     * @param length Number of point to compute, the result will be a list of this length.
     * @param itemWidth Space between each item.
     * @param curveCenter All points will be placed on a circle, with this arg is the center.
     * @param curveRadius Radius of the filled curve.
     * @return The list of desired locations.
     */
    private fun computePositionGroup(targetLocation: Point, length: Int, itemWidth: Int, curveCenter: Point, curveRadius: Int): List<Point> {
        if (length <= 0)
            return emptyList()
        if (length == 1)
            return listOf(targetLocation)

        val shift = sqrt(2 * curveRadius * (curveRadius - sqrt((curveRadius * curveRadius - itemWidth * itemWidth).toDouble())))
        val output = mutableListOf<Point>()
        var first = targetLocation
        var second = targetLocation
        var counter = 0
        val oneSide = floor(length / 2.0).toInt()
        if (length % 2 == 1)
            output.add(oneSide, targetLocation)
        else {
            val h = split(targetLocation, shift, curveCenter, curveRadius)
            first = h.first
            second = h.second
            output.add(oneSide - 1, first)
            output.add(oneSide, second)
            counter++
        }
        while (counter < oneSide) {
            first = split(first, 2 * shift, curveCenter, curveRadius).first
            second = split(second, 2 * shift, curveCenter, curveRadius).second
            output.add(oneSide - 1 - counter, first)
            output.add(oneSide + counter + length % 2, second)
            counter++
        }
        return output
    }

    private fun split(baseLocation: Point, shift: Double, curveCenter: Point, spaceSize: Int): Pair<Point, Point> {
        val r = baseLocation.x
        val a = curveCenter.x
        val s = baseLocation.y
        val b = curveCenter.y
        val l = a - r
        val u = b - s
        val c = sqrt((l * l + u * u).toDouble())
        if (!(c > shift + spaceSize || c < abs(shift - spaceSize) || 0.0 == c && shift == spaceSize.toDouble())) {
            val d = (shift * shift - spaceSize * spaceSize + c * c) / (2 * c)
            val f = sqrt(shift * shift - d * d)
            val p = r + d * l / c
            val m = s + d * u / c
            val g = p + f * u / c
            val v = p - f * u / c
            val y = m - f * l / c
            val z = m + f * l / c
            return Pair(
                    Point(g.roundToInt(), y.roundToInt()),
                    Point(v.roundToInt(), z.roundToInt()))
        }
        throw  RuntimeException("Problem will computing graph position")
    }

    class ComputationData(
            val kind: ProjectDataIntra.Kind,
            val position: Point,
            val circleIndex: Int
    )
}
