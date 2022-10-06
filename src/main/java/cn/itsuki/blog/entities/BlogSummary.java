package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;

/**
 * 文章统计
 *
 * @author: itsuki
 * @create: 2021-11-13 16:52
 **/
@Getter
@Setter
public class BlogSummary {
    private Integer publish;
    private Long value;
    private String title;
    private String state;

    public BlogSummary(Integer publish, Long value) {
        this.publish = publish;
        this.value = value;
    }
}
