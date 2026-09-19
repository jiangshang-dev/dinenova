package com.dine.util.diy.items;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dine.util.diy.DiyItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 图片橱窗
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "window")
public class Window implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Schema(description = "diyItem")
    private DiyItem item;

    public Window(String imagePath){
        this.item = new DiyItem();
        item.setName("图片橱窗");
        item.setType("window");
        item.setGroup("media");
        item.setIcon("icon-tupian11");
        item.setDataNum(4);
        // 样式
        JSONObject style = new JSONObject();
        style.put("background", "#ffffff");
        style.put("bgcolor", "#ffffff");
        style.put("layout", 4);
        style.put("paddingBottom", 10);
        style.put("paddingLeft", 10);
        style.put("paddingTop", 10);
        style.put("marginTop", 0);
        style.put("marginBottom", 0);
        style.put("marginLeft", 0);
        style.put("topRadio", 8);
        style.put("bottomRadio", 8);
        style.put("borderColor", "");

        item.setStyle(style);

        // 参数
        JSONObject params = new JSONObject();
        params.put("dataNum", 4);
        item.setParams(params);


        // 4条数据
        JSONArray data = new JSONArray();
        JSONObject itemData1 = new JSONObject();
        itemData1.put("imgUrl", imagePath + "image/diy/window/01.jpg");
        itemData1.put("linkUrl", "");
        data.add(itemData1);
        JSONObject itemData2 = new JSONObject();
        itemData2.put("imgUrl", imagePath + "image/diy/window/02.jpg");
        itemData2.put("linkUrl", "");
        data.add(itemData2);
        JSONObject itemData3 = new JSONObject();
        itemData3.put("imgUrl", imagePath + "image/diy/window/03.jpg");
        itemData3.put("linkUrl", "");
        data.add(itemData3);
        JSONObject itemData4 = new JSONObject();
        itemData4.put("imgUrl", imagePath + "image/diy/window/04.jpg");
        itemData4.put("linkUrl", "");
        data.add(itemData4);
        item.setData(data);
    }
}
