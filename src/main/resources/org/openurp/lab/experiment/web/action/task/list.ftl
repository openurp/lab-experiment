[#ftl]
[@b.head/]
[@b.grid items=tasks var="task"]
  [@b.gridbar]
    [#if departs?size>2]
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("初始化",action.method("autoCreate","确定更新所有数据？"));
    bar.addItem("删除",action.remove());
    [/#if]
    bar.addItem("打印汇总表",action.method('report',"确认所有课程没有缺项，打印汇总表?",null,"_blank"));
    bar.addItem("${b.text("action.export")}",
                action.exportData("course.code:课程代码,course.name:课程名称,course.defaultCredits:学分,course.creditHours:学时,"+
                "theoryHours:理论学时,practiceHours:实践学时,nature.name:课程性质,"+
                "department.name:开课院系,director.staff.code:课程负责人工号,director.name:课程负责人,office.name:教研室,"+
                "stdCount:学生数,clazzCount:班级数目,expCount:实验数,validated:无缺项,remark:备注",
                null,'fileName=实验课信息'));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="7%" property="course.code" title="代码"/]
    [@b.col property="course.name" title="名称"][@b.a href="!info?id=${task.id}"]${task.course.name}[/@][/@]
    [@b.col width="5%" property="course.defaultCredits" title="学分"/]
    [@b.col width="5%" title="理论学时" property="theoryHours"/]
    [@b.col width="5%" title="实践学时" property="practiceHours"/]
    [@b.col width="7%" property="department.name" title="开课院系"]
      ${task.department.shortName!task.department.name}
    [/@]
    [@b.col width="7%" property="nature.name" title="课程性质"/]
    [@b.col width="8%" property="office.name" title="教研室"/]
    [@b.col width="7%" property="director.name" title="负责人"/]
    [@b.col title="实验室"]
      <div class="text-ellipsis" title="[#list task.labs as lab]${lab.room.name}[#sep],[/#list]">
        [#list task.labs as lab]${lab.room.name}[#sep],[/#list]
      </div>
    [/@]
    [@b.col width="6%" property="rank.name" title="必选修"/]
    [@b.col width="5%" property="stdCount" title="学生数"/]
    [@b.col width="5%" property="expCount" title="实验数"/]
    [@b.col width="5%" property="validated" title="数据缺项"]
      ${task.validated?string("无","有")}
    [/@]
  [/@]
[/@]
[@b.foot/]
