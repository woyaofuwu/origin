
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.trade;
 
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.util.BofHelper;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;

public class SimCardTrade extends BaseTrade implements ITrade // extends BaseTrade implements ITrade
{
    protected static Logger log = Logger.getLogger(SimCardTrade.class);

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        SimCardReqData simCardRD = (SimCardReqData) bd.getRD();
        createMainTable(bd, simCardRD.getOldSimCardInfo(), simCardRD.getNewSimCardInfo());
        createResTrade(bd, simCardRD.getOldSimCardInfo(), simCardRD.getNewSimCardInfo());
        //物理网用户进行补换卡操作  add by tanjl  2014-10-30
        //暂时没有用到，屏蔽 Modify yanwu 2015-5-26
        /*if("07".equals(bd.getRD().getUca().getUser().getNetTypeCode())){
        	if(StringUtils.isNotBlank(simCardRD.getNewSimCardInfo().getFlag4G())){
        		geneTradeSvc4g(bd);
        	}
        }*/
    }

    public void createMainTable(BusiTradeData bd, SimCardBaseReqData oldSimInfo, SimCardBaseReqData newSimInfo) throws Exception
    {
        List<MainTradeData> mainList = bd.getTradeDatas(TradeTableEnum.TRADE_MAIN);
        SimCardReqData simCardRD = (SimCardReqData) bd.getRD();
        mainList.get(0).setRemark(simCardRD.getRemark());
        mainList.get(0).setRsrvStr2(bd.getRD().getPageRequestData().getString("OPR_SEQ"));// 外部流水号，物联网反向接口
        mainList.get(0).setRsrvStr3("");// 待确认REASON的用处
        mainList.get(0).setRsrvStr4(simCardRD.getSimNoOccupyTag());//SIM卡是否需要提前占用标志，默认占用。,用于跨区写卡，完工时不需要占用卡
        mainList.get(0).setRsrvStr5(simCardRD.getmPayTag());// 是否开通了手机支付功能
        mainList.get(0).setRsrvStr6(oldSimInfo.getImsi());// old imsi
        mainList.get(0).setRsrvStr7(newSimInfo.getImsi());// new imsi
        String newResTypeCode = StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", newSimInfo.getResTypeCode());
        mainList.get(0).setRsrvStr8(newResTypeCode);// 新SIM卡类型
        mainList.get(0).setRsrvStr9(oldSimInfo.getSimCardNo());
        mainList.get(0).setRsrvStr10(newSimInfo.getSimCardNo());
        /**REQ201610200008 补换卡业务调整需求 20161102 CHENXY3*/
        //log.info("("**********CXY************simCardRD.getRemotecardType()="+simCardRD.getRemotecardType());
        if("142".equals(mainList.get(0).getTradeTypeCode())){
        	mainList.get(0).setRsrvStr1(simCardRD.getRemotecardType());// cxy 补换卡类型 1=换卡 0=补卡
        }
    }

    public void createResTrade(BusiTradeData bd, SimCardBaseReqData oldSimInfo, SimCardBaseReqData newSimInfo) throws Exception
    {

        SimCardReqData simCardRD = (SimCardReqData) bd.getRD();
        IDataset oldSimInfos = UserResInfoQry.getUserResInfosByUserIdResTypeCode(simCardRD.getUca().getUserId(), "1");
        ResTradeData resTradeDataOld = new ResTradeData(oldSimInfos.getData(0));
        resTradeDataOld.setModifyTag(BofConst.MODIFY_TAG_DEL);
        resTradeDataOld.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
        if(StringUtils.isNotBlank(simCardRD.getOldEid())){
        	resTradeDataOld.setRsrvStr3(simCardRD.getOldEid());
        }
        bd.add(simCardRD.getUca().getSerialNumber(), resTradeDataOld);
        
        if(StringUtils.isNotBlank(simCardRD.getNewEid())){
        	IDataset oldSimInfoset = UserResInfoQry.getUserResInfosByUserIdResTypeCode(simCardRD.getUca().getUserId(), "E");
        	if(IDataUtil.isNotEmpty(oldSimInfoset)){
        		ResTradeData resTradeDataOldE = new ResTradeData(oldSimInfoset.getData(0));
                resTradeDataOldE.setModifyTag(BofConst.MODIFY_TAG_DEL);
                resTradeDataOldE.setEndDate(SysDateMgr.getLastSecond(bd.getRD().getAcceptTime()));
                resTradeDataOldE.setRsrvStr3(simCardRD.getOldEid());
                bd.add(simCardRD.getUca().getSerialNumber(), resTradeDataOldE);
        	}
        }
        String instId = SeqMgr.getInstId();
        ResTradeData resTradeDataNew = new ResTradeData();
        resTradeDataNew.setUserId(simCardRD.getUca().getUserId());
        resTradeDataNew.setUserIdA("-1");
        resTradeDataNew.setResTypeCode("1");
        resTradeDataNew.setResCode(newSimInfo.getSimCardNo());
        resTradeDataNew.setInstId(instId);
        resTradeDataNew.setImsi(newSimInfo.getImsi());
        resTradeDataNew.setKi(newSimInfo.getKi());
        resTradeDataNew.setStartDate(bd.getRD().getAcceptTime());
        resTradeDataNew.setEndDate(SysDateMgr.END_DATE_FOREVER);
        resTradeDataNew.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeDataNew.setRsrvStr1(newSimInfo.getResKindCode());
        resTradeDataNew.setRsrvStr2(newSimInfo.getResTypeCode());// 原SIM_TYPE_CODE
        resTradeDataNew.setRsrvStr3(newSimInfo.getAgentSaleTag());
        resTradeDataNew.setRsrvStr4(newSimInfo.getResKindCode());// 原RES_KIND_CODE
        resTradeDataNew.setRsrvStr5(newSimInfo.getEmptyCardId());
        resTradeDataNew.setRsrvTag1(newSimInfo.getResFlag4G());//  资源接口直接返回的2/3/4G卡标识
        
        // 白卡 RSRV_TAG1= 3（买断标识），sim卡空卡 RSRV_STR3 = 1（空卡买断标识）
        resTradeDataNew.setRsrvTag2(newSimInfo.getAgentSaleTag().equals("1") ? "A" : "B");// 新处理C为买断卡
        if (newSimInfo.getAgentSaleTag().equals("1"))
        {
            resTradeDataNew.setRsrvNum5(newSimInfo.getSaleMoney());// 卡价
        }
        resTradeDataNew.setRsrvTag3(newSimInfo.getFlag4G());// 是否为4G卡
        bd.add(simCardRD.getUca().getSerialNumber(), resTradeDataNew);
        if(StringUtils.isNotBlank(simCardRD.getNewEid())){
        	//一号一终端
        	String instId2 = SeqMgr.getInstId();
            ResTradeData resTradeDataNew2 = new ResTradeData();
            resTradeDataNew2.setUserId(simCardRD.getUca().getUserId());
            resTradeDataNew2.setUserIdA("-1");
            resTradeDataNew2.setResTypeCode("E");
            resTradeDataNew2.setResCode(newSimInfo.getSimCardNo());
            resTradeDataNew2.setInstId(instId2);
            resTradeDataNew2.setImsi("");
            resTradeDataNew2.setKi("");
            resTradeDataNew2.setStartDate(bd.getRD().getAcceptTime());
            resTradeDataNew2.setEndDate(SysDateMgr.END_DATE_FOREVER);
            resTradeDataNew2.setModifyTag(BofConst.MODIFY_TAG_ADD);
            resTradeDataNew2.setRsrvStr1("OneNoOneTerminal");
            resTradeDataNew2.setRsrvStr2(simCardRD.getNewEid());
            resTradeDataNew2.setRsrvStr3(simCardRD.getPrimarySerialNumber());
            resTradeDataNew2.setRsrvStr4(simCardRD.getOldEid());
            resTradeDataNew2.setRsrvStr5(simCardRD.getNewImei());
            resTradeDataNew2.setRsrvTag1(newSimInfo.getResFlag4G());//  资源接口直接返回的2/3/4G卡标识
        
            bd.add(simCardRD.getUca().getSerialNumber(), resTradeDataNew2);
        }
        
        IDataset usersvc=  UserSvcInfoQry.getSvcUserId(simCardRD.getUca().getUserId(),"19");
        if(usersvc!=null&&usersvc.size()>0){
            if("141".equals(bd.getRD().getTradeType().getTradeTypeCode())||"142".equals(bd.getRD().getTradeType().getTradeTypeCode())||"143".equals(bd.getRD().getTradeType().getTradeTypeCode())){
                SendIbossForRoam( bd,  oldSimInfo,  newSimInfo);
            }
        }else{
            //add by liangdg3 at 20191017 for REQ201909040009关于国漫业务返销及补换卡未同步用户数据至国漫平台问题的优化 start
            //无限尊享套餐用户补/换卡后，IMSI会发生变化，省BOSS需要在此时通过该接口通知集中节点
            if (BofHelper.isNotPreTrade(bd.getRD())){
                if("141".equals(bd.getRD().getTradeType().getTradeTypeCode())||"142".equals(bd.getRD().getTradeType().getTradeTypeCode())||"143".equals(bd.getRD().getTradeType().getTradeTypeCode())) {
                    IDataset userDiscnts = UserDiscntInfoQry.getAllDiscntInfo(simCardRD.getUca().getUserId());
                    if (IDataUtil.isNotEmpty(userDiscnts)) {
                        for (int i = 0; i < userDiscnts.size(); i++) {
                            IData distrade = userDiscnts.getData(i);
                            String discntcode = distrade.getString("DISCNT_CODE");
                            if(StringUtils.isNotBlank(discntcode)){
                                IDataset discntSet = CommparaInfoQry.getCommpara("CSM", "1807",
                                        discntcode, "ZZZZ");
                                if (IDataUtil.isNotEmpty(discntSet)) {
                                    SendIbossForRoam(bd, oldSimInfo, newSimInfo);
                                    break;
                                }
                            }

                        }
                    }
                }
            }

            //add by liangdg3 at 20191017 for REQ201909040009关于国漫业务返销及补换卡未同步用户数据至国漫平台问题的优化 end
        }
    }
    
    /**
	 * 处理物联网4G GPRS服务信息
	 * 
	 * @author tanjl
	 * @param pd
	 * @param td
	 * @throws Exception
	 */
	public void geneTradeSvc4g(BusiTradeData<BaseTradeData> btd) throws Exception{
		UcaData uca = btd.getRD().getUca();
		//根据物联网客户对应的产品进行查询，如果是行车卫士的则绑定行车卫士通信服务专用4G服务.如果是机器卡、物联通客户选择对应的数据通信服务,PARA_CODE1标识为W-物联通、J-机器卡、X-行车卫士
		IDataset paramInfos = CommparaInfoQry.getCommpara("CSM", "3055", uca.getProductId(), CSBizBean.getTradeEparchyCode());
		if( IDataUtil.isNotEmpty(paramInfos) ){
			//物联网对应的2G/3G服务以及对应的4G服务，PARAM_CODE标识为W-物联通、J-机器卡、X-行车卫士，PARA_CODE1为2G/3G GPRS服务编码，PARA_CODE2为对应的4G服务编码
			IDataset wlwSvcList =  CommparaInfoQry.getCommpara("CSM", "3055", paramInfos.getData(0).getString("PARA_CODE1"), CSBizBean.getTradeEparchyCode());
			IData wlwSvcMap = new DataMap();
			if( IDataUtil.isNotEmpty(wlwSvcList) ){
				//因为用户有可能办理的是2G，也有可能是3G服务，所以在处理2G/3G的时候需要循环处理，但是4G只会有一个GPRS服务，所以不需要再循环处理。
				for(int i = 0; i < wlwSvcList.size(); i++){
					wlwSvcMap = wlwSvcList.getData(i);
					String serviceId = wlwSvcMap.getString("PARA_CODE1");
					//查询用户是否存在2G/3G服务
					List<SvcTradeData> userSvcInfos = uca.getUserSvcBySvcId(serviceId);
					//取消用户原有的GPRS服务
					if( CollectionUtils.isNotEmpty(userSvcInfos) ){
						SvcTradeData tradeService2 = userSvcInfos.get(0).clone();//new SvcTradeData();
						//tradeService2 = userSvcInfos.get(0);
						tradeService2.setEndDate(btd.getRD().getAcceptTime());
						tradeService2.setModifyTag(BofConst.MODIFY_TAG_DEL);
						btd.add(uca.getSerialNumber(), tradeService2);
					}
				}
				
				//查询用户是否存在4G服务
				String serviceId4g = wlwSvcMap.getString("PARA_CODE2");
				List<SvcTradeData> user4gSvcInfos = uca.getUserSvcBySvcId(serviceId4g);
				if( CollectionUtils.isNotEmpty(user4gSvcInfos) ){
					//如果已经存在了4G服务，那么对4G服务不做任何的处理操作
				}else{
					IDataset elementConfigs = ProductPkgInfoQry.queryUserPackage(uca.getProductId(), serviceId4g, "S");
					if( IDataUtil.isEmpty(elementConfigs) ){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据产品编码和元素编码查询对应的元素信息出错，请检查产品配置信息！");
					}
					IData elementConfig = elementConfigs.getData(0);
					String strMainTag = elementConfig.getString("MAIN_TAG", "0");
					String strPackageId = elementConfig.getString("PACKAGE_ID");
					
					SvcTradeData tradeService1 = new SvcTradeData();
					tradeService1.setUserId(uca.getUserId());
					tradeService1.setUserIdA("-1");
					tradeService1.setProductId(uca.getProductId());
					tradeService1.setPackageId(strPackageId);	
					tradeService1.setElementId(serviceId4g);
					tradeService1.setMainTag(strMainTag);
					tradeService1.setInstId(SeqMgr.getInstId());
					tradeService1.setStartDate(SysDateMgr.getSysTime());
					tradeService1.setEndDate(SysDateMgr.getTheLastTime());
					tradeService1.setModifyTag(BofConst.MODIFY_TAG_ADD);
					tradeService1.setRemark("物联网用户换4G服务时处理");
					btd.add(uca.getSerialNumber(), tradeService1);
				}
			}
		}
	}
	/**
     * 补换卡上发平台
     * 
     * @param cycle
     * @throws Exception
     */
    public void SendIbossForRoam(BusiTradeData bd, SimCardBaseReqData oldSimInfo, SimCardBaseReqData newSimInfo) throws Exception
    {
        IData data =new DataMap();
        String opetime=SysDateMgr.getSysDate("yyyyMMddHHmmss");
        //String procode="891";
        String procode="898";
        
        String inid=bd.getRD().getTradeId();
       String openum=procode+opetime+inid.substring(inid.length()-6,inid.length());
        
        data.put("OPR_NUM",openum );
        data.put("OPR_TIME",opetime );
        data.put("PROV_CODE",procode );
        data.put("SERIAL_NUMBER",bd.getMainTradeData().getSerialNumber());
        data.put("VALID_DATE",opetime );
        data.put("OLD_IMSI",oldSimInfo.getImsi() );
        data.put("NEW_IMSI", newSimInfo.getImsi());
        data.put("BIPCODE", "BIP3A304");
        data.put("ACTIVITYCODE", "T3000307");
        data.put("KIND_ID","BIP3A304_T3000307_0_0" );
        //IDataset dataset= CSAppCall.call("SS.InterFor4gBossSVC.totalInterFor4gBoss", data);
        IDataset dataset=   IBossCall.dealInvokeUrl("BIP3A304_T3000307_0_0","IBOSS",data);
        log.info("SimCardTrade liangdg3>>>SendIbossForRoam "+dataset);
    }
}
