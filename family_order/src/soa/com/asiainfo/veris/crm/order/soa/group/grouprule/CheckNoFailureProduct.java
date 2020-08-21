
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class CheckNoFailureProduct extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 判断是否该业务下面还有未失效的充值产品，如果有，则提示用户。业务不能继续
     * @author xunyl
     * @date 2016-07-11
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {      
        /* 自定义区域 */
        boolean bResult = false;

        String merchUserId = databus.getString("USER_ID");
        String productId = databus.getString("PRODUCT_ID");

        // 如果是产品用户，直接退出
        boolean isMerchId = isMerchId(productId);
        if (!isMerchId)
        {
            return bResult;
        }

        // 如果商产品UU关系不存在则直接退出
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId,relationTypeCode,"0");// 0表示集团UU关系
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            return bResult;
        }
        
        //获取爱流量统付充值产品对应的本省产品编号
        String localProductId = GrpCommonBean.merchToProduct("9001202", 2, null);
        
        for (int i = 0; i < relaBBInfoList.size(); i++)
        {
            IData relaBBInfo = relaBBInfoList.getData(i);
            String productUserId = relaBBInfo.getString("USER_ID_B");            
            IData userProductInfo = UserProductInfoQry.getUserProductBykey(productUserId,localProductId,merchUserId,null);
            if(IDataUtil.isEmpty(userProductInfo)){
                continue;
            }
            IDataset userAttrInfoList = UserAttrInfoQry.getUserAttrByUserId(productUserId,"90012024003");
            if(IDataUtil.isEmpty(userAttrInfoList)){
                continue;
            }else{
                IData userAttrInfo = userAttrInfoList.getData(0);
                String attrValue = userAttrInfo.getString("ATTR_VALUE",SysDateMgr.getEndCycle20501231());
                if(Long.parseLong(SysDateMgr.getSysDateYYYYMMDD())<=Long.parseLong(attrValue)){
                    bResult = true;
                    break;
                }
            }             
        } 

        return bResult;
    }

    /*
     * @description 判断当前注销的产品编号是否为商品编号
     * @author xunyl
     * @date 2014-05-26
     */
    private static boolean isMerchId(String productId) throws Exception
    {
        // 1- 定义返回变量(默认为false)
        boolean result = false;

        // 2- 获取全网产品编号
        IDataset attrBizInfoList = AttrBizInfoQry.getBizAttr("1", "B", "PRO", productId, null);
        if (IDataUtil.isEmpty(attrBizInfoList))
        {
            return result;
        }
        IData data = attrBizInfoList.getData(0);
        String merchpId = data.getString("ATTR_VALUE", "");

        // 3- 查询TD_F_PO表，是否存在该全网产品编号
        IData inparam = new DataMap();
        inparam.put("POSPECNUMBER", merchpId);
        IData poInfoLIst = PoInfoQry.getPoInfoByPK(inparam);
        if (IDataUtil.isNotEmpty(poInfoLIst))
        {
            result = true;
        }

        // 4- 返回查询结果
        return result;
    }


}
