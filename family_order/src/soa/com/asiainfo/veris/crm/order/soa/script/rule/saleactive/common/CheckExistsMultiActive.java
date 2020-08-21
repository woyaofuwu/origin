
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;

/**
 * 营销活动通用规则校验 用户办理过营销活动，不可重复办理： 判断条件： 1、用户存在生效的营销活动 2、用户当前办理的营销活动和生效的营销活动不存在顺延关系
 * 
 * @author Mr.Z
 */
public class CheckExistsMultiActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1398097120421905086L;

    private static Logger logger = Logger.getLogger(CheckExistsMultiActive.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckExistsMultiActive() >>>>>>>>>>>>>>>>>>");
        }

        IDataset userSaleActives = databus.getDataset("TF_F_USER_SALE_ACTIVE");

        if (IDataUtil.isEmpty(userSaleActives))
        {
            return true;
        }

        IData qryParam = new DataMap();
        
        qryParam.put("USER_ID", databus.getString("USER_ID"));
        qryParam.put("PARAM_CODE", "601");
        
        IDataset _tempActiveDataset = CParamQry.getLimitActives(databus.getString("USER_ID"), "601");
        
        String saleProductId = databus.getString("PRODUCT_ID");
        String slaePackageId = databus.getString("PACKAGE_ID");
        String eparchyCode = databus.getString("EPARCHY_CODE");

        if (SaleActiveUtil.isQyyx(databus.getString("CAMPN_TYPE")))
        {
            IDataset qyyxActives = SaleActiveBreUtil.getQyyxActives(userSaleActives);

            if (IDataUtil.isEmpty(qyyxActives))
            {
                return true;
            }

            IData maxEndDateActive = SaleActiveBreUtil.getMaxEndDateActiveFromUserSaleActive(qyyxActives);

            String userSaleProductId = maxEndDateActive.getString("PRODUCT_ID");
            String userSalePackgeId = maxEndDateActive.getString("PACKAGE_ID");

            IDataset bookLimits = CommparaInfoQry.getCommparaBy1to4("CSM", "967", "0", saleProductId, userSaleProductId, slaePackageId, userSalePackgeId, eparchyCode);

            if (IDataUtil.isNotEmpty(bookLimits))
                return true;
        }
        else
        {
            IDataset noQyyxActives = SaleActiveBreUtil.getNoQyyxActives(userSaleActives);

            if (IDataUtil.isEmpty(noQyyxActives))
                return true;

            boolean hasActive = false;

            for (int index = 0, size = noQyyxActives.size(); index < size; index++)
            {
                IData tempData = noQyyxActives.getData(index);
                if (saleProductId.equals(tempData.getString("PRODUCT_ID")))
                {
                    hasActive = true;
                    break;
                }
            }

            IDataset limits1007 = CommparaInfoQry.getCommparaAllColByParser("CSM", "1007", saleProductId, eparchyCode);

            if (!hasActive || IDataUtil.isNotEmpty(limits1007))
                return true;
        }

        boolean errorFlag = true ;
    	//增加判断产品变更标记,如果是升档，且是活动变更活动则允许变更，或包年变活动，且是最后一个月也允许
    	String changeUpDownTag = databus.getString("CHANGE_UP_DOWN_TAG",""); //0 不变，1：升档,2:降档,3:产品变，速率不变
    	/**
    	 * 新增非签约类续约配置commpara=167。用于宽带包年营销活动续约
    	 * chenxy3
    	 * */
    	IDataset limit167NEW = CommparaInfoQry.getCommparaByCodeCode1("CSM", "167", databus.getString("PRODUCT_ID"), databus.getString("PACKAGE_ID"));
    	if (IDataUtil.isNotEmpty(limit167NEW)&&"".equals(changeUpDownTag))
        {
        	changeUpDownTag="0";//【宽带产品变更】功能带过来的；营销活动办理为空。这里借该值做宽带包年活动续约，最后一个月可以办理。
        }
    	if("1".equals(changeUpDownTag) || "2".equals(changeUpDownTag) || "3".equals(changeUpDownTag) || "0".equals(changeUpDownTag))
    	{
    		if("1".equals(changeUpDownTag))
    			errorFlag = false ;
    		else
    		{
    			//取活动的结束时间，如果是最后一个月，则允许变更
    			for(int i = 0 ; i < _tempActiveDataset.size() ; i++ )
    			{
    				String endDate = _tempActiveDataset.getData(i).getString("END_DATE");
    				String packageId = _tempActiveDataset.getData(i).getString("PACKAGE_ID");
    				IDataset pkgExtDataSet = UpcCall.queryEnableModeRelByOfferId("K", packageId);//PkgExtInfoQry.queryPackageExtInfo(packageId, databus.getString("EPARCHY_CODE","0898"));
    				String endEnableTag = pkgExtDataSet.getData(0).getString("DISABLE_MODE");//END_ENABLE_TAG
    				if("0".equals(endEnableTag))
    				{
    					//如果是绝对时间，则取TD_S_COMMPARA ,181参数查询合约期限，重新计算结束时间
    					IDataset com181 = CommparaInfoQry.getCommparaInfoByCode("CSM", "181", "-1", packageId, "0898");
    					String months = "12";//默认12个月
    		        	if (IDataUtil.isNotEmpty(com181))
    		        	{
    		        		months = com181.getData(0).getString("PARA_CODE4","12");
    		        	}
    		        	String startDate = _tempActiveDataset.getData(i).getString("START_DATE");
//    		        	endDate = SysDateMgr.endDate(startDate, "1", pkgExtDataSet.getData(0).getString("END_ABSOLUTE_DATE"), months, "3");
    		        	
    		        	endDate = SysDateMgr.addDays(SysDateMgr.addMonths(startDate, Integer.valueOf(months)),-1) + SysDateMgr.getEndTime235959();
    				}
    				String bookDate = databus.getString("BOOKING_DATE",""); //预约生效时间，如果大于结束时间，也允许办理
    				if(bookDate != null && !"".equals(bookDate))
    				{
    					if(SysDateMgr.compareTo(bookDate, endDate) > 0)
            			{
    						errorFlag = false ;
            			}
    					else
                        {
                            errorFlag = true;
                        }
    				}
    				else
    				{
    					String lastDayOfThisMonth = SysDateMgr.getLastDateThisMonth();
        				if(endDate.substring(0, 7).equals(lastDayOfThisMonth.substring(0, 7)))
        				{
        					errorFlag = false ;
        				}
        				else
        				{
        				    errorFlag = true;
        				}
    				}
    			}
    		}
    	}
    	
    	//BUG20170711092611  魔百和活动无法续约问题 
    	IData modata = new DataMap();
    	modata.put("PRODUCT_ID", saleProductId);
    	modata.put("PACKAGE_ID", slaePackageId);
    	modata.put("USER_ID", databus.getString("USER_ID"));
    	if(IDataUtil.isNotEmpty(UserSaleActiveInfoQry.querySaleActiveByUserId(modata)))
    	{
    		errorFlag = false;
    	}
    	
    	
    	String wideNetMoveSign = databus.getString("WIDENET_MOVE_SALEACTIVE_SIGN","0");
    	String SpecialSaleFlag = databus.getString("SPECIAL_SALE_FLAG",""); //add 20170511
    	if("1".equals(wideNetMoveSign)|| "1".equals(SpecialSaleFlag)) 
    		errorFlag = false;
    	if(errorFlag)
    	{
    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14062201, SaleActiveBreConst.ERROR_19);
    	}
    		

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 退出 CheckExistsMultiActive() >>>>>>>>>>>>>>>>>>");
        }

        return true;
    }

}
