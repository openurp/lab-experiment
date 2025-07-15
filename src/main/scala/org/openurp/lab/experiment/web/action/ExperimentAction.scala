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
import org.beangle.webmvc.support.action.{ExportSupport, RestfulAction}
import org.openurp.base.model.Project
import org.openurp.lab.experiment.model.LabExperiment
import org.openurp.starter.web.support.ProjectSupport

/** 每学期实验管理
 */
class ExperimentAction extends RestfulAction[LabExperiment], ProjectSupport, ExportSupport[LabExperiment] {

  override protected def indexSetting(): Unit = {
    super.indexSetting()

    given project: Project = getProject

    val departs = getDeparts
    put("departs", departs)
    put("project", project)
    put("semester", getSemester)
    forward()
  }

  override protected def simpleEntityName: String = "le"

  override protected def getQueryBuilder: OqlBuilder[LabExperiment] = {
    val query = super.getQueryBuilder
    queryByDepart(query, "experiment.task.department")
    query
  }
}