/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.chlcommfeesubsidymgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @CREATED by gongp@2014-4-9 修改历史 Revision 2014-4-9 下午02:49:30
 */
public abstract class ChlCommFeeSubsidyMgr extends PersonBasePage
{

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        IData userInfo = new DataMap(pagedata.getString("USER_INFO"));
        IData custInfo = new DataMap(pagedata.getString("CUST_INFO"));
        IData vipInfo = new DataMap(pagedata.getString("VIP_INFO"));

        if (IDataUtil.isNotEmpty(vipInfo))
        {
            String vipClassId = vipInfo.getString("VIP_CLASS_ID", "");
            String vipTypeCode = vipInfo.getString("VIP_TYPE_CODE", "");
            String vipClassName = "普通客户";
            if (StringUtils.isNotBlank(vipClassId) && StringUtils.isNotBlank(vipTypeCode))
            {
                vipClassName = StaticUtil.getStaticValue(this.getVisit(), "TD_M_VIPCLASS", new String[]
                { "VIP_TYPE_CODE", "CLASS_ID" }, "CLASS_NAME", new String[]
                { vipTypeCode, vipClassId });
            }
            custInfo.put("CLASS_NAME", vipClassName);
        }
        else
        {
            custInfo.put("CLASS_NAME", "普通客户");
        }

        IData param = new DataMap();

        param.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfo.getString("USER_ID"));
        param.put("TRADE_TYPE_CODE", pagedata.getString("TRADE_TYPE_CODE"));

        IDataset dataset = CSViewCall.call(this, "SS.ChlCommFeeSubSVC.loadChildInfo", param);

        IDataset results = CSViewCall.call(this, "SS.ChlCommFeeSubSVC.getUserInfoChange", userInfo);
        if (results.size() > 0)
        {
            IData userInfoChange = results.getData(0);
            custInfo.putAll(userInfoChange);
            
            //add by duhj 通过调产商品接口,查询产品与名牌名称  2017/03/06
            IData result = CSViewCall.callone(this, "SS.ChlCommFeeSubSVC.getUserName", custInfo);
            custInfo.putAll(result);

        }

        int opern_years = Integer.parseInt(SysDateMgr.getNowYear()) - Integer.parseInt(userInfo.getString("OPEN_DATE").substring(0, 4)) + 1;
        userInfo.put("OPEN_YEARS", opern_years);

        this.setCustInfo(custInfo);
        this.setUserInfo(userInfo);

        if (IDataUtil.isNotEmpty(dataset))
        {
            IData chlOtherInfo = dataset.getData(0);

            if (StringUtils.isNotBlank(chlOtherInfo.getString("PARA_CODE3")))
            {
                IData inparam = new DataMap();
                inparam.put("PARA_CODE1", chlOtherInfo.getString("PARA_CODE3"));
                inparam.put("PARA_CODE2", this.getVisit().getCityCode());
                this.setChlCodes(CSViewCall.call(this, "SS.ChlCommFeeSubSVC.getChlCode", inparam));
            }
            else
            {
                if (StringUtils.isNotBlank(chlOtherInfo.getString("NUMBER_DEPART")))
                {
                    String departKindCode = StaticUtil.getStaticValue(getVisit(), "TD_M_DEPART", "DEPART_ID", "DEPART_KIND_CODE", chlOtherInfo.getString("NUMBER_DEPART"));

                    IData inparam = new DataMap();
                    inparam.put("PARA_CODE1", departKindCode);
                    inparam.put("PARA_CODE2", this.getVisit().getCityCode());
                    this.setChlCodes(CSViewCall.call(this, "SS.ChlCommFeeSubSVC.getChlCode", inparam));
                    this.setAjax("DEPART_KIND_CODE", departKindCode);
                }
            }

            this.setChlInfo(chlOtherInfo);
        }

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.ChlCommFeeSubsidyMgrRegSVC.tradeReg", data);
        setAjax(dataset);

    }

    /**
     * @param cycle
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public void queryChlCode(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        IData param = new DataMap();
        param.put("PARA_CODE1", pagedata.getString("CHL_TYPE"));
        param.put("PARA_CODE2", this.getVisit().getCityCode());
        IDataset results = CSViewCall.call(this, "SS.ChlCommFeeSubSVC.getChlCode", param);
        this.setChlCodes(results);

    }

    /**
     * @param cycle
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public void queryChlName(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        IData param = new DataMap();
        param.put("PARA_CODE1", pagedata.getString("CHL_CODE"));
        IDataset results = CSViewCall.call(this, "SS.ChlCommFeeSubSVC.getChlName", param);

        if (results.size() > 0)
        {
            this.setChlInfo(results.getData(0));
        }
    }

    public abstract void setChlCodes(IDataset chlInfos);

    public abstract void setChlInfo(IData info);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setUserInfo(IData custInfo);

}
