package cn.itsuki.blog.entities;

import cn.itsuki.blog.entities.requests.RoleDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * ๆ้ๅ่กจ
 *
 * @author: itsuki
 * @create: 2021-09-16 09:04
 **/
@JsonDeserialize(using = RoleDeserializer.class)
public enum Role {
    ADMIN
}
