
package com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat;

import java.io.Serializable;
import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class PlatOfficeData implements Serializable
{
    /**
     * 获取局数据对象，如果缓存中存在，则从缓存中取，否则创建一个
     * 
     * @param pd
     *            PageData对象
     * @param serviceId
     *            服务ID
     * @return 返回局数据对象
     * @throws Exception
     */
    public static PlatOfficeData getInstance(String serviceId) throws Exception
    {
        PlatOfficeData officeData = new PlatOfficeData(serviceId);
        return officeData;
    }

    public static PlatOfficeData getInstance(String bizTypeCode, String spCode, String bizCode) throws Exception
    {
        // 非注册类业务局数据由SP_CODE和BIZ_CODE唯一确定， 注册类业务可以由BIZ_TYPE_CODE唯一确定
        if ((StringUtils.isBlank(spCode) || StringUtils.isBlank(bizCode)) && StringUtils.isBlank(bizTypeCode))
        {
            CSAppException.apperr(BofException.CRM_BOF_015);
        }

        PlatOfficeData officeData = new PlatOfficeData(bizTypeCode, spCode, bizCode);
        return officeData;
    }

    /**
     * 服务ID
     */
    private String serviceId;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 平台编码
     */
    private String orgDomain;

    /**
     * 平台业务类型
     */
    private String bizTypeCode;

    /**
     * 服务状态
     */
    private String bizStateCode;

    /**
     * 服务类型 0--注册类，1--订购类，2--全退订类，3--开关类
     */
    private String servType;

    /**
     * 企业代码
     */
    private String spCode;

    /**
     * 业务代码
     */
    private String spName;

    /**
     * 业务代码
     */
    private String bizCode;

    /**
     * 业务属性 G--全网 L--本地
     */
    private String bizAttr;

    /**
     * 计费类型 0-免费 1-按条 2-包月
     */
    private String billType;

    /**
     * 价格
     */
    private String price; // 取出来的价格是已经除了1000，是元为单位的

    private String csTel;// 客服电话

    private String servMode; // 0:梦网 1：移动自有业务

    private String feeModeTag; // 0:立即计费 1:72小时免费

    private String startDate;

    private String endDate;

    private String cancelTag; // 0:不可以通用查询退订 1:可以通用查询退订

    private String feeWarnTag; // 0:不进行扣费提醒 1:进行扣费提醒

    private String secConfirmTag; // 0:订购时，不需要进行二次确认 1：订购时，需要进行二次确认

    /**
     * 第三方平台确认
     */
    private String thirdConfirm;

    private String bizProcessTag; // 用户可主动做的操作

    private String edBizProcessTag; // 用户被动暂停标识为判断，用户停开机时，连带平台服务暂停时，首先需要判断此位置

    /**
     * 该服务支持的操作码集合
     */
    private ArrayList<String> supportOperCode = new ArrayList();

    /**
     * 属性集合
     */
    private ArrayList<PlatAttrData> attrDatas = new ArrayList();

    private PlatOfficeData(String serviceId) throws Exception
    {

    	IDataset platSvcs = new DatasetList();
    	try{
    		platSvcs = UpcCall.querySpServiceAndInfoAndParamByCond(serviceId, "", "", "");
    	}catch(Exception e)
    	{
    		
    	}
    	
    	if (platSvcs == null || platSvcs.size() == 0)
        {
            CSAppException.apperr(BofException.CRM_BOF_016);
        }
    	IData plat = platSvcs.getData(0);
    	
        //IDataset platSvcs = BofQuery.getPlatInfoByServiceId(serviceId);
        /*if (plat == null || platSvcs.size() == 0)
        {
            CSAppException.apperr(BofException.CRM_BOF_016);
        }
        */
        this.serviceId = plat.getString("SERVICE_ID", plat.getString("OFFER_CODE"));
        this.billType = plat.getString("BILL_TYPE");
        this.bizAttr = plat.getString("BIZ_ATTR");
        this.bizStateCode = plat.getString("BIZ_STATE_CODE");
        this.bizTypeCode = plat.getString("BIZ_TYPE_CODE");
        this.orgDomain = plat.getString("ORG_DOMAIN");
        this.price = plat.getString("PRICE");
        this.serviceName = plat.getString("OFFER_NAME");//需要offer_name
        this.servType = plat.getString("SERV_TYPE");
        this.spCode = plat.getString("SP_CODE");
        this.spName = plat.getString("SP_NAME");
        this.bizCode = plat.getString("BIZ_CODE");
        this.thirdConfirm = plat.getString("THIRD_CONFIRM_TAG");//
        this.servMode = plat.getString("SERV_MODE");
        this.feeModeTag = plat.getString("FEEMODE_TAG", "0");
        this.startDate = plat.getString("VALID_DATE");//
        this.endDate = plat.getString("EXPIRE_DATE");//
        this.csTel = plat.getString("CS_TEL");
        this.cancelTag = plat.getString("CANCEL_TAG", "1");
        this.feeWarnTag = plat.getString("FEEWARN_TAG", "1");
        this.secConfirmTag = plat.getString("SECCONFIRM_TAG", "0");
        this.transferOperCode(plat.getString("BIZ_PROCESS_TAG"));
        this.bizProcessTag = plat.getString("BIZ_PROCESS_TAG");
        this.edBizProcessTag = plat.getString("EDBIZ_PROCESS_TAG");
        
        // IDataset platSvcAttrs = BofQuery.getPlatAttrByServiceId(serviceId);
        // if (platSvcAttrs != null && platSvcAttrs.size() > 0)
        // {
        // int size = platSvcAttrs.size();
        // for (int i = 0; i < size; i++)
        // {
        // IData attr = platSvcAttrs.getData(i);
        // PlatAttrData attrData = new PlatAttrData(serviceId, attr.getString("ATTR_CODE"),
        // attr.getString("RSRV_STR2"));
        // this.attrDatas.add(attrData);
        // }
        // }
    }

    private PlatOfficeData(String bizTypeCode, String spCode, String bizCode) throws Exception
    {

    	IDataset platSvcs = UpcCall.querySpServiceAndInfoAndParamByCond("", spCode, bizCode, bizTypeCode);
    	
        //IDataset platSvcs = BofQuery.getPlatInfoByBizTypeCode(bizTypeCode, spCode, bizCode);
        if (platSvcs == null || platSvcs.size() == 0)
        {
            CSAppException.apperr(BofException.CRM_BOF_016);
        }
        IData plat = platSvcs.getData(0);
        this.serviceId = plat.getString("SERVICE_ID", plat.getString("OFFER_CODE"));
        this.billType = plat.getString("BILL_TYPE");
        this.bizAttr = plat.getString("BIZ_ATTR");
        this.bizStateCode = plat.getString("BIZ_STATE_CODE");
        this.bizTypeCode = plat.getString("BIZ_TYPE_CODE");
        this.orgDomain = plat.getString("ORG_DOMAIN");
        this.price = plat.getString("PRICE", "0.0");
        this.serviceName = plat.getString("OFFER_NAME");
        this.servType = plat.getString("SERV_TYPE");
        this.spCode = plat.getString("SP_CODE");
        this.spName = plat.getString("SP_NAME");
        this.bizCode = plat.getString("BIZ_CODE");
        this.thirdConfirm = plat.getString("THIRD_CONFIRM_TAG");
        this.servMode = plat.getString("SERV_MODE");
        this.feeModeTag = plat.getString("FEEMODE_TAG", "0");
        this.startDate = plat.getString("VALID_DATE");
        this.endDate = plat.getString("EXPIRE_DATE");
        this.csTel = plat.getString("CS_TEL");
        this.cancelTag = plat.getString("CANCEL_TAG", "1");
        this.feeWarnTag = plat.getString("FEEWARN_TAG", "1");
        this.secConfirmTag = plat.getString("SECCONFIRM_TAG", "0");
        this.transferOperCode(plat.getString("BIZ_PROCESS_TAG"));
        this.bizProcessTag = plat.getString("BIZ_PROCESS_TAG");
        this.edBizProcessTag = plat.getString("EDBIZ_PROCESS_TAG");
        // IDataset platSvcAttrs = BofQuery.getPlatAttrByServiceId(serviceId);
        // if (platSvcAttrs != null && platSvcAttrs.size() > 0)
        // {
        // int size = platSvcAttrs.size();
        // for (int i = 0; i < size; i++)
        // {
        // IData attr = platSvcAttrs.getData(i);
        // PlatAttrData attrData = new PlatAttrData(serviceId, attr.getString("ATTR_CODE"),
        // attr.getString("RSRV_STR2"));
        // this.attrDatas.add(attrData);
        // }
        // }
    }

    /**
     * 获取该服务对应的属性集合，如果服务没有属性，则为null
     * 
     * @return 返回服务的属性集合
     */
    public ArrayList<PlatAttrData> getAttrDatas()
    {

        return attrDatas;
    }

    /**
     * 获取计费类型 0-免费 1-按条 2-包月
     * 
     * @return 返回计费类型
     */
    public String getBillType()
    {

        return billType;
    }

    /**
     * 获取业务属性 G-全网 L-本地
     * 
     * @return 返回业务属性
     */
    public String getBizAttr()
    {

        return bizAttr;
    }

    /**
     * 获取业务代码
     * 
     * @return 返回业务代码
     */
    public String getBizCode()
    {

        return bizCode;
    }

    public String getBizProcessTag()
    {
        return bizProcessTag;
    }

    /**
     * 获取服务状态 A--正常 E--失效
     * 
     * @return 返回服务状态
     */
    public String getBizStateCode()
    {

        return bizStateCode;
    }

    /**
     * 获取平台业务类型编码
     * 
     * @return 返回平台业务类型编码
     */
    public String getBizTypeCode()
    {

        return bizTypeCode;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getCsTel()
    {
        return csTel;
    }

    public String getEdBizProcessTag()
    {
        return edBizProcessTag;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getFeeModeTag()
    {
        return feeModeTag;
    }

    public String getFeeWarnTag()
    {
        return feeWarnTag;
    }

    /**
     * 获取平台编码
     * 
     * @return 返回平台编码
     */
    public String getOrgDomain()
    {

        return orgDomain;
    }

    /**
     * 获取价格
     * 
     * @return 返回价格
     */
    public String getPrice()
    {

        return price;
    }

    public String getSecConfirmTag()
    {
        return secConfirmTag;
    }

    /**
     * 获取服务ID
     * 
     * @return 返回服务ID
     */
    public String getServiceId()
    {

        return serviceId;
    }

    /**
     * 获取服务名称
     * 
     * @return 返回服务名称
     */
    public String getServiceName()
    {

        return serviceName;
    }

    public String getServMode()
    {
        return servMode;
    }

    /**
     * 获取服务类型 0--注册类，1--订购类，2--全退订类，3--开关类
     * 
     * @return 返回服务类型
     */
    public String getServType()
    {

        return servType;
    }

    /**
     * 获取企业代码
     * 
     * @return 返回企业代码
     */
    public String getSpCode()
    {

        return spCode;
    }

    /**
     * 获取企业名称
     * 
     * @return 返回企业名称
     */
    public String getSpName()
    {

        return spName;
    }

    public String getStartDate()
    {
        return startDate;
    }

    /**
     * 获取该服务支持的操作码
     * 
     * @return 返回服务所支持的操作码
     */
    public ArrayList<String> getSupportOperCode()
    {

        return supportOperCode;
    }

    /**
     * 返回是否需要第三方确认标记
     * 
     * @return
     */
    public String getThirdConfirm()
    {
        return thirdConfirm;
    }

    /**
     * 根据biz_process_tag转换成支持的操作码
     * 
     * @param bizProcessTag
     *            操作码序列
     * @return
     */
    private void transferOperCode(String bizProcessTag)
    {

        for (int i = 0; i < bizProcessTag.length(); i++)
        {
            String j = bizProcessTag.charAt(i) + "";

            if (!j.equals("1"))
            {
                continue;
            }

            switch (i)
            {
                case 0:
                    this.supportOperCode.add("01");// 第一位，注册
                    break;
                case 1:
                    this.supportOperCode.add("02");// --第二位，注销
                    break;
                case 2:
                    this.supportOperCode.add("03");// --第三位，密码修改
                    break;
                case 3:
                    this.supportOperCode.add("04");// --第四位 业务暂停
                    break;
                case 4:
                    this.supportOperCode.add("05");// --第五位 业务恢复
                    break;
                case 5:
                    this.supportOperCode.add("06");// --第六位 服务定购
                    break;
                case 6:
                    this.supportOperCode.add("07");// 第七位 服务订购取消
                    break;
                case 7:
                    this.supportOperCode.add("08");// 第八位 用户资料变更
                    break;
                case 8:
                    this.supportOperCode.add("11");// 第九位 赠送 del
                    break;
                case 9:
                    this.supportOperCode.add("14"); // 第十位 用户主动暂停 del
                    break;
                case 10:
                    this.supportOperCode.add("15"); // 第十一位，用户主动恢复 del
                    break;
                case 11:
                    this.supportOperCode.add("90"); // 第十二位 服务开关开
                    break;
                case 12:
                    this.supportOperCode.add("91"); // 十三位 服务开关关
                    break;
                case 13:
                    this.supportOperCode.add("89");// 第十四位，SP全退订 del
                    break;
                case 14:
                    this.supportOperCode.add("97"); // 第十伍位 全业务恢复 del
                    break;
                case 15:
                    this.supportOperCode.add("98"); // 第十六位 全业务暂停 del
                    break;
                case 16:
                    this.supportOperCode.add("99"); // 第十七位 全业务退订 del
                    break;
                case 17:
                    this.supportOperCode.add("14"); // 第十八位 点播
                    break;
                case 18:
                    this.supportOperCode.add("16"); // 第十九位 充值 编码待定
                    break;
                case 19:
                    this.supportOperCode.add("17"); // 第二十位 预约
                    break;
                case 20:
                    this.supportOperCode.add("18"); // 第二十一位 预约取消
                    break;
                case 21:
                    this.supportOperCode.add("19"); // 第二十二位 挂失 编码待定
                    break;
                case 22:
                    this.supportOperCode.add("20"); // 第二十三位 解挂 编码待定
                    break;
                case 23:
                    this.supportOperCode.add("10"); // 第二十四位 套餐订购
                    break;
                case 24:
                    this.supportOperCode.add("11"); // 第二十五位 套餐退订
                    break;
                case 25:
                    this.supportOperCode.add("09"); // 第二十六位 密码重置
                    break;
                case 26:
                    this.supportOperCode.add("12"); // 第二十七位 WLAN套餐变更
                    break;
                case 29:
                    this.supportOperCode.add("88"); // 第三十位 套餐变更 del
                    break;
                default:
                    ;
            }
        }
    }
}
