
package com.asiainfo.veris.crm.iorder.pub.welfare.consts;

/**
 * @Description 权益业务受理常量类
 * @Auther: zhenggang
 * @Date: 2020/7/3 10:09
 * @version: V1.0
 */
public final class WelfareConstants
{
    /**
     * 权益受理业务类型枚举
     */
    public enum TradeType
    {
        // 权益自有商品业务受理
        ACCEPT("151");

        private final String value;

        TradeType(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public String toString()
        {
            return this.value;
        }
    }

    /**
     * 权益商品类型
     */
    public enum OfferType
    {
        // 权益包商品类型
        WEFFARE("Q");

        private final String value;

        OfferType(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public String toString()
        {
            return this.value;
        }
    }

    /**
     * 权益商品关系类型
     */
    public enum RelType
    {
        // 权益包商品类型
        WEFFARE("Q"),
        FAKE_ID("-1"),
        ZERO_ID("0"),
        JOIN_REL("5");

        private final String value;

        RelType(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public String toString()
        {
            return this.value;
        }
    }

    /**
     * 权益商品关系类型
     */
    public enum offerCode
    {
        // 权益包商品类型
        FAKE_CODE("-1");

        private final String value;

        offerCode(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        public String toString()
        {
            return this.value;
        }
    }
}
