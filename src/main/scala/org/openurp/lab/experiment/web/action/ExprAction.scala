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
import org.openurp.base.edu.model.{Course, Experiment}
import org.openurp.base.model.{Project, Semester}
import org.openurp.code.edu.model.{ExperimentCategory, ExperimentType, Level1Discipline}
import org.openurp.starter.web.support.ProjectSupport

import java.time.LocalDate
import scala.collection.SortedMap

/** 实验项目管理
 */
class ExprAction extends RestfulAction[Experiment], ProjectSupport {

  override protected def indexSetting(): Unit = {
    given project: Project = getProject

    put("departs", getDeparts)
    put("categories", getCodes(classOf[ExperimentCategory]))
    put("experimentTypes", getCodes(classOf[ExperimentType]))
    put("disciplines", getCodes(classOf[Level1Discipline]))
  }

  override def editSetting(experiment: Experiment): Unit = {
    if (!experiment.persisted) {
      val course = entityDao.get(classOf[Course], getLongId("course"))
      experiment.course = course
    }

    given project: Project = getProject

    val query = OqlBuilder.from(classOf[Semester], "s")
    query.where("s.calendar=:calendar", project.calendar)
    query.orderBy("s.code desc")
    val semesters = entityDao.search(query)
    put("semesterDates", SortedMap.from(semesters.map(x => (atStartOfDay(x.beginOn).toString, atStartOfDay(x.beginOn).toString)).sortBy(_._1).reverse))
    put("categories", getCodes(classOf[ExperimentCategory]))
    put("experimentTypes", getCodes(classOf[ExperimentType]))
    put("disciplines", getCodes(classOf[Level1Discipline]))
  }


  def batchEdit(): View = {
    given project: Project = getProject
    put("categories", getCodes(classOf[ExperimentCategory]))
    put("experimentTypes", getCodes(classOf[ExperimentType]))
    put("disciplines", getCodes(classOf[Level1Discipline]))
    put("experiments", entityDao.find(classOf[Experiment], getLongIds("experiment")))
    forward()
  }

  def batchUpdate(): View = {
    val experiments = entityDao.find(classOf[Experiment], getLongIds("experiment"))
    getInt("category.id") foreach { categoryId =>
      val category = entityDao.get(classOf[ExperimentCategory], categoryId)
      experiments foreach { e => e.category = category }
    }
    getInt("experimentType.id") foreach { id =>
      val e = entityDao.get(classOf[ExperimentType], id)
      experiments foreach (_.experimentType = e)
    }
    getInt("discipline.id") foreach { id =>
      val e = entityDao.get(classOf[Level1Discipline], id)
      experiments foreach (_.discipline = e)
    }
    getInt("groupStdCount") foreach { groupStdCount =>
      if (groupStdCount > 0) {
        experiments foreach (_.groupStdCount = groupStdCount)
      }
    }
    entityDao.saveOrUpdate(experiments)
    redirect("search", "info.save.success")
  }

  /** 日期对应当月的第一天的日期
   *
   * @param date
   * @return
   */
  private def atStartOfDay(date: LocalDate): LocalDate = {
    LocalDate.of(date.getYear, date.getMonth, 1)
  }

}
