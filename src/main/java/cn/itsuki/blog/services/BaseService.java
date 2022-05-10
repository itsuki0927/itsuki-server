package cn.itsuki.blog.services;

import cn.itsuki.blog.entities.IdentifiableEntity;
import cn.itsuki.blog.entities.requests.BaseSearchRequest;
import cn.itsuki.blog.entities.responses.SearchResponse;
import cn.itsuki.blog.repositories.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 基础 服务
 *
 * @author: itsuki
 * @create: 2021-09-14 19:49
 **/
@Transactional
public abstract class BaseService<T extends IdentifiableEntity, S extends BaseSearchRequest> {
    // 第一页下标
    protected static final int FIRST_PAGE_NO = 0;
    // 默认搜索下标页码
    protected static final int DEFAULT_SEARCH_CURRENT = 0;
    // 默认搜索数量
    protected static final int DEFAULT_SEARCH_SIZE = 20;
    // 默认搜索排序
    protected static final String DEFAULT_SEARCH_SORT_DESC = "ASC";
    // 有效的排序列表
    protected static final List<String> VALID_SORT_ORDERS = Arrays.asList("asc", "desc", "ASC", "DESC");
    // 有效的排序列
    private final List<String> validSortColumns;
    // 默认排序列
    private final String defaultSortColumn;
    // 实例
    @Autowired
    protected BaseRepository<T> repository;

    /**
     * 创建一个service实例
     *
     * @param defaultSortColumn 默认排序列
     * @param validSortColumns  有效的排序列
     */
    public BaseService(String defaultSortColumn, String... validSortColumns) {
        this.defaultSortColumn = defaultSortColumn;
        this.validSortColumns = Arrays.asList(validSortColumns);
    }

    /**
     * create
     *
     * @param entity 实体数据
     * @return 创建好的实体数据
     */
    public T create(T entity) {
        entity.setId(null);
        validateEntity(entity);
        return repository.saveAndFlush(entity);
    }

    /**
     * update
     *
     * @param id     实体id
     * @param entity 实体数据
     * @return 更新后的实体数据
     */
    public T update(long id, T entity) {
        ensureExist(repository, id, "Entity");
        entity.setId(id);
        validateEntity(entity);
        return repository.saveAndFlush(entity);
    }

    /**
     * 通过id获取数据
     *
     * @param id 实体id
     * @return 实体数据
     */
    @Transactional
    public T get(long id) {
        return ensureExist(repository, id, "Entity");
    }

    /**
     * 通过id删除数据
     *
     * @param id 实体id
     */
    public int delete(long id) {
        ensureExist(repository, id, "Entity");
        repository.deleteById(id);
        return 1;
    }

    /**
     * 搜索
     *
     * @param criteria 搜索标准
     * @return 分页结果
     */
    @Transactional
    public SearchResponse<T> search(S criteria) {
        Pageable pageable = createPageRequest(criteria);

        // 将分页标准拷贝给响应体
        SearchResponse<T> searchResponse = new SearchResponse<>();
        searchResponse.setFilter(criteria);

        Page<T> page = searchWithPageable(criteria, pageable);

        searchResponse.setTotal(page.getTotalElements());
        searchResponse.setData(page.getContent());

        return searchResponse;
    }

    /**
     * 分页的响应体实例
     *
     * @param criteria 分页标准
     * @param pageable 分页
     * @return 分页结果
     */
    protected abstract Page<T> searchWithPageable(S criteria, Pageable pageable);


    protected Pageable createPageRequest(S criteria) {
        if (criteria == null) {
            criteria = (S) new BaseSearchRequest();
        }
        // 设置默认下标
        if (criteria.getCurrent() == null) {
            criteria.setCurrent(DEFAULT_SEARCH_CURRENT);
        }

        // 设置默认size
        if (criteria.getPageSize() == null) {
            criteria.setPageSize(DEFAULT_SEARCH_SIZE);
        }

        // 设置sortOrder
        if (StringUtils.isEmpty(criteria.getSortOrder())) {
            criteria.setSortOrder(DEFAULT_SEARCH_SORT_DESC);
        } else {
            validateStringInList(criteria.getSortOrder(), "sortOrder", VALID_SORT_ORDERS);
            criteria.setSortOrder(criteria.getSortOrder().toUpperCase());
        }

        // 设置sortBy
        if (StringUtils.isEmpty(criteria.getSortBy())) {
            criteria.setSortBy(this.defaultSortColumn);
        } else {
            validateStringInList(criteria.getSortBy(), "sortBy", this.validSortColumns);
        }

        Sort sort = Sort.by("ASC".equals(criteria.getSortBy()) ? Sort.Direction.ASC : Sort.Direction.DESC, criteria.getSortBy());
        int current = getSearchCurrent(criteria.getCurrent());
        int limit = criteria.getPageSize();
        Pageable pageable = new OffsetLimitPageRequest(current, limit, sort);
        return pageable;
    }

    private int getSearchCurrent(int current) {
        if (current <= FIRST_PAGE_NO) {
            return FIRST_PAGE_NO;
        }
        // current 是从 1 开始算的, 所以需要 - 1
        return (current - 1);
    }

    /**
     * 验证list当是否包含propertyName
     *
     * @param propertyName  属性名
     * @param propertyValue 属性值
     * @param list          验证列表
     */
    private void validateStringInList(Object propertyName, String propertyValue, List<String> list) {
        if (!list.contains(propertyValue)) {
            throw new IllegalArgumentException(String.format("%s must be in %s", propertyName, list));
        }
    }

    /**
     * 检查是否在数据库里
     *
     * @param id         id
     * @param entityName 实体名称
     * @return 实体
     * @throws IllegalArgumentException 如果id为空、或者<=0
     * @throws EntityNotFoundException  如果数据不存在
     */
    public static <E extends IdentifiableEntity> E ensureExist(BaseRepository<E> repository, Long id, String entityName) {
        if (id == null) {
            throw new IllegalArgumentException(entityName + " id不能为空");
        }
        if (id <= 0) {
            throw new IllegalArgumentException(entityName + " id必须>0");
        }
        Optional<E> optionalEntity = repository.findById(id);
        if (!optionalEntity.isPresent()) {
            throw new EntityNotFoundException(String.format("%s does not exist with id %s", entityName, id));
        }
        return optionalEntity.get();
        // return repository.getById(id);
    }

    /**
     * 验证实体数据
     *
     * @param entity 实体数据
     */
    protected void validateEntity(T entity) {

    }
}
