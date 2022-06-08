package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.ArticleTag;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.repositories.ArticleTagRepository;
import cn.itsuki.blog.repositories.TagRepository;
import graphql.kickstart.tools.GraphQLResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2022-04-27 13:32
 **/
@Slf4j
@Component
public class ArticleResolver implements GraphQLResolver<Article> {
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ArticleTagRepository articleTagRepository;

    public List<Tag> tags(Article article) {
        ArticleTag probe = new ArticleTag();
        probe.setArticleId(article.getId());
        List<ArticleTag> articleTags = articleTagRepository.findAll(Example.of(probe));
        return tagRepository.findAllById(articleTags.stream().map(ArticleTag::getTagId).collect(Collectors.toList()));
    }

}
