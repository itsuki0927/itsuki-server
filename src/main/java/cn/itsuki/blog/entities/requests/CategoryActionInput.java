package cn.itsuki.blog.entities.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author: itsuki
 * @create: 2022-05-06 22:55
 **/
@Getter
@Setter
@ToString
public class CategoryActionInput {
    @NotBlank
    private String name;
    @NotBlank
    private String path;
    @NotBlank
    private String description;
    @Min(0)
    private Integer sort;
    private String expand;
}
