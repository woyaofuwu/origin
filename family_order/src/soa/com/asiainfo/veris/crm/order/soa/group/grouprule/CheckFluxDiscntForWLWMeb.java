package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckFluxDiscntForWLWMeb extends BreBase implements IBREScript
{	
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckFluxDiscntForWLWMeb.class);

	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception 
	{
		if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckFluxDiscntForWLWMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        String userIdA = databus.getString("USER_ID", "");//集团用户UserId
        String productId = databus.getString("PRODUCT_ID");// 集团产品
        
        //查看有没有选择流量套餐
        boolean isExistEle = false;
        IDataset selectDatas = new DatasetList();
        IDataset userElements = null;
        String userElementsStr = "";
        String subTransCode = databus.getString("X_SUBTRANS_CODE","");
        //批量进来的
        if(StringUtils.isNotBlank(subTransCode) 
        		&& StringUtils.equals(subTransCode, "GrpBat"))
        {
            userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }            
        }
        else 
        {
            userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
            
            if(StringUtils.isBlank(userElementsStr))
            {
            	userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
            }
            
            if (StringUtils.isNotBlank(userElementsStr))
            {
            	userElements = new DatasetList(userElementsStr);
            }
        }
        
        if (StringUtils.isNotBlank(userElementsStr))
        {
        	userElements = new DatasetList(userElementsStr);
        	if(IDataUtil.isNotEmpty(userElements))
        	{
        		int size = userElements.size();
        		for (int i = 0; i < size; i++)
                {
        			IData element = userElements.getData(i);
        			String eleTypeCode = element.getString("ELEMENT_TYPE_CODE","");
        			String modifyTag = element.getString("MODIFY_TAG","");
        			String eleId = element.getString("ELEMENT_ID","");
        			if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) 
        					&& BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				IDataset infos = CommparaInfoQry.getCommPkInfo("CSM", "9018", eleId,"0898");
        				if(IDataUtil.isNotEmpty(infos))
        				{
        					IData selectData = new DataMap();
        					//新增成员时订购了流量套餐
        					isExistEle = true;
        					selectData.put("DISCNT_CODE",eleId);
        					selectDatas.add(selectData);
        				}
        			}
        			
                }
        	}
        }
        
        //物联通产品
        if(productId.equals("20005015"))
        {
        	//集团用户是否订购了流量池产品,服务Id是99010013
        	IDataset svcInfos = UserSvcInfoQry.getGrpSvcInfoByUserId(userIdA,"99010013");
        	if(IDataUtil.isNotEmpty(svcInfos))
        	{
        		//原来用户在个人那边是否订购了物联网的流量套餐
        		IDataset discntInfos = UserDiscntInfoQry.queryWlwUserDiscntInfo(userIdB);
        		//如果用户原来未订购物联网流量套餐,并且本次新增成员又没有订购物联网的流量套餐,则拦截提示
        		if(IDataUtil.isEmpty(discntInfos) && !isExistEle)//
        		{
        			err = "该集团用户[" + userIdA + "]已经订购了流量池产品,该成员用户原来未订购有流量套餐并且新增时也未选择流量套餐,请选择流量套餐!";
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        			return false;
        		}
        		
        		//判断集团用户下是否有成员,如果是第一个成员,则直接不做流量套餐的判断，否则要判断流量套餐是否一致
        		IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(userIdA,"9A");
                int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");
                
                if(mebNum == 0)
                {
                	return true;
                }
                else 
                {
                	boolean containFlag = false;
                	//如果用户原来有订购物联网流量套餐,并且本次新增成员未订购物联网的流量套餐
            		if(IDataUtil.isNotEmpty(discntInfos) && !isExistEle)
            		{
            			//获取该集团成员的优惠编码
            			IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(userIdA);
            			if(IDataUtil.isNotEmpty(mebDiscnts))
            			{
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(discntInfos, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containFlag = true;
            						break;
            					}
            				}
            			}
            			
            			if(!containFlag)
                		{
                			err = "该用户[" + userIdB + "]订购的流量套餐与该集团用户[" + userIdA + "]下的成员订购的流量套餐不一致,不允许添加成员!";
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                			return false;
                		}
            		}
            		
            		containFlag = false;
            		//如果用户原来未订购物联网流量套餐,并且本次新增成员有订购物联网的流量套餐
            		if(IDataUtil.isEmpty(discntInfos) && isExistEle)
            		{
            			//获取该集团成员的优惠编码
            			IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(userIdA);
            			if(IDataUtil.isNotEmpty(mebDiscnts))
            			{
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(selectDatas, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containFlag = true;
            						break;
            					}
            				}
            			}
            			
            			if(!containFlag)
                		{
                			err = "该用户[" + userIdB + "]选择订购的流量套餐与该集团用户[" + userIdA + "]下的成员订购的流量套餐不一致,不允许添加成员!";
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                			return false;
                		}
            		}
            		
            		containFlag = false;
            		boolean containCrmFlag = false;
            		//如果用户原来有订购物联网流量套餐,并且本次新增成员也有订购物联网的流量套餐
            		if(IDataUtil.isNotEmpty(discntInfos) && isExistEle)
            		{
            			//获取该集团成员的优惠编码
            			IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(userIdA);
            			if(IDataUtil.isNotEmpty(mebDiscnts))
            			{
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(selectDatas, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containFlag = true;
            						break;
            					}
            				}
            				
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(discntInfos, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containCrmFlag = true;
            						break;
            					}
            				}
            			}
            			
            			if(containFlag != true || containCrmFlag != true)
            			{
            				err = "该用户[" + userIdB + "]原有的流量套餐,选择订购的流量套餐与该集团用户[" + userIdA + "]下的成员订购的流量套餐不一致,不允许添加成员!";
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                			return false;
            			}
            		}
            		
                }
        		
        	}
        }
        else if(productId.equals("20005013")) //机器卡产品
        {
        	//集团用户是否订购了流量池产品,服务Id是99010012
        	IDataset svcInfos = UserSvcInfoQry.getGrpSvcInfoByUserId(userIdA,"99010012");
        	if(IDataUtil.isNotEmpty(svcInfos))
        	{
        		//原来用户在个人那边是否订购了物联网的流量套餐
        		IDataset discntInfos = UserDiscntInfoQry.queryWlwUserDiscntInfo(userIdB);
        		//如果用户原来未订购物联网流量套餐,并且本次新增成员又没有订购物联网的流量套餐,则拦截提示
        		if(IDataUtil.isEmpty(discntInfos) && !isExistEle)//
        		{
        			err = "该集团用户[" + userIdA + "]已经订购了流量池产品,该成员用户原来未订购有流量套餐并且新增时也未选择流量套餐,请选择流量套餐!";
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        			return false;
        		}
        		
        		//判断集团用户下是否有成员,如果是第一个成员,则直接不做流量套餐的判断，否则要判断流量套餐是否一致
        		IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(userIdA,"9A");
                int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");
                
                if(mebNum == 0)
                {
                	return true;
                }
                else 
                {
                	boolean containFlag = false;
                	//如果用户原来有订购物联网流量套餐,并且本次新增成员未订购物联网的流量套餐
            		if(IDataUtil.isNotEmpty(discntInfos) && !isExistEle)
            		{
            			//获取该集团成员的优惠编码
            			IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(userIdA);
            			if(IDataUtil.isNotEmpty(mebDiscnts))
            			{
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(discntInfos, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containFlag = true;
            						break;
            					}
            				}
            			}
            			
            			if(!containFlag)
                		{
                			err = "该用户[" + userIdB + "]订购的流量套餐与该集团用户[" + userIdA + "]下的成员订购的流量套餐不一致,不允许添加成员!";
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                			return false;
                		}
            		}
            		
            		containFlag = false;
            		//如果用户原来未订购物联网流量套餐,并且本次新增成员有订购物联网的流量套餐
            		if(IDataUtil.isEmpty(discntInfos) && isExistEle)
            		{
            			//获取该集团成员的优惠编码
            			IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(userIdA);
            			if(IDataUtil.isNotEmpty(mebDiscnts))
            			{
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(selectDatas, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containFlag = true;
            						break;
            					}
            				}
            			}
            			
            			if(!containFlag)
                		{
                			err = "该用户[" + userIdB + "]选择订购的流量套餐与该集团用户[" + userIdA + "]下的成员订购的流量套餐不一致,不允许添加成员!";
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                			return false;
                		}
            		}
            		
            		containFlag = false;
            		boolean containCrmFlag = false;
            		//如果用户原来有订购物联网流量套餐,并且本次新增成员也有订购物联网的流量套餐
            		if(IDataUtil.isNotEmpty(discntInfos) && isExistEle)
            		{
            			//获取该集团成员的优惠编码
            			IDataset mebDiscnts = UserDiscntInfoQry.queryWlwOneMebDiscntInfo(userIdA);
            			if(IDataUtil.isNotEmpty(mebDiscnts))
            			{
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(selectDatas, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containFlag = true;
            						break;
            					}
            				}
            				
            				for(int i = 0; i <mebDiscnts.size(); i++)
            				{
            					IData mebDiscnt = mebDiscnts.getData(i);
            					String mebDiscntCode = mebDiscnt.getString("DISCNT_CODE");
            					IDataset discnts = DataHelper.filter(discntInfos, "DISCNT_CODE=" + mebDiscntCode);
            					if(IDataUtil.isNotEmpty(discnts))
            					{
            						containCrmFlag = true;
            						break;
            					}
            				}
            			}
            			
            			if(containFlag != true || containCrmFlag != true)
            			{
            				err = "该用户[" + userIdB + "]原有的流量套餐,选择订购的流量套餐与该集团用户[" + userIdA + "]下的成员订购的流量套餐不一致,不允许添加成员!";
                			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
                			return false;
            			}
            		}
            		
                }
        		
        	}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckFluxDiscntForWLWMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
	}

}
