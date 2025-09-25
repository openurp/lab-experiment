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

package org.openurp.lab.experiment.web.helper

import org.beangle.data.dao.{EntityDao, OqlBuilder}
import org.openurp.base.edu.model.Course
import org.openurp.base.model.{AuditStatus, Semester}
import org.openurp.edu.course.model.Syllabus

import java.util.Locale

class SyllabusHelper(entityDao: EntityDao) {

  def getSyllabus(course: Course, semester: Semester): Option[Syllabus] = {
    val q = OqlBuilder.from(classOf[Syllabus], "s")
    q.where("s.course=:course", course)
    q.where("s.beginOn<=:beginOn and (s.endOn is null or s.endOn >:endOn)", semester.beginOn, semester.beginOn)
    val syllabuses = entityDao.search(q)
    if (syllabuses.size < 2) {
      syllabuses.headOption
    } else {
      val zh = Locale.SIMPLIFIED_CHINESE
      val nonDraft = syllabuses.filter(_.status != AuditStatus.Draft)
      if (nonDraft.nonEmpty) {
        nonDraft.find(_.docLocale == zh).orElse(nonDraft.headOption)
      } else {
        syllabuses.find(_.docLocale == zh).orElse(syllabuses.headOption)
      }
    }
  }

}
