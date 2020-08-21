package com.asiainfo.veris.crm.order.soa.person.common.action.finish.pcc;

import org.apache.log4j.Logger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.UserPccException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;

/**
 * 增加优惠时，PCC速率恢复
 * @author Lee
 */
public class DealPCCResumeAction implements ITradeFinishAction
{
    protected static final Logger log = Logger.getLogger(DealPCCResumeAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception
    { 
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        /*String SysDate = SysDateMgr.getDateForSTANDYYYYMMDD(SysDateMgr.getSysTime());
        if("110".equals(tradeTypeCode)&&StringUtils.isNotBlank(rsrvStr3) && rsrvStr3.length() == 8){
        	String StartDate = SysDateMgr.getDateForSTANDYYYYMMDD(rsrvStr3);
        	if(StartDate.compareTo(SysDate)>0)
        		return;
        }*/
        
        IDataset discnts = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        
        //关于下发全球通无限尊享计划限速与国漫FUP限速支撑改造的通知改造，校验
        this.checkGreatChinaLimit(discnts,serialNumber,userId,eparchyCode);
        
        if (discnts != null)
        {
            for (int i = 0, len = discnts.size(); i < len; i++)
            {
                String modifyTag = discnts.getData(i).getString("MODIFY_TAG");
                //添加指定的优惠调用速率恢复接口
                if (!BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                    continue;
                }

                String data_id = discnts.getData(i).getString("DISCNT_CODE").trim();
                
                String configCode = StaticUtil.getStaticValue("PCC_RESUME_DISCNTCODES", data_id);

                if (null != configCode && !"".equals(configCode))
                {
                    IData params = new DataMap();
                    params.put("USER_ID", mainTrade.getString("USER_ID"));
                    params.put("OPERATOR_MIND", "3");
                    params.put(Route.ROUTE_EPARCHY_CODE, "0898");
                    try{
                    IDataset result = CSAppCall.call("SS.PccActionSVC.pccIntf", params);
                    
                    }catch(Exception ex){
                    	log.error("pccErr= "+ex.getMessage());
                    }
                }

            }

        }
    }
    /**
     * 提速包规则校验
     * @param tradeDiscnts
     * @param uca
     * @throws Exception
     */
    private void checkGreatChinaLimit(IDataset tradeDiscnts,String serialNumber,String userId,String eparchyCode) throws Exception {
    	//用户使用数据流量至100G后，不再允许订购大中华提速包(判断是否是二次限速)
    	boolean releaseTag = false;
    	IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2001", "RELEASE_LIMIT_OFFER", eparchyCode);
        if(IDataUtil.isNotEmpty(commparaSet)){
        	for(int i=0;i<tradeDiscnts.size();i++) {
        		String discntCode = tradeDiscnts.getData(i).getString("DISCNT_CODE");
        		String modifyTag = tradeDiscnts.getData(i).getString("MODIFY_TAG");
        		for(int j=0;j<commparaSet.size();j++){
                    String limitDiscntCode = commparaSet.getData(j).getString("PARA_CODE1", "");
                    if(!"".equals(limitDiscntCode) && discntCode.equals(limitDiscntCode) 
                    		&& BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
                    	releaseTag = true;
                    }
                }
        	}
        }
        if(releaseTag) {
        	IData inParam = new DataMap();
            inParam.put("USRIDENTIFIER", "86"+serialNumber);
            inParam.put("USER_ID", userId);
            inParam.put("PARTITION_ID", userId.substring(userId.length()-1,userId.length()));
            IDataset result = PCCBusinessQry.qryPccOperationTypeForSubscriber(inParam);
            if(IDataUtil.isNotEmpty(result)) {
            	for(int k=0;k<result.size();k++) {
                	String operationType = result.getData(k).getString("OPERATION_TYPE");
                	String usrStatus = result.getData(k).getString("USR_STATUS");
                	//如果用户订购大中华提速包，且用户处于超额封顶二次限速，则不能订购大中华提速包
                	if("U".equals(operationType) && "3".equals(usrStatus)) {
                		CSAppException.apperr(UserPccException.CRM_USERPCCINFO_02);
                	}
                }
            }
        }
	}
}
