[#ftl]
[@b.head/]
[@b.grid items=tasks var="task"]
  [@b.gridbar]
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("初始化",action.method("autoCreate"));
    bar.addItem("删除",action.remove());
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="10%" property="course.code" title="代码"/]
    [@b.col property="course.name" title="名称"][@b.a href="!info?id=${task.id}"]${task.course.name}[/@][/@]
    [@b.col width="7%" property="department.name" title="开课院系"]
      ${task.department.shortName!task.department.name}
    [/@]
    [@b.col width="5%" property="course.defaultCredits" title="学分"/]
    [@b.col width="8%" property="director.name" title="负责人"/]
    [@b.col width="25%" title="实验室"]
      <div class="text-ellipsis" title="[#list task.labs as lab]${lab.name}(${lab.room.name})[#sep],[/#list]">
        [#list task.labs as lab]${lab.name}(${lab.room.name})[#sep],[/#list]
      </div>
    [/@]
    [@b.col width="6%" property="rank.name" title="必选修"/]
    [@b.col width="6%" property="stdCount" title="学生数"/]
    [@b.col width="6%" property="clazzCount" title="班级数"/]
    [@b.col width="6%" property="expCount" title="实验数"/]
  [/@]
  [/@]
[@b.foot/]
