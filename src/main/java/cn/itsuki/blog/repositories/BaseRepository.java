package cn.itsuki.blog.repositories;

import cn.itsuki.blog.entities.IdentifiableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 所有repository基类
 *
 * @author: itsuki
 * @create: 2021-09-14 19:47
 **/
@NoRepositoryBean
public interface BaseRepository<T extends IdentifiableEntity> extends JpaRepository<T, Long> {
}
