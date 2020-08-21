
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.grp;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;

/**
 * 集团成员销户 对应老系统的TCS_DestroyGrpMebInfo子流程
 * 
 * @author liutt
 */
public class DestroyGrpMebInfoAction implements ITradeAction
{
    private static Logger log = Logger.getLogger(DestroyGrpMebInfoAction.class);

    /**
     * 集团接口返回值 <IDataset> <IData> <key>GRP_TRADE_DATA</key> <value> List </value> ... .... </IData> </IDataset>
     * 如果没有需要处理的iTrade请直接new IDatasetList()对象出来。
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        String eparchyCode = btd.getRD().getUca().getUser().getEparchyCode();

        IData params = new DataMap();
        params.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        params.put("USER_ID", userId);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("TRADE_TYPE_CODE", tradeTypeCode);
        params.put("ACCEPT_TIME", btd.getRD().getAcceptTime());// 工单受理时间，传给集团，尽量保证他们工单的执行时间和我们的一致
        if (log.isDebugEnabled())
        {
            log.debug("集团接口[SS.GrpCreditSVC.destroyGrpMemInfo]集团成员销户入参：>" + params.toString());
        }
        IDataset grpTrade = CSAppCall.call("SS.GrpCreditSVC.destroyGrpMemInfo", params);
        if (log.isDebugEnabled())
        {
            log.debug("集团接口[SS.GrpCreditSVC.destroyGrpMemInfo]集团成员销户返回数据：>" + grpTrade.toString());
        }
        if (IDataUtil.isNotEmpty(grpTrade))
        {
            IData data = grpTrade.getData(0);

            List rtTradelList = (List) data.get("GRP_TRADE_DATA");
            if (null != rtTradelList && rtTradelList.size() > 0)
            {
                for (int i = 0; i < rtTradelList.size(); i++)
                {
                    BaseTradeData tData = (BaseTradeData) rtTradelList.get(i);
                    btd.add(serialNumber, tData);// 将集团返回的数据添加到busiTradeData中
                }
            }
        }
        
        List<SvcTradeData> svcTrades = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        Boolean flg910=false,flg20=false;
        String offerInsId="";
        for (SvcTradeData svcTade : svcTrades) {
        	if(svcTade.getElementId().equals("910")&&svcTade.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)){
        		flg910=true;
        		
        		IDataset OfferRelInfo = UserOfferRelInfoQry.qryUserOfferRelInfosByRelTypeAndRelOfferInstId("C",svcTade.getInstId());
        		if(IDataUtil.isNotEmpty(OfferRelInfo)){
        			offerInsId = OfferRelInfo.getData(0).getString("OFFER_INS_ID");
        		}
        	}
            if(svcTade.getElementId().equals("20")&&svcTade.getModifyTag().equals(BofConst.MODIFY_TAG_DEL)){
            	flg20=true;
        	}
        } 

        if(!(flg910&&flg20)&&flg910){
        	if(StringUtils.isNotBlank(offerInsId)){
        		IDataset offerRelInfos = UserOfferRelInfoQry.qryUserOfferRelInfosByOfferInstId(offerInsId);
        		for(int i=0;i<offerRelInfos.size();i++){
        			if("20".equals(offerRelInfos.getData(i).getString("REL_OFFER_CODE"))){
        				OfferRelTradeData offerRel =new OfferRelTradeData(offerRelInfos.getData(i));       	        	
        	     		offerRel.setModifyTag(BofConst.MODIFY_TAG_DEL);     		
        	     		offerRel.setEndDate(SysDateMgr.date2String(new Date(),SysDateMgr.PATTERN_STAND));
        	     		btd.add(serialNumber, offerRel);
        	     		break;
        			}
        		}
        	}
        	 
        }

    }
  
}
