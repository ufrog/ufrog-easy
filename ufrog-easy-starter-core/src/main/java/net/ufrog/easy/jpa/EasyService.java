package net.ufrog.easy.jpa;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * 基础业务接口
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
public interface EasyService<T extends EasyModel> {

    /**
     * 按编号查询实体
     *
     * @param id 编号
     * @return 实体
     */
    T getOne(long id);

    /**
     * 按编号查询实体
     *
     * @param id 编号
     * @return 实体
     */
    Optional<T> findById(long id);

    /**
     * 按条件查询单个实体
     *
     * @param example 查询条件
     * @return 实体
     * @param <S> 实体泛型
     */
    <S extends T> Optional<S> findOne(Example<S> example);

    /**
     * 按条件查询单个实体
     *
     * @param predicate 查询条件
     * @return 实体
     */
    Optional<T> findOne(Predicate predicate);

    /**
     * 查询所有实体
     *
     * @return 实体列表
     */
    List<T> findAll();

    /**
     * 查询所有实体
     *
     * @param sort 排序
     * @return 实体列表
     */
    List<T> findAll(Sort sort);

    /**
     * 分页查询分页实体
     *
     * @param pageable 分页
     * @return 实体分页
     */
    Page<T> findAll(Pageable pageable);

    /**
     * 按编号查询所有实体
     *
     * @param ids 编号列表
     * @return 实体列表
     */
    List<T> findAll(Iterable<Long> ids);

    /**
     * 按条件查询所有实体
     *
     * @param example 查询条件
     * @return 实体列表
     * @param <S> 实体泛型
     */
    <S extends T> List<S> findAll(Example<S> example);

    /**
     * 按条件查询所有实体
     *
     * @param example 查询条件
     * @param sort 排序
     * @return 实体列表
     * @param <S> 实体泛型
     */
    <S extends T> List<S> findAll(Example<S> example, Sort sort);

    /**
     * 按条件查询分页实体
     *
     * @param example 查询条件
     * @param pageable 分页
     * @return 实体分页
     * @param <S> 实体泛型
     */
    <S extends T> Page<S> findAll(Example<S> example, Pageable pageable);

    /**
     * 按条件查询所有实体
     *
     * @param predicate 查询条件
     * @return 实体列表
     */
    List<T> findAll(Predicate predicate);

    /**
     * 按条件查询所有实体
     *
     * @param predicate 查询条件
     * @param sort 排序
     * @return 实体列表
     */
    List<T> findAll(Predicate predicate, Sort sort);

    /**
     * 按条件查询分页实体
     *
     * @param predicate 查询条件
     * @param pageable 分页
     * @return 实体分页
     */
    Page<T> findAll(Predicate predicate, Pageable pageable);

    /**
     * 统计实体数量
     *
     * @return 实体数量
     */
    long count();

    /**
     * 按条件统计实体数量
     *
     * @param example 查询条件
     * @return 实体数量
     */
    long count(Example<T> example);

    /**
     * 按条件统计实体数量
     *
     * @param predicate 查询条件
     * @return 实体数量
     */
    long count(Predicate predicate);

    /**
     * 判断实体是否存在
     *
     * @param id 编号
     * @return 判断结果
     */
    boolean exists(long id);

    /**
     * 判断实体是否存在
     *
     * @param example 查询条件
     * @return 判断结果
     * @param <S> 实体泛型
     */
    <S extends T> boolean exists(Example<S> example);

    /**
     * 判断实体是否存在
     *
     * @param predicate 查询条件
     * @return 判断结果
     */
    boolean exists(Predicate predicate);

    /**
     * 保存实体
     *
     * @param entity 实体
     * @return 保存后的实体
     * @param <S> 实体泛型
     */
    <S extends T> S save(S entity);

    /**
     * 保存并冲刷实体
     *
     * @param entity 实体
     * @return 保存后的实体
     * @param <S> 实体泛型
     */
    <S extends T> S saveAndFlush(S entity);

    /**
     * 保存所有实体
     *
     * @param entities 实体列表
     * @return 实体列表
     * @param <S> 实体泛型
     */
    <S extends T> List<S> saveAll(Iterable<S> entities);

    /**
     * 批量保存所有实体
     *
     * @param entities 实体列表
     * @return 实体列表
     * @param <S> 实体泛型
     */
    <S extends T> List<S> saveInBatch(Iterable<S> entities);

    /**
     * 更新实体
     *
     * @param id 编号
     * @param entity 实体
     * @param excludeFields 排除字段
     * @return 更新后实体
     * @param <S> 实体泛型
     */
    <S extends T> S update(long id, S entity, String... excludeFields);

    /**
     * 通过编号删除实体
     *
     * @param id 编号
     */
    void deleteById(long id);

    /**
     * 通过编号逻辑删除实体
     *
     * @param id 编号
     */
    void logicalDeleteById(long id);

    /**
     * 删除实体
     *
     * @param entity 实体
     */
    void delete(T entity);

    /**
     * 逻辑删除实体
     *
     * @param entity 实体
     */
    void logicalDelete(T entity);

    /**
     * 删除所有实体
     *
     * @param entities 实体列表
     * @param <S> 实体泛型
     */
    <S extends T> void deleteAll(Iterable<S> entities);

    /**
     * 逻辑删除所有实体
     *
     * @param entities 实体列表
     * @param <S> 实体泛型
     */
    <S extends T> void logicalDeleteAll(Iterable<S> entities);

    /**
     * 批量删除实体
     *
     * @param entities 实体列表
     */
    void deleteInBatch(Iterable<T> entities);

    /** 冲刷 */
    void flush();

    /**
     * 读取仓库
     *
     * @return 仓库
     */
    EasyRepository<T> getRepository();

    /**
     * 读取实体管理器
     *
     * @return 实体管理器
     */
    EntityManager getEntityManager();

    /**
     * 读取查询工厂
     *
     * @return 查询工厂
     */
    JPAQueryFactory getJPAQueryFactory();

    /**
     * 保存前回调
     *
     * @param entity 实体
     * @param <S> 实体泛型
     */
    default <S extends T> void onBeforeSave(S entity) {}

    /**
     * 保存后回调
     *
     * @param entity 实体
     * @param <S> 实体泛型
     */
    default <S extends T> void onAfterSave(S entity) {}

    /**
     * 删除前回调
     *
     * @param id 编号
     */
    default void onBeforeDelete(long id) {}

    /**
     * 删除后回调
     *
     * @param id 编号
     */
    default void onAfterDelete(long id) {}
}
