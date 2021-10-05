package cn.itsuki.blog.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author: itsuki
 * @create: 2021-10-05 16:29
 **/
@Entity(name = "black_email")
@Getter
@Setter
@ToString(callSuper = true)
public class BlackEmail extends IdentifiableEntity {
    @NotBlank
    @Email
    private String email;
}
