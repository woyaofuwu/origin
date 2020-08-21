
package com.asiainfo.veris.crm.iorder.web.person.sundryquery.realname;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 功能：用户实名制信息查询 作者：GongGuang
 */
public abstract class QueryChkRealNameNew extends PersonBasePage
{

    public abstract IDataset getInfos();

    /**
     * 功能：用户实名制信息查询
     */
    public void queryRealNameInfo(IRequestCycle cycle) throws Exception
    {
        IData inparam = getData("cond", true);
        inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());// pd.getContext().getEpachyId()
        
        String isCheck=inparam.getString("NORMAL_USER_CHECK", "");
        
        IDataOutput dataCount=null;
        if(isCheck.equals("on")){	//勾选了在网用户，查询在网用户信息
        	dataCount= CSViewCall.callPage(this, "SS.QueryChkRealNameSVC.getUserRealNameInfoValid", inparam, getPagination("navt"));
        }else{	//没有勾选，根据userId查询具体的用户的信息
        	dataCount= CSViewCall.callPage(this, "SS.QueryChkRealNameSVC.getUserRealNameInfoByUserId", inparam, getPagination("navt"));
        }
         
        IDataset results = dataCount.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "查询用户实名制信息无记录~~";
        }
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setInfos(results);
        setCount(dataCount.getDataCount());
        setCondition(getData("cond", true));

    }

    public abstract void setCondition(IData cond);

    public abstract void setCount(long count);

    public abstract void setInfos(IDataset infos);

}
