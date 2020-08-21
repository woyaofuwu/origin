
package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.action.reg;

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
 * 宽带业务引起的IMS家庭固话台帐
 * @author zhuoyingzhi 
 * @date 20171121
 *
 */
public class CreateWidenetIMSTradeAction implements ITradeAction
{
    private static transient final Logger logger = Logger.getLogger(CreateWidenetIMSTradeAction.class);

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	logger.debug("------CreateWidenetIMSTradeAction----------");
    	logger.info("------CreateWidenetIMSTradeAction----------");
        String tradeTypeCode = btd.getMainTradeData().getTradeTypeCode();
        String serialNumber = btd.getMainTradeData().getSerialNumber();
        String eparchyCode = btd.getMainTradeData().getEparchyCode();
        logger.debug("------CreateWidenetIMSTradeAction--------tradeTypeCode--"+tradeTypeCode);
        logger.info("------CreateWidenetIMSTradeAction--------tradeTypeCode--"+tradeTypeCode);
        IData param = new DataMap();
        
        param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
       
        
        IData resultInfo = new DataMap();
        resultInfo.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER"));
        resultInfo.put("TRADE_TYPE_CODE",param.getString("TRADE_TYPE_CODE"));
        resultInfo.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        
        /**
         * 	
			宽带报停  603
			宽带报停  604
                                        宽带局方停机 671
                                        宽带局方开机 672

			
			宽带报停时IMs家庭固话要停机:9809
                                        宽带报开时IMs家庭固话要开机:9810
                                        宽带局方停机时IMs家庭固话要停机:9811
                                        宽带局方开机时IMs家庭固话要开机: 9812
                                     
                                        
                                        手机号码报停	     家庭固话同时停机                    9813
			手机号码报开	     家庭固话同时开机                    9814
			手机号码局方停机	 家庭固话同时停机          9815
			手机号码挂失	     家庭固话同时停机                   9816
			大客户担保开机	     家庭固话同时开机      9817
			紧急开机	         家庭固话同时开机               9818
			客户担保开机	     家庭固话同时开机                   9819
			一键停机	         家庭固话同时停机               9820
         */
        if(StringUtils.equals(tradeTypeCode, "603")
        		||StringUtils.equals(tradeTypeCode, "604")
        		||StringUtils.equals(tradeTypeCode, "671")
        		||StringUtils.equals(tradeTypeCode, "672")
        		||StringUtils.equals(tradeTypeCode, "131")
        		||StringUtils.equals(tradeTypeCode, "133")
        		||StringUtils.equals(tradeTypeCode, "136")
        		||StringUtils.equals(tradeTypeCode, "132")
        		||StringUtils.equals(tradeTypeCode, "492")
        		||StringUtils.equals(tradeTypeCode, "497")
        		||StringUtils.equals(tradeTypeCode, "496")
        		||StringUtils.equals(tradeTypeCode, "126")//手机号码局方开机
        		){
            IData data = new DataMap();
            if(!"".equals(serialNumber)&& serialNumber!=null){
            	if(serialNumber.indexOf("KD_") == -1){
            		
            	}else{
            		//存在
            		serialNumber =serialNumber.replaceAll("KD_", "");
            	}
            }
            data.put("SERIAL_NUMBER", serialNumber);
            logger.debug("------CreateWidenetIMSTradeAction--------data--"+data);
        	IData ImsInfo=CSAppCall.callOne("SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
            logger.debug("------CreateWidenetIMSTradeAction--------ImsInfo--"+ImsInfo);
        	if(IDataUtil.isNotEmpty(ImsInfo)){
        		//该手机号码存在IMS家庭固话业务
        		
        		boolean tag=false;
        		
        		//手机报开时, 前台选择是否关联报开标志
        		String  isOpenIms="Y";
        		//IMS家庭固话 userid
        		String  userIdB=ImsInfo.getString("USER_ID_B", "");
        		//IMS家庭固话   手机号码
        		String serialNumberB=ImsInfo.getString("SERIAL_NUMBER_B", "");
        		
        		param.put("SERIAL_NUMBER", serialNumberB);
        		param.put("USER_ID", userIdB);
            	if(StringUtils.equals(tradeTypeCode, "603")){
            		//宽带报停时  IMs家庭固话要停机
            		param.put("TRADE_TYPE_CODE", "9809");
            		tag=isIMSTrade(userIdB, "9809");
            	}else if(StringUtils.equals(tradeTypeCode, "604")){
            		//宽带报开时  IMs家庭固话要开机
            		param.put("TRADE_TYPE_CODE", "9810");
            		tag=isIMSTrade(userIdB, "9810");
            		
               		isOpenIms=btd.getRD().getPageRequestData().getString("IS_OPEN_IMS","");
            		//用主台帐记录前台是否选择关联报开IMS家庭固话
            		btd.getMainTradeData().setRsrvStr1(isOpenIms);
            		
            	}else if(StringUtils.equals(tradeTypeCode, "671")){
            		//宽带局方停机时  IMs家庭固话要停机
            		param.put("TRADE_TYPE_CODE", "9811");
            		tag=isIMSTrade(userIdB, "9811");
            	}else if(StringUtils.equals(tradeTypeCode, "672")){
            		//宽带局方开机时IMs家庭固话要开机
            		param.put("TRADE_TYPE_CODE", "9812");
            		tag=isIMSTrade(userIdB, "9812");
            	}else if(StringUtils.equals(tradeTypeCode, "131")){
            		//手机号码报停	     家庭固话同时停机
            		param.put("TRADE_TYPE_CODE", "9813");
            		tag=isIMSTrade(userIdB, "9813");
            	}else if(StringUtils.equals(tradeTypeCode, "133")){
            		//手机号码报开	     家庭固话同时开机
            		param.put("TRADE_TYPE_CODE", "9814");
            		tag=isIMSTrade(userIdB, "9814");
            		
            		
            		isOpenIms=btd.getRD().getPageRequestData().getString("IS_OPEN_IMS","");
            		//用主台帐记录前台是否选择关联报开IMS家庭固话
            		btd.getMainTradeData().setRsrvStr1(isOpenIms);
            		
            	}else if(StringUtils.equals(tradeTypeCode, "136")){
            		//手机号码局方停机	 家庭固话同时停机
            		param.put("TRADE_TYPE_CODE", "9815");
            		tag=isIMSTrade(userIdB, "9815");
            	}else if(StringUtils.equals(tradeTypeCode, "126")){
            		//手机号码局方开机	 家庭固话同时开机
            		param.put("TRADE_TYPE_CODE", "9821");
            		tag=isIMSTrade(userIdB, "9821");
            	}else if(StringUtils.equals(tradeTypeCode, "132")){
            		//手机号码挂失	   家庭固话同时停机
            		param.put("TRADE_TYPE_CODE", "9816");
            		tag=isIMSTrade(userIdB, "9816");
            	}else if(StringUtils.equals(tradeTypeCode, "492")){
            		//大客户担保开机	     家庭固话同时开机
            		param.put("TRADE_TYPE_CODE", "9817");
            		tag=isIMSTrade(userIdB, "9817");
            	}else if(StringUtils.equals(tradeTypeCode, "497")){
            		//紧急开机	         家庭固话同时开机
            		param.put("TRADE_TYPE_CODE", "9818");
            		tag=isIMSTrade(userIdB, "9818");
            	}else if(StringUtils.equals(tradeTypeCode, "496")){
            		//客户担保开机(实际是担保开机)	     家庭固话同时开机
            		param.put("TRADE_TYPE_CODE", "9819");
            		tag=isIMSTrade(userIdB, "9819");
            	}
            	
        		//为了不执行bre
        		data.put("X_CHOICE_TAG", "1");
            	
                logger.debug("-----CreateWidenetIMSTradeAction-----SS.CreditTradeRegSVC.tradeReg--param:"+param+",tag:"+tag);
                logger.info("-----CreateWidenetIMSTradeAction-----SS.CreditTradeRegSVC.tradeReg--param:"+param+",tag:"+tag);
                if(tag&&"Y".equals(isOpenIms)){
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
     * @param userId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20171121
     */
    public boolean isIMSTrade(String  userId,String tradeTypeCode)throws Exception{
        // 查询用户有效主体服务状态
        IDataset svcStateDataset = UserSvcStateInfoQry.queryUserMainTagScvState(userId);
        if (IDataUtil.isEmpty(svcStateDataset) || svcStateDataset.size() > 2)
        {	
        	//用户服务状态资料错误！用户主服务没有有效的服务状态，请检查数据！
            logger.debug("-----CreateWidenetIMSTradeAction-----isIMSTrade--svcStateDataset:");
            return false;
        }
        
        IDataset userSvcState = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        if (IDataUtil.isEmpty(userSvcState))
        {
        	//获取用户服务状态无数据
        	logger.debug("-----CreateWidenetIMSTradeAction-----isIMSTrade--userSvcState:");
            return false;
        }
        //获取当前IMS家庭固话主服务状态
        String stateCode=svcStateDataset.getData(0).getString("STATE_CODE", "");
    	logger.debug("-----CreateWidenetIMSTradeAction-----isIMSTrade--stateCode:"+stateCode);
    	
    	boolean  exist=false;
    	//获取服务状态变更业务参数
    	IDataset svcStateParaBuf = new DatasetList();
        svcStateParaBuf = TradeSvcStateParamInfoQry.querySvcStateParamByKey(tradeTypeCode, "", "", "0898");
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
