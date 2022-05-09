package cn.itsuki.blog.entities;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private LocalDateTime createAt;
    private String createAtString;
    private String description;

    public ArticleArchive(Long id, String title, LocalDateTime createAt, String description) {
        this.id = id;
        this.title = title;
        this.createAt = createAt;
        this.description = description;

        String date = LocalDateTimeUtil.format(createAt, "yyyy-MM-dd");
        this.setCreateAtString(date);
    }
}
