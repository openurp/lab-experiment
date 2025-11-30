[#ftl]
[@b.head/]
[@b.toolbar title="实验项目管理"]
[/@]
<div class="search-container">
  <div class="search-panel">
    [@b.form name="searchForm" action="!search" target="experimentList" title="ui.searchForm" theme="search"]
      [@b.textfield name="experiment.course.code" label="课程代码" maxlength="200000"/]
      [@b.textfield name="experiment.course.name" label="课程名称"/]
      [@b.select style="width:100px" name="experiment.course.department.id" label="开课院系" items=departs option="id,name" empty="..." /]
      [@b.textfield name="experiment.name" label="实验名称"/]
      [@b.select name="experiment.category.id" label="实验类别" items=categories empty="..."/]
      [@b.select name="experiment.experimentType.id" label="实验类型" items=experimentTypes empty="..." required="true" /]
      [@b.select name="experiment.discipline.id" label="所属学科" items=disciplines empty="..."/]
      <input type="hidden" name="orderBy" value="experiment.course.code,experiment.code"/>
    [/@]
  </div>
  <div class="search-list">
    [@b.div id="experimentList" href="!search?orderBy=experiment.course.code,experiment.code"/]
  </div>
</div>
[@b.foot/]
