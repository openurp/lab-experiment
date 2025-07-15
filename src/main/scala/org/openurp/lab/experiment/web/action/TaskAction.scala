/*
 * Copyright (C) 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.lab.experiment.web.action

import org.beangle.data.dao.OqlBuilder
import org.beangle.webmvc.support.action.RestfulAction
import org.beangle.webmvc.view.View
import org.openurp.base.edu.model.{Course, TeachingOffice}
import org.openurp.base.model.{Department, Project, Semester}
import org.openurp.base.resource.model.Laboratory
import org.openurp.code.edu.model.CourseRank
import org.openurp.edu.clazz.model.{Clazz, ClazzActivity}
import org.openurp.edu.course.model.{CourseTask, Syllabus}
import org.openurp.lab.experiment.model.{LabExperiment, LabTask}
import org.openurp.starter.web.support.ProjectSupport

/** 课程任务
 */
class TaskAction extends RestfulAction[LabTask], ProjectSupport {

  override protected def indexSetting(): Unit = {
    super.indexSetting()

    given project: Project = getProject

    val departs = getDeparts
    put("departments", departs)
    put("project", project)
    put("semester", getSemester)
    forward()
  }

  def autoCreate(): View = {
    given project: Project = getProject

    val semester = entityDao.get(classOf[Semester], getIntId("task.semester"))
    val ctasks = entityDao.findBy(classOf[CourseTask], "course.project" -> project, "semester" -> semester).groupBy(_.course)
    val tasks = entityDao.findBy(classOf[LabTask], "course.project" -> project, "semester" -> semester).groupBy(_.course)

    //查询所有的实验室
    val labs = entityDao.findBy(classOf[Laboratory], "school", project.school).filter(_.room.nonEmpty)
    val roomToLabs = labs.map(x => (x.room.get, x)).toMap
    val rooms = labs.map(_.room.get).toSet

    if (rooms.nonEmpty) {
      val query = OqlBuilder.from[Course](classOf[ClazzActivity].getName, "activity")
      query.where("activity.clazz.project=:project", project)
      query.where("activity.clazz.semester=:semester", semester)
      query.join("activity.rooms", "room")
      query.where("room in(:rooms)", rooms)
      query.select("distinct activity.clazz.course")
      val expCourses = entityDao.search(query)
      expCourses foreach { course =>
        val clazzes = entityDao.findBy(classOf[Clazz], "project" -> project, "semester" -> semester, "course" -> course)
        val clazzRooms = clazzes.flatMap(_.schedule.activities.flatMap(_.rooms)).toSet.filter(x => roomToLabs.contains(x))
        val clazzLabs = clazzRooms.map(x => roomToLabs(x))
        val cTask = ctasks.get(course) match {
          case None =>
            val ct = new CourseTask(course, clazzes.head.teachDepart, semester, clazzes.head.courseType)
            entityDao.saveOrUpdate(ct)
            ct
          case Some(t) => t.head
        }
        val task = tasks.get(course) match {
          case None =>
            val t = new LabTask(course, semester, cTask.department)
            t.required = Some(true)
            t
          case Some(t) => t.head
        }
        task.director = cTask.director
        task.stdCount = clazzes.map(_.enrollment.stdCount).sum
        task.rank = clazzes.head.courseType.rank.getOrElse(new CourseRank(CourseRank.Selective))
        task.clazzCount = clazzes.size
        task.labs.clear()
        task.labs.addAll(clazzLabs)
        //FIXME 增补或者对比大纲中的实验
        if (task.experiments.isEmpty) {
          val syllabuses = entityDao.findBy(classOf[Syllabus], "course", course).filter(_.within(semester.beginOn))
          val exps = syllabuses.flatMap(_.experiments.sortBy(_.idx).map(_.experiment))
          exps foreach { exp =>
            val labExp = new LabExperiment(task.experiments.size + 1, task, exp)
            task.experiments.addOne(labExp)
          }
        }
        if (task.required.isEmpty) task.required = Some(true)
        task.expCount = task.experiments.size
        entityDao.saveOrUpdate(task)
      }
    }
    redirect("search", "初始化成功")
  }

  override protected def simpleEntityName: String = "task"

  override protected def getQueryBuilder: OqlBuilder[LabTask] = {
    val query = super.getQueryBuilder
    query.where("task.course.project=:project", getProject)
    queryByDepart(query, "task.department")
    query
  }

  override protected def editSetting(task: LabTask): Unit = {
    given project: Project = getProject

    put("project", project)
    super.editSetting(task)
  }

  private def getOffices(project: Project, departs: Seq[Department]): Seq[TeachingOffice] = {
    val query = OqlBuilder.from(classOf[TeachingOffice], "o")
    query.where("o.project=:project", project)
    query.where("o.department in(:departs)", departs)
    query.orderBy("o.name")
    entityDao.search(query)
  }

}
