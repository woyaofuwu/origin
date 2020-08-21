package com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg;

import java.util.ArrayList;

import com.ailk.common.data.IData;

/**
 * 局数据类
 * @author jinnian
 *
 */
public class OfficeDataCfg {
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

    private String validDate;

    private String expireDate;

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
    
    public OfficeDataCfg(IData data){
        this.billType = data.getString("BILL_TYPE");
        this.bizAttr = data.getString("BIZ_ATTR");
        this.bizStateCode = data.getString("BIZ_STATE_CODE");
        this.bizTypeCode = data.getString("BIZ_TYPE_CODE");
        this.orgDomain = data.getString("ORG_DOMAIN");
        this.price = data.getString("PRICE");
        this.serviceName = data.getString("SERVICE_NAME");
        this.servType = data.getString("SERV_TYPE");
        this.spCode = data.getString("SP_CODE");
        this.spName = data.getString("SP_NAME");
        this.bizCode = data.getString("BIZ_CODE");
        this.thirdConfirm = data.getString("THIRD_CONFIRM");
        this.servMode = data.getString("SERV_MODE");
        this.feeModeTag = null == data.getString("FEEMODE_TAG") ? "0" : data.getString("FEEMODE_TAG");
        this.validDate = data.getString("VALID_DATE");
        this.expireDate = data.getString("EXPIRE_DATE");
        this.csTel = data.getString("CS_TEL");
        this.cancelTag = null == data.getString("CANCEL_TAG") ? "1" : data.getString("CANCEL_TAG");
        this.feeWarnTag = null == data.getString("FEEWARN_TAG") ? "1" : data.getString("FEEWARN_TAG");
        this.secConfirmTag = null == data.getString("SECCONFIRM_TAG")? "0" : data.getString("SECCONFIRM_TAG");
        this.transferOperCode(data.getString("BIZ_PROCESS_TAG"));
        this.bizProcessTag = data.getString("BIZ_PROCESS_TAG");
        this.edBizProcessTag = data.getString("EDBIZ_PROCESS_TAG");
    }

	public String getServiceName() {
		return serviceName;
	}

	public String getOrgDomain() {
		return orgDomain;
	}

	public String getBizTypeCode() {
		return bizTypeCode;
	}

	public String getBizStateCode() {
		return bizStateCode;
	}

	public String getServType() {
		return servType;
	}

	public String getSpCode() {
		return spCode;
	}

	public String getSpName() {
		return spName;
	}

	public String getBizCode() {
		return bizCode;
	}

	public String getBizAttr() {
		return bizAttr;
	}

	public String getBillType() {
		return billType;
	}

	public String getPrice() {
		return price;
	}

	public String getCsTel() {
		return csTel;
	}

	public String getServMode() {
		return servMode;
	}

	public String getFeeModeTag() {
		return feeModeTag;
	}

	public String getValidDate() {
		return validDate;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public String getCancelTag() {
		return cancelTag;
	}

	public String getFeeWarnTag() {
		return feeWarnTag;
	}

	public String getSecConfirmTag() {
		return secConfirmTag;
	}

	public String getThirdConfirm() {
		return thirdConfirm;
	}

	public String getBizProcessTag() {
		return bizProcessTag;
	}

	public String getEdBizProcessTag() {
		return edBizProcessTag;
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
