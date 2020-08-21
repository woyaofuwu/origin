
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;

public class CheckBbossDiscntExist extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckBbossDiscntExist.class);

    /*
     * @description 检查资费状态是否发生改变，资费状态对应为新增、删除和修改的状态都说明资费发生了改变
     * @author xunyl
     * @date 2013-10-02
     */
    protected static boolean isDiscntChg(IDataset elementInfoList) throws Exception
    {
        // 1- 定义返回值
        boolean isDiscntChg = false;

        // 2- 如果参数列表为空，则说明没有资费变更
        if (IDataUtil.isEmpty(elementInfoList))
        {
            return isDiscntChg;
        }

        // 3- 循环检验资费值是否发生变更
        for (int i = 0; i < elementInfoList.size(); i++)
        {
            IData elementInfo = elementInfoList.getData(i);
            String elementType = elementInfo.getString("ELEMENT_TYPE_CODE");
            if (elementType.equals("D"))
            {// 说明当前元素是资费而非服务
                String elemntState = elementInfo.getString("MODIFY_TAG");
                if (!TRADE_MODIFY_TAG.EXIST.getValue().equals(elemntState))
                {
                    isDiscntChg = true;
                }
            }
        }

        // 4- 返回结果
        return isDiscntChg;
    }

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBbossDiscntExist() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;

        /* 获取产品操作编号，如果是资费变更需要进行资费校验 */
        String opType = databus.getString("PRODUCT_OPER_TYPE");
        if (StringUtils.isEmpty(opType) || !GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_DISCNT.getValue().equals(opType))
        {// 产品和商品的资费变更操作类型都为5
            return bResult;
        }

        /* 获取用户编号，判断产、商品类型 */
        String productType = "PO";// 默认为商品
        String userId = databus.getString("USER_ID");
        IDataset userGrpMerchInfoList = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(userId, null, "A", null);
        if (IDataUtil.isEmpty(userGrpMerchInfoList))
        {
            IDataset userGrpMerchpInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByUserIdMerchSpecProductSpecStatus(userId, null, "A", null, null);
            if (!IDataUtil.isEmpty(userGrpMerchpInfoList))
            {
                productType = "PRO";
            }
        }

        /* 商品需要判断是否存在有商品资费，存在商品资费的情况下需要校验商品资费是否发生改变 */
        if ("PO".equals(productType))
        {
            String productId = databus.getString("PRODUCT_ID");
            IDataset staticValue = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", new String[]
            { "TYPE_ID", "DATA_ID" }, new String[]
            { "BBOSS_MERCH_DISCNT", productId });
            if (!IDataUtil.isEmpty(staticValue))
            {
                IDataset elementInfoList = databus.getDataset("ELEMENT_INFO");
                // 资费校验结果与该规则的返回结果互为相反数，资费没改变，返回false,规则校验不通过，需要返回true
                bResult = !isDiscntChg(elementInfoList);
            }
        }

        /* 产品资费变更，也需要判断数据中是否有发生变更的元素 */
        if ("PRO".equals(productType))
        {
            IDataset elementInfoList = databus.getDataset("ELEMENT_INFO");
            // 资费校验结果与该规则的返回结果互为相反数，资费没改变，返回false,规则校验不通过，需要返回true
            bResult = !isDiscntChg(elementInfoList);
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckBbossDiscntExist() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }
}
