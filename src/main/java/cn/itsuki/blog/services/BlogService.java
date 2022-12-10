package cn.itsuki.blog.services;

import cn.hutool.core.bean.BeanUtil;
import cn.itsuki.blog.constants.PublishState;
import cn.itsuki.blog.entities.*;
import cn.itsuki.blog.entities.requests.*;
import cn.itsuki.blog.entities.BlogSummary;
import cn.itsuki.blog.entities.responses.BlogDetailResponse;
import cn.itsuki.blog.entities.responses.BlogSummaryResponse;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.*;
import cn.itsuki.blog.utils.UrlUtil;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章 服务
 *
 * @author: itsuki
 * @create: 2021-09-20 22:19
 **/
@Service
public class BlogService extends BaseService<Blog, SearchBlogInput> implements GraphQLQueryResolver, GraphQLMutationResolver {
    @Autowired
    private AdminService adminService;
    @Autowired
    private BlogTagRepository blogTagRepository;
    @Autowired
    private TagService tagService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private SeoService seoService;
    @Autowired
    private UrlUtil urlUtil;

    /**
     * 创建一个service实例
     */
    public BlogService() {
        super("createAt", "commenting", "liking", "reading", "createdAt");
    }

    private void saveAllTags(List<Long> tagIds, Long blogId) {
        if (tagIds != null) {
            List<BlogTag> tagList = tagIds.stream().map(tagId -> {
                BlogTag tag = new BlogTag();
                tag.setTagId(tagId);
                tag.setBlogId(blogId);
                return tag;
            }).collect(Collectors.toList());
            blogTagRepository.saveAll(tagList);
        }
    }

    private void deleteTag(long blogId) {
        blogTagRepository.deleteAllByBlogIdEquals(blogId);
    }

    private void ensureBlogAllowOperate(Blog blog) {
        if (blog.getPublish() != PublishState.Published) {
            throw new IllegalArgumentException("文章还没发布");
        }
    }

    public Page<Blog> recentBlogs() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createAt");
        Pageable pageable = new OffsetLimitPageRequest(0, 6, sort);
        Blog probe = new Blog();
        probe.setPublish(PublishState.Published);
        return repository.findAll(Example.of(probe), pageable);
    }

    public Page<Blog> hotBlogs() {
        Sort sort = Sort.by(Sort.Direction.DESC, "reading");
        Pageable pageable = new OffsetLimitPageRequest(0, 3, sort);
        Blog probe = new Blog();
        probe.setPublish(PublishState.Published);
        return repository.findAll(Example.of(probe), pageable);
    }

    private Tag getTag(String name) {
        Tag tag = tagService.getTagByNameOrPath(name);
        if (tag == null) {
            throw new EntityNotFoundException("Tag 不存在");
        }
        return tag;
    }

    private Long getSearchTagId(SearchBlogInput request) {
        Long tagId = request.getTagId();
        if (tagId == null && request.getTagPath() != null) {
            tagId = getTag(request.getTagPath()).getId();
        }
        return tagId;
    }

    @Override
    protected Page<Blog> searchWithPageable(SearchBlogInput criteria, Pageable pageable) {
        criteria = Optional.ofNullable(criteria).orElse(new SearchBlogInput());

        if (criteria.getRecent() != null && criteria.getRecent()) {
            return recentBlogs();
        }

        if (criteria.getHot() != null && criteria.getHot()) {
            return hotBlogs();
        }

        Long tagId = getSearchTagId(criteria);

        return ((BlogRepository) repository).search(criteria.getName(), criteria.getPublish(), tagId, criteria.getBanner(), pageable);
    }

    public Integer count(SearchBlogInput criteria) {
        Long tagId = criteria.getTagId();
        if (tagId == null && criteria.getTagPath() != null) {
            tagId = tagService.getTagByNameOrPath(criteria.getTagPath()).getId();
        }
        return ((BlogRepository) repository).count(criteria.getName(), criteria.getPublish(), tagId, criteria.getBanner());
    }

    public BlogSummaryResponse blogSummary() {
        List<BlogSummary> summaries = ((BlogRepository) repository).summary();

        BlogSummaryResponse response = new BlogSummaryResponse();
        summaries.forEach(summary -> {
            if (summary.getPublish() == PublishState.Draft) {
                summary.setTitle("草稿");
                summary.setState("warning");
                response.setDraft(summary);
            } else if (summary.getPublish() == PublishState.Published) {
                summary.setTitle("已发布");
                summary.setState("success");
                response.setPublished(summary);
            } else if (summary.getPublish() == PublishState.Recycle) {
                summary.setTitle("回收站");
                summary.setState("error");
                response.setRecycle(summary);
            }
        });
        BlogSummary total = new BlogSummary(0, summaries.stream().map(BlogSummary::getValue).reduce(0L, Long::sum));
        total.setState("processing");
        total.setTitle("全部");
        response.setTotal(total);
        return response;
    }

    private BlogDetailResponse getPreviousAndNextBlog(Blog blog, DataFetchingEnvironment environment) {
        BlogDetailResponse response = new BlogDetailResponse();
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();
        BeanUtil.copyProperties(blog, response);

        if (selectionSet.contains("prevBlog")) {
            response.setPrevBlog(((BlogRepository) repository).prev(blog.getId()));
        }
        if (selectionSet.contains("nextBlog")) {
            response.setNextBlog(((BlogRepository) repository).next(blog.getId()));
        }

        return response;
    }

    private Blog getBlogByPath(String path) {
        Blog probe = new Blog();
        probe.setPath(path);
        Optional<Blog> optionalBlog = repository.findOne(Example.of(probe));
        if (optionalBlog.isEmpty()) {
            throw new EntityNotFoundException("未找到" + path + "相关的文章");
        }
        return optionalBlog.get();
    }

    public BlogDetailResponse blog(String path, DataFetchingEnvironment environment) {
        Blog blog = getBlogByPath(path);
        return getPreviousAndNextBlog(blog, environment);
    }

    public SearchResponse<Blog> blogs(SearchBlogInput criteria) {
        return search(criteria);
    }

    public List<Long> getBlogTagIds(Long blogId) {
        return blogTagRepository.findAllByBlogIdEquals(blogId).stream().map(BlogTag::getTagId).collect(Collectors.toList());
    }

    public Blog createBlog(BlogCreateRequest request) {
        adminService.ensureAdminOperate();

        Blog entity = new Blog();
        BeanUtils.copyProperties(request, entity);
        Blog blog = super.create(entity);

        saveAllTags(request.getTagIds(), blog.getId());

        // 更新tag count
        updateTagCount(request.getTagIds());

        if (request.getPublish() == PublishState.Published) {
            seoService.push(urlUtil.getBlogUrl(blog.getPath()));
        }

        return blog;
    }

    /**
     * 更新文章标签count
     *
     * @param tagIds 标签id数组
     */
    private void updateTagCount(List<Long> tagIds) {
        tagIds.forEach(tagId -> {
            Tag tag = tagService.get(tagId);
            tagService.syncTagCount(tag);
        });
    }

    public Blog updateBlog(Long id, BlogCreateRequest entity) {
        adminService.ensureAdminOperate();

        Blog blog = get(id);

        List<Long> oldTagIds = getBlogTagIds(id);
        List<Long> newTagIds = entity.getTagIds();
        // 删除当前文章的tag
        deleteTag(id);
        // 添加新的tag
        saveAllTags(newTagIds, id);

        // 更新文章
        BeanUtil.copyProperties(entity, blog);
        Blog update = super.update(id, blog);

        updateTagCount(newTagIds);
        // 更新旧的tag count
        updateTagCount(oldTagIds);

        seoService.update(urlUtil.getBlogUrl(blog.getPath()));

        return update;
    }

    public int deleteBlog(Long blogId) {
        adminService.ensureAdminOperate();

        Blog oldBlog = get(blogId);
        List<Long> oldTagIds = getBlogTagIds(blogId);

        deleteTag(blogId);
        commentService.deleteBlogComments(blogId);
        seoService.delete(urlUtil.getBlogUrl(oldBlog.getPath()));
        updateTagCount(oldTagIds);

        return super.delete(blogId);
    }

    public int updateBlogState(List<Long> ids, Integer publish) {
        adminService.ensureAdminOperate();

        int state = ((BlogRepository) repository).batchUpdateState(publish, ids);

        ids.forEach(id -> {
            List<Long> tagIds = getBlogTagIds(id);
            updateTagCount(tagIds);
        });

        return state;
    }

    public int updateBlogBanner(List<Long> ids, Integer banner) {
        adminService.ensureAdminOperate();

        List<Blog> blogs = repository
                .findAllById(ids).stream()
                .filter(v -> v.getPublish() == PublishState.Published)
                .peek(blog -> blog.setBanner(banner)).collect(Collectors.toList());

        repository.saveAll(blogs);
        return blogs.size();
    }

    public int readBlogByPath(String path) {
        Blog blog = getBlogByPath(path);
        ensureBlogAllowOperate(blog);
        blog.setReading(blog.getReading() + 1);

        repository.saveAndFlush(blog);
        return blog.getReading();
    }

    public int readBlog(Long id) {
        Blog blog = get(id);
        ensureBlogAllowOperate(blog);
        blog.setReading(blog.getReading() + 1);

        repository.saveAndFlush(blog);
        return blog.getReading();
    }

    public int likeBlog(Long id, int count) {
        Blog blog = get(id);
        ensureBlogAllowOperate(blog);
        if(count > 50){
            throw new IllegalArgumentException("count should be < 50");
        }
        blog.setLiking(blog.getLiking() + count);

        repository.saveAndFlush(blog);
        return blog.getLiking();
    }

    public int syncBlogCommentCount(List<Long> blogIds) {
        blogIds.forEach(blogId -> commentService.updateBlogCommentCount(blogId));
        return 1;
    }
}