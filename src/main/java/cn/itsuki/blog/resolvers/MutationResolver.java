package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.ArticleTag;
import cn.itsuki.blog.entities.requests.ArticleCreateRequest;
import cn.itsuki.blog.repositories.ArticleRepository;
import cn.itsuki.blog.repositories.ArticleTagRepository;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: itsuki
 * @create: 2022-04-28 07:08
 **/
@Component
public class MutationResolver implements GraphQLMutationResolver {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ArticleTagRepository articleTagRepository;

    private void saveAllTags(List<Long> tagIds, Long articleId) {
        if (tagIds != null) {
            List<ArticleTag> tagList = tagIds.stream().map(tagId -> {
                ArticleTag tag = new ArticleTag();
                tag.setTagId(tagId);
                tag.setArticleId(articleId);
                return tag;
            }).collect(Collectors.toList());
            articleTagRepository.saveAll(tagList);
        }
    }

    public Article createArticle(ArticleCreateRequest request) {
        Article entity = new Article();
        BeanUtils.copyProperties(request, entity);
        Article article = articleRepository.save(entity);

        saveAllTags(request.getTagIds(), article.getId());

        return article;
    }
}
