
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotewritecard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.AssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncPresetData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.IssueData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.RoamEncPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.RoamEncPreDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service.WebServiceClient;
import com.asiainfo.veris.crm.order.soa.person.busi.countryreceipt.PrintDataSetBean;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.selfhelpcard.KIFunc;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

public class RemoteWriteCardBean extends CSBizBean
{
    static transient final Logger logger = Logger.getLogger(RemoteWriteCardBean.class);
    
	private boolean isTest = false;
    /**
     * 生成登记费用子表订单信息
     * 
     * @throws Exception
     */
    private void createFeeSubTrade(String tradeId, IData input) throws Exception
    {

        IDataset tradefeeSub = new DatasetList();
        IDataset tradeFeePaySub = new DatasetList();
        String fee = input.getString("FEE");

        if (StringUtils.isNotEmpty(fee))
        {
        	IData tradefee = new DataMap();
            // 金额为0 不拼串
            tradefee.put("FEE_MODE", "0");
            tradefee.put("FEE_TYPE_CODE", "10");
            tradefee.put("OLDFEE",input.getString("FEE",""));
            tradefee.put("FEE", input.getString("PAY",""));// 需要清查
            // tradefee.put("RSRV_STR1", data.getString("RSRV_STR1"));//天津存放销售模式的值

            tradefee.put("TRADE_ID", tradeId);
            tradefee.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
            tradefee.put("UPDATE_TIME", SysDateMgr.getSysTime());
            tradefee.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            tradefee.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            tradefee.put("REMARK", "");
            tradefee.put("USER_ID", input.getString("USER_ID"));
            tradefeeSub.add(tradefee);
            Dao.insert("TF_B_TRADEFEE_SUB", tradefeeSub, Route.getJourDbDefault());
        }
        // 金额为0 不拼串
        if (!"0".equals(input.getString("FEE","0")) )
        {
            IData tradeFeePay = new DataMap();

            tradeFeePay.put("PAY_MONEY_CODE", "0");
            tradeFeePay.put("MONEY", fee);

            tradeFeePay.put("TRADE_ID", tradeId);
            tradeFeePay.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
            tradeFeePay.put("UPDATE_TIME", SysDateMgr.getSysTime());
            tradeFeePay.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            tradeFeePay.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            tradeFeePay.put("REMARK", input.getString("REMARK", ""));
            tradeFeePay.put("ORDER_ID", "1");
            tradeFeePaySub.add(tradeFeePay);
            Dao.insert("TF_B_TRADEFEE_PAYMONEY", tradeFeePaySub);
        }      
    }

    /**
     * 生成登记费用子表订单信息
     * 
     * @throws Exception
     */
    private void createFeeTaxTrade(String tradeId, IData input) throws Exception
    {
        IData paramsAll = new DataMap();
        input.put("TRADE_ID", tradeId);
        IData temp_params = this.createTaxTrade(input);
        paramsAll.put("RATE", temp_params.getString("RATE", ""));
        paramsAll.put("FEE1", temp_params.getString("FEE1", ""));
        paramsAll.put("FEE2", temp_params.getString("FEE2", ""));
        paramsAll.put("FEE3", temp_params.getString("FEE3", ""));
        paramsAll.put("DISCOUNT", temp_params.getString("DISCOUNT", ""));
        paramsAll.put("SCORE_VALUE", temp_params.getString("SCORE_VALUE", ""));
        paramsAll.put("FEE_MODE", temp_params.getString("FEE_MODE", ""));
        paramsAll.put("FEE", temp_params.getString("FEE", ""));
        paramsAll.put("COUNT", temp_params.getString("COUNT", ""));
        paramsAll.put("UNIT", temp_params.getString("UNIT", ""));
        paramsAll.put("FEE_TYPE_CODE", temp_params.getString("FEE_TYPE_CODE", ""));
        paramsAll.put("FACT_PAY_FEE", temp_params.getString("FACT_PAY_FEE", ""));

        Dao.insert("TF_B_TRADEFEE_TAX", paramsAll);

    }

    private IData createTaxForSim(IData input) throws Exception
    {
        IData data = new DataMap();
        IDataset retDataset = new DatasetList();

        IDataset feelist = new DatasetList();
        IDataset tradefeeSub = new DatasetList();
        IData temp_retData = new DataMap();
        feelist = new DatasetList(input.getString("X_TRADE_FEESUB", "[]"));
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        String tradeEparchyCode = getTradeEparchyCode();
        String nowDate = SysDateMgr.getSysTime();

        IData userProductInfo = UcaInfoQry.qryUserMainProdInfoBySn(input.getString("SERIAL_NUMBER"));
        String productId = userProductInfo.getString("PRODUCT_ID");
        String packageId = "-1";
        String elementId = "";
        String goods_name = "";
        String x_check_tag = "";
        String sale_type = "";
        for (int i = 0; i < feelist.size(); i++)
        {
            IData temp = new DataMap();

            IData fee = (IData) feelist.get(i);
            data.put("TRADE_TYPE_CODE", tradeTypeCode);
            data.put("FEE_TYPE_CODE", fee.getString("FEE_TYPE_CODE"));
            data.put("FEE_MODE", fee.getString("FEE_MODE"));
            String sale_fee = "";
            String res_type = "";
            sale_fee = fee.getString("FEE");
            if (fee.getString("FEE_MODE").equals("2") || ("0".equals(fee.getString("FEE_MODE")) && "0".equals(fee.getString("FEE"))))
            {
                // 天津暂时不处理预存
                continue;
            }
            IData goodfeeData = new DataMap();
            IData retData = new DataMap();
            // IDataset tradeDatas = (IDataset) td.getTradeDatas();
            // IData tradeData = tradeDatas.getData(0);
            if (tradeTypeCode.equals("416"))
            {// 有价卡销售
                productId = "-1";
                x_check_tag = "1";// 有价卡

                res_type = input.getString("RES_TYPE");

            }
            else if (tradeTypeCode.equals("10") || tradeTypeCode.equals("141") || tradeTypeCode.equals("142") || tradeTypeCode.equals("310"))
            {
                x_check_tag = "0";// SIM卡
                productId = "-1";
                res_type = input.getString("SIM_TYPE_CODE");
            }

            data.put("PRODUCT_ID", productId);
            data.put("PACKAGE_ID", packageId);
            data.put("ELEMENT_ID", elementId);
            data.put("NOW_DATE", nowDate);
            data.put("EPARCHY_CODE", tradeEparchyCode);
            IDataset taxInfo = FeeItemTaxInfoQry.qryTaxByTypeCode(tradeTypeCode, fee.getString("FEE_TYPE_CODE"), fee.getString("FEE_MODE"), productId, packageId, elementId, nowDate, tradeEparchyCode);

            if (taxInfo.size() == 0 || taxInfo == null || taxInfo.size() > 1)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "税率配置错误，请检查!");

            }
            else
            {
                IData taxData = taxInfo.getData(0);

                retData.put("TRADE_ID", input.getString("TRADE_ID"));// 业务流水号
                retData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(input.getString("TRADE_ID")));// 受理月份：受理时间的月份，可作为分区字段。
                retData.put("USER_ID", input.getString("USER_ID"));// 用户标识
                retData.put("FEE_MODE", fee.getString("FEE_MODE", ""));
                retData.put("FACT_PAY_FEE", fee.getString("FACT_PAY_FEE"));// 实收价格
                retData.put("FEE", sale_fee);// 销售价格
                retData.put("DISCOUNT", Float.valueOf(sale_fee) - Float.valueOf(fee.getString("FACT_PAY_FEE")));
                retData.put("FEE_TYPE_CODE", fee.getString("FEE_TYPE_CODE", ""));// 费用类型编码：可以为营业费用项、押金或存折类型的编码
                sale_type = taxData.getString("TYPE", "");
                retData.put("TYPE", taxData.getString("TYPE", ""));
                String taxRate = taxData.getString("RATE", "");
                if (taxData.getString("RES_TAG", "0").equals("1"))
                {// TODO 需要去资源去取税率 hefeng
                    data.put("X_CHECK_TAG", x_check_tag);
                    data.put("RES_TYPE", res_type);
                    IData resInfo = new DataMap();
                    // getGoodsRate(pd,data);
                    taxRate = resInfo.getString("RSRV_STR1", "");
                }
                retData.put("RATE", taxRate);
                retData.put("UPDATE_TIME", nowDate);// 更新时间
                retData.put("UPDATE_STAFF_ID", getVisit().getStaffId());// 更新员工
                retData.put("UPDATE_DEPART_ID", getVisit().getDepartId());// 更新部门
                retData.put("GOODS_NAME", goods_name);
                String saleModeValue = "";
                if ("s".equals(saleModeValue))
                {
                    retData.put("FACT_PAY_FEE", fee.getString("FEE"));
                    // paramsAll.put("FEE", "0");
                    retData.put("FEE", fee.getString("FACT_PAY_FEE"));// 实缴费用
                }
                else
                {
                    retData.put("FACT_PAY_FEE", fee.getString("FEE"));
                    retData.put("FEE", fee.getString("FEE"));
                }
            }
            if (!"3".equals(sale_type) && !"0".equals(fee.getString("FACT_PAY_FEE")))
            { // 销售方式为折扣折让 并且 实收费用为0的过滤该税率信息
                temp_retData = retData;
            }
        }
        return temp_retData;
    }

    /**
     * 拼个人记税台账
     */
    private IData createTaxTrade(IData input) throws Exception
    {
        IData taxInfo = createTaxForSim(input);
        IDataset temp_taxinfo = new DatasetList();
        IData new_taxinfo = new DataMap();
        temp_taxinfo.add(taxInfo);
        TaxCalcUtils.getTradeFeeTaxForCalculate(temp_taxinfo);
        if (temp_taxinfo != null && temp_taxinfo.size() > 0)
        {
            new_taxinfo = temp_taxinfo.getData(0);
        }
        else
        {
            return null;
        }

        return new_taxinfo;
    }

    /**
     * 财务化BOSS处理方法
     */
    public void financialHandle(IData input) throws Exception
    {
    	/**插入财务接口日志表 TF_B_TRADE_BFAS_IN **/
		insertTradeBfasIn(input);
		/**插入财务接口日志表 TF_A_BFAS_IN **/
		input.put("SUB_LOG_ID", input.getString("TRADE_ID"));
		input.put("CANCEL_TAG", "0");//返销标记 0 正业务  1返销业务
		insertBfasIn(input);	
    }

    private void insertBfasIn(IData input) throws Exception {
    	 String sysDate=SysDateMgr.getSysTime();
    	 String paymoneyCode="0"; //收款方式 TF_B_TRADEFEE_PAYMONEY表获取pay_money_code默认现金0
    	 String checkCode="";       //支票/转账号
    	 String emptyCardId = input.getString("EMPTY_CARD_ID");
    	 String sn  = input.getString("IDVALUE");
    	 IData accdataparam = new DataMap();
    		 
    	 String resTypeCode="1"; //资源大类编码1 sim卡  6 白卡
    	 String simTypeCode= "";//资源小类编码
    	 String stockIdO= "";
    	 String  resState = "";
    	 String capacityTypeCode="";//面值/容量
    	 if("".equals(emptyCardId)){
             CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地写卡 卡号不能为空！");
         }
    	 /******************add yangyz3 2013530 end**************************/
    	 if(!"".equals(emptyCardId)){//白卡
//    		 resTypeCode="6";
    		 IDataset emptycarddata = ResCall.getEmptycardInfo(emptyCardId, sn,""); //dao.queryByPK("TF_R_EMPTYCARD", emptycardparam);
    		 resTypeCode=emptycarddata.getData(0).getString("RES_TYPE_CODE","");
    		 simTypeCode=emptycarddata.getData(0).getString("RES_KIND_CODE","");
    		 capacityTypeCode=emptycarddata.getData(0).getString("CAPACITY_TYPE_CODE","");//容量
    		 stockIdO=emptycarddata.getData(0).getString("IN_STAFF_ID","");//原库存编码应记录卡的实际归属员工号
    		 resState= emptycarddata.getData(0).getString("RES_STATE");//资源状态编码
    	 }	 
    		 String present_fee=""; //促销
    		 String formFee=""; //代收手续费/佣金
    		 
    		 String feeMode="10";//费用类型
    		 String feeItemTypeCode="10";//费用明细类型 (TF_B_TRADEFEE_SUB)
    		    String feeTypeCode="10";  //费用类型
    		    String oldFee=input.getString("FEE","0");
    		    String fee=input.getString("PAY","0");//实收金额
    		    String saleTypeCode="1" ; //销售类型，默认为正常销售 1  打折销售   2  赠送3
    		    present_fee="";
    		    formFee="";  
    		    
    			if("0".equals(feeMode))//营业费，卡费
    			{      
    				feeTypeCode = "10";//费用类型 (营业费用)
    			}                      
    		// if("".equals(feeTypeCode)){
    		// continue;
    		// }
    			if(Integer.parseInt(oldFee)>0)
    			{
    				if(Integer.parseInt(oldFee)>Integer.parseInt(fee) && Integer.parseInt(fee)!= 0)
    				{	//打折销售
    					saleTypeCode = "2";													
    				}
    				else if(Integer.parseInt(fee)==0)
    				{	//赠送
    					saleTypeCode = "3";													
    				}
    			}
    			if(saleTypeCode != "1")
    			{
    				int itempFee = Integer.parseInt(oldFee) - Integer.parseInt(fee);
    				present_fee = String.valueOf(itempFee);
    				formFee = "0";
    			}
    			    
    				 IData param = new DataMap();
    			     param.put("SALE_TYPE_CODE",	    saleTypeCode); //销售类型
    			     param.put("PAY_MONEY_CODE",		paymoneyCode); //收款方式
    			     param.put("FEE_TYPE_CODE",		    feeTypeCode); //费用类型     
    			     param.put("FEE_ITEM_TYPE_CODE",	feeItemTypeCode); //费用明细类型       	
    			     param.put("PAY_MODE_CODE",			"0"); //账户类别
    			     param.put("BRAND_CODE",		    input.getString("BRAND_CODE",""));//用户品牌
    			     param.put("PRODUCT_ID",		    input.getString("PRODUCT_ID",""));//用户产品
    			     param.put("USER_TYPE_CODE",		input.getString("USER_TYPE_CODE","")); //用户类型		
    			     param.put("RES_TYPE_CODE",		    resTypeCode);//资源类型
    			     param.put("RES_KIND_CODE",		    simTypeCode);//资源种类
    			     param.put("CAPACITY_TYPE_CODE",	capacityTypeCode);//面值/容量
    			     param.put("RECE_FEE",			    oldFee);//应收金额
    			     param.put("FEE",				    fee);//实收金额
    			     param.put("PRESENT_FEE",			present_fee);//促销赠送金额
    			     param.put("FORM_FEE",			    formFee); //代收手续费/佣金
    				 param.put("ACC_DATE",			    sysDate);//会计日期
    				 param.put("OPER_DATE",			    sysDate);//交易日期
    				 param.put("CANCEL_TAG",			"0");//返销标记 0 正业务  1返销业务
    			     param.put("PROC_TAG",	      		"0"); //0 未处理  1正在拆分  3拆分成功  4错单
    				 param.put("RSRV_NUM1",				input.getString("USER_ID","")); //填写用户的USER_ID
    				 param.put("CHECK_NUMBER",			checkCode);//支票/转账号
    				 //公共参数bfas
    				 param.put("TRADE_ID",              input.getString("TRADE_ID"));
    				 param.put("EPARCHY_CODE",          getVisit().getStaffEparchyCode());//地市编码 
    				 param.put("CITY_CODE",				getVisit().getCityCode());//业务区编码
    				 param.put("DEPART_ID",				getVisit().getDepartId());//部门编码
    			     param.put("OPER_STAFF_ID",			getVisit().getStaffId());//员工编码
    			     param.put("OPER_TYPE_CODE",		"141");//系统业务类型
    			     param.put("IN_MODE_CODE",			getVisit().getInModeCode());//接入方式td.getInModeCode()
    			     param.put("NET_TYPE_CODE",			"00");//网别
    			     param.put("BFAS_ID",			SeqMgr.getCrmBfasId());
    			     param.put("SYS_CODE",			"BUSNS");
    	             param.put("LOG_ID", 			"20" + input.getString("TRADE_ID"));// 记账凭证号
    	             param.put("SUB_LOG_ID", 		input.getString("TRADE_ID"));// 系统业务流水
    	             param.put("PARTITION_ID", 	StrUtil.getAcceptMonthById(input.getString("TRADE_ID")));// 分区标示

      Dao.insert("TF_A_BFAS_IN", param);
  	
	}

	private void insertTradeBfasIn(IData input) throws Exception {
   	 String sysDate=SysDateMgr.getSysTime();
	 String paymoneyCode="0"; //收款方式 TF_B_TRADEFEE_PAYMONEY表获取pay_money_code默认现金0
	 String checkCode="";       //支票/转账号
	 String emptyCardId = input.getString("EMPTY_CARD_ID");
	 String sn  = input.getString("IDVALUE");
	 IData accdataparam = new DataMap();
		 
	 String resTypeCode="1"; //资源大类编码1 sim卡  6 白卡
	 String simTypeCode= "";//资源小类编码
	 String stockIdO= "";
	 String  resState = "";
	 String capacityTypeCode="";//面值/容量
	 if("".equals(emptyCardId)){
         CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地写卡 卡号不能为空！");
     }
	 /******************add yangyz3 2013530 end**************************/
	 if(!"".equals(emptyCardId)){//白卡
//		 resTypeCode="6";
		 IDataset emptycarddata = ResCall.getEmptycardInfo(emptyCardId, sn,""); //dao.queryByPK("TF_R_EMPTYCARD", emptycardparam);
		 resTypeCode=emptycarddata.getData(0).getString("RES_TYPE_CODE","");
		 simTypeCode=emptycarddata.getData(0).getString("RES_KIND_CODE","");
		 capacityTypeCode=emptycarddata.getData(0).getString("CAPACITY_TYPE_CODE","");//容量
		 stockIdO=emptycarddata.getData(0).getString("IN_STAFF_ID","");//原库存编码应记录卡的实际归属员工号
		 resState= emptycarddata.getData(0).getString("RES_STATE");//资源状态编码
	 }	 
		 String present_fee=""; //促销
		 String formFee=""; //代收手续费/佣金
		 
		 String feeMode="10";//费用类型
		 String feeItemTypeCode="10";//费用明细类型 (TF_B_TRADEFEE_SUB)
		    String feeTypeCode="10";  //费用类型
		    String oldFee=input.getString("FEE","0");
		    String fee=input.getString("PAY","0");//实收金额
		    String saleTypeCode="1" ; //销售类型，默认为正常销售 1  打折销售   2  赠送3
		    present_fee="";
		    formFee="";  
		    
			if("0".equals(feeMode))//营业费，卡费
			{      
				feeTypeCode = "10";//费用类型 (营业费用)
			}                      
		// if("".equals(feeTypeCode)){
		// continue;
		// }
			if(Integer.parseInt(oldFee)>0)
			{
				if(Integer.parseInt(oldFee)>Integer.parseInt(fee) && Integer.parseInt(fee)!= 0)
				{	//打折销售
					saleTypeCode = "2";													
				}
				else if(Integer.parseInt(fee)==0)
				{	//赠送
					saleTypeCode = "3";													
				}
			}
			if(saleTypeCode != "1")
			{
				int itempFee = Integer.parseInt(oldFee) - Integer.parseInt(fee);
				present_fee = String.valueOf(itempFee);
				formFee = "0";
			}
			    
				 IData param = new DataMap();
			     param.put("SALE_TYPE_CODE",	    saleTypeCode); //销售类型
			     param.put("PAY_MONEY_CODE",		paymoneyCode); //收款方式
			     param.put("FEE_TYPE_CODE",		    feeTypeCode); //费用类型     
			     param.put("FEE_ITEM_TYPE_CODE",	feeItemTypeCode); //费用明细类型       	
			     param.put("PAY_MODE_CODE",			"0"); //账户类别
			     param.put("BRAND_CODE",		    input.getString("BRAND_CODE",""));//用户品牌
			     param.put("PRODUCT_ID",		    input.getString("PRODUCT_ID",""));//用户产品
			     param.put("USER_TYPE_CODE",		input.getString("USER_TYPE_CODE","")); //用户类型		
			     param.put("RES_TYPE_CODE",		    resTypeCode);//资源类型
			     param.put("RES_KIND_CODE",		    simTypeCode);//资源种类
			     param.put("CAPACITY_TYPE_CODE",	capacityTypeCode);//面值/容量
			     param.put("RECE_FEE",			    oldFee);//应收金额
			     param.put("FEE",				    fee);//实收金额
			     param.put("PRESENT_FEE",			present_fee);//促销赠送金额
			     param.put("FORM_FEE",			    formFee); //代收手续费/佣金
				 param.put("ACC_DATE",			    sysDate);//会计日期
				 param.put("OPER_DATE",			    sysDate);//交易日期
				 param.put("CANCEL_TAG",			"0");//返销标记 0 正业务  1返销业务
			     param.put("PROC_TAG",	      		"0"); //0 未处理  1正在拆分  3拆分成功  4错单
				 param.put("RSRV_NUM1",				input.getString("USER_ID","")); //填写用户的USER_ID
				 param.put("CHECK_NUMBER",			checkCode);//支票/转账号
				 //公共参数bfas
				 param.put("TRADE_ID",              input.getString("TRADE_ID"));
				 param.put("EPARCHY_CODE",          getVisit().getStaffEparchyCode());//地市编码 
				 param.put("CITY_CODE",				getVisit().getCityCode());//业务区编码
				 param.put("DEPART_ID",				getVisit().getDepartId());//部门编码
			     param.put("OPER_STAFF_ID",			getVisit().getStaffId());//员工编码
			     param.put("OPER_TYPE_CODE",		"141");//系统业务类型
			     param.put("IN_MODE_CODE",			getVisit().getInModeCode());//接入方式td.getInModeCode()
			     param.put("NET_TYPE_CODE",			"00");//网别
			     param.put("BFAS_ID",			SeqMgr.getCrmBfasId());
			     param.put("SYS_CODE",			"BUSNS");
	             param.put("LOG_ID", 			"20" + input.getString("TRADE_ID"));// 记账凭证号
	             param.put("SUB_LOG_ID", 		input.getString("TRADE_ID"));// 系统业务流水
	             param.put("PARTITION_ID", 	StrUtil.getAcceptMonthById(input.getString("TRADE_ID")));// 分区标示

			     Dao.insert("TF_B_TRADE_BFAS_IN", param);

			     //add by yuezy 20131227
//			     if("true".equals(VatUtils.getJudeYGZTag(pd))){
//			    	 IData rateInfo = new DataMap();
//			    	 String nowDate = DualMgr.getSysDate(pd);
//			    	 String tradeEparchyCode = td.getTradeEparchyCode();
//			    	 rateInfo.put("TRADE_TYPE_CODE","2108");
//			    	 rateInfo.put("NOW_DATE", nowDate);
//			    	 rateInfo.put("EPARCHY_CODE", tradeEparchyCode);
//					IDataset taxInfo = LanuchUtil.getTaxInfo(pd,rateInfo);
//					if(taxInfo.size() == 0  || taxInfo == null || taxInfo.size()>1)
//					{	
//						common.error("900099","税率配置错误，请检查");
//							
//					}else{
//					    String rate = taxInfo.getData(0).getString("RATE");
//					    String type = taxInfo.getData(0).getString("TYPE");
//                       IDataset infos = new DatasetList();
//					    IData info = new DataMap();
//					    info.put("RATE",rate);
//					    info.put("TYPE",type);
//					    info.put("FEE",oldFee);
//					    info.put("FACT_PAY_FEE",fee);
//					    infos.add(info);
//					    IDataset vatInfos = VatUtils.getTradeFeeTaxForCalculate(infos);
//					    if(vatInfos!=null&&vatInfos.size()>0){
//					    	param.put("NO_TAX_FEE",vatInfos.getData(0).getString("FEE1"));//不含税价
//					    	param.put("TAX_FEE",vatInfos.getData(0).getString("FEE2"));//不含税价
//					    	param.put("RATE",vatInfos.getData(0).getString("RATE"));//不含税价
//					    	param.put("RSRV_NUM5",vatInfos.getData(0).getString("FEE3"));//不含税价
//					    	param.put("RSRV_STR3",type);//不含税价
//					    }
//					    
//					}

			     }
				 
				 
       

	public IDataset getSimCardInfo(IData input) throws Exception
    {
        IDataset simcardInfos = IBossCall.getSimCardInfo(input.getString("SERIAL_NUMBER"), SeqMgr.getChargeId(), SeqMgr.getTradeId());
        if (IDataUtil.isEmpty(simcardInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取写卡数据错误（异地）！");
        }
        IData result = simcardInfos.getData(0);
        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            // 申请写卡得到的数据
            String imsi = result.getString("IMSI");
            String simCardNo = result.getString("SIM_CARD_NO");

            simcardInfos.getData(0).put("SMSP", result.getString("ID"));
            String encodeStr = simCardEncode(simcardInfos);

            IData returnInfo = new DataMap();
            returnInfo.put("ENCODE_STR", encodeStr);
            returnInfo.put("IMSI", imsi);
            returnInfo.put("SIM_CARD_NO", simCardNo);
            IDataset set = new DatasetList();
            set.add(returnInfo);
            return set;
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取写卡数据错误（异地）！" + result.getString("X_RSPDESC"));
            return null;
        }
    }

    public IDataset queryCustInfo(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxxx569 "+input); 
        LanuchUtil util = new LanuchUtil();
        // endDate传入什么值
        String idCardType = input.getString("IDCARDTYPE","");
        idCardType = changCardType(idCardType);
        
        IData result = IBossCall.queryRemoteRWCustInfo(input.getString("IDTYPE"), input.getString("IDVALUE"), input.getString("USER_PASSWD"), idCardType, input.getString("IDCARDNUM"), SysDateMgr.getSysDateYYYYMMDD(), "", input.getString(
                "ROUTE_TYPE", "01"), input.getString("MOBILENUM"), SeqMgr.getTradeId());
        String crmBalance = result.getString("CRM_BALANCE", "");
        if (!result.getString("CRM_BALANCE", "").equals(""))
        {
            double balance = Double.parseDouble(result.getString("CRM_BALANCE", ""));
            balance = balance / 1000.0;
            crmBalance = Double.toString(balance);
        }
        String debtBalance = result.getString("DEBT_BALANCE", "");
        if (!result.getString("DEBT_BALANCE", "").equals(""))
        {
            double dbalance = Double.parseDouble(result.getString("DEBT_BALANCE", ""));
            dbalance = dbalance / 1000.0;
            debtBalance = Double.toString(dbalance);
        }
        IData info = result;
        info.put("BRAND_CODE", result.getString("BRAND_CODE", ""));
        info.put("OPEN_DATE", result.getString("OPEN_DATE", ""));
        info.put("SCORE", result.getString("SCORE", ""));
        info.put("PUK", result.getString("PUK", ""));
        info.put("USER_STATE_CODESET", result.getString("USER_STATE_CODESET", ""));

        info.put("CUST_NAME", result.getString("CUST_NAME") == null ? "" : result.getString("CUST_NAME"));
        info.put("DEBT_BALANCE", debtBalance);
        info.put("BALANCE", crmBalance);
        info.put("IDCARDTYPE", result.getString("IDCARDTYPE") == null ? "" : result.getString("IDCARDTYPE"));
       
        String IDCARDNUM = result.getString("IDCARDNUM","").trim();
        info.put("IDCARDNUM", IDCARDNUM);
        /*info.put("IDCARDNUMCOMPARE", IDCARDNUM);
        if(IDCARDNUM.length()>=15){
            info.put("IDCARDNUM", IDCARDNUM.substring(0,4)+"***********"+IDCARDNUM.substring(IDCARDNUM.length()-3,IDCARDNUM.length()-1)+"*");
        }else{
            info.put("IDCARDNUM", IDCARDNUM);
        } */       
        info.put("PSPT_ADDR", result.getString("PSPT_ADDR") == null ? "" : result.getString("PSPT_ADDR"));
        info.put("GPRS_TAG", result.getString("GPRS_TAG") == null ? "" : result.getString("GPRS_TAG"));
        info.put("ROAM_TYPE", result.getString("ROAM_TYPE") == null ? "" : result.getString("ROAM_TYPE"));
        info.put("OPER_FEE", "0");

        info.put("LEVEL", result.getString("LEVEL") == null ? "" : result.getString("LEVEL"));
        info.put("USER_MGR", result.getString("USER_MGR") == null ? "" : result.getString("USER_MGR"));
        info.put("USER_MGR_NUM", result.getString("USER_MGR_NUM") == null ? "" : result.getString("USER_MGR_NUM"));
        info.put("SERV_OPR", result.getString("SERV_OPR") == null ? "" : result.getString("SERV_OPR"));
        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            String lanuchTdType = util.encodeIdType(result.getString("IDCARDTYPE"));
            info.put("IDCARDTYPE", lanuchTdType);

            String provCode = result.getString("HSNDUNS", "");
            if (provCode.length() > 1)
            {
                info.put("COP_SI_PROV_CODE", provCode.substring(0, provCode.length() - 1));
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地客户资料校验失败，请确认输入信息是否正确！");
        }
        IDataset set = new DatasetList();
        System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxxx635 "+info);
        set.add(info);
        return set;
    }

    private String changCardType(String idCardType) {
    	
    	if("0".equals(idCardType)){
    		idCardType = "00";//身份证件
    		
    	}else if("1".equals(idCardType)){
    		idCardType = "01";//VIP卡

    	}else if("A".equals(idCardType)){
    		idCardType = "02";//护照

    	}else if("C".equals(idCardType)){
    		idCardType = "04";//军官证

    	}else if("K".equals(idCardType)){
    		idCardType = "05";//武装警察身份证

    	}else if("Z".equals(idCardType)){
    		idCardType = "99";//其他证件
    	}else if("R".equals(idCardType)){
    		idCardType = "14";//外国人永久居住身份证
    	}
    	return idCardType;
	}

	public IDataset queryMPayInfo(IData input) throws Exception
    {
        IDataset payInfos = IBossCall.getMPayInfo(input.getString("IDTYPE"), input.getString("IDVALUE"), input.getString("USER_PASSWD"), input.getString("IDCARDTYPE"), input.getString("IDCARDNUM"), SysDateMgr.getSysTime(), "", input
                .getString("ROUTE_TYPE","01"), input.getString("MOBILENUM"), SeqMgr.getTradeId());
        if (IDataUtil.isEmpty(payInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户订购业务错误（异地）！");
        }
        IData result = payInfos.getData(0);
        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            return payInfos;
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户订购关系错误！" + result.getString("X_RSPDESC"));
            return null;
        }
    }

    /**
     * 异地写卡 白卡回写
     * 
     * @param result
     * @param emptyCardId
     * @return
     * @throws Exception
     */
    public IData remoteWriteEmptyCard(String result, String emptyCardId) throws Exception
    {
        IDataset resInfos = ResCall.remoteWriteEmptyCard(result, emptyCardId);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "白卡回写错误！");
        }
        return resInfos.getData(0);
    }

    /**
     * 对写卡信息进行拼串
     * 
     * @param simcards
     * @return
     * @throws Exception
     */
    public String simCardEncode(IDataset simcards) throws Exception
    {
        StringBuilder encodeBuilder = new StringBuilder();
        String decodeMode = "0";

        String SMSP = simcards.getData(0).getString("SMSP", "");
        String Separator = simcards.getData(0).getString("SEPARATOR", "&");
        String PhoneNum = simcards.getData(0).getString("PHONE_NUM", "0");
        String OcxVersion = null;
        String isUsim = simcards.getData(0).getString("isUsim", "0");

        if (simcards.getData(0).containsKey("OcxVersion"))
            OcxVersion = simcards.getData(0).getString("OcxVersion");

        if (OcxVersion != null && "WatchData V1.0.0".equals(OcxVersion))
        {
            for (int i = 0; i <= simcards.size(); i++)
            {
                encodeBuilder.append(simcards.getData(i).getString("SIM_CARD_NO") + Separator).append(simcards.getData(i).getString("IMSI") + Separator).append(simcards.getData(i).getString("KI") + Separator).append(SMSP + Separator).append(
                        simcards.getData(i).getString("PIN") + Separator).append(simcards.getData(i).getString("PIN2") + Separator).append(simcards.getData(i).getString("PUK") + Separator).append(simcards.getData(i).getString("PUK2") + Separator);
            }
        }
        else
        {
            encodeBuilder.append("DATATYPE=" + Integer.toString(simcards.size()) + Separator);
            for (int i = 1; i <= simcards.size(); i++)
            {
                encodeBuilder.append("ICCID" + i + "=" + simcards.getData(i - 1).getString("SIM_CARD_NO") + Separator).append("IMSI" + i + "=" + simcards.getData(i - 1).getString("IMSI") + Separator).append(
                        "KI" + i + "=" + simcards.getData(i - 1).getString("KI") + Separator).append("SMSP" + i + "=" + SMSP + Separator).append("PIN1" + i + "=" + simcards.getData(i - 1).getString("PIN") + Separator).append(
                        "PUK1" + i + "=" + simcards.getData(i - 1).getString("PUK") + Separator).append("PIN2" + i + "=" + simcards.getData(i - 1).getString("PIN2") + Separator).append(
                        "PUK2" + i + "=" + simcards.getData(i - 1).getString("PUK2") + Separator);
                if ("20".equals(PhoneNum) || "1".equals(isUsim))
                {
                    encodeBuilder.append("OPC" + i + "=" + simcards.getData(i - 1).getString("OPC") + Separator);
                }
                if (simcards.size() == i - 1)
                {
                    encodeBuilder.append("DECODEMODE" + i + "=" + decodeMode);
                }
                else
                {
                    encodeBuilder.append("DECODEMODE" + i + "=" + decodeMode + Separator);
                }
            }
        }

        return encodeBuilder.toString();
    }

    public IDataset writeCardActive(IData input) throws Exception
    {
        // 费用一致性校验
        // end
        LanuchUtil util = new LanuchUtil();
        input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        input.put("SIM_CARD_NO", input.getString("NEW_SIM_CARD"));
        input.put("IMSI", input.getString("NEW_IMSI"));
        input.put("TRADE_TYPE_CODE", "141");
        input.put("USER_ID", "-1");
        input.put("OPER_FEE", input.getString("FEE","0"));//卡费
        String pay = input.getString("PAY","");//实缴

        String[] id  = util.writeLanuchLog(input).split(",");
        String tradeId = id[0];
        String order_id  =id[1];
        
        input.put("TRADE_ID", tradeId);
        
        // 插入费用台账表
        createFeeSubTrade(tradeId, input);

        IDataset ibossResults = IBossCall.realWriteCardActive(input.getString("SERIAL_NUMBER"), input.getString("IMSI"), input.getString("SIM_CARD_NO"), input.getString("EMPTY_CARD_ID"), input.getString("SIM_CARD_NO"), input.getString("IMSI"), "",
                input.getString("CARD_FEE", "00"));
        if (IDataUtil.isEmpty(ibossResults))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地激活失败！" + ibossResults.getData(0).getString("X_RSPDESC"));
            return null;
        }
        
        util.updateLanuchLog(tradeId, ibossResults.getData(0).getString("X_RSPCODE"), ibossResults.getData(0).getString("X_RSPDESC"));
        // 插入tf_bh_trade_staff表
        util.createBhStaffTrade(tradeId);

        if (ibossResults.getData(0).getString("X_RSPCODE", "").equals("0000"))
        {
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地激活失败！" + ibossResults.getData(0).getString("X_RSPDESC"));
            return null;
        }
        // 插入财务接口日志表,财务化BOSS
        financialHandle(input);
        
        IDataset result =  new DatasetList();
        IData da  = new DataMap();
        da.put("ORDER_ID", order_id);
        da.put("TRADE_ID", tradeId);
        da.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        da.put("SIM_CARD_NO",input.getString("NEW_SIM_CARD"));
        da.put("IMSI", input.getString("NEW_IMSI"));
        da.put("EMPTY_CARD_ID", input.getString("EMPTY_CARD_ID"));
        da.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        result.add(da);
        return result;
    }

    public IDataset writeCardResultback(IData input) throws Exception
    {
        // 白卡回写
        String emptyCardId = input.getString("EMPTY_CARD_ID");
        String resultCode = input.getString("RESULT_CODE");// 0 成功 1 失败

        // 调用资源接口，写卡信息回传,写卡成功与失败，均需要进行回写
        remoteWriteEmptyCard(resultCode, emptyCardId);
        IDataset set = new DatasetList();
        set.add(input);
        
        //一级boss结果码转换
        if("0".equals(resultCode)){
        	resultCode ="00";
        }else if("1".equals(resultCode)){
        	resultCode ="11";
        }

        // 异地写卡数据回写
        IDataset writeBackInfos = IBossCall.wrCrdResBack(input.getString("IMSI"), resultCode, input.getString("SERIAL_NUMBER"), SeqMgr.getTradeId());
        if (IDataUtil.isEmpty(writeBackInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "回写写卡数据错误（异地）！");
        }
        IData result = writeBackInfos.getData(0);
        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            return set;
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "回写写卡数据错误（异地）！" + result.getString("X_RSPDESC"));
            return null;
        }
    }
    /**
     * 获取卡费，通过resTypeCode获取卡费
     * @throws Exception 
     */
    public IDataset getDevicePrice(IData input) throws Exception {
    	
    	String emptyCardId = input.getString("EMPTY_CARD_ID", "");
    	String sn = input.getString("SERIAL_NUMBER", "");

    	IDataset emptyInfo = ResCall.getEmptycardInfo(emptyCardId,"", "");
    	if(IDataUtil.isEmpty(emptyInfo)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取白卡信息失败！");
    	}
    	String resKindCode = emptyInfo.getData(0).getString("RES_KIND_CODE", "");
    	String resTypeCode = emptyInfo.getData(0).getString("RES_TYPE_CODE", "");
    	IDataset set = new DatasetList();
    	IData returnInfo = new DataMap();
    	
        IData feeData = DevicePriceQry.getDevicePrice(getTradeEparchyCode(), "", "141", resKindCode, resTypeCode);
        if (IDataUtil.isEmpty(feeData))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "544032:获取补换卡费用失败！");
        }
        else
        {
            returnInfo.put("FEE", feeData.getString("DEVICE_PRICE"));
        }
        set.add(returnInfo);
    	return set;
    }
    
    //打印免填单
    public IDataset loadPrintData(IData data) throws Exception
    {
        IDataset printTemp = new DatasetList();
        data.put("TRADE_DEPT_NAME", getVisit().getDepartName());// 受理部门
        data.put("TRADE_STAFF_NAME", getVisit().getStaffName());// 受理员工
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());// 受理员工
        data.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(data.getString("TRADE_TYPE_CODE")));// 业务类型
        data.put("ID_CARD_TYPE", StaticUtil.getStaticValue("IBOSS_PSPT_TYPE_CODE", data.getString("ID_CARD_TYPE")));// 证件类型
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("VERIFY_TYPE", StaticUtil.getStaticValue("EBOSS_VERIFY_TYPE", data.getString("VERIFY_TYPE")));
        data.put("TRADE_DATE", data.getString("ACCEPT_DATE", SysDateMgr.getSysTime()));
        data.put("TRADE_TYPE","异地写卡");
        ReceiptNotePrintMgr printMgr = new ReceiptNotePrintMgr();
        printTemp = printMgr.printInterBossReceipt(data);
        
        IDataset printTemp2 = PrintDataSetBean.printDataSet(1,data.getString("TRADE_ID"),"0",getVisit().getStaffName(),getVisit().getDepartName());//1为正数发票
        printTemp2.addAll(printTemp);
        return printTemp2;
    }
    
    /**
     * 登记解析串
     * 
     * @param tradeData
     * @param receiptData
     * @throws Exception
     */
    private void regCnoteInfo(String tradeId, IData receiptData) throws Exception
    {
        String noteType = "1";
        String acceptTime = SysDateMgr.getSysTime();
        String acceptMonth = acceptTime.substring(5, 7);

        IDataset noteInfoLogs = TradeReceiptInfoQry.getNoteInfoByPk(tradeId, acceptMonth, noteType);
        // 如果没有解析串记录，则记录打印模板数据到TF_B_TRADE_CNOTE_INFO表
        if (IDataUtil.isEmpty(noteInfoLogs))
        {
            IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            param.put("ACCEPT_MONTH", acceptMonth);
            param.put("RECEIPT_INFO1", "受理员工："+CSBizBean.getVisit().getStaffName());
            param.put("RECEIPT_INFO2", receiptData.getString("RECEIPT_INFO2", ""));
            param.put("RECEIPT_INFO3", receiptData.getString("RECEIPT_INFO3", ""));
            param.put("RECEIPT_INFO4", receiptData.getString("RECEIPT_INFO4", ""));
            param.put("RECEIPT_INFO5", receiptData.getString("RECEIPT_INFO5", ""));
            param.put("NOTE_TYPE", noteType);
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

            param.put("ACCEPT_DATE", acceptTime);
            param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
            param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

            Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "INS_RECEIPT_LOGINFO", param);
        }
    }
    
    /**
     * @Description 构建打印发票数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset printTrade(IData param) throws Exception{
    	
    	String tradeId = param.getString("TRADE_ID","");
    	String feeMode = "0";
    	
    	return PrintDataSetBean.printDataSet(1,tradeId,feeMode,param.getString("STAFF_NAME",""),
    			param.getString("DEPART_NAME",""));//1为正数发票
    }
    public IDataset applyRemoteWrite(IData input) throws Exception
    {
    	if (isTest) {//测试代码，务必删除
    		return testApplyRemoteWrite();
    	} else {
          	String transactionId = getTransID();
          	String cardsn = input.getString("EMPTY_CARD_ID");
          	String iccid = input.getString("SIM_CARD_NO");
          	String serialNumber = input.getString("MOBILENUM");
          	String identCode = input.getString("IDENT_CODE");
          	IData ibossParam = new DataMap();
          	ibossParam.put("SEQ", transactionId);
          	ibossParam.put("ID_VALUE", serialNumber);
          	ibossParam.put("CARDSN", cardsn);
          	ibossParam.put("ICCID", iccid);
          	ibossParam.put("IDENT_CODE", identCode);
          	ibossParam.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
            IDataset simcardInfos = IBossCall.applyRemoteWrite(ibossParam);
            if (IDataUtil.isEmpty(simcardInfos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取写卡数据错误（异地）！");
            }
            IData result = simcardInfos.getData(0);
            if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
            {	
                IData returnInfo = new DataMap();
                returnInfo.put("IMSI", result.getString("IMSI"));
                returnInfo.put("ICCID", result.getString("ICCID"));
                returnInfo.put("SMSP", result.getString("SMSP"));
                returnInfo.put("PIN1", result.getString("PIN1"));
                returnInfo.put("PIN2", result.getString("PIN2"));
                returnInfo.put("PUK1", result.getString("PUK1"));
                returnInfo.put("PUK2", result.getString("PUK2"));
                returnInfo.put("EXTENSION_RSP", result.getDataset("EXTENSION_RSP"));
                returnInfo.put("HomeProv", result.getString("HOME_PROV"));
                returnInfo.put("ReqSeq", result.getString("SEQ"));
                IDataset set = new DatasetList();
                set.add(returnInfo);           
                return set;
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取写卡数据错误（异地）！" + result.getString("X_RSPDESC"));
                return simcardInfos;
            }
    	}
    }
    private IDataset testApplyRemoteWrite() throws Exception{
        IData returnInfo = new DataMap();
        String transactionId = getTransID();
        returnInfo.put("IMSI", "460077173166905");
        returnInfo.put("ICCID", "89860097026947316805");
        returnInfo.put("PIN1", "1234");
        returnInfo.put("PIN2", "2298");
        returnInfo.put("PUK1", "91314998");
        returnInfo.put("PUK2", "91314998");
        returnInfo.put("Name", "测试Nae");
        returnInfo.put("Value", "测试Nae");
        returnInfo.put("HomeProv", "731");
        returnInfo.put("ReqSeq", transactionId);
        IDataset dataset = new DatasetList();
        dataset.add(returnInfo);
        return dataset;
    }
	public String getTransID() throws Exception{
		String eparchcode=getVisit().getLoginEparchyCode();
		String id = SeqMgr.getInstId(eparchcode);
		return "898"+"BIP2B021"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+id.substring(id.length()-6);
	}
    public IDataset afterWriteCard(IData input) throws Exception
    {
        // 物联网 net _type_code =07 调用老资源接口
        // String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        String imsi = input.getString("IMSI");
        String emptyCardId = input.getString("EMPTY_CARD_ID");
        String resultCode = input.getString("RESULT_CODE");// 0 成功 1 失败
        // 调用资源接口，写卡信息回传,写卡成功与失败，均需要进行回写
        remoteWriteUpdate(imsi, emptyCardId, simCardNo, "3", resultCode);
        IDataset set = new DatasetList();
        set.add(input);
        return set;
    } 
    /**
     * 根据写卡返回值更新白卡及SIM个性化资料状态
     * 
     * @param remoteMode
     *            0-异地SIM卡写卡；1-异地USIM卡写卡；2-本地写卡；不填默认为成卡开户
     * @param xChoiceTag
     *            写卡标识 0-成功；1-失败；不填默认为0
     */
    public IData remoteWriteUpdate(String imsi, String emptyCardId, String simCardNo, String remoteMode, String xChoiceTag) throws Exception
    {
        IDataset resInfos = ResCall.backWriteSimCard(imsi, emptyCardId, "", getTradeEparchyCode(), "", simCardNo, "", "", remoteMode, xChoiceTag);
        if (IDataUtil.isEmpty(resInfos) || !"0".equals(resInfos.getData(0).getString("X_RESULTCODE")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "写卡信息回写错误！");
        }
        return resInfos.getData(0);
    }    
    public IDataset beforeWriteCard(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        String emptyCardId = input.getString("EMPTY_CARD_ID");
        String sourceMode = input.getString("SOURCE_MODE");
        IData params = new DataMap(input.getString("PARAMS"));
        params.put("SIM_CARD_NO", params.getString("ICCID"));
        params.put("PIN", params.getString("PIN1"));
        params.put("PUK", params.getString("PUK1"));
        IDataset speSimList = new DatasetList();
        speSimList.add(params);
        String imsi = speSimList.getData(0).getString("IMSI");
        String simCardNo = speSimList.getData(0).getString("SIM_CARD_NO");
        String smsp = speSimList.getData(0).getString("SMSP");
        String encodeStr = "";
        String isNewCard = input.getString("IS_NEW");
        // 写卡数据拼串处理
        // 老卡
        if ("0".equals(isNewCard))
        {
        	encodeStr = simCardEncode(speSimList, "");
        }
        // 新卡
        if ("1".equals(isNewCard))
        {
            String tradeId = SeqMgr.getTradeId().substring(6);
            AssemDynData ass = new AssemDynData();
            EncAssemDynData enAss = new EncAssemDynData();
            List<EncAssemDynData> enAsses = new ArrayList<EncAssemDynData>();
            IssueData issue = new IssueData();
            issue.setIccId(speSimList.getData(0).getString("SIM_CARD_NO", ""));
            issue.setImsi(speSimList.getData(0).getString("IMSI", ""));
            issue.setPin1(speSimList.getData(0).getString("PIN", ""));
            issue.setPin2(speSimList.getData(0).getString("PIN2", "1425"));
            issue.setPuk1(speSimList.getData(0).getString("PUK", ""));
            issue.setPuk2(speSimList.getData(0).getString("PUK2", "56541837"));
            issue.setSmsp(speSimList.getData(0).getString("SMSP", ""));
            enAss.setMsisdn(serialNumber);
            enAss.setIssueData(issue);
            enAsses.add(enAss);
            ass.setChanelflg("1");
            String cardInfo = "080A" + speSimList.getData(0).getString("SIM_CARD_NO", "") + "0E0A" + emptyCardId;
            ass.setCardInfo(cardInfo);
            ass.setEnc(enAsses);
            ass.setSeqNo(tradeId);
            WebServiceClient client = new WebServiceClient();
            System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxx1069 "+ass);
            EncAssemDynDataRsp resAssemData = client.encAssemClient(ass);
            encodeStr = resAssemData.getIssueData();
            String result_code = resAssemData.getResultCode();
            if (!"0".equals(result_code))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用现场实时写卡系统失败");
            }
            returnInfo.put("TRADE_ID", tradeId);
        }
        returnInfo.put("ENCODE_STR", encodeStr);
        returnInfo.put("IMSI", imsi);
        returnInfo.put("SIM_CARD_NO", simCardNo);
        returnInfo.put("SIM_INFO", speSimList.getData(0));
        IDataset set = new DatasetList();
        set.add(returnInfo);
        return set;
    }
    public String simCardEncode(IDataset simcards, String smsp) throws Exception
    {
        StringBuilder encodeBuilder = new StringBuilder();
        String decodeMode = "1";
        String separator = "&";
        encodeBuilder.append("DATATYPE=" + simcards.size() + separator);
        for (int i = 1; i <= simcards.size(); i++)
        {
            IDataset set = CommparaInfoQry.getCommparaAllCol("CSM", "3060", simcards.getData(i - 1).getString("SWITCH_ID"), "ZZZZ");
            if (IDataUtil.isEmpty(set))
            {
                decodeMode = "0";
            }
            encodeBuilder.append("DECODEMODE" + i + "=" + decodeMode + separator);
            encodeBuilder.append("ICCID" + i + "=" + simcards.getData(i - 1).getString("SIM_CARD_NO") + separator).append("IMSI" + i + "=" + simcards.getData(i - 1).getString("IMSI") + separator).append(
                    "KI" + i + "=" + simcards.getData(i - 1).getString("KI") + separator).append("SMSP" + i + "=" + simcards.getData(i - 1).getString("SMSP") + separator).append("PIN1" + i + "=" + simcards.getData(i - 1).getString("PIN") + separator).append(
                    "PUK1" + i + "=" + simcards.getData(i - 1).getString("PUK") + separator).append("PIN2" + i + "=" + simcards.getData(i - 1).getString("PIN2") + separator).append(
                    "PUK2" + i + "=" + simcards.getData(i - 1).getString("PUK2") + separator).append("OPC" + i + "=" + simcards.getData(i - 1).getString("OPC", "") + separator);
        }
        return encodeBuilder.toString();
    }
    public IDataset getStaticValue(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData info = new DataMap();
        IData staticInfo = StaticInfoQry.getStaticInfoByTypeIdDataId("IBOSS_PSPT_TYPE_CODE", input.getString("IDCARDTYPE", "0"));
        IData level = StaticInfoQry.getStaticInfoByTypeIdDataId("IBOSS_STAR_LEVEL", input.getString("LEVEL", "09"));
        if(staticInfo!=null&&level!=null){
            info.put("ICARDSTYPES", staticInfo.getString("DATA_NAME",""));
            info.put("LEVELS", level.getString("DATA_NAME",""));                     
       }else{
            info.put("ICARDSTYPES", "身份证");
            info.put("LEVELS", "未评级");
        } 
        dataset.add(info);
        return dataset;
    } 
    public IDataset queryRemoteWriteCustomer(IData input) throws Exception
    {
        System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1127 "+input);
        
    	if (isTest) {
    	    
    	    
    	    
    		return this.testQueryRemoteWriteCustomer();
    	} else {
    	    System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1132 ");
            LanuchUtil util = new LanuchUtil();
            IDataUtil.chkParam(input, "IDTYPE");
            IDataUtil.chkParam(input, "ID_ITEM_RANGE");
            String userPasswd = input.getString("USER_PASSWD", "");
            if (StringUtils.isEmpty(userPasswd)) {
            	IDataUtil.chkParam(input, "IDCARDTYPE");
            	IDataUtil.chkParam(input, "IDCARDNUM");
            }
            String idCardType = input.getString("IDCARDTYPE", "");
            idCardType = changCardType(idCardType);
            IDataset typeIdset = new DatasetList(); 
            typeIdset.add("0");//0:基本资料 
            typeIdset.add("1");//1:个性化资料            
            System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1146 ");
            //xxxxxxxxxxxx测试数据
           //15829130981 610524199308280030  123458     
           //14789016836 440582199608215451  568805
            //String stl = "{CCPASSWD=[\"568805\"], DEPART_ID=[\"35541\"], IDCARD_NUM=[\"440582199608215451\"], IDCARD_TYPE=[\"00\"], ID_TYPE=[\"01\"], IN_MODE_CODE=[\"0\"], KIND_ID=[\"BIP1A010_T1000008_0_0\"], PROVINCE_CODE=[\"HAIN\"], ROUTETYPE=[\"01\"], ROUTEVALUE=[\"14789016836\"], ROUTE_EPARCHY_CODE=[\"0898\"], SERIAL_NUMBER=[\"14789016836\"], TRADE_CITY_CODE=[\"HNSJ\"], TRADE_DEPART_ID=[\"35541\"], TRADE_EPARCHY_CODE=[\"0898\"], TRADE_STAFF_ID=[\"SUPERUSR\"], TYPE_ID=[\"0\", \"1\"], X_TRANS_CODE=[\"IBOSS\"], ORIGDOMAIN=[\"BOSS\"], HOMEDOMAIN=[\"BOSS\"], BIPCODE=[\"BIP1A010\"], ACTIVITYCODE=[\"T1000008\"], BIPVER=[\"\"], ACTIONCODE=[\"1\"], SVCCONTVER=[\"\"], TESTFLAG=[\"0\"], BUSI_SIGN=[\"BIP1A010_T1000008_0_0\"], UIPBUSIID=[\"317053113084192450234\"], TRANSIDO=[\"2017053113084094116308\"], PROCID=[\"\"], TRANSIDH=[\"12510336257\"], PROCESSTIME=[\"\"], TRANSIDC=[\"89801110-t8980-hjgw620170531131844846000095\"], CUTOFFDAY=[\"20170531\"], OSNDUNS=[\"8980\"], HSNDUNS=[\"8910\"], CONVID=[\"7d70d352-ebaf-439b-a87b-6428bf17b452\"], MSGSENDER=[\"8981\"], MSGRECEIVER=[\"8911\"], X_RSPTYPE=[\"0\"], X_RSPCODE=[\"0000\"], X_RSPDESC=[\"成功\"], X_RESULTINFO=[\"受理成功\"], X_RESULTCODE=[\"0000\"], INFO_CONT=[{INFO_TYPEID=[\"0\"], INFO_ITEMS=[{ITEM_ID=[\"101\", \"103\", \"104\", \"114\", \"118\", \"119\", \"122\", \"123\", \"126\", \"127\", \"128\", \"129\"], ITEM_CONT=[\"郑植锋\", \"00\", \"440582199608215451\", \"0\", \"20170321113943\", \"00\", \"66720431\", \"0\", \"西藏拉萨\", \"\", \"\", \"全球通\"]}]}, {INFO_TYPEID=[\"1\"], INFO_ITEMS=[{ITEM_ID=[\"601\", \"602\", \"603\", \"604\", \"605\", \"606\", \"607\", \"608\", \"609\", \"610\", \"611\", \"627\", \"628\"], ITEM_CONT=[\"郑植锋\", \"0\", \"20\", \"00\", \"440582199608215451\", \"2\", \"\", \"14789016836\", \"\", \"\", \"\", \"\", \"\"]}]}], ACNT_PAYAMOUNT=[\"0\"], ACNT_BALANCE=[\"0\"], BIPSTATUS=[\"1\"], IBBFEE=[\"0\"], IBBFEEDIR=[\"0\"], IBBFEEDIRTAG=[\"0\"], IBAFEE=[\"0\"], IBAFEEDIR=[\"3\"], IBSFEE=[\"0\"], IBSFEEDIR=[\"0\"], RECONTAG=[\"0\"]}";             
            //IData iboosResult = Wade3DataTran.wade3To4DataMap(Wade3DataTran.strToMap(stl));            
            IData iboosResult = IBossCall.querySingleRemoteCust(input.getString("IDTYPE"), input.getString("ID_ITEM_RANGE"), userPasswd, idCardType, input.getString("IDCARDNUM"),typeIdset);
            
            System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1148 "+iboosResult);
            if(IDataUtil.isEmpty(iboosResult)){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "跨区补卡调用IBoss返回数据为空！");
            }
            IData info = new DataMap();
            info.put("IDTYPE", iboosResult.getString("ID_TYPE"));
            info.put("IDVALUE", iboosResult.getString("SERIAL_NUMBER"));
            if("0000".equals(iboosResult.get("X_RSPCODE"))
            		&& "0000".equals(iboosResult.get("X_RESULTCODE"))) {//返回成功
                System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1154 ");
    			 info.put("ACNTPAYAMOUNT",round(Double.parseDouble(iboosResult.getString("ACNT_PAYAMOUNT","0"))/1000.0));
    			 info.put("ACNTBALANCE", round(Double.parseDouble(iboosResult.getString("ACNT_BALANCE","0"))/1000.0));
    			 IDataset infoContList = iboosResult.getDataset("INFO_CONT");
                 System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1157 "+infoContList);

    			 for (int i = 0, size = infoContList.size(); i < size; i++) {
     		            IData infoCont = infoContList.getData(i);
    		            String infoTypeId = infoCont.getString("INFO_TYPEID");
    		            IDataset infoItems = infoCont.getDataset("INFO_ITEMS");
    		            System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1164 "+infoItems);

                    if ("0".equals(infoTypeId)) {//客户基本资料
                        System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1167 " + infoItems);
                        for (int j = 0; j < infoItems.size(); j++) {//按目前规范，该值应该为1,
                            IData infoItem = infoItems.getData(j);
                            IDataset itemIdList = infoItem.getDataset("ITEM_ID");
                            IDataset itemContList = infoItem.getDataset("ITEM_CONT");
                            if (itemIdList != null && itemIdList.size() > 0) {
                                for (int z = 0; z < itemIdList.size(); z++) {
                                    String itemId = (String) itemIdList.get(z);
                                    String itemCont = "";

                                    if (itemContList != null && itemContList.size() > z && itemContList.get(z) != null) {
                                        itemCont = (String) itemContList.get(z);
                                    }

                                    String keyName = this.transItemId(itemId);
                                    if (StringUtils.isEmpty(keyName)) {
                                        keyName = itemId;
                                    }
                                    info.put(keyName, itemCont);
                                }
                            }
                        }
                    } else if ("1".equals(infoTypeId)) {//客户个性化资料
                        System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1183 " + infoItems);
                        for (int j = 0; j < infoItems.size(); j++) {//按目前规范，该值应该为1,
                            IData infoItem = infoItems.getData(j);
                            IDataset itemIdList = infoItem.getDataset("ITEM_ID");
                            IDataset itemContList = infoItem.getDataset("ITEM_CONT");
                            if (itemIdList != null && itemIdList.size() > 0) {
                                for (int z = 0; z < itemIdList.size(); z++) {
                                    String itemId = (String) itemIdList.get(z);
                                    String itemCont = "";
                                    if (itemContList != null && itemContList.size() > z && itemContList.get(z) != null) {
                                        itemCont = (String) itemContList.get(z);
                                    }
                                    String keyName = this.transItemId(itemId);
                                    if (StringUtils.isEmpty(keyName)) {
                                        keyName = itemId;
                                    }
                                    if(keyName.trim().equals("631")){//
                                        System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1249 " + itemCont);
                                        info.put(keyName, "");
                                    }else{
                                    info.put(keyName, itemCont);
                                    }
                                }
                            }
                        }
                    }
    			 }
    		}else{
    		    System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1201 ");
    			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地客户资料校验失败，请确认输入信息是否正确！"+iboosResult.getString("X_RESULTINFO"));
    		}
            String crmBalance = iboosResult.getString("CRM_BALANCE", "");
            if (!iboosResult.getString("CRM_BALANCE", "").equals(""))
            {
                double balance = Double.parseDouble(iboosResult.getString("CRM_BALANCE", ""));
                balance = balance / 1000.0;
                crmBalance = Double.toString(balance);
            }
            String debtBalance = iboosResult.getString("DEBT_BALANCE", "");
            if (!iboosResult.getString("DEBT_BALANCE", "").equals(""))
            {
                double dbalance = Double.parseDouble(iboosResult.getString("DEBT_BALANCE", ""));
                dbalance = dbalance / 1000.0;
                debtBalance = Double.toString(dbalance);
            }
            System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1218 ");
            info.put("BRAND_CODE", info.getString("BRAND_CODE", ""));//129
            info.put("OPEN_DATE", info.getString("OPEN_DATE", ""));//118		
            info.put("SCORE", info.getString("SCORE", ""));//114  
            info.put("PUK", info.getString("PUK", ""));//122
            info.put("USER_STATE_CODESET", info.getString("USER_STATE_CODESET", ""));//119
            info.put("CUST_NAME", info.getString("CUST_NAME") == null ? "" : info.getString("CUST_NAME"));//101
            info.put("DEBT_BALANCE", debtBalance);
            info.put("BALANCE", crmBalance);
            info.put("IDCARDTYPE", info.getString("IDCARDTYPE") == null ? "" : info.getString("IDCARDTYPE"));//103
            
            info.put("IDCARDNUM", info.getString("IDCARDNUM") == null ? "" : info.getString("IDCARDNUM"));//104
            info.put("PSPT_ADDR", info.getString("PSPT_ADDR") == null ? "" : info.getString("PSPT_ADDR"));
            info.put("GPRS_TAG", info.getString("GPRS_TAG") == null ? "" : info.getString("GPRS_TAG"));
            info.put("ROAM_TYPE", info.getString("ROAM_TYPE") == null ? "" : info.getString("ROAM_TYPE"));
            info.put("OPER_FEE", "0");
            info.put("LEVEL", info.getString("CREDIT_LEVEL") == null ? "" : info.getString("CREDIT_LEVEL"));
            info.put("USER_MGR", info.getString("CUST_MANAGER") == null ? "" : info.getString("CUST_MANAGER"));
            info.put("USER_MGR_NUM", info.getString("CUST_MANAGER_PHONE") == null ? "" : info.getString("CUST_MANAGER_PHONE"));
            info.put("SERV_OPR", info.getString("SERV_OPR") == null ? "" : info.getString("SERV_OPR"));
            String lanuchTdType = util.encodeIdType(info.getString("IDCARDTYPE"));
            info.put("IDCARDTYPE", lanuchTdType);
            info.put("ICARDSTYPES", StaticUtil.getStaticValue("IBOSS_PSPT_TYPE_CODE", lanuchTdType));
            info.put("LEVELS", StaticUtil.getStaticValue("IBOSS_STAR_LEVEL", info.getString("LEVEL")));
            String provCode = iboosResult.getString("HSNDUNS", "");
            if (provCode.length() > 1)
            {
                info.put("COP_SI_PROV_CODE", provCode.substring(0, provCode.length() - 1));
            }
            IDataset set = new DatasetList();
            info.put("PSPT_TYPE_CODE", info.getString("IDCARDTYPE") == null ? "" : info.getString("IDCARDTYPE"));
            IData authCustInfo = new DataMap();
            authCustInfo.putAll(info);
            info.put("AUTH_CUST_INFO", authCustInfo);
            set.add(info);
            System.out.println("RemoteWriteCardBean.java:queryRemoteWriteCustomerxxxxxxxxxxxxxxx1248 "+info);
            return set;    		
    	}
    }
  private IDataset testQueryRemoteWriteCustomer() {       
        IData info = new DataMap();
        info.put("X_RSPTYPE","0");
        info.put("X_RSPCODE","0000");
        info.put("BRAND_CODE","90");//129
        info.put("BRAND_CODE","90");//129
        info.put("OPEN_DATE", info.getString("OPEN_DATE", ""));//118		
        info.put("SCORE", 11);//114
        info.put("PUK", info.getString("PUK", "122"));//122
        info.put("USER_STATE_CODESET", info.getString("USER_STATE_CODESET", "00"));//119
        info.put("CUST_NAME", "钟大大");//101
        info.put("DEBT_BALANCE", 100);
        info.put("BALANCE", 200);
        info.put("IDCARDTYPE", "00");//103
        info.put("IDCARDNUM", "430723198410154929");//104
        info.put("PSPT_ADDR", "钟大大家");
        info.put("GPRS_TAG", "");
        info.put("ROAM_TYPE", "");
        info.put("OPER_FEE", "0");
        info.put("LEVEL", "03");
        info.put("USER_MGR", "钟大大总");
        info.put("USER_MGR_NUM", "13829213572");
        info.put("SERV_OPR", "");
        info.put("HSNDUNS", "7310");
		LanuchUtil util = new LanuchUtil();
        String lanuchTdType = util.encodeIdType(info.getString("IDCARDTYPE"));
        info.put("IDCARDTYPE", lanuchTdType);


        IData authCustInfo = new DataMap();
        authCustInfo.putAll(info);
        info.put("AUTH_CUST_INFO", authCustInfo);
        IDataset ds = new DatasetList();
        ds.add(info);
        return ds;  
    }
  public static String round(double value) {  
	   	 DecimalFormat df2 = new DecimalFormat("###.00");  
	   	 return df2.format(value); 
	    }  
  private String transItemId(String itemId) {
  	String keyName = "";
  	if (StringUtils.equalsIgnoreCase("100", itemId)) {//客户标识
  		keyName = "CUST_ID";
  	} else if (StringUtils.equalsIgnoreCase("101", itemId)) {//客户姓名
  		keyName = "CUST_NAME";
  	} else if (StringUtils.equalsIgnoreCase("103", itemId)) {//证件类别
  		keyName = "IDCARDTYPE";
  	} else if (StringUtils.equalsIgnoreCase("104", itemId)) {//证件号码
  		keyName = "IDCARDNUM";
  	} else if (StringUtils.equalsIgnoreCase("114", itemId)) {//客户积分:可用积分余额
  		keyName = "SCORE";
  	} else if (StringUtils.equalsIgnoreCase("118", itemId)) {//注册日期/入网时间:YYYYMMDDhhmmss
  		keyName = "OPEN_DATE";
  	} else if (StringUtils.equalsIgnoreCase("119", itemId)) {//用户状态
  		keyName = "USER_STATE_CODESET";
  	} else if (StringUtils.equalsIgnoreCase("122", itemId)) {//PUK码
  		keyName = "PUK";
  	} else if (StringUtils.equalsIgnoreCase("123", itemId)) {//信用度
  		keyName = "CREDIT";
  	} else if (StringUtils.equalsIgnoreCase("124", itemId)) {//增值业务开放情况
  	} else if (StringUtils.equalsIgnoreCase("127", itemId)) {//用户星级
  		keyName = "CREDIT_LEVEL";
  	} else if (StringUtils.equalsIgnoreCase("128", itemId)) {//客户俱乐部
  	} else if (StringUtils.equalsIgnoreCase("129", itemId)) {//客户品牌
  		keyName = "BRAND_CODE";
  	} 
  	else if (StringUtils.equalsIgnoreCase("601", itemId)) {//姓名
  		keyName = "NAME";
  	} else if (StringUtils.equalsIgnoreCase("602", itemId)) {//性别
  		keyName = "SEX";
  	} else if (StringUtils.equalsIgnoreCase("603", itemId)) {//年龄
  		keyName = "AGE";
  	} else if (StringUtils.equalsIgnoreCase("604", itemId)) {//身份证件类型
  		keyName = "PSTP_TYPE_CODE";
  	} else if (StringUtils.equalsIgnoreCase("605", itemId)) {//身份证件号码
  		keyName = "PSTP_ID";
  	} else if (StringUtils.equalsIgnoreCase("606", itemId)) {//婚姻状况
  		keyName = "MARRIAGE";
  	} else if (StringUtils.equalsIgnoreCase("607", itemId)) {//教育程度
  		keyName = "EDUCATE_DEGREE";
  	} else if (StringUtils.equalsIgnoreCase("608", itemId)) {//手机号码
  		keyName = "SERIAL_NUMBER";
  	} else if (StringUtils.equalsIgnoreCase("609", itemId)) {//联系电话
  		keyName = "CONTACT_PHONE";
  	} else if (StringUtils.equalsIgnoreCase("610", itemId)) {//联系地址
  		keyName = "CONTACT_ADDRESS";
  	} else if (StringUtils.equalsIgnoreCase("611", itemId)) {//客户经理工号/姓名
  		keyName = "CUST_MANAGER";
  	} else if (StringUtils.equalsIgnoreCase("627", itemId)) {//客户经理联系电话
  		keyName = "CUST_MANAGER_PHONE";
  	} else if (StringUtils.equalsIgnoreCase("628", itemId)) {//工作单位
  		keyName = "WORK_NAME";
  	} else if (StringUtils.equalsIgnoreCase("621", itemId)) {//本地市话月消费额(当月)
  	} else if (StringUtils.equalsIgnoreCase("622", itemId)) {//国内长途月消费额(当月)
  	}  else if (StringUtils.equalsIgnoreCase("623", itemId)) {//国际长途月消费额(当月)
  	}  else if (StringUtils.equalsIgnoreCase("624", itemId)) {//国内漫游月消费额(当月)
  	}  else if (StringUtils.equalsIgnoreCase("625", itemId)) {//国际漫游月消费额(当月)
  	}  else if (StringUtils.equalsIgnoreCase("629", itemId)) {//主要增值业务收入(当月)
  	}
  	return keyName;
  }
  
  

  public IDataset applyResultActiveCallRes(IData input) throws Exception
  {
      
      System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxxxxxx1365(applyResultActive) "+input);
                  
      if("".equals(input.getString("FEE", ""))){
          input.put("FEE", "0");
      }
      
      LanuchUtil util = new LanuchUtil();

      input.put("SERIAL_NUMBER", input.getString("MOBILENUM"));
      input.put("IMSI", input.getString("IMSI"));
      input.put("TRADE_TYPE_CODE", "149");
      input.put("IS_REMOTE_WRITE_CARD", true);
  	if (isTest) {
  		input.put("TRADE_TYPE_CODE", "141");
  	} 
      input.put("USER_ID", "-1");
      input.put("OPER_FEE", input.getString("FEE", "0"));// 卡费
      String pay = input.getString("PAY", "");// 实缴
/*      String[] id = util.writeLanuchLog(input).split(",");
      String tradeId = id[0];
      String order_id = id[1];
      // 插入费用台账表
      createFeeSubTrade(tradeId, input);*/

      IDataset emptyCardKiInfos = ResCall.getEmptycardInfo(input.getString("EMPTY_CARD_ID"), "", "");
      IData emptyCardKiInfo = emptyCardKiInfos.getData(0);
      String encKi = emptyCardKiInfo.getString("KI");
      String encOpc = emptyCardKiInfo.getString("OPC");
      
      if (StringUtils.isBlank(encKi) || StringUtils.isBlank(encOpc)) {
          CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取空白卡的KI,OPC！");
      }
      
      IData kiInfo = new DataMap();
      kiInfo.putAll(emptyCardKiInfo);
      desKi(kiInfo,SeqMgr.getTradeId());
      String ki = kiInfo.getString("KI");
      String opc = kiInfo.getString("OPC");
      
/*      input.put("ki",ki);
      input.put("opc",opc);*/
      
      //String seq = input.getString("ReqSeq");
      String sn = input.getString("MOBILENUM");
      //String imsi = input.getString("IMSI");
     //String result  = input.getString("RESULT","0");
      //String signature = input.getString("SIGNATURE","");
      if (isTest) {
        IData returnData = new DataMap();
/*          returnData.put("TRADE_ID", tradeId);
        returnData.put("ORDER_ID", order_id);*/
        IDataset returnSet = new DatasetList();
        returnSet.add(returnData);
        return returnSet;
      } else {
          String simCardNo = input.getString("ICCID");        
          IDataset resDs = ResCall.occupyEmptyCard(input.getString("EMPTY_CARD_ID"), simCardNo, sn, "1");
          System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxxxx1461 "+resDs);
          if(resDs!=null&&resDs.size()>0){
              resDs.getData(0).put("KI",ki);
              resDs.getData(0).put("OPC",opc);
          }
          return resDs;
      }
  }
  
  public IDataset applyResultActive(IData input) throws Exception
  {
        String ki = input.getString("KI","").trim();
        String opc = input.getString("OPC","").trim();
        String seq = input.getString("ReqSeq");
        String sn = input.getString("MOBILENUM");
        String imsi = input.getString("IMSI");
        String result = input.getString("RESULT", "0");
        String signature = input.getString("SIGNATURE", "");
        IDataset ibossResults = IBossCall.applyResultActive(sn, seq, imsi, result, ki,opc,signature);
        if (IDataUtil.isEmpty(ibossResults))
        {
          ResCall.transFormEmptyCardCancel(input.getString("EMPTY_CARD_ID"));
          //CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地激活失败：没有收到归属省应答消息 ");
          // return null;
        }else if (ibossResults.getData(0).getString("X_RSPCODE", "").equals("0000"))
        //util.updateLanuchLog(tradeId, ibossResults.getData(0).getString("X_RSPCODE"), ibossResults.getData(0).getString("X_RSPDESC"));
        
        {/*
            //采用临时方案，将下面逻辑移到 跨区补卡反馈接口里面去处理。
              ibossResults.getData(0).put("TRADE_ID", tradeId);
            ibossResults.getData(0).put("ORDER_ID", order_id);
            IData param = new DataMap();
            param.putAll(input);
            param.put("TRADE_ID", tradeId);
            String provCode = ibossResults.getData(0).getString("HSNDUNS", "");
            if (provCode.length() > 1)
            {
              param.put("HOME_PROV", provCode.substring(0, provCode.length() - 1));
            }
            util.writeRealNameLog4RemoteWriteCard(param);
        */}else
        {
            ResCall.transFormEmptyCardCancel(input.getString("EMPTY_CARD_ID"));
            //CSAppException.apperr(CrmCommException.CRM_COMM_103, "异地激活失败！" + ibossResults.getData(0).getString("X_RSPDESC"));
            //return null;
        }
        //ibossResults.add(input);  
        return ibossResults;
        //return new DatasetList();

    }  
  
  private void desKi(IData input,String trade_id)  throws Exception {
  	  String encKi = input.getString("KI");
  	  String encOpc = input.getString("OPC");
      
      // 第一次解密
      KIFunc kifunc = new KIFunc();
      encKi = kifunc.DecryptKI(encKi);
      encOpc = kifunc.DecryptKI(encOpc);
      // 第二次解密webservice
      WebServiceClient wsc = new WebServiceClient();
      String seqNo = SeqMgr.getTradeId().substring(6);
      RoamEncPreData roam = new RoamEncPreData();
      roam.setSeqNo(seqNo);
      roam.setLocalProvCode("898");

      EncPresetData enc = new EncPresetData();
      enc.setK(encKi);
      enc.setOPC(encOpc);
      roam.setEncPresetData(enc);
      RoamEncPreDataRsp rsp = new RoamEncPreDataRsp();
      try
      {
          rsp = wsc.encPreData(roam);
          String resultCode = rsp.getResultCode();
          if (!"0".equals(resultCode))
          {
          	String strError = "加密机解密错误，" + rsp.getResultMessage();
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);
          }
      }
      catch (Exception e)
      {
      	String strError = "加密机解密错误，" + rsp.getResultMessage();
    	CSAppException.apperr(CrmCommException.CRM_COMM_103, strError);

      }
      String ki = rsp.getEncPresetDataK();
      String opc = rsp.getEncPresetDataOPc();
      
      input.put("KI", ki);
      input.put("OPC", opc);
      
      //input.put("KI", "21E4D76175E70F5B8C78617D922A0B43");//21E4D76175E70F5B8C78617D922A0B43
      //input.put("OPC", "5E3A38FA9E15CC9CFC0F5A7282D1301D");//5E3A38FA9E15CC9CFC0F5A7282D1301D
      
  }
  
  

  /**
   * 人像比对受理单编号与照片编号同步接口调用
   * @param cycle
   * @throws Exception
   */

  public void SynPicId(IData data) throws Exception
  {
      /*
       * input数据
       * RemoteWriteCardBean.javaxxxxxxxxxxxxxxxxxxxx1365(applyResultActive) {"SIM_CARD_NO":"898600092610F1271346","HIDDEN_IDCARDNUM":"610524199308280030","ROUTETYPE":"01","SUB
MIT_TYPE":"submit","listener":"onTradeSubmit","PIC_STREAM":"","PAY":"0","MOBILENUM":"15829130981","HIDDEN_CUST_NAME":"房路遥","CSSubmitID":"1","PROVINCE_CODE":"290","c
ustinfo_PhoneFlag":"0","M2M_FLAG":"","SMSP":"+8613800290500","ID_ITEM_RANGE":"15829130981","IMSI":"460029918718201,","SCAN_TAG":"1","SUBMIT_SOURCE":"CRM_PAGE","ICCID":
"898600092610F1271346","IDTYPE":"01","EMPTY_CARD_ID":"21170045080040000159","NEW_IMSI":"","FEE":"","PUK2":"36774412","IDVALUES":"15829130981","LEVEL":"09","l":"2017061
940527308","m":"IBS9236","USER_PASSWD":"123467","RECORDID":"","PUK1":"22923778","p":"simcardmgr.RemoteWriteCardSingle","IDCARDTYPE":"0","ReqSeq":"898BIP2B0212017061916
5522094224","NEW_SIM_CARD":"898600092610F1271346","service":"ajax","FRONTBASE64":"","IDCARDNUM":"610524199308280030","PIC_ID":"","PIN2":"0000","PIN1":"4321","BACKBASE6
4":"","custinfo_RemoteVerifyFlag":"1","page":"simcardmgr.RemoteWriteCardSingle","TRADE_EPARCHY_CODE":"0898","COP_SI_PROV_CODE_NAME":"","SIM_FEE_TAG":"","TRADE_ID":"12345676576867"}
       * 
       */ 
      System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1530 "+data);
      
     
      String serialNumber = data.getString("MOBILENUM","").trim(); 
      String tradeId = data.getString("TRADE_ID","").trim(); 
      String tradeTypeCode = "149";

      // 调用接口
      IData inParam = new DataMap();
      inParam.put("first_pic_id", data.get("PIC_ID"));
      inParam.put("trade_id", tradeId);
      inParam.put("op_code", tradeTypeCode);
      inParam.put("phone", serialNumber);    
      inParam.put("work_no",CSBizBean.getVisit().getStaffId());
      inParam.put("org_info",  CSBizBean.getVisit().getDepartId());
      IDataset ds = StaffInfoQry.qryStaffInfoByStaffId(CSBizBean.getVisit().getStaffId());
      if (ds != null && ds.size() == 1) {
          inParam.put("work_name", ds.getData(0).getString("STAFF_NAME"));
      }
      IData param = new DataMap();
      param.put("DEPART_ID",  CSBizBean.getVisit().getDepartId());
      ds = Dao.qryByCode("TD_M_DEPART", "SEL_ALL_BY_PK", param);
      if (ds != null && ds.size() == 1) {
          inParam.put("org_name", ds.getData(0).getString("DEPART_NAME"));
      }
      inParam.put("op_time", SysDateMgr.getSysTime());
      System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1572 "+inParam);
      JSONObject jSONObject = null;
      jSONObject = JSONObject.fromObject(inParam);

      String contentJson = jSONObject.toString();
      IData ibossData = new DataMap();
      ibossData.put("buffer", contentJson);

      try {
          //IDataset callSyn = SynCall.synPicTrade(ibossData);
          System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1582 "+contentJson);
          String strResult = sendAutoAudit(contentJson);
          System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1584 "+strResult);
      } catch (Exception e) {
          logger.error(e);
          //e.printStackTrace();
      }
  }   
  
  /**
   * 1.2受理单编号与照片编号同步接口
   * @param saveBillRequ
   */
  private String sendAutoAudit(String str){
      System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1595str "+str);   
      OutputStreamWriter out = null;
      URL httpurl = null;
      HttpURLConnection httpConn=null;
      boolean flag = false;
      try{
          String url = BizEnv.getEnvString("crm.pic.syn.url");
          System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1602 "+url);      
          //String url ="http://localhost:8080/idvs/get_boss_custpic_info"; 
          httpurl = new URL(url);
    
          httpConn = (HttpURLConnection) httpurl.openConnection();
          httpConn.setRequestMethod("POST");
          httpConn.setDoOutput(true);
          httpConn.setDoInput(true);
          httpConn.setConnectTimeout(10000);
          httpConn.setReadTimeout(10000);
          httpConn.setRequestProperty("content-type", "text/html");
          out = new OutputStreamWriter(httpConn.getOutputStream(), "UTF-8");
          System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1611str "+str);   
          out.write(str);
          out.flush();
          System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1615 ");   
          //serviceLogger.info("|#受理单保存|#发送自动稽核请求|#工单流水号=" + saveBillRequ.getTradeId() + "|#消息内容=" + autoAuditStr+"|#发送成功");
          flag =true;
      }catch(Exception e){
          //serviceLogger.error("|#受理单保存|#发送自动稽核请求失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
      }finally{
          if(null!=out){
              try {
                  out.close();
              } catch (IOException e) {

              //  serviceLogger.error("|#受理单保存|#发送自动稽核请求|#关闭流失败,工单流水号：=" + saveBillRequ.getTradeId(), e);
              }
          }
      }
      InputStream inStream = null;
      String strResult = null;
      BufferedReader br = null;
      System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1635 "+flag);   
      if (flag) {
          try {
                  String line = null; 
                  inStream = (InputStream) httpConn.getInputStream();
                  System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1640 "+inStream);   
              //  serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId());
                  br = new BufferedReader(new InputStreamReader(inStream,"UTF-8"));
                  System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1643 "+br);   
                  StringBuilder sb = new StringBuilder();
                  while((line = br.readLine())!=null){
                      sb.append(line);                
                  }
              strResult = sb.toString();
              System.out.println("RemoteWriteCardBean.javaxxxxxxxxxxxxxxxx1642 "+strResult);   
              //System.out.print(strResult);
          //  serviceLogger.info("|#受理单保存|#接收自动稽核响应|#工单流水号=" + saveBillRequ.getTradeId() + "|#响应状态码=" +strResult);
              br.close();
          } catch (Exception e) {
          //  serviceLogger.info("|#受理单保存|#接收自动稽核响应失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
          } finally {
              if (inStream != null) {
                  try {
                      inStream.close();
                  } catch (IOException e) {

                  //  serviceLogger.info("|#受理单保存|#接收自动稽核响应|#关闭流失败|#工单流水号=" + saveBillRequ.getTradeId(),e);
                  }
              }
              if (br != null) {
                  try {
                      br.close();
                  } catch (IOException e) {
                 
                  }
              }
              if (httpConn != null) {
                  httpConn.disconnect();
                  httpConn = null;
              }
          }
      }
      return strResult;
  }
  /**
   * 记录省际跨区写卡实名认证信息
   * @param cycle
   * @throws Exception
   */

  public IDataset logRealNameInfo(IData data) throws Exception
  {
	  LanuchUtil util = new LanuchUtil();
	  util.writeRealNameLog4RemoteWriteCard(data);
	  
	  IData returnData = new DataMap();
	  IDataset returnSet = new DatasetList();
	  returnSet.add(returnData);
	  return returnSet;	  
  }
  /**
   * 获取白卡预置数据 一级能开调用
   * @param input
   * @return
   * @throws Exception
   */
	public IData qryEmptyCardResult(IData input) throws Exception {
		IData retnData = new DataMap();
		String homeProv = input.getString("HOME_PROV");//归属省代码 暂时没什么用
		String emptyCardId = input.getString("EMPTY_CARD_ID");//空卡序列号
		IDataset emptyCardKiInfos = ResCall.getEmptycardInfo(emptyCardId, "", "");
	    IData emptyCardKiInfo = emptyCardKiInfos.getData(0);
	    String encKi = emptyCardKiInfo.getString("KI");
	    String encOpc = emptyCardKiInfo.getString("OPC");
	      
	    if (StringUtils.isBlank(encKi) || StringUtils.isBlank(encOpc)) {
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取空白卡的KI,OPC！");
	    }
	    IData kiInfo = new DataMap();
	    kiInfo.putAll(emptyCardKiInfo);
	    desKi(kiInfo,SeqMgr.getTradeId());//加密
	    String ki = kiInfo.getString("KI");
	    String opc = kiInfo.getString("OPC");
	    
	    retnData.put("X_RESULTCODE", "0000");
	    retnData.put("ENC_KI", ki);
	    retnData.put("ENC_OPC",opc);
	    retnData.put("EMPTY_CARD_ID",emptyCardId);
	    retnData.put("SIGNATURE", "");
	    
	    return retnData;
	}
}
