package pers.niaonao.dingtalkrobot.enums;

/**
 * @className: MsgTypeEnum
 * @description: 消息类型枚举嘞
 * @date: 2021/6/15
 * @author niaonao
 **/
public enum MsgTypeEnum {

    MSG_TYPE_TEXT("text", "MSG_TYPE_TEXT"),
    MSG_TYPE_LINK("link", "MSG_TYPE_LINK"),
    MSG_TYPE_MARKDOWN("markdown", "MSG_TYPE_MARKDOWN"),
    MSG_TYPE_ACTION_CARD("actionCard", "MSG_TYPE_ACTION_CARD"),
    MSG_TYPE_FEED_CARD("feedCard", "MSG_TYPE_FEED_CARD");

    private String value;
    private String code;

    MsgTypeEnum(String value, String code) {
        this.code = code;
        this.value = value;
    }

    public static MsgTypeEnum getEnumValue(String value){
        for (MsgTypeEnum constants : values()) {
            if (constants.getValue() == value) {
                return constants;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
