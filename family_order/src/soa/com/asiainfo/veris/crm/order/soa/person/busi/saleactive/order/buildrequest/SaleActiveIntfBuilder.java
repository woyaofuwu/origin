
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.buildrequest;

import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.TerminalBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TerminalOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.buildrequest.common.SaleActiveCommonBuilder;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SaleActiveIntfBuilder extends SaleActiveCommonBuilder implements IBuilder
{
	private static Logger logger = Logger.getLogger(SaleActiveIntfBuilder.class);
    private IData buildBusinessFee(IData input, String productId, String packageId, String elementId, String elementTypeCode, String payMode, String feeMode, String feeTypeCode, String fee, String rsrvStr1, String rsrvStr2, String rsrvStr3,
            String rsrvStr4, String eparchyCode) throws Exception
    {
        IData feeData = new DataMap();

        feeData.put("PRODUCT_ID", productId);
        feeData.put("PACKAGE_ID", packageId);
        feeData.put("PAY_MODE", payMode);
        feeData.put("FEE_MODE", feeMode);
        // 由于之前没有传该参数，信用购机活动受理需要传该参数。
        boolean isMoreTerminalSaleActive = isMoreTerminalSaleActive(productId, packageId, "240");
        if (isMoreTerminalSaleActive){
            feeData.put("FEE_TYPE_CODE", feeTypeCode);
        }
        feeData.put("OLDFEE", fee);
        feeData.put("FEE", fee);
        feeData.put("TRADE_TYPE_CODE", "240");
        feeData.put("ELEMENT_ID", elementId);
        feeData.put("ELEMENT_TYPE_CODE", elementTypeCode);
        feeData.put("RSRV_STR1", rsrvStr1);
        feeData.put("RSRV_STR2", rsrvStr2);
        feeData.put("RSRV_STR3", rsrvStr3);
        feeData.put("RSRV_STR4", rsrvStr4);

        if ("A".equals(elementTypeCode))
        {
            feeData.put("DISCNT_GIFT_ID", elementId);
        }

        if ("G".equals(elementTypeCode) && "0".equals(feeMode) && ("60".equals(feeTypeCode) || "61".equals(feeTypeCode)))
        {
            setTerminalOperFee(input, feeData, eparchyCode);
        }

        return feeData;
    }

    private boolean isMoreTerminalSaleActive(String productId, String packageId, String s) throws Exception{
        boolean flag = false;
        IDataset packageIdInfo = CommparaInfoQry.getCommparaByCode4("CSM","3119",productId,packageId,"0898");
        if (CollectionUtils.isNotEmpty(packageIdInfo)){
            String paraCode10 = packageIdInfo.getData(0).getString("PARA_CODE10","");
            String paraCode8 = packageIdInfo.getData(0).getString("PARA_CODE8","");

            if (!paraCode10.equals("") && "Y".equals(paraCode8) ){// 该活动属于有营业款的 信用购机活动，不需要返销支付中心
                flag = true;
            }else {// 无预存款的活动
                flag = false;
            }
        }
        return flag;
    }

    public IDataset buildSelectedElementsFee(IDataset selectedElements, IData input, String productId, String packageId, String eparchyCode) throws Exception
    {
    	
    	//标记是办理宽带业务时同时办理宽带
    	String wideUserCreateSaleActive = input.getString("WIDE_USER_CREATE_SALE_ACTIVE","");
    	
    	//宽带产品变更时包年活动需要补缴的费用
    	String wideActivePayFee = input.getString("WIDE_ACTIVE_PAY_FEE","");
    	
        if (!SaleActiveUtil.isComputeActiveFee(input.getString("CALL_TYPE")))
        {
            return null;
        }


        IDataset tradeFeeSubSet = new DatasetList();
        
        for (int index = 0, size = selectedElements.size(); index < size; index++)
        {
            
            IData selectedElement = selectedElements.getData(index);
            String elementTypeCode = selectedElement.getString("ELEMENT_TYPE_CODE");
            String elementId = selectedElement.getString("ELEMENT_ID");

            IDataset businessFee = ProductFeeInfoQry.getSaleActiveFee("240", BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, elementTypeCode, elementId, productId);
            
            if (IDataUtil.isEmpty(businessFee))
            {
                continue;
            }
            
            for (int j = 0, s = businessFee.size(); j < s; j++)
            {
                IData feeData = businessFee.getData(j);

                String payMode = feeData.getString("PAY_MODE");
                String feeMode = feeData.getString("FEE_MODE");
                String feeTypeCode = feeData.getString("FEE_TYPE_CODE");
                
                String fee = "";
                
                if("1".equals(wideUserCreateSaleActive))
                {
                    if (StringUtils.isNotEmpty(wideActivePayFee))
                    {
                        fee = wideActivePayFee;
                    }
                }
                
                if (StringUtils.isEmpty(fee))
                {
                    fee = feeData.getString("FEE");
                }
                
                String rsrvStr1 = feeData.getString("RSRV_STR1");
                String rsrvStr2 = feeData.getString("RSRV_STR2");
                String rsrvStr3 = feeData.getString("RSRV_STR3");
                String rsrvStr4 = feeData.getString("RSRV_STR4");
                
                IData businessFeeData = buildBusinessFee(input, productId, packageId, elementId, elementTypeCode, payMode, feeMode, feeTypeCode, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, eparchyCode);
                
                /**
                 * REQ201604180021 网上营业厅合约终端销售价格调整需求
                 * chenxy3 20160428 
                 */
                String fee_old=businessFeeData.getString("FEE"); 
                String orderId=input.getString("NET_ORDER_ID","");
                IDataset terminalOrderInfos = TerminalOrderInfoQry.qryTerminalOrderInfoForCheck(orderId,productId, packageId, input.getString("SERIAL_NUMBER"),"0","0");
                
                if(terminalOrderInfos!=null &&terminalOrderInfos.size()>0){
                	String orderPrice=terminalOrderInfos.getData(0).getString("ORDER_PRICE","");//订单总额
                	String rsrvNum2=terminalOrderInfos.getData(0).getString("RSRV_NUM2","");//购机款（不能为空）
                	String rsrvNum3=terminalOrderInfos.getData(0).getString("RSRV_NUM3","0");//预存款
                	if(Double.parseDouble(orderPrice)*100!=(Double.parseDouble(rsrvNum3)+Double.parseDouble(rsrvNum2))){
                		CSAppException.apperr(CrmCommException.CRM_COMM_103, "订单总额不等于购机款与预存款相加。订单总额："+Double.parseDouble(orderPrice)+"，相加总额："+(Double.parseDouble(rsrvNum3)+Double.parseDouble(rsrvNum2))*0.01+"。");
                	} 
                	/**根据手机串号调用华为接口获取型号信息*/
            		IData terminalParam = new DataMap();
            		String orderModelCode=terminalOrderInfos.getData(0).getString("DEVICE_MODEL_CODE","");
            	    terminalParam.put("RES_NO", input.getString("TERMINAL_ID"));
            	    terminalParam.put("PRODUCT_ID", productId);
//            	    IDataset labals=TerminalOrderInfoQry.qryLabelByProdId(terminalParam);
                    IDataset labals = UpcCall.qryCatalogByCatalogId(productId);

            	    String label="";
            	    if(labals!=null && labals.size()>0){
            	    	label=labals.getData(0).getString("UP_CATALOG_ID","");
            	    }
            	    terminalParam.put("CAMPN_TYPE", label);
            	    TerminalBean terminalBean = BeanManager.createBean(TerminalBean.class);
            	    IData terminalInfo = terminalBean.getTerminalByResNoOnly(terminalParam);
            	    String newModelCode=terminalInfo.getString("DEVICE_MODEL_CODE","");
            		if(!"".equals(newModelCode) && !newModelCode.equals(orderModelCode)){
                		CSAppException.apperr(CrmCommException.CRM_COMM_103, "预订的终端型号与输入的不符。预订型号："+orderModelCode+"，录入型号："+newModelCode+"。");
                	}
                	
                	if ("G".equals(elementTypeCode) && "0".equals(feeMode)){  
                     	if(!"".equals(rsrvNum2)){
                     		//当前办理活动的购机款小于rsrv_num2时，本次购机款以rsrv_num2为准
                     		//当前办理活动的购机款大于rsrv_num2，拦截办理
                     		if(Double.parseDouble(fee_old)<Double.parseDouble(rsrvNum2)){
                     			fee_old=rsrvNum2;
                     		}else if(Double.parseDouble(fee_old)>Double.parseDouble(rsrvNum2)){
                     			CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机款价格已发生变动，请重新办理。订单价："+Integer.parseInt(rsrvNum2)*0.01+"，终端现价："+Integer.parseInt(fee_old)*0.01+"。");
                     		}
                     	}
                     	
                    }
                	if ("A".equals(elementTypeCode)){  
                		if(!"".equals(rsrvNum3)){
                			fee_old=rsrvNum3;
                		}else{
                			if(!"".equals(rsrvNum2)){
                				fee_old="0";
                			}
                		} 
                    }
                	 
                }
                /******** chenxy3 ******/
                
                selectedElement.put("PAY_MODE", payMode);
                selectedElement.put("FEE_MODE", feeMode);
                selectedElement.put("FEE", fee_old);
                selectedElement.put("IN_DEPOSIT_CODE", feeData.getString("IN_DEPOSIT_CODE"));
                selectedElement.put("OUT_DEPOSIT_CODE", feeData.getString("OUT_DEPOSIT_CODE"));
                selectedElement.put("PAYMENT_ID", feeTypeCode);
    			businessFeeData.put("FEE", fee_old);
                if (!"0".equals(feeData.getString("PAY_MODE")))
                {
                    continue;
                }
                
                tradeFeeSubSet.add(businessFeeData);
            }

        }
        //关于增设商城后台库存管理的需求:非必须参数：用户下单价格（不传、则取TF_F_TERMINALORDER.ORDER_PRICE）若是系统调价了，用户下单的价格与办理时boss的价格不一致了，系统需判定拦截，并返回无法办理原因 20141120
//        if ("1".equals(input.getString("AUTO_FLAG"))){
//        	double orderPrice=input.getDouble("ORDER_PRICE", 0);
//        	double allFee = 0;
//			for (int index = 0; index < tradeFeeSubSet.size(); index++) {
//				allFee += Double.parseDouble(tradeFeeSubSet.getData(index).getString("FEE", "0"));
//			}
//			if(orderPrice*100!=allFee){		//以分进行比对
//				CSAppException.apperr(CrmCommException.CRM_COMM_103, "下单的价格与办理时boss的价格不一致");
//			}
//		}
        return tradeFeeSubSet;
    }

    private void checkGoodsTerminalId(IData param, String goodsId) throws Exception
    {
//        IDataset datas = SaleGoodsInfoQry.querySaleGoodsByGoodsId(goodsId);
//        IData goodsData = datas.getData(0);

        IData goodsData = UpcCall.qryOfferComChaTempChaByCond(goodsId, BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        
        String preType = param.getString("PRE_TYPE");
        if (BofConst.PRE_TYPE_CHECK.equals(preType))
        {
            return;
        }

        if ("1".equals(goodsData.getString("RES_TAG")) && "4".equals(goodsData.getString("GOODS_TYPE_CODE")))
        {
            String terminalId = param.getString("SALEGOODS_IMEI");
            if(StringUtils.isNotEmpty(terminalId)){
            	terminalId = terminalId.trim();
            }
            String actionType = param.getString("ACTION_TYPE");
            String deviceModeCode = param.getString("DEVICE_MODEL_CODE");
            if ("1".equals(actionType) && StringUtils.isBlank(deviceModeCode) && StringUtils.isBlank(terminalId))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_40);
            }
            else if (!"1".equals(actionType) && StringUtils.isBlank(terminalId))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_41);
            }
        }
    }

    /**
     * 海南营销活动，如果接口调用方传入了优惠，则以调用方传入的优惠为准，不以营销包下的默认必选优惠为准！
     * @param elements
     * @param discntCodes
     * @param packageId
     * @param eparchyCode
     * @throws Exception
     */
    private void dealDisnctElems4Inparam(IDataset elements, String discntCodes, String productId, String packageId, String eparchyCode) throws Exception
    {
        String[] disnctArr = discntCodes.split(",");
        IDataset matchElements = new DatasetList();
        
//      IDataset pkgDiscnts = DiscntInfoQry.queryDiscntsByPkgIdEparchy(packageId, eparchyCode);
        IDataset pkgDiscnts = UDiscntInfoQry.queryDiscntsByPkgIdEparchy(packageId, eparchyCode);
        
        if (IDataUtil.isNotEmpty(pkgDiscnts))
        {
            for (String discntCode : disnctArr)
            {
                for (int i = 0; i < pkgDiscnts.size(); i++)
                {
                    IData each = pkgDiscnts.getData(i);
                    if ("D".equals(each.getString("ELEMENT_TYPE_CODE")) && discntCode.equals(each.getString("ELEMENT_ID", "")))
                    {
                    	each.put("PRODUCT_ID", productId);
                    	each.put("PACKAGE_ID", packageId);
                    	each.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                        matchElements.add(each);
                    }
                }
            }
        }
        if (IDataUtil.isEmpty(matchElements))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_26);
        }

        IData pkgInfo = SaleActiveUtil.getPkgLimitInfo(packageId, "D");//UPackageInfoQry.getPackageLimitByPackageId(packageId);
        String limitType = pkgInfo.getString("LIMIT_TYPE");
        if ("D".equals(limitType))
        {
            int discntNumber = disnctArr.length;
            int minNumber = pkgInfo.getInt("MIN_NUMBER", -1);
            int maxNumber = pkgInfo.getInt("MAX_NUMBER", -1);

            discntNumber = matchElements.size();
            if (minNumber != -1 && discntNumber < minNumber)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_27, minNumber, discntNumber);
            }

            if (maxNumber != -1 && discntNumber > maxNumber)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_28, maxNumber, discntNumber);
            }
        }

        for (int i = 0; i < elements.size(); i++)
        {
            IData each = elements.getData(i);
            if ("D".equals(each.getString("ELEMENT_TYPE_CODE", "")))
            {
                elements.remove(each);
                i--;
            }
        }
        elements.addAll(matchElements);
    }

    private IDataset filterElement(UcaData uca, IDataset elements) throws Exception
    {
        IDataset matchElements = new DatasetList();
        IDataset depositDataset = new DatasetList();
        IDataset creditDataset = new DatasetList();

        for (int index = 0, size = elements.size(); index < size; index++)
        {
            IData element = elements.getData(index);
            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");

            if ("A".equals(elementTypeCode))
            {
                depositDataset.add(element);
            }

            if ("C".equals(elementTypeCode))
            {
                creditDataset.add(element);
            }
        }

        String userCreditClass =uca.getUserCreditClass();

        if ("-1".equals(userCreditClass))
        {
        	userCreditClass = "0";
        }

        if ((depositDataset != null && depositDataset.size() > 1) || (creditDataset != null && creditDataset.size() > 1))
        {
            for (int i = 0; i < elements.size(); i++)
            {
                IData element = elements.getData(i);
                String elementTypeCode = element.getString("ELEMENT_TYPE_CODE", "");
                if ((depositDataset != null && depositDataset.size() > 1) && "A".equals(elementTypeCode))
                {
                    String rsrvStr1 = element.getString("RSRV_STR1", "");
                    int length = "CREDIT_CLASS".length();
                	String configCreditClass = rsrvStr1.substring(length);
                    if (configCreditClass.indexOf(userCreditClass)>-1)
                    {
                        matchElements.add(element);
                    }
                }
                else if ((creditDataset != null && creditDataset.size() > 1) && "C".equals(elementTypeCode))
                {
                	String rsrvStr1 = element.getString("RSRV_STR1", "");
                    int length = "CREDIT_CLASS".length();
                	String configCreditClass = rsrvStr1.substring(length);
                    if (configCreditClass.indexOf(userCreditClass)>-1)
                    {
                        matchElements.add(element);
                    }
                }
                else
                {
                    matchElements.add(element);
                }
            }
        }
        else
        {
            matchElements = elements;
        }

        return matchElements;
    }

    protected IData getActiveDates(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveReqData saleActiveRequestData = (SaleActiveReqData) brd;

        String campnType = saleActiveRequestData.getCampnType();
        String productId = saleActiveRequestData.getProductId();
        String packageId = saleActiveRequestData.getPackageId();

        UcaData uca = saleActiveRequestData.getUca();
        String eparchyCode = uca.getUserEparchyCode();

//        IDataset pkgExtDataSet = PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);
        IData pkgExtData = UPackageExtInfoQry.qryPkgExtEnableByPackageId(packageId);
        
        pkgExtData.put("ACCT_DAY", uca.getAcctDay());
        pkgExtData.put("FIRST_DATE", uca.getFirstDate());
        pkgExtData.put("NEXT_ACCT_DAY", uca.getNextAcctDay());
        pkgExtData.put("NEXT_FIRST_DATE", uca.getNextFirstDate());
        pkgExtData.put("CAMPN_TYPE", campnType);
        pkgExtData.put("PRODUCT_ID", productId);
        pkgExtData.put("SERIAL_NUMBER", uca.getSerialNumber());
        pkgExtData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset saleActiveDateSet = CSAppCall.call("CS.SaleActiveDateSVC.callActiveStartEndDate", pkgExtData);
        //add begin on 20160618  增加预约时间的处理
        String userBookTime = param.getString("BOOKING_DATE");
        if(userBookTime != null && !"".equals(userBookTime))
        {
        	//根据传入的时间计算开始结束时间
    		String startDate = userBookTime;
        	
        	String endEnableTag = pkgExtData.getString("END_ENABLE_TAG");
        	String endAbsoluteDate = pkgExtData.getString("END_ABSOLUTE_DATE");
        	String endOffset = pkgExtData.getString("END_OFFSET");
        	String endUnit = pkgExtData.getString("END_UNIT");
        	//计算结束时间
        	String endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);
        	
        	saleActiveDateSet.getData(0).put("START_DATE", startDate);
        	saleActiveDateSet.getData(0).put("END_DATE", endDate);
        	saleActiveDateSet.getData(0).put("BOOK_DATE", userBookTime);
        }
        //add end on 20160618
        return saleActiveDateSet.getData(0);
    }

    public IDataset getPayMoneyList(IData input, IDataset tradeFeeSub)
    {
        if (IDataUtil.isEmpty(tradeFeeSub))
            return null;

        long allFee = 0;
        for (int index = 0, size = tradeFeeSub.size(); index < size; index++)
        {
            IData feeData = tradeFeeSub.getData(index);
            allFee += Long.parseLong(feeData.getString("FEE", "0"));
        }

        String payMoneyCode = input.getString("PAY_MONEY_CODE","0");
        String actionType = input.getString("ACTION_TYPE");
        if(input.getString("AUTO_FLAG","").equals("1")){		//电子上网处理
        	payMoneyCode = input.getString("PAY_MONEY_CODE", "0");
        }else{
        	if ("1".equals(actionType))
            {
                payMoneyCode = "8";
            }
            else if ("2".equals(actionType))
            {
                payMoneyCode = input.getString("PAY_MONEY_CODE", "0");
            }
            else if ("3".equals(actionType))
            {
                payMoneyCode = "3";
            }
        }
        

        IData payMoneyData = new DataMap();
        payMoneyData.put("MONEY", allFee);
        payMoneyData.put("PAY_MONEY_CODE", payMoneyCode);

        IDataset payMoneyDataset = new DatasetList();
        payMoneyDataset.add(payMoneyData);

        return payMoneyDataset;
    }

    protected IDataset getSelectedElems(IData param, BaseReqData brd, String bookDate) throws Exception
    {
        SaleActiveReqData saleActiveRequestData = (SaleActiveReqData) brd;

        String productId = param.getString("PRODUCT_ID");
        String packageId = param.getString("PACKAGE_ID");
        String discntCodes = param.getString("DISCNT_CODE");

        UcaData uca = saleActiveRequestData.getUca();
        IDataset selectedElementDataset = new DatasetList();
        //IDataset forceAndDefaultElements=PkgElemInfoQry.queryForceDefaultElem(productId, packageId);
        IDataset forceAndDefaultElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, "1", "1");
        
        //20141208 关于增设商城后台库存管理的需求:支持，用户自定义传的元素，即支持可传参数：优惠、组合包等 ,如果为网上业营业厅的渠道
        if (StringUtils.equals(param.getString("AUTO_FLAG",""), "1")
        		||(param.getString("WTSC_HDFK_SIGNUP","")!=null&&param.getString("WTSC_HDFK_SIGNUP","").equals("1"))){
        	
        	String payMode=param.getString("PAY_MONEY_CODE","");	//付款方式	
        	String netOrderId=param.getString("NET_ORDER_ID","");
        	String elementIds = param.getString("ELEMENT_IDS","");
        	
        	if(payMode.equals("L")){	//如果是货到付款，需要将办理的相关信息保存，在激活的时候再进行办理
        		String serialNumber = param.getString("SERIAL_NUMBER");
    			IDataset userSet=UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);	//查询用户信息
    			String userId=userSet.getData(0).getString("USER_ID");
    			String partitionId=userSet.getData(0).getString("PARTITION_ID");
    			
    			IDataset idSets=Dao.qryByCode("TF_B_WT_HDFK_ELEMENTS", "GET_HDFK_ELEMENTS_ID", null, Route.CONN_CRM_CG);
    			String wtHdFkId=idSets.getData(0).getString("HDFK_ELEMENTS_ID");
    			
    			IData insertParam=new DataMap();
    			insertParam.put("HDFK_ELEMENTS_ID", wtHdFkId);
    			insertParam.put("PARTITION_ID", partitionId);
    			insertParam.put("USER_ID", userId);
    			insertParam.put("ELEMENT_IDS", elementIds);
    			insertParam.put("CREATE_DATE", SysDateMgr.getSysDate());
    			insertParam.put("ORDER_ID", netOrderId);
    			Dao.insert("TF_B_WT_HDFK_ELEMENTS",insertParam,Route.CONN_CRM_CG);
    			
    			//将ID信息放到表TF_F_USER_SALEACTIVE_BOOK表的remark字段
        		if(elementIds!=null&&!elementIds.trim().equals("")){	//如果用户自定义了元素
        			saleActiveRequestData.setRemark("WTHDFK_YE_"+wtHdFkId);
        		}else{	//如果没有自定义元素
        			saleActiveRequestData.setRemark("WTHDFK_NO_"+wtHdFkId);
        		}
        	}
        	
        	IData paramElements=new DataMap();
    		paramElements.put("PRODUCT_ID", productId);
    		paramElements.put("PACKAGE_ID", packageId);
    		forceAndDefaultElements.clear();
        	if(StringUtils.isNotBlank(elementIds)){		//如果用户选择了元素,则选择产品包当中的必选元素
        	    //Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_ELEMENTS", paramElements, Route.CONN_CRM_CEN);
        		forceAndDefaultElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, "1", "NO_DEFAULT");
        		dealFreeChoiceElementsInparam(forceAndDefaultElements, elementIds, productId, packageId);
        	}else{	//用户没选择元素，选产品的必选和默认元素（这个海南的不知道是否有原有的queryForceDefaultElem中的类似，如果一样建议不需要这段）
        	    //Dao.qryByCode("TD_B_PACKAGE_ELEMENT", "SEL_FORCE_OR_DEFAULT_ELEMENTS", paramElements,Route.CONN_CRM_CEN);
        	    forceAndDefaultElements = UPackageElementInfoQry.queryForceDefaultElementByPackageId(packageId, "1", "1");
        	}
        	
        	saleActiveRequestData.setNetOrderId(netOrderId);
        }
        
        if (StringUtils.isNotBlank(discntCodes))
        {
            dealDisnctElems4Inparam(forceAndDefaultElements, discntCodes, productId, packageId, uca.getUserEparchyCode());
        }
        forceAndDefaultElements = filterElement(uca, forceAndDefaultElements);
        
        for (int index = 0, size = forceAndDefaultElements.size(); index < size; index++)
        {
            IData forceAndDefaultElement = forceAndDefaultElements.getData(index);
            
            forceAndDefaultElement.put("CAMPN_TYPE", saleActiveRequestData.getCampnType());
            forceAndDefaultElement.put("SERIAL_NUMBER", saleActiveRequestData.getUca().getSerialNumber());
            forceAndDefaultElement.put("PRODUCT_ID", saleActiveRequestData.getProductId());
            forceAndDefaultElement.put("BASIC_CAL_START_DATE", bookDate);
            forceAndDefaultElement.put("CUSTOM_ABSOLUTE_START_DATE", bookDate);
            forceAndDefaultElement.put(Route.ROUTE_EPARCHY_CODE, uca.getUserEparchyCode());
            
            IDataset dataset = CSAppCall.call("CS.SaleActiveElementDateSVC.callElementStartEndDate", forceAndDefaultElement);
            IData data = dataset.getData(0);
            
            //增加预约时间的处理 add begin on 20160618 
            String userBookTime = param.getString("BOOKING_DATE");
            //根据传入的时间计算开始结束时间,针对传入了预约的日期的情况，此处强制修改开始时间为预约日期
            if(userBookTime != null && !"".equals(userBookTime))
            {
        		String startDate = userBookTime;        		
            	String endEnableTag = forceAndDefaultElement.getString("END_ENABLE_TAG");
            	String endAbsoluteDate = forceAndDefaultElement.getString("END_ABSOLUTE_DATE");
            	String endOffset = forceAndDefaultElement.getString("END_OFFSET");
            	String endUnit = forceAndDefaultElement.getString("END_UNIT");
            	//计算结束时间
            	String endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);
            	
            	forceAndDefaultElement.put("START_DATE", startDate);
                forceAndDefaultElement.put("END_DATE", endDate);
            }
            else
            {
            	 forceAndDefaultElement.put("START_DATE", data.getString("START_DATE"));
                 forceAndDefaultElement.put("END_DATE", data.getString("END_DATE"));
            }
            //add end on 20160618 
            SaleActiveUtil.filterPackageElementPropertys(forceAndDefaultElement);
            
            if ("G".equals(forceAndDefaultElement.getString("ELEMENT_TYPE_CODE")))
            {
                checkGoodsTerminalId(param, forceAndDefaultElement.getString("ELEMENT_ID"));
            }

            selectedElementDataset.add(forceAndDefaultElement);
        }

        return selectedElementDataset;
    }

    protected void setCampnInfo(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveReqData saleActiveRequestData = (SaleActiveReqData) brd;
        saleActiveRequestData.setCampnType(param.getString("CAMPN_TYPE"));
        saleActiveRequestData.setCampnId("-1");
        saleActiveRequestData.setCampnCode("-1");
        
        if (SaleActiveConst.CALL_TYPE_NET_STORE.equals(param.getString("CALL_TYPE")))
        {
            saleActiveRequestData.setNetStoreActive(true);
            saleActiveRequestData.setDeviceModelCode(param.getString("DEVICE_MODEL_CODE"));
            saleActiveRequestData.setResTypeId(param.getString("RES_TYPE_ID"));
            saleActiveRequestData.setDeviceModel(param.getString("DEVICE_MODEL"));
            saleActiveRequestData.setProdOrderId(param.getString("PROD_ORDER_ID"));
        }
        
        if("3".equals(param.getString("ACTION_TYPE")))
        {
        	saleActiveRequestData.setCallType("4");
        }

        saleActiveRequestData.setChargeId(param.getString("PAY_CHARGE_ID"));

    }

    private void setTerminalOperFee(IData input, IData feeData, String eparchyCode) throws Exception
    {
        String deviceModeCode = input.getString("DEVICE_MODEL_CODE");
        String saleGoodsImei = input.getString("SALEGOODS_IMEI");
        if(StringUtils.isNotEmpty(saleGoodsImei)){
        	saleGoodsImei = saleGoodsImei.trim();
        }
        IData feeParam = new DataMap();
        feeParam.put("PRODUCT_ID", feeData.getString("PRODUCT_ID"));
        feeParam.put("PACKAGE_ID", feeData.getString("PACKAGE_ID"));
        feeParam.put("FEE", feeData.getString("FEE"));
        feeParam.put("RSRV_STR1", feeData.getString("RSRV_STR1"));
        feeParam.put("RSRV_STR2", feeData.getString("RSRV_STR2"));
        feeParam.put("RSRV_STR3", feeData.getString("RSRV_STR3"));
        feeParam.put("RSRV_STR4", feeData.getString("RSRV_STR4"));

        if (StringUtils.isNotBlank(deviceModeCode))
        {
            feeParam.put("DEVICE_MODEL_CODE", deviceModeCode);
            feeParam.put("RES_TYPE_ID", input.getString("RES_TYPE_ID"));
            feeParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset dataset = CSAppCall.call("CS.SaleActiveElementFeeSVC.getTerminalOperFeeByDeviceModelCode", feeParam);
            IData data = dataset.getData(0);
            feeData.put("FEE", data.getString("FEE"));
        }
        else if (StringUtils.isNotBlank(saleGoodsImei))
        {
            feeParam.put("RES_NO", saleGoodsImei);
            feeParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
            IDataset dataset = CSAppCall.call("CS.SaleActiveElementFeeSVC.getTerminalOperFeeByResNo", feeParam);
            IData data = dataset.getData(0);
            feeData.put("FEE", data.getString("FEE"));
        }
    }
    /**
     * 关于增设商城后台库存管理的需求:支持，用户自定义传的元素，即支持可传参数：优惠、组合包等 20141120
     */
    private void dealFreeChoiceElementsInparam(IDataset currentElements, String elementIds, String productId, String packageId) throws Exception {
        String[] elementIdArr = elementIds.split(",");
        if(elementIdArr!=null&&elementIdArr.length>0){
        	for (String elementId : elementIdArr) {
        		String elementIddb=elementId.substring(1);
        		String elementTypeCodedb=elementId.substring(0,1);
//    			IDataset freeChoiceElements = PkgElemInfoQry.queryFreeChoiceElem(productId, packageId, elementIddb,elementTypeCodedb);
                IDataset freeChoiceElements = UpcCall.qryOffersByCatalogIdAndOfferId(packageId, elementIddb, elementTypeCodedb);

    			if(IDataUtil.isEmpty(freeChoiceElements)){
    				CSAppException.apperr(CrmCommException.CRM_COMM_103, "所传可选元素不属于办理包的可选元素");
    			}
    			IData elementDb=freeChoiceElements.first();
    			boolean isContain=false;
    			
    			for(int i=0,size=currentElements.size();i<size;i++){
    				IData curElement=currentElements.getData(i);
    				String curElementType=curElement.getString("ELEMENT_TYPE_CODE");
    				String curElementId=curElement.getString("ELEMENT_ID");
    				
    				if(elementDb.getString("ELEMENT_TYPE_CODE").equals(curElementType)&&elementDb.getString("ELEMENT_ID").equals(curElementId)){
    					isContain=true;
    					break;
    				}
    			}
    			if(!isContain){
    				currentElements.add(elementDb);
    			}
    		}
        }
    }
}