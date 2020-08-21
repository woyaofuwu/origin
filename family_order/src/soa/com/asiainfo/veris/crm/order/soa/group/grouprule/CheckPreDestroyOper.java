
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class CheckPreDestroyOper extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 根据产品编号查询该产品是否有预注销操作，如果有预注销操作不能直接进行集团注销
     * @author xunyl
     * @date 2013-09-04
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        /* 自定义区域 */
        boolean bResult = false;

        /* 获取商品编号 */
        String productId = databus.getString("PRODUCT_ID");

        /* 根据商品编号查询该商品是否对应有预注销操作 */
        IDataset preDestroyInfoList = AttrBizInfoQry.getBizAttr("1", "B", "PREDESTORY", productId, null);

        /* 开始逻辑规则校验 */
        if (IDataUtil.isNotEmpty(preDestroyInfoList))
        {
        	/* 判断是否为流量统付业务的预注销，如果是，就绕开规则校验 */
            if(isFlowPayment(productId)){
            	return bResult;
            }

            //判断是否已经预注销过
            if(isPreDestory(databus)){
            	return bResult;
            }
            
            bResult = true;
        }

        return bResult;
    }
    
    /*
     * @description 根据商品编码查询是否为电子流量卡统付，实体流量卡统付不允许预取消！
     * @author wangzc7
     * @date 2017-02-24
     */
    private boolean isFlowPayment(String productId) throws Exception{
    	//根据产品编码获取对应的商品编码
    	String merch_code = GrpCommonBean.productToMerch(productId, 0);
    	//判断是否为电子流量卡统付
    	if("010190014".equals(merch_code)||"9001401".equals(merch_code)){
    		return true;
    	}
    	return false;
	}

    /**
     * 判断该商品是否已经预注销过了
     * @param databus
     * @return
     * @throws Exception
     */
    private boolean isPreDestory(IData databus) throws Exception{
    	//获取商品用户
    	String userId = databus.getString("USER_ID");
    	//获取商品编号
        String productId = databus.getString("PRODUCT_ID");
        //根据产品编码获取对应的商品编码
    	String merchSpecCode = GrpCommonBean.productToMerch(productId, 0);
    	IDataset grpMerchInfo = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(userId, merchSpecCode, "D", null);
    	if(IDataUtil.isNotEmpty(grpMerchInfo)){
    		return true;
    	}
    	return false;
    }
}
