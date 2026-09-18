package com.dine.util.diy.items;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dine.util.diy.DiyItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 导航组组件
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "navBar")
public class NavBar implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Schema(description = "diyItem")
    private DiyItem item;

    public NavBar(String imagePath){
        this.item = new DiyItem();
        item.setName("导航组");
        item.setType("navBar");
        item.setGroup("media");
        item.setIcon("icon-mulu");
        // 样式
        JSONObject style = new JSONObject();
        style.put("background", "#ffffff");
        style.put("background1", "#fff5cf");
        style.put("background2", "#fff");
        style.put("bgcolor", "#f2f2f2");
        style.put("bottomRadio", 8);
        style.put("paddingBottom", 10);
        style.put("paddingLeft", 10);
        style.put("paddingTop", 10);
        style.put("rowsNum", 2);
        style.put("topRadio", 8);
        item.setStyle(style);

        // 参数
        JSONObject params = new JSONObject();
        item.setParams(params);

        // 加1条数据
        JSONArray data = new JSONArray();
        JSONObject itemData1 = new JSONObject();
        itemData1.put("imgUrl", imagePath + "image/diy/navbar/01.png");
        itemData1.put("linkUrl", "");
        itemData1.put("text", "按钮文字1");
        itemData1.put("title", "按钮标题1");
        itemData1.put("color", "#666666");
        data.add(itemData1);
        item.setData(data);
    }
}
