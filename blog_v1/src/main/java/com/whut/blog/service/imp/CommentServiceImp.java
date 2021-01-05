package com.whut.blog.service.imp;

import com.whut.blog.dao.CommentRepository;
import com.whut.blog.po.Comment;
import com.whut.blog.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImp implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = Sort.by("creatTime");
        List<Comment> comments = commentRepository.findCommentsByBlogIdAndParentCommentNull(blogId, sort);
        List<Comment> comments1 = eachComment(comments);
        return comments1;
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId = comment.getParentComment().getId();
        if (parentCommentId != -1) {
            comment.setParentComment(commentRepository.findById(parentCommentId).orElse(null));
        } else {
            comment.setParentComment(null);
        }
        comment.setCreatTime(new Date());
        return commentRepository.save(comment);
    }

    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment, c);
            commentsView.add(c);
        }
        //合并评论的各层子代到第一级子代集合
        combineChildren(commentsView);
        return commentsView;
    }

    private void combineChildren(List<Comment> comments){
        for (Comment comment : comments){
            List<Comment> replyComments = comment.getReplyComments();
            for (Comment replyComment : replyComments){
                //循环迭代找出子代，存放在tempReplys中
                recursively(replyComment);
            }
            //修改顶节点的reply集合为迭代处理后的集合
            comment.setReplyComments(tempReplays);
            //清除临时缓存区
            tempReplays = new ArrayList<>();
        }
    }

    //存放迭代找出的所有子代的集合
    private List<Comment> tempReplays = new ArrayList<>();

    private void recursively(Comment comment) {
        tempReplays.add(comment);
        if (comment.getReplyComments().size() > 0) {
            List<Comment> replys = comment.getReplyComments();
            for (Comment reply : replys) {
                tempReplays.add(reply);
                if (reply.getReplyComments().size()>0) {
                    recursively(reply);
                }
            }
        }
    }

}
