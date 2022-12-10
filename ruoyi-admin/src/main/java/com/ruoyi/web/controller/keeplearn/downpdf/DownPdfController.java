package com.ruoyi.web.controller.keeplearn.downpdf;

import com.ruoyi.common.asynThreadTask.PendJobPool;
import com.ruoyi.common.asynThreadTask.TaskResult;
import com.ruoyi.common.pdf.HtmlToPdfUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异步并发框架生成pdf
 * 
 * @author wh
 */
@RestController
@RequestMapping("/pdf")
public class DownPdfController {
    private static final Logger log = LoggerFactory.getLogger(DownPdfController.class);

    private final static String JOB_NAME = "处理PDF";
    private final static int JOB_LENGTH = 20;

    /**
     * 生成某个班PDF
     * @Params classId 班级ID
     */
    @GetMapping("/create")
    public void createPDF(String classId) {

        // 此处应该有 通过classId 查询数据库，拿到要用的数据

        // JOB_NAME 固定任务名称
        // JOB_LENGTH 任务要处理的数量 = 查询数据库得到的数据长度


        //PDF业务实现类
        HtmlToPdfUtil myTask = new HtmlToPdfUtil();
        // 并发框架实例
        PendJobPool pool = PendJobPool.getInstance();
        // 注册job，传入 任务名称，任务长度，具体实例，过期长度
        pool.registerJob(JOB_NAME,JOB_LENGTH,myTask,1000*60);

        // 循环从数据库中查询到的数据 加入任务队列
        for(int i = 0; i < JOB_LENGTH; i++) {
            // 此处应该是单条数据
            String pdfName =  String.valueOf(i);
            // 依次推入Task
            pool.putTask(JOB_NAME,pdfName);
        }

    }

    /**
     * 查询任务进度详情
     * @Params jobName 任务名称
     */
    @GetMapping("/get")
    public void findTask(String jobName) {

        // 并发框架实例
        PendJobPool pool = PendJobPool.getInstance();
        // 拿取任务详情
        List<TaskResult<String>> taskDetail =  pool.getTaskDetail(JOB_NAME);
        if(!taskDetail.isEmpty()){
            System.err.println(pool.getTaskProcess(JOB_NAME));
            System.err.println(taskDetail);
        }

    }



}
