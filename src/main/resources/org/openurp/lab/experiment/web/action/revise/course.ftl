[#ftl/]
[@b.head/]
  <style>
    table.info-table{
      table-layout:fixed;
    }
    table.info-table td.title {
      padding: 0.2rem 0rem;
      text-align:right;
      color: #6c757d !important;
    }
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
  [@b.messages slash="3"/]
  <div class="card card-info card-primary card-outline">
    <div class="card-header">
      <h4 class="card-title">${course.code} ${course.name}</h4>
    </div>
    <table class="table table-sm info-table">
      <tr>
        <td class="title" width="10%">代码:</td>
        <td>${course.code}</td>
        <td class="title" width="10%">名称:</td>
        <td>${course.name}</td>
        <td class="title" width="10%">学分:</td>
        <td>${course.defaultCredits!}</td>
      </tr>
      <tr>
        <td class="title">开课院系:</td>
        <td>${(course.department.name)!}</td>
        [#if task??]
        <td class="title">必选修:</td>
        <td>${(task.rank.name)!}</td>
        [#else]
        <td class="title">课程类别:</td>
        <td>${(course.courseType.name)!}</td>
        [/#if]
        <td class="title">学时:</td>
        <td>${course.creditHours!}[#if syllabus??]([#list syllabus.hours?sort_by(['nature','code']) as h]${h.nature.name}${h.creditHours}[#sep]，[/#list])[/#if]</td>
      </tr>
      [#if task??]
      <tr>
        <td class="title">人数:</td>
        <td>${(task.clazzCount)!}班 ${(task.stdCount)!}人</td>
        <td class="title">实验室:</td>
        <td colspan="3">[#list task.labs as lab]${lab.room.name}[#sep],[/#list]</td>
      </tr>
      [/#if]
    </table>
  </div>

  [#if task??]
  <div class="card card-info card-primary card-outline">
    [#assign hours =0/]
    [#list task.experiments as e][#assign hours = hours+e.experiment.creditHours/][/#list]
    <div class="card-header">
        <h4 class="card-title">本学期课程实验项目(${task.experiments?size}项 ${hours}学时)</h4>
        <div class="card-tools">
            <a class="btn btn-outline-primary btn-sm" href='${b.url("!edit?task.id=" + task.id)}'
               data-toggle="modal" data-target="#experimentDialog">新增</a> &nbsp;
            <a class="btn btn-outline-primary btn-sm" href='${b.url("!batchEdit?task.id=" + task.id)}' title="批量设置实验项目"
                           data-toggle="modal" data-target="#experimentDialog">批量设置</a> &nbsp;
        </div>
    </div>
    <div class="card-body" style="padding-top: 0px;">
      [#if task.experiments?size==0]
        <p class="alert alert-warning">缺少实验项目，需要填写。</p>
      [/#if]
      [@b.grid items=task.experiments?sort_by("idx") var="exp" theme="mini"]
        [@b.row]
          [@b.col property="idx" title="实验序号"/]
          [@b.col property="experiment.name" title="实验名称"]
            <a href='${b.url("!edit?experiment.id=${exp.experiment.id}&task.id=${task.id}")}'
               data-toggle="modal" data-target="#experimentDialog">${exp.experiment.name}</a>
          [/@]
          [@b.col property="experiment.category.name" title="实验类别"]
            [#if exp.experiment.category??]${exp.experiment.category.name}[#else]<span class="red-point" title="缺少数据">--</span>[/#if]
          [/@]
          [@b.col property="experiment.experimentType.name" title="实验类型"/]
          [@b.col property="experiment.creditHours" title="学时"]
            [#if exp.experiment.creditHours>0]${exp.experiment.creditHours}[#else]<span class="red-point" title="需要大于0">0</span>[/#if]
          [/@]
          [@b.col property="experiment.groupStdCount" title="每组人数"]
             [#if exp.experiment.groupStdCount>0]${exp.experiment.groupStdCount}[#else]<span class="red-point" title="需要大于0">0</span>[/#if]
          [/@]
          [@b.col property="experiment.discipline.name" title="学科"]
            [#if exp.experiment.discipline??]${exp.experiment.discipline.name}[#else]<span class="red-point" title="缺少数据">--</span>[/#if]
          [/@]
          [@b.col title="操作"]
            [@b.a href="!evict?experiment.id="+exp.experiment.id+"&task.id="+task.id onclick="if(confirm('确认这学期移除该实验项目？')){return bg.Go(this,null)}else{return false;}"]移除[/@]
          [/@]
        [/@]
      [/@]
      [#if hasErrorData]<p class="text-muted"><span class="red-point">红点</span>表示有数据问题，需要维护。</p>[/#if]
    </div>
  </div>
  [/#if]

  <div class="card card-info card-primary card-outline">
    <div class="card-header">
      <h4 class="card-title">课程实验项目库</h4>
    </div>
    <div class="card-body" style="padding-top: 0px;">
      [@b.grid items=experiments?sort_by("code") var="exp" theme="mini"]
        [@b.row]
          [@b.col property="name" title="实验名称"]${exp.name}[/@]
          [@b.col property="category.name" title="实验类别"/]
          [@b.col property="experimentType.name" title="实验类型"/]
          [@b.col property="creditHours" title="学时"/]
          [@b.col property="groupStdCount" title="每组人数"/]
          [@b.col property="discipline.name" title="学科"/]
          [#if task??]
          [@b.col title="操作"]
            [@b.a href="!add?experiment.id="+exp.id+"&task.id="+task.id onclick="if(confirm('确认这学期添加该实验项目？')){return bg.Go(this,null)}else{return false;}"]添加[/@]
            [#if orphans?seq_contains(exp)]
            [@b.a href="!remove?experiment.id="+exp.id+"&task.id="+task.id onclick="if(confirm('确认彻底删除该实验项目？')){return bg.Go(this,null)}else{return false;}"]删除[/@]
            [/#if]
          [/@]
          [/#if]
        [/@]
      [/@]
    </div>
  </div>

[@b.foot/]
