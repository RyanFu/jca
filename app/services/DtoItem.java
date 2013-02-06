package services;

import java.io.Serializable;
import java.util.Collection;

/**
 * 数据传输对象实例
 *
 * @author wujinliang
 * @since 9/25/12
 */
public class DtoItem implements Serializable {
    /** 数据 */
    public Collection results;

    /** 总数 */
    public long total;

    /** 一页个数 */
    public int psize;

    /** 当前页 */
    public int page;

    public DtoItem() {
    }

    public DtoItem(Collection results, long total, int psize, int page) {
        this.results = results;
        this.total = total;
        this.psize = psize;
        this.page = page;
    }

    public int getTotalPage() {
        return psize <= 0 ? 1 : (int) Math.ceil(total * 1.0 / psize);
    }
}
