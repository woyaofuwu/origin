
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CreatePersonUserException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatchCreateUserIntfFilter implements IFilterIn
{

    /**
     * 批量开户接口入参规制校验
     * 
     * @author sunxin
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        String simCardNo = param.getString("SIM_CARD_NO");
        String productId = param.getString("PRODUCT_ID");
        String serialNumberPre = "";
        String simCardNoPre = "";
        String serialNumberOut = serialNumber;
        String simCardNoOut = simCardNo;
        if (StringUtils.isBlank(serialNumber) && StringUtils.isBlank(simCardNo))
        {
            CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_1);
        }
        IDataset checkMphoneDatas = new DatasetList();
        IDataset checkSimDatas = new DatasetList();
        // 导入模式为卡号段
        if (StringUtils.isBlank(serialNumber))
        {
            checkSimDatas = ResCall.getSimCardInfo("0", simCardNo, "", "");
            IData checkSimData = checkSimDatas.getData(0);
            serialNumberPre = checkSimData.getString("SERIAL_NUMBER_CODE");
            if (StringUtils.isEmpty(serialNumberPre))
                CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_8, simCardNo);
            serialNumberOut = serialNumberPre;
            checkProduct(serialNumberPre, productId);

        }
        // 导入模式为卡号段
        if (StringUtils.isBlank(simCardNo))
        {
            checkMphoneDatas = ResCall.getMphonecodeInfo(serialNumber);
            IData checkMphoneData = checkMphoneDatas.getData(0);
            simCardNoPre = checkMphoneData.getString("SIM_CARD_NO", "");
            if (StringUtils.isEmpty(simCardNoPre))
                CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_7, serialNumber);
            simCardNoOut = simCardNoPre;
            checkProduct(serialNumber, productId);
        }
        if (StringUtils.isNotBlank(serialNumber) && StringUtils.isNotBlank(simCardNo))
        {
            if (serialNumber.substring(0, 3).equals("898"))
            {
                CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_2);
            }
            // 校验在公用规则里 CreatePersonUserBaseCheck

        }
        // 校验资源 暂时不写，sunxin

        // 检查用户资料
        IDataset userInfo = UserInfoQry.getAllUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_5);
        }
        // 判断是否有未完工工单
        IDataset exitsTrade = TradeInfoQry.CheckIsExistNotFinishedTrade(serialNumber);
        if (IDataUtil.isNotEmpty(exitsTrade))
        {
            CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_6);
        }
        param.put("SERIAL_NUMBER", serialNumberOut);
        param.put("SIM_CARD_NO", simCardNoOut);
    }

    /*
     * 校验产品 sunxin
     */
    public void checkProduct(String serialNumber, String productId) throws Exception
    {
        IDataset defaultData = ProductInfoQry.getDefaultProductByPhone(serialNumber, "", CSBizBean.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(defaultData))
        {
            String productIdForDefault = defaultData.getData(0).getString("PRODUCT_ID");
            if (!productId.equals(productIdForDefault))
            {
                CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_3, serialNumber, productIdForDefault);
            }
        }
        if ("10009433".equals(productId))
        {
            IDataset productData = ProductInfoQry.getDefaultProductById(serialNumber, productId);
            if (IDataUtil.isEmpty(productData))
            {
                CSAppException.apperr(CreatePersonUserException.CREATEPERSONUSER_4, serialNumber, productId);
            }
        }
    }

    public void transferDataInput(IData input) throws Exception
    {
        // 进行规制校验
        checkInparam(input);

        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        String userPwd = input.getString("USER_PASSWD", "");
        String brandcode = input.getString("BRAND_CODE", "");
        /*
         * if ("".equals(userPwd)) { userPwd = RandomStringUtils.randomNumeric(6); }
         */
        IDataset checkMphoneDatas = new DatasetList();
        IDataset simCardInfo = new DatasetList();
        if ("1".equals(input.getString("M2M_FLAG", "0")))
        {

            checkMphoneDatas = ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0", "");
            simCardInfo = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", "", "", "0");
        }
        else
        {
            checkMphoneDatas = ResCall.checkResourceForMphoneBatch("0", serialNumber, "0","1",brandcode);
            simCardInfo = ResCall.checkResourceForSimBatch("0", serialNumber, simCardNo, "1",brandcode);
        }
        String imsi = simCardInfo.getData(0).getString("IMSI", "-1");
        String ki = simCardInfo.getData(0).getString("KI", "");
        if("500".equals(input.getString("TRADE_TYPE_CODE"))){ //由于很少部分RES_KIND_CODE为空时造成预开没有正常回收问题核查，陈小梅要求。
        	IDataset flags = StaticInfoQry.getStaticValidValueByTypeId("RES_KIND_CODE_FLAG", "1");
        	String resKindCode = simCardInfo.getData(0).getString("RES_KIND_CODE", "");
        	if(IDataUtil.isNotEmpty(flags) && flags.size() > 0 && StringUtils.isBlank(resKindCode)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量预开户时，调资源接口返回RES_KIND_CODE为空了，请核查！");
        	}
        }
        String res_kind_code = simCardInfo.getData(0).getString("RES_KIND_CODE", "");
        String res_kind_name = simCardInfo.getData(0).getString("RES_KIND_NAME", "");
        String sim_type_code = simCardInfo.getData(0).getString("SIM_TYPE_CODE", "");
        String strResTypeCode = simCardInfo.getData(0).getString("RES_TYPE_CODE", "0"); 
        String opc_value = simCardInfo.getData(0).getString("OPC", "");
        input.put("IMSI", imsi);
        input.put("KI", ki);
        input.put("RES_KIND_CODE", res_kind_code);
        input.put("RES_KIND_NAME", res_kind_name);
        input.put("SIM_TYPE_CODE", sim_type_code);
        input.put("RES_TYPE_CODE", strResTypeCode);
        input.put("OPC_VALUE", opc_value);
        input.put("CUST_NAME", input.getString("CUST_NAME", "无档户"));
        input.put("PAY_NAME", input.getString("CUST_NAME", "无档户"));
        input.put("PAY_MODE_CODE", input.getString("PAY_MODE_CODE", "0"));
        input.put("PSPT_TYPE_CODE", input.getString("PSPT_TYPE_CODE", "Z"));
        input.put("PSPT_ID", input.getString("PSPT_ID", "11111111111111111111"));
        input.put("PHONE", input.getString("PHONE", "10086"));
        input.put("POST_CODE", input.getString("POST_CODE", "000000"));
        input.put("CONTACT", input.getString("CUST_NAME", "无档户"));
        input.put("CONTACT_PHONE", input.getString("CONTACT_PHONE", "10086"));
        input.put("SUPER_BANK_CODE", input.getString("SUPER_BANK_CODE", ""));
        input.put("BANK_CODE", input.getString("BANK_CODE", ""));
        input.put("BANK_ACCT_NO", input.getString("BANK_ACCT_NO", ""));
        input.put("SIM_FEE_TAG", simCardInfo.getData(0).getString("FEE_TAG", ""));
        // input.put("ACCT_TAG", "0");
        // input.put("OPEN_MODE", "0");

        if ("500".equals(input.getString("TRADE_TYPE_CODE", "500")))
        {
            input.put("ACCT_TAG", input.getString("ACCT_TAG", "2"));
            String preOpenSelf = input.getString("PREOPENSELF", "");
            String openMode = input.getString("OPEN_MODE", "0");
            if("Y".equals(preOpenSelf)) {
                input.put("OPEN_MODE", openMode);
            } else {
                input.put("OPEN_MODE", "1");
            }
        }
        if (StringUtils.isNotEmpty(input.getString("BATCH_ID", "")) && "10".equals(input.getString("TRADE_TYPE_CODE", "500")))
        {
            input.put("OPEN_MODE", "5");
        }
        // 新批开
        if ("700".equals(input.getString("TRADE_TYPE_CODE")))
        {
            input.put("OPEN_MODE", "2");
            int advanceFee = Integer.parseInt(input.getString("ADVANCE_FEE"));
            if (advanceFee >= 50)
                input.put("AGENT_FEE", "1000");
            else
                input.put("AGENT_FEE", "0");
            input.put("ADVANCE_FEE", advanceFee * 100 + "");
            input.put("SIM_CARD_SALE_MONEY", simCardInfo.getData(0).getString("SALE_MONEY", "0"));// 默认为0
        }

        input.put("USER_TYPE_CODE", input.getString("USER_TYPE_CODE", "0"));
        input.put("REAL_NAME", "0");
        /*
         * IDataset tagInfos = TagInfoQry.getTagInfoBySubSys(CSBizBean.getVisit().getStaffEparchyCode(),
         * "CS_INF_DEFAULTPWD", "0", "CSM", null); if (IDataUtil.isNotEmpty(tagInfos)) { userPwd =
         * tagInfos.getData(0).getString("TAG_CHAR", ""); }
         */
        input.put("CARD_PASSWD", simCardInfo.getData(0).getString("PASSWORD", ""));// 密码密文 ceshi
        input.put("PASSCODE", simCardInfo.getData(0).getString("KIND", ""));// 密码加密因子 ceshi
        if (StringUtils.isNotBlank(simCardInfo.getData(0).getString("PASSWORD", "")) && StringUtils.isNotBlank(simCardInfo.getData(0).getString("KIND", "")))
            input.put("DEFAULT_PWD_FLAG", "1");

        input.put("USER_PASSWD", userPwd);
        
        IDataset uimInfo =ResCall.qrySimCardTypeByTypeCode(strResTypeCode);
        if("01".equals(uimInfo.getData(0).getString("NET_TYPE_CODE")))
        	input.put("FLAG_4G", "1");
        	
        /*
         * String netTypeCode =input.getString("NET_TYPE_CODE"); if(!"".equals(netTypeCode)){
         * if("07".equals(netTypeCode)){ input.put("M2M_FLAG", "1"); } }
         */
    }

}
