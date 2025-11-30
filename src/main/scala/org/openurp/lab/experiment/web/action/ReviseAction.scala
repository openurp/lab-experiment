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

import org.beangle.commons.collection.Collections
import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.security.Securities
import org.beangle.webmvc.support.EntitySupport
import org.beangle.webmvc.support.helper.PopulateHelper
import org.beangle.webmvc.view.View
import org.openurp.base.edu.model.{Course, Experiment}
import org.openurp.base.hr.model.Teacher
import org.openurp.base.model.{Project, Semester}
import org.openurp.code.edu.model.{ExperimentCategory, ExperimentType, Level1Discipline, TeachingNatureCategory}
import org.openurp.edu.clazz.domain.ClazzProvider
import org.openurp.edu.clazz.model.Clazz
import org.openurp.edu.course.model.SyllabusExperiment
import org.openurp.edu.course.service.CourseTaskService
import org.openurp.lab.experiment.model.{LabExperiment, LabTask}
import org.openurp.lab.experiment.web.helper.SyllabusHelper
import org.openurp.starter.web.support.TeacherSupport

import java.time.Instant

class ReviseAction extends TeacherSupport, EntitySupport[Experiment] {

  var courseTaskService: CourseTaskService = _
  var clazzProvider: ClazzProvider = _

  protected override def projectIndex(teacher: Teacher)(using project: Project): View = {
    val semester = getSemester
    put("semester", semester)

    val tasks = getTasks(project, semester, teacher)
    val taskCourses = tasks.map(_.course)

    val clazzCourses = clazzProvider.getClazzes(semester, teacher, project).map(_.course).toSet.toBuffer.subtractAll(taskCourses)

    val query2 = OqlBuilder.from[Course](classOf[Clazz].getName, "c")
    query2.join("c.teachers", "t")
    query2.where("c.semester.beginOn <= :today", semester.beginOn)
    query2.where("t.staff.code=:me", Securities.user)
    query2.select("distinct c.course")
    query2.orderBy("c.course.code")
    val hisCourses = Collections.newBuffer(entityDao.search(query2))
    hisCourses.subtractAll(clazzCourses)
    hisCourses.subtractAll(taskCourses)

    put("taskCourses", taskCourses)
    put("hisCourses", hisCourses)
    put("clazzCourses", clazzCourses)
    put("courses", taskCourses ++ clazzCourses ++ hisCourses)
    put("project", project)
    put("semester", semester)
    forward()
  }

  def course(): View = {
    val course = entityDao.get(classOf[Course], getLongId("course"))
    val semester = entityDao.get(classOf[Semester], getIntId("semester"))
    var experiments: Iterable[Experiment] = entityDao.findBy(classOf[Experiment], "course" -> course)
    val task = getTask(semester, course, getTeacher)
    task foreach { t =>
      val taskExperiments = t.experiments.map(_.experiment).toSet
      experiments = experiments.toBuffer.subtractAll(taskExperiments)
      //检查是否有缺项
      t.expCount = t.experiments.size
      t.checkValidated()
      entityDao.saveOrUpdate(t)
    }
    put("experiments", experiments)
    if (experiments.nonEmpty) {
      val qq = OqlBuilder.from(classOf[Experiment], "e")
      qq.where(s"not exists(from ${classOf[SyllabusExperiment].getName} se where se.experiment=e)")
      qq.where(s"not exists(from ${classOf[LabExperiment].getName} le where le.experiment=e)")
      qq.where("e.id in(:ids)", experiments.map(_.id))
      val orphans = entityDao.search(qq)
      put("orphans", orphans)
    }
    put("course", course)
    val syllabuses = new SyllabusHelper(entityDao).getSyllabus(course, semester)
    put("syllabus", syllabuses)
    put("semester", semester)
    put("task", task)

    task foreach { t =>
      val hasErrorData = t.experiments.exists { le =>
        val e = le.experiment
        !isExpValidated(e)
      }
      put("hasErrorData", t.experiments.isEmpty || hasErrorData)
    }
    forward()
  }

  private def isExpValidated(exp: Experiment): Boolean = {
    exp.discipline.name != "无" && exp.category.name != "无" && exp.groupStdCount > 0 && exp.creditHours > 0
  }

  def edit(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))
    val course = task.course
    val experiment = getLong("experiment.id") match {
      case None => new Experiment(course)
      case Some(id) => entityDao.get(classOf[Experiment], id)
    }
    put("experiment", experiment)
    //顺序进行实验序号
    val idx = task.experiments.find(_.experiment == experiment) match {
      case None => if (task.experiments.isEmpty) 1 else task.experiments.map(_.idx).max + 1
      case Some(le) => le.idx
    }
    put("idx", idx)

    given project: Project = course.project

    put("categories", getCodes(classOf[ExperimentCategory]))
    put("experimentTypes", getCodes(classOf[ExperimentType]))
    put("disciplines", getCodes(classOf[Level1Discipline]))
    put("task", task)

    var maxHours = course.creditHours.toFloat
    val syllabuses = new SyllabusHelper(entityDao).getSyllabus(course, task.semester)
    syllabuses foreach { s =>
      val syllabusHours = s.hours.filter(_.nature.category != TeachingNatureCategory.Theory).map(_.creditHours).sum
      val taskHours = task.experiments.map(_.experiment.creditHours).sum
      maxHours = syllabusHours - taskHours
      if experiment.persisted then maxHours += experiment.creditHours
    }
    put("maxHours", maxHours)
    put("syllabuses", syllabuses)
    put("newExperiment", false)
    if (experiment.persisted) {
      syllabuses foreach { syllabus =>
        if (!syllabus.experiments.map(_.experiment).contains(experiment)) {
          put("newExperiment", true)
        }
      }
    } else {
      put("newExperiment", true)
    }
    forward()
  }

  def batchEdit(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))

    given project: Project = task.course.project

    put("categories", getCodes(classOf[ExperimentCategory]))
    put("experimentTypes", getCodes(classOf[ExperimentType]))
    put("disciplines", getCodes(classOf[Level1Discipline]))
    put("task", task)
    forward()
  }

  def batchUpdate(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))
    getInt("category.id") foreach { categoryId =>
      val category = entityDao.get(classOf[ExperimentCategory], categoryId)
      task.experiments foreach { e => e.experiment.category = category }
    }
    getInt("experimentType.id") foreach { id =>
      val e = entityDao.get(classOf[ExperimentType], id)
      task.experiments foreach (_.experiment.experimentType = e)
    }
    getInt("discipline.id") foreach { id =>
      val e = entityDao.get(classOf[Level1Discipline], id)
      task.experiments foreach (_.experiment.discipline = e)
    }
    getInt("groupStdCount") foreach { groupStdCount =>
      if (groupStdCount > 0) {
        task.experiments foreach (_.experiment.groupStdCount = groupStdCount)
      }
    }
    entityDao.saveOrUpdate(task.experiments.map(_.experiment))

    redirect("course", s"&course.id=${task.course.id}&semester.id=${task.semester.id}", "info.save.success")
  }

  def save(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))
    val experiment = getLong("experiment.id") match {
      case None => new Experiment()
      case Some(id) => entityDao.get(classOf[Experiment], id)
    }
    val exp = PopulateHelper.populate(experiment, entityDao.domain.getEntity(classOf[Experiment]).get, "experiment")
    if (!exp.persisted) {
      exp.code = nextExpCode(task.course)
      exp.beginOn = task.semester.beginOn
    }
    exp.updatedAt = Instant.now
    entityDao.saveOrUpdate(exp)

    addExperiment(exp, task, getBoolean("addToSyllabus", true), getInt("idx"))
    task.checkValidated()
    task.expCount = task.experiments.size
    entityDao.saveOrUpdate(task)
    redirect("course", s"&course.id=${exp.course.id}&semester.id=${task.semester.id}", "info.save.success")
  }

  /** 从这学期的修订任务中删除,但不会彻底删除，会保留在实验库中
   *
   * @return
   */
  def evict(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))
    val experiment = entityDao.get(classOf[Experiment], getLongId("experiment"))
    if (task.director.get.code == Securities.user) {
      //删除大纲中的实验
      val syllabuses = new SyllabusHelper(entityDao).getSyllabus(experiment.course, task.semester)
      syllabuses foreach { syllabus =>
        syllabus.removeExperiment(experiment)
      }
      entityDao.saveOrUpdate(syllabuses)
      //从这学期的修订任务中删除
      task.remove(experiment)
      relocate(task, experiment, -1)
      task.expCount = task.experiments.size

      entityDao.saveOrUpdate(task)
    }
    redirect("course", s"&course.id=${task.course.id}&semester.id=${task.semester.id}", "info.save.success")
  }

  /** 将实验项目添加到修订任务中
   * FIXME 追加实践学时校验
   *
   * @return
   */
  def add(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))
    val experiment = entityDao.get(classOf[Experiment], getLongId("experiment"))
    addExperiment(experiment, task, true, None)
    redirect("course", s"&course.id=${task.course.id}&semester.id=${task.semester.id}", "info.save.success")
  }

  /** 彻底删除实验项目
   *
   * @return
   */
  def remove(): View = {
    val task = entityDao.get(classOf[LabTask], getLongId("task"))
    val experiment = entityDao.get(classOf[Experiment], getLongId("experiment"))

    val qq = OqlBuilder.from(classOf[Experiment], "e")
    qq.where(s"not exists(from ${classOf[SyllabusExperiment].getName} se where se.experiment=e)")
    qq.where(s"not exists(from ${classOf[LabExperiment].getName} le where le.experiment=e)")
    qq.where("e.id = :id", experiment.id)
    val orphans = entityDao.search(qq)
    if (orphans.isEmpty) {
      redirect("course", s"&course.id=${task.course.id}&semester.id=${task.semester.id}", "已有历史数据使用，无法删除")
    } else {
      entityDao.remove(experiment)
      redirect("course", s"&course.id=${task.course.id}&semester.id=${task.semester.id}", "info.remove.success")
    }

  }

  /** 重新定位实验位置，计算其他实验的序号
   *
   * @param task
   * @param exp
   * @param index
   */
  private def relocate(task: LabTask, exp: Experiment, index: Int): Unit = {
    var idx = index
    if (idx < 1) idx = 1
    else if (idx > task.experiments.size) idx = task.experiments.size

    task.experiments.find(_.experiment == exp) match {
      case Some(le) =>
        if (le.idx != idx) {
          //生成一个临时排序集
          val experiments = task.experiments.sortBy(_.idx)
          experiments.subtractOne(le)
          experiments.insert(idx - 1, le)
          var i = 1001
          experiments foreach { le =>
            le.idx = i
            i += 1
          }
          entityDao.saveOrUpdate(task)
          experiments foreach { le => le.idx -= 1000 }
          entityDao.saveOrUpdate(task)
        }
      case None => //may be removal
        var i = 1001
        val experiments = task.experiments.sortBy(_.idx)
        experiments foreach { le =>
          le.idx = i
          i += 1
        }
        entityDao.saveOrUpdate(task)
        experiments foreach { le => le.idx -= 1000 }
        entityDao.saveOrUpdate(task)
    }
  }

  private def addExperiment(exp: Experiment, task: LabTask, addToSyllabus: Boolean, index: Option[Int]): Unit = {
    if (!task.experiments.exists(_.experiment == exp)) {
      task.experiments.addOne(new LabExperiment(-1, task, exp))
      relocate(task, exp, task.experiments.size)
      entityDao.saveOrUpdate(task)
    }
    if (addToSyllabus) {
      val syllabuses = new SyllabusHelper(entityDao).getSyllabus(exp.course, task.semester)
      syllabuses.foreach { s =>
        if (!s.experiments.map(_.experiment).contains(exp)) {
          s.experiments.addOne(new SyllabusExperiment(s, s.experiments.size + 1, exp))
          s.reIndex()
        }
      }
      entityDao.saveOrUpdate(syllabuses)
    }
    //根据编号重新排序
    if (index.nonEmpty) {
      relocate(task, exp, index.get)
    }
  }

  private def getTasks(project: Project, semester: Semester, teacher: Teacher): Seq[LabTask] = {
    val q = OqlBuilder.from(classOf[LabTask], "c")
    q.where("c.course.project=:project", project)
    q.where("c.semester=:semester", semester)
    q.where("c.director=:me", teacher)
    entityDao.search(q)
  }

  private def getTask(semester: Semester, course: Course, teacher: Teacher): Option[LabTask] = {
    val q = OqlBuilder.from(classOf[LabTask], "c")
    q.where("c.semester=:semester", semester)
    q.where("c.course=:course", course)
    q.where("c.director=:me", teacher)
    entityDao.search(q).headOption
  }

  private def nextExpCode(course: Course): String = {
    val exps = entityDao.findBy(classOf[Experiment], "course" -> course)
    val idx =
      if (exps.isEmpty) then 1
      else exps.map(_.code).max.toInt + 1
    Strings.leftPad(idx.toString, 2, '0')
  }

}
