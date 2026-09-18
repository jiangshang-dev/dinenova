package com.dine.util.diy.items;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dine.util.diy.DiyItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 辅助线组件
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "guide")
public class Guide implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Schema(description = "diyItem")
    private DiyItem item;

    public Guide(){
        this.item = new DiyItem();
        item.setName("辅助线");
        item.setType("guide");
        item.setGroup("tools");
        item.setIcon("icon-fuzhuxian");
        // 样式
        JSONObject style = new JSONObject();
        style.put("background", "#f2f2f2");
        style.put("lineColor", "#eeeeee");
        style.put("lineHeight", 1);
        style.put("lineStyle", "solid");
        style.put("paddingBottom", 0);
        style.put("paddingLeft", 10);
        style.put("paddingTop", 10);
        item.setStyle(style);

        // 参数
        JSONObject params = new JSONObject();
        item.setParams(params);

        // 默认数据
        JSONArray data = new JSONArray();
        item.setData(data);
    }
}
