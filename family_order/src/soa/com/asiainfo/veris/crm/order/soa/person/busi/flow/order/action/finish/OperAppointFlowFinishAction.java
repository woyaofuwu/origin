package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.util.OfferUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOfferRelInfoQry;



/**   
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: OperAppointFlowFinishAction
 * @Description: 当操作了定向流量时，则需要调用IBOSS接口实时推送给第三方电商平台。
 *
 * @version: v1.0.0
 * @author: zhangbo18   
 * @date: 2017-5-22 下午3:10:25 
 */
public class OperAppointFlowFinishAction implements ITradeFinishAction 
{
    Logger log = Logger.getLogger(OperAppointFlowFinishAction.class);
    
	@Override
	public void executeAction(IData mainTrade) throws Exception {
	    try{
    	    String tradeId = mainTrade.getString("TRADE_ID");
    	    IDataset tradeDiscntList =new DatasetList();
            tradeDiscntList = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
    		IDataset tradeOfferRels = TradeOfferRelInfoQry.getOfferRelByTradeId(tradeId);//查询trade_offer_rel表回填PRODUCT_ID,PACKAGE_ID
    		tradeDiscntList=OfferUtil.fillStructAndFilterForPf(tradeDiscntList, tradeOfferRels);
    		 if (log.isDebugEnabled())
    			 log.debug(">>>>> 进入 OperAppointFlowFinishAction>>>>>tradeDiscntList:"+tradeDiscntList);
            if (IDataUtil.isNotEmpty(tradeDiscntList)){
                //校验当前操作是否为变更操作(即A订购    B取消)
                boolean isModifyOper = isModifyOper(tradeDiscntList);
                for(int i = 0 ; i < tradeDiscntList.size() ; i++)
                {
                    IData discntInfo = tradeDiscntList.getData(i);
                    IData ibossParam = buildCallIbossParam(mainTrade, discntInfo,"FALSE",isModifyOper);
                    if (log.isDebugEnabled())
           			 log.debug(">>>>> 进入 OperAppointFlowFinishAction>>>>>ibossParam:"+ibossParam);
                    if (IDataUtil.isNotEmpty(ibossParam) && ("0".equals(discntInfo.getString("MODIFY_TAG",""))||"1".equals(discntInfo.getString("MODIFY_TAG",""))))
                    {
                        //调用IBOSS接口
                        IBossCall.dealInvokeUrl("BIP3B520_T3000520_0_0", "IBOSS", ibossParam);
                        //如果是变更操作，则需要调两次IBOSS接口，一次03(变更取消)操作   一次04(变更订购)操作
                        if (log.isDebugEnabled())
                  			 log.debug(">>>>> 进入 OperAppointFlowFinishAction>>>>>isModifyOper:"+isModifyOper+",STATUS:"+ibossParam.getString("STATUS"));
                        if ("03".equals(ibossParam.getString("STATUS")) && !isModifyOper)
                        {
                            ibossParam = buildCallIbossParam(mainTrade, discntInfo,"TRUE",isModifyOper);
                            if (IDataUtil.isNotEmpty(ibossParam))
                            {
                                //调用IBOSS接口
                                IBossCall.dealInvokeUrl("BIP3B520_T3000520_0_0", "IBOSS", ibossParam);
                            }
                        }
                    }
                }
            }
	    }catch (Exception e) {
            log.debug("OperAppointFlowFinishAction.catch--->"+e.getMessage());
        }
	}
	
	/**
	 * 校验该操作是否为视频流量变更操作
	 * @param tradeDiscntList
	 * @return
	 * @throws Exception
	 */
	public boolean isModifyOper(IDataset tradeDiscntList) throws Exception
	{
	    String modify_tag = "";
        for(int i = 0 ; i < tradeDiscntList.size() ; i++)
        {
            IData discntInfo = tradeDiscntList.getData(i);
            IDataset commparaInfo = getAppointFlow(discntInfo.getString("DISCNT_CODE"));
            //不为空，则表示该资费为视频流量包资费
            if (IDataUtil.isNotEmpty(commparaInfo))
            {
                if (StringUtils.isNotBlank(modify_tag) && !modify_tag.equals(discntInfo.getString("MODIFY_TAG")))
                {
                    return true;
                }else{
                    modify_tag = discntInfo.getString("MODIFY_TAG");
                }
            }
        }
	    return false;
	}
	/***
	 * 构建调用IBOSS的参数
	 * @param mainTrade
	 * @param discntInfo
	 * @return
	 * @throws Exception
	 */
	public IData buildCallIbossParam(IData mainTrade,IData discntInfo,String isCancel,boolean isModifyOper) throws Exception
	{
	    IData params = new DataMap();
	    IDataset commparaInfo = getAppointFlow(discntInfo.getString("DISCNT_CODE"));
        //不为空，则表示该资费为视频流量包资费
        if (IDataUtil.isNotEmpty(commparaInfo))
        {
            params = getServiceID(discntInfo,commparaInfo,isCancel);
            //APPID可能为-1，如果为-1，则不需要调用.
            if (StringUtils.isBlank(params.getString("SERVICEID_LIST")))
            {
                return null;
            }
        }else{
            return null;
        }
        params.put("KIND_ID", "BIP3B520_T3000520_0_0");
        params.put("BOSS_PRODUCTID", discntInfo.getString("DISCNT_CODE"));
        params.put("OPR_NUMB", discntInfo.getString("INST_ID"));
        params.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
        IData crmProduct = getCrmProductsInfo(mainTrade,discntInfo);
        params.put("GOODS_ID", IDataUtil.isNotEmpty(crmProduct) ? crmProduct.getString("CTRM_PRODUCT_ID") : "");
        //新增或退订操作
        if ("0".equals(discntInfo.getString("MODIFY_TAG")) || 
                "1".equals(discntInfo.getString("MODIFY_TAG")))
        {
            params.put("STATUS", "0".equals(discntInfo.getString("MODIFY_TAG")) ? (isModifyOper ? "04" : "01") : (isModifyOper ? "03":"02"));
            
        }
        
        //退订及变更取消时，状态生效时间为失效时间
        //变更订购的状态生效时间为下月生效时间
        if (StringUtils.isBlank(params.getString("EFFECT_TIME")))
        {
            if ("1".equals(discntInfo.getString("MODIFY_TAG")) && !"TRUE".equals(isCancel))
            {
                params.put("EFFECT_TIME", SysDateMgr.getDateForYYYYMMDD(discntInfo.getString("END_DATE"))+"235959");
                params.put("EXPIRE_TIME", SysDateMgr.getDateForYYYYMMDD(discntInfo.getString("END_DATE"))+"235959");

            }else{
                //订购时状态生效时间为起始时间
                params.put("EFFECT_TIME", SysDateMgr.getDateForYYYYMMDD(discntInfo.getString("START_DATE"))+"000000");
                params.put("EXPIRE_TIME", SysDateMgr.getDateForYYYYMMDD(discntInfo.getString("END_DATE"))+"235959");

            }
        }
        
        params.put("ORIGIN",discntInfo.getString("RSRV_STR3") );
        params.put("ORDER_ID",discntInfo.getString("RSRV_STR4") );
        params.put("SUB_ORDER_ID",discntInfo.getString("RSRV_STR5") );

        params.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	    return params;
	}
	/**
	 * 查询全网统一APP服务编码
	 * @param data

	 * @throws Exception
	 */
	public IData getServiceID(IData data,IDataset commparaInfo,String isCancel) throws Exception
	{
	    IData params = new DataMap();
	    
        StringBuffer sb = new StringBuffer();
        //查询compara表里面的paramCode20,如果是绑定APP的情况则存在值，无需查询attr记录
        if (IDataUtil.isNotEmpty(commparaInfo) && 
                IDataUtil.isNotEmpty(commparaInfo.getData(0)) &&
                StringUtils.isNotBlank(commparaInfo.getData(0).getString("PARA_CODE20")))
        {
            sb.append(commparaInfo.getData(0).getString("PARA_CODE20"));
        }else{
            //目前只有18元走此分支
            //否则为一个优惠对应多个APP的情况，需查询ATTR表进行确认
            IData input=new DataMap();
            IDataset attrInfo= null;
            input.put("DISCNT_CODE",data.getString("DISCNT_CODE"));
            input.put("TRADE_ID", data.getString("TRADE_ID"));
            //退订或变更操作为月底失效，所以查询当前生效的数据
            if ("1".equals(data.getString("MODIFY_TAG")) || "2".equals(data.getString("MODIFY_TAG")))
            {
                //查询attr属性，视频流量包的appId
                attrInfo=Dao.qryByCodeParser("TF_B_TRADE_ATTR", "SEL_BY_DISCNTATTR_EFFECTIVENOW", input,Route.getJourDb(BizRoute.getRouteId()));
                if ("TRUE".equals(isCancel))
                {//取消操作时，获取下个月生效的数据
                    attrInfo=Dao.qryByCodeParser("TF_B_TRADE_ATTR", "SEL_BY_DISCNTATTR_NEXTMONTH", input,Route.getJourDb(BizRoute.getRouteId()));
                }
            }else{
                attrInfo=Dao.qryByCodeParser("TF_B_TRADE_ATTR", "SEL_BY_DISCNTATTR_EFFECTIVE", input,Route.getJourDb(BizRoute.getRouteId()));
            }
            if(IDataUtil.isNotEmpty(attrInfo)){//查attr表是否有app列表，最终方案需要用到
                for(int a=0;a<attrInfo.size();a++){ 
                    String appId=attrInfo.getData(a).getString("ATTR_VALUE","");
                    if (!"-1".equals(appId))
                    {
                        sb.append(a != (attrInfo.size() - 1) ? appId+";" : appId);
                        if("2".equals(data.getString("MODIFY_TAG")))
                        {
                            params.put("STATUS", "TRUE".equals(isCancel) ? "04" : "03");
                            params.put("EFFECT_TIME", "TRUE".equals(isCancel) ? 
                                SysDateMgr.getDateForYYYYMMDD(attrInfo.getData(a).getString("START_DATE"))+"000000" : 
                                SysDateMgr.getDateForYYYYMMDD(attrInfo.getData(a).getString("END_DATE"))+"235959");
                        }
                    }
                }          
            }
        }
        
        String service_id = sb.toString();
        if (StringUtils.isNotBlank(service_id))
        {
            params.put("PRODUCT_TYPE", "01");
            params.put("SERVICEID_LIST", service_id);
        }
        return params;
	}
	/****
	 * 查询资费静态参数配置
	 * @param discnt_code
	 * @return
	 * @throws Exception
	 */
	public IDataset getAppointFlow(String discnt_code) throws Exception
    {
        IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017", discnt_code, "IS_VIDEO_PKG",null);
        if (IDataUtil.isNotEmpty(paraList))
        {
            return paraList;
        }
        return null;
    }
	 
	/***
	  *  根据产品id找出全网对应的信息，PRODUCT_ID 与 PACKAGE_ID 会默认查-1
	  * @param mainTrade
	  * @param discntInfo
	  * @return
	  * @throws Exception
	  */
	public IData getCrmProductsInfo(IData mainTrade,IData discntInfo) throws Exception {    
	    IData params = new DataMap();
	    params.put("ELEMENT_ID", discntInfo.getString("DISCNT_CODE"));
        params.put("ELEMENT_TYPE_CODE", "D");
        params.put("EPARCHY_CODE", mainTrade.getString("EPARCHY_CODE"));
        params.put("PRODUCT_ID", discntInfo.getString("PRODUCT_ID"));
        params.put("PACKAGE_ID", discntInfo.getString("PACKAGE_ID"));
	    IDataset result = Dao.qryByCodeParser("TD_B_CTRM_RELATION","SEL_BY_PRODUCT_DEF", params,Route.CONN_CRM_CEN);
	    if (IDataUtil.isNotEmpty(result))
	    {
	        return result.getData(0);
	    }
	    return null;
    }
}
