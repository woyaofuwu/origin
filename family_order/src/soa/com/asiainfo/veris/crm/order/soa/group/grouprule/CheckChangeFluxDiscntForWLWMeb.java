package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckChangeFluxDiscntForWLWMeb extends BreBase implements IBREScript
{
	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckChangeFluxDiscntForWLWMeb.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckChangeFluxDiscntForWLWMeb() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String userIdB = databus.getString("USER_ID_B", "");//成员用户UserId
        String userIdA = databus.getString("USER_ID", "");//集团用户UserId
        String productId = databus.getString("PRODUCT_ID");// 集团产品
        
        //查看有没有选择流量套餐
        boolean isAddExistEle = false;
        boolean isUpdateExistEle = false;
        boolean isDelExistEle = false;
        
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
        			
        			if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode))
        			{
        				if(BofConst.MODIFY_TAG_UPD.equals(modifyTag))
        				{
        					IDataset infos = CommparaInfoQry.getCommPkInfo("CSM", "9018", eleId,"0898");
            				if(IDataUtil.isNotEmpty(infos))
            				{
            					isUpdateExistEle = true;
            				}
        				}
        				else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag))
        				{
        					IDataset infos = CommparaInfoQry.getCommPkInfo("CSM", "9018", eleId,"0898");
            				if(IDataUtil.isNotEmpty(infos))
            				{
            					isDelExistEle = true;
            				}
        				}
        				else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        				{
        					IDataset infos = CommparaInfoQry.getCommPkInfo("CSM", "9018", eleId,"0898");
            				if(IDataUtil.isNotEmpty(infos))
            				{
            					isAddExistEle = true;
            				}
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
        		//判断集团用户下是否有成员,如果只有一个成员,则允许变更流量套餐,否则不允许变更套餐
        		IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(userIdA,"9A");
                int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");
                
                if(mebNum == 0)
                {
                	if(isDelExistEle && !isAddExistEle)
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许取消流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                	return true;
                }
                else 
                {
                	if(isDelExistEle && isAddExistEle)//不允许变更流量套餐
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许变更流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                	else if(isDelExistEle && !isAddExistEle)
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许取消流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                	else if(!isDelExistEle && isAddExistEle)
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许新增流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
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
        		//判断集团用户下是否有成员,如果只有一个成员,则允许变更流量套餐,否则不允许变更套餐
        		IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(userIdA,"9A");
                int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");
                
                if(mebNum == 0)
                {
                	if(isDelExistEle && !isAddExistEle)
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许取消流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                	return true;
                }
                else 
                {
                	if(isUpdateExistEle)//不允许变更流量套餐
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许变更流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                	else if(isDelExistEle)
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许取消流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                	else if(isAddExistEle)
                	{
                		err = "该用户[" + userIdB + "]所在的集团用户[" + userIdA + "]有流量池产品,不允许新增流量套餐!";
            			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            			return false;
                	}
                }
        	}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckChangeFluxDiscntForWLWMeb()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
    }

}
