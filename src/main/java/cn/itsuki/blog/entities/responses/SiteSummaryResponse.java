package cn.itsuki.blog.entities.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 网站信息统计 响应类
 *
 * @author: itsuki
 * @create: 2021-11-13 16:41
 **/
@Getter
@Setter
public class SiteSummaryResponse {
    private long article;
    private long tag;
    private long reading;
    private long comment;
    private long guestbook;
    private LocalDateTime startTime;
}
