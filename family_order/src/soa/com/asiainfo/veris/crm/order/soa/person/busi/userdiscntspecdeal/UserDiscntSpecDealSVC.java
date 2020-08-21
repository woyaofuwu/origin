
package com.asiainfo.veris.crm.order.soa.person.busi.userdiscntspecdeal;

import java.util.ArrayList;
import java.util.List;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.UserDiscntException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserDiscntSpecDealSVC.java
 * @Description: 用户优惠特殊处理SVC类
 * @version: v1.0.0
 * @author: maoke
 * @date: May 26, 2014 3:31:16 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 26, 2014 maoke v1.0.0 修改原因
 */
public class UserDiscntSpecDealSVC extends CSBizService
{
    /**
     * @Description: 获取用户优惠
     * @param param
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 26, 2014 3:32:29 PM
     */
    public IData getUserDiscntList(IData param) throws Exception
    {
        IData returnData = new DataMap();
        returnData.clear();

        String userId = param.getString("USER_ID");
        boolean UnPrimeDiscntTag = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "UNPRIME_DISCNT_MODE", "1");// 员工数据权限 收取
        IDataset allUserDiscnt = UserDiscntInfoQry.queryUserAllDiscntByUserId(userId);
        //根据权限判断,有权限的员工才能查到宽带号码的非主套餐优惠
        if (UnPrimeDiscntTag)
        {	
        	//查询宽带号码对应信息，如果没有对应宽带号码，就按普通号码查
        	IData user = UcaInfoQry.qryUserInfoByUserId(userId);
        	String serialNumber = "KD_"+user.getString("SERIAL_NUMBER");
        	String eparchyCode = param.getString("EPARCHY_CODE", "");
        	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber, eparchyCode);
        	if(IDataUtil.isNotEmpty(userInfo))
        	{
        		//宽带号码对应的USER_ID
        		String kdUserId = userInfo.getString("USER_ID");
        		IDataset newAllUserDiscnt = UserDiscntInfoQry.queryUserAllDiscntByUserId(kdUserId);
        		if(IDataUtil.isNotEmpty(newAllUserDiscnt))
        		{
        			for (int i = 0; i < newAllUserDiscnt.size(); i++)
        			{
        				IData discnt = newAllUserDiscnt.getData(i);
        				String rel_offer_ins_id = discnt.getString("INST_ID");
        				
        				IDataset offerRelList = UserOfferRelQry.getOfferRelByRelOfferInsId(rel_offer_ins_id);
        				if(IDataUtil.isNotEmpty(offerRelList))
        				{
        					List<String> newStrList = new  ArrayList<String>();
        					for(int j = 0; j < offerRelList.size(); j++){
        						IData offerRel = offerRelList.getData(j);
        						String productId = offerRel.getString("OFFER_CODE");
        						String groupId = offerRel.getString("GROUP_ID");
        						String relOfferType = offerRel.getString("REL_OFFER_TYPE");
        						String relType = offerRel.getString("REL_TYPE");
        						//去重复
            					if(!newStrList.contains(productId))
            					{
            						newStrList.add(productId);
    								if("0".equals(groupId) && "D".equals(relOfferType) && "C".equals(relType))
        							{
    									newAllUserDiscnt.remove(i);
        								i--;
        							}
            					}
        					}
        				}
        			}
        		}
        		allUserDiscnt.addAll(newAllUserDiscnt);
        	}
        }
        

        if (IDataUtil.isNotEmpty(allUserDiscnt))
        {
            // 2天前
            // String tempEndDate = SysDateMgr.addDays(-2);
            // 应用户要求改成上月底之前
            String tempEndDate = SysDateMgr.getLastMonthLastDate();
            String sysTime = SysDateMgr.getSysTime();

            // 过滤
            for (int i = 0; i < allUserDiscnt.size(); i++)
            {
                IData discnt = allUserDiscnt.getData(i);

                String discntCode = discnt.getString("DISCNT_CODE");
                String startDate = SysDateMgr.decodeTimestamp(discnt.getString("START_DATE"), SysDateMgr.PATTERN_STAND);
                String endDate = SysDateMgr.decodeTimestamp(discnt.getString("END_DATE"), SysDateMgr.PATTERN_STAND);
                IDataset discntInfo = CommparaInfoQry.getCommparaByCodeCode1("CSM","9786","KNO_CANCEL_DISCN",discntCode);
                if (startDate.compareTo(sysTime) > 0 || endDate.compareTo(tempEndDate) < 0 || isExistsDtypeDiscnt(discntCode)||IDataUtil.isNotEmpty(discntInfo))
                {
					
                    allUserDiscnt.remove(i);
                    i--;
                }
                else
                {
                    if (endDate.compareTo(tempEndDate) > 0 && endDate.compareTo(sysTime) <= 0)
                    {
                        discnt.put("START_DATE_CHG_TAG", "TRUE");
                    }
                    discnt.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode));
                    discnt.put("MODIFY_TAG", "EXIST");
                }
                
            }
        }
        else
        {
            CSAppException.apperr(UserDiscntException.CRM_USER_DISCNT_3);
        }

        returnData.put("DISCNT_LIST", allUserDiscnt);

        returnData.put("SYS_DATE", SysDateMgr.getSysTime());
        returnData.put("LAST_DAY_THIS_MONTH", AcctDayDateUtil.getLastDayThisAcct(userId));
        returnData.put("LAST_DAY_LAST_MONTH", SysDateMgr.addDays(AcctDayDateUtil.getFirstDayThisAcct(userId), -1) + SysDateMgr.END_DATE);
        returnData.put("END_DATE_FOREVER", SysDateMgr.END_DATE_FOREVER);

        return returnData;
    }

    /**
     * @Description: 优惠是否存在于【TD_B_DTYPE_DISCNT】,且类型为5
     * @param discntCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 26, 2014 3:47:27 PM
     */
    public boolean isExistsDtypeDiscnt(String discntCode) throws Exception
    {
        String dtypeDiscnt = UDiscntInfoQry.getDiscntTypeByDiscntCode(discntCode);

        if ("5".equals(dtypeDiscnt))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public IDataset isExistsMainDiscntPriv(IData data) throws Exception
    {
    	IDataset bResult = new DatasetList();
    	IData returnData = new DataMap();
    	returnData.put("USERDISCNT_SPECDEAL", "0");
    	
		String serialNumber = data.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		if(uca != null)
		{
			String strProduct = uca.getProductId();

			IDataset newProductElements = ProductElementsCache.getProductElements(strProduct);
        	if(IDataUtil.isNotEmpty(newProductElements))
        	{
        		boolean bExtend = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "USERDISCNT_SPECDEAL");
        		IDataset idsSpecDiscnts = new DatasetList(data.getString("DISCNT_LIST"));
        		if(IDataUtil.isNotEmpty(idsSpecDiscnts))
        		{
        			boolean isExit = false;
        			for (int i = 0; i < newProductElements.size(); i++)
            		{
            			IData ProductElement = newProductElements.getData(i);
            			String strElementID = ProductElement.getString("ELEMENT_ID", "");
            			String strElementTypeCode = ProductElement.getString("ELEMENT_TYPE_CODE", "");
            			String strElementForceTag = ProductElement.getString("ELEMENT_FORCE_TAG", "");
            			for (int j = 0; j < idsSpecDiscnts.size(); j++) 
            			{
            				IData idsSpecDiscnt = idsSpecDiscnts.getData(j);
            				String strDiscntCode = idsSpecDiscnt.getString("DISCNT_CODE", "");
            				String strDiscntName = idsSpecDiscnt.getString("DISCNT_NAME", "");
            				String strStartDate = idsSpecDiscnt.getString("START_DATE");
            	            String oldStartDate = idsSpecDiscnt.getString("OLD_START_DATE");
            	            String strEndDate = idsSpecDiscnt.getString("END_DATE");
            	            String oldEndDate = idsSpecDiscnt.getString("OLD_END_DATE");
            	            if(strStartDate.compareTo(oldStartDate) > 0)
            	            {
            	            	CSAppException.apperr(BizException.CRM_BIZ_5, strDiscntCode + "|" + strDiscntName + "，开始时间只能往前修改，不能往后修改。");
            	            	returnData.put("USERDISCNT_SPECDEAL", "1");  
            	            	isExit = true;
            	        		break;
            	            }
            	            if(strDiscntCode.equals(strElementID) && "D".equals(strElementTypeCode) && "1".equals(strElementForceTag))
                			{
                	        	if((!bExtend) && (strEndDate.compareTo(oldEndDate) != 0))
                	        	{
                	        		CSAppException.apperr(BizException.CRM_BIZ_5, strDiscntCode + "|" + strDiscntName + "，您没有修改必选优惠的权限。");
                	        		returnData.put("USERDISCNT_SPECDEAL", "2");
                	        		isExit = true;
                	        		break;
                	        	}
                			}

                			//6067优惠编码，需要权限才能修改
							boolean terminateDiscnt6067 = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "TERMINATE_DISCNT_6067", "1");// 员工数据权限 收取

							if ("6067".equals(strDiscntCode) && !terminateDiscnt6067)
							{
								CSAppException.apperr(BizException.CRM_BIZ_5, strDiscntCode + "|" + strDiscntName + "，您没有修改该优惠的权限。");
								returnData.put("USERDISCNT_SPECDEAL", "3");
								isExit = true;
								break;
							}
						}
            			if(isExit)
            			{
            				break;
            			}
            			
            		}
        		}
        	}
		}
    		
    	bResult.add(returnData);
    	return bResult;
    }
}
