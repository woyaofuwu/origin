package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class Check5GServiceAction implements ITradeAction
{
    private static Logger logger = Logger.getLogger(Check5GServiceAction.class);

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	boolean is4G = false;
    	boolean is5G = false;
	    boolean isOpen22 = false;
    	String tradeTypeCode = btd.getTradeTypeCode();
        List<SvcTradeData> svclist = btd.get("TF_B_TRADE_SVC");
        List<ResTradeData> reslist = btd.get("TF_B_TRADE_RES");
        
        if (svclist != null && svclist.size() > 0) {
        	for (int i = 0; i < svclist.size(); i++) {
        		String serviceId = svclist.get(i).getElementId();
        		String modifyTag = svclist.get(i).getModifyTag();
        		if (null != serviceId && !"".equals(serviceId))
                {
        			//是否办理了5G服务
        			if("84069442".equals(serviceId) && "0".equals(modifyTag)){
        				is5G = true;
        			}
        			//是否办理了手机上网服务
        			if ("22".equals(serviceId) && "0".equals(modifyTag)){
                     	isOpen22 = true;
                    }
                }
			}
        }

        //开户，携入开户，复机，批量预开户
        if("10".equals(tradeTypeCode) || "40".equals(tradeTypeCode) || "310".equals(tradeTypeCode) || "500".equals(tradeTypeCode)){
            if (is5G) {
            	if (reslist != null && reslist.size() > 0) {
                	for (int i = 0; i < reslist.size(); i++) {
                		String resCode = reslist.get(i).getResCode();
                		String resTypeCode = reslist.get(i).getResTypeCode();
                		String modifyTag = reslist.get(i).getModifyTag();
                		if (null != resCode && !"".equals(resCode))
                        {
                			if(null != resTypeCode && !"".equals(resTypeCode)){
                				//是否4G卡用户
                    			if("0".equals(modifyTag) && "1".equals(resTypeCode)){
                    				is4G = is4GUser(resCode);
                    			}
                			}
                        }
        			}
                }
            	if (!isOpen22 || !is4G)
                {
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "办理5G网络服务，必须开通上网服务、使用USIM卡（即开通4G服务）！");
                }
            }
        }else if("110".equals(tradeTypeCode)){//客户资料变更
            if (is5G) {
            	UcaData uca = btd.getRD().getUca();
                String user_id = uca.getUserId();
                IDataset userInfoQry = UserResInfoQry.getUserResInfoByUserId(user_id);
                if (userInfoQry != null && userInfoQry.size() > 0) {
                	for (int i = 0; i < userInfoQry.size(); i++) {
                		String simCardNo = userInfoQry.getData(i).getString("RES_CODE");
                		String simCardType = userInfoQry.getData(i).getString("RES_TYPE_CODE");
                		if (null != simCardType && !"".equals(simCardType))
                        {
                			if("1".equals(simCardType)){
                				if (null != simCardNo && !"".equals(simCardNo))
                                {
                        			//是否4G卡用户
                    				is4G = is4GUser(simCardNo);
                                }
                			}
                        }
        			}
                }
            	if (!is4G)
                {
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "办理5G网络服务，必须开通上网服务、使用USIM卡（即开通4G服务）！");
                }
            }
        }
    }
    
    
    /**
     * @Description: 是否4G卡用户
     * @param simCardNo
     * @return
     * @throws Exception
     * @author: zhangxing3
     */
    public boolean is4GUser(String simCardNo) throws Exception
    {


        // 调用资源接口
        IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "");

        if (IDataUtil.isNotEmpty(simCardDatas))
        {
            IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simCardDatas.getData(0).getString("RES_KIND_ID"));
            if (IDataUtil.isNotEmpty(reSet))
            {
            	String netTypeCode = reSet.getData(0).getString("NET_TYPE_CODE", "");
    			if ("01".equals(netTypeCode))
    			{
    				return true;// 4G卡
    			}
            }
        }
        else
        {
            // CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
            return false;// 因测试资料不全 暂时返回false
        }
       
        return false;
    }
}