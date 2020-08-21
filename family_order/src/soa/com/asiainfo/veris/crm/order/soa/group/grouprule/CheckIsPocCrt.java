
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class CheckIsPocCrt extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 判断业务是否为和对讲业务，如果是和对讲业务，则不做新增处理
     * @author xunyl
     * @date 2016-01-20
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        /* 自定义区域 */
        boolean bResult = false;

        /* 获取商品编号 */
        String productId = databus.getString("PRODUCT_ID");
        
        /*将本省商品编号转化为集团商品编号*/
        String merchId = GrpCommonBean.productToMerch(productId, 0);       

        /* 开始逻辑规则校验 ,集团商品编号为*/
        if (StringUtils.equals("010111002", merchId) || StringUtils.equals("010111003", merchId))
        {           
        	bResult = true;           
        }

        return bResult;
    }

}
