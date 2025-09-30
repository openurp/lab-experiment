[@b.head/]
<div class="container" style="overflow: scroll;">
  <h5 style="text-align:center;">${project.school.name} ${semester.year.name} 学年度 实验数据上报汇总表</h5>
  <table class="table table-sm table-mini" style="width:1740px;" id="report_table">
    <colgroup>
      <col width="50px">
      <col width="100px">
      <col width="220px">
      <col width="100px">
      <col width="150px"><!--所属学院-->
      <col width="60px">
      <col width="80px">
      <col width="100px"><!--实验编号-->
      <col width="250px">
      <col width="60px"><!--实验类别-->
      <col width="60px">
      <col width="60px">
      <col width="60px">
      <col width="60px">
      <col width="60px">
      <col width="60px">
      <col width="60px">
      <col width="80px">
      <col width="120px">
    </colgroup>
    <thead>
      <tr>
        <th>序号</th>
        <th>课程代码</th>
        <th>课程名称</th>
        <th>课程负责人</th>
        <th>所属学院</th>
        <th>实验班级数</th>
        <th>学校代码</th>
        <th>实验编号</th>
        <th>实验名称</th>
        <th>实验类别</th>
        <th>实验类型</th>
        <th>实验所属学科</th>
        <th>实验要求</th>
        <th>实验者类别</th>
        <th>实验者人数</th>
        <th>每组人数</th>
        <th>实验学时数</th>
        <th>实验室编号</th>
        <th>实验室名称</th>
      </tr>
    </thead>
    <tbody>
      [#list courseExperiments as ce]
      <tr>
        <td>${ce_index+1}</td>
        <td>${ce.course.code}</td>
        <td>${ce.course.name}</td>
        <td>${(ce.director.name)!}</td>
        <td>${ce.department.name}</td>
        <td>${ce.clazzCount}</td>
        <td>${ce.course.project.school.code}</td>
        <td>${ce.code}</td>
        <td>${ce.experiment.name}</td>
        <td>${(ce.experiment.category.code)!}</td>
        <td>${(ce.experiment.experimentType.code)!}</td>
        <td>${ce.experiment.discipline.code}</td>
        <td>${ce.rank.compulsory?string("1","2")}</td>
        <td>3</td>
        <td>${ce.stdCount}</td>
        <td>${ce.experiment.groupStdCount}</td>
        <td>${ce.experiment.creditHours}</td>
        <td>${(ce.laboratory.code)!}</td>
        <td>${(ce.laboratory.name)!}</td>
      </tr>
      [/#list]
    </tbody>
  </table>
  <div style="text-align:center;"><button id="downloadBtn" class="btn btn-sm btn-outline-primary">下载到Excel</button></div>
  <script>
    var fileUrl="${ems_api}/tools/doc/excel";
    var fileName="${project.school.name} ${semester.year.name} 学年度实验上报汇总表.xlsx";
    // 下载按钮点击事件
    downloadBtn.addEventListener('click', async () => {
        try {
            // 使用Fetch API获取远程资源
            const response = await fetch(fileUrl,{
                method: 'POST',
                body: document.getElementById("report_table").outerHTML
              }
            );

            // 检查请求是否成功
            if (!response.ok) {
                [#noparse]
                throw new Error(`请求失败: ${response.status}`);
                [/#noparse]
            }

            // 将响应转换为Blob对象
            const blob = await response.blob();

            // 创建下载链接
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName; // 设置保存的文件名

            // 触发下载
            document.body.appendChild(a);
            a.click();

            // 清理资源
            setTimeout(() => {
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            }, 100);
        } catch (error) {
            console.error('下载错误:', error);
        }
    });
  </script>
</div>
[@b.foot/]
