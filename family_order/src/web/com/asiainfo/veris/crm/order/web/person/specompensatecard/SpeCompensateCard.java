/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.specompensatecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-9 修改历史 Revision 2014-4-9 上午09:33:59
 */
public abstract class SpeCompensateCard extends PersonBasePage
{

    public void getInfoBySimCardNo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        IDataset results = CSViewCall.call(this, "SS.SpeCompensateCardSVC.getInfoBySimCardNo", pagedata);

        this.setCond(results.getData(0));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData("cond", true);

        IDataset dataset = CSViewCall.call(this, "SS.SpeCompensateCardRegSVC.tradeReg", data);
        if(IDataUtil.isNotEmpty(dataset)){
        	IData result = dataset.getData(0);
        	result.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
            IDataset infos = CSViewCall.call(this, "CS.PrintNoteSVC.checkReceiptAndSetTradeState", result);
        }
        
        setAjax(dataset);

    }
    
    /**
     * 打印，不走组件
     * @param cycle
     * @throws Exception
     */
    public void printSpecompensateCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.SpeCompensateCardSVC.printSpeCompensateCard", data);
        setAjax(dataset);
    }

    public abstract void setCond(IData cond);

}
