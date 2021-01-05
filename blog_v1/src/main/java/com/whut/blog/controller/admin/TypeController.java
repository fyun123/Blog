package com.whut.blog.controller.admin;

import com.whut.blog.po.Type;
import com.whut.blog.service.TypeService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    @GetMapping("/types")
    public String types(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){
        model.addAttribute("page",typeService.listType(pageable));
        return "admin/types";
    }
    @GetMapping("/types/input")
    public String toAddTypesPage(Model model){
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }
    @PostMapping("/types")
    public String addTypes(@Valid Type type, BindingResult result, RedirectAttributes attributes){
        Type t = typeService.getTypeByName(type.getName());
        if (t != null){
            result.rejectValue("name","nameError","不能添加重复的分类");
        }
        if (result.hasErrors()){
            return "admin/types-input";
        }
        Type type1 = typeService.saveType(type);
        if (type1 == null){
            attributes.addFlashAttribute("message","新增失败");
        }else {
            attributes.addFlashAttribute("message","新增成功");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/input")
    public String toUpdateTypesPage(@PathVariable Long id,Model model){
        model.addAttribute("type",typeService.getType(id));
        return "admin/types-input";
    }
    @PutMapping("/types")
    public String updateTypes(@Valid Type type, BindingResult result,Long id, RedirectAttributes attributes){
        Type t1 = typeService.getTypeByName(type.getName());
        if (t1 != null){
            result.rejectValue("name","nameError","不能添加重复的分类");
        }
        if (result.hasErrors()){
            return "admin/types-input";
        }
        Type type1 = typeService.updateType(id,type);
        System.out.println("更新");
        if (type1 == null){
            attributes.addFlashAttribute("message","更新失败");
        }else {
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/types";
    }
    @GetMapping("/types/{id}/delete")
    public String deleteType(@PathVariable Long id,RedirectAttributes attributes){
        typeService.deleteType(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/types";
    }
}
