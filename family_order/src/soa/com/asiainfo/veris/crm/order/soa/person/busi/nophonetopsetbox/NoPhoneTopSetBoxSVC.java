
package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.TerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

/**
 * @ClassName: NoPhoneTopSetBoxSVC.java
 * @author: zhengkai5
 */
public class NoPhoneTopSetBoxSVC extends CSBizService
{
    /**
     * @Function: queryDiscntPackagesByPID()
     * @throws：异常描述
     * @version: v1.0.0
     * @author: zhengkai5
     * @date: 2017-9-11  Modification History: Date Author Version Description
     */
    private boolean check8M(String tradeId) throws Exception
    {
        // 1.取8M服务配置|2013|2022|2103|：PARA_CODE1
        IDataset wideLimitSet = CommparaInfoQry.getCommpara("CSM", "4910", "WIDELIMIT", CSBizBean.getTradeEparchyCode());
        // 2.服务台帐信息：SERVICE_ID
        IDataset tradeSvcSet = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        boolean is8M = false;
        for (Object obj : wideLimitSet)
        {
            IData wideLimit = (IData) obj;
            String limitValue = wideLimit.getString("PARA_CODE1");
            for (Object objSvc : tradeSvcSet)
            {
                IData svcData = (IData) objSvc;
                String svcId = svcData.getString("SERVICE_ID");
                if (StringUtils.indexOf(limitValue, svcId) >= 0)
                {
                    is8M = true;
                }
            }
        }
        return is8M;
    }

    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: zhengkai5
     * @date: 2017-9-11  Modification History: Date Author Version Description
     */
    public IData checkTerminal(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		}
		
        IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
        	
        	 String OLD_IMEI=input.getString("OLD_RES_ID");//老的串号
        	 IData data = new DataMap();
        	 data.put("NEW_IMEI", resNo);
             data.put("OLD_IMEI", OLD_IMEI);
             data.put("RES_TYPE_CODE", "4");
             data.put("RES_TRADE_CODE", "IAgentExchSale");
             data.put("CHECK_FLAG", "1");
             data.put("SERIAL_NUMBER", serialNumber);
             IDataset checkDataset = TerminalCall.callHwTerminal("ITF_MONNI", data);
         
             if (DataSetUtils.isNotBlank(checkDataset) && StringUtils.equals(checkDataset.first().getString("X_RESULTCODE"), "0"))
             {}else{
            	 String resultInfo = checkDataset.first().getString("X_RESULTINFO", "-华为接口调用异常-！");
                 CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
             }
        	/******************校验老的串号是否可以换机    结束******************************/
             
            IData res = retDataset.first();
            String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
            String supplyId = res.getString("SUPPLY_COOP_ID", "");
            retData.put("X_RESULTCODE", "0");
            retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
            retData.put("RES_ID", resNo); // 终端串号
            retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
            retData.put("RES_TYPE_CODE", "4"); // 终端类型编码：4
            retData.put("RES_BRAND_CODE", res.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
            retData.put("RES_BRAND_NAME", res.getString("DEVICE_BRAND")); // 终端品牌描述
            retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
            retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
            String resStateCode = res.getString("TERMINAL_STATE", ""); // 资源状态编码1 空闲 4 已销售
            retData.put("RES_STATE_CODE", resStateCode);
            retData.put("RES_STATE_NAME", "1".equals(resStateCode) ? "空闲" : "4".equals(resStateCode) ? "已销售" : "其他");
            retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
            retData.put("RES_SUPPLY_COOPID", supplyId); // 终端供货商编码
            
            retData.put("DEVICE_COST", res.getString("DEVICE_COST","0")); // 进货价格
            
            
            // 获取产品信息
            IDataset prodInfos = ProductInfoQry.querySTBProducts(resKindCode, supplyId);
            if (DataSetUtils.isBlank(prodInfos) && !StringUtils.equals(resKindCode, "N"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1185, resKindCode); // 该机型[%s]未绑定产品，请联系系统管理员!
            }
            retData.put("PRODUCT_INFO_SET", prodInfos);
        }
        else
        {
            String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
        return retData;
    }
        
    /**
     * @Function: checkModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @author: zhengkai5
     * @date: 2017-9-11  Modification History: Date Author Version Description
     */
    public IData checkModem(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
        	serialNumber = "KD_" + serialNumber;
		}
        IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
            IData res = retDataset.first();
            String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
            String supplyId = res.getString("SUPPLY_COOP_ID", "");
            retData.put("X_RESULTCODE", "0");
            retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
            retData.put("RES_ID", resNo); // 终端串号
            retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
            retData.put("RES_TYPE_CODE", "4"); // 终端类型编码：4
            retData.put("RES_BRAND_CODE", res.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
            retData.put("RES_BRAND_NAME", res.getString("DEVICE_BRAND")); // 终端品牌描述
            retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
            retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
            String resStateCode = res.getString("TERMINAL_STATE", ""); // 资源状态编码1 空闲 4 已销售
            retData.put("RES_STATE_CODE", resStateCode);
            retData.put("RES_STATE_NAME", "1".equals(resStateCode) ? "空闲" : "4".equals(resStateCode) ? "已销售" : "其他");
            retData.put("RES_FEE", Double.parseDouble(res.getString("RSRV_STR6", "0"))); // 设备费用  - feeMgr.js接收单位：分
            retData.put("RES_SUPPLY_COOPID", supplyId); // 终端供货商编码
            retData.put("DEVICE_COST", res.getString("DEVICE_COST", "")); //终端成本价
        }
        else
        {
            String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
        return retData;
    }
    
    public IData checkModem1(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		}
        System.out.println("TopSetBoxSVC.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx240 " + resNo + "  " + serialNumber);
        IDataset retDataset = HwTerminalCall.querySetTopBox1(serialNumber, resNo);
        System.out.println("TopSetBoxSVC.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx242 " + retDataset.first());
        if (DataSetUtils.isNotBlank(retDataset)) {
            String resultCode = retDataset.first().getString("X_RESULTCODE", "");            
            // 1:无在库信息，无销售信息      2:在库，无销售信息       3:数据齐全        4:数据有误
/*            if ("3".equals(resultCode)) {
                retData.put("X_RESULTCODE", "3");
            } else if ("1".equals(resultCode)||"2".equals(resultCode)) {
                retData.put("X_RESULTCODE", "1");
                //retData.put("X_RESULTINFO", "");
            }else if ("4".equals(resultCode)) {
                retData.put("X_RESULTCODE", "4");
                //retData.put("X_RESULTINFO", retDataset.first().getString("X_RESULTINFO", ""));
            }*/
            retData.put("X_RESULTCODE", resultCode);
        }
        return retData;
    }
    
    /*
     * 光猫归还前增加校验规则需求
     */
    public IData addModem(IData input) throws Exception
    {
        IData retData = new DataMap();
        //String resNo = input.getString("RES_NO");
        //String serialNumber = input.getString("SERIAL_NUMBER");
        System.out.println("TopSetBoxSVC.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx263 " + input + "  " );
        IDataset retDataset = HwTerminalCall.addModem(input);
        System.out.println("TopSetBoxSVC.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx265 " + retDataset.first());
        if (DataSetUtils.isNotBlank(retDataset)) {
            String resultCode = retDataset.first().getString("X_RESULTCODE", "");
            if ("0".equals(resultCode)) {
                retData.put("X_RESULTCODE", "0");
            } else if ("1".equals(resultCode)) {
                retData.put("X_RESULTCODE", "1");
                retData.put("X_RESULTINFO", retDataset.first().getString("X_RESULTINFO", ""));
            }
        }
        System.out.println("TopSetBoxSVC.javaxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx254 " + retData);
        return retData;
    }
    
    /**
     * @Function: updateModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void updateModem(IData input) throws Exception
    {
    	IData param = new DataMap();
    	String billId = input.getString("BILL_ID");
    	param.put("RES_NO", input.getString("RES_NO"));//串号
    	param.put("SALE_FEE", "0");//销售费用:不是销售传0
    	param.put("PARA_VALUE1", billId);//购机用户的手机号码
    	param.put("PARA_VALUE7", "0");//代办费
    	param.put("DEVICE_COST", input.getString("DEVICE_COST"));//进货价格--校验接口取
    	param.put("TRADE_ID ",  input.getString("TRADE_ID"));//台账流水 
    	param.put("X_CHOICE_TAG", "0");//0-终端销售,1—终端销售退货
    	param.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
    	param.put("CONTRACT_ID",  input.getString("TRADE_ID"));//销售订单号
    	param.put("PRODUCT_MODE", "0");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	param.put("PARA_VALUE11", sdf.format(new Timestamp(System.currentTimeMillis())));//销售时间
    	param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
    	param.put("PARA_VALUE14",  input.getString("DEVICE_COST"));//裸机价格  从检验接口取裸机价格
    	param.put("PARA_VALUE15", "0");//客户购机折让价格
    	param.put("PARA_VALUE16", "0");
    	param.put("PARA_VALUE17", "0");
    	param.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
    	param.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
    	param.put("PARA_VALUE1", billId);//客户号码
    	param.put("USER_NAME", input.getString("CUST_NAME"));//客户姓名
    	param.put("STAFF_ID", getVisit().getStaffId());//销售员工
    	param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

    	IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(param);
    	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
    		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
    		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    		}
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"华为接口调用异常！");
    	}
    }

    /**
     * @Function: checkUserInfo()
     * @Description: 校验用户信息（包括产品信息初始化、宽带信息）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-31 下午5:21:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-31 yxd v1.0.0 修改原因
     */
    public IData checkUserInfo(IData input) throws Exception
    {
        // "SERIAL_NUMBER\":\"KD_13687533981\ "AUTH_SERIAL_NUMBER":"13687533981" 业务入表统一用原手机号码（存在宽带未完工工单）
        // 整个不入产品台帐：产品信息查询直接从tf_f_user_res 获取
        // 1.机顶盒未完工工单查询
        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		}
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        String userIdB = userInfo.getString("USER_ID");
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A");
        
        String serialNumberA = userInfoA.getString("SERIAL_NUMBER_A");
        userInfo.put("SERIAL_NUMBER_A",serialNumberA);
                
        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3800", userIdA, "0");
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
        // 2.是否有购机信息
        IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userIdA, "4", "J");
        userInfo.put("USER_ACTION", boxInfos.isEmpty() ? "0" : "1"); // 0:购机 1：换机
        if (DataSetUtils.isNotBlank(boxInfos))
        {
            userInfo.putAll(boxInfos.first());
        }
        // 3.是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (DataSetUtils.isBlank(wideInfos))
        {
            // 3.1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            // 3.2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            // 3.3.校验是否为8M宽带用户
            if (DataSetUtils.isBlank(UserSvcInfoQry.checkUserWide(serialNumber)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1184); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            if (DataSetUtils.isNotBlank(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            }
            // 校验在途工单8M宽带
            if (!this.check8M(wideTD.getString("TRADE_ID")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1184); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
        }
        // 4.设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        // 5.购机信息处理
        if (StringUtils.equals("1", userInfo.getString("USER_ACTION")))
        {
            IData resInfo = new DataMap();
            String resKindCode = userInfo.getString("RES_CODE");
            resInfo.put("RES_ID", userInfo.getString("IMSI"));
            resInfo.put("OLD_RESNO", userInfo.getString("IMSI")); // 老终端号
            resInfo.put("RES_NO", userInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
            resInfo.put("RES_BRAND_NAME", userInfo.getString("RSRV_STR4").split(",")[0]);
            resInfo.put("RES_KIND_NAME", userInfo.getString("RSRV_STR4").split(",")[1]);
            resInfo.put("RES_STATE_NAME", "已销售");
            resInfo.put("RES_FEE", userInfo.getString("RSRV_NUM5"));
            resInfo.put("RES_SUPPLY_COOPID", userInfo.getString("KI"));
            resInfo.put("RES_TYPE_CODE", userInfo.getString("RES_TYPE_CODE"));
            resInfo.put("RES_KIND_CODE", resKindCode);
            resInfo.put("products", userInfo.getString("RSRV_STR1"));
            resInfo.put("basePackages", userInfo.getString("RSRV_STR2"));
            resInfo.put("optionPackages", userInfo.getString("RSRV_STR3"));
            userInfo.put("RES_INFO_MAP", resInfo);
            // 产品信息 RES_CODE:存资源类型（老系统这么整的，涉及数据倒换，没法改了）
            IDataset prodInfos = ProductInfoQry.querySTBProducts(resKindCode, userInfo.getString("KI"));
            if (DataSetUtils.isBlank(prodInfos) && !StringUtils.equals(resKindCode, "N"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1185, resKindCode); // 该机型[%s]未绑定产品，请联系系统管理员!
            }
            userInfo.put("PRODUCT_INFO_SET", prodInfos);
            userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
            userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
        }
        else
        {
            userInfo.put("SALE_ACTIVE", "0");
            if (this.saleActiveFee(userIdA))
            {
                userInfo.put("SALE_ACTIVE", "1");
            }
        }
        // PRODUCT_INFO_SET,RES_INFO_MAP
        return userInfo;
    }

    /**
     * @Function: queryDiscntPackagesByPID()
     * @Description: 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-4 下午7:58:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-4 yxd v1.0.0 修改原因
     */
    public IData queryDiscntPackagesByPID(IData input) throws Exception
    {
        IData retData = new DataMap();
        String productId = input.getString("PRODUCT_ID");
        
        IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(productId);
        
        // 基础服务包
        retData.put("B_P", topSetBoxPlatSvcPackages.getDataset("B_P"));
        
        // 可选服务包
        retData.put("O_P", topSetBoxPlatSvcPackages.getDataset("O_P")); 
        
        return retData;
    }

    /**
     * @Function: saleActiveFee()
     * @Description: 是否有营销活动
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-1 下午8:26:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-1 yxd v1.0.0 修改原因
     */
    private boolean saleActiveFee(String userId) throws Exception
    {
        IDataset saConfigs = CommparaInfoQry.querySaleActiveFeeConfig();
        if (DataSetUtils.isNotBlank(saConfigs))
        {
            for (Object obj : saConfigs)
            {
                IData config = (IData) obj;
                String productId = config.getString("PARA_CODE2");
                String pkgId = config.getString("PARA_CODE1");
                IDataset userSAInfos = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userId, productId, pkgId);
                if (DataSetUtils.isNotBlank(userSAInfos))
                {
                    return true;
                }
            }
            
            //核对营销活动预处理表，看是否存在预处理营销活动
            for (Object obj : saConfigs)
            {
                IData config = (IData) obj;
                String productId = config.getString("PARA_CODE2");
                String pkgId = config.getString("PARA_CODE1");
                IDataset userSAInfos = UserSaleActiveInfoQry.queryUserBookSaleActive(userId, productId, pkgId);
                if (DataSetUtils.isNotBlank(userSAInfos))
                {
                    return true;
                }
            }
            
        }
        return false;
    }
    
        /**
     * @Function: 铁通APP录入光猫
     * @Description: 
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: wuhao
     * @date: 2015-11-3
     */
    public IData writeModemCode(IData data) throws Exception
    {
    	IData retData = new DataMap();
    	retData.put("X_RESULTINFO", "OK");
    	retData.put("X_RESULTCODE", "0");
    	IData param = new DataMap();
    	IData checkModem = this.checkModem(data);
    	data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        data.put("RES_NO", data.getString("RES_ID"));
        data.put("TRADE_ID", data.getString("SUBSCRIBE_ID"));
        IData crmdata = this.updModermNumber(data);
        if(IDataUtil.isNotEmpty(crmdata) && "0".equals(crmdata.getString("X_RESULTCODE")))
        {
        	param.put("TRADE_ID", data.getString("TRADE_ID"));
        	param.put("RES_NO", data.getString("RES_ID"));//串号 
        	param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        	param.put("DEVICE_COST", checkModem.getString("DEVICE_COST"));
        	param.put("BILL_ID", data.getString("SERIAL_NUMBER"));
        	IData SN = new DataMap();
        	SN.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        	IData userInfo = this.getUserInfo(SN);
        	param.put("CUST_NAME", userInfo.getString("CUST_NAME"));
        	this.updateModem(param);
        }
        else
        {
        	String resultInfo = crmdata.getString("X_RESULTINFO", "crm调用异常！[光猫]");
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
        return retData;
    }
    
    public IData updModermNumber(IData userInfo) throws Exception
    {
    	NoPhoneTopSetBoxBean bean= BeanManager.createBean(NoPhoneTopSetBoxBean.class);
    	IData returnInfo = new DataMap();
    	IDataset userInfos=bean.getUserModermInfo(userInfo);
        if(userInfos!=null && userInfos.size()>0){ 
        	String moderm=userInfos.getData(0).getString("RSRV_STR1");
        	if(moderm!=null && !"".equals(moderm)){
        		returnInfo.put("X_RESULTCODE", "6131");
                returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】已存在光猫信息！");
        	}else{
        		bean.updModermNumber(userInfo);
            	returnInfo.put("X_RESULTCODE", "0");
                returnInfo.put("X_RESULTINFO", "办理成功!"); 
        	}
        }else{
        	returnInfo.put("X_RESULTCODE", "6130");
            returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】还没有办理光猫申领业务!"); 
        }    	
        return returnInfo;
    }
    
    public IData getUserInfo(IData userInfo) throws Exception
    {
    	NoPhoneTopSetBoxBean bean= BeanManager.createBean(NoPhoneTopSetBoxBean.class);
    	IData returnInfo = new DataMap();
    	IDataset userInfos = bean.getUserInfo(userInfo);
    	if(userInfos.isEmpty())
    	{
    		CSAppException.apperr(CustException.CRM_CUST_134, userInfo.getString("SERIAL_NUMBER"));
    	}
    	returnInfo.put("CUST_NAME", userInfos.get(0, "CUST_NAME"));
        return returnInfo;
    }
    
    /**
     * @Function: checkUserInfo()
     * @Description: 校验用户信息（包括产品信息初始化、宽带信息）
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-31 下午5:21:20 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-31 yxd v1.0.0 修改原因
     */
    public IData checkUserForOpenInternetTV(IData input) throws Exception
    {
        // "SERIAL_NUMBER\":\"KD_13687533981\ "AUTH_SERIAL_NUMBER":"13687533981" 业务入表统一用原手机号码（存在宽带未完工工单）
        // 整个不入产品台帐：产品信息查询直接从tf_f_user_res 获取
        // 1.机顶盒未完工工单查询
        String serialNumber = input.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		}
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        String userIdB = userInfo.getString("USER_ID");
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A");
        
        String serialNumberA = userInfoA.getString("SERIAL_NUMBER_A");
        userInfo.put("SERIAL_NUMBER_A",serialNumberA);
        
        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("3800", userIdA, "0");
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
        // 2.是否有购机信息
        IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userIdA, "4", "J");
        if (DataSetUtils.isNotBlank(boxInfos))
        {
        	CSAppException.apperr(TradeException.CRM_TRADE_333,"用户当前存在生效的魔百和业务，不能再办理。");
        }
        
        userInfo.put("USER_ACTION","0"); // 0:购机 1：换机
//        if (DataSetUtils.isNotBlank(boxInfos))
//        {
//            userInfo.putAll(boxInfos.first());
//        }
        // 3.是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (DataSetUtils.isBlank(wideInfos))
        {
            // 3.1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            // 3.2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2","")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            // 3.3.校验是否为8M宽带用户
            if (DataSetUtils.isBlank(UserSvcInfoQry.checkUserWide(serialNumber)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            if (DataSetUtils.isNotBlank(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            }
            // 校验在途工单8M宽带
            if (!this.check8M(wideTD.getString("TRADE_ID")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
        }
        // 4.设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        
      userInfo.put("SALE_ACTIVE", "0");
      if (this.saleActiveFee(userIdA))
      {
//          userInfo.put("SALE_ACTIVE", "1");
    	  userInfo.put("IS_HAS_SALE_ACTIVE", "1");
    	  
      }else{
    	  //获取费用信息
    	  String money="20000";
    	  IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
    	  if(IDataUtil.isNotEmpty(moneyDatas)){
    		 money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
    	  }
    	  
    	  int moneyInt=Integer.parseInt(money);
    	  
    	  /*
           * 查明用户现在的押金金额
           */
          IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
      	  String acctId=accts.getData(0).getString("ACCT_ID");
      	  IData foreGiftParam=new DataMap();
      	  foreGiftParam.put("X_PAY_ACCT_ID", acctId); 
      	  //4、调接口判断用户的现金是否足够，不够则提示缴费，不登记台账；调用接口
      	  IData checkCash= AcctCall.getZDepositBalance(foreGiftParam);
      	  long foregift=checkCash.getLong("CASH_BALANCE",0);
      	  
          if(foregift<moneyInt){
          	userInfo.put("SALE_ACTIVE", "0");
          	userInfo.put("SALE_ACTIVE_MONEY", (moneyInt-foregift)/100);
          }else{
          	userInfo.put("SALE_ACTIVE", "1");
          	userInfo.put("SALE_ACTIVE_MONEY", "0");
          }
    	  
    	  userInfo.put("IS_HAS_SALE_ACTIVE", "0");
    	  
      }
  	  
      	return userInfo;
    }
    
    
    
    /**
	 * 换机核对用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData checkUserValidForChangeTopsetbox(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		} 
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        
        IData userInfoUU = getRelaUUInfoByUserIdB(userInfo.getString("USER_ID"));
        
        String userIdA = userInfoUU.getString("USER_ID_A");
        String serialNumberA = userInfoUU.getString("SERIAL_NUMBER_A");
        userInfo.put("SERIAL_NUMBER_A", serialNumberA);
        
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userIdA, "4", "J");
		if(IDataUtil.isEmpty(boxInfos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和机顶盒换机！");
        }
        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4910", userIdA, "0");
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
        
        userInfo.put("USER_ACTION", "1"); // 0:购机 1：换机
        if (DataSetUtils.isNotBlank(boxInfos))
        {
            userInfo.putAll(boxInfos.first());
        }
        // 3.是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (DataSetUtils.isBlank(wideInfos))
        {
            // 3.1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            // 3.2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            // 3.3.校验是否为8M宽带用户
            if (DataSetUtils.isBlank(UserSvcInfoQry.checkUserWide(serialNumber)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            if (DataSetUtils.isNotBlank(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            }
            // 校验在途工单8M宽带
            if (!this.check8M(wideTD.getString("TRADE_ID")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
        }
        // 4.设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
        userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
        
        /*
         * 处理机顶盒信息
         */
        IData resInfo = new DataMap();
        
        IData boxInfo=boxInfos.first();
        String resKindCode = boxInfo.getString("RES_CODE");
        resInfo.put("OLD_RES_ID", boxInfo.getString("IMSI"));
        resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
        resInfo.put("OLD_RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
        resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
        resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
        resInfo.put("OLD_RES_STATE_NAME", "已销售");
        resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));
        resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI"));
        resInfo.put("OLD_RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
        resInfo.put("OLD_RES_KIND_CODE", resKindCode);
        
        String productId=boxInfo.getString("RSRV_STR1","");
        resInfo.put("products", productId);
        
        //查询产品的名称
        if(productId!=null&&!productId.trim().equals("")){
        	
        	String productName = UProductInfoQry.getProductNameByProductId(productId);
        	
        	if(StringUtils.isNotEmpty(productName)){
        		resInfo.put("OLD_PRODUCT_NAME", productName);
        	}else{
        		resInfo.put("OLD_PRODUCT_NAME", "");
        	}
        }else{
        	resInfo.put("OLD_PRODUCT_NAME", "");
        }
        
        //必选包
        String basePackageInfo=boxInfo.getString("RSRV_STR2");
        resInfo.put("basePackages", basePackageInfo);
        if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
        	String[] basePackages=basePackageInfo.split(",");
        	if(basePackages!=null&&basePackages.length>0){
        		String serviceId=basePackages[0];
        		
        		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("SERVICE_NAME",""));
        			}else{
        				resInfo.put("OLD_BASEPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_BASEPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_BASEPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_BASEPACKAGE_NAME", "");
        }
        
        //可选包
        String optionPackageInfo=boxInfo.getString("RSRV_STR3","");
        resInfo.put("optionPackages", optionPackageInfo);
        if(optionPackageInfo!=null&&!optionPackageInfo.trim().equals("")){
        	String[] optionPackages=optionPackageInfo.split(",");
        	if(optionPackages!=null&&optionPackages.length>0){
        		String serviceId=optionPackages[0];
        		
        		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", svcInfo.getString("SERVICE_NAME",""));
        			}else{
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        }
        
        resInfo.put("OLD_ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
        resInfo.put("OLD_REMARK", boxInfo.getString("REMARK",""));
        result.put("OLD_RES_INFO", resInfo);
        result.put("USER_INFO", userInfo);
        
		return result;
		
	}
	
	
	/**
	 * 魔百和产品变更核对用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData checkUserValidForChangeProduct(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		} 
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        String userId = userInfo.getString("USER_ID");
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userId, "4", "J");
		if(IDataUtil.isEmpty(boxInfos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和产品变更！");
        }
        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4910", userId, "0");
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
        
        userInfo.put("USER_ACTION", "1"); // 0:购机 1：换机
        if (DataSetUtils.isNotBlank(boxInfos))
        {
            userInfo.putAll(boxInfos.first());
        }
        // 3.是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (DataSetUtils.isBlank(wideInfos))
        {
            // 3.1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            // 3.2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1183); // 校园宽带不能办理互联网电视业务！
            }
            // 3.3.校验是否为8M宽带用户
            if (DataSetUtils.isBlank(UserSvcInfoQry.checkUserWide(serialNumber)))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            if (DataSetUtils.isNotBlank(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            }
            // 校验在途工单8M宽带
            if (!this.check8M(wideTD.getString("TRADE_ID")))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_3004); // 该用户不是宽带8M用户,不能办理互联网电视业务！
            }
        }
        // 4.设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
        userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
        
        /*
         * 处理机顶盒信息
         */
        IData resInfo = new DataMap();
        
        IData boxInfo=boxInfos.first();
        String resKindCode = boxInfo.getString("RES_CODE");
        resInfo.put("OLD_RES_ID", boxInfo.getString("IMSI"));
        resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
        resInfo.put("OLD_RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
        resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
        resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
        resInfo.put("OLD_RES_STATE_NAME", "已销售");
        resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));
        resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI"));
        resInfo.put("OLD_RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
        resInfo.put("OLD_RES_KIND_CODE", resKindCode);
        resInfo.put("TOPSETBOX_FOREGIFT", boxInfo.getString("RSRV_NUM2",""));
        
        String productId=boxInfo.getString("RSRV_STR1","");
        resInfo.put("products", productId);
        
        //查询产品的名称
        if(productId!=null&&!productId.trim().equals("")){
            String productName = UProductInfoQry.getProductNameByProductId(productId);
        	if(StringUtils.isNotEmpty(productName)){
        		resInfo.put("OLD_PRODUCT_NAME", productName);
        	}else{
        		resInfo.put("OLD_PRODUCT_NAME", "");
        	}
        }else{
        	resInfo.put("OLD_PRODUCT_NAME", "");
        }
        
        //必选包
        String basePackageInfo=boxInfo.getString("RSRV_STR2");
        resInfo.put("basePackages", basePackageInfo);
        if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
        	String[] basePackages=basePackageInfo.split(",");
        	if(basePackages!=null&&basePackages.length>0){
        		String serviceId=basePackages[0];
        		
        		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("SERVICE_NAME",""));
        			}else{
        				resInfo.put("OLD_BASEPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_BASEPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_BASEPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_BASEPACKAGE_NAME", "");
        }
        
        //可选包
        String optionPackageInfo=boxInfo.getString("RSRV_STR3","");
        resInfo.put("optionPackages", optionPackageInfo);
        if(optionPackageInfo!=null&&!optionPackageInfo.trim().equals("")){
        	String[] optionPackages=optionPackageInfo.split(",");
        	if(optionPackages!=null&&optionPackages.length>0){
        		String serviceId=optionPackages[0];
        		
        		if(serviceId!=null&&!serviceId.trim().equals("")&&!serviceId.trim().equals("-1")&&!serviceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(serviceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", svcInfo.getString("SERVICE_NAME",""));
        			}else{
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        }
        
        resInfo.put("OLD_ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
        resInfo.put("OLD_REMARK", boxInfo.getString("REMARK",""));
        result.put("OLD_RES_INFO", resInfo);
        result.put("USER_INFO", userInfo);
        
        
		return result;
		
	}
	
	
	
	/**
	 * 暂停魔百和业务，组装用户相关数据
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData checkUserValidForStopTopsetbox(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3))){
			serialNumber ="KD_"+ serialNumber;
		}
		
		//查询是否存在该用户
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        String userIdB = userInfo.getString("USER_ID");
       
        //通过UU关系表 获取 147号码 
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A");
        String serialNumberA = userInfoA.getString("SERIAL_NUMBER_A");
        userInfo.put("SERIAL_NUMBER_A", serialNumberA);
      
        //查询用户是否开通   无手机魔百合  业务（TF_F_USER_RES ）
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userIdA, "4", "J");
		if(IDataUtil.isEmpty(boxInfos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和业务！");
        }
		
		/*
		 * 验证魔百和  客户  是否已经被主动报停
		 */
		IData boxInfo=boxInfos.first();
		String stopSignal=boxInfo.getString("RSRV_TAG3","");
		if(stopSignal.equals("1")){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户的魔百和业务已经处理停机状态,无需重复操作！");
		}
		//查询该 客户  是否正在办理    无手机魔百合报停    业务（表 TF_B_TRADE）
        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4901", userIdA, "0");
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
        
        userInfo.put("USER_ACTION", "1"); // 0:购机 1：换机  ?
        if (DataSetUtils.isNotBlank(boxInfos))
        {
            userInfo.putAll(boxInfo);  //存储的用户表，用户业务资源表，以及user_actioin = 1
        }
        // 3.是否有宽带在途工单  
              // 查询用户  是否办理   宽带业务
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (DataSetUtils.isBlank(wideInfos))
        {
            // 3.1是否办理过宽带 （宽带用户） 
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            // 3.2校园宽带不能受理 
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            if(IDataUtil.isNotEmpty(wideNetInfo)){
            	wideState = "2"; // 2-正常
                userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE",""));
                userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
                userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
                userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
            }
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            if (DataSetUtils.isNotBlank(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            }
            
        }
        // 4.设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
        userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
        
        /*
         * 处理机顶盒信息
         */
        IData resInfo = new DataMap();
        
        
        String resKindCode = boxInfo.getString("RES_CODE");
        resInfo.put("OLD_RES_ID", boxInfo.getString("IMSI"));
        resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
        resInfo.put("OLD_RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
        resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
        resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
        resInfo.put("OLD_RES_STATE_NAME", "已销售");
        resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));
        resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI"));
        resInfo.put("OLD_RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
        resInfo.put("OLD_RES_KIND_CODE", resKindCode);
        
        String productId=boxInfo.getString("RSRV_STR1","");
        resInfo.put("products", productId);
        
        //查询产品的名称
        if(productId!=null&&!productId.trim().equals("")){
        	String productName = UProductInfoQry.getProductNameByProductId(productId);
        	if(StringUtils.isNotEmpty(productName)){
        		resInfo.put("OLD_PRODUCT_NAME", productName);
        	}else{
        		resInfo.put("OLD_PRODUCT_NAME", "");
        	}
        }else{
        	resInfo.put("OLD_PRODUCT_NAME", "");
        }
        
        //必选包
        String baseServiceId=null;
        String basePackageInfo=boxInfo.getString("RSRV_STR2");
        resInfo.put("basePackages", basePackageInfo);
        if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
        	String[] basePackages=basePackageInfo.split(",");
        	if(basePackages!=null&&basePackages.length>0){
        		baseServiceId=basePackages[0];
        		
        		if(baseServiceId!=null&&!baseServiceId.trim().equals("")&&!baseServiceId.trim().equals("-1")
        				&&!baseServiceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(baseServiceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
        			}else{
        				CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
        			}
        		}else{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
        		}
        	}else{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
        	}
        }else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
        }
        
        
        /*
         * 验证用户的必选服务是否正常
         */
        if(baseServiceId!=null&&!baseServiceId.equals("")){
        	IDataset userPlatSvcs=UserPlatSvcInfoQry.queryUserPlatSvcByState(userIdA, baseServiceId, "N");
        	if(IDataUtil.isNotEmpty(userPlatSvcs)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已经欠费停机，请先缴费开机以后再进行报停！");
        	}
        }
        
        //可选包
        String optionServiceId=null;
        String optionPackageInfo=boxInfo.getString("RSRV_STR3","");
        resInfo.put("optionPackages", optionPackageInfo);
        if(optionPackageInfo!=null&&!optionPackageInfo.trim().equals("")){
        	String[] optionPackages=optionPackageInfo.split(",");
        	if(optionPackages!=null&&optionPackages.length>0){
        		optionServiceId=optionPackages[0];
        		
        		if(optionServiceId!=null&&!optionServiceId.trim().equals("")&&!optionServiceId.trim().equals("-1")&&
        				!optionServiceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(optionServiceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
        			}else{
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        }
        
        resInfo.put("OLD_ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
        resInfo.put("OLD_REMARK", boxInfo.getString("REMARK",""));
        result.put("OLD_RES_INFO", resInfo);
        result.put("USER_INFO", userInfo);
		return result;
	}
	
	/**
	 * 开启无手机魔百和业务，组装用户相关数据
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData checkUserValidForStartTopsetbox(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
        {
			serialNumber="KD_" + serialNumber;
        }
		//根据手机号 查询用户 （ 实际要根据宽带用户查询   用户信息  ）   
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        String userIdB = userInfo.getString("USER_ID");
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A");
        
        String serialNumberA = userInfoA.getString("SERIAL_NUMBER_A");
        userInfo.put("SERIAL_NUMBER_A",serialNumberA);
        
        //查询用户资源表（TF_F_USER_RES）
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userIdA, "4", "J");
		
		//判断是否办理过魔百合业务
		if(IDataUtil.isEmpty(boxInfos)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和产品变更！");
        }
		
		IData boxInfo=boxInfos.first();
		String stopSignal=boxInfo.getString("RSRV_TAG3","");  //魔百合状态（TF_F_USER_RES中RSRV_TAG3）
		if(!stopSignal.equals("1")){
			if(stopSignal.equals("2"))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户的魔百和业务是欠费停机状态,请进行无手机魔百和续费操作！");
			}else {
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户的魔百和业务不是主动停机状态,不能进行报开操作！");
			}
		}
		
		//查询TF_B_TRADE表  判断该用户是否有  无手机魔百合  的   在途工单
        IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4902", userIdA, "0");
        if (IDataUtil.isNotEmpty(outDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
        }
        
        userInfo.put("USER_ACTION", "1"); // 0:购机 1：换机
        if (DataSetUtils.isNotBlank(boxInfos))
        {
            userInfo.putAll(boxInfo);
        }
        // 3.是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
        String wideState = "0"; // 0-系统异常
        if (DataSetUtils.isBlank(wideInfos))
        {
            // 3.1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            // 3.2校园宽带不能受理 （ 查询  TF_F_USER_WIDENET 表  ）
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
            if(IDataUtil.isNotEmpty(wideNetInfo)){
            	wideState = "2"; // 2-正常
                userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE",""));
                userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
                userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
                userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
            }
            
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            if (DataSetUtils.isNotBlank(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
            }
            
        }
        // 4.设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
        userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
        
        /*
         * 处理机顶盒信息
         */
        IData resInfo = new DataMap();
        
        
        String resKindCode = boxInfo.getString("RES_CODE");
        resInfo.put("OLD_RES_ID", boxInfo.getString("IMSI"));
        resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
        resInfo.put("OLD_RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
        resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
        resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
        resInfo.put("OLD_RES_STATE_NAME", "已销售");
        resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));
        resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI"));
        resInfo.put("OLD_RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
        resInfo.put("OLD_RES_KIND_CODE", resKindCode);
        
        String productId=boxInfo.getString("RSRV_STR1","");
        resInfo.put("products", productId);
        
        //查询产品的名称
        if(productId!=null&&!productId.trim().equals("")){
            String productName = UProductInfoQry.getProductNameByProductId(productId);
        	if(StringUtils.isNotEmpty(productName)){
        		resInfo.put("OLD_PRODUCT_NAME", productName);
        	}else{
        		resInfo.put("OLD_PRODUCT_NAME", "");
        	}
        }else{
        	resInfo.put("OLD_PRODUCT_NAME", "");
        }
        
        //必选包
        String baseServiceId=null;
        String basePackageInfo=boxInfo.getString("RSRV_STR2");
        resInfo.put("basePackages", basePackageInfo);
        if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
        	String[] basePackages=basePackageInfo.split(",");
        	if(basePackages!=null&&basePackages.length>0){
        		baseServiceId=basePackages[0];
        		
        		if(baseServiceId!=null&&!baseServiceId.trim().equals("")&&!baseServiceId.trim().equals("-1")
        				&&!baseServiceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(baseServiceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
        			}else{
        				resInfo.put("OLD_BASEPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_BASEPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_BASEPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_BASEPACKAGE_NAME", "");
        }
        
        
        
        //可选包
        String optionServiceId=null;
        String optionPackageInfo=boxInfo.getString("RSRV_STR3","");
        resInfo.put("optionPackages", optionPackageInfo);
        if(optionPackageInfo!=null&&!optionPackageInfo.trim().equals("")){
        	String[] optionPackages=optionPackageInfo.split(",");
        	if(optionPackages!=null&&optionPackages.length>0){
        		optionServiceId=optionPackages[0];
        		
        		if(optionServiceId!=null&&!optionServiceId.trim().equals("")&&!optionServiceId.trim().equals("-1")&&
        				!optionServiceId.trim().equals("null")){
        			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(optionServiceId);
        			
        			if(IDataUtil.isNotEmpty(svcInfo)){
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
        			}else{
        				resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        			}
        		}else{
        			resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        		}
        	}else{
        		resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        	}
        }else{
        	 resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
        }
        
        resInfo.put("OLD_ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
        resInfo.put("OLD_REMARK", boxInfo.getString("REMARK",""));
        result.put("OLD_RES_INFO", resInfo);
        result.put("USER_INFO", userInfo);
        
		return result;
		
	}
	 
	
	 /**
	 * 换机核对用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset queryTopsetboxProductInfo(IData param)throws Exception{
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3)))
		{
			serialNumber = "KD_" + serialNumber;
		} 
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        
        String userIdB = userInfo.getString("USER_ID");
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A");
        
        String serialNumberA = userInfoA.getString("SERIAL_NUMBER_A");
        userInfo.put("SERIAL_NUMBER_A",serialNumberA);
          
        
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1(userIdA, "4", "J");
		
		//判断是否办理过魔百合业务
		if(IDataUtil.isEmpty(boxInfos)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和产品变更！");
		}
		
		IData boxInfo=boxInfos.first();
		String resKindCode = boxInfo.getString("RES_CODE");
		
		// 产品信息 RES_CODE:存资源类型（老系统这么整的，涉及数据倒换，没法改了）
        IDataset prodInfos = ProductInfoQry.querySTBProducts(resKindCode, boxInfo.getString("KI"));
        if (DataSetUtils.isBlank(prodInfos) && !StringUtils.equals(resKindCode, "N"))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1185, resKindCode); // 该机型[%s]未绑定产品，请联系系统管理员!
        }
        
        /*
         * 限制产品变更只能对原有的产品进行变更
         */
        String productId=boxInfo.getString("RSRV_STR1","");
        IDataset productInfoFinal=new DatasetList();
        for(int i=0,size=prodInfos.size();i<size;i++){
        	IData prodInfo=prodInfos.getData(i);
        	
        	if(prodInfo.getString("PRODUCT_ID","").equals(productId)){
        		productInfoFinal.add(prodInfo);
        	}
        }
        
        return productInfoFinal;
	}
	
    
    
    public IData obtainTopsetboxForegift(IData param)throws Exception{
    	//获取费用信息
		String money="20000";
		IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
		if(IDataUtil.isNotEmpty(moneyDatas)){
			money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
		}
		int fee=Integer.parseInt(money);
		
		
		
		IData result=new DataMap();
		result.put("TOP_SETBOX_FEE", String.valueOf(fee/100));
		
		return result;
    }
   

    
   /**
    * 魔百和产品更换校验接口
    * <br/>
    * 提供给app使用
    * <br/>
    * REQ201608030015_手机APP承载光猫、魔百和管理界面的申领、更换功能
    * @author zhuoyingzhi
    * 20161021
    * @param param
    * @return
    * @throws Exception
    */
   public IData  checkUserValidForChangeProductToApp(IData param)throws Exception{
	   IData result=new DataMap();
	   
	   IData checkUserValidInfo=checkUserValidForChangeProduct(param);
	   if(IDataUtil.isNotEmpty(checkUserValidInfo)){
		   //
		   IData  oldResInfo=checkUserValidInfo.getData("OLD_RES_INFO");
		   result=oldResInfo;
		   //用户信息
		   IData  userInfo=checkUserValidInfo.getData("USER_INFO");
		   if(IDataUtil.isNotEmpty(userInfo)){
			   //宽带状态  2正常  1 未完工  其他值为异常
			   result.put("WIDE_STATE", userInfo.getString("WIDE_STATE"));
			   //WIDE_STATE_NAME  宽带状态
			   result.put("WIDE_STATE_NAME", userInfo.getString("WIDE_STATE_NAME"));
			   //WIDE_START_DATE 开始时间
			   result.put("WIDE_START_DATE", userInfo.getString("WIDE_START_DATE"));
			   //WIDE_END_DATE  结束时间
			   result.put("WIDE_END_DATE", userInfo.getString("WIDE_END_DATE"));
			   //WIDE_ADDRESS  宽带地址
			   result.put("WIDE_ADDRESS", userInfo.getString("WIDE_ADDRESS"));
		   }else{
			   //宽带状态  2正常  1 未完工  其他值为异常
			   result.put("WIDE_STATE", "");
			   //WIDE_STATE_NAME  宽带状态
			   result.put("WIDE_STATE_NAME", "");
			   //WIDE_START_DATE 开始时间
			   result.put("WIDE_START_DATE", "");
			   //WIDE_END_DATE  结束时间
			   result.put("WIDE_END_DATE", "");
			   //WIDE_ADDRESS  宽带地址
			   result.put("WIDE_ADDRESS", "");
		   }
	   }
	   return result;
   }
   
   /**
    * 魔百和终端编号校验接口
    * <br/>
    * 提供给app使用
    * <br/>
    * REQ201608030015_手机APP承载光猫、魔百和管理界面的申领、更换功能
    * @param input
    * @return
    * @throws Exception
    */
   public IData checkTerminalToApp(IData input) throws Exception{
	   IData result=new DataMap();
	   IData checkInfo=checkTerminal(input);
	   if(IDataUtil.isNotEmpty(checkInfo)){
	   	  result.putAll(checkInfo);
	      result.put("PRODUCT_INFO_SET", "");//判断是否办理过魔百合业务
	   }
	   return result;
   }
   
   //根据USER_ID 获取147手机号码
   public IData getRelaUUInfoByUserIdB(String userIdB) throws Exception
   {
       IDataset relaUUInfos = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userIdB,"47","1");
       if(IDataUtil.isEmpty(relaUUInfos))
       {
    	   CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通无手机魔百和业务！"); 
       }
       return relaUUInfos.first();
   }
   
   public IData getRelaUUInfoByUserIdA(String userIdA) throws Exception
   {
	   IDataset relaUUInfos = RelaUUInfoQry.getAllMebByUSERIDA(userIdA,"47");
       if(IDataUtil.isEmpty(relaUUInfos))
       {
    	   CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通无手机魔百和业务！"); 
       }
       return relaUUInfos.first();
   }
   
   //根据服务号码，获取147号码
   public IData getRelaUUInfoBySnB(IData data) throws Exception
   {
	   String serialNumberB = data.getString("SERIAL_NUMBER");
	   IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumberB);
	   if(IDataUtil.isNotEmpty(userInfos))
	   {
		   String userIdB = userInfos.first().getString("USER_ID");
		   IData relaUU = getRelaUUInfoByUserIdB(userIdB);
		   return relaUU;
	   }else{
		   CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有该用户信息！");
	   }
	   return null;
   }
   
   //根据 147号码，获取  宽带号码 
   public IData getRelaUUInfoBySnA(IData data) throws Exception 
   {
	   String serialNumberA = data.getString("SERIAL_NUMBER");
	   IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumberA);
	   if(IDataUtil.isNotEmpty(userInfos))
	   {
		   String userIdA = userInfos.first().getString("USER_ID");
		   IData relaUU = getRelaUUInfoByUserIdA(userIdA);
		   return relaUU;
	   }else{
		   CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有该用户信息！");
	   }
	   return null;
   }
   
   /**
    * 超过3年的用户的押金转化成话费
    * @param param
    * @return
    * @throws Exception
    */
   public IData transferCustForegift(IData param)throws Exception{
	   	//调用账务的押金转化成话费的接口来处理
	   	String serialNumber=param.getString("SERIAL_NUMBER");
	   	UcaData uca= UcaDataFactory.getNormalUca(serialNumber);
	   	
   		//获取费用信息
		String money="20000";
		IDataset moneyDatas=CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");
		if(IDataUtil.isNotEmpty(moneyDatas)){
			money=moneyDatas.getData(0).getString("PARA_CODE1","20000");
		}
		
		
		String dealCondStr=param.getString("DEAL_COND","");
		IData dealCond=new DataMap(dealCondStr);
		
		//调用账务的接口进行押金返回
		IData params=new DataMap(); 
		params.put("SERIAL_NUMBER_1", serialNumber);
		params.put("SERIAL_NUMBER_2", serialNumber);
		params.put("DEPOSIT_CODE_1", "9016");
		params.put("DEPOSIT_CODE_2", "0");
		params.put("FEE", money);
		params.put("REMARK", "魔百和退机押金转预存");
  		params.put("USER_ID_IN", uca.getUserId()); 
  		params.put("USER_ID_OUT", uca.getUserId()); 
  		
  		params.put("TRADE_EPARCHY_CODE", dealCond.getString("TRADE_EPARCHY_CODE","0898")); 
  		params.put("TRADE_CITY_CODE", dealCond.getString("TRADE_CITY_CODE",""));
  		params.put("TRADE_DEPART_ID", dealCond.getString("TRADE_DEPART_ID",""));
  		params.put("TRADE_STAFF_ID", dealCond.getString("TRADE_STAFF_ID",""));
  		
  		BizVisit visitor=CSBizBean.getVisit();
  		visitor.setStaffEparchyCode(dealCond.getString("TRADE_CITY_CODE",""));
  		
  		//调用接口，将【押金】——>【现金】
		IData callRtn=AcctCall.depositeToPhoneMoney(params);
		
		if(IDataUtil.isEmpty(callRtn)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
		}else{
			String resultCode=callRtn.getString("X_RESULTCODE","");
			if(!resultCode.equals("0")){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+callRtn.getString("X_RESULTINFO",""));
			}else{
				/*
				 * 更新是否返回标识
				 */
				UserResInfoQry.updateTopsetboxOnlineIsback(uca.getUserId(), "1");
			}
		}
		return callRtn;
   }
   
   /**
	 * 续费魔百和业务，组装用户相关数据
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IData checkUserValidForRenewTopsetbox(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		if(!"KD_".equals(serialNumber.substring(0, 3))){
			serialNumber ="KD_"+ serialNumber;
		}
			
		//查询是否存在该用户
		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
		}
		String userIdB = userInfo.getString("USER_ID");
		  
		//通过UU关系表 获取 147号码 
		IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
		String userIdA = userInfoA.getString("USER_ID_A");
		String serialNumberA = userInfoA.getString("SERIAL_NUMBER_A");
		userInfo.put("SERIAL_NUMBER_A", serialNumberA);
		 
       
		//查询用户是否开通   无手机魔百合  业务（TF_F_USER_RES ）
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userIdA, "4", "J");
		if(IDataUtil.isEmpty(boxInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和业务！");
		}
		
		IDataset UserOtherInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userIdB,"TOPSETBOX");
		if(IDataUtil.isEmpty(UserOtherInfos))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通魔百和，无法办理魔百和业务！");
		}
		IData userOther = UserOtherInfos.first();
		String topsetboxTime = userOther.getString("RSRV_STR28"); //受理时长
		
		IData boxInfo = boxInfos.getData(0);
		
		/*
		 * 验证魔百和  客户  是否已经被主动报停
		 */
		String stopSignal=boxInfo.getString("RSRV_TAG3","");
		String topsetboxState = "正常";
		String topsetboxStateTag = "0";
		if(stopSignal.equals("1")){
			//用户已停机
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户魔百和已主动停机！");
		}else if(stopSignal.equals("2"))
		{
			topsetboxState = "欠费停机";
			topsetboxStateTag = "2";
		}
		
		//查询该 客户  是否正在办理    无手机魔百合报停    业务（表 TF_B_TRADE）
       IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("4901", userIdA, "0");
       if (IDataUtil.isNotEmpty(outDataset))
       {
           CSAppException.apperr(TradeException.CRM_TRADE_93); // 有未完工工单，业务不能继续办理！
       }
       
       userInfo.put("USER_ACTION", "1"); // 0:购机 1：换机  ?
       if (DataSetUtils.isNotBlank(boxInfos))
       {
           userInfo.putAll(boxInfo);  //存储的用户表，用户业务资源表，以及user_actioin = 1
       }
       // 3.是否有宽带在途工单  
             // 查询用户  是否办理   宽带业务
       IDataset wideInfos = TradeInfoQry.queryExistWideTrade(serialNumber);
       String wideState = "0"; // 0-系统异常
       if (DataSetUtils.isBlank(wideInfos))
       {
           // 3.1是否办理过宽带 （宽带用户） 
           IData wUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
           if (IDataUtil.isEmpty(wUserInfo))
           {
               CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
           }
           // 3.2校园宽带不能受理 
           IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber).first();
           if(IDataUtil.isNotEmpty(wideNetInfo)){
        	   wideState = "2"; // 2-正常
               userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE",""));
               userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
               userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
               userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
           }
       }
       else
       {
           wideState = "1"; // 1-未完工
           IData wideTD = wideInfos.getData(0);
           userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
           userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
           userInfo.put("WIDE_START_DATE", "--");
           userInfo.put("WIDE_END_DATE", "--");
           IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
           if (DataSetUtils.isNotBlank(addrTD))
           {
               userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
           }
           
       }
       // 4.设置宽带状态
       userInfo.put("WIDE_STATE", wideState);
       userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
       userInfo.put("SALE_ACTIVE", "1"); // 换机不收费用
       userInfo.put("WIDE_ADDRESS", userInfo.getString("RSRV_STR5"));
       
       /*
        * 处理机顶盒信息
        */
       IData resInfo = new DataMap();
       
       String resKindCode = boxInfo.getString("RES_CODE");
       resInfo.put("OLD_RES_ID", boxInfo.getString("IMSI"));
       resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
       resInfo.put("OLD_RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
       resInfo.put("OLD_RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
       resInfo.put("OLD_RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
       resInfo.put("OLD_RES_STATE_NAME", "已销售");
       resInfo.put("OLD_RES_FEE", boxInfo.getString("RSRV_NUM5"));
       resInfo.put("OLD_RES_SUPPLY_COOPID", boxInfo.getString("KI"));
       resInfo.put("OLD_RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
       resInfo.put("OLD_RES_KIND_CODE", resKindCode);
       resInfo.put("OLD_START_DATE", boxInfo.getString("START_DATE"));
       
       String endDate = SysDateMgr.END_DATE;
       //获取偏移n个月后的最后一天
       if(StringUtils.isNotEmpty(topsetboxTime))
       {
    	   endDate = SysDateMgr.getAddMonthsLastDayNoEnv(Integer.parseInt(topsetboxTime),boxInfo.getString("START_DATE"));
       }
       resInfo.put("OLD_END_DATE", endDate);
       
       String productId=boxInfo.getString("RSRV_STR1","");
       resInfo.put("products", productId);
       
       //查询产品的名称
       if(productId!=null&&!productId.trim().equals(""))
       {
	       	String productName = UProductInfoQry.getProductNameByProductId(productId);
	       	if(StringUtils.isNotEmpty(productName)){
	       		resInfo.put("OLD_PRODUCT_NAME", productName);
	       	}else{
	       		resInfo.put("OLD_PRODUCT_NAME", "");
	       	}
       }else{
    	   resInfo.put("OLD_PRODUCT_NAME", "");
       }
       
       //必选包
       String baseServiceId=null;
       String basePackageInfo=boxInfo.getString("RSRV_STR2");
       resInfo.put("basePackages", basePackageInfo);
       if(basePackageInfo!=null&&!basePackageInfo.trim().equals("")){
	       String[] basePackages=basePackageInfo.split(",");
	       if(basePackages!=null&&basePackages.length>0){
	       		baseServiceId=basePackages[0];
	       		
	       		if(baseServiceId!=null&&!baseServiceId.trim().equals("")&&!baseServiceId.trim().equals("-1")
	       				&&!baseServiceId.trim().equals("null")){
	       			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(baseServiceId);
	       			
	       			if(IDataUtil.isNotEmpty(svcInfo)){
	       				resInfo.put("OLD_BASEPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
	       			}else{
	       				CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
	       			}
	       		}else{
	       			CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
	       		}
	       	}else{
	       		CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
	       	}
       	}else{
       		CSAppException.apperr(CrmCommException.CRM_COMM_103,"获取基础服务信息失败！");
       }
       
       //可选包
       String optionServiceId=null;
       String optionPackageInfo=boxInfo.getString("RSRV_STR3","");
       resInfo.put("optionPackages", optionPackageInfo);
       if(optionPackageInfo!=null&&!optionPackageInfo.trim().equals("")){
       	String[] optionPackages=optionPackageInfo.split(",");
       	if(optionPackages!=null&&optionPackages.length>0){
       		optionServiceId=optionPackages[0];
       		
       		if(optionServiceId!=null&&!optionServiceId.trim().equals("")&&!optionServiceId.trim().equals("-1")&&
       				!optionServiceId.trim().equals("null")){
       			IData svcInfo=PlatSvcInfoQry.queryPlatsvcByPk(optionServiceId);
       			
       			if(IDataUtil.isNotEmpty(svcInfo)){
       				resInfo.put("OLD_OPTIONPACKAGE_NAME", svcInfo.getString("OFFER_NAME",""));
       			}else{
       				resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
       			}
       		}else{
       			resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
       		}
       	}else{
       		resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
       	}
       }else{
       	 resInfo.put("OLD_OPTIONPACKAGE_NAME", "");
       }
       
       resInfo.put("OLD_ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
       resInfo.put("OLD_REMARK", boxInfo.getString("REMARK",""));
       resInfo.put("TOP_SET_BOX_STATE", topsetboxState);
       resInfo.put("TOP_SET_BOX_STATE_TAG", topsetboxStateTag);
       result.put("OLD_RES_INFO", resInfo);
       result.put("USER_INFO", userInfo);
       return result;
	}
	
	/**
     * 提交前费用校验
     * @param cycle
     * @throws Exception
     * @author zhengkai5
     */
    public IData checkFeeBeforeSubmit(IData param) throws Exception
    {
    	IData result = new DataMap();
    	String fee = param.getString("TOPSETBOX_FEE","0");
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	
        String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
        
        int allTotalTransFee = Integer.parseInt(fee)*100 ;
        
        if(Integer.parseInt(leftFee)< allTotalTransFee )
        {
            CSAppException.appError("61314", "您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[魔百和费用金额：" + Double.parseDouble(fee)+"元]");
        }
        
    	result.put("X_RESULTCODE", "0");
    	
    	return result;
    }
    
    /**
     * 根据无手机宽带号码，获取无手机魔百和信息
     * @throws Exception 
     * @author zhengkai5
     * */
    public IData queryTopSetBoxInfoByWsn(IData param) throws Exception
    {
    	String wsn = param.getString("SERIAL_NUMBER");
    	if(StringUtils.isNotEmpty(wsn))
    	{
    		String serialNumber ;
    		if(wsn.startsWith("KD_"))
    		{
    			serialNumber = wsn;
    		}
    		else
    		{
				serialNumber="KD_"+wsn;
			}
    		//检验宽带用户
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    		if(IDataUtil.isEmpty(userInfo))
    		{
    			return null;
    		}
    		String userIdB = userInfo.getString("USER_ID");
    		  
    		//通过UU关系表 获取 147号码 
    		IDataset userInfoA = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userIdB,"47","1");
    		if(IDataUtil.isEmpty(userInfoA))
    		{
    			return null;
    		}
    		String userIdA = userInfoA.first().getString("USER_ID_A");
    		String serialNumberA = userInfoA.first().getString("SERIAL_NUMBER_A");
    		userInfo.put("SERIAL_NUMBER_A", serialNumberA);
    		 
    		//查询用户是否开通   无手机魔百合  业务（TF_F_USER_RES ）
    		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userIdA, "4", "J");
    		if(IDataUtil.isEmpty(boxInfos))
    		{
    			return null;
    		}
    		IData topsetboxInfo = new DataMap();
    		topsetboxInfo.put("SERIAL_NUMBER",serialNumberA);     //147手机号码
    		topsetboxInfo.put("USER_ID",userIdA);				  //147user_id
    		topsetboxInfo.put("WIDE_SERIAL_NUMBER",serialNumber);  //宽带号码
    		topsetboxInfo.put("WIDE_USER_ID",userIdB);         //宽带userid
    		return topsetboxInfo;
    	}
    	return null;
    }
    
}
