package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.repositories.CategoryRepository;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * @author: itsuki
 * @create: 2022-04-27 13:32
 **/
@Slf4j
@Component
public class CategoryResolver implements GraphQLResolver<Article> {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category category(Article article) {
        System.out.println(article.getCategoryId());
        Optional<Category> optionalCategory = categoryRepository.findById(article.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return null;
        }
        return optionalCategory.get();
    }

}
