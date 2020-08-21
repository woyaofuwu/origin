
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CheckProductPaused extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckProductPaused.class);

    private static final long serialVersionUID = 1L;

    /*
     * @description 成员订购的BBOSS产品中有暂停状态，不能注销
     * @author xunyl
     * @date 2013-09-04
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistEcCode() >>>>>>>>>>>>>>>>>>");

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

        for (int i = 0; i < relaBBInfoList.size(); i++)
        {
            IData relaBBInfo = relaBBInfoList.getData(i);
            String productUserId = relaBBInfo.getString("USER_ID_B");
            IData productUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(productUserId, "0");
            if (IDataUtil.isEmpty(productUserInfo))
            {
                continue;
            }
            String userState = productUserInfo.getString("RSRV_STR5", "");
            if (userState.equals("N"))
            {
                bResult = true;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckProductPaused() " + bResult + "<<<<<<<<<<<<<<<<<<<");

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
