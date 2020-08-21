/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.grpvaluecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-5-9 修改历史 Revision 2014-5-9 下午03:43:09
 */
public abstract class GRPValueCard extends PersonBasePage
{

    public void addClick(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        //data.put("TRADE_TYPE_CODE", "461");
        IDataset results = CSViewCall.call(this, "SS.GRPValueCardMgrSVC.getValueCardInfo", data);

        IDataset set1 = results.getData(0).getDataset("TABLE1");
        IDataset set2 = results.getData(0).getDataset("TABLE2");

        String temp = data.getString("table2");
        IDataset tempSet = new DatasetList(temp);
        if (tempSet.size() > 0)
        {

            set2.addAll(tempSet);
        }
        setAjax(set2);

        this.setBasicInfos(set1);
        this.setSaleInfos(set2);

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        this.setCsValueCardDiscount(StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "csGRPValueCardDiscount", StaffPrivUtil.PRIV_TYPE_FUNCTION));
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        this.setCsValueCardDiscount(StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "csGRPValueCardDiscount", StaffPrivUtil.PRIV_TYPE_FUNCTION));
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.GRPValueCardRegSVC.tradeReg", data);
        setAjax(dataset);

    }
    
    /**
     * 作用：查询集团客户信息
     * 
     * @author luoy
     * @param cycle
     * @throws Throwable
     */
    public void queryGroupCusts(IRequestCycle cycle) throws Exception
    {
        IDataset result = new DatasetList();
        String alertInfo = "";
        IData conParams = getData("cond", true);
        String id = conParams.getString("groupId");
        String strQueryType = getData().getString("QueryType");
        long tt = 0;

        if (strQueryType.equals("0")) // 按集团客户编码
        {
            result.add(UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, id, false));
        	//UserGrpInfoQry.qryGrpByGIdAndPId(id, "7346");
        	IData data = new DataMap();
        	data.put("GROUP_ID", id);
        	data.put("PRODUCT_ID", "7346");         
        	IDataset ds = CSViewCall.call(this, "SS.GRPValueCardMgrSVC.qryGrpByGIdAndPId", data);
        	if(IDataUtil.isEmpty(ds))
         	{
        		alertInfo = "该集团【"+ id +"】没有订购流量卡产品！";
        		setCustInfoView(data);
         		//CSViewException.apperr(GrpException.CRM_GRP_682, id);
         	}else{
         		setCustInfoView(ds.getData(0));      		
         	}
        	
        }

        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        setCcondition(getData("cond", true));

    }
    
    public abstract void setCustInfoView(IData custInfoView);
    
    public abstract void setCcondition(IData ccondition);
    
    public abstract void setAuditInfos(IDataset dataset);

    public abstract void setBasicInfos(IDataset dataset);

    public abstract void setCond(IData cond);

    public abstract void setCsValueCardDiscount(boolean can);

    public abstract void setSaleInfos(IDataset dataset);

}
