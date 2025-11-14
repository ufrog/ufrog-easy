package net.ufrog.easy.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 基础仓库
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-13
 * @since 3.5.3
 */
@NoRepositoryBean
public interface EasyRepository<T> extends JpaRepository<T, Long>, QuerydslPredicateExecutor<T> {
}
