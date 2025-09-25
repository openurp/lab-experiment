[#ftl]
[@b.head/]
<div class="container">
  [#list departTasks?keys as depart]
  <table style="text-align:center;width:100%">
    [#if hasEmpties.get(depart)]
    <tr>
      <td><h5>教学实验项目汇总表 [${semester.year.name}学年${semester.name}学期]</h5></td>
    <tr>
      <td><div class="alert alert-warning">(尚有数据存在缺项，请修正）</div></td>
    </tr>
    [#else]
    <tr>
      <td colspan="2"><h5>教学实验项目汇总表 [${semester.year.name}学年${semester.name}学期]</h5></td>
    </tr>
    <tr style="text-align:left;">
      <td style="padding-left:50px;">填报学院（盖章）：${depart.name}</td>
      <td style="padding-left:50px;">完成日期：${b.now?string("yyyy年MM月dd日")}</td>
    </tr>
    <tr style="text-align:left;">
      <td style="padding-left:50px;">教学院长（签字）：<u>[#list 1..20 as i]&nbsp;[/#list]</td>
      <td></td>
    </tr>
    [/#if]
  </table>
  [#assign tasks = departTasks.get(depart)/]
  [@b.grid items=tasks?sort_by(['course',"code"]) var="task" theme="mini" class="table-sm table-mini table-bordered"]
    [@b.row]
      [@b.col width="10%" property="course.code" title="代码"/]
      [@b.col property="course.name" title="名称"/]
      [@b.col width="5%" property="course.defaultCredits" title="学分"/]
      [@b.col width="5%" title="理论学时" property="theoryHours"/]
      [@b.col width="5%" title="实践学时" property="practiceHours"/]
      [@b.col width="7%" property="nature.name" title="课程性质"/]
      [@b.col width="8%" property="director.name" title="负责人"/]
      [@b.col title="实验室"]
        <div class="text-ellipsis" title="[#list task.labs as lab]${lab.room.name}[#sep],[/#list]">
          [#list task.labs as lab]${lab.room.name}[#sep],[/#list]
        </div>
      [/@]
      [@b.col width="6%" property="rank.name" title="必选修"/]
      [@b.col width="6%" property="stdCount" title="学生数"/]
      [@b.col width="6%" property="clazzCount" title="班级数"/]
      [@b.col width="6%" property="expCount" title="实验数"/]
      [@b.col width="6%" property="remark" title="备注"/]
    [/@]
  [/@]
  [#if depart_has_next]
  <div style="page-break-after: always;"></div>
  [/#if]
  [/#list]
</div>
[@b.foot/]
