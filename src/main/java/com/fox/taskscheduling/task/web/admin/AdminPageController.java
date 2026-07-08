package com.fox.taskscheduling.task.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fox.taskscheduling.execution.application.TaskExecutionService;
import com.fox.taskscheduling.task.application.TaskDefinitionService;
import com.fox.taskscheduling.task.domain.TaskStatus;

@Controller
public class AdminPageController {

    private final TaskDefinitionService taskService;
    private final TaskExecutionService executionService;

    public AdminPageController(TaskDefinitionService taskService, TaskExecutionService executionService) {
        this.taskService = taskService;
        this.executionService = executionService;
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping({"/", "/admin"})
    String dashboard(Model model) {
        var tasks = taskService.list();
        var executions = executionService.listRecent();
        model.addAttribute("taskCount", tasks.size());
        model.addAttribute("enabledCount", tasks.stream().filter(task -> task.status() == TaskStatus.ENABLED).count());
        model.addAttribute("pausedCount", tasks.stream().filter(task -> task.status() == TaskStatus.PAUSED).count());
        model.addAttribute("disabledCount", tasks.stream().filter(task -> task.status() == TaskStatus.DISABLED).count());
        model.addAttribute("recentExecutions", executions.stream().limit(5).toList());
        return "admin/dashboard";
    }

    @GetMapping("/admin/tasks")
    String tasks(Model model) {
        model.addAttribute("tasks", taskService.list());
        return "admin/tasks";
    }

    @GetMapping("/admin/executions")
    String executions(Model model) {
        model.addAttribute("executions", executionService.listRecent());
        return "admin/executions";
    }
}
