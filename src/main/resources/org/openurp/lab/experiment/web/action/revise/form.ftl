  [#assign course=experiment.course/]
  [@b.form theme="list" action="!save" target="course_list" onsubmit="closeDialog"]
    [@b.field label="课程"]${course.code} ${course.name} ${course.defaultCredits!}学分 ${course.creditHours}学时[/@]
    [@b.textfield name="experiment.name" label="实验名称" value=experiment.name! required="true" style="width:300px"/]
    [@b.number name="idx" label="实验序号" value=idx required="true" min="1" /]
    [@b.number name="experiment.creditHours" label="学时" value=experiment.creditHours! required="true" max=maxHours?string comment="不超过"+maxHours+"学时"/]
    [@b.select name="experiment.category.id" label="实验类别" items=categories value=experiment.category! empty="..." required="true" /]
    [@b.select name="experiment.experimentType.id" label="实验类型" items=experimentTypes value=experiment.experimentType! empty="..." required="true" /]
    [@b.select name="experiment.discipline.id" label="实验所属学科" items=disciplines value=experiment.discipline! empty="..." required="true" option=r"${item.code} ${item.name}"
        comment="<a href='http://www.moe.gov.cn/srcsite/A08/moe_1034/s4930/202504/W020250422312780837078.pdf' target='_blank'><i class='fa-solid fa-up-right-from-square'></i> 查看学科目录</a>"/]
    [@b.number label="每组人数" name="experiment.groupStdCount" value=experiment.groupStdCount required="true" min="1" max="50"/]

    [@b.formfoot]
      [#if experiment.id??]
      <input name="experiment.id" type="hidden" value="${experiment.id}"/>
      [/#if]
      <input name="experiment.online" type="hidden" value="[#if experiment.online]1[#else]0[/#if]"/>
      <input name="experiment.course.id" type="hidden" value="${experiment.course.id}"/>
      <input name="task.id" type="hidden" value="${task.id}"/>
      [#if syllabuses?size>0 && newExperiment][#--自动添加到大纲 --]
      <input name="addToSyllabus" type="hidden" value="1"/>
      [/#if]
      [@b.submit value="保存" /]
    [/@]
  [/@]
  [#list 1..3 as i]<br>[/#list]
  <script>
    function closeDialog(f){
       $('#experimentDialog').modal('hide');
       return true;
    }
  </script>
