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
public class NavBarMoRen implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Schema(description = "diyItem")
    private DiyItem item;

    public NavBarMoRen(String imagePath){
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

        // 加2条数据
        JSONArray data = new JSONArray();
        JSONObject itemData1 = new JSONObject();
        itemData1.put("color", "#666666");
        itemData1.put("imgUrl", imagePath + "image/diy/navbar/03.png");
        itemData1.put("linkUrl", "pages/product/list/store");
        itemData1.put("name", "链接到 页面 堂食点餐");
        itemData1.put("text", "支持多种点餐模式");
        itemData1.put("textcolor", "#999999");
        itemData1.put("title", "堂食点餐");
        itemData1.put("titlecolor", "#333333");
        JSONObject itemData2 = new JSONObject();
        itemData2.put("color", "#666666");
        itemData2.put("imgUrl", imagePath + "image/diy/navbar/04.png");
        itemData2.put("linkUrl", "pages/product/list/store");
        itemData2.put("name", "链接到 页面 快餐模式");
        itemData2.put("text", "下单叫号，到店取餐");
        itemData2.put("textcolor", "#C5B011");
        itemData2.put("title", "快餐模式");
        itemData2.put("titlecolor", "#000000");

        data.add(itemData1);
        data.add(itemData2);
        item.setData(data);
    }
}
