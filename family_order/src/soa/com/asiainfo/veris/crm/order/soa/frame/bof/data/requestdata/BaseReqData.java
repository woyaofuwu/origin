
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class BaseReqData
{
    private String batchId = "";// 批量编码

    private List<FeeData> feeList;// 费用列表

    private List<PayMoneyData> payMoneyList;// 付费列表

    private String remark = "";// 备注

    private String tradeId;// 工单流水号

    private TradeTypeData tradeType;// 业务类型

    private UcaData uca;// 三户资料

    private String xTransCode = "";// 服务名称

    private String preType = "";// 预受理类型

    private String isConfirm;// 是否预受理回复、true为是

    private String checkMode;// 身份校验方式

    private String batchDealType;

    private String submitType;

    private IData pageRequestData;
    
    private String submitSource;//区分是接口过来还是crm前台

    private String joinType;

    private boolean needSms = true; // 默认发完工短信

    public boolean isNeedAction() {
        return needAction;
    }

    public void setNeedAction(boolean needAction) {
        this.needAction = needAction;
    }

    private boolean needAction = true; // 默认走action

    private boolean needRule = true;// 默认需要规则校验

    public void addFeeData(FeeData feeData)
    {
        if (feeList == null)
        {
            feeList = new ArrayList<FeeData>();
        }
        feeList.add(feeData);
    }

    public void addPayMoneyData(PayMoneyData payMoneyData)
    {
        if (payMoneyList == null)
        {
            payMoneyList = new ArrayList<PayMoneyData>();
        }
        payMoneyList.add(payMoneyData);
    }

    public String getAcceptTime() throws Exception
    {
        OrderDataBus dataBus = DataBusManager.getDataBus();
        if (StringUtils.isBlank(dataBus.getAcceptTime()))
        {
            dataBus.setAcceptTime(SysDateMgr.getSysTime());
        }
        return dataBus.getAcceptTime();
    }

    public String getBatchDealType()
    {
        return batchDealType;
    }

    public final String getBatchId()
    {
        return batchId;
    }

    /***
     * * @return Returns the checkMode.
     */
    public String getCheckMode()
    {
        return checkMode;
    }

    /**
     * 获取费用列表
     * 
     * @return
     */
    public List<FeeData> getFeeList()
    {
        return feeList;
    }

    public String getIsConfirm()
    {
        return isConfirm;
    }

    public String getJoinType()
    {
        return joinType;
    }

    public String getOrderId() throws Exception
    {
        return DataBusManager.getDataBus().getOrderId();
    }

    public String getOrderTypeCode() throws Exception
    {
        return DataBusManager.getDataBus().getOrderTypeCode();
    }

    public IData getPageRequestData()
    {
        return pageRequestData;
    }

    /**
     * 获取付费列表
     * 
     * @return
     */
    public List<PayMoneyData> getPayMoneyList()
    {
        return payMoneyList;
    }

    public String getPreType()
    {
        return preType;
    }

    public final String getRemark()
    {
        return remark;
    }

    public String getSubmitType()
    {
        return submitType;
    }

    public String getTradeId()
    {
        return tradeId;
    }

    public TradeTypeData getTradeType()
    {
        return tradeType;
    }

    /**
     * 返回三户资料对象
     * 
     * @return
     */
    public final UcaData getUca()
    {
        return uca;
    }

    public String getXTransCode()
    {
        return xTransCode;
    }

    public boolean isNeedRule()
    {
        return needRule;
    }

    public boolean isNeedSms()
    {
        return needSms;
    }

    public void setBatchDealType(String batchDealType)
    {
        this.batchDealType = batchDealType;
    }

    public final void setBatchId(String batchId)
    {
        this.batchId = batchId;
    }

    /***
     * @param checkMode
     *            The checkMode to set.
     */
    public void setCheckMode(String checkMode)
    {
        this.checkMode = checkMode;
    }

    /**
     * 设置费用列表
     * 
     * @param feeList
     */
    public void setFeeList(List<FeeData> feeList)
    {
        this.feeList = feeList;
    }

    public void setIsConfirm(String isConfirm)
    {
        this.isConfirm = isConfirm;
    }

    public void setJoinType(String joinType)
    {
        this.joinType = joinType;
    }

    public void setNeedRule(boolean needRule)
    {
        this.needRule = needRule;
    }

    public void setNeedSms(boolean needSms)
    {
        this.needSms = needSms;
    }

    public void setPageRequestData(IData pageRequestData)
    {
        this.pageRequestData = pageRequestData;
    }

    /**
     * 设置付费列表
     * 
     * @param payMoneyList
     */
    public void setPayMoneyList(List<PayMoneyData> payMoneyList)
    {
        this.payMoneyList = payMoneyList;
    }

    public void setPreType(String preType)
    {
        this.preType = preType;
    }

    public final void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setSubmitType(String submitType)
    {
        this.submitType = submitType;
    }

    public void setTradeId(String tradeId)
    {
        this.tradeId = tradeId;
    }

    public void setTradeType(TradeTypeData tradeType)
    {
        this.tradeType = tradeType;
    }

    /**
     * 设置三户资料对象
     * 
     * @param uca
     */
    public final void setUca(UcaData uca)
    {
        this.uca = uca;
    }

    public void setXTransCode(String xTransCode)
    {
        this.xTransCode = xTransCode;
    }

	public String getSubmitSource()
	{
		return submitSource;
	}

	public void setSubmitSource(String submitSource)
	{
		this.submitSource = submitSource;
	}
    
    
}
