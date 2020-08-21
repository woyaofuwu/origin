
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CheckMebProductPaused extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckMebProductPaused.class);

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

        String merchUserId = databus.getString("USER_ID", "");
        String mebUserId = databus.getString("USER_ID_B", "");
        String productId = databus.getString("PRODUCT_ID", "");

        // 如果商产品BB关系不存在则直接退出
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);
        IDataset relaBBInfoList = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId, relationTypeCode, "0");// 0表示商品与产品BB关系
        if (IDataUtil.isEmpty(relaBBInfoList))
        {
            return bResult;
        }

        for (int i = 0; i < relaBBInfoList.size(); i++)
        {
            IData relaBBInfo = relaBBInfoList.getData(i);
            String productUserId = relaBBInfo.getString("USER_ID_B");
            // 校验产品用户是否被成员订购
            boolean isMebUserInfo = isMebProductUserInfo(productUserId, mebUserId, relationTypeCode);
            if (!isMebUserInfo)
            {
                continue;
            }
            // 判断用户资料表状态是否为暂停状态
            IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(productUserId, "0");
            String userState = userInfo.getString("RSRV_STR5", "");
            if (userState.equals("N"))
            {
                bResult = true;
                break;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckProductPaused() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

    /*
     * @description 校验产品用户是否被成员订购
     * @author xunyl
     * @date 2014-05-28
     */
    private static boolean isMebProductUserInfo(String userId, String mebUserId, String relationTypeCode) throws Exception
    {
        // 1- 定义返回变量(默认为false)
        boolean result = false;

        // 2- 查询用户是否为当前商品的产品用户
        IDataset productUserInfoList = RelaBBInfoQry.getBBByUserIdAB(userId, mebUserId, relationTypeCode, "1");// 1表示集团与成员BB关系
        if (IDataUtil.isNotEmpty(productUserInfoList))
        {
            result = true;
        }

        // 3- 返回查询结果
        return result;
    }
}
