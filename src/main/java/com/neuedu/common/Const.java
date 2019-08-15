package com.neuedu.common;

public class Const {
    public static final String CURRENT_USER="user";
    public enum RoleEnum{
        ROLE_ADMIN(0,"管理员"),
        ROLE_CUSTOMER(1,"普通用户");
        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        private RoleEnum(int code, String desc){
            this.code=code;

            this.desc=desc;
        }
    }
}
