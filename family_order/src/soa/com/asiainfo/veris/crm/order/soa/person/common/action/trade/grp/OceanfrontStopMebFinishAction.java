package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;

import org.apache.log4j.Logger;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.SynJingxinUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;

/**
 * 海洋通业务 欠费停机
 * @author xuzh5
 *
 */
public class OceanfrontStopMebFinishAction implements ITradeFinishAction {
	
	public static final Logger logger=Logger.getLogger(OceanfrontStopMebFinishAction.class);
	//停复机标识 0报停   、1复通
     static String itftype="0";
     @Override
 	public void executeAction(IData mainTrade) throws Exception {
		
		 String account = mainTrade.getString("SERIAL_NUMBER");
	        String userId = mainTrade.getString("USER_ID");
	        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
	        IData result=new DataMap();
        //判断是否海洋通用户 船东用户
        IDataset  idataset=UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "HYT");
        if(IDataUtil.isNotEmpty(idataset)){
        	if("192".equals(tradeTypeCode)&&"0".equals(idataset.getData(0).getString("RSRV_STR2"))){//立即销户时，船员可以调京信接口 船东不行
        		IData param=new DataMap();
                param.put("account", account);
                param.put("appId", SynJingxinUtils.getJingxinAppId());
                param.put("itfType", itftype);
                param.put("timeStamp", String.valueOf(System.currentTimeMillis()));
                //接口调用
                result= SynJingxinUtils.post("stopOrRepeat", param);
        	}else if("1".equals(idataset.getData(0).getString("RSRV_STR2"))){ //欠费停机，欠费销户 得船东才可以调接口
	        	IData param=new DataMap();
	            param.put("account", account);
	            param.put("appId", SynJingxinUtils.getJingxinAppId());
	            param.put("itfType", itftype);
	            param.put("timeStamp", String.valueOf(System.currentTimeMillis()));
	            //接口调用
	            result=SynJingxinUtils.post("stopOrRepeat", param);
        	}
        	
        	if(IDataUtil.isNotEmpty(result)){
            	String code=result.getString("code", "");
            	if("0".equals(code) && StringUtils.isNotBlank(userId))//接口返回成功  修改other表的状态
            		UserOtherInfoQry.updateOtherStatus(userId, "0");
            	
            }else{
            	logger.debug("---OceanfrontStopMebFinishAction---海洋通报停，调第三方接口报错.");
            	System.out.println("---OceanfrontStopMebFinishAction---海洋通报停，调第三方接口报错.");
            }
        	
        }
        
	}
	
	
}
