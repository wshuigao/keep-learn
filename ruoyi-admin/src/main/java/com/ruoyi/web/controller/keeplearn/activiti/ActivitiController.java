package com.ruoyi.web.controller.keeplearn.activiti;

import cn.hutool.core.date.DateUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * activiti 工作流
 * 
 * @author wh
 */
@RestController
@RequestMapping("/activiti")
public class ActivitiController {
    private static final Logger log = LoggerFactory.getLogger(ActivitiController.class);

    //资源管理
    @Autowired
    private RepositoryService repositoryService;
    // 流程运行管理
    @Autowired
    private RuntimeService runtimeService;
    //任务管理
    @Autowired
    private TaskService taskService;
    //历史管理
    @Autowired
    private HistoryService historyService;

    //引擎管理
//  private ManagerService managerService;

    /**
     * 创建流程
     */
    @GetMapping("/create")
    public void createActiviti() {
        Deployment deployment = repositoryService.createDeployment()
            // 名称
            .name("请假流程")
            // 资源
            .addClasspathResource("acviti/learn.bpmn20.xml")
            .deploy();
        System.out.println("流程id：" + deployment.getId());
        System.out.println("流程名称：" + deployment.getName());


    }

    /**
     * 开启流程
     */
    @GetMapping("/start")
    public void startActiviti() {
        Map<String, Object> map = new HashMap<String, Object>();
        //在holiday.bpmn中,填写请假单的任务办理人为动态传入的userId,此处模拟一个id
        map.put("userId", "10001");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("learn",map);
        System.out.println("流程id：" + processInstance.getProcessDefinitionId());
        System.out.println("流程实例id：" + processInstance.getId());
        System.out.println("当前活动id：" + processInstance.getActivityId());
    }

    /**
     * 查询任务
     */
    @GetMapping("/get")
    public void findTask() {
        List<Task> taskList = taskService.createTaskQuery()
            // 流程id
            .processDefinitionKey("learn")
            // 处理人
            .taskAssignee("张三")
            .list();
        for (Task task : taskList) {
            System.out.println("任务id：" + task.getId());
            System.out.println("任务负责人：" + task.getAssignee());
            System.out.println("任务名称：" + task.getName());
        }

    }

    /**
     * 处理任务
     */
    @GetMapping("/deal")
    public void handleTask() {
        Task task = taskService.createTaskQuery()
            // 流程id
            .processDefinitionKey("learn")
            // 处理人
            .taskAssignee("张三")
            .singleResult();
        taskService.complete(task.getId());
    }

    /**
     * 完成任务
     * 任务处理只需重复执行上述方法即可，只是更改了任务的处理人，直至任务结束。
     */
    @GetMapping("/finish")
    public void finishHandleTask() {
        Task task = taskService.createTaskQuery()
            // 流程id
            .processDefinitionKey("learn")
            // 处理人
            .taskAssignee("李四")
            .singleResult();
        taskService.complete(task.getId());
    }

    /**
     * 获取历史
     */
    @GetMapping("/history")
    public void getHistoryList() {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
            .processDefinitionId("learn:1:e9cf6e60-7d04-11ed-aecd-d4d25271f7c9")
            .list();
        for (HistoricTaskInstance historicTaskInstance : list) {
            System.out.println("开始时间：" + DateUtil.formatDateTime(historicTaskInstance.getStartTime()));
            System.out.println("结束时间：" + DateUtil.formatDateTime(historicTaskInstance.getEndTime()));
            System.out.println("流程名称：" + historicTaskInstance.getName());
            System.out.println("处理人名称：" + historicTaskInstance.getAssignee());
        }
    }


}
