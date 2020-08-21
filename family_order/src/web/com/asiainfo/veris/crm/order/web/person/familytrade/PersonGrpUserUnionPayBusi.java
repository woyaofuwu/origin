package com.asiainfo.veris.crm.order.web.person.familytrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
/**
 * 和商务融合产品包订购页面处理
 * REQ201804080012和商务融合产品包系统开发需求
 * @author chenzg
 *
 */
public abstract class PersonGrpUserUnionPayBusi extends PersonBasePage
{
	public abstract void setCommInfo(IData info);// 
    public abstract void setInfos(IDataset infos);//
    /**
     * 校验统付的集团产品用户
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void checkBySerialNumber(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.PersonGrpUserUnionPaySVC.checkBySerialNumber", pagedata);
        this.setAjax(results.getData(0));
    }
    /**
     * 本业务相关的数据加载
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.PersonGrpUserUnionPaySVC.loadChildTradeInfo", pagedata);
        this.setCommInfo(results.getData(0).getData("FAM_PARA"));
        this.setInfos(results.getData(0).getDataset("QRY_MEMBER_LIST"));
    }
    /**
     * 查询集团产品包
     * td_s_commpara param_attr=1698
     * @chenjg
     * @20200220
     */
     public void queryGroupProductPackage(IRequestCycle cycle) throws Throwable
     {
         IData param = getData();
         IDataset results = CSViewCall.call(this, "SS.PersonGrpUserUnionPaySVC.queryGroupProductPackage", param);   
         setAjax(results);
     }
    /**
     * 页面初始化方法
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
    }
    /**
     * 提交业务办理处理方法
     * @param cycle
     * @throws Exception
     * @author chenzg
     * @date 2018-4-11
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put("GROUP_PACK", data.getString("GROUP_PACK"));
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.PersonGrpUserUnionPayRegSVC.tradeReg", data);
        setAjax(dataset);
    }

}
