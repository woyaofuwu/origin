
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;

public class BatDestroyGrpSpecialPayTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
    	//IData svcData = batData.getData("svcData", new DataMap());
        //IData condData = batData.getData("condData", new DataMap());
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        
        String condStr = batData.getString("CODING_STR", "");
        if (StringUtils.isBlank(condStr))
        {
            String batchTaskId = IDataUtil.getMandaData(batData, "BATCH_TASK_ID");

            condStr = BatTradeInfoQry.getTaskCondString(batchTaskId);
        }

        if (StringUtils.isNotBlank(condStr))
        {
        	batData.putAll(new DataMap(condStr));
        }
        
        IData condData = new DataMap(condStr);
        
        //查询方式
        String queryType = IDataUtil.chkParam(condData, "QUERY_TYPE");
        if(!"1".equals(queryType))
        {
        	String errMes = "查询方式不正确,请核实!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        
        //失效方式
        String actionFlag = IDataUtil.chkParam(condData, "ACTION_FLAG");
        if(!"1".equals(actionFlag) && !"2".equals(actionFlag))
        {
        	String errMes = "失效方式不正确,请核实!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        
        // 查询用户信息
        //IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        
        IDataset userPayList = PayRelaInfoQry.qryGrpSpecialPayBySerialNumber(serialNumber, null);
        if (IDataUtil.isEmpty(userPayList))
        {
        	String errMes = "未获取到该号码" + serialNumber + "的付费关系!!";
			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        String userIdA = "";
        for (int i = 0, iRow = userPayList.size(); i < iRow; i++)
        {
        	IData userPayData = userPayList.getData(i);
			String userId = userPayData.getString("USER_ID","");
			String payItemCode = userPayData.getString("PAYITEM_CODE", "");
			String acctId = userPayData.getString("ACCT_ID", "");
			IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, acctId, payItemCode);
			//if (IDataUtil.isEmpty(userSpecialPayList))
            //{
			//	String message = "找不到用户的统付费关系[集团账户标识=" + acctId + ",用户标识=" + userId + "]!!";
            //    CSAppException.apperr(GrpException.CRM_GRP_713, message);
            //}
			//userIdA = userSpecialPayList.getData(0).getString("USER_ID_A","");
			//if(StringUtils.isNotBlank(userIdA))
			//{
			//	break;
			//}
			if(IDataUtil.isNotEmpty(userSpecialPayList))
			{
				for(int j = 0, jRow = userSpecialPayList.size(); j < jRow; j++)
				{
					IData userSpecData = userSpecialPayList.getData(j);
					userIdA = userSpecData.getString("USER_ID_A","");
					if(StringUtils.isNotBlank(userIdA)){
						break;
					}
				}
			}
			
			if(StringUtils.isNotBlank(userIdA)){
				break;
			}
        }
        
        batData.put("USER_ID", userIdA);
        batData.put("NEED_RULE", true);
        batData.put("QUERY_TYPE", queryType);//查询方式
        batData.put("ACTION_FLAG", actionFlag);//本月底失效
        batData.put("ORIG_BATCH_ID", batData.getString("BATCH_ID"));
        
    }

}
