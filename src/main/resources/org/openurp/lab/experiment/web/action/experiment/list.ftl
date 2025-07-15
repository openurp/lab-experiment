[#ftl]
[@b.head/]
[@b.grid items=experiments var="le"]
  [@b.gridbar]
    bar.addItem("${b.text("action.export")}",
                action.exportData("course.project.school.code:学校代码,course.code:课程代码,course.name:课程名称,code:实验编号,"+
                "name:实验名称,category.name:实验类别,experimentType.name:实验类型,discipline.name:实验所属学科,"+
                "rank.name:实验要求,stdCount:实验者人数,groupStdCount:每组人数,creditHours:实验学时数,clazzCount:实验班级数",
                null,'fileName=课程实验项目信息'));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="task.course.code" title="课程代码"/]
    [@b.col width="15%" property="task.course.name" title="课程名称"/]
    [@b.col width="8%" property="task.course.department.name" title="开课院系"]
      ${le.task.course.department.shortName!le.task.course.department.name}
    [/@]
    [@b.col property="experiment.name" title="实验项目"/]
    [@b.col width="5%" property="experiment.creditHours" title="学时"/]
    [@b.col width="6%" property="task.rank.name" title="必修选修"/]
    [@b.col width="8%" property="experiment.category.name" title="实验类别"/]
    [@b.col width="8%" property="experiment.experimentType.name" title="实验类型"/]
    [@b.col width="8%" property="experiment.discipline.name" title="学科"/]
    [@b.col width="5%" property="task.stdCount" title="人数"/]
    [@b.col width="5%" property="experiment.groupStdCount" title="每组人数"/]
  [/@]
[/@]
[@b.foot/]
