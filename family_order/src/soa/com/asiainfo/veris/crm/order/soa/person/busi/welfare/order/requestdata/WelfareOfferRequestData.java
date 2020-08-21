
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.requestdata;

import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

import java.util.List;

/**
 * @Description 权益自有商品受理请求对象
 * @Auther: zhenggang
 * @Date: 2020/7/3 10:54
 * @version: V1.0
 */
public class WelfareOfferRequestData extends BaseReqData
{
    // 权益订单流水号
    private String welfareTradeId;

    // 前置订单流水号（触发权益订购的订单）
    private String advanceOrderId;

    //
    private String advanceTradeId;

    // 权益商品编码
    private String delWelfareOfferCode;

    // 权益商品类型
    private String delWelfareOfferType;

    // 新增主商品实例ID
    private String addMainOfferInsId;

    // 删除主商品实例ID
    private String delMainOfferInsId;

    // 自定义开始时间
    private String selfDefStartDate;

    // 自定义结束时间
    private String selfDefEndDate;

    // 权益订购打印内容
    private String printContent;

    // 自有商品集合
    private List<ProductModuleData> pmds;

    public String getWelfareTradeId()
    {
        return welfareTradeId;
    }

    public void setWelfareTradeId(String welfareTradeId)
    {
        this.welfareTradeId = welfareTradeId;
    }

    public String getAdvanceOrderId()
    {
        return advanceOrderId;
    }

    public void setAdvanceOrderId(String advanceOrderId)
    {
        this.advanceOrderId = advanceOrderId;
    }

    public String getAdvanceTradeId()
    {
        return advanceTradeId;
    }

    public void setAdvanceTradeId(String advanceTradeId)
    {
        this.advanceTradeId = advanceTradeId;
    }

    public String getDelWelfareOfferCode()
    {
        return delWelfareOfferCode;
    }

    public void setDelWelfareOfferCode(String delWelfareOfferCode)
    {
        this.delWelfareOfferCode = delWelfareOfferCode;
    }

    public String getDelWelfareOfferType()
    {
        return WelfareConstants.OfferType.WEFFARE.getValue();
    }

    public void setDelWelfareOfferType(String delWelfareOfferType)
    {
        this.delWelfareOfferType = delWelfareOfferType;
    }

    public String getAddMainOfferInsId()
    {
        return addMainOfferInsId;
    }

    public void setAddMainOfferInsId(String addMainOfferInsId)
    {
        this.addMainOfferInsId = addMainOfferInsId;
    }

    public String getDelMainOfferInsId()
    {
        return delMainOfferInsId;
    }

    public void setDelMainOfferInsId(String delMainOfferInsId)
    {
        this.delMainOfferInsId = delMainOfferInsId;
    }

    public String getSelfDefStartDate()
    {
        return selfDefStartDate;
    }

    public void setSelfDefStartDate(String selfDefStartDate)
    {
        this.selfDefStartDate = selfDefStartDate;
    }

    public String getSelfDefEndDate()
    {
        return selfDefEndDate;
    }

    public void setSelfDefEndDate(String selfDefEndDate)
    {
        this.selfDefEndDate = selfDefEndDate;
    }

    public String getPrintContent()
    {
        return printContent;
    }

    public void setPrintContent(String printContent)
    {
        this.printContent = printContent;
    }

    public List<ProductModuleData> getPmds()
    {
        return pmds;
    }

    public void setPmds(List<ProductModuleData> pmds)
    {
        this.pmds = pmds;
    }
}
