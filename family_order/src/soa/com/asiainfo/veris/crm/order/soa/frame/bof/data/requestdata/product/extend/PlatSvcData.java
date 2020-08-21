
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public class PlatSvcData extends ProductModuleData
{
    private String activeTag;// 1-主动，2-被动

    private String allTag; // 全业务标记 00-正常，01-全业务，02-SP全业务，03-总开关，04-分开关

    private String cashPayMode;// 手机钱包业务时，填写支付方式 01-通信帐户支付 02-手机银行卡支付

    private String entityCardNo;

    private String giftSerialNumber;

    private String giftUserId;

    private String inCardNo;

    private String intfTradeId;

    private PlatOfficeData officeData;// 可以根据service或者biz_type_code+sp_code+biz_code构建

    private String operCode;

    private String oprSource;

    private String packageId = PlatConstants.PACKAGE_ID;

    private String pkgSeq;

    private String productId = "50000000";

    private boolean scoreChange;

    private String scoreChangeTime;

    private String udSum;

    private String opeReasion;
    
    private String rsrvStr10; // 保存互联网默认开通的PASS_ID

    private String isNeedPf; // 某些特殊业务，可能需要根据掺入参数，判断是否发服开；

    private String rsrvStr8; //保存原OPER_CODE 用于VOIP 
    
    private String rsrvStr9; //封顶暂停的时候需要标记这个字段为1

    private String rsrvStr7; //咪咕音乐平台CHANNEL_CODE字段
    
    /**
     * 构造函数，通过建造者构建出局部对象，然后构建出完整的od
     */
    public PlatSvcData()
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
    }

    public PlatSvcData(IData map) throws Exception
    {
        String serviceId = map.getString("SERVICE_ID", map.getString("ELEMENT_ID"));
        PlatOfficeData officeData = null;
        if (StringUtils.isNotBlank(serviceId))
        {
            officeData = PlatOfficeData.getInstance(serviceId);
        }
        else
        {
            officeData = PlatOfficeData.getInstance(map.getString("BIZ_TYPE_CODE"), map.getString("SP_CODE"), map.getString("BIZ_CODE"));
        }
        this.setOfficeData(officeData);
        if (!StringUtils.isBlank(map.getString("START_DATE")))
        {
            this.setStartDate(map.getString("START_DATE"));
        }
        if (!StringUtils.isBlank(map.getString("END_DATE")))
        {
            this.setEndDate(map.getString("END_DATE"));
        }
        this.setGiftSerialNumber(map.getString("GIFT_SERIAL_NUMBER"));
        if (StringUtils.isNotBlank(this.giftSerialNumber))
        {
            if (StringUtils.isNotBlank(map.getString("GIFT_START_DATE")))
            {
                this.setStartDate(map.getString("GIFT_START_DATE"));
            }
            if (StringUtils.isNotBlank(map.getString("GIFT_END_DATE")))
            {
                this.setEndDate(map.getString("GIFT_END_DATE"));
            }
        }
        this.setOperCode(map.getString("OPER_CODE"));
        this.setInCardNo(map.getString("IN_CARD_NO"));
        this.setElementId(officeData.getServiceId());
        this.setActiveTag(map.getString("OPR_REASION", "").equals("2")?"1":"0");// 0表示主动，1表示被动
        this.setIntfTradeId(map.getString("INTF_TRADE_ID", map.getString("TRANS_ID")));

        // 处理 前台导入批量平台业务
//        String batchCode = map.getString("BATCH_CODE");
//        if (StringUtils.isBlank(map.getString("PKGSEQ")) && StringUtils.isNotBlank(batchCode) && "16_23_60_B3".indexOf(officeData.getBizTypeCode()) > -1)
//        {
//            this.setPkgSeq(batchCode);
//            IDataset batchSum = BatDealInfoQry.queryBatchDealSum(batchCode);
//            int udsum = 1;
//            if (IDataUtil.isNotEmpty(batchSum))
//            {
//                udsum = batchSum.getData(0).getInt("UDSUM");
//            }
//            this.setUdSum(String.valueOf(udsum));
//        }
//        else
//        {
            this.setPkgSeq(map.getString("PKGSEQ"));
            this.setUdSum(map.getString("UDSUM"));
//        }

        this.setInstId(map.getString("INST_ID"));
        this.setScoreChange(Boolean.valueOf(map.getString("SCORE_CHANGE")));
        this.setScoreChangeTime(map.getString("SCORE_CHANGE_TIME"));
        this.setEntityCardNo(map.getString("ENTITY_CARD_NO"));
        this.setProductId(map.getString("PRODUCT_ID", "50000000"));
        this.setPackageId(map.getString("PACKAGE_ID", PlatConstants.PACKAGE_ID));
        this.setRsrvStr8(map.getString("RSRV_STR8","")); 
        this.setRsrvStr7(map.getString("CHANNEL_CODE","")); 
        this.setRsrvStr10(map.getString("RSRV_STR10", ""));
        this.setIsNeedPf(map.getString("IS_NEED_PF", ""));
        this.setOpeReasion(map.getString("OPR_REASION", ""));
        if (!StringUtils.isBlank(map.getString("OPR_SOURCE")))
        {
            //渠道编码转换 zhangbo18 20150921 begin
            String opr_sourceStr = map.getString("OPR_SOURCE");
            String in_mode_code  = CSBizBean.getVisit().getInModeCode();
            String param_code = officeData.getBizTypeCode() +"_"+ opr_sourceStr + "_"+ in_mode_code;
            
            IDataset paramList = ParamInfoQry.getCommparaByCode("CSM", "4602", param_code,"ZZZZ");
            if (null != paramList && !paramList.isEmpty()){
                IData paramInfo = paramList.getData(0);
                //如果配置表中有记录，但未设置对应的新的渠道编码，则默认取传入的渠道编码
                opr_sourceStr = paramInfo.getString("PARA_CODE1");
                if (null == opr_sourceStr || opr_sourceStr.isEmpty()){
                    opr_sourceStr = map.getString("OPR_SOURCE");
                }
            }
            //渠道编码转换 zhangbo18 20150921 end
            this.setOprSource(opr_sourceStr);
        }
        else
        {
            if ("14".equals(officeData.getBizTypeCode()) || 
                    "36".equals(officeData.getBizTypeCode()) ||
                    //"60".equals(officeData.getBizTypeCode()) ||
                    "28".equals(officeData.getBizTypeCode()) ||
                    "19".equals(officeData.getBizTypeCode()) ||
                    "25".equals(officeData.getBizTypeCode()) ||
                    "77".equals(officeData.getBizTypeCode()) ||
                    "46".equals(officeData.getBizTypeCode()) ||
                    "78".equals(officeData.getBizTypeCode()) ||
                    "80".equals(officeData.getBizTypeCode())){
                this.setOprSource("11");
            }else{
                this.setOprSource("08");
            }
        }
        this.setRemark(map.getString("REMARK"));

        IDataset attrDatas = map.getDataset("ATTR_PARAM");
        if (!IDataUtil.isEmpty(attrDatas))
        {
            int size = attrDatas.size();
            List<AttrData> attrs = new ArrayList<AttrData>();
            for (int i = 0; i < size; i++)
            {
                IData attrData = attrDatas.getData(i);
                AttrData attr = new AttrData();
                attr.setAttrCode(attrData.getString("ATTR_CODE"));
                attr.setAttrValue(attrData.getString("ATTR_VALUE"));
                attr.setModifyTag(attrData.getString("MODIFY_TAG", this.getAttrModifyTag(this.getOperCode())));
                attrs.add(attr);
            }
            this.setAttrs(attrs);
        }

        this.setElementType(BofConst.ELEMENT_TYPE_CODE_PLATSVC);
    }

    public String getActiveTag()
    {
        return activeTag;
    }

    public String getAllTag()
    {
        return allTag;
    }

    public String getAttrModifyTag(String operCode)
    {
        if (PlatConstants.OPER_CANCEL_TC.equals(operCode))
        {
            return BofConst.MODIFY_TAG_DEL;
        }

        return this.getModifyTag();
    }

    public String getCashPayMode()
    {
        return cashPayMode;
    }

    public String getEntityCardNo()
    {
        return entityCardNo;
    }

    public String getGiftSerialNumber()
    {

        return giftSerialNumber;
    }

    public String getGiftUserId()
    {
        return giftUserId;
    }

    public String getInCardNo()
    {
        return inCardNo;
    }

    public String getIntfTradeId()
    {
        return intfTradeId;
    }

    public String getIsNeedPf()
    {
        return isNeedPf;
    }

    /**
     * 获取局数据对象
     * 
     * @return 返回局数据对象
     */
    public PlatOfficeData getOfficeData()
    {

        return officeData;
    }

    /**
     * 获取操作码
     * 
     * @return 返回本次操作的操作码
     */
    public String getOperCode()
    {

        return operCode;
    }

    /**
     * 获取操作来源
     * 
     * @return 返回操作来源
     */
    public String getOprSource()
    {

        return oprSource;
    }

    /**
     * 获取包ID，平台服务默认包ID为-1
     * 
     * @return 返回包ID
     */
    @Override
    public String getPackageId()
    {

        return packageId;
    }

    public String getPkgSeq()
    {
        return pkgSeq;
    }

    /**
     * 获取产品ID，平台服务默认产品ID为50000000
     * 
     * @return 返回产品ID
     */
    @Override
    public String getProductId()
    {

        return productId;
    }
    
    public String getRsrvStr8() 
    {
        return rsrvStr8;
    }
    
    public String getRsrvStr7() 
    {
        return rsrvStr7;
    }

    public String getRsrvStr10()
    {
        return rsrvStr10;
    }

    public String getScoreChangeTime()
    {
        return scoreChangeTime;
    }

    public String getUdSum()
    {
        return udSum;
    }

    public boolean isScoreChange()
    {
        return scoreChange;
    }

    public void setActiveTag(String activeTag)
    {
        this.activeTag = activeTag;
    }

    public void setAllTag(String allTag)
    {
        this.allTag = allTag;
    }

    public void setCashPayMode(String cashPayMode)
    {
        this.cashPayMode = cashPayMode;
    }

    public void setEntityCardNo(String entityCardNo)
    {
        this.entityCardNo = entityCardNo;
    }

    public void setGiftSerialNumber(String giftSerialNumber) throws Exception
    {
        this.giftSerialNumber = giftSerialNumber;
        if (StringUtils.isNotBlank(giftSerialNumber))
        {
            UcaData uca = UcaDataFactory.getNormalUca(giftSerialNumber, false, false);
            this.giftUserId = uca.getUserId();
        }
    }

    public void setInCardNo(String inCardNo)
    {
        this.inCardNo = inCardNo;
    }

    public void setIntfTradeId(String intfTradeId)
    {
        this.intfTradeId = intfTradeId;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }

    /**
     * 设置局数据对象PlatOfficeData
     * 
     * @param officeData
     *            局数据对象，见PlatOfficeData
     */
    public void setOfficeData(PlatOfficeData officeData)
    {

        this.officeData = officeData;
    }

    /**
     * 设置操作码，同时根据操作码生成业务状态
     * 
     * @param operCode
     *            操作码
     */
    public void setOperCode(String operCode) throws Exception
    {

        this.operCode = operCode;
        if (StringUtils.isNotBlank(operCode) && this.officeData != null && !this.officeData.getSupportOperCode().contains(operCode))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_0904);
        }
        if (operCode.equals(PlatConstants.OPER_ORDER) || PlatConstants.OPER_SERVICE_CLOSE.equals(operCode))
        {
            this.setModifyTag(BofConst.MODIFY_TAG_ADD);
        }
        else if (operCode.equals(PlatConstants.OPER_CANCEL_ORDER) || PlatConstants.OPER_SERVICE_OPEN.equals(operCode))
        {
            this.setModifyTag(BofConst.MODIFY_TAG_DEL);
        }
        else
        {
            this.setModifyTag(BofConst.MODIFY_TAG_UPD);
        }
    }

    /**
     * 设置操作来源
     * 
     * @param oprSource
     *            操作来源
     */
    public void setOprSource(String oprSource)
    {

        this.oprSource = oprSource;
    }

    /**
     * 设置包ID
     * 
     * @param packageId
     *            包ID
     */
    @Override
    public void setPackageId(String packageId)
    {

        this.packageId = packageId;
    }

    public void setPkgSeq(String pkgSeq)
    {
        this.pkgSeq = pkgSeq;
    }

    /**
     * 设置产品ID
     * 
     * @param productId
     *            产品ID
     */
    @Override
    public void setProductId(String productId)
    {

        this.productId = productId;
    }

    public void setRsrvStr10(String rsrvStr10)
    {
        this.rsrvStr10 = rsrvStr10;
    }

    public void setScoreChange(boolean scoreChange)
    {
        this.scoreChange = scoreChange;
    }

    public void setScoreChangeTime(String scoreChangeTime)
    {
        this.scoreChangeTime = scoreChangeTime;
    }

    public void setUdSum(String udSum)
    {
        this.udSum = udSum;
    }
    
    public void setRsrvStr8(String rsrvStr8) { 
        this.rsrvStr8 = rsrvStr8;
     }
    
    public void setRsrvStr7(String rsrvStr7) { 
    	this.rsrvStr7 = rsrvStr7;
     }
    
    public String getRsrvStr9(){
        return rsrvStr9;
    }
    
    public void setRsrvStr9(String rsrvStr9){
        this.rsrvStr9 = rsrvStr9;
    }
    public String getOpeReasion() {
        return opeReasion;
    }
    
    public void setOpeReasion(String opeReasion) {
        this.opeReasion = opeReasion;
    }
}
