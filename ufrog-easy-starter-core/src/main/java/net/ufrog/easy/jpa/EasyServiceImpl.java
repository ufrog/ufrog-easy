package net.ufrog.easy.jpa;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.ufrog.easy.exceptions.DataNotFoundException;
import net.ufrog.easy.utils.ArrayUtil;
import net.ufrog.easy.utils.CollectionUtil;
import net.ufrog.easy.utils.DictUtil;
import net.ufrog.easy.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class EasyServiceImpl<T extends EasyModel> implements EasyService<T> {

    /** 仓库 */
    private EasyRepository<T> repository;

    /** 实体管理器 */
    private EntityManager entityManager;

    /** 查询工厂 */
    private JPAQueryFactory jpaQueryFactory;

    /** 审计识别 */
    private AuditorAware<Long> auditorAware;

    @Override
    public T getOne(long id) {
        return getRepository().getReferenceById(id);
    }

    @Override
    public Optional<T> findById(long id) {
        return getRepository().findById(id);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return getRepository().findOne(example);
    }

    @Override
    public Optional<T> findOne(Predicate predicate) {
        return getRepository().findOne(predicate);
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return getRepository().findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    @Override
    public List<T> findAll(Iterable<Long> ids) {
        return getRepository().findAllById(ids);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return getRepository().findAll(example);
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return getRepository().findAll(example, sort);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return getRepository().findAll(example, pageable);
    }

    @Override
    public List<T> findAll(Predicate predicate) {
        return CollectionUtil.toArrayList(getRepository().findAll(predicate));
    }

    @Override
    public List<T> findAll(Predicate predicate, Sort sort) {
        return CollectionUtil.toArrayList(getRepository().findAll(predicate, sort));
    }

    @Override
    public Page<T> findAll(Predicate predicate, Pageable pageable) {
        return getRepository().findAll(predicate, pageable);
    }

    @Override
    public long count() {
        return getRepository().count();
    }

    @Override
    public long count(Example<T> example) {
        return getRepository().count(example);
    }

    @Override
    public long count(Predicate predicate) {
        return getRepository().count(predicate);
    }

    @Override
    public boolean exists(long id) {
        return getRepository().existsById(id);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return getRepository().exists(example);
    }

    @Override
    public boolean exists(Predicate predicate) {
        return getRepository().exists(predicate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> S save(S entity) {
        onBeforeSave(entity);
        getRepository().save(entity);
        onAfterSave(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> S saveAndFlush(S entity) {
        onBeforeSave(entity);
        getRepository().saveAndFlush(entity);
        onAfterSave(entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> list = new ArrayList<>();
        entities.forEach(e -> {
            save(e);
            list.add(e);
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> List<S> saveInBatch(Iterable<S> entities) {
        int batchSize = 2000;
        AtomicInteger counter = new AtomicInteger(0);
        List<S> list = Collections.synchronizedList(new ArrayList<>(batchSize));
        List<S> temp = Collections.synchronizedList(new ArrayList<>(batchSize));

        // Batch save
        log.info("Start data saving...");
        entities.forEach(e -> {
            onBeforeSave(e);
            temp.add(e);
            if (counter.incrementAndGet() == batchSize) {
                list.addAll(saveList(temp));
                temp.clear();
                counter.set(0);
                getRepository().flush();
                log.debug("Saved and flushed {} records.", batchSize);
            }
        });

        // Save rest entities
        if (!temp.isEmpty()) {
            list.addAll(saveList(temp));
            log.debug("Saved and flushed last {} record(s).", temp.size());
        }
        log.info("Complete data saving, total {} record(s) saved.", list.size());
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> S update(long id, S entity, String... excludeFields) {
        List<String> lExclusion = ArrayUtil.isEmpty(excludeFields) ? ArrayUtil.toArrayList(EasyModel.AUDITOR_FIELDS) : ArrayUtil.toArrayList(excludeFields, EasyModel.AUDITOR_FIELDS);
        String[] exclusions = lExclusion.toArray(String[]::new);

        //noinspection unchecked
        return findById(id).map(o -> (S) save(ObjectUtil.copy(o, entity, true, false, exclusions)))
                .orElseThrow(() -> new DataNotFoundException(entity.getClass(), "id", id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        onBeforeDelete(id);
        getRepository().deleteById(id);
        onAfterDelete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicalDeleteById(long id) {
        findById(id).ifPresent(this::logicalDelete);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(T entity) {
        if (entity != null && entity.getId() != null) {
            onBeforeDelete(entity.getId());
            getRepository().delete(entity);
            onAfterDelete(entity.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logicalDelete(T entity) {
        if (entity != null && entity.getId() != null) {
            onBeforeDelete(entity.getId());
            entity.setIsDeleted(DictUtil.Bool.TRUE);
            entity.setDeleter(auditorAware.getCurrentAuditor().orElse(EasyModel.NULL));
            entity.setDeleteTime(new Date());
            save(entity);
            onAfterDelete(entity.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> void deleteAll(Iterable<S> entities) {
        entities.forEach(this::delete);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> void logicalDeleteAll(Iterable<S> entities) {
        entities.forEach(this::logicalDelete);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInBatch(Iterable<T> entities) {

    }

    @Override
    public void flush() {
        getRepository().flush();
    }

    @Override
    public EasyRepository<T> getRepository() {
        return repository;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public JPAQueryFactory getJPAQueryFactory() {
        if (jpaQueryFactory == null) {
            jpaQueryFactory = new JPAQueryFactory(entityManager);
        }
        return jpaQueryFactory;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void setRepository(EasyRepository<T> repository) {
        this.repository = repository;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void setAuditorAware(AuditorAware<Long> auditorAware) {
        this.auditorAware = auditorAware;
    }

    /**
     * 保存列表
     *
     * @param list 列表
     * @param <S> 实体泛型
     */
    private <S extends T> List<S> saveList(List<S> list) {
        getRepository().saveAll(list);
        list.forEach(this::onAfterSave);
        return list;
    }
}
