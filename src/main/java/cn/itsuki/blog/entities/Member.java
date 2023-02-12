package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member {
    private String email;
    private String nickname;
    private String avatar;
    private String uid;
    private String token;
    private String provider;
}
