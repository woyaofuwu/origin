
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossQuery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PoProductQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;

/**
 * 一级BBOSS业务集团客户订购处理查询--此类用于查询页面处理逻辑
 */
public class QryBBossTradeInfoBean
{
    /**
     * 一级BBOSS业务集团客户订购处理查询 -- 处理逻辑1传递进来的值是CUST_NAME的处理流程
     * 
     * @author liuxx3
     * @date 2014-07-08
     */
    public static IDataset qryBBossTradeInfo(IData inParam, Pagination pg) throws Exception
    {
        String groupId = inParam.getString("GROUP_ID");
        String custName = inParam.getString("CUST_NAME");
        String productOrderId = inParam.getString("PRODUCT_ORDER_ID");
        String productspecnumber = inParam.getString("PRODUCTSPECNUMBER");
        String pospecnumber = inParam.getString("POSPECNUMBER");
        String state = inParam.getString("STATE");

        IDataset result = new DatasetList();

        if (StringUtils.isNotEmpty(state) && "0".equals(state))
        {
            IDataset grpTradeInfos = TradeGrpMerchpInfoQry.qryBBossBHTradeInfo(groupId, custName, productspecnumber, productOrderId, pospecnumber, pg);
            result = spellProductName(grpTradeInfos);
        }
        else if (StringUtils.isNotEmpty(state) && "2".equals(state))
        {
            IDataset grpTradeInfos = TradeGrpMerchpInfoQry.qryBBossBTradeInfo(groupId, custName, productspecnumber, productOrderId, pospecnumber, pg);
            result = spellProductName(grpTradeInfos);
        }
        else
        {
            IDataset grpTradeInfos = TradeGrpMerchpInfoQry.qryBBossAllTradeInfo(groupId, custName, productspecnumber, productOrderId, pospecnumber, pg);
            result = spellProductName(grpTradeInfos);
        }

        return result;
    }

    /**
     * 一级BBOSS业务集团客户订购处理查询 -- 拼装前台页面所需要展示的信息 商产品名称
     * 
     * @author liuxx3
     * @date 2014-07-08
     */
    protected static IDataset spellProductName(IDataset grpTradeInfos) throws Exception
    {
        IDataset result = new DatasetList();

        if (IDataUtil.isEmpty(grpTradeInfos))
        {
            return result;
        }

        for (int i = 0; i < grpTradeInfos.size(); i++)
        {
            IData merchInfo = grpTradeInfos.getData(i);
            String merPospecnumber = merchInfo.getString("MERCH_SPEC_CODE");
            String merProductspecnumber = merchInfo.getString("PRODUCT_SPEC_CODE");

            String poSpecName = PoInfoQry.getPOSpecNameByPoSpecNumber(merPospecnumber);

            IDataset proInfos = PoProductQry.qryProductInfosByProductSpecNumber(merPospecnumber, merProductspecnumber);

            merchInfo.put("MERCH_SPEC_NAME", poSpecName);

            if (IDataUtil.isEmpty(proInfos))
            {
                merchInfo.put("PRODUCT_SPEC_NAME", "该产品已失效");
            }
            else
            {
                IData proInfo = proInfos.getData(0);
                merchInfo.put("PRODUCT_SPEC_NAME", proInfo.getString("PRODUCTSPECNAME"));// 产品名称
            }

            result.add(merchInfo);
        }

        return result;
    }

}
