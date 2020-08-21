
package com.asiainfo.veris.crm.order.pub.frame.bcf.base;

public final class CSBaseConst
{
    public static enum TAX_TYPE
    {
        SALE("1"), // 视同销售
        MIX_SALE("2"), // 混业经营
        DIS_SALE("3"); // 折扣折让

        private final String value;

        private TAX_TYPE(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    public static enum TRADE_IS_PF
    {
        EXIST("EXIST"), // 没有修改
        ONLYADD("4"), // 新增不发指令
        ONLYDEL("5");// 删除不发指令

        private final String value;

        private TRADE_IS_PF(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }

    public static enum TRADE_MODIFY_TAG
    {
        Add("0"), // 增加
        DEL("1"), // 删除
        MODI("2"), // 修改
        EXIST("EXIST"); // 没有修改

        private final String value;

        private TRADE_MODIFY_TAG(String value)
        {

            this.value = value;
        }

        public String getValue()
        {

            return value;
        }
    }
}
