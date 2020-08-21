
package com.asiainfo.veris.crm.iorder.web.person.twobusiarrearagered;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class TwoBusiArrearageRed extends PersonBasePage
{
    /**
     * 通添加红名单信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void createAddRedInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String start_date = SysDateMgr.getSysTime();
        data.put("REMOVE_TAG","0");
        data.put("INSERT_DATE",start_date);
        data.put("INSERT_STAFF_ID",getVisit().getStaffId());

        IData param = new DataMap();
        param.put("REMOVE_TAG","0");
        param.put("OFFER_CODE",data.getString("OFFER_CODE"));
        param.put("GROUP_ID",data.getString("GROUP_ID"));
        IDataOutput iDataOutput= CSViewCall.callPage(this, "SS.TwoBusiArrearageRedSVC.selectRednfoPagination", param,getPagination("PageNav"));
        String hintInfo = "两级业务欠费红名单查询查询成功！";
        IDataset  iDataset = iDataOutput.getData();
        if (!DataUtils.isEmpty(iDataset)) {
            this.setAjax("error_message", "集团编码和商品录入重复, 请检查");
            return;
        }else{
            CSViewCall.call(this, "SS.TwoBusiArrearageRedSVC.inserRednfo", data);

        }


    }

    /**
     * 删除批次信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void delRedTrades(IRequestCycle cycle) throws Exception
    {

        String redIds = getParameter("RED_IDS");

        IData data = new DataMap();
        data.put("UPDATE_DATE",SysDateMgr.getSysTime());
        data.put("UPDATE_STAFF_ID",getVisit().getStaffId());
        data.put("RED_ID", redIds);
        CSViewCall.call(this, "SS.TwoBusiArrearageRedSVC.delelteRednfo", data);
      //  queryTwoBusinessRedInfo(cycle);// 初始化批量查询界面
    }







    /**
     * 初始化集团批量(查询)页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void initalDeal(IRequestCycle cycle) throws Exception
    {

        IData data = new DataMap();
        data.put("UPDATE_DATE",SysDateMgr.getSysDate());
        data.put("UPDATE_STAFF_ID",getVisit().getStaffId());
        IDataset tradeDatas= CSViewCall.call(this, "SS.TwoBusiArrearageRedSVC.queryComparalistValue", data);

        if (DataUtils.isEmpty(tradeDatas)) {
            this.setAjax("error_message", "请先在TD_S_COMMPARA表配置在业务类型信息！");

        }

        for (int i = 0; i < tradeDatas.size(); i++) {
            tradeDatas.getData(i).put("PARAM_NAME", tradeDatas.getData(i).getString("PARAM_NAME")+tradeDatas.getData(i).getString("PARA_CODE1"));
        }


        setTradeTypes(tradeDatas);
    }

    /**
     * 初始化批量业务新增模块
     * 
     * @param cycle
     * @throws Exception
     */
    public void initBatCreatePoup(IRequestCycle cycle) throws Exception
    {
        getData().put("initCreatePoup", "true");// 设置初始化新增模块标识为True
        IData data = new DataMap();
        data.put("SMS_FLAG", "0");// 设置短信发送标记为不发送

        data.put("INPUT_START_DATE", SysDateMgr.getSysTime());
        data.put("INPUT_END_DATE", SysDateMgr.getSysTime());

        setInfo(data);

    }




    /**
     * 2.2	两级业务欠费红名单查询 集团名称
     *
     * @throws Exception
     */
    public void queryGroupName(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("GROUP_ID", data.getString("GROUP_ID"));
        data.put("GROUP_NAME","");
        IDataset groupInfo = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByGrpId", data);
        if (DataUtils.isNotEmpty(groupInfo)) {
            data.put("GROUP_NAME",groupInfo.getData(0).getString("CUST_NAME"));

        } else {
            this.setAjax("error_message", "根据GROUP_ID" + data.getString("GROUP_ID") + "没有查询到集团信息！");
        }

        setInfo(data);
    }

    public void queryOfferName(IRequestCycle cycle) throws Exception {
        IData data = getData();
        data.put("OFFER_CODE", data.getString("OFFER_CODE"));
        data.put("OFFER_NAME","");
        IData offerInfo = IUpcViewCall.getOfferInfoByOfferCode(data.getString("OFFER_CODE"));//主产品
        if (DataUtils.isNotEmpty(offerInfo)) {
            String offerName = offerInfo.getString("OFFER_NAME");
            data.put("OFFER_NAME",offerName);
        } else {
            this.setAjax("error_message", "根据OFFER_CODE" + data.getString("OFFER_CODE") + "没有查询到商品信息！");
        }
        setInfo(data);
    }

    /**
     * 2.2	两级业务欠费红名单查询
     * 
     * @throws Exception
     */
    public void queryTwoBusinessRedInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("GROUP_ID", data.getString("cond_GROUP_ID_NAME"));
        data.put("GROUP_NAME", data.getString("cond_GROUP_ID_NAME"));
        data.put("OFFER_CODE", data.getString("cond_OFFER_CODE"));

        data.put("REMOVE_TAG","0");
        long pageCount = 0;
//        IDataOutput outPut1 = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatTrades", data, getPagination("PageNav"));
        IDataOutput iDataOutput= CSViewCall.callPage(this, "SS.TwoBusiArrearageRedSVC.selectRednfoPagination", data,getPagination("PageNav"));
        IDataset  iDataset = iDataOutput.getData();
//        IDataset iDataset= CSViewCall.call(this, "CS.TwoBusiArrearageRedSVC.selectRednfo", data);
        pageCount =iDataOutput.getDataCount();
        if (DataUtils.isEmpty(iDataset)) {
            this.setAjax("error_message", "未查询到两级业务欠费红名单信息");

        }
        this.setInfos(iDataset);
        this.setPageCount(pageCount);
         this.setCondition(getData("cond", false));

    }
    public abstract void setCondition(IData cond);
    public abstract void setTradeTypes(IDataset dataset);
    public abstract void setInfo(IData info);
    public abstract void setInfos(IDataset dataset);
    public abstract void setPageCount(long count);


}
