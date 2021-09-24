package cn.itsuki.blog.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * 具有 id 属性的所有实体的基类
 *
 * @author: itsuki
 * @create: 2021-09-14 19:43
 **/
@MappedSuperclass
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class IdentifiableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_at")
    private Date createAt;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_at")
    private Date updateAt;

    @PrePersist
    protected void onCreate() {
        setCreateAt(new Date());
        setUpdateAt(new Date());
        setId(null);
        onCreateAction();
    }

    /**
     * create之前的action
     */
    protected void onCreateAction() {
    }

    /**
     * update之前的action
     */
    protected void onUpdateAction() {

    }

    @PreUpdate
    protected void onUpdate() {
        setUpdateAt(new Date());
        onUpdateAction();
    }
}
