
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.cancelwnchangeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CancelWNChangeProduct extends PersonBasePage
{
    /**
     * 执行取消动作
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        pdData.put("SERIAL_NUMBER", "KD_" + pdData.getString("SERIAL_NUMBER"));
        IDataset ret = CSViewCall.call(this, "SS.CancelWNChangeProductSVC.cancelChangeProductTrade", pdData);
//        setAjax(ret);
    }

    /**
     * 查询可返销的订单
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData ajax = new DataMap();
        ajax.put("QUERY_CODE", "N");
        ajax.put("QUERY_INFO", "该用户没有可取消的预约产品变更数据！");

        // IDataset cancelTradeInfos = new DatasetList();
        IData pdData = getData();
        IData input = new DataMap();
        input.put("SERIAL_NUMBER", "KD_" + pdData.getString("SERIAL_NUMBER"));
        IDataset widenetInfos = CSViewCall.call(this, "SS.CancelWNChangeProductSVC.getUserWidenetInfo", input);
        if (IDataUtil.isNotEmpty(widenetInfos))
        {
            IDataset errTradeInfos = CSViewCall.call(this, "SS.CancelWNChangeProductSVC.queryErrorInfoTrade", input);
            if (IDataUtil.isEmpty(errTradeInfos))
            {
                input.put("TRADE_TYPE_CODE", "601");

                IDataset cancelTradeInfos = CSViewCall.call(this, "SS.CancelWNChangeProductSVC.queryChangeProductTrade", input);

                if (IDataUtil.isNotEmpty(cancelTradeInfos))
                {
                    ajax.put("QUERY_CODE", "Y");
                    
                    IData cancelTradeInfo = cancelTradeInfos.getData(0);
                    
                    cancelTradeInfo.put("PRODUCT_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", cancelTradeInfo.getString("PRODUCT_ID")));
                    
                    setInfo(cancelTradeInfo);

                }

            }
            else
            {
                ajax.put("QUERY_INFO", "该用户有错单未处理,不能办理预约产品变更取消！");
            }
        }
        else
        {
            ajax.put("QUERY_INFO", "该用户未开通宽带,不能办理预约产品变更取消！");
        }

        setAjax(ajax);

    }

    public abstract void setInfo(IData data);

}
