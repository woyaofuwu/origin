package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class DredgeSmartDealFeeAction implements ITradeAction{
	
	protected static Logger log = Logger.getLogger(DredgeSmartDealFeeAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		String SerialNumber = btd.getRD().getPageRequestData().getString("SERIAL_NUMBER");
		String firstType = btd.getRD().getPageRequestData().getString("FIRST_TYPE");
		String secondType = btd.getRD().getPageRequestData().getString("SECOND_TYPE");
	    String thirdType = btd.getRD().getPageRequestData().getString("THIRD_TYPE");
		System.out.println("执行DredgeSmartDealFeeAction");
		int feeAll = 0;
		
		if(!(StringUtils.isBlank(firstType)&&StringUtils.isBlank(secondType)&&StringUtils.isBlank(thirdType))){
			IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9211",null,null);
			for(int i=0;i<commparaInfos9211.size();i++){
				if(IDataUtil.isNotEmpty(commparaInfos9211)){
					
				    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(firstType)){
				    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4")))
				    	{
				    		payDredgeFee(SerialNumber,commparaInfos9211.getData(i),btd);
				    		feeAll = feeAll + Integer.parseInt(commparaInfos9211.getData(i).getString("PARA_CODE4"))/100;
				    		
				    	}
				    }
				    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(secondType)){
				    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4")))
				    	{
				    		payDredgeFee(SerialNumber,commparaInfos9211.getData(i),btd);
				    		feeAll = feeAll + Integer.parseInt(commparaInfos9211.getData(i).getString("PARA_CODE4"))/100;
				    	}
				    }
                    if(commparaInfos9211.getData(i).getString("PARAM_CODE").equals(thirdType)){
                    	if(StringUtils.isNotBlank(commparaInfos9211.getData(i).getString("PARA_CODE4")))
				    	{
				    		payDredgeFee(SerialNumber,commparaInfos9211.getData(i),btd);
				    		feeAll = feeAll + Integer.parseInt(commparaInfos9211.getData(i).getString("PARA_CODE4"))/100;
				    	}
				    }
                    
				}
				
			}						
			btd.getMainTradeData().setRsrvStr1(feeAll+"");
			
		}
		
	}

	
	public void payDredgeFee(String SerialNumber,IData commparaInfos9211,BusiTradeData btd) throws Exception{
		
		IData params=new DataMap(); 
    	params.put("OUTER_TRADE_ID", SeqMgr.getTradeId());
        params.put("SERIAL_NUMBER", SerialNumber);
        params.put("DEPOSIT_CODE_OUT",commparaInfos9211.getString("PARA_CODE5",WideNetUtil.getOutDepositCode()));//账务给存折后修改
        params.put("DEPOSIT_CODE_IN", commparaInfos9211.getString("PARA_CODE6",""));//账务给存折后修改
        params.put("TRADE_FEE", commparaInfos9211.getString("PARA_CODE4"));
        params.put("CHANNEL_ID", "15000");
        params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        params.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
        params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        
        IDataset widenetInfo =WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+SerialNumber);
        String detailAddress="";
        String cityCode="";
        if(IDataUtil.isNotEmpty(widenetInfo)){
        	detailAddress=widenetInfo.getData(0).getString("DETAIL_ADDRESS");
        	cityCode = widenetInfo.getData(0).getString("RSRV_STR4");
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_"+SerialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
        	//cityCode = userInfo.getString("CITY_CODE");
        	if("HNSJ".equals(cityCode)){
        		cityCode="HNHK";
        	}
        }
        
        //创建other台账
        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setRsrvValueCode("ZNZW_ACCT_IN");
        //otherTD.setRsrvValue(prd.getPassId());
        //REQ202003240027  优化智能组网资费的需求—BOSS侧
        //按月套餐不走存折转账方式,费用设置为0,完工时根据该值判断是否中间存折转正式存折
        String paraCode9 = commparaInfos9211.getString("PARA_CODE9");//包月月份
        otherTD.setRsrvNum1(StringUtils.isNotBlank(paraCode9)?"0":commparaInfos9211.getString("PARA_CODE4"));//费用(包月记录0元)
        otherTD.setRsrvNum2(commparaInfos9211.getString("PARA_CODE2"));//终端数量
        otherTD.setRsrvNum3(commparaInfos9211.getString("PARA_CODE11"));//受理单短信模板

        otherTD.setRsrvStr1(btd.getTradeId());
        otherTD.setRsrvStr2(params.getString("OUTER_TRADE_ID"));//存临时账户的流水号OUTER_TRADE_ID
        otherTD.setRsrvStr3(params.getString("DEPOSIT_CODE_OUT"));
        otherTD.setRsrvStr4(commparaInfos9211.getString("PARAM_CODE"));
        otherTD.setRsrvStr5(commparaInfos9211.getString("PARA_CODE3"));
        otherTD.setRsrvStr6(commparaInfos9211.getString("PARA_CODE1"));
        otherTD.setRsrvStr7(widenetInfo.getData(0).getString("RSRV_NUM1"));//宽带设备

        otherTD.setRsrvStr10(commparaInfos9211.getString("PARA_CODE17"));//智能组网设备
        otherTD.setRsrvStr11(SerialNumber);//服务号码
        otherTD.setRsrvStr21(detailAddress);//宽带地址 
        otherTD.setRsrvStr19(cityCode);  
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setUserId(btd.getRD().getUca().getUserId());
        otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otherTD.setRemark("智能组网other数据");
        otherTD.setModifyTag("0");
        otherTD.setInstId(SeqMgr.getInstId());
        btd.add(SerialNumber, otherTD);
        //DEPOSIT_CODE_OUT存折不配置,不走存折收费方式
        if(StringUtils.isNotBlank(params.getString("TRADE_FEE"))&&StringUtils.isNotBlank(params.getString("DEPOSIT_CODE_IN"))){
        	 //调用接口，将【现金类】——>【押金】
            IData resultData = AcctCall.transFeeInADSL(params);
            
            if(log.isDebugEnabled())
            {
                log.debug("调用AM_CRM_TransFeeInADSL 接口返回结果:" + resultData);
            }
            
            String result=resultData.getString("RESULT_CODE","");
            
            if("".equals(result) || !"0".equals(result))
            {
                CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
            }
        }
        
	}
	
	
}
