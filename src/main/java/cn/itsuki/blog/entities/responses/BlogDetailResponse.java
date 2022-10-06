package cn.itsuki.blog.entities.responses;

import cn.itsuki.blog.entities.Blog;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: itsuki
 * @create: 2022-04-04 21:42
 **/
@Getter
@Setter
@ToString
public class BlogDetailResponse extends Blog {
    private Blog prevBlog;
    private Blog nextBlog;
}
