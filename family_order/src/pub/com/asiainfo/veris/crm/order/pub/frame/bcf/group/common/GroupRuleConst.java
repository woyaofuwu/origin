
package com.asiainfo.veris.crm.order.pub.frame.bcf.group.common;

public class GroupRuleConst
{

    public static enum RULE_EVNT_CODE
    {
        BaseInfo(8001), // BaseInfo
        ProductInfo(8002), // ProductInfo
        PreView(8003), // preview
        BBoss(8004), // BBoss
        PayRelaAdv(8005), // PayRelaAdv
        VGPOPayRelaAdv(8006), // VGPOPayRelaADV
        BatRule(8007), // BatRule
        BatRuleForEsop(8008), // BatRuleForEsop
        DiscntMatch(8009), // DiscntMatch
        AccountInfo(8010); // AccountInfo

        private final int value;

        private RULE_EVNT_CODE(int value)
        {

            this.value = value;
        }

        public int getValue()
        {

            return value;
        }
    }

    public static final String BaseInfo = "BaseInfo";

    public static final String ProductInfo = "ProductInfo";
    
    public static final String AccountInfo = "AccountInfo";

    public static final String PreView = "PreView";

    public static final String BBoss = "BBoss";

    public static final String PayRelaAdv = "PayRelaAdv";

    public static final String VGPOPayRelaAdv = "VGPOPayRelaAdv";

    public static final String BatRule = "BatRule";

    public static final String BatRuleForEsop = "BatRuleForEsop";
}
