package com.dine.enums;

import lombok.Getter;

@Getter
public enum PageEnum {

    // 状态  0 暂存  1使用上一次 2发布
    TEMP_SAVE(0, "暂存"),
    USE_LAST(1, "使用上一次"),
    USE_ING(2, "发布");

    int code;
    String msg;

    PageEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    PageEnum() {
    }
}
