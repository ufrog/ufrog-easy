package net.ufrog.easy.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import net.ufrog.easy.utils.DictUtil;
import net.ufrog.easy.utils.ObjectUtil;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * 基础模型
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2025-11-12
 * @since 3.5.3
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class EasyModel implements Serializable, Persistable<Long> {

    public static final String[] AUDITOR_FIELDS = ObjectUtil.getAllDeclaredFields(EasyModel.class).stream().map(Field::getName).toArray(String[]::new);
    public static final Long NULL               = -1L;

    @Serial
    private static final long serialVersionUID = -5549163150950069455L;

    /** 编号 */
    @Id
    @Column(name = "pk_id")
    @SequenceID
    private Long id;

    /** 创建用户 */
    @Column(name = "fk_creator")
    @CreatedBy
    private Long creator;

    /** 创建时间 */
    @Column(name = "dt_create_time")
    @CreatedDate
    private Date createTime;

    /** 更新用户 */
    @Column(name = "fk_updater")
    @LastModifiedBy
    private Long updater;

    /** 更新时间 */
    @Column(name = "dt_update_time")
    @LastModifiedDate
    private Date updateTime;

    /** 是否删除 */
    @Column(name = "dc_is_deleted")
    private String isDeleted = DictUtil.Bool.FALSE;

    /** 删除用户 */
    @Column(name = "fk_deleter")
    @CreatedBy
    private Long deleter;

    /** 删除时间 */
    @Column(name = "dt_delete_time")
    @CreatedDate
    private Date deleteTime;

    @Override
    public boolean isNew() {
        return createTime == null;
    }
}
