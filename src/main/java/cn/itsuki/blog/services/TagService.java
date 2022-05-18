package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.constants.PublishState;
import cn.itsuki.blog.entities.ArticleTag;
import cn.itsuki.blog.entities.Category;
import cn.itsuki.blog.entities.Tag;
import cn.itsuki.blog.entities.requests.TagActionInput;
import cn.itsuki.blog.entities.requests.TagSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.ArticleRepository;
import cn.itsuki.blog.repositories.ArticleTagRepository;
import cn.itsuki.blog.repositories.TagRepository;
import cn.itsuki.blog.utils.UrlUtil;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签 服务
 *
 * @author: itsuki
 * @create: 2021-09-21 18:18
 **/
@Service
public class TagService extends BaseService<Tag, TagSearchRequest> implements GraphQLQueryResolver, GraphQLMutationResolver {

    @Autowired
    private AdminService adminService;
    @Autowired
    private SeoService seoService;
    @Autowired
    private ArticleTagRepository articleTagRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UrlUtil urlUtil;

    /**
     * 创建一个service实例
     */
    public TagService() {
        super("id", "id", "sort");
    }

    private void ensureTagExist(Tag entity) {
        Tag probe = new Tag();
        probe.setName(entity.getName());
        Optional<Tag> optionalEntity = repository.findOne(Example.of(probe));
        if (optionalEntity.isPresent()) {
            throw new EntityExistsException("tag exist with name:" + entity.getName());
        }
    }

    @Override
    protected Page<Tag> searchWithPageable(TagSearchRequest criteria, Pageable pageable) {
        criteria = criteria == null ? new TagSearchRequest() : criteria;
        String name = criteria.getName();
        if (name != null) {
            return ((TagRepository) repository).findByNameContainingOrPathContaining(name, name, pageable);
        }
        return repository.findAll(pageable);
    }

    public Tag getTagByNameOrPath(String name) {
        Category probe = new Category();
        probe.setName(name);
        Tag tag = (((TagRepository) repository).findTagByNameEqualsOrPathEquals(name, name));
        if (tag == null) {
            throw new IllegalArgumentException("Tag 不存在");
        }
        return tag;
    }

    public SearchResponse<Tag> tags(TagSearchRequest criteria) {
        return search(criteria);
    }

    public Tag createTag(TagActionInput entity) {
        Tag probe = new Tag();
        BeanUtil.copyProperties(entity, probe);
        ensureTagExist(probe);
        adminService.ensureAdminOperate();

        seoService.push(urlUtil.getTagUrl(probe.getPath()));

        return super.create(probe);
    }

    public Tag updateTag(Long id, TagActionInput input) {
        Tag oldTag = get(id);
        Tag entity = new Tag();
        BeanUtil.copyProperties(input, entity);

        adminService.ensureAdminOperate();

        entity.setId(id);
        entity.setCount(oldTag.getCount());
        entity.setCreateAt(oldTag.getCreateAt());

        ensureTagExist(entity);
        validateEntity(entity);

        seoService.update(urlUtil.getTagUrl(entity.getPath()));

        return repository.saveAndFlush(entity);
    }

    public int deleteTag(Long id) {
        adminService.ensureAdminOperate();

        Tag tag = get(id);

        seoService.delete(urlUtil.getTagUrl(tag.getPath()));

        return super.delete(id);
    }

    public int syncAllTagCount() {
        List<Tag> tags = search(new TagSearchRequest()).getData();
        tags.forEach(tag -> syncTagCount(tag));
        return 1;
    }

    public int syncTagCount(Tag tag) {
        long id = tag.getId();
        // 找到当前tag的记录
        List<ArticleTag> articleTags = articleTagRepository.findAllByTagIdEquals(id);
        List<Long> ids = articleTags.stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
        // 获取当前tag的已发布文章数
        int count = articleRepository.countArticlesByIdInAndPublishEquals(ids, PublishState.Published);
        System.out.println(tag.getName() + " count: " + count);
        tag.setCount(count);
        update(id, tag);
        return 1;
    }
}

