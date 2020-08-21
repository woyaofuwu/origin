
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.common;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 用户必须存在A优惠，且A优惠从开始至结束这个时间段内，须包含B活动的开始时间至结束时间，方可办理B活动
 * 
 * @author yanwu
 */
public class CheckUserDiscntActive extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1398097120421905086L;

    private static Logger logger = Logger.getLogger(CheckUserDiscntActive.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckExistsMultiActive() >>>>>>>>>>>>>>>>>>");
        }
        boolean Resualt = true;
        boolean signed1 = false;
        boolean signed2 = false;
        boolean signed3 = false;
        String strERROR = "用户办理的%s优惠起止时间不包含在此活动内或用户未办理%s优惠，不能办理此营销包";
        //获取办理营销活动
        String saleProductId = databus.getString("PRODUCT_ID");
        String slaePackageId = databus.getString("PACKAGE_ID");
        String eparchyCode = databus.getString("EPARCHY_CODE");
        String campnType = databus.getString("CAMPN_TYPE");
        String strSn = databus.getString("SERIAL_NUMBER");
        //根据办理营销活动，捞配置依赖关系优惠，
        IDataset CommparaActives = CommparaInfoQry.getInfoParaCode1_2("CSM","9979",saleProductId,slaePackageId);
        if( IDataUtil.isNotEmpty(CommparaActives) ){
        	//获取用户所有生效营销活动
            IDataset userDiscnts = databus.getDataset("TF_F_USER_DISCNT");
          //IDataset TradeDiscntset = databus.getDataset("TF_B_TRADE_DISCNT");
            if ( IDataUtil.isEmpty(userDiscnts) ) { 
            	IData CommparaActive = CommparaActives.getData(0);
            	strERROR = CommparaActive.getString("PARAM_NAME","");
            	Resualt = true;
            }else{
            	//根据包获取活动优惠
//            	IDataset result = PkgExtInfoQry.queryPackageExtInfo(slaePackageId, eparchyCode);
            	IData result = databus.getData("PM_OFFER_EXT");//UPackageExtInfoQry.queryPkgEnableByPackageId(slaePackageId);//UpcCall.queryOfferEnableModeByCond(BofConst.ELEMENT_TYPE_CODE_PACKAGE, slaePackageId);
                if ( IDataUtil.isEmpty(result) ){
                	IData CommparaActive = CommparaActives.getData(0);
                	strERROR = CommparaActive.getString("PARAM_NAME","");
                	Resualt = true;
                }else{
                	IData saleactive = result;
                	saleactive.put("CAMPN_TYPE", campnType);
                    saleactive.put("PRODUCT_ID", saleProductId);
                    saleactive.put("SERIAL_NUMBER", strSn);
                    saleactive.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
                    //通过接口获取活动合同时间，在网时间，活动内容
                    IDataset activeDates = CSAppCall.call("CS.SaleActiveDateSVC.callActiveStartEndDate", saleactive);
                    if ( IDataUtil.isEmpty(activeDates) ){
                    	IData CommparaActive = CommparaActives.getData(0);
                    	strERROR = CommparaActive.getString("PARAM_NAME","");
                    	Resualt = true;
                    }else{
                    	IData activeDate = activeDates.getData(0);
                    	String strSaleSD = activeDate.getString("START_DATE");
                    	String strSaleED = activeDate.getString("END_DATE");
                    	
                    	/*saleactive.put("START_DATE", activeDate.getString("START_DATE"));
                        saleactive.put("END_DATE", saleActiveDate.getString("END_DATE"));
                        saleactive.put("BOOK_DATE", saleActiveDate.getString("BOOK_DATE"));
                        saleactive.put("IS_BOOK", saleActiveDate.getString("IS_BOOK"));
                        saleactive.put("ONNET_START_DATE", saleActiveDate.getString("ONNET_START_DATE"));
                        saleactive.put("ONNET_END_DATE", saleActiveDate.getString("ONNET_END_DATE"));*/
                    	
                    	//循环判断是否存在依赖优惠
                    	for (int index = 0, size = CommparaActives.size(); index < size; index++) {
                    		IData CommparaActive = CommparaActives.getData(index);
                    		String DiscntCode = CommparaActive.getString("PARA_CODE3");//获取配置优惠
                        	strERROR = CommparaActive.getString("PARAM_NAME","");
                        	//匹配用户是否存在生效产品和包,包含返回true
                        	//int nIndex = SaleActiveBreUtil.getIDatasetByKey(userDiscnts, "DISCNT_CODE", DiscntCode);
                        	
                        	IData userinfo = UcaInfoQry.qryUserInfoBySn(strSn);
                        	String userid = userinfo.getString("USER_ID","0");
                        	IData param = new DataMap();
                        	param.put("DISCNT_CODE", DiscntCode);
                        	param.put("USER_ID", userid);
                        	IDataset dscnts = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_DISCNT_ALL", param);
                        	
                        	if(IDataUtil.isNotEmpty(dscnts))
                        	{
                        		for(int j=0;j<dscnts.size();j++)
                        		{
	                        		IData dscnt = dscnts.getData(j);
	                        		String strSD = dscnt.getString("START_DATE");
	                        		String strED = dscnt.getString("END_DATE");
	                        		
	                            	int nSd = strSD.compareTo(strSaleSD);
	                            	int nEd = strED.compareTo(strSaleED);
	                            	
	                            	if( nSd <= 0 && nEd >= 0){
	                            		Resualt = false;
	                            		signed3 = true ;
	                            		break;	//只要有，就终止!
	                            	}
	                            	/*
	                            	else{
	                            		String discntName = StaticUtil.getStaticValue(
	                            			     			 CSBizBean.getVisit(), "TD_B_DISCNT", 
	                            			     			 "DISCNT_CODE", "DISCNT_NAME", DiscntCode);
	                            		strERROR = String.format("用户办理的%s优惠起止时间不包含在此活动内，不能办理此营销包", discntName);
	                            		Resualt = true;
	                            	}*/
	                            	if(nSd <= 0 && nEd <= 0 && strED.compareTo(strSaleSD)>0)
	                            	{
	                            		signed1 = true ;                            		
	                            	}
	                            	
	                            	if(nSd >= 0 && nEd >= 0 && strSD.compareTo(strSaleED)<0)
	                            	{
	                            		signed2 = true ;                            		
	                            	}
                        		}
                        		if(signed3 == true)
                        		{
                        			break ;
                        		}
                        	}else{
                        		/*String discntName = StaticUtil.getStaticValue(
           			     			 				CSBizBean.getVisit(), "TD_B_DISCNT", 
           			     			 				"DISCNT_CODE", "DISCNT_NAME", DiscntCode);
                        		strERROR = String.format("用户未办理%s优惠，不能办理此营销包", discntName);*/
                        		Resualt = true;
                        	}
                        }
                    	if(signed1 && signed2)
                    	{
                    		Resualt = false;
                    	}                    	
                    }
                }
            }
        }else{
        	//没有配置，不走此规则
        	Resualt = false;
        }
        //不满足报错
        if( Resualt ){
        	//SaleActiveBreConst.ERROR_23
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 15020301, strERROR);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 退出 CheckExistsMultiActive() >>>>>>>>>>>>>>>>>>");
        }

        return Resualt;
    }

}
