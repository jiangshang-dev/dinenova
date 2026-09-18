

package com.dine.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dine.repository.model.MtPage;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(name = "页面VO")
public class PageDto extends MtPage {

    private static final long serialVersionUID = 1L;

    @Schema(description = "页面数据，json格式")
    private JSONObject pageDataJson;

    @Schema(description = "页面数据，json格式")
    private JSONArray items;

    @Schema(description = "页面数据，json格式")
    private JSONObject page;
}
