package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: itsuki
 * @create: 2022-04-01 09:25
 **/
@Getter
@Setter
public class BlogId {
    public BlogId(Long id) {
        this.id = id;
    }

    private Long id;
}
