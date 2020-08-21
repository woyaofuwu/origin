
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.ResTypeEnum;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * REQ201505210004关于新增FTTH光猫办理流程的需求
 * chenxy3
 * 2015-6-8
 * 入参：手机号码SERIAL_NUMBER、光猫串号MODERM_NUMBER
 * 返回：0成功，其他失败。
 * */
public class FTTHModemManageSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(FTTHModemManageSVC.class);

    private static final long serialVersionUID = 1L;

    public IData updModemNumber(IData userInfo) throws Exception
    {
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	IData returnInfo = new DataMap();
    	String tradeId = userInfo.getString("TRADE_ID");
		String rsrv_value_code = "FTTH";
		
		String tradeTypeCode = "";
		
		IData tradeBhInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", getTradeEparchyCode());
		
		if (IDataUtil.isNotEmpty(tradeBhInfo))
		{
		    tradeTypeCode = tradeBhInfo.getString("TRADE_TYPE_CODE","");
		}
		
    	IDataset userOtherInfos=TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
    	
    	//无手机宽带宽带走资料表
    	if(DataSetUtils.isNotBlank(userOtherInfos) && !"680".equals(tradeTypeCode))
    	{
        	IDataset userInfos = new DatasetList();
    		int tradeSize = userOtherInfos.size();
        	for(int i = 0 ; i < tradeSize ; i++){
        		if("0".equals(userOtherInfos.getData(i).getString("MODIFY_TAG"))){
        			userInfos.add(userOtherInfos.getData(i));
        		}
        	}
        	if(userInfos!=null && userInfos.size()>0){ 
             	String moderm=userInfos.getData(0).getString("RSRV_STR1");
             	if(moderm!=null && !"".equals(moderm)){
             		returnInfo.put("X_RESULTCODE", "6131");
                     returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】已存在光猫信息！");
             	}else{
             		bean.updModemNumber(userInfo);
                 	returnInfo.put("X_RESULTCODE", "0");
                     returnInfo.put("X_RESULTINFO", "办理成功!"); 
             	}
             }else{
             	returnInfo.put("X_RESULTCODE", "6130");
                 returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】还没有办理光猫申领业务!"); 
             }    	
    	}
    	else
    	{
    	    IData inParam = new DataMap(userInfo);
    	    
    	    if ("680".equals(tradeTypeCode))
    	    {
    	        inParam.put("SERIAL_NUMBER", "KD_"+inParam.getString("SERIAL_NUMBER"));
    	    }
    	    
    	    IDataset userInfos=FTTHModemManageBean.getOldUserModermInfo(inParam);
    	    
    		if(userInfos!=null && userInfos.size()>0)
    		{ 
   	        	String moderm=userInfos.getData(0).getString("RSRV_STR1");
   	        	
   	        	if(moderm!=null && !"".equals(moderm))
   	        	{
   	        		returnInfo.put("X_RESULTCODE", "6131");
   	                returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】已存在光猫信息！");
   	        	}
   	        	else
   	        	{
   	        		FTTHModemManageBean applyBean= BeanManager.createBean(FTTHModemManageBean.class);
   	        		applyBean.updOldModemNumber(inParam);
   	            	returnInfo.put("X_RESULTCODE", "0");
   	                returnInfo.put("X_RESULTINFO", "办理成功!"); 
   	        	}
   	        }
    		else
    		{
   	        	returnInfo.put("X_RESULTCODE", "6130");
   	            returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】还没有办理光猫申领业务!"); 
   	        }    	
    	}
        return returnInfo;
    }

    
    /**
     * AEE定时任务
     * 开户失败 
     * */
    public void checkWilenFailUser(IData userInfo) throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	bean.checkWilenFailUser(userInfo);
    }
    
    /**
     * 开户撤单光猫、魔百和处理
     * @param inParam
     * @throws Exception
     * @author lijun17 2016-5-31
     */
    public IDataset checkWidenetWilenFailUser(IData param)throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	return bean.checkWidenetWilenFailUser(param);
    }
    
    /**
     * AEE定时任务
     * 开户失败 --返还光猫单独处理接口
     * SS.FTTHModermApplySVC.returnFtthModem
     * 用法：先检查TF_BH_TRADE_FTTH表是否存在需要退的TRADE_ID，存在就改标记成5，不存在就插入一条标记5的TRADE_ID记录。
     * */
    public void returnFtthModem(IData userInfo) throws Exception{
    	IData param=new DataMap(); 
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	bean.getTempFtthUserReturnModem(param);
    }
    /**
     * AEE定时任务
     * 满3年用户
     * 修改查询条件	rsrv_tag1:0  申领模式  0租赁，1购买，2赠送 
     * 				rsrv_tag2:1  光猫状态  1:申领，2:更改，3:退还，4:丢失		
     * 				rsrv_str7:0  押金状态  0,押金、1,已沉淀、2已退还		
     * */
	public void checkThreeYearsUser(IData userInfo) throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	bean.checkThreeYearsUser(userInfo);
	}
    
    /**
     * AEE定时任务
     * 移机、拆机光猫90天未归还押金沉淀处理
     * */
    public void checkThreeMonthNotReturnModermUser(IData userInfo) throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	bean.checkThreeMonthNotReturnModermUser(userInfo);
    }
   
    /**
     * REQ201510260003 光猫申领提示优化【2015下岛优化】
     * 判断是否FTTH宽带用户。
     * chenxy3 20151027
     * */
    public IData checkIfFtthNewUser(IData inParam) throws Exception{
    	IData rtnData=new DataMap();
    	 
    	boolean isExist=false; 
		IDataset wilens=FTTHModemManageBean.getUserFTTHProd(inParam);
		if(wilens!=null && wilens.size()>0){
			isExist=true;
		}else{
			wilens=FTTHModemManageBean.getUserFTTHProdWWG(inParam);
			if(wilens!=null && wilens.size()>0){
				isExist=true;
			}
		}
		rtnData.put("IS_FTTH_USER", isExist);
		return rtnData;
    }
    
    
    /**
     * REQ201510260003 光猫申领提示优化【2015下岛优化】
     * 判断是否宽带1+产品
     * chenxy3 20151027
     * */
    public IData checkProdInCommpara(IData inParam) throws Exception{
    	IData rtnData=new DataMap();
    	String inTag="0";
    	String productId=inParam.getString("PRODUCT_ID","");  
    	IDataset commparas=CommparaInfoQry.getCommparaAllColByParser("CSM","1025", productId, "0898");
    	if(commparas!=null && commparas.size()>0){
    		inTag="1";
    	}
    	rtnData.put("IN_TAG", inTag);
    	return rtnData;
    }
    
    /**
     * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
     * 判断用户该收取的押金金额
     * chenxy3 20151029
     * */

    public IData checkFTTHdeposit(IData inParam) throws Exception{
      	IData rtnData=new DataMap();
      	String deposit="";
      	String serialNumber=inParam.getString("SERIAL_NUMBER","");
      	String applyType = inParam.getString("APPLYTYPE","");
      	if(StringUtils.isBlank(applyType)){
      		rtnData.put("APPLYTYPE","");
          	rtnData.put("DEPOSIT","");
          	rtnData.put("X_INFO","2");
          	return rtnData;
      	}else{
      		if("1".equals(applyType)){//申领模式为购买，查询购买金额
      			IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", "3", "0898");
  	      		deposit=paras.getData(0).getString("PARA_CODE1");
      		}else if("0".equals(applyType)){//租赁
      			FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
          		IData param=new DataMap();
          		param.put("SERIAL_NUMBER", serialNumber);
          		String payType=bean.getPayMoneyType(param);
          		////2、判断用户存在那种产品，只开通FTTH宽带押金200；开通宽带且办理宽带1+押金100    		
          		//根据用户判断是否开通FTTH宽带和宽带1+，返回值是commpara表attr=6131的内容。
          		if("0".equals(payType)){
          			CSAppException.appError("61310", "未办理FTTH宽带业务，无法办理租赁光猫。");
          		}else{
          	      		IDataset datas=bean.getCustModermInfo(param); 
    	      	      	if(datas!=null && datas.size()>0){
    	              		rtnData.put("APPLYTYPE","");
    	                  	rtnData.put("DEPOSIT","");
    	                  	rtnData.put("X_INFO","1");
    	                  	return rtnData;
    	              	}else{
    	              		//先取押金金额commpara表param_attr=6131
    	          			IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", payType, "0898");
    	          			deposit=paras.getData(0).getString("PARA_CODE1");
    	              	}
          	      	}
      		}else if("2".equals(applyType)){//赠送
  	      		deposit = "0";
  	      	}
      		rtnData.put("APPLYTYPE",applyType);
          	rtnData.put("DEPOSIT", Integer.parseInt(deposit)/100);
          	rtnData.put("X_INFO","0");
          	return rtnData;
      	}
      }
    public IDataset queryTradeModemInfo(IData inParam) throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	return bean.getUserFTTHProdWWG(inParam);
    }
    
    /**
     * 查询光猫信息
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryModemInfo(IData inParam) throws Exception{
    	String serialNumber=inParam.getString("SERIAL_NUMBER","");
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("MODERM_ID", inParam.getString("MODERM_ID",""));
    	return bean.queryModemInfo(param);
    }
    
    /**
     * 查询光猫信息
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryUserModemInfo(IData inParam) throws Exception{
    	String serialNumber=inParam.getString("SERIAL_NUMBER","");
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("MODERM_ID", inParam.getString("MODERM_ID",""));
    	return bean.queryUserModemInfo(param);
    }
    
    
    /**
     * BUG20151109155214 光猫串号必须输入bug 
     * 判断是否用户已经申领光猫
     * 是的话返回相应信息
     * chenxy3 20151116 SS.FTTHModermApplySVC.getUserFtthResNO
     * */
    public IData getUserFtthResNO(IData inParam) throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	IData data=new DataMap();
    	data.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER",""));
    	IDataset datas=bean.getUserModermInfo(data); 
    	if(datas!=null && datas.size()>0){
    		String resNO=datas.getData(0).getString("RSRV_STR1","");
    		if(resNO !=null && !"".equals(resNO)){
    			data.put("FTTH_TAG", "2");//已经办理光猫申领，且已经存在光猫串号
    			data.put("RES_NO",resNO);
    		}else{
    			data.put("FTTH_TAG", "1");//已经办理光猫申领，还未存在光猫串号
    			data.put("RES_NO","");
    		}
    	}else{
    		data.put("FTTH_TAG", "0");//还未办理光猫申领
			data.put("RES_NO","");
    	}
    	return data;
    } 
    
    /**
     * 光猫串号必须输入bug 
     * 判断是否用户已经申领光猫
     * 是的话返回相应信息
     * lijun17 20160620 SS.FTTHModemManageSVC.getUserTradeFtthResNO
     * */
    public IData getUserTradeFtthResNO(IData inParam) throws Exception{
    	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
    	String tradeId = inParam.getString("TRADE_ID");
		IData data = new DataMap();
    	IDataset tradeInfos = TradeInfoQry.queryTradeSet(tradeId, null);
		IData tradeBhInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", getTradeEparchyCode());
    	if(DataSetUtils.isNotBlank(tradeInfos)){
    		String tradeTypeCode = tradeInfos.getData(0).getString("TRADE_TYPE_CODE");
    		IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "3910", tradeTypeCode, "0898");
    		if(DataSetUtils.isNotBlank(paras)){
    			data.put("FTTH_TAG", "0");//还未办理光猫申领
    			data.put("RES_NO","");
    		}else{
    	    	String rsrv_value_code = "FTTH";
    	    	IDataset userTradeOtherInfos=TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
    	    	if(userTradeOtherInfos!=null && userTradeOtherInfos.size()>0){
    	    		String resNO=userTradeOtherInfos.getData(0).getString("RSRV_STR1","");
    	    		if(resNO !=null && !"".equals(resNO)){
    	    			data.put("FTTH_TAG", "2");//已经办理光猫申领，且已经存在光猫串号
    	    			data.put("RES_NO",resNO);
    	    		}else{
    	    			data.put("FTTH_TAG", "1");//已经办理光猫申领，还未存在光猫串号
    	    			data.put("RES_NO","");
    	    		}
    	    	}else{
    	    		IData otherParam = new DataMap();
    	    		otherParam.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
    	    		IDataset userOtherInfos=FTTHModemManageBean.getOldUserModermInfo(otherParam);
    	    		if(DataSetUtils.isNotBlank(userOtherInfos)){
    	    			String resNO=userOtherInfos.getData(0).getString("RSRV_STR1","");
    	        		if(resNO !=null && !"".equals(resNO)){
    	        			data.put("FTTH_TAG", "2");//已经办理光猫申领，且已经存在光猫串号
    	        			data.put("RES_NO",resNO);
    	        		}else{
    	        			data.put("FTTH_TAG", "1");//已经办理光猫申领，还未存在光猫串号
    	        			data.put("RES_NO","");
    	        		}
    	    		}else{
    	    			data.put("FTTH_TAG", "0");//还未办理光猫申领
    	    			data.put("RES_NO","");
    	    		}
    	    	}
    		}
    	}else{
    		if("680".equals(tradeBhInfo.getString("TRADE_TYPE_CODE","")))
            {
    			IData otherParam = new DataMap();
    			if(!inParam.getString("SERIAL_NUMBER").startsWith("KD_"))
    			{
    				otherParam.put("SERIAL_NUMBER", "KD_"+inParam.getString("SERIAL_NUMBER"));
    			}
    			else
    			{
    				otherParam.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
    			}
	    		IDataset userOtherInfos=FTTHModemManageBean.getOldUserModermInfo(otherParam);

	    		if(DataSetUtils.isNotBlank(userOtherInfos)){
	    			String resNO=userOtherInfos.getData(0).getString("RSRV_STR1","");
	        		if(resNO !=null && !"".equals(resNO)){
	        			data.put("FTTH_TAG", "2");//已经办理光猫申领，且已经存在光猫串号
	        			data.put("RES_NO",resNO);
	        		}else{
	        			data.put("FTTH_TAG", "1");//已经办理光猫申领，还未存在光猫串号
	        			data.put("RES_NO","");
	        		}
	    		}else{
	    			data.put("FTTH_TAG", "0");//还未办理光猫申领
	    			data.put("RES_NO","");
	    		}
            }
    		else{
	    		data.put("FTTH_TAG", "0");//还未办理光猫申领
				data.put("RES_NO","");
    		}
    	}
    	return data;
    } 
    
    /**
     * 外线打单光猫录入接口
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset writeModemCode(IData inParam)throws Exception{
		IDataset result = new DatasetList();
		this.checkIsModem(inParam.getString("RES_ID"),ResTypeEnum.MODEM.getValue());
		IData retData = this.checkModem(inParam);
		
		inParam.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
		inParam.put("RES_NO", inParam.getString("RES_ID"));
		inParam.put("USER_ID", inParam.getString("USER_ID"));
		inParam.put("TRADE_ID", inParam.getString("TRADE_ID"));
		inParam.put("RES_KIND_CODE", retData.getString("RES_KIND_CODE"));
		
		String tradeId = inParam.getString("TRADE_ID");
		IData tradeBhInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", getTradeEparchyCode());
	    IData tradeBInfo = UTradeInfoQry.qryTradeByTradeId(tradeId,"0", getTradeEparchyCode());
	        
        if("686".equals(tradeBInfo.getString("TRADE_TYPE_CODE","")))
        {
            inParam.put("SERIAL_NUMBER", "KD_"+inParam.getString("SERIAL_NUMBER"));
        }
        
        //无手机宽带开户也走此逻辑    （20170207追加无手机宽带移机686也走此逻辑，但从现表tradeBInfo取数据）
        if(inParam.getString("SERIAL_NUMBER").length() == 11 || "680".equals(tradeBhInfo.getString("TRADE_TYPE_CODE","")) || "686".equals(tradeBInfo.getString("TRADE_TYPE_CODE","")))
        {

			IData crmdata = this.updModemNumber(inParam);
			IDataset assureUserDataset = null;
			IDataset assureCustDataset = null;
			if (crmdata != null && StringUtils.equals(crmdata.getString("X_RESULTCODE"), "0")){
				IData param = new DataMap();
				param.put("TRADE_ID", inParam.getString("TRADE_ID"));
				param.put("RES_NO", inParam.getString("RES_ID"));//串号 
				param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
				param.put("DEVICE_COST", retData.getString("DEVICE_COST"));
				param.put("BILL_ID", inParam.getString("SERIAL_NUMBER"));
				IData param4UserQry = new DataMap();
				
				if ("680".equals(tradeBhInfo.getString("TRADE_TYPE_CODE","")))
				{
				    param4UserQry.put("SERIAL_NUMBER", "KD_"+inParam.getString("SERIAL_NUMBER"));
				}
				else
				{
				    param4UserQry.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
				}
				
				assureUserDataset = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoBySn", param4UserQry);
				if (assureUserDataset.isEmpty())
				{
					CSAppException.apperr(CustException.CRM_CUST_134, inParam.getString("SERIAL_NUMBER"));
				}
				IData param4CustQry = new DataMap();
				param4CustQry.put("CUST_ID", assureUserDataset.get(0, "CUST_ID"));
				param4CustQry.put(Route.ROUTE_EPARCHY_CODE, assureUserDataset.get(0, "EPARCHY_CODE"));
				assureCustDataset = CSAppCall.call("CS.UcaInfoQrySVC.qryCustInfoByCustId", param4CustQry);
				if (assureCustDataset.isEmpty())
				{
					CSAppException.apperr(CustException.CRM_CUST_134, inParam.getString("SERIAL_NUMBER"));
				}
				String custName =  String.valueOf(assureCustDataset.get(0, "CUST_NAME"));
				param.put("CUST_NAME",  custName);
				param.put("TRADE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
				this.updateModem(param);
			}
			else{
				String resultInfo = crmdata.getString("X_RESULTINFO", "crm调用异常！[光猫]");
				CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
			}
		}
		else
		{
			IDataset crmdataSet = CSAppCall.call("SS.FTTHBusiModemManageSVC.updFtthBusiResNO", inParam);
			IDataset assureUserDataset = null;
			IDataset assureCustDataset = null;
			if (DataSetUtils.isNotBlank(crmdataSet) && StringUtils.equals(crmdataSet.first().getString("X_RESULTCODE"), "0")){
				IData param = new DataMap();
				param.put("TRADE_ID", inParam.getString("TRADE_ID"));
				param.put("RES_NO", inParam.getString("RES_ID"));//串号 
				param.put("SERIAL_NUMBER", "KD_" + inParam.getString("SERIAL_NUMBER"));
				param.put("DEVICE_COST", retData.getString("DEVICE_COST"));
				param.put("BILL_ID", "KD_" + inParam.getString("SERIAL_NUMBER"));
				IData param4UserQry = new DataMap();
				param4UserQry.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER").substring(0, 11));
				assureUserDataset = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoBySn", param4UserQry);
				if (assureUserDataset.isEmpty())
				{
					CSAppException.apperr(CustException.CRM_CUST_134, inParam.getString("SERIAL_NUMBER"));
				}
				IData param4CustQry = new DataMap();
				param4CustQry.put("CUST_ID", assureUserDataset.get(0, "CUST_ID"));
				param4CustQry.put(Route.ROUTE_EPARCHY_CODE, assureUserDataset.get(0, "EPARCHY_CODE"));
				assureCustDataset = CSAppCall.call("CS.UcaInfoQrySVC.qryCustInfoByCustId", param4CustQry);
				if (assureCustDataset.isEmpty())
				{
					CSAppException.apperr(CustException.CRM_CUST_134, inParam.getString("SERIAL_NUMBER"));
				}
				String custName =  String.valueOf(assureCustDataset.get(0, "CUST_NAME"));
				param.put("CUST_NAME",  custName);
				param.put("TRADE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
				this.updateModem(param);
			}else{
				String resultInfo = crmdataSet.first().getString("X_RESULTINFO", "crm调用异常！[光猫]");
				CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
			}
		}
		
		retData.put("X_RESULTINFO", "0");
		result.add(retData);
		return result;
    }
    
    /**
	 * @Description：校验是否为光猫
	 * @param:@param string
	 * @param:@param value
	 * @return void
     * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-31上午09:31:59
	 */
	private void checkIsModem(String resNo, String ResType) throws Exception {
	     IDataset retDataset = HwTerminalCall.checkIsResRightType(resNo,ResType);
	     if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
         {
	    	 if(StringUtils.equals(retDataset.first().getString("retVal"), "0")){
	    		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "串号不是光猫串号，请确认！"); // 接口返回异常
	    	 }
         }
	}


	/**
     * @Function: checkModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public IData checkModem(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        IDataset retDataset = HwTerminalCall.querySetTopBoxForApp(input);
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
    /**
     * 终端查询
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryModem(IData input) throws Exception
    {
        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        IDataset resNoInfo = FTTHModemManageBean.getResNoByOhter(resNo);
        if(DataSetUtils.isNotBlank(resNoInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该光猫串号终端["+resNo+"]已经在正常使用");	
        }
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        //查询终端当前状态
        IDataset retDataset = HwTerminalCall.queryTerminalStateInfo(serialNumber, resNo);
        
        if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
        {
        	String resState = retDataset.first().getString("RES_STATE");
        	
        	//如果是空闲状态
        	if ("1".equals(resState))
        	{
        		//前台预占，后台完工实占
        		IDataset resDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
        		
        		if (DataSetUtils.isNotBlank(resDataset) && StringUtils.equals(resDataset.first().getString("X_RESULTCODE"), "0"))
                {
        			IData res = resDataset.first();
                    String resKindCode = res.getString("DEVICE_MODEL_CODE", "");
                    retData.put("X_RESULTCODE", "0");
                    retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
                    retData.put("RES_ID", resNo); // 终端串号
                    retData.put("RES_NO", res.getString("SERIAL_NUMBER", "")); // 接口返回的终端串号IMEI
                    retData.put("RES_KIND_CODE", resKindCode); // 终端型号编码
                    retData.put("RES_KIND_NAME", res.getString("DEVICE_MODEL", "")); // 终端型号描述
                    retData.put("SUPPLEMENT_TYPE", "0");
                }
        		
        	}
        	//如果是预占状态
        	else if ("3".equals(resState))
        	{
        		//后台完工实占
        		retData.put("SUPPLEMENT_TYPE", "0");
        	}
        	//如果是占用状态
        	else if ("4".equals(resState))
        	{
        		//后台完工只录资料表，不调用资源接口
        		retData.put("SUPPLEMENT_TYPE", "1");
        		retData.put("RES_NO", resNo); // 终端串号
                retData.put("RES_KIND_CODE", ""); // 终端型号编码
        	}
        	else
        	{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "终端为未知状态，请联系管理员！");
        	}
        }
        else
        {
    		String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
	        CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
        
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
    	param.put("STAFF_ID", input.getString("TRADE_STAFF_ID","SUPERUSR"));//销售员工
    	param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

    	IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalIdForApp(param);
    	if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
    		String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
    		if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
    		}
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"华为接口调用异常！");
    	}
    }
    
    public IDataset checkOperType(IData inParam)throws Exception{
    	String  serialNumber = inParam.getString("SERIAL_NUMBER","");
    	String wSerialNumber = serialNumber.indexOf("KD_")>-1 ? serialNumber:"KD_" + serialNumber;
		IDataset userInfos = new DatasetList();
    	String operType = inParam.getString("OPER_TYPE","");
    	if(StringUtils.equals("2", operType)){
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
    		if(IDataUtil.isEmpty(userInfo)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户宽带已拆机或未办理宽带，不能办理光猫更换业务！");
    		}else{
    			String userId = userInfo.getString("USER_ID");
    			IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("605", userId, "0");
    			if(DataSetUtils.isNotBlank(outDataset)){//有未完工拆机业务
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"您有未完工拆机业务不能再次办理光猫更换!");
    			}
    			IDataset destorySpecDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("615", userId, "0");
    			if(DataSetUtils.isNotBlank(destorySpecDataset)){//有未完工特殊拆机业务
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"您有未完工特殊拆机业务不能再次办理光猫更换!");
    			}
    		}
    		userInfos.add(userInfo);
    	}else if(StringUtils.equals("3", operType) || StringUtils.equals("4", operType)){
    		String userId = "";
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
    		if(IDataUtil.isEmpty(userInfo)){
    			IData wideUserInfo = FTTHBusiModemManageBean.qryUserInfoBySn(wSerialNumber).first();
        		if(IDataUtil.isEmpty(wideUserInfo)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未办理宽带或宽带开户未完工!");
        		}else{
        			userId = wideUserInfo.getString("USER_ID");
        		}
    		}else{
    			userId = userInfo.getString("USER_ID");
    		}
    		String errInfos = StringUtils.equals("3", operType) ? "退还" : "丢失" ;
//    		IDataset moveDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("606", userId, "0");
//    		if(DataSetUtils.isNotBlank(moveDataset)){//有未完工拆机业务
//    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"您有完工移机工单，不能办理"+errInfos+"业务!");
//			}
			IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("605", userId, "0");
			if(DataSetUtils.isNotBlank(outDataset)){//有未完工拆机业务
				if(StringUtils.equals("1", outDataset.getData(0).getString("RSRV_STR1"))){//有未完工拆机业务并且已退还光猫
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"您在办理拆机业务时已退还光猫，不能再次办理"+errInfos+"业务!");
				}
			}
			IDataset destorySpecDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("615", userId, "0");
			if(DataSetUtils.isNotBlank(destorySpecDataset)){//有未完工特殊拆机业务
				if(StringUtils.equals("1", destorySpecDataset.getData(0).getString("RSRV_STR1"))){//有未完工拆机业务并且已退还光猫
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"您在办理特殊拆机业务时已退还光猫，不能再次办理"+errInfos+"业务!");
				}
			}
    	}
		return userInfos;
    }
    
    /**
     * 查询用户可补录光猫串号 信息
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset queryModemSupplementInfo(IData inParam) throws Exception{
    	String serialNumber=inParam.getString("SERIAL_NUMBER","");
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
    	return FTTHModemManageBean.queryModemSupplementInfo(param);
    }
    
    //提供新大陆 ‘光猫更新接口’@tanzheng 20171107
    
    public IData changeModem(IData input) throws Exception {
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	input.put("RES_ID", IDataUtil.chkParam(input,"RES_NO"));   	
		IDataset dataset=CSAppCall.call("SS.TopSetBoxSVC.checkModem", input);//this.queryUserSaleActiveInfo(saleData); 
		IData result =  new DataMap();
		//调用光猫校验接口通过
        if(dataset!=null &&dataset.size()>0){
        	IData data = dataset.first();
    		IData inParam=new DataMap();
    		inParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
    		inParam.put("MODERM_ID", "");
    		IDataset modeInfoDataset= queryModemInfo(inParam);
    		IData modeInfo=null;
    		if(modeInfoDataset!=null && modeInfoDataset.size()>0){
    			modeInfo = modeInfoDataset.first();
    		}else{
    			result.put("X_RESULTCODE", "2998");
            	result.put("X_RESULTINFO", "光猫信息不存在！");
            	return result;
    		}
    		
    		IData param=new DataMap();
    		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
    		param.put("MODERM_ID", modeInfo.getString("RSRV_STR1"));
    		param.put("INST_ID", modeInfo.getString("INST_ID"));
    		param.put("NEW_MODERM_TYPE", data.getString("NEW_MODERM_TYPE"));
    		param.put("NEW_MODERM_ID", input.getString("RES_NO"));

    		dataset = CSAppCall.call("SS.FTTHModemChangeRegSVC.tradeReg", param);
    		
    		if(dataset!=null &&dataset.size()>0){
    			 result.put("X_RESULTCODE", "0");
    			 result.put("X_RESULTINFO", "修改成功！");
    		}else{
	    		 result.put("X_RESULTCODE", "2998");
	        	 result.put("X_RESULTINFO", "SS.FTTHModemChangeRegSVC.tradeReg 调用异常");
    	    }
        	
    	}else{
    		result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "华为接口调用异常！");
    	}
		return result;
	}
    
    public IDataset checkModermUp(IData inParam)throws Exception{
    	
    	IDataset results = new DatasetList();
    	IData result = new DataMap();
    	result.put("RESULT_CODE", "0000");
    	String userId = inParam.getString("USER_ID");
    	String moderm_id = inParam.getString("MODERM_ID");
    	String modemType = inParam.getString("MODEM_TYPE");
    	IDataset commparaInfos2311 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","2311",modemType,null);
    	if (IDataUtil.isNotEmpty(commparaInfos2311))
        {
    		result.put("COMMPARA_TAG", "1");
        }
    	IDataset discntInfos = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId,"80176874",Route.CONN_CRM_CG);
    	if(IDataUtil.isNotEmpty(discntInfos)){
    		result.put("DISCNT_TAG", "1");
    	}
    	
		inParam.put("MODERM_ID", moderm_id);
		inParam.put("RSRV_VALUE_CODE","FTTH");
		IDataset wideUserInfos = UserInfoQry.getUserInfoBySn("KD_"+inParam.getString("SERIAL_NUMBER"), "0");
		if (IDataUtil.isNotEmpty(wideUserInfos))
        {
			//获取该号码的宽带资料
			IDataset dataset = WidenetInfoQry.getUserWidenetInfo(wideUserInfos.getData(0).getString("USER_ID"));
			if (IDataUtil.isNotEmpty(dataset))
	        {
				String startDate = dataset.first().getString("START_DATE");
				if("2017-03-01".compareTo(startDate)>=0){
					result.put("DATE_TAG", "1");
				}
	        }
        }
		
		results.add(result);
    	
    	return results;
    }	
    
}
