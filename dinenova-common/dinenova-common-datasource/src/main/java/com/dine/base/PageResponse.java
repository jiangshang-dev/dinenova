package com.dine.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: haier
 * @description: Page
 * @author: java开发组
 * @create: 2021-06-23 16:10
 */
@Data
@Schema(description = "分页信息")
public class PageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1430633339880116031L;

    @Schema(description = "每页条数")
    private Long pageSize;
    @Schema(description = "当前页码数")
    private Long pageNum;
    @Schema(description = "总条数")
    private Long total;
    @Schema(description = "当前页数据")
    private List<T> result;

    /**
     * 将MybatisPlus的iPage接口类数据转化为PageResponse
     *
     * @param iPage iPage
     * @param <E>   数据类型
     * @return PageResponse
     */
    public static <E> PageResponse<E> res(IPage<E> iPage) {
        PageResponse<E> response = new PageResponse<>();
        response.setPageNum(iPage.getCurrent());
        response.setPageSize(iPage.getSize());
        response.setTotal(iPage.getTotal());
        response.setResult(iPage.getRecords());
        return response;
    }

    /**
     * 将MybatisPlus的iPage接口类数据转化为PageResponse
     *
     * @param pageNum  页面
     * @param pageSize 每页条数
     * @param total    总条数
     * @param data     数据
     * @param <E>      数据类型
     * @return PageResponse
     */
    public static <E> PageResponse<E> res(long pageNum, long pageSize, long total, List<E> data) {
        PageResponse<E> response = new PageResponse<>();
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setTotal(total);
        response.setResult(data);
        return response;
    }
}
