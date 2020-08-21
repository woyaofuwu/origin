
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.order.requestdata.WidenetMoveRequestData;

public class CheckCommissioningFeeActiveLimitsAction implements ITradeAction
{
	//79082918	机顶盒调测费包(度假扶贫专用)(预受理)
	//79082908	机顶盒调测费包(度假扶贫专用)(转正包)
	//79082818	智能网关调测费包(度假扶贫专用)(预受理)
	//79082808	智能网关调测费包(度假扶贫专用)(转正包)
	//60828536	缴调测费赠话费活动(光猫)
	//66000308	缴调测费赠话费活动(机顶盒)


	public void executeAction(BusiTradeData btd) throws Exception
    {
		String tradeTypeCode = btd.getTradeTypeCode();
		String serialNumberWD = btd.getRD().getUca().getSerialNumber(); //宽带号码
		String userIdWD = btd.getRD().getUca().getUserId(); //宽带USER_ID
		String serialNumber = "";//手机号码
		String userId = btd.getRD().getUca().getUserId();//手机USER_ID
		boolean HasActiveTag = false;
		//String isBusinessWide = btd.getRD().getPageRequestData().getString("IS_BUSINESS_WIDE", "");
		
		if ("600".equals(tradeTypeCode) || "606".equals(tradeTypeCode))
		{
			if(serialNumberWD.length() > 14)
			{
				return;
			}
	        if(serialNumberWD.startsWith("KD_"))
	        {
	        	serialNumber = serialNumberWD.substring(3);
	        }
	        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
	        if(IDataUtil.isNotEmpty(userInfos)){
	        	userId = userInfos.getData(0).getString("USER_ID","");
	        }
		}
		
		if ("4800".equals(tradeTypeCode))
		{
			userId = btd.getRD().getUca().getUserId();//手机USER_ID
			String moSaleProductId2 = btd.getRD().getPageRequestData().getString("MO_PRODUCT_ID2", "");
			String moSalePackageId2 = btd.getRD().getPageRequestData().getString("MO_PACKAGE_ID2", "");

			IDataset ids = UserSaleActiveInfoQry.querySaleActiveByUserIdProcess(userId,"0");

			if (IDataUtil.isNotEmpty(ids) )
	        { 
	            for(int i = 0; i < ids.size(); i++)
	            {
	            	String userSaleProductId = ids.getData(i).getString("PRODUCT_ID","");
	            	String userSalePackageId = ids.getData(i).getString("PACKAGE_ID","");	

					if ( "84071247".equals(userSalePackageId) || "84071246".equals(userSalePackageId)
							|| "84071443".equals(userSalePackageId) || "84071442".equals(userSalePackageId))
		            {
		          	  	HasActiveTag = true;
		            }
	            }
	        }
			if(HasActiveTag && (!"79082918".equals(moSalePackageId2) && !"79082908".equals(moSalePackageId2)))
			{
    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您有扶贫活动，魔百和开户时请选择机顶盒调测费扶贫专用包!");
			}
            if(!HasActiveTag &&("79082918".equals(moSalePackageId2) || "79082908".equals(moSalePackageId2)))
			{
    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您没有扶贫活动，不能选择机顶盒调测费扶贫专用包!");
			}
            if( "".equals(moSalePackageId2) )
			{
    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "魔百和开户，请选择机顶盒调测费营销包!");
			}
			
		}
		
		if ("600".equals(tradeTypeCode))
		{
			MergeWideUserCreateRequestData mergeWideUserCreateRD = (MergeWideUserCreateRequestData) btd.getRD();
			String MoSaleActiveId1 = mergeWideUserCreateRD.getTopSetBoxSaleActiveId();
			String MoSaleActiveId2 = mergeWideUserCreateRD.getTopSetBoxSaleActiveId2();
			String saleActiveId = mergeWideUserCreateRD.getSaleActiveId();
			String saleActiveId2 = mergeWideUserCreateRD.getSaleActiveId2();
			String moSaleProductId2="";
			String moSalePackageId2="";
			String saleProductId2="";
			String salePackageId2="";
			
			Boolean FTTH_TAG = false;
			if("3".equals(mergeWideUserCreateRD.getWideType()) || "5".equals((mergeWideUserCreateRD.getWideType())))
	        {
				FTTH_TAG = true;
	        }
			
			if (StringUtils.isNotBlank(mergeWideUserCreateRD.getSaleActiveId2()))
	        {
	            IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", "FTTH", saleActiveId2, "0898");
	            
	            if (IDataUtil.isNotEmpty(saleActiveIDataset))
	            { 
	                  IData saleActiveData = saleActiveIDataset.first();
	                  saleProductId2 =saleActiveData.getString("PARA_CODE4","");
	                  salePackageId2 =saleActiveData.getString("PARA_CODE5","");
	            }
	        }
			
			if (StringUtils.isNotBlank(mergeWideUserCreateRD.getTopSetBoxSaleActiveId2()))
	        {
		        IDataset saleActiveIDataset = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", MoSaleActiveId2, "0898");
	            
	            if (IDataUtil.isNotEmpty(saleActiveIDataset))
	            { 
	                  IData saleActiveData = saleActiveIDataset.first();
	                  moSaleProductId2 =saleActiveData.getString("PARA_CODE4","");
	                  moSalePackageId2 =saleActiveData.getString("PARA_CODE5","");
	            }
	        }
			
            if( "".equals(salePackageId2) && FTTH_TAG )
			{
    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "宽带开户，请选择智能网关调测费营销包!");
			}
            
            if( StringUtils.isNotBlank(mergeWideUserCreateRD.getTopSetBoxProductId())
            		&& "".equals(moSalePackageId2))
			{
    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "开通魔百和业务，请选择机顶盒调测费营销包！");
			}

	        if (StringUtils.isNotBlank(mergeWideUserCreateRD.getSaleActiveId()))
	        {
	            IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", mergeWideUserCreateRD.getMainProduct().getProductId(), mergeWideUserCreateRD.getSaleActiveId(), btd.getMainTradeData().getEparchyCode());
	            
	            if (IDataUtil.isNotEmpty(saleActiveIDataset))
	            { 
	                  IData saleActiveData = saleActiveIDataset.first();                 
	                  String saleProductId = saleActiveData.getString("PARA_CODE4","");
	                  String salePackageId = saleActiveData.getString("PARA_CODE5","");

	                  if ( "84071247".equals(salePackageId) || "84071246".equals(salePackageId)
								|| "84071443".equals(salePackageId) || "84071442".equals(salePackageId))
			          {
			          	  	HasActiveTag = true;
			          }

	            }
	            if(HasActiveTag &&(!"79082818".equals(salePackageId2) && !"79082808".equals(salePackageId2)) && FTTH_TAG)
				{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您选择了扶贫活动，请选择智能网关调测费扶贫专用包!");
				}
	            
	            if(!HasActiveTag &&("79082818".equals(salePackageId2) || "79082808".equals(salePackageId2)) && FTTH_TAG)
				{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您没有选择扶贫活动，不能选择智能网关调测费扶贫专用包!");
				}

	            
	            if(HasActiveTag && StringUtils.isNotBlank(mergeWideUserCreateRD.getTopSetBoxProductId())
	            		&&(!"79082918".equals(moSalePackageId2) && !"79082908".equals(moSalePackageId2)))
				{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您选择了扶贫活动，请选择机顶盒调测费扶贫专用包!");
				}
	            
	            if(!HasActiveTag && StringUtils.isNotBlank(mergeWideUserCreateRD.getTopSetBoxProductId())
	            		&&("79082918".equals(moSalePackageId2) || "79082908".equals(moSalePackageId2)))
				{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您没有选择扶贫活动，不能选择机顶盒调测费扶贫专用包!");
				}

	            
	        }
	        else
	        {
	        	if(("79082818".equals(salePackageId2) || "79082808".equals(salePackageId2)) && FTTH_TAG)
	        	{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您没有选择扶贫活动，不能选择智能网关调测费扶贫专用包!");
	        	}
	        }
	        
		}
       
		if ("606".equals(tradeTypeCode))
		{
			WidenetMoveRequestData widenetMoveRD = (WidenetMoveRequestData) btd.getRD();

			String salePackageId = btd.getRD().getPageRequestData().getString("SALEACTIVE_PACKAGE_ID","");
			String salePackageId2 = widenetMoveRD.getSaleActiveId2();
			
			Boolean FTTH_TAG = false;
			if("3".equals(widenetMoveRD.getNewWideType()) || "5".equals((widenetMoveRD.getNewWideType())))
	        {
				FTTH_TAG = true;
	        }			

            if( "".equals(salePackageId2) && FTTH_TAG )
			{
    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "宽带移机，请选择智能网关调测费营销包!");
			}
            

	        if (StringUtils.isNotBlank(salePackageId))
	        {


	        	if ( "84071247".equals(salePackageId) || "84071246".equals(salePackageId)
						|| "84071443".equals(salePackageId) || "84071442".equals(salePackageId))
	        	{
	          	  	HasActiveTag = true;
	        	}

	            if(HasActiveTag &&(!"79082818".equals(salePackageId2) && !"79082808".equals(salePackageId2)) && FTTH_TAG)
				{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您选择了扶贫活动，请选择智能网关调测费扶贫专用包!");
				}
	            
	            if(!HasActiveTag &&("79082818".equals(salePackageId2) || "79082808".equals(salePackageId2)) && FTTH_TAG)
				{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您没有选择扶贫活动，不能选择智能网关调测费扶贫专用包!");
				}
	            
	        }
	        else
	        {
	        	if(("79082818".equals(salePackageId2) || "79082808".equals(salePackageId2)) && FTTH_TAG)
	        	{
	    			CSAppException.apperr(CrmCommException.CRM_COMM_888, "您没有选择扶贫活动，不能选择智能网关调测费扶贫专用包!");
	        	}
	        }
	        
		}

    }
	
}
