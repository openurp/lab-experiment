[#ftl]
[@b.head/]
[@b.grid items=experiments var="experiment"]
  [@b.gridbar]
    bar.addItem("删除",action.remove());
    bar.addItem("修改",action.edit());
    bar.addItem("批量修改",action.multi('batchEdit'));
    bar.addItem("${b.text("action.export")}",
                action.exportData("course.project.school.code:学校代码,course.code:课程代码,course.name:课程名称,code:实验编号,"+
                "name:实验名称,category.name:实验类别,experimentType.name:实验类型,discipline.name:实验所属学科,"+
                "rank.name:实验要求,groupStdCount:每组人数,creditHours:实验学时数",
                null,'fileName=实验项目信息'));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="course.code" title="课程代码"/]
    [@b.col width="15%" property="course.name" title="课程名称"/]
    [@b.col width="8%" property="course.department.name" title="开课院系"]
      ${experiment.course.department.shortName!experiment.course.department.name}
    [/@]
    [@b.col width="5%" property="code" title="编号"/]
    [@b.col property="name" title="名称"/]
    [@b.col width="5%" property="creditHours" title="学时"/]
    [@b.col width="8%" property="category.name" title="实验类别"/]
    [@b.col width="8%" property="experimentType.name" title="实验类型"/]
    [@b.col width="8%" property="discipline.name" title="学科"/]
    [@b.col width="5%" property="groupStdCount" title="每组人数"/]
  [/@]
[/@]
[@b.foot/]
