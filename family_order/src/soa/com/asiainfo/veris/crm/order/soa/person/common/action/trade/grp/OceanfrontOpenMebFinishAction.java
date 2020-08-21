package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;

import org.apache.log4j.Logger;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.SynJingxinUtils;

/**
 * 海洋通业务 缴费开机
 * @author xuzh5
 *2018-6-8 10:44:35
 */
public class OceanfrontOpenMebFinishAction implements ITradeFinishAction {
	public static final Logger logger=Logger.getLogger(OceanfrontOpenMebFinishAction.class);

	//停复机标识 0报停   、1复通
     static String itftype="1";
     @Override
	 public void executeAction(IData mainTrade) throws Exception {
		
        String account = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        IData result=new DataMap();
        //判断是否海洋通 船东用户  
        IDataset  idataset=UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "HYT");
        if(IDataUtil.isNotEmpty(idataset)){
	        	if("1".equals(idataset.getData(0).getString("RSRV_STR2"))){
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
            		UserOtherInfoQry.updateOtherStatus(userId, "1");
            	
            }else{
            	logger.debug("---OceanfrontOpenMebFinishAction---海洋通报开，调第三方接口报错.");
            	System.out.println("---OceanfrontOpenMebFinishAction---海洋通报开，调第三方接口报错.");
            }
        }
	}
	
}
