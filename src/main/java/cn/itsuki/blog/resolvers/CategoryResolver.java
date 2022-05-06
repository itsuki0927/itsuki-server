package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.repositories.CategoryRepository;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: itsuki
 * @create: 2022-04-27 08:05
 **/
@Component
public class CategoryResolver implements GraphQLQueryResolver {
    @Autowired
    private CategoryRepository categoryRepository;

    public Iterable<Category> categories() {
        return categoryRepository.findAll();
    }
}
