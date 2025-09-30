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

import org.beangle.commons.bean.orderings.PropertyOrdering
import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.ems.app.Ems
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.beangle.webmvc.view.View
import org.openurp.base.edu.model.{Course, Experiment}
import org.openurp.base.hr.model.Teacher
import org.openurp.base.model.{Department, Project, Semester}
import org.openurp.base.resource.model.Laboratory
import org.openurp.code.edu.model.{CourseRank, TeachingNature}
import org.openurp.edu.clazz.model.{Clazz, ClazzActivity}
import org.openurp.edu.course.model.CourseTask
import org.openurp.lab.experiment.model.{LabExperiment, LabTask}
import org.openurp.lab.experiment.web.helper.SyllabusHelper
import org.openurp.starter.web.support.ProjectSupport

/** 课程任务
 */
class TaskAction extends RestfulAction[LabTask], ProjectSupport, ExportSupport[LabTask] {

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

    val theoryNature = new TeachingNature(TeachingNature.Theory)
    val practiceNature = new TeachingNature(TeachingNature.Practice)
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
            t.required = true
            t
          case Some(t) => t.head
        }
        task.director = cTask.director
        task.office = cTask.office
        if (!cTask.syllabusRequired) {
          task.required = false
          if (task.remark.isEmpty) {
            task.remark = Some("无大纲要求")
          }
        }

        task.stdCount = clazzes.map(_.enrollment.stdCount).sum
        task.rank = clazzes.head.courseType.rank.getOrElse(new CourseRank(CourseRank.Selective))
        task.clazzCount = clazzes.size
        task.labs.clear()
        task.labs.addAll(clazzLabs)
        val syllabuses = new SyllabusHelper(entityDao).getSyllabus(course, semester)
        //FIXME 增补或者对比大纲中的实验
        if (task.experiments.isEmpty) {
          syllabuses foreach { syllabus =>
            val exps = syllabus.experiments.sortBy(_.idx).map(_.experiment)
            exps foreach { exp =>
              val labExp = new LabExperiment(task.experiments.size + 1, task, exp)
              task.experiments.addOne(labExp)
            }
          }
        }
        syllabuses foreach { syllabus =>
          task.nature = syllabus.nature
          task.theoryHours = syllabus.getCreditHours(theoryNature).intValue
          task.practiceHours = syllabus.getCreditHours(practiceNature).intValue
        }
        if (null == task.nature) {
          task.nature = course.nature
          task.theoryHours = course.getJournal(task.semester).getHour(theoryNature).getOrElse(0)
          task.practiceHours = course.getJournal(task.semester).getHour(practiceNature).getOrElse(0)
        }
        task.expCount = task.experiments.size
        task.checkValidated()
        entityDao.saveOrUpdate(task)
      }
    }
    redirect("search", "初始化成功")
  }

  override protected def simpleEntityName: String = "task"

  override protected def getQueryBuilder: OqlBuilder[LabTask] = {
    given project: Project = getProject

    val query = super.getQueryBuilder
    query.where("task.course.project=:project", project)
    getBoolean("hasEmpty") foreach { hasEmpty =>
      if (hasEmpty) {
        query.where("task.required=true")
        query.where("not exists(from task.experiments) or " +
          "exists(from task.experiments as te where te.experiment.discipline is null)")
      } else {
        query.where("task.required = false or exists(from task.experiments) and not exists(from task.experiments as te where te.experiment.discipline is null)")
      }
    }
    queryByDepart(query, "task.department")
    query
  }

  override def search(): View = {
    given project: Project = getProject

    put("teachingNatures", getCodes(classOf[TeachingNature]))

    val departs = getDeparts
    put("departs", departs)

    val tasks = entityDao.search(getQueryBuilder)
    put("tasks", tasks)
    forward()
  }

  override protected def editSetting(task: LabTask): Unit = {
    given project: Project = getProject

    put("project", project)
    super.editSetting(task)
  }

  override protected def saveAndRedirect(task: LabTask): View = {
    task.checkValidated()
    task.expCount = task.experiments.size
    super.saveAndRedirect(task)
  }

  def stat(): View = {
    val project = getProject
    val semester = entityDao.get(classOf[Semester], getIntId("task.semester"))
    //需要修订的总数
    val q = OqlBuilder.from[Array[Any]](classOf[LabTask].getName, "t")
    q.where("t.course.project=:project and t.semester=:semester", project, semester)
    q.groupBy("t.department.id,t.department.code,t.department.name,t.department.shortName")
    q.select("t.department.id,t.department.code,t.department.name,t.department.shortName,count(*),sum(case when t.validated then 1 else 0 end) vcount")
    val taskStats = entityDao.search(q)

    val items = Collections.newBuffer[StatItem]
    taskStats foreach { stat =>
      val entry = Collections.newMap[String, Any]
      val enName = if null == stat(3) then stat(2) else stat(3)
      entry.addAll(Map("id" -> stat(0).toString, "code" -> stat(1).toString, "name" -> stat(2).toString, "shortName" -> enName))
      val item = new StatItem
      item.entry = entry
      item.counters = Seq(stat(4).asInstanceOf[Number], stat(5).asInstanceOf[Number])
      items.addOne(item)
    }

    put("project", project)
    put("semester", semester)
    put("items", items.sorted(PropertyOrdering.by("entry(code)")))
    forward()
  }

  def report(): View = {
    given project: Project = getProject

    val semester = entityDao.get(classOf[Semester], getIntId("task.semester"))
    val departs = getDeparts
    val q = OqlBuilder.from(classOf[LabTask], "task")
    q.where("task.course.project=:project", project)
    q.where("task.semester=:semester", semester)
    queryByDepart(q, "task.department")

    val tasks = entityDao.search(q)
    val departTasks = tasks.groupBy(_.department).map { x => (x._1, x._2.sorted(PropertyOrdering.by("required,course.code"))) }

    val hasEmpties = departTasks.map { case (depart, ts) =>
      val hasEmpty = ts.exists(!_.validated)
      (depart, hasEmpty)
    }
    put("teachingNatures", getCodes(classOf[TeachingNature]))

    put("departTasks", departTasks)
    put("hasEmpties", hasEmpties)
    put("semester", semester)
    forward()
  }

  /** 上报教委的年度报表
   *
   * @return
   */
  def yearReport(): View = {
    val project = getProject
    val semester = entityDao.get(classOf[Semester], getIntId("task.semester"))
    val q = OqlBuilder.from(classOf[LabTask], "task")
    q.where("task.semester.year=:year", semester.year)
    q.where("task.course.project=:project", project)
    q.where("task.required=true and task.validated=true")
    val tasks = entityDao.search(q)
    val experiments = Collections.newMap[String, CourseExperiment]
    tasks foreach { task =>
      task.experiments foreach { te =>
        val ce = new CourseExperiment(task, te.experiment)
        experiments.get(ce.id) match {
          case None => experiments.put(ce.id, ce)
          case Some(e) => e.merge(ce)
        }
      }
    }
    experiments.values.groupBy(_.course) foreach { case (c, cp) =>
      var i = 1
      val iter = cp.iterator
      while (iter.hasNext) {
        val ce = iter.next()
        ce.generateCode(i)
        i += 1
      }
    }
    put("semester", semester)
    put("project", project)
    put("courseExperiments", experiments.values.toSeq.sorted)
    put("ems_api", Ems.api)
    forward()
  }

}

class CourseExperiment extends Ordered[CourseExperiment] {
  def id: String = s"${course.id}_${experiment.id}"

  var code: String = _
  var course: Course = _
  var experiment: Experiment = _
  var director: Option[Teacher] = None
  var department: Department = _
  var rank: CourseRank = _

  var clazzCount: Int = _
  var stdCount: Int = _
  var laboratory: Option[Laboratory] = None

  def generateCode(seq: Int): Unit = {
    this.code = course.code + Strings.leftPad(seq.toString, 2, '0')
  }

  def merge(newer: CourseExperiment): Unit = {
    this.clazzCount += newer.clazzCount
    this.stdCount += newer.stdCount
  }

  def this(task: LabTask, experiment: Experiment) = {
    this()
    this.course = task.course
    this.rank = task.rank
    this.director = task.director
    this.department = task.department
    this.clazzCount = task.clazzCount
    this.stdCount = task.stdCount
    this.experiment = experiment
    this.laboratory = task.labs.headOption
  }

  override def compare(that: CourseExperiment): Int = {
    val rs = this.department.code.compareTo(that.department.code)
    if (rs == 0) {
      this.code.compareTo(that.code)
    } else {
      rs
    }
  }
}

class StatItem {

  var entry: Any = _

  var counters: Seq[Any] = Seq.empty
}

