package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.data.offer;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

/**
 * @author Administrator
 */
public class GPlatSvcData
{
    private String platsvcInsId;
    
    private String serviceId;// 服务ID
    
    private String servCode; //服务代码
    
    private String bizTypeCode;

    private String bizCode;
    
    private String bizAttr;
    
    private String bizName;
    
    private String bizInCode;
    
    private String bizStateCode;
    
    private String bizStatus;
    
    private String bizPri;
    
    private String authCode;
    
    private String usageDesc;
    
    private String introUrl;
    
    private String billingType;
    
    private String billingMode;

    private String price;
    
    private String preCharge;
    
    private String csTel;
    
    private String csUrl;

    private String accessMode;

    private String accessNumber;

    private String siBaseInCode;

    private String siBaseInCodeAttr;

    private String ecBaseInCode;

    private String ecBaseInCodeAttr;

    private String maxItemPreDay;
    
    private String maxItemPreMon;
    
    private String deliverNum;
    
    private String forbidStartTimeA;

    private String forbidEndTimeA;

    private String forbidStartTimeB;

    private String forbidEndTimeB;

    private String forbidStartTimeC;

    private String forbidEndTimeC;

    private String forbidStartTimeD;

    private String forbidEndTimeD;

    private String isTextEcgn;
    
    private String defaultEcgnLang;
    
    private String textEcgnEn;
    
    private String textEcgnZh;
    
    private String oprEffTime;
    
    private String operState;

    private String adminAccessNum;

    private String masId;
    
    private String platSyncState;
    
    private String validDate;

    private String expireDate;
    
    private String action;

    private String remarks;
    
    private String sendPfType;
    
    /** 以下属性新疆暂时没有 **/
    private String svrCodeHead;

    private String moAccessNum;

    private String spCode;

    private String rsrvStr2;

    private String rsrvStr4;

    private String isMasServ;

    private String rsrvTag1;

    private String rsrvTag2;

    private String rsrvTag3;

    private String isNeedPf;

    private String serviceType;

    private String whiteTwocheck;

    private String smsTemplate;

    private String portType;

    private String cancelFlag;

    public String getMasId()
    {
        return masId;
    }

    public void setMasId(String masId)
    {
        this.masId = masId;
    }

    private List<MoListData> moList = new ArrayList<MoListData>();;

    public List<MoListData> getMoList()
    {
        return moList;
    }

    public void setMoList(List<MoListData> moList)
    {
        this.moList = moList;
    }

    public void addMoList(List<MoListData> moList)
    {
        this.moList.addAll(moList);
    }

    public void addMo(MoListData mo)
    {
        this.moList.add(mo);
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getCsTel()
    {
        return csTel;
    }

    public void setCsTel(String csTel)
    {
        this.csTel = csTel;
    }

    public String getBizInCode()
    {
        return bizInCode;
    }

    public void setBizInCode(String bizInCode)
    {
        this.bizInCode = bizInCode;
    }

    public String getBizCode()
    {
        return bizCode;
    }

    public void setBizCode(String bizCode)
    {
        this.bizCode = bizCode;
    }

    public String getBizName()
    {
        return bizName;
    }

    public void setBizName(String bizName)
    {
        this.bizName = bizName;
    }

    public String getBillingType()
    {
        return billingType;
    }

    public void setBillingType(String billingType)
    {
        this.billingType = billingType;
    }

    public String getAccessMode()
    {
        return accessMode;
    }

    public void setAccessMode(String accessMode)
    {
        this.accessMode = accessMode;
    }

    public String getBizStatus()
    {
        return bizStatus;
    }

    public void setBizStatus(String bizStatus)
    {
        this.bizStatus = bizStatus;
    }

    public String getBizAttr()
    {
        return bizAttr;
    }

    public void setBizAttr(String bizAttr)
    {
        this.bizAttr = bizAttr;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getBizTypeCode()
    {
        return bizTypeCode;
    }

    public void setBizTypeCode(String bizTypeCode)
    {
        this.bizTypeCode = bizTypeCode;
    }

    public String getUsageDesc()
    {
        return usageDesc;
    }

    public void setUsageDesc(String usageDesc)
    {
        this.usageDesc = usageDesc;
    }

    public String getBizPri()
    {
        return bizPri;
    }

    public void setBizPri(String bizPri)
    {
        this.bizPri = bizPri;
    }

    public String getIntroUrl()
    {
        return introUrl;
    }

    public void setIntroUrl(String introUrl)
    {
        this.introUrl = introUrl;
    }

    public String getCsUrl()
    {
        return csUrl;
    }

    public void setCsUrl(String csUrl)
    {
        this.csUrl = csUrl;
    }

    public String getPreCharge()
    {
        return preCharge;
    }

    public void setPreCharge(String preCharge)
    {
        this.preCharge = preCharge;
    }

    public String getMaxItemPreDay()
    {
        return maxItemPreDay;
    }

    public void setMaxItemPreDay(String maxItemPreDay)
    {
        this.maxItemPreDay = maxItemPreDay;
    }

    public String getIsTextEcgn()
    {
        return isTextEcgn;
    }

    public void setIsTextEcgn(String isTextEcgn)
    {
        this.isTextEcgn = isTextEcgn;
    }

    public String getMaxItemPreMon()
    {
        return maxItemPreMon;
    }

    public void setMaxItemPreMon(String maxItemPreMon)
    {
        this.maxItemPreMon = maxItemPreMon;
    }

    public String getDefaultEcgnLang()
    {
        return defaultEcgnLang;
    }

    public void setDefaultEcgnLang(String defaultEcgnLang)
    {
        this.defaultEcgnLang = defaultEcgnLang;
    }

    public String getTextEcgnEn()
    {
        return textEcgnEn;
    }

    public void setTextEcgnEn(String textEcgnEn)
    {
        this.textEcgnEn = textEcgnEn;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getForbidStartTimeA()
    {
        return forbidStartTimeA;
    }

    public void setForbidStartTimeA(String forbidStartTimeA)
    {
        this.forbidStartTimeA = forbidStartTimeA;
    }

    public String getForbidEndTimeA()
    {
        return forbidEndTimeA;
    }

    public void setForbidEndTimeA(String forbidEndTimeA)
    {
        this.forbidEndTimeA = forbidEndTimeA;
    }

    public String getForbidStartTimeB()
    {
        return forbidStartTimeB;
    }

    public void setForbidStartTimeB(String forbidStartTimeB)
    {
        this.forbidStartTimeB = forbidStartTimeB;
    }

    public String getForbidEndTimeB()
    {
        return forbidEndTimeB;
    }

    public void setForbidEndTimeB(String forbidEndTimeB)
    {
        this.forbidEndTimeB = forbidEndTimeB;
    }

    public String getForbidStartTimeC()
    {
        return forbidStartTimeC;
    }

    public void setForbidStartTimeC(String forbidStartTimeC)
    {
        this.forbidStartTimeC = forbidStartTimeC;
    }

    public String getForbidEndTimeC()
    {
        return forbidEndTimeC;
    }

    public void setForbidEndTimeC(String forbidEndTimeC)
    {
        this.forbidEndTimeC = forbidEndTimeC;
    }

    public String getForbidStartTimeD()
    {
        return forbidStartTimeD;
    }

    public void setForbidStartTimeD(String forbidStartTimeD)
    {
        this.forbidStartTimeD = forbidStartTimeD;
    }

    public String getForbidEndTimeD()
    {
        return forbidEndTimeD;
    }

    public void setForbidEndTimeD(String forbidEndTimeD)
    {
        this.forbidEndTimeD = forbidEndTimeD;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public String getSvrCodeHead()
    {
        return svrCodeHead;
    }

    public void setSvrCodeHead(String svrCodeHead)
    {
        this.svrCodeHead = svrCodeHead;
    }

    public String getMoAccessNum()
    {
        return moAccessNum;
    }

    public void setMoAccessNum(String moAccessNum)
    {
        this.moAccessNum = moAccessNum;
    }

    public String getSpCode()
    {
        return spCode;
    }

    public void setSpCode(String spCode)
    {
        this.spCode = spCode;
    }

    public String getRsrvStr2()
    {
        return rsrvStr2;
    }

    public void setRsrvStr2(String rsrvStr2)
    {
        this.rsrvStr2 = rsrvStr2;
    }

    public String getRsrvStr4()
    {
        return rsrvStr4;
    }

    public void setRsrvStr4(String rsrvStr4)
    {
        this.rsrvStr4 = rsrvStr4;
    }

    public String getIsMasServ()
    {
        return isMasServ;
    }

    public void setIsMasServ(String isMasServ)
    {
        this.isMasServ = isMasServ;
    }

    public String getRsrvTag1()
    {
        return rsrvTag1;
    }

    public void setRsrvTag1(String rsrvTag1)
    {
        this.rsrvTag1 = rsrvTag1;
    }

    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public String getRsrvTag3()
    {
        return rsrvTag3;
    }

    public void setRsrvTag3(String rsrvTag3)
    {
        this.rsrvTag3 = rsrvTag3;
    }

    public String getIsNeedPf()
    {
        return isNeedPf;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }

    public String getSiBaseInCode()
    {
        return siBaseInCode;
    }

    public void setSiBaseInCode(String siBaseInCode)
    {
        this.siBaseInCode = siBaseInCode;
    }

    public String getSiBaseInCodeAttr()
    {
        return siBaseInCodeAttr;
    }

    public void setSiBaseInCodeAttr(String siBaseInCodeAttr)
    {
        this.siBaseInCodeAttr = siBaseInCodeAttr;
    }

    public String getEcBaseInCode()
    {
        return ecBaseInCode;
    }

    public void setEcBaseInCode(String ecBaseInCode)
    {
        this.ecBaseInCode = ecBaseInCode;
    }

    public String getEcBaseInCodeAttr()
    {
        return ecBaseInCodeAttr;
    }

    public void setEcBaseInCodeAttr(String ecBaseInCodeAttr)
    {
        this.ecBaseInCodeAttr = ecBaseInCodeAttr;
    }

    public String getPlatSyncState()
    {
        return platSyncState;
    }

    public void setPlatSyncState(String platSyncState)
    {
        this.platSyncState = platSyncState;
    }

    public String getBillingMode()
    {
        return billingMode;
    }

    public void setBillingMode(String billingMode)
    {
        this.billingMode = billingMode;
    }

    public String getDeliverNum()
    {
        return deliverNum;
    }

    public void setDeliverNum(String deliverNum)
    {
        this.deliverNum = deliverNum;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
    }

    public String getWhiteTwocheck()
    {
        return whiteTwocheck;
    }

    public void setWhiteTwocheck(String whiteTwocheck)
    {
        this.whiteTwocheck = whiteTwocheck;
    }

    public String getSmsTemplate()
    {
        return smsTemplate;
    }

    public void setSmsTemplate(String smsTemplate)
    {
        this.smsTemplate = smsTemplate;
    }

    public String getPortType()
    {
        return portType;
    }

    public void setPortType(String portType)
    {
        this.portType = portType;
    }

    public String getAdminAccessNum()
    {
        return adminAccessNum;
    }

    public void setAdminAccessNum(String adminAccessNum)
    {
        this.adminAccessNum = adminAccessNum;
    }

    public String getTextEcgnZh()
    {
        return textEcgnZh;
    }

    public void setTextEcgnZh(String textEcgnZh)
    {
        this.textEcgnZh = textEcgnZh;
    }

    public String getOperState()
    {
        return operState;
    }

    public void setOperState(String operState)
    {
        this.operState = operState;
    }

    public String getPlatsvcInsId()
    {
        return platsvcInsId;
    }

    public void setPlatsvcInsId(String platsvcInsId)
    {
        this.platsvcInsId = platsvcInsId;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getCancelFlag()
    {
        return cancelFlag;
    }

    public void setCancelFlag(String cancelFlag)
    {
        this.cancelFlag = cancelFlag;
    }

    public String getValidDate()
    {
        return validDate;
    }

    public void setValidDate(String validDate)
    {
        this.validDate = validDate;
    }

    public String getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(String expireDate)
    {
        this.expireDate = expireDate;
    }

    public String getOprEffTime()
    {
        return oprEffTime;
    }

    public void setOprEffTime(String oprEffTime)
    {
        this.oprEffTime = oprEffTime;
    }
    
    public String getServCode()
    {
        return servCode;
    }

    public void setServCode(String servCode)
    {
        this.servCode = servCode;
    }

    public String getBizStateCode()
    {
        return bizStateCode;
    }

    public void setBizStateCode(String bizStateCode)
    {
        this.bizStateCode = bizStateCode;
    }

    public String getAccessNumber()
    {
        return accessNumber;
    }

    public void setAccessNumber(String accessNumber)
    {
        this.accessNumber = accessNumber;
    }

    public String getSendPfType()
    {
        return sendPfType;
    }

    public void setSendPfType(String sendPfType)
    {
        this.sendPfType = sendPfType;
    }

    public static GPlatSvcData getInstance(IData platsvc) throws Exception
    {

        if (IDataUtil.isEmpty(platsvc))
            return null;
        GPlatSvcData data = new GPlatSvcData();

        data.setPlatsvcInsId(platsvc.getString("PLATSVC_INS_ID", ""));
        data.setServiceId(platsvc.getString("SERVICE_ID"));
        data.setServCode(platsvc.getString("BIZ_IN_CODE")); // 服务代码
        data.setBizTypeCode(platsvc.getString("BIZ_TYPE_CODE")); // (短信,wap..)NOW这个要加到其他要展现的地方做获取
        data.setBizCode(platsvc.getString("BIZ_CODE")); // 1 业务代码
        data.setBizAttr(platsvc.getString("BIZ_ATTR")); // 7 业务属性(订购关系，白黑名单)
        data.setBizName(platsvc.getString("BIZ_NAME")); // 2 业务名称
        data.setBizInCode(platsvc.getString("BIZ_IN_CODE"));// 业务接入号
        data.setBizStateCode(platsvc.getString("BIZ_STATE_CODE"));
        data.setBizStatus(platsvc.getString("BIZ_STATUS")); // 6 业务状态(正常商用..)
        data.setBizPri(platsvc.getString("BIZ_PRI")); // 11业务优先级
        data.setAuthCode(platsvc.getString("AUTH_CODE"));// 23业务接入号鉴权方式
        data.setUsageDesc(platsvc.getString("USAGE_DESC")); // 10业务方法描述
        data.setIntroUrl(platsvc.getString("INTRO_URL")); // 12 业务介绍网址
        data.setBillingType(platsvc.getString("BILLING_TYPE")); // 4 计费类型 (包月。。)
        data.setBillingMode(platsvc.getString("BILLING_MODE", "")); // 计费模式
        data.setPrice(platsvc.getString("PRICE")); // 8 单价
        data.setPreCharge(platsvc.getString("PRE_CHARGE")); // 15 预付费标记
        data.setCsTel(platsvc.getString("CS_TEL"));
        data.setCsUrl(platsvc.getString("CS_URL")); // 13EC URL 14 计费模式 (上行，下行..)NOW暂无表字段
        data.setAccessMode(platsvc.getString("ACCESS_MODE", "")); // 5对应1.3.3版业务承载方式01－SMS，02－WAPPush，03－MMS
        data.setAccessNumber(platsvc.getString("BIZ_IN_CODE"));
        data.setSiBaseInCode(platsvc.getString("SI_BASE_IN_CODE"));// SI基本接入号 todayadd 2 rows,这里填扩展前的接入号
        data.setSiBaseInCodeAttr(platsvc.getString("SIBASE_INCODE_A"));// SI 基本接入号属性
        data.setEcBaseInCode(platsvc.getString("BIZ_IN_CODE", ""));// 09年12月9号联调，网关平台要求ECBaseServCode与BizServCode一致
        data.setEcBaseInCodeAttr(platsvc.getString("SI_BASE_IN_CODE_A")); // 现直接写SI接入号的属性
        data.setMaxItemPreDay(platsvc.getString("MAX_ITEM_PRE_DAY")); // 16 每天最大短信数
        data.setMaxItemPreMon(platsvc.getString("MAX_ITEM_PRE_MON")); // 18 每月最大短信
        data.setDeliverNum(platsvc.getString("DELIVER_NUM", "0")); // 限制下发次数(0为不限制)
        data.setForbidStartTimeA("".equals(platsvc.getString("FORBID_START_TIME_A")) ? "000000" : platsvc.getString("FORBID_START_TIME_A"));
        data.setForbidEndTimeA("".equals(platsvc.getString("FORBID_END_TIME_A")) ? "000000" : platsvc.getString("FORBID_END_TIME_A"));
        data.setForbidStartTimeB(platsvc.getString("FORBID_START_TIME_B"));
        data.setForbidEndTimeB(platsvc.getString("FORBID_END_TIME_B"));
        data.setForbidStartTimeC(platsvc.getString("FORBID_START_TIME_C"));
        data.setForbidEndTimeC(platsvc.getString("FORBID_END_TIME_C"));
        data.setForbidStartTimeD(platsvc.getString("FORBID_START_TIME_D"));
        data.setForbidEndTimeD(platsvc.getString("FORBID_END_TIME_D"));
        data.setIsTextEcgn(platsvc.getString("IS_TEXT_ECGN")); // 17 短信正文签名
        data.setDefaultEcgnLang(platsvc.getString("DEFAULT_ECGN_LANG")); // 19 签名语言
        data.setTextEcgnEn(platsvc.getString("TEXT_ECGN_EN")); // 20 英文签名
        data.setTextEcgnZh(platsvc.getString("TEXT_ECGN_ZH")); // 22 中文签名
        data.setOprEffTime(platsvc.getString("OPR_EFF_TIME", ""));
        data.setOperState(platsvc.getString("OPER_STATE", "")); // 新疆直接写死"0"
        data.setAdminAccessNum(platsvc.getString("ADMIN_NUM"));// 21 管理员手机号码
        data.setMasId(platsvc.getString("MAS_ID", ""));
        data.setPlatSyncState(platsvc.getString("PLAT_SYNC_STATE")); // 新疆直接写死"1"
        data.setValidDate(platsvc.getString("START_DATE", ""));
        data.setExpireDate(platsvc.getString("END_DATE", ""));
        data.setRemarks(platsvc.getString("REMARKS"));
        data.setSendPfType("0");// 标识是否走服务开通:0-正常走服务开通模式; 1-ADC平台; 2-行业网关; 告警平台业务赋值4，语音业务不发指令
        data.setAction(platsvc.getString("MODIFY_TAG", ""));
        
        
        /** 以下属性新疆暂时没有用到 **/
        data.setSvrCodeHead(platsvc.getString("SVR_CODE_HEAD"));// 服务代码头
        data.setMoAccessNum(platsvc.getString("MO_ACCESS_NUM", ""));// 短信上行访问码
        data.setSpCode(platsvc.getString("SP_CODE", ""));// 校讯通填写合作伙伴编码
        data.setRsrvStr2(platsvc.getString("RSRV_STR2", ""));// 企业邮箱业务时前台传入的邮箱域名
        data.setRsrvTag1(platsvc.getString("RSRV_TAG1", ""));// 全网ADC时 产品受理付费模式
        data.setRsrvTag2(platsvc.getString("RSRV_TAG2", "1"));// 集团客户等级
        // J2EE以前标识是否发服务开通，现在没用了.反向接口用TF_B_TRADE.in_mode_code字段,值为P只发网关送， G只发ADC平台， 其他值adc平台和网关都发送.
        data.setRsrvTag3(platsvc.getString("RSRV_TAG3", "0"));
        data.setIsNeedPf(platsvc.getString("IS_NEED_PF", "1"));// 1：走服务开通发指令, 0：不走服务开通不发指令
        data.setServiceType(platsvc.getString("SERVICE_TYPE", "")); // 业务类型
        data.setWhiteTwocheck(platsvc.getString("WHITE_TOWCHECK", "")); // 白名单二次确认
        data.setSmsTemplate(platsvc.getString("SMS_TEMPALTE", "")); // 模板短信管理
        data.setPortType(platsvc.getString("PORT_TYPE", "")); // 端口类别
        data.setRsrvStr4(platsvc.getString("RSRV_STR4", ""));
        data.setIsMasServ(platsvc.getString("IS_MAS_SERV", ""));
        data.setCancelFlag(platsvc.getString("CANCLE_FLAG", ""));

        IDataset moList = platsvc.getDataset("MO_LIST");
        if (IDataUtil.isNotEmpty(moList))
        {
            for (Object mObject : moList)
            {
                MoListData mo = MoListData.getInstance((IData)mObject);
                data.addMo(mo);
            }
        }

        return data;
    }
}
