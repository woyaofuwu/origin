package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;



public class CheckPackageConfig extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 3024917760355623778L;
    private static Logger logger = Logger.getLogger(CheckPackageConfig.class);

    //@Override
    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckPackageConfig() >>>>>>>>>>>>>>>>>>");
        }

        String serialNumber = databus.getString("SERIAL_NUMBER");
        String packageId = databus.getString("PACKAGE_ID");
        String userId = databus.getString("USER_ID");
        
//        IDataset pkgInfo = PkgInfoQry.queryPackageById(packageId);//获取td_b_package中的配置
        IData pkgInfo = databus.getData("PM_OFFER_EXT");//UpcCall.qryOfferComChaTempChaByCond(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
    	if(IDataUtil.isNotEmpty(pkgInfo))
        {
        	String rsrvStr5 = pkgInfo.getString("RSRV_STR5","");//取RSRV_STR4值
        	if(rsrvStr5.startsWith("depend"))
        	{
        		if(!SaleActiveUtil.saleActvieHave(serialNumber, rsrvStr5))
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 16062601, "必须办理宽带1+活动，才能办理魔百和活动！！");
        		}
        	}
        }

    	//新增和路通营销活动校验REQ201704140015
    	if("60170526".equals(packageId))
    	{
    		//物联网号码是和路通集团产品成员
    		int number = CParamQry.getRelationMemberNumber(userId, "9B");
    		if(number<=0)
    		{
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024058, SaleActiveBreConst.ERROR_28);
                return false;
    		}
    	}
    	
        if("60170527".equals(packageId) || "60170528".equals(packageId))
        {
        	IDataset relationUuUserIdB56 = RelaUUInfoQry.getEnableRelationByUidBRelaTypeRoleB(userId, "56", "2");
        	if (IDataUtil.isEmpty(relationUuUserIdB56))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024047, SaleActiveBreConst.ERROR_18_2);
                return false;
            }
        	
        	if(relationUuUserIdB56.size() > 1)
        	{
        		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024053, SaleActiveBreConst.ERROR_24);
        		return false;
        	}
        	
        	String userIdA = relationUuUserIdB56.getData(0).getString("USER_ID_A");
        	String mainUserId = RelaUUInfoQry.qryByRelaUserIdARoleCodeB(userIdA, "56", "1", null).getData(0).getString("USER_ID_B");
        	if("60170527".equals(packageId))
        	{
        		IDataset groupMember = CParamQry.get898GroupMember(mainUserId);
        		if(IDataUtil.isEmpty(groupMember))
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024054, SaleActiveBreConst.ERROR_25);
        		}
        		
        		boolean isABCGroup = false;
        		for(int i=0;i<groupMember.size();i++)
        		{
        			String classId = groupMember.getData(i).getString("CLASS_ID");
        			String className = StaticUtil.getStaticValue("CUSTGROUP_CLASSID", classId);
        			if(StringUtils.isNotBlank(className))
        			{
        				if(className.indexOf("A") > -1 || className.indexOf("B") > -1 || className.indexOf("C") > -1)
            			{
        					isABCGroup = true;
        					break;
            			}
        			}
        		}
        		
        		if(!isABCGroup)
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024054, SaleActiveBreConst.ERROR_30);
        		}
        	}
        	else if("60170528".equals(packageId))
        	{
        		UcaData mainUca = UcaDataFactory.getUcaByUserId(mainUserId);
        		
        		String mainOpenDate = mainUca.getUser().getOpenDate();
        		int monthInterval = SysDateMgr.monthInterval(mainOpenDate, SysDateMgr.getSysDate());
        		if(monthInterval < 36)
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024055, SaleActiveBreConst.ERROR_26);
        		}
        		
        		String mainCreditClass = mainUca.getUserCreditClass();
        		if(Integer.parseInt(mainCreditClass) < 3)
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024056, SaleActiveBreConst.ERROR_27);
        		}
        		
        		IData userInfo = UcaInfoQry.qryUserInfoByUserId(mainUserId);
        		String mainStateCode = userInfo.getString("USER_STATE_CODESET");
        		if(!"0".equals(mainStateCode))
        		{
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14024057, SaleActiveBreConst.ERROR_29);
        		}
        	}
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckPackageConfig() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }
}
