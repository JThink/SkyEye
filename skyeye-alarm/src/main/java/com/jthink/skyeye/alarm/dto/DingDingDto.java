package com.jthink.skyeye.alarm.dto;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 钉钉报警dto
 * @date 2017-09-29 17:32:33
 */
public class DingDingDto {

    private String msgtype = "text";

    private Text text;

    private At at;

    public String getMsgtype() {
        return msgtype;
    }

    public DingDingDto setMsgtype(String msgtype) {
        this.msgtype = msgtype;
        return this;
    }

    public Text getText() {
        return text;
    }

    public DingDingDto setText(Text text) {
        this.text = text;
        return this;
    }

    public At getAt() {
        return at;
    }

    public DingDingDto setAt(At at) {
        this.at = at;
        return this;
    }

    public static class Text {
        private String content;

        public Text() {
        }

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public Text setContent(String content) {
            this.content = content;
            return this;
        }
    }

    public static class At {
        private Boolean isAtAll;

        public At() {
        }

        public At(boolean isAtAll) {
            this.isAtAll = isAtAll;
        }

        public Boolean getIsAtAll() {
            return isAtAll;
        }

        public At setIsAtAll(Boolean atAll) {
            isAtAll = atAll;
            return this;
        }
    }
}
