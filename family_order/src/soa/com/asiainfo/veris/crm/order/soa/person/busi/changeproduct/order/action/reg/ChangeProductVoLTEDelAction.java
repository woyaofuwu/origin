package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * @author Administrator
 */
public class ChangeProductVoLTEDelAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		ChangeProductReqData changeProductRD=(ChangeProductReqData)btd.getRD();
		
		UcaData uca=changeProductRD.getUca();
		String userId = uca.getUserId();
		
		//判断是否 取消190服务
 		boolean isCancelVoLte = false;
 		//判断是否 取消5G消息服务
 		boolean isCancel5GService = false;
 		
 		//判断是否 新增190服务
 		boolean isAddVoLte = false;
 		//判断是否 新增5G消息服务
 		boolean isAdd5GService = false;
 		
		//获取主产品台账数据
		List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if (DataUtils.isNotEmpty(svcTrades) && svcTrades.size() > 0)
 		{
 			for (int i = 0; i < svcTrades.size(); i++) 
 			{
 				SvcTradeData svcTradeData = svcTrades.get(i);
 				
 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && 
 					"190".equals(svcTradeData.getElementId()) && 
 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
				{
 					isCancelVoLte = true;
				}
 				if (BofConst.MODIFY_TAG_DEL.equals(svcTradeData.getModifyTag()) && 
 	 					"84076654".equals(svcTradeData.getElementId()) && 
 	 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
 				{
 					isCancel5GService = true;
				}
 				
 				if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && 
 	 					"190".equals(svcTradeData.getElementId()) && 
 	 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
				{
					isAddVoLte = true;
				}
 				if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && 
 	 					"84076654".equals(svcTradeData.getElementId()) && 
 	 					BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType()))
 				{
 					isAdd5GService = true;
				}
 				
 			}
 		}
		
		boolean is4G = false;
        IDataset userInfoQry = UserResInfoQry.getUserResInfoByUserId(userId);
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
    	if (!is4G && isAdd5GService)
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"非4/5G用户，不能办理5G消息服务!");
        }
    	
    	IDataset svcDataset22 = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId, "22");
		if(DataUtils.isEmpty(svcDataset22) && isAdd5GService){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"手机上网服务业务未开通，不能办理5G消息服务!");
        }
		
		if(isAdd5GService && !isAddVoLte){
			//判断是否VOLTE用户
	        IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId, "190");
			if(DataUtils.isEmpty(svcDataset)){//5GMC开通5G消息服务，如果volte服务没有，volte服务也需要同时绑上
				for(int i = 0; i < svcTrades.size(); i++){
					SvcTradeData svcTradeData = svcTrades.get(i);
					if (BofConst.MODIFY_TAG_ADD.equals(svcTradeData.getModifyTag()) && "84076654".equals(svcTradeData.getElementId()) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(svcTradeData.getElementType())){
						SvcTradeData addVolteTrade = svcTrades.get(i).clone();	//新增VoLTE服务开通
						addVolteTrade.setInstId(SeqMgr.getInstId());
						addVolteTrade.setElementId("190");
						btd.add(uca.getSerialNumber(), addVolteTrade);	
					}
		   		}
	        }
		}
		
		//5G消息服务   84076654 
    	IDataset userVideoInfo = UserSvcInfoQry.getSvcUserId(userId, "84076654");
    	if(DataUtils.isNotEmpty(userVideoInfo) && isCancelVoLte && !isCancel5GService)
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已订购5G消息服务，请先退订5G消息服务!");
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