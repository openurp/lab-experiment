[#ftl]
[@b.head/]
[@b.toolbar title="实验填写要求"]
  bar.addBack();
[/@]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="taskSearchForm" action="!search" target="tasklist" title="ui.searchForm" theme="search"]
      [@base.semester name="task.semester.id" value=semester label="学年学期"/]
      [@b.textfield name="task.course.code" label="代码"/]
      [@b.textfield name="task.course.name" label="名称"/]
      [@b.select name="task.department.id" label="开课院系" items=departments option="id,name" empty="..." /]
      [@b.textfield name="task.director.name" label="负责人"/]

      [@b.select name="task.required" label="实验要求" empty="..."]
        <option value="">...</option>
        <option value="1">要求实验</option>
        <option value="0">不要求</option>
        <option value="null">未设置</option>
      [/@]
      <input type="hidden" name="orderBy" value="task.course.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="tasklist" href="!search?task.semester.id=${semester.id}&orderBy=task.course.code asc"/]
  </div>
</div>
[@b.foot/]
