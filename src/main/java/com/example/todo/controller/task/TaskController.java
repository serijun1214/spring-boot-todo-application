package com.example.todo.controller.task;

import com.example.todo.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("tasks")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String list (Model model) {
        var taskList = taskService.find()
                .stream()
                .map(TaskDTO::toDTO
                )
                .toList();
        model.addAttribute("taskList", taskList);
        return "tasks/list";
    }

    @GetMapping("/{id}")
    public String showDetail (@PathVariable("id") long taskId, Model model) {
        var taskEntity = taskService.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);
        model.addAttribute("task", TaskDTO.toDTO(taskEntity));
        return "tasks/detail";
    }

    @GetMapping("/creationForm")
    public String showCreationForm(@ModelAttribute TaskForm form) {
        return "tasks/form";
    }

    @PostMapping
    public String create(@Validated TaskForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return showCreationForm(form);
        }

        taskService.create(form.toEntity());
        return "redirect:/tasks";
    }

    @GetMapping("/{id}/editForm")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        var taskEntiry = taskService.findById(id)
                        .orElseThrow(TaskNotFoundException::new);
        var form = new TaskForm(taskEntiry.summary(), taskEntiry.description(), taskEntiry.status().name());
        model.addAttribute("taskForm", form);
        return "tasks/form";
    }
}
