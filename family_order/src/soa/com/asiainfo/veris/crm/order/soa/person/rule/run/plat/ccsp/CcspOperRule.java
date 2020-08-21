
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.ccsp;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class CcspOperRule extends BreBase implements IBREScript
{
	public CcspOperRule()
    {
    }
	
	private static Logger logger = Logger.getLogger(CcspOperRule.class);
	
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CcspOperRule() >>>>>>>>>>>>>>>>>>");
		
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);
        PlatOfficeData officeData = psd.getOfficeData();
        
        if (!"79".equals(officeData.getBizTypeCode())){
        	return true;
        }
        
		IData param = new DataMap();
		//增加用户主动退订则不自动开通和飞信业务
		if ("05".equals(psd.getOprSource()))
		{
			param.put("USER_ID", uca.getUserId());
			IDataset userPlatSvcs = PlatInfoQry.getUserPlatSvc(param,"SEL_BY_USERID_NEW"); 
			for(int i = 0 ; null != userPlatSvcs && i < userPlatSvcs.size() ; i++){
				IData userPlatsvc = userPlatSvcs.getData(i);
				if ("79".equals(userPlatsvc.getString("BIZ_TYPE_CODE")) &&
						"1".equals(userPlatsvc.getString("RSRV_STR4"))){
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027_4.toString(), PlatException.CRM_PLAT_3027_4.getValue());
					return false;
				}
			}
		}
			//非2G/3G/4G的中国移动用户不允许办理和飞信业务
			IData userBrandInfo = ParamInfoQry.getCommparaByCode("CSM", "4602", "USERPRODUCTINFO", uca.getUser().getEparchyCode()).getData(0);
			String brandCodeStr = userBrandInfo.getString("PARAM_NAME");
			String[] brandCodeArr = null != brandCodeStr ? brandCodeStr.split(",") : new String[1];
			
			param.put("USER_ID", uca.getUserId());
			IDataset userInfoChanage = PlatInfoQry.getUserChangeInfo(param, "SEL_BY_USERID");
			boolean existFlag = false;
			for(int i = 0 ; !existFlag && null != userInfoChanage && i < userInfoChanage.size() ; i++){
				IData userInfoChgData = userInfoChanage.getData(i);
				for(int j = 0 ; !existFlag && j < brandCodeArr.length ; j++){
					if (brandCodeArr[j].equals(userInfoChgData.getString("PRODUCT_ID"))){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027_5.toString(), PlatException.CRM_PLAT_3027_5.getValue());
						return false;
					}
				}
			}
			/*
			 * a)	通过融合通信Native终端自动开通：仅支持中国移动4G用户“三新”的一并开通；
			 * b)	通过营业厅、短信、10086坐席和网厅渠道开通：中国移动2/3/4G用户可申请开通“三新”，或开通“两新”，或仅开通VoLTE；
			 * c)	通过融合通信APP客户端开通：中国移动2/3/4G用户和他网用户可开通“两新”；
			 * d)	通过融合通信PC客户端开通：中国移动2/3/4G用户和他网用户可开通“两新”。
			 * (2)	业务功能关闭
			 * 用户可通过营业厅、短信、10086坐席和网厅渠道申请关闭融合通信服务，用户可申请关闭“三新”，或关闭VoLTE。
			 */
			
			/*if (PlatConstants.OPER_ORDER.equals(psd.getOperCode())){
				//三新业务
				if ("01".equals(databus.getString("RANGE"))){
					if ("05".equals(psd.getOprSource())||"23".equals(psd.getOprSource())){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027.toString(), PlatException.CRM_PLAT_3027.getValue());
						return false;
					}
				//二新业务
				}else if ("02".equals(databus.getString("RANGE"))){
					if("16".equals(psd.getOprSource())){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027_1.toString(), PlatException.CRM_PLAT_3027_1.getValue());
						return false;
					}
				//VOLTE业务
				}else if ("03".equals(databus.getString("RANGE"))){
					if ("05".equals(psd.getOprSource())
							||"23".equals(psd.getOprSource())
							||"16".equals(psd.getOprSource())){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027_2.toString(), PlatException.CRM_PLAT_3027_2.getValue());
						return false;
					}
				}
			}else if("07".equals(psd.getOperCode())){
				if ("02".equals(databus.getString("RANGE"))){
					BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027_3.toString(), PlatException.CRM_PLAT_3027_3.getValue());
					return false;
				}
			}*/

			if (PlatConstants.OPER_ORDER.equals(psd.getOperCode())){
				//三新业务
				if ("16".equals(psd.getOprSource())){
					if (!checkSimCard(uca)){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2052","该渠道仅支持中国移动4G用户受理和飞信业务。");
						return false;
					}
				}
				//三新业务
				if ("01".equals(databus.getString("RANGE"))){
					if ("05".equals(psd.getOprSource())||"23".equals(psd.getOprSource())){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027.toString(), PlatException.CRM_PLAT_3027.getValue());
						return false;
					}
				//VOLTE业务
				}else if ("03".equals(databus.getString("RANGE"))){
					if ("05".equals(psd.getOprSource())
							||"23".equals(psd.getOprSource())){
						BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_3027_2.toString(), PlatException.CRM_PLAT_3027_2.getValue());
						return false;
					}
				}
			}
			
		if (logger.isDebugEnabled())
			logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 退出 CcspOperRule() >>>>>>>>>>>>>>>>>>");
		return true;
    }

    public boolean checkSimCard(UcaData ucaData) throws Exception{
    	UserTradeData userInfo = ucaData.getUser();
    	String userId = null != ucaData ? userInfo.getUserId() : "";
        List<ResTradeData> resInfos = ucaData.getUserAllRes();        
        String simCardNo = "" ;
        for(ResTradeData resInfo:resInfos){
        	if("1".equals(resInfo.getResTypeCode()))
        		simCardNo=resInfo.getResCode();
        }       
        IDataset simCardInfos = ResCall.getSimCardInfo("0", simCardNo, "", "1");
        IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simCardInfos.getData(0).getString("RES_TYPE_CODE"));
        if (IDataUtil.isNotEmpty(reSet))
        {
        	String netTypeCode = reSet.getData(0).getString("NET_TYPE_CODE","");
        	String parentTypeCode = reSet.getData(0).getString("PARENT_TYPE_CODE","");
        	if("01".equals(netTypeCode)&&("1".equals(parentTypeCode)||"6".equals(parentTypeCode))){
        		return true;
        	}else{
        		return false;
        	}
        }else{
        	return false;
        }
    }
}
