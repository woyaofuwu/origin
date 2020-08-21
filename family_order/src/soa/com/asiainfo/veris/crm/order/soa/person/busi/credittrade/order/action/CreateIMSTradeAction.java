
package com.asiainfo.veris.crm.order.soa.person.busi.credittrade.order.action;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeSvcStateParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;


/**
 * 
 * @author zhuoyingzhi
 * @date 20171106
 *
 */
public class CreateIMSTradeAction implements ITradeAction
{
    private static transient final Logger logger = Logger.getLogger(CreateIMSTradeAction.class);

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	logger.debug("------CreateIMSTradeAction----------");
        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        String serialNumber = btd.getMainTradeData().getSerialNumber();
        String eparchyCode = btd.getMainTradeData().getEparchyCode();
        logger.debug("------CreateIMSTradeAction--------tradeTypeCode--"+tradeTypeCode);
        IData param = new DataMap();
        
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
       
        
        IData resultInfo = new DataMap();
        resultInfo.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER"));
        resultInfo.put("TRADE_TYPE_CODE",param.getString("TRADE_TYPE_CODE"));
        resultInfo.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        
        /**
         * 	高额半停机: 7101 
			高额停机:7110
			欠费停机 7220  
			缴费开机  :7301
		 	家庭欠费停机 :459
		 	家庭缴费开机 :460
			
			IMSJ家庭固话 高额半停机    7901
			IMSJ家庭固话  高额停机       7902
			IMSJ家庭固话  欠费停机       7903
			IMS家庭固话缴费开机            7904

         */
        if(StringUtils.equals(tradeTypeCode, "7101")
        		||StringUtils.equals(tradeTypeCode, "7110")
        		||StringUtils.equals(tradeTypeCode, "7220")
        		||StringUtils.equals(tradeTypeCode, "7301")
        		||StringUtils.equals(tradeTypeCode, "7303")
        		||StringUtils.equals(tradeTypeCode, "7317")
        		||StringUtils.equals(tradeTypeCode, "459")
				||StringUtils.equals(tradeTypeCode, "460")){

            IData data = new DataMap();
            data.put("SERIAL_NUMBER", serialNumber);
            logger.debug("------CreateIMSTradeAction--------data--"+data);
        	IData ImsInfo=CSAppCall.callOne("SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);//该接口只查了MS 没查SM 后续一起改
            logger.debug("------CreateIMSTradeAction--------ImsInfo--"+ImsInfo);
        	if(IDataUtil.isNotEmpty(ImsInfo)){
        		//该手机号码存在IMS家庭固话业务
        		
        		boolean tag=false;
        		//IMS家庭固话 userid
        		String  userIdB=ImsInfo.getString("USER_ID_B", "");
        		//IMS家庭固话   手机号码
        		String serialNumberB=ImsInfo.getString("SERIAL_NUMBER_B", "");
        		
        		param.put("SERIAL_NUMBER", serialNumberB);
        		param.put("USER_ID", userIdB);
            	if(StringUtils.equals(tradeTypeCode, "7101")){
            		//IMSJ家庭固话 高额半停机
            		param.put("TRADE_TYPE_CODE", "7901");
            		tag=isIMSTrade(userIdB, "7901");
            	}else if(StringUtils.equals(tradeTypeCode, "7110")){
            		//IMSJ家庭固话  高额停机
            		param.put("TRADE_TYPE_CODE", "7902");
            		tag=isIMSTrade(userIdB, "7902");
            	}else if(StringUtils.equals(tradeTypeCode, "7220") || StringUtils.equals(tradeTypeCode, "459")){
            		//IMSJ家庭固话  欠费停机
            		param.put("TRADE_TYPE_CODE", "7903");
            		tag=isIMSTrade(userIdB, "7903");
            	}else if(StringUtils.equals(tradeTypeCode, "7301") || StringUtils.equals(tradeTypeCode, "460") ){
            		//IMS家庭固话缴费开机
            		param.put("TRADE_TYPE_CODE", "7904");
            		tag=isIMSTrade(userIdB, "7904");
            	}
            	else if(StringUtils.equals(tradeTypeCode, "7303")){//高额开机
            		//IMS家庭固话缴费开机
            		param.put("TRADE_TYPE_CODE", "7904");
            		tag=isIMSTrade(userIdB, "7904");
            	}
            	else if(StringUtils.equals(tradeTypeCode, "7317")){//批量缴费开机
            		//IMS家庭固话缴费开机
            		param.put("TRADE_TYPE_CODE", "7904");
            		tag=isIMSTrade(userIdB, "7904");
            	}
            	
        		//为了不执行bre
        		data.put("X_CHOICE_TAG", "1");
            	
                logger.debug("-----CreateIMSTradeAction-----SS.CreditTradeRegSVC.tradeReg--param:"+param+",tag:"+tag);
                if(tag){
                	//调接口
                	CSAppCall.callOne("SS.CreditTradeRegSVC.tradeReg", param);
                }else{
                	
                }
        	}else{
        		logger.debug("该手机号码不存在IMS家庭固话业务");
        	}
        	
        }
    }

    /**
     *  判断IMS家庭固话是否需要生成台帐
     * 
     * @param userId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20171106
     */
    public boolean isIMSTrade(String  userId,String tradeTypeCode)throws Exception{
        // 查询用户有效主体服务状态
        IDataset svcStateDataset = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
        if (IDataUtil.isEmpty(svcStateDataset) || svcStateDataset.size() > 2)
        {	
        	//用户服务状态资料错误！用户主服务没有有效的服务状态，请检查数据！
            logger.debug("-----CreateIMSTradeAction-----isIMSTrade--svcStateDataset:");
            return false;
        }
        
        IDataset userSvcState = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        if (IDataUtil.isEmpty(userSvcState))
        {
        	//获取用户服务状态无数据
        	logger.debug("-----CreateIMSTradeAction-----isIMSTrade--userSvcState:");
            return false;
        }
        //获取当前IMS家庭固话主服务状态
        String stateCode=svcStateDataset.getData(0).getString("STATE_CODE", "");
    	logger.debug("-----CreateIMSTradeAction-----isIMSTrade--stateCode:"+stateCode);
    	boolean  exist=false;
//    	if("7904".equals(tradeTypeCode)){
//    		//IMS家庭固话缴费开机
//    		if(!"0".equals(stateCode)){
//    			//暂时处理方式:对非开通状态的，进行开通处理
//    			return true;
//    		}
//    	}else if("7901".equals(tradeTypeCode)||"7902".equals(tradeTypeCode)||"7903".equals(tradeTypeCode)){
//    		//IMSJ家庭固话 高额半停机\IMSJ家庭固话  高额停机 \IMSJ家庭固话  欠费停机
//            if("0".equals(stateCode)){
//            	//当前IMS家庭固话服务开通状态,则进行停机处理
//            	return true;
//            }
//    	}
    	//获取服务状态变更业务参数
    	IDataset svcStateParaBuf = new DatasetList();
        svcStateParaBuf = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode, "", "84004236", "0898");
        if(IDataUtil.isNotEmpty(svcStateParaBuf)){
        	for(int i=0;i<svcStateParaBuf.size();i++){
        		IData paramSvcState = svcStateParaBuf.getData(i);
                String paramOldStateCode = paramSvcState.getString("OLD_STATE_CODE");
                if(stateCode.equals(paramOldStateCode)){
                	exist=true;
                	break;
                }
        	}
        }
    	return exist;
    }
    
}
