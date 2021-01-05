package com.whut.blog.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.whut.blog.po.Blog;
import com.whut.blog.po.User;
import com.whut.blog.service.BlogService;
import com.whut.blog.service.TagService;
import com.whut.blog.service.TypeService;
import com.whut.blog.vo.BlogQuery;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.UUID;


@Controller
@RequestMapping("/admin")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size=2,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model){
        model.addAttribute("types",typeService.allType());
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs";
    }
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size=2,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model){
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs :: blogList";
    }
    private void setTypeAndTag(Model model){
        model.addAttribute("types",typeService.allType());
        model.addAttribute("tags",tagService.allTag());
    }

    @GetMapping("/blogs/input")
    public String toBlogInputPage(Model model){
        setTypeAndTag(model);
        model.addAttribute("blog",new Blog());
        return "admin/blogs-input";
    }

    @PostMapping("/blogs")
    public String addBlog(Blog blog, RedirectAttributes attributes, HttpSession session){
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.tags(blog.getTagIds()));
        Blog blog1 = blogService.saveBlog(blog);
        if (blog1 == null){
            attributes.addFlashAttribute("message","操作失败");
        }else {
            attributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/blogs";
    }

    @GetMapping("/blogs/{id}/input")
    public String toBlogEditPage(@PathVariable Long id, Model model){
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog",blog);
        return "admin/blogs-input";
    }

    @GetMapping("/blogs/{id}/delete")
    public String deleteBlog(@PathVariable Long id,RedirectAttributes attributes){
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/blogs";
    }

    @PostMapping("/uploadfile")
    @ResponseBody
    public JSONObject hello(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "editormd-image-file", required = false) MultipartFile attach) throws JSONException {

        JSONObject jsonObject=new JSONObject();

        try {
            request.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "text/html");
            String path = ResourceUtils.getURL("classpath:").getPath()+"static/asserts/images";
            /**
             * 文件路径不存在则需要创建文件路径
             */
            File filePath = new File(path);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            UUID uuid = UUID.randomUUID();
            // 最终文件名
            File realFile = new File(path, uuid + "_" + attach.getOriginalFilename());
//            FileUtils.copyInputStreamToFile(attach.getInputStream(), realFile);
            attach.transferTo(realFile);
            // 下面response返回的json格式是editor.md所限制的，规范输出就OK
            jsonObject.put("success", 1);
            jsonObject.put("message", "上传成功");
            jsonObject.put("url", "/asserts/images/"+uuid + "_" + attach.getOriginalFilename());

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("success", 0);
        }

        return jsonObject;
    }
}
