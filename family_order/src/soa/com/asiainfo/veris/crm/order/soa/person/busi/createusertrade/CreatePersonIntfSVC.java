
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade;

import org.apache.log4j.Logger;
import java.util.Iterator;
import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.UpcConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.blackusermanager.BlackUserManagerBean;


public class CreatePersonIntfSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CreatePersonIntfSVC.class);
    /**
	 * 1:根据身份证号码查询该用户已经预占的号码接口
	 * 与“个人开户业务”的新开户号码“选号”功能一致
	 * SS.CreatePersonIntfSVC.queryNetChoosePhone
	 * @param data
	 * @throws Exception
	 * @return IDataset
	 */
	public IDataset queryNetChoosePhone(IData data) throws Exception {
		
		String pspt_id = data.getString("PSPT_ID","");
		String netchooseType = "";
		if("".equals(pspt_id)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:请输入身份证号码!");
		}
		
		/*******REQ201606300007 关于增加系统黑名单后台查询日志的需求 2016-7-15 chenxy3*************/ 
		String code="0";//编码，0=非黑名单； 9=黑名单用户
    	String msg="本次办理不成功。您的证件名下的手机号码";//黑名单用户拼串：您的证件名下的手机号码***********已欠费*元，手机号码***********已欠费*元 ，请及时缴费。
    	 
    	
        //变更主产品时候才处理 调用接口
    	IData inparam=new DataMap();
    	inparam.put("PSPT_ID", pspt_id);
    	IDataset callSet=AcctCall.qryBlackListByPsptId(inparam);
    	
    	String fee="0";
        String feeOther="";
        if (IDataUtil.isNotEmpty(callSet) )
        {
        	for(int i=0;i<callSet.size();i++){
        		IData blackInfo=callSet.getData(i);
        		String owe_sn=blackInfo.getString("SERIAL_NUMBER","");
        		String owe_fee=blackInfo.getString("OWE_FEE","0");
        		if(owe_fee!=null && !"0".equals(owe_fee)){
        			code="9";
        			msg+=""+owe_sn+"已欠费"+owe_fee+"元。";
        			feeOther=feeOther+"号码"+owe_sn+"欠费"+owe_fee+"元;";
        		}
        	} 
        	if("9".equals(code)){
        		msg+="请及时缴费，以便正常使用该证件办理业务。"; 
        		String tipsMsg=msg;
        		//插黑名单日志表
        		IData insData=new DataMap(); 
            	insData.put("PSPT_ID",pspt_id);         
            	insData.put("IN_MODE_CODE",CSBizBean.getVisit().getInModeCode());     
            	insData.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());  
            	insData.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());      
            	insData.put("TRADE_TYPE_CODE","10");  
            	insData.put("FEE", fee);              
            	insData.put("OTHER_FEE", feeOther);  
        		//CSAppCall.call("SS.BlackUserManageSVC.insertBlackUserCheckLogInfo", insData); 
            	BlackUserManagerBean.insertBlackUserCheckLogInfo(insData);
        		//阻止 
   	         	CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:"+tipsMsg);
        	}
        }
		/***********black list************/
        IDataset dataset = ResCall.getSelTempOccupyNum("0", netchooseType, pspt_id); 
		return dataset;
	}
	
	/**
	 * 2:新开户号码校验（同BOSS开户新开户号码【校验】按钮实现的功能）
	 * SS.CreatePersonIntfSVC.queryIsOpen
	 * @param data
	 * @throws Exception
	 * @return IData
	 */
	public IData queryIsOpen(IData data) throws Exception{
    	String serialNumber = data.getString("SERIAL_NUMBER","");
    	if("".equals(serialNumber)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:请输入服务号码!");
    	}
    	
    	IData result = new DataMap();
    	IDataset resNumber = CSAppCall.call("SS.CreatePersonUserSVC.checkSerialNumber", data);
        if (IDataUtil.isNotEmpty(resNumber))
        { 
           String checkResult = resNumber.getData(0).getString("CHECK_RESULT_CODE","");
           
           if("0".equals(checkResult)||"1".equals(checkResult)){
        	   //result = resNumber.getData(0);//该方式不可行，由于UIP封装后会对idata中的value为idataset的数据多加一层[]，导致新大陆不能解析为正确的json
        	   result.put("X_CAN_REOPEN","0");//可以开户
        	   if(StringUtils.isNotBlank(resNumber.getData(0).getString("EXISTS_SINGLE_PRODUCT",""))){//如果存在强制绑定产品，则传出该值
        		   result.put("PRODUCT_ID", resNumber.getData(0).getString("EXISTS_SINGLE_PRODUCT",""));
        	   }
        	   if(StringUtils.isNotBlank(resNumber.getData(0).getString("X_BIND_DEFAULT_DISCNT",""))){//如果是吉祥号码，存在绑定优惠，则传出该值
        		   result.put("X_BIND_DEFAULT_DISCNT", resNumber.getData(0).getString("X_BIND_DEFAULT_DISCNT",""));
        	   }
        	   if(StringUtils.isNotBlank(resNumber.getData(0).getString("SIM_CARD_NO",""))){//如果存在绑定的SIM卡，则传出该值
        		   result.put("SIM_CARD_NO", resNumber.getData(0).getString("SIM_CARD_NO",""));
        	   }
        	   if(StringUtils.isNotBlank(resNumber.getData(0).getString("FLAG_4G",""))){//如果存在绑定的SIM卡且是4G卡，则传出该值
        		   result.put("FLAG_4G", resNumber.getData(0).getString("FLAG_4G",""));
        	   }
               return result;
           }
        }
        result.put("X_CAN_REOPEN","1");//不可以开户 
        return result;
    }
    
    /**
     * 3:校验开户号码是否有SIM卡号已绑定（查询号码是否为预配卡）
     * SS.CreatePersonIntfSVC.checkIsBind
     * @param data
     * @throws Exception
     * @return IData
     * 该接口不再使用，由2接口把有绑定SIM卡的SIM卡值传出
     */
	public IData checkIsBind(IData data) throws Exception{
    	
    	String serialNumber = data.getString("SERIAL_NUMBER","");
    	if("".equals(serialNumber)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:请输入服务号码!");
    	}    	
    	data.put("TRADE_TYPE_CODE", "10");	
    	IDataset checkMphoneDatas = new DatasetList();
    	checkMphoneDatas = ResCall.checkResourceForMphone("0", serialNumber, "0", getVisit().getDepartId(), "0");
        if(IDataUtil.isEmpty(checkMphoneDatas)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:号码资源信息不存在!");
        }
        IData result = new DataMap();
        IData resInfo = checkMphoneDatas.first();
        String strSimCardNo = resInfo.getString("SIM_CARD_NO", "");
        String strPreCodeTag = resInfo.getString("PRECODE_TAG", "0");//预配
        if(!"".equals(strSimCardNo)&&"1".equals(strPreCodeTag)){//0:没预配；1:已预配
        	result.put("ISBIND", "TRUE");
        	result.put("SIM_CARD_NO", strSimCardNo);
        }
        else{
        	result.put("ISBIND", "FLASE");
        }
        return result;
    }
    
    /**
	 * 4:获取写卡数据接口（获取能够写入白卡的SIM卡数据）
	 * SS.CreatePersonIntfSVC.getIdleSimCardInfo
	 * @param data
	 * @throws Exception
	 * @return IDataset
	 * 已经被11-13接口替代，该接口不再使用
	 */
	public IDataset getIdleSimCardInfo(IData data) throws Exception {
		IDataUtil.chkParam(data, "EMPTY_CARD_ID");
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		String serialNumber = data.getString("SERIAL_NUMBER");
	    String emptyCardId = data.getString("EMPTY_CARD_ID");
	    WriteCardBean bean = new WriteCardBean();
	    //获取SIM卡个性资料
	    IDataset speSimList = bean.getSpeSimInfo(serialNumber, emptyCardId, "2", "2");//本地写卡
	    if(IDataUtil.isEmpty(speSimList)){
	    	CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:获取写卡数据失败!");
	    }
	    return speSimList;
	}
	
	/**
	 * 5: SIM卡卡号校验的接口
	 * SS.CreatePersonIntfSVC.checkSimCard
	 * @param data
	 * @throws Exception
	 * @return IData
	 */
	public IData checkSimCard(IData data) throws Exception {
		IData retnData = new DataMap();
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataset dataset = CSAppCall.call("SS.CreatePersonUserSVC.checkSimCardNo", data);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:获取资源数据失败!");
		}
		IData datas = dataset.first();

		if("1".equals(datas.getString("CHECK_RESULT_CODE","0"))){
			retnData = dataset.getData(0);
			retnData.put("X_CAN_REOPEN", "0");//成功
		}
		else{
			retnData.put("X_CAN_REOPEN", "1");//失败
		}
		return retnData;	
	}
	
	/**
     * 6:根据产品类型查询主产品目录信息
     * SS.CreatePersonIntfSVC.queryProductByBrand
     * @param data
	 * @throws Exception
	 * @return IDataset
     */
    public IDataset queryProductByBrand(IData data) throws Exception{
    	 IData inputParam = new DataMap();
         inputParam.put("PRODUCT_TYPE_CODE", data.getString("PRODUCT_TYPE_CODE",""));
         inputParam.put("USER_PRODUCT_ID", "");
         inputParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
         String productId = data.getString("PRODUCT_ID","");
         IDataset transProducts=new  DatasetList();
         if(!"".equals(productId)){
        	 String eparchyCode = data.getString("TRADE_EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
//modify by lijun17        	 IData productInfo = ProductInfoQry.getProductInfo(productId);
             IData productInfo = UProductInfoQry.getProductInfo(productId);
             transProducts.add(productInfo);
         }else{
        	 transProducts = CSAppCall.call("CS.ProductTreeSVC.getCanTransProduct", inputParam);
         }
         return transProducts;
    }
    
    /**
     * 7:根据产品编码查询产品模型下【默认】服务
     * SS.CreatePersonIntfSVC.queryServiceByPruductId
     * @param data
	 * @throws Exception
	 * @return IDataset
     */
    public IDataset queryServiceByPruductId(IData data) throws Exception{
    	
    	String product_id = data.getString("PRODUCT_ID");
        String eparchyCode = data.getString("TRADE_EPARCHY_CODE",CSBizBean.getTradeEparchyCode());

        IData productInfo = UProductInfoQry.getProductInfo(product_id);
        if (IDataUtil.isEmpty(productInfo))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_135, product_id);
        }
        //默认服务处理
//modify by lijun17        IDataset svcElems = PkgElemInfoQry.querySvcForceElementsByProductId(product_id, eparchyCode);
        IDataset svcElems = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, product_id, BofConst.ELEMENT_TYPE_CODE_SVC);
        if (svcElems.isEmpty())
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_192, product_id);
        }
        return svcElems;
    }
    
    /**
     * 8:根据产品编码查询产品模型下【默认】优惠
     * SS.CreatePersonIntfSVC.queryDiscntByPruductId
     * @param data
	 * @throws Exception
	 * @return IDataset
     */
    public IDataset queryDiscntByPruductId(IData data) throws Exception{
    	
    	String product_id = data.getString("PRODUCT_ID","");
    	if("".equals(product_id)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:请输入产品编号!");
		}
//modify by lijun17    	IDataset discntinfo = PkgElemInfoQry.queryDiscntForceElementsByProductId(pruduct_id, CSBizBean.getTradeEparchyCode());
    	IDataset discntinfo = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, product_id, BofConst.ELEMENT_TYPE_CODE_DISCNT);
    	return discntinfo;
    }
    
    /**
	 * 9:获取用户开户费用信息
	 * SS.CreatePersonIntfSVC.queryFeeListForOpenUser
	 * @param data
	 * @throws Exception
	 * @return IDataset
	 */
	public IDataset queryFeeListForOpenUser(IData data) throws Exception {
		
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "PRODUCT_ID");

		data.put("TRADE_TYPE_CODE", "10");
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		IDataset FeeList = new DatasetList();
		IData feeData1 = new DataMap();
		IData feeData2 = new DataMap();
		IData feeData3 = new DataMap();
		IData feeData4 = new DataMap();

		//获取服务号码相关的费用信息
		IDataset resNumber = CSAppCall.call("SS.CreatePersonUserSVC.checkSerialNumber", data);
		
		IData data0 = resNumber.getData(0);
		String simCardNo = data0.getString("SIM_CARD_NO","");
		
		if(IDataUtil.isNotEmpty(resNumber)) {
				//号码需要交纳的预存费用 sunxin
				if(StringUtils.isNotEmpty(data0.getString("FEE_CODE_FEE",""))){
					feeData1.put("FEE_MODE", "2");
					feeData1.put("FEE_TYPE_CODE", "62");//选号预存收入
					feeData1.put("FEE", data0.getString("FEE_CODE_FEE","0"));
					feeData1.put("FEE_TYPE_DESC", "预存");
					feeData1.put("FEE_DESC", "选号预存收入");
					FeeList.add(feeData1);
				}
		}

		//预配预开时，sim卡自动带出来，且为不可修改
		if(StringUtils.isNotEmpty(simCardNo)) {
			//预配卡费用 sunxin
			if(StringUtils.isNotEmpty(data0.getString("FEE"))){
					feeData2.put("FEE_MODE", data0.getString("FEE_MODE",""));
					feeData2.put("FEE_TYPE_CODE", data0.getString("FEE_TYPE_CODE",""));
					feeData2.put("FEE", data0.getString("FEE","0"));
					feeData2.put("FEE_TYPE_DESC", "营业费用");
					feeData2.put("FEE_DESC", "购卡费");
					FeeList.add(feeData2);
		    }
		}
		else{
			//获取sim号相关的费用信息
			IDataset simNumberSet = CSAppCall.call("SS.CreatePersonUserSVC.checkSimCardNo", data);
			 if(IDataUtil.isNotEmpty(simNumberSet)){
				     IData simdata = simNumberSet.getData(0);
					if(StringUtils.isNotEmpty(simdata.getString("FEE",""))){
						feeData3.put("FEE_MODE", simdata.getString("FEE_MODE",""));
						feeData3.put("FEE_TYPE_CODE", simdata.getString("FEE_TYPE_CODE",""));
						feeData3.put("FEE", simdata.getString("FEE","0"));
						feeData3.put("FEE_TYPE_DESC", "营业费用");
						feeData3.put("FEE_DESC", "购卡费");
						FeeList.add(feeData3);
			        }
			 }
		}
		
		//获取产品的费用
		String svcName = "SS.CreatePersonUserSVC.getProductFeeInfo";
        IDataset proNumber = CSAppCall.call(svcName, data);
        if(IDataUtil.isNotEmpty(proNumber)){
	        for(int i = 0; i < proNumber.size(); i++) {
		  	     IData proFee = proNumber.getData(i);
		  	     if(IDataUtil.isNotEmpty(proFee)){
							feeData4.put("FEE_MODE", proFee.getString("FEE_MODE",""));
							feeData4.put("FEE_TYPE_CODE",  proFee.getString("FEE_TYPE_CODE",""));
							feeData4.put("FEE",   proFee.getString("FEE","0"));
							feeData4.put("FEE_TYPE_DESC", "预存");
							feeData4.put("FEE_DESC", "预存款(开户缴费)");	
							FeeList.add(feeData4);
					}
		  	}
        }
        
        return FeeList;
	}
	
	/**
	 * 10:开户接口
	 * SS.CreatePersonIntfSVC.openPersonUser
	 * @param data
	 * @throws Exception
	 * @return IData
	 */
	public IData openPersonUser(IData data) throws Exception {
	 
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
		IDataUtil.chkParam(data, "PSPT_ID");
		IDataUtil.chkParam(data, "CUST_NAME");
		IDataUtil.chkParam(data, "PSPT_ADDR");
		IDataUtil.chkParam(data, "USER_TYPE_CODE");
		IDataUtil.chkParam(data, "PHONE");
		IDataUtil.chkParam(data, "PAY_NAME");
		IDataUtil.chkParam(data, "PAY_MODE_CODE");
		IDataUtil.chkParam(data, "ACCT_DAY");
		IDataUtil.chkParam(data, "USER_PASSWD");
		IDataUtil.chkParam(data, "PRODUCT_ID");
		IDataUtil.chkParam(data, "BRAND_CODE");
		
		data.put("TRADE_TYPE_CODE", "10");
		data.put("ORDER_TYPE_CODE", "10");
		data.put("TRADE_DEPART_PASSWD", data.getString("TRADE_DEPART_PASSWD","0"));//孙鑫说没用到，但被校验不能空，所以随便赋值了
		data.put("CITY_CODE", data.getString("TRADE_CITY_CODE"));
		
		
		//费用相关的采用BaseBuilder中的X_TRADE_FEESUB传值了，就不用下面三个参数封装了，由于缺少RES_FEE选号费，在BuildIntfCreatePersonUserRequestData.java中被注释了，所以用不了
		data.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
		data.put("OPER_FEE", "0");//卡费  0 10
		data.put("FOREGIFT", "0");//押金费 1 0
		
		IDataset finalFeeList = data.getDataset("X_TRADE_FEESUB");
		if (finalFeeList == null || finalFeeList.size() <=0 ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"费用列表信息X_TRADE_FEESUB不能为空！");
		}
		
		IDataset finalPayList = data.getDataset("X_TRADE_PAYMONEY");
		if (finalPayList == null || finalPayList.size() <=0 ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"付款列表信息X_TRADE_PAYMONEY不能为空！");
		}
		data.put("PAY_MONEY_CODE", finalPayList.getData(0).getString("PAY_MONEY_CODE"));
		
		if (!"0".equals(data.getString("PAY_MODE_CODE"))) {
			IDataUtil.chkParam(data, "SUPER_BANK_CODE");
			IDataUtil.chkParam(data, "BANK_CODE");
			IDataUtil.chkParam(data, "BANK_ACCT_NO");
		}
		/**
		 * REQ201609280015 关于开户用户名优化的需求
		 * 姓名去空格 前后中间 chenxy3
		 * */
		String custName=data.getString("CUST_NAME"); 
		custName=custName.replaceAll(" +", "");
		//log.info("("***********cxy**********custName=|"+custName+"|");
		data.put("CUST_NAME", custName);
		//实名制验证
     
		//实名制验证结束
		//调用开户接口
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
	
		String svcName = "SS.CreatePersonUserIntfSVC.tradeReg";
        IDataset rtDataset = CSAppCall.call(svcName, data);
        IData  retnData=new  DataMap();
		IData datas = rtDataset.getData(0);
		retnData.put("TRADE_ID", datas.getString("TRADE_ID"));
		return retnData;
	}
	
	/**
	 * 11:获取写卡加密数据接口（获取能够写入白卡的SIM卡的加密后的数据）
	 * SS.CreatePersonIntfSVC.getEncodeSimCardInfo
	 * @param data  
	 * OCX_VERSION    来源于：读卡读到的ngrpsocx.OPS_GetVersion();  或者    ngrpsocx.GetOcxVersion();判断规则见apps\csserv\html\scripts\csserv\component\person\simcard\SimCard.js  getOcxVersion:function()
	 * PHONE_NUM      来源于：【如果PRE_SIM：1是预制卡，则PHONE_NUM=1】；【如果PRE_SIM：0是非预制卡，则根据USIM判断ngrpsocx.isPhoneNumsUsim() : ngrpsocx.isPhoneNums() 同上的SimCard.js  getPhoneNums:function()】
	 * EMPTY_CARD_ID  来源于：cardSn=ngrpsocx.OPS_GetCardSN();    this.EMPTY_CARD_ID=cardSn.substr(2); 即同上的SimCard.js 中 initParam:function()
	 * USIM(可空)      来源于：如果PRE_SIM是0：非预制卡就跟ngrpsocx.CheckCardSim()有关    即同上的SimCard.js 中 initParam:function()  预制卡无该入参
	 * IS_NEW         来源于：IS_NEW=PRE_SIM  //PRE_SIM 0：非预制卡 老卡  1：预制卡 新卡   PRE_SIM来源于ngrpsocx.OPS_GetCardSN()   即同上的SimCard.js 中 initParam:function() 
	 * MODE           固定值：1    为了以后的扩展，如传2等，所以使用入参，不写死在java
	 * SERIAL_NUMBER  开户的手机号码
	 * @throws Exception
	 * @return IDataset
	 * 出参：ENCODE_STR（加密后的写卡数据）  IMSI   SIM_CARD_NO   TRADE_ID(PRE_SIM：1预制卡即新卡才有该值)
	 */
	public IDataset getEncodeSimCardInfo(IData data) throws Exception {
		IDataUtil.chkParam(data, "PHONE_NUM");
		IDataUtil.chkParam(data, "EMPTY_CARD_ID");
		IDataUtil.chkParam(data, "IS_NEW");
		IDataUtil.chkParam(data, "MODE");
		IDataUtil.chkParam(data, "SERIAL_NUMBER");

	    IDataset retnData = CSAppCall.call("CS.WriteCardSVC.beforeWriteCard", data);
	    return retnData;
	}
	
	/**
	 * 12:写卡后校验接口     
	 * 不满足($.simcard.ocx.PRE_SIM==0 || ($.simcard.ocx.PRE_SIM==1 && resultCode==1))才调用该接口
	 * 即是PRE_SIM：1 预制卡  且  resultCode!=1 即成功   即预制卡写卡成功才调用
	 * SS.CreatePersonIntfSVC.checkWriteCardInfo
	 * @param data  
	 * CARD_INFO   来源于ngrpsocx.OPS_GetCardInfo().substring(2) 见SimCard.js 中getCardInfo:function()
	 * RESULT_INFO 来源于 【PRE_SIM：1 ngrpsocx.OPS_WriteCard(str)】  由于只有新卡预制卡成功才调用，所以非预制卡PRE_SIM：0的代码都无用了  参见SimCard.js   writeCard:function(str)
	 * TRADE_ID    来源于11接口SS.CreatePersonIntfSVC.getEncodeSimCardInfo的出参
	 * @throws Exception
	 * @return IDataset
	 * 出参 RESULT_CODE RESULT_INFO
	 */
	public IDataset checkWriteCardInfo(IData data) throws Exception {
		IDataUtil.chkParam(data, "CARD_INFO");
		IDataUtil.chkParam(data, "RESULT_INFO");
		IDataUtil.chkParam(data, "TRADE_ID");

	    IDataset retnData = CSAppCall.call("CS.WriteCardSVC.checkWriteCard", data);
	    return retnData;
	}
	
	/**
	 * 13:写卡后通知资源接口    
	 * SS.CreatePersonIntfSVC.afterWriteSimCard
	 * @param data  
	 * RESULT_CODE    0成功 1失败 【预制卡写卡失败传1 即($.simcard.ocx.PRE_SIM==1 && resultCode==1)】【预制卡成功传12接口的RESULT_CODE出参】 【非预制卡PRE_SIM：0 成功传0 失败传1】
	 * EMPTY_CARD_ID  来源于：cardSn=ngrpsocx.OPS_GetCardSN();    this.EMPTY_CARD_ID=cardSn.substr(2); 即SimCard.js 中 initParam:function()
	 * SERIAL_NUMBER  开户的手机号码
	 * PHONE_NUM      一卡单号 1 或一卡多号 2 来源于：【如果PRE_SIM：1是预制卡，则PHONE_NUM=1】；【否则根据USIM判断ngrpsocx.isPhoneNumsUsim() : ngrpsocx.isPhoneNums() 同上的SimCard.js  getPhoneNums:function()】
	 * SIM_CARD_NO    来源于11接口的出参SIM_CARD_NO
	 * SIM_CARD_NO2
	 * IMSI           来源于11接口的出参IMSI
	 * IMSI2
	 * @throws Exception
	 * @return IDataset
	 * 出参 SIM_CARD_NO IMSI EMPTY_CARD_ID RESULT_CODE
	 */
	public IDataset afterWriteSimCard(IData data) throws Exception {
		IDataUtil.chkParam(data, "RESULT_CODE");
		IDataUtil.chkParam(data, "EMPTY_CARD_ID");
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "PHONE_NUM");
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "IMSI");


	    IDataset retnData = CSAppCall.call("CS.WriteCardSVC.afterWriteCard", data);
	    return retnData;
	}

	//==========add by xingkj3=========================================
	/**
	 * 10:自助终端开户接口
	 * SS.CreatePersonIntfSVC.openPersonUserByAT
	 * @param data
	 * @throws Exception
	 * @return IData
	 */
	public IData openPersonUserByAT(IData data) throws Exception {
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
		IDataUtil.chkParam(data, "PSPT_ID");
		IDataUtil.chkParam(data, "CUST_NAME");
		IDataUtil.chkParam(data, "PSPT_ADDR");
		IDataUtil.chkParam(data, "USER_TYPE_CODE");
		IDataUtil.chkParam(data, "PHONE");
		IDataUtil.chkParam(data, "PAY_NAME");
		IDataUtil.chkParam(data, "PAY_MODE_CODE");
		IDataUtil.chkParam(data, "ACCT_DAY");
		//IDataUtil.chkParam(data, "USER_PASSWD");
		IDataUtil.chkParam(data, "PRODUCT_ID");
		//IDataUtil.chkParam(data, "BRAND_CODE");
		
		/*data.put("TRADE_TYPE_CODE", "10");
		data.put("ORDER_TYPE_CODE", "10");
		data.put("TRADE_DEPART_PASSWD", data.getString("TRADE_DEPART_PASSWD","0"));//孙鑫说没用到，但被校验不能空，所以随便赋值了
		data.put("CITY_CODE", data.getString("TRADE_CITY_CODE"));*/
		
		
		//费用相关的采用BaseBuilder中的X_TRADE_FEESUB传值了，就不用下面三个参数封装了，由于缺少RES_FEE选号费，在BuildIntfCreatePersonUserRequestData.java中被注释了，所以用不了
		/*data.put("ADVANCE_PAY", "-1");//预存款 2 0  为了避免出现多一条记录的情况，所以赋值为-1
		data.put("OPER_FEE", "0");//卡费  0 10
		data.put("FOREGIFT", "0");//押金费 1 0
		*/
		
		
		/*IDataset finalFeeList = data.getDataset("X_TRADE_FEESUB");
		if (finalFeeList == null || finalFeeList.size() <=0 ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"费用列表信息X_TRADE_FEESUB不能为空！");
		}
		
		IDataset finalPayList = data.getDataset("X_TRADE_PAYMONEY");
		if (finalPayList == null || finalPayList.size() <=0 ) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"付款列表信息X_TRADE_PAYMONEY不能为空！");
		}
		data.put("PAY_MONEY_CODE", finalPayList.getData(0).getString("PAY_MONEY_CODE"));
		*/
		if (!"0".equals(data.getString("PAY_MODE_CODE"))) {
			IDataUtil.chkParam(data, "SUPER_BANK_CODE");
			IDataUtil.chkParam(data, "BANK_CODE");
			IDataUtil.chkParam(data, "BANK_ACCT_NO");
		}
		/**
		 * REQ201609280015 关于开户用户名优化的需求
		 * 姓名去空格 前后中间 chenxy3
		 * */
		String custName=data.getString("CUST_NAME"); 
		custName=custName.replaceAll(" +", "");
		//log.info("("***********cxy**********custName=|"+custName+"|");
		data.put("CUST_NAME", custName);
		//实名制验证
     
		//实名制验证结束
		//调用开户接口
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
	
		String svcName = "SS.CreatePersonUserRegSVC.tradeReg";
        IDataset rtDataset = CSAppCall.call(svcName, data);
        IData  retnData=new  DataMap();
		IData datas = rtDataset.getData(0);
		retnData.put("TRADE_ID", datas.getString("TRADE_ID"));
		return retnData;
	}
	/**
	 * 10:物联网开户接口
	 * SS.CreatePersonIntfSVC.openPersonUserByWLW
	 * @param data
	 * @throws Exception
	 * @return IData
	 */
	public IData openPersonUserByWLW(IData data) throws Exception {
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
		IDataUtil.chkParam(data, "PSPT_ID");
		IDataUtil.chkParam(data, "CUST_NAME");
		IDataUtil.chkParam(data, "PSPT_ADDR");
		IDataUtil.chkParam(data, "USER_TYPE_CODE");
		IDataUtil.chkParam(data, "PHONE");
		IDataUtil.chkParam(data, "PAY_NAME");
		IDataUtil.chkParam(data, "PAY_MODE_CODE");
		IDataUtil.chkParam(data, "ACCT_DAY");
		//IDataUtil.chkParam(data, "USER_PASSWD");
		IDataUtil.chkParam(data, "PRODUCT_ID");
		//IDataUtil.chkParam(data, "BRAND_CODE");
		
		//物联网开户 联系号码校验 @yanwu
        String strOpenType = data.getString("OPEN_TYPE"); 
        if( "IOT_OPEN".equals(strOpenType) ){
        	IData putData = new DataMap(); 
        	putData.put("IN_MODE_CODE", "0");
        	putData.put("SERIAL_NUMBER", data.getString("PHONE",""));
        	putData.put("PHONE", data.getString("PHONE",""));
        	putData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        	CSAppCall.call("SS.CreatePersonUserSVC.checkPhone", putData);
        	
        	data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        	CSAppCall.call("SS.ChangeProductIntfSVC.checkPwlwApnNameK", data);
        }
		
		if (!"0".equals(data.getString("PAY_MODE_CODE"))) {
			IDataUtil.chkParam(data, "SUPER_BANK_CODE");
			IDataUtil.chkParam(data, "BANK_CODE");
			IDataUtil.chkParam(data, "BANK_ACCT_NO");
		}
		/**
		 * REQ201609280015 关于开户用户名优化的需求
		 * 姓名去空格 前后中间 chenxy3
		 * */
		String custName=data.getString("CUST_NAME"); 
		custName=custName.replaceAll(" +", "");
		//log.info("("***********cxy**********custName=|"+custName+"|");
		data.put("CUST_NAME", custName);
		//实名制验证
     
		//实名制验证结束
		//调用开户接口
		data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
	
		String svcName = "SS.CreatePersonUserRegSVC.tradeReg";
        IDataset rtDataset = CSAppCall.call(svcName, data);
        IData  retnData=new  DataMap();
		IData datas = rtDataset.getData(0);
		retnData.put("TRADE_ID", datas.getString("TRADE_ID"));
		return retnData;
	}
	/**
	 * 通过产品ID查询包
	 */
	public IDataset queryGroupByProductId(IData dataParam) throws Exception {
		IDataset groups = new DatasetList();
		//获取主产品下的组===========================================
        String productId=dataParam.getString("PRODUCT_ID");
        IData data = new DataMap();
    	data.put("OFFER_CODE", productId);
    	data.put("OFFER_TYPE", "P");
    	IData result = call(null, "UPC.Out.OfferQueryFSV.queryOfferGroupRelOfferId", data);
		IDataset dataset = result.getDataset("OUTDATA");
		
		if (IDataUtil.isNotEmpty(dataset))
        {
            for(int i = 0, isize = dataset.size(); i < isize; i++ )
            {
                IData packageInfo = dataset.getData(i);
                
                packageInfo.put("PRODUCT_ID", productId);// 12位的需要转换成短的
                packageInfo.put("PACKAGE_ID", packageInfo.getString("GROUP_ID"));
                packageInfo.put("PACKAGE_NAME", packageInfo.getString("GROUP_NAME"));
                packageInfo.put("FORCE_TAG", UpcConst.getForceTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                packageInfo.put("DEFAULT_TAG", UpcConst.getDefaultTagForSelectFlag(packageInfo.getString("SELECT_FLAG")));
                packageInfo.put("PACKAGE_TYPE_CODE", packageInfo.getString("GROUP_TYPE"));
                packageInfo.put("LIMIT_TYPE", "");
                packageInfo.put("MIN_NUMBER", packageInfo.getString("MIN_NUM"));
                packageInfo.put("MAX_NUMBER", packageInfo.getString("MAX_NUM"));
                packageInfo.put("RSRV_STR1", "");
            }
        }
        //=========================================================
		
        if(IDataUtil.isNotEmpty(dataset)){
        	groups.addAll(dataset);
        }
        DataHelper.sort(groups, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        //构造用于获取打散商品的组
        if(StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL"))){
            IData group = new DataMap();
            group.put("GROUP_NAME", "其它");
            group.put("GROUP_ID", "-1");
            groups.add(group);
        }
        
		return groups;
	}
	/**
	 * 通过组ID查找元素
	 * @param dataParam
	 * @return
	 * @throws Exception
	 */
	public IDataset queryOfferByGroupId(IData dataParam) throws Exception {
		IDataset children = new DatasetList();
		
		IData data = new DataMap();
    	data.put("GROUP_ID", dataParam.getString("GROUP_ID"));
    	IData result = call(null, "UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", data);
    	children = result.getDataset("OUTDATA");
		
    	ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), children);
		DataHelper.sort(children, "OFFER_TYPE",IDataset.TYPE_STRING,IDataset.ORDER_DESCEND,"OFFER_CODE", IDataset.TYPE_INTEGER,IDataset.ORDER_ASCEND);
		
		if(StringUtils.isEmpty(dataParam.getString("QRY_TAG"))
				||"0".equals(dataParam.getString("QRY_TAG"))){
			for (Iterator iterator = children.iterator(); iterator.hasNext();)
		        {
		            IData item = (IData) iterator.next();
		            
		            String offerType = StaticUtil.getStaticValue("OFFER_LIST_NODISPLAY", item.getString("OFFER_CODE"));
		            
		            if (item.getString("OFFER_TYPE","").equals(offerType))
		            {
		                iterator.remove();
		            } 
	        }
		}
		
		return children;
	}
	/**
	 * 身份证黑名单校验
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData checkPsptidBlackListInfo(IData data) throws Exception {
		String psptid=data.getString("PSPT_ID","");
        String tradeTypeCode=data.getString("BLACK_TRADE_TYPE","");
        IData param = new DataMap();
        param.putAll(data);
        String code="0";//编码，0=非黑名单； 9=黑名单用户
    	String msg="本次办理不成功。您的证件名下的手机号码";//黑名单用户拼串：本次办理不成功。您的证件名下的手机号码*******已欠费*元，请及时缴费。以便正常使用该证件办理业务
    	
    	IDataset results =  CSAppCall.call("SS.CreatePersonUserSVC.checkPsptidBlackListInfo", data);
    	 String fee="0";
         String feeOther="";
         if (IDataUtil.isNotEmpty(results) )
         {
         	for(int i=0;i<results.size();i++){
         		IData blackInfo=results.getData(i);
         		String owe_sn=blackInfo.getString("SERIAL_NUMBER","");
         		String owe_fee=blackInfo.getString("OWE_FEE","0");
         		if(owe_fee!=null && !"0".equals(owe_fee)){
         			code="9";
         			msg+=""+owe_sn+"已欠费"+owe_fee+"元。"; 
         			feeOther=feeOther+"号码"+owe_sn+"欠费"+owe_fee+"元;";
         		}
         	} 
         	if("9".equals(code)){
         		msg+="请及时缴费，以便正常使用该证件办理业务。";
         		if(!"".equals(tradeTypeCode)&&!"undefined".equals(tradeTypeCode)){
 	        		//插黑名单日志表
 	        		IData insData=new DataMap();
 	        		insData.put("USER_ID",""); 
 	            	insData.put("SERIAL_NUMBER","");   
 	            	insData.put("PSPT_ID",psptid);         
 	            	insData.put("IN_MODE_CODE",getVisit().getInModeCode());     
 	            	insData.put("UPDATE_STAFF_ID",getVisit().getStaffId());  
 	            	insData.put("UPDATE_DEPART_ID",getVisit().getDepartId());
 	            	insData.put("TRADE_TYPE_CODE",tradeTypeCode);  
 	            	insData.put("FEE", fee);              
 	            	insData.put("OTHER_FEE", feeOther);  
 	            	CSAppCall.call("SS.BlackUserManageSVC.insertBlackUserCheckLogInfo", insData);
        		}
        	}
        }
        param.put("BLACKCODE", code);
        param.put("BLACKMSG", msg);
        return param;
	}
	/**
	 * 业务校验规则
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData checkBusinessRule(IData data) throws Exception {
		String sn=data.getString("SERIAL_NUMBER","");
		if("310".equals(data.getString("TRADE_TYPE_CODE"))){//复机
			IData result=this.queryUserByDestroy(data);
			data.putAll(result.getData("USER_INFO"));
		}else{
			UcaData uca=UcaDataFactory.getNormalUca(sn);
			data.put("USER_ID", uca.getUserId());
			data.put("CUST_ID", uca.getCustId());
			data.put("PRODUCT_ID", uca.getProductId());
			data.put("BRAND_CODE", uca.getBrandCode());
		}
		IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade", data);
		return infos.getData(0);
	}
	/**
	 * 查询可选占号码
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryNumInfo4AreaSel(IData param) throws Exception {
		IDataset datas= ResCall.queryNumInfo4AreaSel(param);
		
		if(StringUtils.isEmpty(param.getString("pageSize"))
				&&StringUtils.isEmpty(param.getString("pageNumber"))){
			return datas;
		}else{
			int pageSize=Integer.parseInt(param.getString("pageSize"));
			int pageNumber=Integer.parseInt(param.getString("pageNumber"));
			int start=(pageNumber-1)*pageSize;
			int end=start+pageSize;
			if(datas.size()>0){
				if(datas.size()<end){
					end=datas.size();
				}
				IDataset newDatas =  new DatasetList();
				for(int i=start;i<end;i++){
					IData data = (DataMap)datas.get(i);
					data.put("pageCount", datas.size());
					newDatas.add(data);
				}
				return newDatas;
			}else{
				return datas;
			}
		}
	}
	private final static IData call(IBizCommon bc,String svcName, IData input) throws Exception{
        return call(bc, svcName, input, null, true);
    }
	private final static IData call(IBizCommon bc,String svcName, IData input, Pagination pagin, boolean iscatch) throws Exception{

        ServiceResponse response = BizServiceFactory.call(svcName, input, pagin);
        IData out = response.getBody();
        if(pagin != null)
            out.put("X_COUNTNUM", response.getDataCount());
        
        return out;
        
    }
	
	//================营销活动接口=================================
	public IData querySaleActives(IData data) throws Exception {
		String productId=data.getString("PRODUCT_ID");
		String newImei=data.getString("NEW_IMEI"); 
		String searchContent=data.getString("SEARCH_CONTENT");
		String isIphone6=data.getString("IS_IPHONE6");
		String iphone6Imei=data.getString("IPHONE6_IMEI");
		
		IDataset saleactives = new DatasetList();
		if (StringUtils.isNotBlank(productId) || StringUtils.isNotBlank(newImei) || StringUtils.isNotBlank(searchContent)|| StringUtils.isNotBlank(isIphone6)|| StringUtils.isNotBlank(iphone6Imei)){
			String querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActives";
            if (StringUtils.isNotBlank(newImei))
            {
                querySaleActivesSvc = "CS.SaleActiveQuerySVC.querySaleActivesByImei";
            }
            saleactives = CSAppCall.call(querySaleActivesSvc, data);
            //如果是IPHONE6的 则进行处理 caolin 20141016
            if(StringUtils.isNotBlank(isIphone6)){
            	saleactives=iphone6Deal(saleactives, productId, isIphone6, iphone6Imei,data);
            }
		}
		IData retData = new DataMap();
	    for (int i = saleactives.size() - 1; i >= 0; i--)
         {
            IData saleactive = saleactives.getData(i);
            if ("true".equals(saleactive.getString("OTHER_INFO")))
            {
            	//retData.put("OTHER_INFOS", saleactive);
            	retData.put("ALL_FAIL", saleactive.getString("ALL_FAIL"));
            	IData goodInfo=saleactive.getData("GOODS_INFO");
            	if(goodInfo!=null){
            		retData.put("BATTERY", goodInfo.getString("BATTERY",""));
            		retData.put("COLOR", goodInfo.getString("COLOR",""));
            		retData.put("DEVICE_BRAND", goodInfo.getString("DEVICE_BRAND",""));
            		retData.put("DEVICE_MODEL", goodInfo.getString("DEVICE_MODEL",""));
            		//retData.put("TERMINAL_DETAIL_INFO", goodInfo.getString("TERMINAL_DETAIL_INFO",""));
            	}
            	
                saleactives.remove(i);
                continue;
            }
            saleactive.put("PACKAGE_NAME", saleactive.getString("PACKAGE_NAME") + "|" + saleactive.getString("PACKAGE_ID"));
        }
	    retData.put("SALE_ACTIVES", saleactives);
	    return retData;
	}
	 /**
	 *根据IMEI情况显示免预存还是预存IPHONE6营销包 caolin 20141016 
	 */
	public IDataset iphone6Deal(IDataset saleActives, String productId, String isIphone6, String iphone6Imei,IData data) throws Exception {
		//输入IPHONE6IMEI号，调接口校验
		if ("1".equals(isIphone6)) {
			//查询该产品的SALE_TAG值，调用华为的接口校验
			IData product =CSAppCall.call("CS.ProductInfoQrySVC.getProductByPK", data).first();
			data.put("IPHONE6_IMEI", iphone6Imei);
			data.put("SALE_TAG", product.getString("RSRV_TAG1",""));
			IData iphone6CheckIMEISaleInfoResults =CSAppCall.call("CS.SaleActiveQuerySVC.iphone6CheckIMEISaleInfo", data).first();
			String x_resultCode = iphone6CheckIMEISaleInfoResults.getString("X_RESULTCODE");
			//输入的IMEI符合免预存条件,校验机型
			if (StringUtils.equals(x_resultCode, "0")) {//华为测接口文档有误，0为成功，其他失败
				String terminal_type_code = iphone6CheckIMEISaleInfoResults.getString("TERMINAL_TYPE_CODE");
				String device_model_code = iphone6CheckIMEISaleInfoResults.getString("DEVICE_MODEL_CODE");
				return this.filterTerminalSalePackages(saleActives,terminal_type_code,device_model_code,data.getString("EPARCHY_CODE"));
			}
			if(IDataUtil.isNotEmpty(saleActives)){
				saleActives.getData(saleActives.size()-1).put("IPHONE6_X_RESULTCODE", "-1");
				saleActives.getData(saleActives.size()-1).put("IPHONE6_X_RESULTINFO", "校验结果:["+iphone6CheckIMEISaleInfoResults.getString("X_RESULTINFO")+"],只能办理预存活动！");
			}
		}
		//不输入IPHONE6IMEI号,或者输入的IMEI没有免预存条件的活动，过滤掉免预存的
		return this.filterFreeStoredSalePackages(saleActives);
	}
	/**
	 * 根据华为查找的终端信息 查找能办理的营销活动
	 */
	private IDataset filterTerminalSalePackages(IDataset salePackages,String terminal_type_code, String device_model_code,String eparchy_code) throws Exception
    {
        if (IDataUtil.isNotEmpty(salePackages)) {
			IDataset iphone6PackageList = new DatasetList();
			for (int i = salePackages.size() - 1; i >= 0; i--) {
				IData salePackage = salePackages.getData(i);
				String package_type = salePackage.getString("PACKAGE_TYPE");
				String package_terminal_type_code = salePackage.getString("RSRV_STR5");
				String package_device_model_code = salePackage.getString("RSRV_STR2");
				String productId = salePackage.getString("PRODUCT_ID");
				String packageId = salePackage.getString("PACKAGE_ID");
				if (StringUtils.equals("IS_IPHONE6", package_type)) {
					// 1、只有deviceModelCode机型才能办理该营销包
					if (!"ZZZZ".equals(package_device_model_code)&& device_model_code.equals(package_device_model_code)) {
						iphone6PackageList.add(salePackage);
					}
					// 2、某一类终端下的所有机型都可以办理该活动，但是需要排除td_B_sale_terminal_limit的当前产品和包下的terminal_type_code下的terminal_model_code机型。
					else if ("ZZZZ".equals(package_device_model_code)&& StringUtils.isNotBlank(package_terminal_type_code)) {
						if (package_terminal_type_code.contains("|"	+ terminal_type_code + "|")) {
							IData inputData = new DataMap();
							inputData.put("PRODUCT_ID", productId);
							inputData.put("PACKAGE_ID", packageId);
							inputData.put("TERMINAL_TYPE_CODE", terminal_type_code);
							inputData.put("DEVICE_MODEL_CODE", device_model_code);
							inputData.put("EPARCHY_CODE", eparchy_code);
							IDataset terminalLimitList = CSAppCall.call("CS.SaleActiveQuerySVC.getSaleTerminalLimitInfo", inputData);
							if (IDataUtil.isEmpty(terminalLimitList)) {
								iphone6PackageList.add(salePackage);
							}
						}
					}
					//3、多个终端机型都可以办理该营销包，营销包与机型的对应关系为：TD_B_SALE_TERMINAL_LIMIT的当前产品和包下，且TERMINAL_TYPE_CODE='0'下的terminal_model_code机型。
					else if ("ZZZZ".equals(package_device_model_code)&& StringUtils.isBlank(package_terminal_type_code)) {
						IData inputData = new DataMap();
						inputData.put("PRODUCT_ID", productId);
						inputData.put("PACKAGE_ID", packageId);
						inputData.put("TERMINAL_TYPE_CODE", "0");
						inputData.put("DEVICE_MODEL_CODE", device_model_code);
						inputData.put("EPARCHY_CODE", eparchy_code);
						IDataset terminalLimitList = CSAppCall.call("CS.SaleActiveQuerySVC.getSaleTerminalLimitInfo", inputData);
						if (IDataUtil.isNotEmpty(terminalLimitList)) {
							iphone6PackageList.add(salePackage);
						}
					}
					salePackages.remove(i);
				}
			}
			// 有符合条件的营销活动，则返回符合条件的
			if (IDataUtil.isNotEmpty(iphone6PackageList)) {
				return iphone6PackageList;
			}
			if(IDataUtil.isNotEmpty(salePackages)){
				// 没有，则返回默认的营销活动（即预存的）
		        salePackages.getData(salePackages.size()-1).put("IPHONE6_X_RESULTCODE", "-1");
		        salePackages.getData(salePackages.size()-1).put("IPHONE6_X_RESULTINFO", "没有该机型匹配的营销活动，只能办理预存活动！");
			}
		}
		
		return salePackages;
    }
	/**
	 * 过滤掉免预存的营销活动
	 */
	private IDataset filterFreeStoredSalePackages(IDataset salePackages) throws Exception
    {
        if (IDataUtil.isNotEmpty(salePackages)){
        	for (int i = salePackages.size() - 1; i >= 0; i--) {
    			IData salePackage = salePackages.getData(i);
    			String package_type = salePackage.getString("PACKAGE_TYPE");
    			if (StringUtils.equals("IS_IPHONE6",package_type)) {
    				salePackages.remove(i);
    			}
    		}
        }
        return salePackages;
    }
	
	//================无手机宽带开户接口=================================
	public IData queryNoPhoneProduct(IData data) throws Exception {
		IDataUtil.chkParam(data, "wideProductType");
		data.put("ROUTE_EPARCHY_CODE", "0898");
		
		IData resultData = CSAppCall.callOne("SS.NoPhoneWideUserCreateSVC.getWidenetProductInfoByWideType", data);
        
        IDataset wideModemStyle  = resultData.getDataset("WIDE_MODEM_STYLE");
        
        IDataset productList  = resultData.getDataset("PRODUCT_LSIT");
        
        //获取魔百和信息
        IDataset topSetBoxProducts = CSAppCall.call( "SS.NoPhoneWideUserCreateSVC.loadTopSetBoxInfo", data);
       
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), productList);
        
		IData retData = new DataMap();
		
		retData.put("wideModemStyle", wideModemStyle);
		retData.put("productList", productList);
		if (IDataUtil.isNotEmpty(topSetBoxProducts))
        {
			retData.put("topSetBoxProducts", topSetBoxProducts);
        }
	    
	    return retData;
	}
	/**
	 * 查询最新的用户信息复机使用
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData queryUserByDestroy(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataset userList = new DatasetList();
		 // 查询在线用户
        IDataset netUserDataset = UserInfoQry.getUsersBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(netUserDataset))
        {
        	userList.addAll(netUserDataset);
        }
        // 查询历史用户
        IDataset hisUserDataset = UserInfoQry.qryAllUserInfoBySnFromHis(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(hisUserDataset))
        {
        	userList.addAll(hisUserDataset);
        }
        IData userData=null;
        if (IDataUtil.isNotEmpty(userList)){
            for(int i = 0, count = userList.size(); i < count; i++){
            	if(i==0){
            		userData=userList.getData(i);
            	}else{
            		if(SysDateMgr.getTimeDiff(userData.getString("OPEN_DATE"), userList.getData(i).getString("OPEN_DATE"),SysDateMgr.PATTERN_STAND)>0){
            			userData=userList.getData(i);
            		}
            	}
            }
        }
        if(IDataUtil.isNotEmpty(userData)){
        	IData params=new DataMap();
        	params.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        	params.put("USER_ID", userData.getString("USER_ID"));
        	params.put("TRADE_TYPE_CODE", "310");
        	IDataset results = CSAppCall.call("CS.GetInfosSVC.getUCAInfos", params);
        	return results.getData(0);
        }
        return null;
	}
	/**
	 * 复机接口（原号复机）
	 */
	public IData restoreUser(IData input) throws Exception {
		input=new DataMap(input.toString());
		
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "PRODUCT_ID");
		IDataUtil.chkParam(input, "OLD_SIMCARD_NO");
		IDataUtil.chkParam(input, "NEW_SIMCARD_NO");
		
		//组织号卡信息
		IDataset resInfos=new DatasetList();
		IData numberData=new DataMap();
		numberData.put("col_RES_TYPE_CODE","0");
		numberData.put("col_RES_CODE", input.getString("SERIAL_NUMBER"));
		numberData.put("col_OLD_RES_CODE", input.getString("SERIAL_NUMBER"));
		resInfos.add(numberData);
		IData simData=new DataMap();
		simData.put("col_RES_TYPE_CODE","1");
		simData.put("col_RES_CODE", input.getString("NEW_SIMCARD_NO"));
		simData.put("col_OLD_RES_CODE", input.getString("OLD_SIMCARD_NO"));
		resInfos.add(simData);
		
		input.put("X_CODING_STR", resInfos.toString());
		input.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE","310"));
		input.put("ROUTE_EPARCHY_CODE", input.getString("ROUTE_EPARCHY_CODE","0898"));
		input.put("EPARCHY_CODE", input.getString("EPARCHY_CODE","0898"));
		
		IDataset dataset = CSAppCall.call("SS.RestorePersonUserRegSVC.tradeReg", input);
		return dataset.getData(0);
	}
	//==================================================跨区业务=========================================
	/**
	 * 跨区销户提交接口
	 */
	public IData remoteDestroyUser(IData data) throws Exception {
		data.put("PSPT_TYPE_CODE", "0");
		data.put("PSPT_ID", data.getString("IDCARDNUM"));
		String serialNumber = data.getString("MOBILENUM");
		IDataset resultInfo =CSAppCall.call("SS.RemoteDestroyUserRegSVC.tradeReg", data);
		if(IDataUtil.isEmpty(resultInfo)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_595);
		}
		String orderId = resultInfo.first().getString("ORDER_ID");
        String tradeId = resultInfo.first().getString("TRADE_ID");
        String sid=SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        orderId=orderId.substring(0, orderId.length()-4);
		data.put("REMOTE_ORDER_ID", "COP898" + sid + orderId);
        data.put("TRADE_ID", tradeId);
        CSAppCall.call("SS.RemoteDestroyUserSVC.applySubmitCancel", data);
        CSAppCall.call("SS.RemoteDestroyUserSVC.insPsptFrontBack", data);
        IData queryParam = new DataMap();
        queryParam.put("REMOTE_ORDER_ID", data.getString("REMOTE_ORDER_ID"));
        IDataset destroyOrders = CSAppCall.call("SS.RemoteDestroyUserSVC.queryApplyDestroyUserTradeByOrderId", queryParam);
        if(IDataUtil.isNotEmpty(destroyOrders)){
  			IData data1 = new DataMap();
  			data1.put("REMOTE_SERIAL_NUMBER", serialNumber);
  			String remoteOrderId = destroyOrders.getData(0).getString("REMOTE_ORDER_ID", "");
  			data1.put("REMOTE_ORDER_ID", remoteOrderId);
  			CSAppCall.call("SS.RemoteDestroyUserSVC.applyCancelAccount", data1);
        }else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"无派单数据");
  		}
        return resultInfo.getData(0);
	}
	/**
	 * 异地补换卡查询卡类型
	 */
	public IData queryRemoteWriteCardType(IData data) throws Exception {
		 String changeTag = data.getString("CHANGE_CARD_TAG");
		 if ("2".equals(changeTag)) {
			 data.put("BIZ_TYPE", "1016");
		 }else {
			 data.put("BIZ_TYPE", "1012");
		 }
		 data.put("SERIAL_NUMBER", data.getString("MOBILENUM"));
		 data.put("ID_ITEM_RANGE", data.getString("MOBILENUM"));
		 data.put("IDTYPE", "01");
		 IDataset cardType = CSAppCall.call("SS.RemoteResetPswdSVC.queryCardType", data);
		 if("1".equals(cardType.first().getString("result"))) {
		    CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省号码无法办理异地业务！");
         }
		 if(IDataUtil.isNotEmpty(cardType)){
			 IData cardInfo = cardType.getData(0);
			 String sCardType = cardInfo.getString("CARD_TYPE");
			 String isJXH = cardInfo.getString("IS_JXH");
			 String cardName = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", 
					 new String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
	    			"PARAM_NAME", 
	    			new String[]{"CSM","149","CARDTYPE",sCardType});
	    	 //对卡类型的处理配在数据库中  2不可办理 1提示信息但可办理 0可以办理
	    	 String cardRetn = StaticUtil.getStaticValue(getVisit(),"TD_S_COMMPARA", 
	    			 new String[]{"SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
	    			"PARA_CODE2", 
	    			new String[]{"CSM","149","CARDTYPE",sCardType});
	    	 if(StringUtils.isEmpty(cardName)){
	    		//没查到数据就设置默认值
	    		data.put("CARD_NAME", "非标准实体卡");
	    		data.put("CARD_RETN", "2");
	    	 }else{
	    		data.put("CARD_NAME", cardName);
	    		data.put("CARD_RETN", cardRetn);//2拦截 1提示 0可以办理
	    	 }
			 data.putAll(cardInfo);
			 if("0".equals(isJXH)&&!"2".equals(changeTag)){//好友号码查询 
				IDataset friendCounts = CSAppCall.call("SS.RemoteResetPswdSVC.numCheckQuery", data);
				if(IDataUtil.isNotEmpty(friendCounts)){
					IData friendCount = friendCounts.getData(0);
					String rspCode = friendCount.getString("RSP_CODE");
					String count = friendCount.getString("NUM_COUNT");
					if ("00".equals(rspCode)) {
						data.put("NUM_COUNT", count);
					}
				}
			}
		 }else{
			 CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询卡类型无数据！");
		 }
         return data;
	}
	/**
	 * 异地补换卡查询客户资料
	 */
	public IData queryRemoteWriteCardCustInfo(IData data) throws Exception {
		 data.put("BIZ_TYPE", "1012");
         data.put("SERIAL_NUMBER", data.getString("MOBILENUM"));
         data.put("ID_ITEM_RANGE", data.getString("MOBILENUM"));
         data.put("IDTYPE", "01");
         IDataset cardType =CSAppCall.call("SS.RemoteResetPswdSVC.queryCardType", data);
         if("1".equals(cardType.first().getString("result"))) {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省号码无法办理异地业务！");
         }
         IDataset custInfos =CSAppCall.call("SS.RemoteWriteCardSVC.queryRemoteWriteCustomer", data);
         if (IDataUtil.isEmpty(custInfos)) {
        	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户资料（异地）为空！");
         }
         IData custInfo = custInfos.getData(0);
         if (!"00".equals(custInfo.getString("USER_STATE_CODESET"))) {
             if ("02".equals(custInfo.getString("USER_STATE_CODESET"))) {
            	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户处于停机状态，不能办理此业务！");
             } else {
            	 CSAppException.apperr(CrmCommException.CRM_COMM_103, "530000:非正常在网用户不能办理远程写卡业务！");
             }
         }
         IData custInfo1 = new DataMap();
         custInfo1.putAll(data);
         custInfo1.putAll(custInfo);
         IDataset outputq =CSAppCall.call("SS.RemoteWriteCardSVC.getStaticValue", custInfo1);
         IData custInfo12 = outputq.getData(0);
         custInfo.putAll(custInfo12);
        
         if("0".equals(cardType.getData(0).getString("IS_JXH"))){
         	custInfo.put("ISJXH", "是");
         }else{
         	custInfo.put("ISJXH", "否");
         }
         String sCardType = cardType.getData(0).getString("CARD_TYPE");
         IData authCustInfo = custInfo.getData("AUTH_CUST_INFO", new DataMap());
         authCustInfo.put("IS_JXH", cardType.getData(0).getString("IS_JXH"));
         authCustInfo.put("IS_SHIMING", cardType.getData(0).getString("IS_SHIMING"));
         authCustInfo.put("PROVINCE_CODE", custInfo.getString("COP_SI_PROV_CODE"));
         String cardName = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
                    { "SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
                    "PARAM_NAME", 
                    new String[]{ "CSM","149","CARDTYPE",sCardType });
        //对卡类型的处理配在数据库中  2不可办理 1提示信息但可办理 0可以办理
         String cardRetn = StaticUtil.getStaticValue(getVisit(), "TD_S_COMMPARA", new String[]
                     { "SUBSYS_CODE","PARAM_ATTR","PARAM_CODE","PARA_CODE1"}, 
                     "PARA_CODE2", 
                       new String[]{ "CSM","149","CARDTYPE",sCardType });
         if(StringUtils.isEmpty(cardName)){
     		//没查到数据就设置默认值
     		authCustInfo.put("CARD_NAME", "非标准实体卡");
     		authCustInfo.put("CARD_RETN", "2");
     	}else{
     		//如果查到为2的数据则不设置查询后页面信息 不能继续办理
     		if(!"2".equals(cardRetn)){
     			//authCustInfo.put("CUST_INFO", custInfo);
     		}
     		authCustInfo.put("CARD_NAME", cardName);
     		authCustInfo.put("CARD_RETN", cardRetn);//2拦截 1提示 0可以办理
     	}
        return authCustInfo;
	}
	/**
	 * 跨区补换卡写卡数据加密
	 */
	public IData encryRemoteWriteCardData(IData data) throws Exception {
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "EMPTY_CARD_ID");
		data.put("PARAMS", data.toString());
		IDataset results=CSAppCall.call("SS.RemoteWriteCardSVC.beforeWriteCard", data);
		return results.getData(0);
	}
	/**
	 * 跨区补换卡激活提交
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData activeRemoteWriteCardUser(IData data) throws Exception {
		IData custInfo=new DataMap();
		custInfo.put("IDCARDTYPE", data.getString("IDCARDTYPE",""));
		custInfo.put("IDCARDNUM", data.getString("IDCARDNUM",""));
		custInfo.put("CUST_NAME", data.getString("CUST_NAME",""));
		custInfo.put("SCORE", data.getString("SCORE",""));
		custInfo.put("CONTACT_ADDRESS", data.getString("CONTACT_ADDRESS",""));
		custInfo.put("CONTACT_PHONE", data.getString("CONTACT_PHONE",""));
		custInfo.put("BRAND_CODE", data.getString("BRAND_CODE",""));
		custInfo.put("OPEN_DATE", data.getString("OPEN_DATE",""));
		data.put("AUTH_CUST_INFO", custInfo);
	    data.put("SERIAL_NUMBER", data.getString("MOBILENUM"));
	    
	    //第一步，发起方空卡处理包括资源预占，ki,opc解密
	    IDataset resinfos = CSAppCall.call("SS.RemoteWriteCardSVC.applyResultActiveCallRes", data);
        if (resinfos != null && resinfos.size() > 0 && resinfos.getData(0).getString("X_RESULTCODE", "").trim().equals("0")) {
            System.out.println("RemoteWriteCardSingle.javaxxxxxxxxxxxxxxxxxxxx150(onTradeSubmit) " + resinfos.getData(0));
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resinfos.getData(0).getString("X_RESULTINFO", "").trim());
        }
        
        //第三步，人像比对受理单编号与照片编号同步接口调用
        CSAppCall.call("SS.RemoteWriteCardSVC.SynPicId", data);
        
        //第四步，调用接口通知归属省跨区补卡
        data.put("KI", resinfos.getData(0).getString("KI", "").trim());
        data.put("OPC", resinfos.getData(0).getString("OPC", "").trim());        
        IDataset resultInfo = CSAppCall.call("SS.RemoteWriteCardSVC.applyResultActive", data);
        if (resultInfo == null || resultInfo.isEmpty() || !"0000".equals(resultInfo.getData(0).getString("X_RSPCODE", ""))) {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "跨区写卡激活失败："+resultInfo.getData(0).getString("X_RSPDESC", ""));
        }
       
        data.put("HOME_PROV", data.getString("PROVINCE_CODE","").trim());
        //跨区补卡流程优化：前台操作写卡成功激活提交时，若调用网状网激活接口失败自动返销已经生成的换卡工单，调用资源返销已用卡资源（采用将第二步后移到调用iboss之后）
        //第二步，发起方登记台账，如果有问题还可以直接事务回退，中止业务受理
        IDataset tradeInfo = CSAppCall.call("SS.RemoteWriteCardRegSVC.tradeReg", data);
        data.put("TRADE_ID", tradeInfo.getData(0).getString("TRADE_ID", ""));
        
        //第五步，归属省补卡业务成功受理后，记录业务发起方实名认证信息        
        CSAppCall.call("SS.RemoteWriteCardSVC.logRealNameInfo", data);
        
        return tradeInfo.getData(0);
	}
}