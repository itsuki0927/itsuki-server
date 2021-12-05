package cn.itsuki.blog.entities;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 文章归档
 *
 * @author: itsuki
 * @create: 2021-11-13 16:52
 **/
@Getter
@Setter
public class ArticleArchive {
    private Long id;
    private String title;
    private Date createAt;
    private String createAtString;
    private String description;

    public ArticleArchive(Long id, String title, Date createAt, String description) {
        this.id = id;
        this.title = title;
        this.createAt = createAt;
        this.description = description;

        String date = DateUtil.format(createAt, "yyyy-MM-dd");
        this.setCreateAtString(date);
    }
}