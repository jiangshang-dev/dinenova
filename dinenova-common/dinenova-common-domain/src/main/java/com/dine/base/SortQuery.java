package com.dine.base;


import com.dine.common.enums.SortTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: airworthindess-db
 * @description: OrderQuery
 * @author: Yjz
 * @create: 2022-05-25 17:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "排序查询参数")
public class SortQuery {
    @Schema(description = "排序字段")
    private String sort;

    @Schema(description = "排序规则升序(ASC)/降序(DESC)")
    private SortTypeEnum rule = SortTypeEnum.ASC;
}
