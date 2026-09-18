package com.dine.util.diy.items;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dine.util.diy.DiyItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户信息组件
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Schema(name = "userInfo")
public class UserInfo {
    private static final long serialVersionUID = 1L;

    @Schema(description = "diyItem")
    private DiyItem item;

    public UserInfo() {
        this.item = new DiyItem();
        item.setName("用户信息");
        item.setType("userInfo");
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
        item.setData(data);
    }
}
