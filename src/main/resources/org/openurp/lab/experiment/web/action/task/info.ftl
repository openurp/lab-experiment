[#ftl]
[@b.head/]
[@b.toolbar title="课程实验信息"]
  bar.addBack();
[/@]
<style>
    .red-point{ position: relative; }
    .red-point::before{
       content: " "; border: 3px solid red;
       border-radius:3px;
       position: absolute;
       z-index: 1000;
       right: 0;
       margin-right: -5px;
    }
</style>
[#assign hours =0/]
[#list task.experiments as e][#assign hours = hours+e.experiment.creditHours/][/#list]
<div class="container">
  <div class="card card-primary card-outline">
    <div class="card-header">
      <h4 class="card-title">实验项目信息[#if hours>0](${task.experiments?size}项 ${hours}学时)[/#if]</h4>
    </div>
    <div class="card-body" style="padding-top:0px">
      <table class="table table-sm table-detail">
        <tr>
          <td class="title" width="10%">代码:</td>
          <td>${task.course.code}</td>
          <td class="title" width="10%">名称:</td>
          <td>${task.course.name}</td>
          <td class="title" width="10%">学分:</td>
          <td>${task.course.defaultCredits!}</td>
        </tr>
        <tr>
          <td class="title">英文名:</td>
          <td>${task.course.enName!}</td>
          <td class="title">院系:</td>
          <td>${(task.course.department.name)!}</td>
          <td class="title">负责人:</td>
          <td>${(task.director.name)!}</td>
        </tr>
      <tr>
        <td class="title">人数:</td>
        <td>${(task.clazzCount)!}班 ${(task.stdCount)!}人</td>
        <td class="title">实验室:</td>
        <td colspan="3">[#list task.labs as lab]${lab.room.name}[#sep],[/#list]</td>
      </tr>
      [#if !task.required]
      <tr>
        <td class="title">说明:</td>
        <td colspan="3">不需要填写实验项目：${task.remark!}</td>
      </tr>
      [/#if]
      </table>
      [#if task.required]
        [#if task.experiments?size==0]
          <p class="alert alert-warning">缺少实验项目，需要填写。</p>
        [/#if]
        [@b.grid items=task.experiments?sort_by("idx") var="exp" theme="mini" class="table-sm table-mini"]
          [@b.row]
            [@b.col property="idx" title="序号" width="7%"/]
            [@b.col property="experiment.name" title="实验名称"/]
            [@b.col property="experiment.category.name" title="实验类别"  width="15%"]
              [#if exp.experiment.category??]${exp.experiment.category.name}[#else]<span class="red-point" title="缺少数据">--</span>[/#if]
            [/@]
            [@b.col property="experiment.experimentType.name" title="实验类型"  width="15%"/]
            [@b.col property="experiment.creditHours" title="学时"  width="8%"]
              [#if exp.experiment.creditHours>0]${exp.experiment.creditHours}[#else]<span class="red-point" title="需要大于0">0</span>[/#if]
            [/@]
            [@b.col property="experiment.groupStdCount" title="每组人数"  width="8%"]
               [#if exp.experiment.groupStdCount>0]${exp.experiment.groupStdCount}[#else]<span class="red-point" title="需要大于0">0</span>[/#if]
            [/@]
            [@b.col property="experiment.discipline.name" title="学科"  width="17%"]
              [#if exp.experiment.discipline??]${exp.experiment.discipline.name}[#else]<span class="red-point" title="缺少数据">--</span>[/#if]
            [/@]
          [/@]
        [/@]
      [/#if]
    </div>
  </div>
</div>
[@b.foot/]
