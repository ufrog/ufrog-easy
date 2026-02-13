package net.ufrog.easy.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 链表工具
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 3.5.3, 2026-02-13
 * @since 3.5.3
 */
@Slf4j
public class LinkUtil {

    /** 构造函数<br>不允许外部构造 */
    private LinkUtil() {}

    /**
     * 排序链块
     *
     * @param blocks 链块列表
     * @param lastId 最终链块编号
     * @return 排序后的链块列表
     * @param <T> 链块类型范型
     * @param <ID> 链块编号类型范型
     */
    public static <T extends Block<ID>, ID> List<T> sort(final List<T> blocks, final ID lastId) {
        if (blocks == null || blocks.isEmpty()) {
            log.debug("Link block is empty.");
            return blocks;
        } else {
            List<T> sorted = new ArrayList<>(blocks.size());
            Map<ID, T> map = blocks.stream().collect(Collectors.toMap(item -> getNextId(item, lastId), item -> item));
            for (T current = map.get(lastId); current != null; current = map.get(current.getId())) {
                sorted.add(0, current);
            }
            if (log.isDebugEnabled()) log.debug("Link blocks: {}", sorted.stream().map(item -> String.valueOf(item.getId())).collect(Collectors.joining(" -> ")));
            return sorted;
        }
    }

    /**
     * 读取链块相邻编号
     *
     * @param block 链块
     * @param lastId 最终链块编号
     * @return 链块相邻编号
     * @param <ID> 链块编号类型范型
     */
    public static <ID> ID getNextId(final Block<ID> block, final ID lastId) {
        if (block == null || block.getNextId() == null || (lastId != null && lastId.equals(block.getNextId()))) {
            return lastId;
        } else {
            return block.getNextId();
        }
    }

    /**
     * 链块前移
     *
     * @param block 链块
     * @param prev 前链块
     * @param xPrev 前前链块
     * @param <ID> 链块编号类型范型
     */
    public static <ID> void moveToPrev(final Block<ID> block, final Block<ID> prev, final Block<ID> xPrev) {
        Assert.notNull(block, "Cannot move null block.");
        Assert.notNull(prev, "Cannot move to prev with first block.");

        prev.setNextId(block.getNextId());
        block.setNextId(prev.getId());
        if (xPrev != null) {
            xPrev.setNextId(block.getId());
        }
    }

    /**
     * 链块后移
     *
     * @param block 链块
     * @param prev 前链块
     * @param next 后链块
     * @param <ID> 链块编号类型范型
     */
    public static <ID> void moveToNext(final Block<ID> block, final Block<ID> prev, final Block<ID> next) {
        Assert.notNull(block, "Cannot move null block.");
        Assert.notNull(next, "Cannot move to next with last block.");

        block.setNextId(next.getNextId());
        next.setNextId(block.getId());
        if (prev != null) {
            prev.setNextId(next.getId());
        }
    }

    /**
     * 链块接口
     *
     * @author ultrafrog, ufrog.net@gmail.com
     * @version 3.5.3, 2026-02-13
     * @since 3.5.3
     */
    public interface Block<ID> {

        /**
         * 读取块编号
         *
         * @return 块编号
         */
        ID getId();

        /**
         * 读取相邻块编号
         *
         * @return 相邻块编号
         */
        ID getNextId();

        /**
         * 设置相邻块编号
         *
         * @param nextId 相邻块编号
         */
        void setNextId(ID nextId);
    }
}
