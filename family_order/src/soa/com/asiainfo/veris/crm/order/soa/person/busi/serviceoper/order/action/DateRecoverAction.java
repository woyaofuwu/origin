package com.asiainfo.veris.crm.order.soa.person.busi.serviceoper.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class DateRecoverAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		
		String kindId = "BIP3A303_T3000306_0_0";
		String oprNum = "";//本次操作流水号
		String provCode = "898";//省代码

		List<SvcStateTradeData> stateList = btd.getTradeDatas(TradeTableEnum.TRADE_SVCSTATE);
        UserTradeData user = btd.getRD().getUca().getUser();
        
        //判断用户是否在封顶业务场景下发的数据功能恢复短信
        IData param = new DataMap();
        param.put("USER_ID", user.getUserId());
        StringBuilder strSql = new StringBuilder();
        strSql.append(" SELECT * FROM TL_F_USER_SERTRACK_LOG ");
        strSql.append(" WHERE USER_ID = :USER_ID ");
        strSql.append(" AND MODIFY_TAG ='1' ");
        strSql.append(" AND SERV_TYPE = '0' ");
        strSql.append(" AND STATE_CODE = '0' ");
        strSql.append(" AND trunc(END_DATE) = trunc(SYSDATE) ");
        IDataset result = Dao.qryBySql(strSql, param);
        
        if(IDataUtil.isNotEmpty(result)&&IDataUtil.isNotEmpty(result.getData(0))){
        	IData inParam = new DataMap();
        	inParam.put("KIND_ID", kindId);//
        	inParam.put("OPR_NUM", oprNum);//本次操作的流水号
        	inParam.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));//操作时间
        	inParam.put("PROV_CODE", provCode);//省代码
        	inParam.put("SERV_TYPE", "02");//服务类型：00 – 全部;01 - 语音，短信业务;02 - 数据业务
        	inParam.put("SERIAL_NUMBER", user.getSerialNumber());//用户手机号码
        	for(SvcStateTradeData data : stateList ){
        		if("0".equals(data.getModifyTag())){
        			inParam.put("VALID_DATE", SysDateMgr.date2String(SysDateMgr.string2Date(data.getStartDate(), SysDateMgr.PATTERN_STAND), SysDateMgr.PATTERN_STAND_SHORT));//服务恢复生效时间
        		}	
        	}	
        
        	//调用IBoss
        	IDataset returnDaset = IBossCall.callHttpIBOSS("IBOSS", inParam);
            if(IDataUtil.isEmpty(returnDaset))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP3A303_T3000306_0_0！");
            }
            IData tmpData = returnDaset.getData(0);
            if (!StringUtils.equals("0", tmpData.getString("X_RESULTCODE"))) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "国漫数据恢复功能出错，错误信息【" + tmpData.getString("X_RESULTINFO")+ "】!");
            }
        	
        }
	}

}
