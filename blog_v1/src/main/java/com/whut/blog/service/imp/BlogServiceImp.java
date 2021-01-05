package com.whut.blog.service.imp;

import com.whut.blog.dao.BlogRepository;
import com.whut.blog.exception.NotFoundException;
import com.whut.blog.po.Blog;
import com.whut.blog.service.BlogService;
import com.whut.blog.utils.MarkdownUtil;
import com.whut.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;


@Service
public class BlogServiceImp implements BlogService {

    @Autowired
    BlogRepository blogRepository;


    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public Blog getAndConvertBlog(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog == null){
            throw new NotFoundException("该博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b);
        String content = b.getContent();
        String text = MarkdownUtil.markdownToHtmlExtensions(content);
        blogRepository.updateViews(id);
        b.setContent(text);
        return b;
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(criteriaBuilder.like(root.get("title"), "%" + blog.getTitle() + "%"));
                }
                if (blog.getTypeId()!=null){
                    predicates.add(criteriaBuilder.equal(root.get("type").get("id"),blog.getTypeId()));
                }
                if (blog.isRecommend()){
                    predicates.add(criteriaBuilder.equal(root.get("recommend"),blog.isRecommend()));
                }
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Join join = root.join("tags");
                return criteriaBuilder.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable = PageRequest.of(0,size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> yearMap = new LinkedHashMap<>();
        for (String year : years) {
            yearMap.put(year,blogRepository.findByYear(year));
        }
        return yearMap;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null){
            blog.setCreatTime(new Date());
            blog.setViews(0);
        } else {
            Blog blog1 = this.getBlog(blog.getId());
            blog.setCreatTime(blog1.getCreatTime());
            blog.setViews(blog1.getViews());
        }
        blog.setUpdateTime(new Date());

        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.findById(id).orElse(null);
        if (b == null) {
            throw new NotFoundException("博客不存在");
        }
        BeanUtils.copyProperties(blog, b);
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Page<Blog> searchBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

}
