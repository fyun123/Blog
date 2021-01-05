package com.whut.blog.controller.admin;

import com.whut.blog.po.Tag;

import com.whut.blog.service.TagService;

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
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/tags")
    public String tags(@PageableDefault(size = 10,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){
        model.addAttribute("page",tagService.listTag(pageable));
        return "admin/tags";
    }
    @GetMapping("/tags/input")
    public String toAddTagsPage(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags-input";
    }
    @PostMapping("/tags")
    public String addTags(@Valid Tag tag, BindingResult result, RedirectAttributes attributes){
        Tag t = tagService.getTagByName(tag.getName());
        if (t != null){
            result.rejectValue("name","nameError","不能添加重复的标签");
        }
        if (result.hasErrors()){
            return "admin/tags-input";
        }
        Tag tag1 = tagService.saveTag(tag);
        if (tag1 == null){
            attributes.addFlashAttribute("message","新增失败");
        }else {
            attributes.addFlashAttribute("message","新增成功");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/input")
    public String toUpdateTagsPage(@PathVariable Long id,Model model){
        model.addAttribute("tag",tagService.getTag(id));
        return "admin/tags-input";
    }
    @PutMapping("/tags")
    public String updateTags(@Valid Tag tag, BindingResult result,Long id, RedirectAttributes attributes){
        Tag t1 = tagService.getTagByName(tag.getName());
        if (t1 != null){
            result.rejectValue("name","nameError","不能添加重复的标签");
        }
        if (result.hasErrors()){
            return "admin/tags-input";
        }
        Tag tag1 = tagService.updateTag(id,tag);
        System.out.println("更新");
        if (tag1 == null){
            attributes.addFlashAttribute("message","更新失败");
        }else {
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/tags";
    }
    @GetMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable Long id,RedirectAttributes attributes){
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags";
    }
}
