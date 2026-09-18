package com.dine.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @program: airworthindess-db
 * @description: BaseQuery
 * @author: Yjz
 * @create: 2022-05-25 17:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "分页查询参数")
public class PagingQuery implements Serializable {
    @Schema(hidden = true)
    private static final long serialVersionUID = 1430633339880116031L;

    @Positive(message = "输入合法的pageNum")
    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Positive(message = "输入合法的pageSize")
    @Schema(description = "每页条数")
    private Integer pageSize = 20;
}
