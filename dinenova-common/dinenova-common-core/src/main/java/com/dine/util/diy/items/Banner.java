package com.dine.util.diy.items;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dine.util.diy.DiyItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 轮播图组件
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "banner")
public class Banner implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @Schema(description = "diyItem")
    private DiyItem item;

    public Banner(String imagePath){
        this.item = new DiyItem();
        item.setName("图片轮播");
        item.setType("banner");
        item.setGroup("media");
        item.setIcon("icon-lunbotu");
        // 样式
        JSONObject style = new JSONObject();
        style.put("background", "#ffffff");
        style.put("bottomRadio", 0);
        style.put("paddingBottom", 0);
        style.put("paddingLeft", 0);
        style.put("paddingTop", 0);
        style.put("topRadio", 0);
        style.put("rowsNum", 2);
        style.put("loginTopRadio", 10);
        style.put("loginQrSize", 60);
        style.put("loginPaddingLeft", 10);
        style.put("loginOpacity", 100);
        style.put("loginMarginLeft", 10);
        style.put("loginHeight", 90);
        style.put("loginBtnTxtColor", "#ffffff");
        style.put("loginBtnRadius", 15);
        style.put("loginBtnBg", "#113a28");
        style.put("loginBottomRadio", 10);
        style.put("loginBorderColor", "#cccccc");
        style.put("loginBgImage", "image/diy/banner/11.jpg");
        style.put("loginBgType", 1);
        style.put("loginBeforeTxtColor", "#000000");
        style.put("loginBeforeSubTxtColor", "#777777");
        style.put("loginBeforeAvatarSize", 75);
        style.put("loginBackground", "#ffffff");
        style.put("loginAvatarRadius", 100);
        style.put("loginAvatarSize", 80);
        style.put("loginAfterTxtColor", "#000000");
        style.put("loginAfterSubTxtColor", "#777777");
        style.put("loginAfterRightTxtColor", "#FFFFFF");
        style.put("loginAfterAvatarSize", 75);
        style.put("isLogin", 1);
        style.put("imgShape", "square");
        style.put("height", 600);
        style.put("btnShape", "round");
        style.put("btnColor", "#113a28");
        item.setStyle(style);

        // 默认数据
        JSONArray data = new JSONArray();
        JSONObject itemData1 = new JSONObject();
        itemData1.put("imgUrl", imagePath + "image/diy/banner/09.png");
        itemData1.put("linkUrl", "");
        JSONObject itemData2 = new JSONObject();
        itemData2.put("imgUrl", imagePath + "image/diy/banner/10.jpg");
        itemData2.put("linkUrl", "");
        // 加2条数据
        data.add(itemData1);
        data.add(itemData2);
        item.setData(data);
    }
}
