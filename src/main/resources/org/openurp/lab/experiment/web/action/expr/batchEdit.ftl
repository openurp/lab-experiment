[@b.toolbar title="批量设置实验项目"]
  bar.addBack();
[/@]
  [@b.form theme="list" action="!batchUpdate"]
    [@b.field label="实验项目数量"]${experiments?size}[/@]
    [@b.select name="category.id" label="实验类别" items=categories empty="不做更改" /]
    [@b.select name="experimentType.id" label="实验类型" items=experimentTypes  empty="不做更改" /]
    [@b.select name="discipline.id" label="实验所属学科" items=disciplines empty="不做更改" option=r"${item.code} ${item.name}"
               comment="<a href='http://www.moe.gov.cn/srcsite/A08/moe_1034/s4930/202504/W020250422312780837078.pdf' target='_blank'><i class='fa-solid fa-up-right-from-square'></i> 查看学科目录</a>"/]
    [@b.number label="每组人数" name="groupStdCount" value=0 min="0" max="50" comment="0表示不做更改"/]
    [@b.formfoot]
      [#list experiments as experiment]
      <input name="experiment.id" type="hidden" value="${experiment.id}"/>
      [/#list]
      [@b.submit value="批量设置" /]
    [/@]
  [/@]
  [#list 1..3 as i]<br>[/#list]
