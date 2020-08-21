
package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.requestdata.NpOutApplyReqData;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

public class BuildNpOutApplyReqData extends BaseBuilder implements IBuilder
{

    /**
     * 入参 {BACK_TAG=[""], PORT_OUT_NETID=["00238980"], CREDTYPE=["0"], CREDNUMBER=["360121198602283948"], CARDTYPE=[""],
     * PORT_IN_NETID=["00338980"], TRADE_DEPART_ID=["NGPF0"], ACTOR_PHONE=[""], CREDNUMBER2=[""],
     * NPCODE=["18389804604"], X_TRANS_CODE=["SS.DispatcherNpRequestSVC.dispatcherNpRequest"], CREDTYPE2=["Z"],
     * REQTIME=["20140902114646"], HOME_NETID=["00238980"], SERVICETYPE=["MOBILE"], TRADE_EPARCHY_CODE=["0898"],
     * TRADE_CITY_CODE=["NGPF"], TELEPHONE=["15024107210"], EPARCHY_CODE=["0898"], COMMANDCODE=["APPLY_REQ"],
     * NAME2=[""], FLOWID=["8981140902000006"], USERNAME=["测试长沙2"], TRADE_STAFF_ID=["NGPF0000"],
     * MESSAGEID=["0008980000000040"]}
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        NpOutApplyReqData reqData = (NpOutApplyReqData) brd;

        reqData.setActorCredType(param.getString("CREDTYPE2"));
        reqData.setActorCustName(param.getString("NAME2"));
        reqData.setActorPsptId(param.getString("CREDNUMBER2"));
        reqData.setCommandCode(param.getString("COMMANDCODE"));
        reqData.setCredType(param.getString("CREDTYPE"));

        reqData.setCustName(param.getString("USERNAME"));
        reqData.setFlowId(param.getString("FLOWID"));
        reqData.setHomeNetID(param.getString("HOME_NETID"));
        reqData.setMessageId(param.getString("MESSAGEID"));
        reqData.setNpServiceType(param.getString("SERVICETYPE"));

        reqData.setPhone(param.getString("TELEPHONE"));
        reqData.setPortInNetID(param.getString("PORT_IN_NETID"));
        reqData.setPortOutNetID(param.getString("PORT_OUT_NETID"));
        reqData.setPsptId(param.getString("CREDNUMBER"));
        reqData.setAuthTag(param.getString("AUTH_TAG",""));
        reqData.setRsrvStr3(param.getString("PORT_BACK_ID","0"));
        reqData.setxResultinfo(param.getString("X_RESULTINFO"));
        String serialNum = reqData.getUca().getSerialNumber();

        IDataset commparas = CommparaInfoQry.getCommpara("CSM", "163", "0", "0898");
        if (commparas.size() != 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "获取EPARCHY_CODE=[0898],TRADE_TYPE_CODE=[41]的业务类型的人工审核配置参数失败！");
        }
        reqData.setRsrvNum1(commparas.getData(0).getString("PARA_CODE1", "1"));// 人工审核时间配置
        
        IDataset commparas1 = CommparaInfoQry.getCommpara("CSM", "166", "0", "0898");
        if (IDataUtil.isNotEmpty(commparas1))
        {
        	reqData.setRsrvStr1("ALLPASS");
        }
        else
        {
	        IDataset commparas2 = CommparaInfoQry.getCommpara("CSM", "164", serialNum, "0898");
	        if (IDataUtil.isNotEmpty(commparas2))
	        {
	        	reqData.setRsrvStr1("NOSTOPOUT");
	        }
	        else
	        {
	        	int count = Integer.parseInt(TradeNpQry.getRecodeCountByNpSaveFlag(serialNum));
	        	
	        	if(count>0)
	        	{
	        		reqData.setRsrvStr1("NOSTOPOUT");
	        	}
	        	else
	        	{
					checkAutoStop(reqData);
	        	}
	        }
        }
    }
       /* IDataset ids = TradeNpQry.getTradeNpByUserId(reqData.getUca().getUserId());
        if (IDataUtil.isNotEmpty(ids))
        {
            reqData.setPortInDate(ids.getData(0).getString("PORT_IN_DATE"));
        }

        IDataset commparas = CommparaInfoQry.getCommpara("CSM", "163", "0", "0898");
        if (commparas.size() != 1)
        {
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "获取EPARCHY_CODE=[0898],TRADE_TYPE_CODE=[41]的业务类型的人工审核配置参数失败！");
        }
        reqData.setRsrvNum1(commparas.getData(0).getString("PARA_CODE1", "3"));// 人工审核时间配置，默认为3分钟
    }*/

 // 记入日志，不在前台拦截
	@SuppressWarnings("unused")
	private void checkAutoStop(NpOutApplyReqData reqData) throws Exception{
    	String userId = reqData.getUca().getUserId();
    	String serialNumber = reqData.getUca().getSerialNumber();
    	reqData.setRsrvStr1("AUTOPASS");
    	// 实时欠费不允许携出
	        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);
	        IData userOweFee = AcctCall.queryOweFee(serialNumber);
	        if(IDataUtil.isNotEmpty(userOweFee)){
	        	long acctBalance = userOweFee.getLong("ALL_BALANCE");//实时结余
		        if(acctBalance <0 && "0".equals(ucaData.getAccount().getPayModeCode())) {
	     			reqData.setRsrvStr1("AUTOSTOP");
	        		reqData.setRsrvStr2("用户与携出方签约时间未满（用户有实时欠费："+Math.abs((acctBalance))/100.0+"元");
	        		return;
		        }
	        }
        
			//一卡付多号业务 add by panyu5
		IDataset payMoreCards = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "97", "2");
		if(IDataUtil.isNotEmpty(payMoreCards)){
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("用户在携出方使用了一卡付多号副卡业务，不允许携出");
    		return;
		}
		//一卡双号副卡业务 add by panyu5
		IDataset payDoubleCards = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "97", "2");
		if(IDataUtil.isNotEmpty(payDoubleCards)){	
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("用户在携出方使用了一卡双号副卡业务，不允许携出");
    		return;
		}
		//双卡统一付费 add by panyu5
		IDataset payOneCards = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "97", "2");
		if(IDataUtil.isNotEmpty(payOneCards)){
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("用户在携出方使用了双卡统一付费副卡业务，不允许携出");
    		return;
		}
        UcaData ucaDataTemp = UcaDataFactory.getUcaByUserId(userId);
        IDataset days = CommparaInfoQry.getCommPkInfo("CSM", "170", "0", "0898");// SEL_BY_ATTR_CODE
        int para_code1 = 120;
        if (IDataUtil.isNotEmpty(days))
        {
            para_code1 = days.getData(0).getInt("PARA_CODE1", 120);// 默认为120天

        }
        int day = SysDateMgr.dayInterval(ucaDataTemp.getUser().getOpenDate(), SysDateMgr.getSysDate());
        if (para_code1 > day)
        {
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("号码入网激活后的时间未达到允许携转的法规规定值，不允许携出");
    		return;
        }
        IDataset WideUsers = UserInfoQry.getWideUsersBySerialNumber(serialNumber);
        if(IDataUtil.isNotEmpty(WideUsers)){
			
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("用户在携出方使用了影响其他号码付费或资费套餐使用的业务，不允许携出");
    		return;
		}
        IDataset RelationUus = RelaUUInfoQry.getRelationUusByUserIdBTypeCode(userId, "56");
        if(IDataUtil.isNotEmpty(RelationUus)){
		
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("用户在携出方使用了影响其他号码付费或资费套餐使用的业务，不允许携出");
    		return;
		}
        IDataset WindTrades = TradeInfoQry.getWindTradeInfoBySn("KD_" + serialNumber);
        if(IDataUtil.isNotEmpty(WindTrades)){
			reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("该用户处在营销活动有效期内：该手机用户有宽带用户不能办理手机号码携转出网");
    		return;
		}
        IDataset wideTypes = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber));
        if (IDataUtil.isNotEmpty(wideTypes))
        {
        	IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
        	IData data = GetMaxEndDate(widenetInfos);
        	String product_name = "";
        	String wideType = data.getString("RSRV_STR2");
        	if("1".equals(wideType))
        	{
        		product_name = "GPON宽带开户业务";
        	}
        	else if("2".equals(wideType))
        	{
        		product_name = "ADSL宽带开户业务";
        	}
        	else if("3".equals(wideType))
        	{
        		product_name = "FTTH宽带开户业务";
        	}
        	else
        	{
        		product_name = "未知宽带业务";
        	}
            
    		reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("该用户处在"+ product_name + "有效期内, " + data.getString("END_DATE") + "到期，不允许携出");
    		return;
            
        }
    	IDataset custInfos = CustomerInfoQry.getCustomerInfoByUserId(userId);
    	IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custInfos.getData(0).getString("CUST_ID"));
    	String specialCustDate = custInfo.getString("RSRV_DATE1", "");
		
		if(!specialCustDate.trim().equals("")){
			//终止时间
			String validEndDate=SysDateMgr.addYears(specialCustDate, 2);
			
			//当前时间
			String curTime=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
			
			if(curTime.compareTo(validEndDate)<=0){
	    		reqData.setRsrvStr1("AUTOSTOP");
	    		reqData.setRsrvStr2("用户与携出方有未解除的协议，不允许携出！");
	    		return;
			}
			}
		IDataset GropMember = BreQry.getGropMemberInfoByUserId(userId);
		if(IDataUtil.isNotEmpty(GropMember)){
    		reqData.setRsrvStr1("AUTOSTOP");
    		reqData.setRsrvStr2("该号码为集团成员，不允许携出");
    		return;
		}
		//海洋通船东成员不允许携出
		IDataset idataset = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "HYT");
		if(IDataUtil.isNotEmpty(idataset)){
         	if("1".equals(idataset.getData(0).getString("RSRV_STR2"))){
        		reqData.setRsrvStr1("AUTOSTOP");
        		reqData.setRsrvStr2("海洋通船东用户，不允许携出！");
        		return;
         	}
		}
    }
    private IData GetMaxEndDate(IDataset Array) throws Exception
    {
        String MaxDate = "";
        int iMax = 0;
        for(int i = 0; i < Array.size(); i++)
        {
        	if("".equals(MaxDate.toString()))
        	{
        		MaxDate = Array.getData(i).getString("END_DATE");
        		iMax = i;
        		continue;
        	}
        	String date = SysDateMgr.decodeTimestamp(MaxDate, SysDateMgr.PATTERN_STAND);
            String compareDate = Array.getData(i).getString("END_DATE");
            compareDate = SysDateMgr.decodeTimestamp(compareDate, SysDateMgr.PATTERN_STAND);
            int result = 0;
            if(date.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER)
                    && compareDate.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER))
            {
            	result = 0;
            	continue;
            }
            result = date.compareTo(compareDate);
        	if(result < 0)
        	{
        		MaxDate = Array.getData(i).getString("END_DATE");
        		iMax = i;
        	}
        }
        return Array.getData(iMax);
    }
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new NpOutApplyReqData();
    }
    

}
