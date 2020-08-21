 
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.label.LabelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleGoodsInfoQry;

public class SaleActiveBean extends CSBizBean
{
    public void bookTerminalActive(IData inparams) throws Exception
    {
        TerminalOrderInfoQry.insertTerminalOrderInfo(inparams);
    }

    private String buildRequestString(IData input)
    {
        IData acceptData = new DataMap();

        acceptData.putAll(input);
        acceptData.put("IS_CONFIRM", "true");
        acceptData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        acceptData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        acceptData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        acceptData.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        acceptData.put("IN_MODE_CODE", getVisit().getInModeCode());

        return acceptData.toString();
    }

    public void cancelBookTerminalActive(IData params) throws Exception
    {
        SaleActiveUtil.checkIntfInparam(params, "RELATION_TRADE_ID");
        SaleActiveUtil.checkIntfInparam(params, "SERIAL_NUMBER");

        String orderId = params.getString("RELATION_TRADE_ID");
        IDataset terminalInfos = TerminalOrderInfoQry.qryTerminalOrderInfoByOrderId(orderId, "0");
        if (IDataUtil.isEmpty(terminalInfos))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_20, orderId);
        }

        IData inparams = new DataMap();
        inparams.put("RSRV_STR2", "2");
        inparams.put("RELATION_TRADE_ID", orderId);
        Dao.executeUpdateByCodeCode("TF_F_TERMINALORDER", "UPDATE_BY_ORDER_ID", inparams);
    }

    public IDataset checkSaleBook(String serialNumber) throws Exception
    {
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber, false, false);

        IDataset hdfkActives = SaleActiveInfoQry.queryHdfkActivesByUserId(uca.getUserId());

        if (IDataUtil.isNotEmpty(hdfkActives))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户存在待签收的终端预售订单,业务办理不能继续！");
        }
        
//        IDataset hdfkActives2 = SaleActiveInfoQry.queryHdfkActivesByUserId2(uca.getUserId());
//
//        if (IDataUtil.isNotEmpty(hdfkActives2))
//        {
//        	for(int i=0;i<hdfkActives2.size();i++){
//        		IData bookActive = hdfkActives2.getData(i);
//        		String tradeId = bookActive.getString("ACCEPT_TRADE_ID");
//        		IDataset tradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
//        		if(IDataUtil.isNotEmpty(tradeInfos)){
//        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户存在预受理的营销活动，业务办理不能继续！");
//        		}
//        	}
//        }

        IData returnData = new DataMap();
        IDataset returnSet = new DatasetList();
        boolean isAgent = false;
//        IData departInfo = UDepartInfoQry.qryDepartByDepartId(getVisit().getDepartId());
//        if (IDataUtil.isNotEmpty(departInfo))
//        {
//            String departKindCode = departInfo.getString("DEPART_KIND_CODE", "");
//           if (!"100".equals(departKindCode) && !"500".equals(departKindCode))
//            {
//                isAgent = true;
//            }
//        }
        if (!isAgent && StringUtils.isNotBlank(serialNumber))
        {
            IDataset saleBooks = TerminalOrderInfoQry.qryTerminalOrderInfo(serialNumber);
            if (IDataUtil.isNotEmpty(saleBooks))
            {
                returnData.put("AUTH_BOOK_SALE", 1);
            }
            else
            {
                returnData.put("AUTH_BOOK_SALE", 0);
            }
        }
        else
        {
            returnData.put("AUTH_BOOK_SALE", 0);
        }
        returnSet.add(returnData);
        return returnSet;
    }

    public void checkSchoolActiveTrade(IData params) throws Exception
    {
        SaleActiveUtil.checkIntfInparam(params, "OPER_NUMB");
        SaleActiveUtil.checkIntfInparam(params, "ORDER_TIME");
        SaleActiveUtil.checkIntfInparam(params, "ORDER_CODE");
        SaleActiveUtil.checkIntfInparam(params, "BIZ_CATALOG");
        SaleActiveUtil.checkIntfInparam(params, "OPER_IDENTIFY");
        SaleActiveUtil.checkIntfInparam(params, "SERIAL_NUMBER");
    }

    public IData checkTerminalInfoByPk(IData params) throws Exception
    {
        SaleActiveUtil.checkIntfInparam(params, "ORDER_ID");
        SaleActiveUtil.checkIntfInparam(params, "ORDER_STATE");
        SaleActiveUtil.checkIntfInparam(params, "ORDER_TYPE");
        SaleActiveUtil.checkIntfInparam(params, "ORDER_PRICE");
        SaleActiveUtil.checkIntfInparam(params, "SERIAL_NUMBER");
        SaleActiveUtil.checkIntfInparam(params, "PRODUCT_ID");
        SaleActiveUtil.checkIntfInparam(params, "PACKAGE_ID");
        SaleActiveUtil.checkIntfInparam(params, "DEVICE_MODEL_CODE");
        SaleActiveUtil.checkIntfInparam(params, "START_TIME");
        SaleActiveUtil.checkIntfInparam(params, "END_TIME");
        SaleActiveUtil.checkIntfInparam(params, "REMARK");
        SaleActiveUtil.checkIntfInparam(params, "DEVICE_STAFF_ID");

        UcaDataFactory.getNormalUca(params.getString("SERIAL_NUMBER"));

        if (StringUtils.isNotBlank(params.getString("ID")))
        {
            IDataset terminalInfos = TerminalOrderInfoQry.qryTerminalOrderInfoById(params.getString("ID"));
            if (IDataUtil.isNotEmpty(terminalInfos))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_21);
            }
        }

        if (StringUtils.isNotBlank(params.getString("ORDER_ID")))
        {
            IDataset terminalInfos = TerminalOrderInfoQry.qryTerminalOrderInfoByOrderId(params.getString("ORDER_ID"), "0");
            if (IDataUtil.isNotEmpty(terminalInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "订单号重复！");
            }
        }

        String productId = params.getString("PRODUCT_ID", "");
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(productId);
        if (IDataUtil.isEmpty(productInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_22, productId);
        }

        String packageId = params.getString("PACKAGE_ID", "");
        IData packageInfo = UPackageInfoQry.getPackageByPK(packageId);
        if (IDataUtil.isEmpty(packageInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, packageId);
        }

        IData returnData = new DataMap();
        returnData.put("PRODUCT_NAME", productInfo.getString("PRODUCT_NAME"));
        returnData.put("PACKAGE_NAME", packageInfo.getString("PACKAGE_NAME"));
        return returnData;
    }

    public void endUser1593Actives(UcaData uca, String serialNumber, String productId, String packageId, String endDate, String eparchyCode) throws Exception
    {
        IDataset commparaDataset = CommparaInfoQry.getCommPkInfo("CSM", "1593", productId, eparchyCode);
        List<SaleActiveTradeData> userActiveList = uca.getUserSaleActives();

        String endAcctCycleOfThisMonth = SysDateMgr.getLastDateThisMonth();
        userActiveList = SaleActiveUtil.filterActiesByEendDate(userActiveList, endAcctCycleOfThisMonth);
        
        if (IDataUtil.isEmpty(commparaDataset) || CollectionUtils.isEmpty(userActiveList))
        {
            return;
        }
        
        userActiveList = SaleActiveUtil.sortUserSaleActivesByEndDateDesc(userActiveList);

        for (int index = 0, size = commparaDataset.size(); index < size; index++)
        {
            IData commparaData = commparaDataset.getData(index);

            String paraCode1 = commparaData.getString("PARA_CODE1");

            for (SaleActiveTradeData saleActiveTradeData : userActiveList)
            {
                String endPackageId = saleActiveTradeData.getPackageId();
                String endProductId = saleActiveTradeData.getProductId();

                if (!endProductId.equals(paraCode1) || ("60003192".equals(endPackageId) || "60003193".equals(endPackageId)))
                {
                    continue;
                }

                IData endActiveParam = new DataMap();

                endActiveParam.put("SERIAL_NUMBER", serialNumber);
                endActiveParam.put("PRODUCT_ID", endProductId);
                endActiveParam.put("PACKAGE_ID", endPackageId);
                endActiveParam.put("RELATION_TRADE_ID", saleActiveTradeData.getRelationTradeId());
                endActiveParam.put("REMARK", "STOPXHLB_" + commparaData.getString("PARAM_NAME"));
                endActiveParam.put("IS_RETURN", "0");
                endActiveParam.put("FORCE_END_DATE", endDate);

                CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
            }
        }
    }

    public void endUser3800Actives(UcaData uca, String serialNumber, String productId, String packageId, String endDate, String eparchyCode) throws Exception
    {
        IDataset commparaDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "3800", "SALELIMIT", packageId, productId, eparchyCode);
        List<SaleActiveTradeData> userActiveList = uca.getUserSaleActives();
        
        String endAcctCycleOfThisMonth = SysDateMgr.getLastDateThisMonth();
        userActiveList = SaleActiveUtil.filterActiesByEendDate(userActiveList, endAcctCycleOfThisMonth);

        if (IDataUtil.isEmpty(commparaDataset) || CollectionUtils.isEmpty(userActiveList))
        {
            return;
        }
        
        userActiveList = SaleActiveUtil.sortUserSaleActivesByEndDateDesc(userActiveList);

        for (int index = 0, size = commparaDataset.size(); index < size; index++)
        {
            IData commparaData = commparaDataset.getData(index);

            String paraCode3 = commparaData.getString("PARA_CODE3");
            String paraCode4 = commparaData.getString("PARA_CODE4");

            for (SaleActiveTradeData saleActiveTradeData : userActiveList)
            {
                String endPackageId = saleActiveTradeData.getPackageId();
                String endProductId = saleActiveTradeData.getProductId();

                if (!endPackageId.equals(paraCode3) || !endProductId.equals(paraCode4))
                {
                    continue;
                }

                IData endActiveParam = new DataMap();

                endActiveParam.put("SERIAL_NUMBER", serialNumber);
                endActiveParam.put("PRODUCT_ID", endProductId);
                endActiveParam.put("PACKAGE_ID", endPackageId);
                endActiveParam.put("RELATION_TRADE_ID", saleActiveTradeData.getRelationTradeId());
                endActiveParam.put("IS_RETURN", "0");
                endActiveParam.put("FORCE_END_DATE", endDate);

                CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
            }
        }
    }

    public String getCampnType(String productId) throws Exception
    {
        return LabelInfoQry.getLogicLabelIdByElementId(productId);
    }

    public void insertPreOrderData(IData input) throws Exception
    {
        IData preOrderData = new DataMap();
        String preId = input.getString("PRE_ID");
        if (StringUtils.isBlank(preId))
        {
            preId = SeqMgr.getOrderId();
        }
        preOrderData.put("PRE_ID", preId);
        preOrderData.put("PRE_TYPE", input.getString("PRE_TYPE"));
        preOrderData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(preId));
        preOrderData.put("ACCEPT_STATE", "0");
        preOrderData.put("REQUEST_ID", input.getString("OUT_REQUEST_ID", ""));
        preOrderData.put("START_DATE", SysDateMgr.getSysTime());
        preOrderData.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        preOrderData.put("TRADE_TYPE_CODE", DataBusManager.getDataBus().getOrderTypeCode());
        preOrderData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        preOrderData.put("REPLY_STATE", "0");
        preOrderData.put("ACCEPT_DATA1", buildRequestString(input));

        if (StringUtils.isBlank(input.getString("OUT_REQUEST_ID")))
        {
            preOrderData.put("REQUEST_ID", SeqMgr.getPreSmsSendId());
        }

        Dao.insert("TF_B_ORDER_PRE", preOrderData, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public void preTrade4SchoolActive(IData params) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", params.getString("TRADE_ID"));
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(params.getString("TRADE_ID")));
        inparam.put("OPER_NUMBER", params.getString("OPER_NUMB"));
        inparam.put("VERIFY_TIME", params.getString("VERIFY_TIME"));
        inparam.put("ORDER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        inparam.put("ORDER_CODE", params.getString("ORDER_CODE"));
        inparam.put("BIZ_CATALOG", params.getString("BIZ_CATALOG"));
        inparam.put("OPER_IDENTIFY", "");
        inparam.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
        inparam.put("PROMOTION_CODE", params.getString("PROMOTION_CODE"));
        inparam.put("START_DATE", params.getString("START_DATE"));
        inparam.put("END_DATE", params.getString("END_DATE"));
        Dao.executeUpdateByCodeCode("TF_B_TRADE_CUSTSALE_PROMOTION", "INS_ALL", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public IDataset querySaleBookList(String serialNumber) throws Exception
    {
        return TerminalOrderInfoQry.qryTerminalOrderInfo(serialNumber);
    }

    public void trade4SchoolActive(IData params) throws Exception
    {
        IData tradeInfo = SaleActiveInfoQry.querySchoolSalePreTradeInfo(params.getString("ORDER_CODE"));

        if (IDataUtil.isEmpty(tradeInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_36);
        }

        IData orderInfo = OrderPreInfoQry.queryOrderPreInfoByPreId(tradeInfo.getString("TRADE_ID"));

        if (IDataUtil.isEmpty(tradeInfo))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_36);
        }

        String operIdentify = params.getString("OPER_IDENTIFY");

        if ("0".equals(operIdentify)) // 撤销
        {
            orderInfo.put("END_DATE", SysDateMgr.getSysTime());
            orderInfo.put("REPLY_STATE", "1");
            orderInfo.put("ACCEPT_STATE", "1");
            Dao.save("TF_B_ORDER_PRE", orderInfo, Route.getJourDb(Route.CONN_CRM_CG));
        }
        else if ("1".equals(operIdentify)) // 受理
        {
            StringBuilder sb = new StringBuilder();
            sb.append(orderInfo.getString("ACCEPT_DATA1", "")).append(orderInfo.getString("ACCEPT_DATA2", ""));
            IData intfParam = new DataMap(sb.toString());
            intfParam.remove("PRE_TYPE");

            IDataset intfReturnDataset = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", intfParam);

            if (IDataUtil.isNotEmpty(intfReturnDataset))
            {
                String orderId = intfReturnDataset.getData(0).getString("ORDER_ID");
                orderInfo.put("ORDER_ID", orderId);
                orderInfo.put("END_DATE", SysDateMgr.getSysTime());
                orderInfo.put("REPLY_STATE", "1");
                orderInfo.put("ACCEPT_STATE", "9");

                Dao.executeUpdateByCodeCode("TF_B_ORDER_PRE", "UPD_BY_PREID", orderInfo, Route.getJourDb(Route.CONN_CRM_CG));
            }
        }

        IData updateParam = new DataMap();
        updateParam.put("ORDER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        updateParam.put("OPER_IDENTIFY", operIdentify);
        updateParam.put("ORDER_CODE", params.getString("ORDER_CODE"));
        Dao.executeUpdateByCodeCode("TF_B_TRADE_CUSTSALE_PROMOTION", "UPDATE_BY_ORDER", updateParam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }
    /*
    public IDataset queryAddrInfo(String serialNumber) throws Exception
    {
        return AddrInfoQry.queryAddrInfo(serialNumber);
    }
    public IDataset queryAddrInfoByUserId(String userId) throws Exception
    {
        return AddrInfoQry.queryAddrInfoByUserId(userId);
    }
    public IDataset queryTrackInfoByCond(IData input,Pagination page) throws Exception
    {
    	String state = input.getString("STATE");
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	String startDate = input.getString("START_DATE");
    	String endDate = input.getString("END_DATE");
    	String trackId = input.getString("TRACK_ID");
    	String cityCode = input.getString("AREA_CODE");
    	return AddrInfoQry.qryTrackInfo(serialNumber, trackId, state, startDate, endDate, cityCode, page);
    }*/
    
    public IDataset querySaleActiveByPid() throws Exception
    {
    	
    	//要求必须是指定的礼品才执行插表及发短信。
    	/**
    	 * REQ201603280028 关于新增集团机惠专享积分购机活动的需求（优化规则及新增参数）
    	 * chenxy3 2016-04-05
    	 * 根据配置来，不再写死。
    	 * */
		IDataset prods= CommparaInfoQry.getOnlyByAttr("CSM", "2403", "0898"); 
		IDataset rtnSet=new DatasetList();
		for(int k=0;k<prods.size();k++){
			String prodId=prods.getData(k).getString("PARAM_CODE","");
			IData Param = new DataMap();
			Param.put("PRODUCT_ID", prodId);
	    	IDataset results = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_PRODUCTID2", Param);
	    	rtnSet.addAll(results);
		}
    	
        return rtnSet ;
    }
    
    public IDataset queryConsumeByPid(IData input) throws Exception
    {
    	IData Param = new DataMap();
    	Param.put("USER_ID", input.getString("USER_ID"));
    	Param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
    	Param.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
    	IDataset results = Dao.qryByCode("TF_F_USER_SALEACTIVE_CONSUME", "SEL_BY_USER_PKID", Param);
        return results ;
    }
    
    public IDataset queryConsumeByCurMonths(IData input) throws Exception
    {
    	IData Param = new DataMap();
    	Param.put("USER_ID", input.getString("USER_ID"));
    	Param.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
    	Param.put("PACKAGE_ID", input.getString("PACKAGE_ID"));
    	IDataset results = Dao.qryByCode("TF_F_USER_SALEACTIVE_CONSUME", "SEL_BY_CURMON_USER_PKID", Param);
        return results ;
    }
    
	  /**
     * 信用购机贷款结果通知接口
     * 该接口用于同步用户贷款结果，全网信用购机平台请求到网状网，网状网收到请求后，网状网向BOSS通知贷款结果
     * @param data
     * @return
     * @throws Exception
     * 
     * TODO  接口还需完善
     */
	
	public IData dealWholeNetCreditPurchases(IData input) throws Exception {
		IDataUtil.chkParam(input, "REG_ORD_REQ"); 
		IDataset ordInfos=input.getDataset("REG_ORD_REQ");
		IData ordInfo = ordInfos.getData(0);
		  
        String seq = IDataUtil.chkParam(ordInfo, "SEQ"); 
        String cusMblNo = IDataUtil.chkParam(ordInfo, "CUS_MBL_NO"); 
        String mplOrdNo = IDataUtil.chkParam(ordInfo, "MPL_ORD_NO"); 
        String mplOrdDt = IDataUtil.chkParam(ordInfo, "MPL_ORD_DT"); 
        String acpSts = IDataUtil.chkParam(ordInfo, "ACP_STS"); 
        String acpStsDesc = IDataUtil.chkParam(ordInfo, "ACP_STS_DESC"); 
        String loanAmt = IDataUtil.chkParam(ordInfo, "LOAN_AMT"); 
    	IData returnData = new DataMap();
    	IDataset trades = TradeInfoQry.getMainTradeBySN(cusMblNo, "240");
    	IData otherTrade =  getCreditPurchasesOtherTrade(seq, trades);
    	if(DataUtils.isEmpty(otherTrade)){
    		returnData.put("X_RSPCODE", "2998"); 
    		returnData.put("RSP_CODE", "2998");
            returnData.put("RSP_INFO", "未找到对应的信用购机台账"); 
            return returnData;
    	}
    	String tradeId = otherTrade.getString("TRADE_ID");
  	  	IDataset tradeInfos = TradeInfoQry.getMainTradeByTradeId(tradeId);
        if(IDataUtil.isEmpty(tradeInfos))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_853);
        }
        String orderId = tradeInfos.getData(0).getString("ORDER_ID");
        String routeId =  tradeInfos.getData(0).getString("EPARCHY_CODE");
        String usersn =  tradeInfos.getData(0).getString("SERIAL_NUMBER");
        if(StringUtils.equals(acpSts, "00")){//贷款成功

              IDataset tradeList = TradeInfoQry.getMainTradeByOrderId(orderId, "0", routeId);
              
      		  //IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(tradeId, "CREDIT_PURCHASES");
              if(IDataUtil.isNotEmpty(tradeList)){
            	  IData param  = new DataMap();
            	  for(int i=0; i<tradeList.size(); i++){
            		  param.put("REMARK", acpStsDesc);
            		  param.put("RSRV_STR1", mplOrdNo);
            		  param.put("RSRV_STR2", mplOrdDt);
            		  param.put("RSRV_STR3", acpSts);
            		  param.put("RSRV_STR4", loanAmt);
            		  param.put("TRADE_ID", tradeId);
            		  param.put("RSRV_VALUE_CODE", "CREDIT_PURCHASES");
      				  Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_RSRV_STR_BY_TRADE_ID", param, Route.getJourDb(routeId));
          		      IData param1 = new DataMap();
          		      param1.put("EXEC_TIME", TimeUtil.getSysDate());
          		      param1.put("TRADE_ID", tradeId);
          		      Dao.executeUpdateByCodeCode("TF_B_TRADE", "UPD_EXEC_TIME_BY_TRADE_ID", param1, Route.getJourDb(routeId));
            	  }
              }
             returnData.put("RSP_INFO", "贷款成功，业务继续办理"); 
        }else{//贷款未成功
              IDataset tradeList = TradeInfoQry.getMainTradeByOrderId(orderId, "0", routeId);
              IData orderInfo=TradeInfoQry.getOrderByOrderId(orderId);
              if(IDataUtil.isNotEmpty(tradeList)){
            	 
            	  for(int i=0; i<tradeList.size(); i++){
            		  IData tradeInfo = tradeList.getData(i);
            		  //String tradeId = tradeList.getData(i).getString("TRADE_ID");
            		  IData pubData = this.getPublicData(tradeId, tradeInfo);// 操作员相信息
            		  createCancelTrade(tradeInfo,pubData);
            		  TradeInfoQry.moveBhTrade(tradeInfo);
            	  }
            	  if(IDataUtil.isNotEmpty(orderInfo)){
                 	 TradeInfoQry.moveBhOrder(orderInfo);
            	  }
              }
              
              releaseIMEI(tradeId,usersn);//释放终端
              
        	returnData.put("RSP_INFO", "贷款失败，业务取消"); 
        }
		returnData.put("RSP_CODE", "0000");
		
	    return returnData;
	    
	}
	
	private void releaseIMEI(String  saletradeId,String usersn) throws Exception {
		IDataset ds = TradeSaleGoodsInfoQry.getTradeSaleGoodsByTradeId(saletradeId);
		if(IDataUtil.isNotEmpty(ds))
		{
			IData data = ds.getData(0);
			data.put("RES_NO", data.getString("RES_CODE",""));
    		data.put("SERIAL_NUMBER", usersn);
    		//释放终端预占
    		IDataset retDataset =HwTerminalCall.releaseResTempOccupy(data);
    		
		}
	}

	private IData getCreditPurchasesOtherTrade(String seq, IDataset trades) throws Exception {
		String tradeId;
		for (Object object : trades) {
			IData trade = (IData) object;
			tradeId =  trade.getString("TRADE_ID");
			IDataset otherDatas = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(tradeId, "CREDIT_PURCHASES");
			if(DataUtils.isNotEmpty(otherDatas)){
				String otherSeq = otherDatas.first().getString("RSRV_STR9");
				if(StringUtils.equals(seq, otherSeq)){
					return trade;
				}
			}
			
		}
		return new DataMap();
	}
	
	  public IData createCancelTrade(IData tradeData, IData pubData) throws Exception
	    {
	        IData newTradeData = new DataMap();
	        newTradeData.putAll(tradeData);

	        /********* 费用 *******************************/

	        long lOperFee = -tradeData.getLong("OPER_FEE", 0);
	        long lAdvancePay = -tradeData.getLong("ADVANCE_PAY", 0);
	        long lforegift = -tradeData.getLong("FOREGIFT", 0);
	        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
	        newTradeData.put("CANCEL_TRADE_ID", tradeData.getString("TRADE_ID"));
	        
	        newTradeData.put("OPER_FEE", String.valueOf(lOperFee));
	        newTradeData.put("ADVANCE_PAY", String.valueOf(lAdvancePay));
	        newTradeData.put("FOREGIFT", String.valueOf(lforegift));
	        newTradeData.put("FEE_STATE", strFeeState);
	        newTradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
	        newTradeData.put("FEE_STAFF_ID", (strFeeState == "0") ? "" : pubData.getString("STAFF_ID"));

	        newTradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
	        newTradeData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
	      
	        newTradeData.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
	        newTradeData.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
	        newTradeData.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
	        newTradeData.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
	        newTradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
	        newTradeData.put("CANCEL_TAG", "3");// 2=返销 3=取消
	        newTradeData.put("CANCEL_DATE", tradeData.getString("ACCEPT_DATE"));
	        newTradeData.put("CANCEL_STAFF_ID", tradeData.getString("TRADE_STAFF_ID"));
	        newTradeData.put("CANCEL_DEPART_ID", tradeData.getString("TRADE_DEPART_ID"));
	        newTradeData.put("CANCEL_CITY_CODE", tradeData.getString("TRADE_CITY_CODE"));
	        newTradeData.put("CANCEL_EPARCHY_CODE", tradeData.getString("TRADE_EPARCHY_CODE"));
	        newTradeData.put("UPDATE_TIME", pubData.getString("SYS_TIME"));
	        newTradeData.put("UPDATE_STAFF_ID", pubData.getString("STAFF_ID"));
	        newTradeData.put("UPDATE_DEPART_ID", pubData.getString("DEPART_ID"));
	        newTradeData.put(Route.ROUTE_EPARCHY_CODE, pubData.getString("LOGIN_EPARCHY_CODE"));


	        if (!Dao.insert("TF_BH_TRADE", newTradeData, Route.getJourDb(BizRoute.getRouteId())))
	        {
	            CSAppException.apperr(TradeException.CRM_TRADE_304, pubData.getString("TRADE_ID"));
	        }

	        return newTradeData;
	    }
	
	/**
     * 获取一些公共信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    private IData getPublicData(String tradeId, IData tradeInfo) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("CANCEL_TAG", "3");//
        pubData.put("TRADE_ID", tradeId);
        pubData.put("USER_ID", tradeInfo.getString("USER_ID"));
        pubData.put("USER_EPARCHY_CODE", tradeInfo.getString("EPARCHY_CODE"));
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("LOGIN_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }
	/**
	 * @description 校验用户是否有办理
	 * @param  psptId
	 * @param  productId
	 * @param  packageId
	 * @return IData
	 * @author tanzheng
	 * @throws Exception 
	 * @date 2019年3月14日
	 */
	public IData checkPsptValideForActive(String psptId, String productId, String packageId) throws Exception {
		IData param = new DataMap();
    	param.put("PSPT_ID", psptId);
    	param.put("PRODUCT_ID", productId);
    	param.put("PACKAGE_ID", packageId);
    	IDataset results = Dao.qryByCode("TL_B_ACTIVE_PSPT_ID", "SEL_RECORD_PSPTID", param);
        if(IDataUtil.isNotEmpty(results)){
        	return results.first();
        }
    	return null ;
	}
    
	/**
     * REQ202005260002_关于开展5G招募活动的开发需求
     * 赠送积分、兑换话费
     * @param data
     * @return
     * @throws Exception
     */	
	public void giftActiveScore(IData input) throws Exception {
		IData dealParam = new DataMap(input.getString("DEAL_COND"));
        //查看是否存在对办理的该活动有积分的特殊处理配置
        String productId = dealParam.getString("PRODUCT_ID", "");
        String packageId = dealParam.getString("PACKAGE_ID", "");
        String userId = dealParam.getString("USER_ID", "");
        String serialNumber = dealParam.getString("SERIAL_NUMBER", "");
        String relaTradeId = dealParam.getString("RELATION_TRADE_ID", "");

		IDataset saleActiveConfigs = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "2611", productId, packageId);
		
		//如果存在特殊处理的配置
		if (IDataUtil.isNotEmpty(saleActiveConfigs) )
		{
			IData saleActiveConfig = saleActiveConfigs.getData(0);

			String scoreType = saleActiveConfig.getString("PARA_CODE7","");// 积分类型
			int addScore = Integer.parseInt(saleActiveConfig.getString("PARA_CODE9","0"));// 赠送积分，正值
			String scoreExchangeRuleId = saleActiveConfig.getString("PARA_CODE10","");// 积分兑换编码

			//调用账务接口，赠送积分
			AcctCall.userScoreModify(userId, "ALL", scoreType, "240", addScore, relaTradeId);

			
			//调用积分兑换接口
			IData input1 = new DataMap();
			input1.put("SERIAL_NUMBER", serialNumber);
			input1.put("RULE_ID", scoreExchangeRuleId);
			input1.put("COUNT", "1");
	        IDataset ids = CSAppCall.call("SS.ScoreExchangeRegSVC.infTradeReg", input1);
	        //更新活动的RSRV_NUM5记录是否已经赠送计费
	        updateSaleActiveRsrvNum5(userId,productId,packageId,relaTradeId,SysDateMgr.getCurMonth());
	        
		}
	    
	}
	
	/**
     * REQ202005260002_关于开展5G招募活动的开发需求
     * 更新活动标识是否本月已经赠送过积分了
     * @param data
     * @return
     * @throws Exception
     */
	private static int updateSaleActiveRsrvNum5(String userId,String productId,String packageId,String relaTradeId,String rsrvNum5) throws Exception
	{
		IData params = new DataMap();
		params.put("USER_ID", userId);
		params.put("PRODUCT_ID", productId);
		params.put("PACKAGE_ID", packageId);
		params.put("RELATION_TRADE_ID", relaTradeId);
		params.put("RSRV_NUM5", rsrvNum5);
		return Dao.executeUpdateByCodeCode("TF_F_USER_SALE_ACTIVE", "UPD_SALEACTIVE_RSRV_NUM5", params);
	}
}
