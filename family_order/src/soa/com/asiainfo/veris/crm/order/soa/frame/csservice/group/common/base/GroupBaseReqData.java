
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.CommData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.cha.ChaModData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer.GOfferModuleData;

public class GroupBaseReqData extends BaseReqData
{
    private IData bci = new DataMap();

    public CommData cd = new CommData();

    private IDataset eos;// EOS

    private String memFinish = "";

    private boolean effectNow = false; // 立即生失效

    private String devStaffId = "";// 发展人

    private String grpProductId = "";// 集团产品id,规则等都通过集团产品反射取对应类

    private String validateCtrlClass = CheckForGrp.class.getName();// 规则类

    private String validateMethod = "";// 规则类方法

    private String batchOperType; // 批量处理编码

    private boolean isChange = false; // 特殊业务可以通过修改此标记实现变更，比如集团彩铃资料修改界面实现一次性制作费多次收取需求

    private boolean ifBooking = false; // 是否预约

    private String acceptTime = ""; // 受理时间
    
    private String enterpriseOfferId = null; //集团销售品ID
    
    private List<ChaModData> compOfferChaList = new ArrayList<ChaModData>();//组合销售品特征信息

    private List<GOfferModuleData> offerModuleList = new ArrayList<GOfferModuleData>();//销售品列表
    
    public String getEnterpriseOfferId()
    {
        return enterpriseOfferId;
    }
    
    public void setEnterpriseOfferId(String enterpriseOfferId)
    {
        this.enterpriseOfferId = enterpriseOfferId;
    }

    public List<ChaModData> getCompOfferChaList()
    {
        return compOfferChaList;
    }
    
    public void setCompOfferChaList(List<ChaModData> compOfferChaList)
    {
        this.compOfferChaList = compOfferChaList;
    }
    
    public List<GOfferModuleData> getOfferModuleList()
    {
        return offerModuleList;
    }
    
    public void setOfferModuleList(List<GOfferModuleData> offerModuleList)
    {
        this.offerModuleList = offerModuleList;
    }
    
    public void addCompOfferChaList(List<ChaModData> compOfferChaList)
    {
        this.compOfferChaList.addAll(compOfferChaList);
    }
    
    public void addCompOfferCha(ChaModData compOfferChaList)
    {
        this.compOfferChaList.add(compOfferChaList);
    }
    
    public void addOfferModuleList(List<GOfferModuleData> offerModuleList)
    {
        this.offerModuleList.addAll(offerModuleList);
    }
    
    public void addOfferModuleData(GOfferModuleData res)
    {
        this.offerModuleList.add(res);
    }

    public String getAcceptTime()
    {
        return acceptTime;
    }

    public String getBatchOperType()
    {
        return batchOperType;
    }

    public String getDevStaffId()
    {
        return devStaffId;
    }

    public IDataset getEos()
    {
        return eos;
    }

    public String getGrpProductId()
    {
        return grpProductId;
    }

    public boolean getIsChange()
    {
        return isChange;
    }

    public String getMemFinish()
    {
        return memFinish;
    }

    /**
     * 获取产品控制参数
     * 
     * @return 产品控制参数
     * @author xiajj
     * @throws Exception
     */
    public BizCtrlInfo getProductCtrlInfo(String productId) throws Exception
    {

        return (BizCtrlInfo) bci.get(productId);
    }

    public String getValidateCtrlClass()
    {
        return validateCtrlClass;
    }

    public String getValidateMethod()
    {
        return validateMethod;
    }

    public boolean isEffectNow()
    {
        return effectNow;
    }

    public boolean isIfBooking()
    {
        return ifBooking;
    }

    public void setAcceptTime(String acceptTime)
    {
        this.acceptTime = acceptTime;
    }

    public void setBatchOperType(String batchOperType)
    {
        this.batchOperType = batchOperType;
    }

    public void setDevStaffId(String devStaffId)
    {
        this.devStaffId = devStaffId;
    }

    public void setEffectNow(boolean effectNow)
    {
        this.effectNow = effectNow;
    }

    public void setEos(IDataset eos)
    {
        this.eos = eos;
    }

    public void setGrpProductId(String grpProductId)
    {
        this.grpProductId = grpProductId;
    }

    public void setIfBooking(boolean ifBooking)
    {
        this.ifBooking = ifBooking;
    }

    public void setIsChange(boolean isChange)
    {
        this.isChange = isChange;
    }

    public void setMemFinish(String memFinish)
    {
        this.memFinish = memFinish;
    }

    /**
     * 设置产品控制参数
     * 
     * @param productCtrlInfo
     *            产品控制参数
     * @author xiajj
     * @throws Exception
     */
    public void setProductCtrlInfo(String productId, BizCtrlInfo ctrlInfo) throws Exception
    {
        bci.put(productId, ctrlInfo);
    }

    public void setValidateCtrlClass(String validateCtrlClass)
    {
        this.validateCtrlClass = validateCtrlClass;
    }

    public void setValidateMethod(String validateMethod)
    {
        this.validateMethod = validateMethod;
    }
}
