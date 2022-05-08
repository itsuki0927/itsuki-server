package cn.itsuki.blog.entities.requests;

import lombok.Data;

/**
 * @author: itsuki
 * @create: 2022-05-08 16:01
 **/
@Data
public class UpdateBlackListInput {
    private String ip;
    private String email;
    private String keyword;
}
