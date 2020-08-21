package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;

public class Check5GdiscntAction implements ITradeAction
{
    private static Logger logger = Logger.getLogger(Check5GdiscntAction.class);

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	boolean is5Gzx = false;
    	boolean is5Gjs = false;
        List<DiscntTradeData> dislist = btd.get("TF_B_TRADE_DISCNT");
        
        if (dislist != null && dislist.size() > 0) {
        	for (int i = 0; i < dislist.size(); i++) {
        		String discntCode = dislist.get(i).getDiscntCode();
        		String modifyTag = dislist.get(i).getModifyTag();
        		if (null != discntCode && !"".equals(discntCode))
                {
        			//是否办理了5G优享服务
        			if("84018500".equals(discntCode) && "0".equals(modifyTag)){
        				is5Gzx = true;
        			}
        			//是否办理了5G极速服务
        			if("84018501".equals(discntCode) && "0".equals(modifyTag)){
        				is5Gjs = true;
        			}
                }
			}
        }

    	UcaData uca = btd.getRD().getUca();
        String user_id = uca.getUserId();
        IData inData = new DataMap();
        inData.put("USER_ID", user_id);
        IDataset dataList = UserClassInfoQry.queryUserClass(inData);
		if(IDataUtil.isNotEmpty(dataList) && dataList.size()>0){
            IData outMap = dataList.getData(0);	 
            String USER_CLASS = outMap.getString("USER_CLASS");
            
            if(is5Gzx){
            	//全球通-银卡	，全球通-金卡
                if(!"1".equals(USER_CLASS) && !"2".equals(USER_CLASS)){
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "非全球通-银卡或者全球通-金卡用户，不能办理5G优享服务！");
                }
            }
            if(is5Gjs){
            	//全球通-白金卡,全球通-钻石卡
                if(!"3".equals(USER_CLASS) && !"4".equals(USER_CLASS)){
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, "非全球通-白金卡或者全球通-钻石卡用户，不能办理5G极速服务！");
                }
            }
		}else{
			if(is5Gzx){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "非全球通-银卡或者全球通-金卡用户，不能办理5G优享服务！");
            }
            if(is5Gjs){
            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "非全球通-白金卡或者全球通-钻石卡用户，不能办理5G极速服务！");
            }
		}
    }
}