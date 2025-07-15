[#ftl]
[@b.head/]
[@b.toolbar title="每学期课程实验填写"/]
[@base.semester_bar value=semester/]

<div class="container-fluid">
  <div class="row">
     <div class="col-3" id="accordion">

       [#if taskCourses?size>0]
       <div class="card card-info card-primary card-outline">
         <div class="card-header" id="stat_header_1">
           <h5 class="mb-0">
              <button class="btn btn-link" data-toggle="collapse" data-target="#stat_body_1" aria-expanded="true" aria-controls="stat_body_1" style="padding: 0;">
                我的修订任务(实验)
              </button>
           </h5>
         </div>
         <div id="stat_body_1" class="collapse show" aria-labelledby="stat_header_1" data-parent="#accordion">
           <div class="card-body" style="padding-top: 0px;">
             <table class="table table-hover table-sm">
               <tbody>
               [#list taskCourses as course]
                <tr>
                 <td>
                   <span style="color:#6c757d;font-size:0.8em">${course.code}</span>
                   [@b.a href="!course?course.id="+course.id+"&semester.id="+semester.id target="course_list"]<span>${course.name}</span>[/@]
                 </td>
                </tr>
                [/#list]
               </tbody>
             </table>
           </div>
         </div>
       </div>
       [/#if]


       [#if clazzCourses?size>0]
       <div class="card card-info card-primary card-outline">
         <div class="card-header" id="stat_header_2">
          <h5 class="mb-0">
              <button class="btn btn-link" data-toggle="collapse" data-target="#stat_body_2" aria-expanded="true" aria-controls="stat_body_2" style="padding: 0;">
                我的课程
              </button>
            </h5>
         </div>
         <div id="stat_body_2" class="collapse show" aria-labelledby="stat_header_2" data-parent="#accordion">
           <div class="card-body" style="padding-top: 0px;">
             <table class="table table-hover table-sm">
               <tbody>
               [#list clazzCourses as course]
                <tr>
                 <td>
                   <span style="color:#6c757d;font-size:0.8em">${course.code}</span>
                   [@b.a href="!course?course.id="+course.id+"&semester.id="+semester.id target="course_list"]<span>${course.name}</span>[/@]
                 </td>
                </tr>
                [/#list]
               </tbody>
             </table>
           </div>
         </div>
       </div>
       [/#if]

       [#if hisCourses?size>0]
       <div class="card card-info card-primary card-outline">
         <div class="card-header" id="stat_header_3">
          <h5 class="mb-0">
              <button class="btn btn-link" data-toggle="collapse" data-target="#stat_body_3" aria-expanded="true" aria-controls="stat_body_3" style="padding: 0;">
                历史学期课程
              </button>
            </h5>
         </div>
         <div id="stat_body_3" class="collapse show" aria-labelledby="stat_header_3" data-parent="#accordion">
           <div class="card-body" style="padding-top: 0px;">
             <table class="table table-hover table-sm">
               <tbody>
               [#list hisCourses as course]
                <tr>
                 <td>
                   <span style="color:#6c757d;font-size:0.8em">${course.code}</span>
                   [@b.a href="!course?course.id="+course.id+"&semester.id="+semester.id target="course_list"]<span>${course.name}</span>[/@]
                 </td>
                </tr>
                [/#list]
               </tbody>
             </table>
           </div>
         </div>
       </div>
       [/#if]

     </div><!--end col-3-->

     [#if courses?size>0]
     [@b.div class="col-9" id="course_list" href="!course?course.id="+courses?first.id+"&semester.id="+semester.id/]
     [#else]
     <div>你还没有带课</div>
     [/#if]
  </div><!--end row-->
</div><!--end container-->

[@b.dialog id="experimentDialog" title="实验信息" /]
[@b.foot/]
