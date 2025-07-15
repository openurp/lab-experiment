[#ftl]
[@b.head/]
[@b.toolbar title="每学期实验项目管理"]
[/@]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="searchForm" action="!search" target="courseTaskList" title="ui.searchForm" theme="search"]
      [@base.semester name="le.task.semester.id" value=semester label="学年学期"/]
      [@b.textfield name="le.experiment.course.code" label="课程代码" maxlength="200000"/]
      [@b.textfield name="le.experiment.course.name" label="课程名称"/]
      [@b.select style="width:100px" name="le.task.department.id" label="开课院系" items=departs option="id,name" empty="..." /]
      <input type="hidden" name="orderBy" value="le.task.course.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="courseTaskList" href="!search?le.task.semester.id=${semester.id}&orderBy=le.task.course.code asc"/]
  </div>
</div>
[@b.foot/]
