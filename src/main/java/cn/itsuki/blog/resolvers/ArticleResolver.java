package cn.itsuki.blog.resolvers;

import cn.itsuki.blog.entities.Article;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.ArticleSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.ArticleRepository;
import cn.itsuki.blog.repositories.CategoryRepository;
import cn.itsuki.blog.repositories.TagRepository;
import cn.itsuki.blog.services.BaseService;
import cn.itsuki.blog.services.OffsetLimitPageRequest;
import graphql.GraphqlErrorException;
import graphql.execution.DataFetcherResult;
import graphql.kickstart.execution.error.GenericGraphQLError;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: itsuki
 * @create: 2022-04-27 13:24
 **/
@Slf4j
@Component
public class ArticleResolver extends BaseService<Article, ArticleSearchRequest> implements GraphQLQueryResolver {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TagRepository tagRepository;

    /**
     * 创建一个service实例
     */
    public ArticleResolver() {
        super("id", "id");
    }

    public SearchResponse<Article> articles(ArticleSearchRequest criteria) {
        return search(criteria);
    }

    public List<Tag> tags() {
        return tagRepository.findAll();
    }

    @Override
    protected Page<Article> searchWithPageable(ArticleSearchRequest criteria, Pageable pageable) {
        Page<Article> articles = articleRepository.search2(criteria.getName(), criteria.getPublish(), criteria.getOrigin(),
                criteria.getOpen(), criteria.getBanner(), PageRequest.of(criteria.getCurrent(), criteria.getPageSize()));
        return articles;
    }
}
