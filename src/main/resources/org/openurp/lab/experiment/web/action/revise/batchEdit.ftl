  [#assign course=task.course/]
  [@b.form theme="list" action="!batchUpdate" target="course_list" onsubmit="closeDialog"]
    [@b.field label="课程"]${course.code} ${course.name} ${course.defaultCredits!}学分 ${course.creditHours}学时[/@]
    [@b.select name="category.id" label="实验类别" items=categories empty="不做更改" /]
    [@b.select name="experimentType.id" label="实验类型" items=experimentTypes  empty="不做更改" /]
    [@b.select name="discipline.id" label="实验所属学科" items=disciplines empty="不做更改" option=r"${item.code} ${item.name}"/]
    [@b.number label="每组人数" name="groupStdCount" value=0 min="0" max="50" comment="0表示不做更改"/]
    [@b.formfoot]
      <input name="task.id" type="hidden" value="${task.id}"/>
      [@b.submit value="批量设置" /]
    [/@]
  [/@]
  [#list 1..3 as i]<br>[/#list]
  <script>
    function closeDialog(f){
       $('#experimentDialog').modal('hide');
       return true;
    }
  </script>
