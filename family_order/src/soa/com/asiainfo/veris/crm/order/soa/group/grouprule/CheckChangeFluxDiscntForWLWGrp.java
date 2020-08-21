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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class CheckChangeFluxDiscntForWLWGrp extends BreBase implements IBREScript
{

	private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckChangeFluxDiscntForWLWGrp.class);
    
	@Override
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
        	logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckChangeFluxDiscntForWLWGrp() >>>>>>>>>>>>>>>>>>");
        }

        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        /* 自定义区域 */
        String userIdA = databus.getString("USER_ID", "");//集团用户UserId
        
        //查看有没有选择流量套餐
        boolean isAddSvc = false;
        boolean isDelSvc = false;
        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
        IDataset userElements = null;
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
        					&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode)
        					&& eleId.equals("99010013"))
        			{
        				isAddSvc = true;
        			}
        			else if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)
        						&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode)
        						&& eleId.equals("99010012"))
        			{
        				isAddSvc = true;
        			}
        			else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)
       					 		&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode)
       					 		&& eleId.equals("99010013"))
        			{
        				isDelSvc = true;
        			}
        			else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)
    							&& BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode)
    							&& eleId.equals("99010012"))
        			{
        				isDelSvc = true;
        			}
                }
        	}
        }
        
        if(isAddSvc)//集团用户订购流量池产品时
        {
        	IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(userIdA,"9A");
        	int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");//集团下成员的个数
        	if(mebNum == 0)//没有成员
        	{
        		return true;
        	}
        	else if(mebNum == 1)//就一个成员时
        	{
        		//判断是否订购了流量套餐
        		IDataset discntInfos = UserDiscntInfoQry.queryWlwMebDiscntInfo(userIdA);
        		if(IDataUtil.isEmpty(discntInfos))
        		{
        			err = "该集团用户[" + userIdA + "]下的成员有未订购流量套餐,不允许订购流量池产品!";
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "_997", err);
        			return false;
        		}
        	}
        	else
        	{
        		//判断集团物联网下的是否有未订购流量套餐的成员
        		IDataset mebDiscnts = UserDiscntInfoQry.queryWlwMebNotExistDiscnt(userIdA);
        		if(IDataUtil.isNotEmpty(mebDiscnts))
        		{
        			err = "该集团用户[" + userIdA + "]下有成员未订购流量套餐,不允许订购流量池产品!";
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode + "_998", err);
        			return false;
        		}
        		
        		//判断集团物联网下的成员订购的流量套餐是否一样
        		IDataset numCounts = UserDiscntInfoQry.queryWlwMebNumDiscnt(userIdA);
        		int counts = numCounts.getData(0).getInt("RECORDCOUNT");//集团下成员的个数
        		if(counts != 1)
        		{
        			err = "该集团用户[" + userIdA + "]下的成员订购流量套餐不一致,不允许订购流量池产品!";
        			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
        			return false;
        		}
        	}
        }
        
        if(isDelSvc)
        {
        	IDataset uuInfos = RelaUUInfoQry.getRelaCoutByPK(userIdA,"9A");
        	int mebNum = uuInfos.getData(0).getInt("RECORDCOUNT");//集团下成员的个数
        	if(mebNum > 0)
        	{
        		err = "该集团用户[" + userIdA + "]下有成员,不允许取消流量池产品!";
    			BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
    			return false;
        	}
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< CheckChangeFluxDiscntForWLWGrp()<<<<<<<<<<<<<<<<<<<");
        }

        return true;
    }

}
