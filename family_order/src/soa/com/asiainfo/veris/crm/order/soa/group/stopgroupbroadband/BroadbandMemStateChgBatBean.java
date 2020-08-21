package com.asiainfo.veris.crm.order.soa.group.stopgroupbroadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;



public class BroadbandMemStateChgBatBean extends CSBizBean
{

    /**
     * 创建批量任务、暂停集团商务宽带产品下的成员
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtStopBat(IData inParam) throws Exception
    {
        IDataset relaList = new DatasetList();
        StringBuilder builder = new StringBuilder(50);
        
        String userId = IDataUtil.chkParam(inParam, "USER_ID");//集团商务宽带的userId
        //String stateFlag = IDataUtil.chkParam(inParam,"STATE_FLAG"); //停开机标识
            
        // 查询集团用户资料
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        String productId = userInfo.getString("PRODUCT_ID");
        
        if(!"7341".equals(productId)){
            CSAppException.apperr(GrpException.CRM_GRP_905, userId);
        }
                
        // 查询集团客户资料
        String grpCustId = userInfo.getString("CUST_ID", "");

        IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpCustInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }
        
        //集团编码
        String groupId = grpCustInfos.getString("GROUP_ID","");
        
        String inModeCode = inParam.getString("IN_MODE_CODE","0");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员注销的批量任务
        IData mebBatData = new DataMap();

        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("STATE_FLAG", "STOP");//停机标识
        mebCondStrData.put("NEED_RULE", false);
        
        mebBatData.put("BATCH_OPER_TYPE", "CREDITSTOPGROUPKDMEM");
        mebBatData.put("BATCH_TASK_NAME", "信控集团成员商务宽带信控欠费停机");
        mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inModeCode);//反向一键注销不走服务开通
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";
        relaList = RelaUUInfoQry.qryRelationUUAllForKDMem(userId, "47",null);
        
        IDataset relaListTemp = new DatasetList();
        
        // 如果存在成员则状态变更,本身非正常状态的不停机(非0状态)
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0; i < relaList.size(); i++)
            {
                IData relaData = relaList.getData(i);
                String serialNum = relaData.getString("SERIAL_NUMBER_B");
                String userIdB = relaData.getString("USER_ID_B");
                
                IDataset userinfos = UserInfoQry.getUserInfoBySn(serialNum,"0");
               
                if (IDataUtil.isNotEmpty(userinfos))
                {
                    String userStateCode = userinfos.getData(0).getString("USER_STATE_CODESET","0");
                    if(!StringUtils.equals("0", userStateCode))
                    {
                        relaList.remove(i);
                        i--;
                        continue;
                    }
                }
                else
                {
                    relaList.remove(i);
                    i--;
                    continue;
                }

                //查看表TF_F_GROUPKDMEM_UU是否有记录，无记录则准备记录的数据
                IDataset dataList = BatBroadbandMebDealBean.queryGrpKdMeb(userIdB);
                if(IDataUtil.isEmpty(dataList)){
                    relaData.put("INSERT_DATE", systime);
                    relaData.put("REMOVE_TAG", "0");
                    relaListTemp.add(relaData);
                }
                
                relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
            }

            //如果成员的明细不为空时,才会登记批量台账
            if(IDataUtil.isNotEmpty(relaList))
            {
            	mebBatchId = BatDealBean.createBat(mebBatData, relaList);
            }
        }

        //捞取集团商务宽带成员开户的在主台账未完工的工单
        IData custInfo = new DataMap();
        custInfo.put("CUST_ID", grpCustId);
        IDataset mebInfos = TradeInfoQry.queryTradeBroadbandMebByCustId(custInfo);
        if(IDataUtil.isNotEmpty(mebInfos))
        {
        	for(int i = 0; i < mebInfos.size(); i++)
        	{
        		IData mebInfo = mebInfos.getData(i);
        		String serialNumberB = mebInfo.getString("SERIAL_NUMBER_B","");
        		String userIdB = mebInfo.getString("USER_ID_B","");
        		//查看表TF_F_GROUPKDMEM_UU是否有记录，无记录则准备记录的数据
                IDataset dataList = BatBroadbandMebDealBean.queryGrpKdMeb(userIdB);
                if(IDataUtil.isEmpty(dataList))
                {
                	IData relaMap = new DataMap();
            		relaMap.put("INSERT_DATE", systime);
            		relaMap.put("REMOVE_TAG", "0");
            		relaMap.put("PARTITION_ID",  StrUtil.getPartition4ById(userIdB));
            		relaMap.put("USER_ID_B", userIdB);
            		relaMap.put("SERIAL_NUMBER_B", serialNumberB);
            		relaMap.put("USER_ID_A", userId);
            		relaMap.put("SERIAL_NUMBER_A", "-1");
            		relaMap.put("REMARK", serialNumberB + "该宽带在主台账里是开户的工单");
                    relaListTemp.add(relaMap);
                }
        	}
        }
        
        //创建集团注销的批量任务
        IData grpBatData = new DataMap();

        IData grpCondStrData = new DataMap();
        grpCondStrData.put("GROUP_ID", groupId);
        grpCondStrData.put("PRODUCT_ID", productId);
        grpCondStrData.put("USER_ID", userId);
        grpCondStrData.put("NEED_RULE", false);
        grpCondStrData.put("STATE_FLAG", "STOP");//停机标识
        grpBatData.put("BATCH_OPER_TYPE", "CRESTOPGROUPKDUSER");
        grpBatData.put("BATCH_TASK_NAME", "信控集团商务宽带产品欠费停机");
        grpBatData.put("SMS_FLAG", "0");
        grpBatData.put("CREATE_TIME", systime);
        grpBatData.put("ACTIVE_FLAG", "1");
        grpBatData.put("ACTIVE_TIME", systime);
        grpBatData.put("DEAL_TIME", systime);
        grpBatData.put("DEAL_STATE", "1");
        grpBatData.put("IN_MODE_CODE", inModeCode);
        grpBatData.put("CODING_STR", grpCondStrData.toString());
        grpBatData.put("REMARK", "成员批次[" + mebBatchId + "]");

        IData grpData = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
        String grpBatchId = BatDealBean.createBat(grpBatData, IDataUtil.idToIds(grpData));

        builder.append("集团批次[" + grpBatchId + "]");
        
        if (StringUtils.isNotEmpty(mebBatchId))
        {
            builder.append(";成员批次[" + mebBatchId + "]");
            // 插入批量关联信息表
            //BatDealBean.createBatRealtion(grpBatchId, mebBatchId, "0");
            
            //登记记录
            BatBroadbandMebDealBean.createGrpBroadbandMeb(relaListTemp);
        }

        IData retData = new DataMap();
        retData.put("ORDER_ID", builder.toString());
        retData.put("X_RESULTINFO", "成功!");
        retData.put("X_RESULTCODE", "0");
        
        return IDataUtil.idToIds(retData);
    }
    
    /**
     * 创建批量任务、恢复集团商务宽带产品下的成员
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtOpenBat(IData inParam) throws Exception
    {
        IDataset relaList = new DatasetList();
        StringBuilder builder = new StringBuilder(50);
        
        String userId = IDataUtil.chkParam(inParam, "USER_ID");//集团商务宽带的userId
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        
        // 查询集团用户资料
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        String productId = userInfo.getString("PRODUCT_ID");
        
        if(!"7341".equals(productId)){
            CSAppException.apperr(GrpException.CRM_GRP_905, userId);
        }
                
        // 查询集团客户资料
        String grpCustId = userInfo.getString("CUST_ID", "");

        IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpCustInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }
        
        String groupId = grpCustInfos.getString("GROUP_ID","");
        
        
        String inModeCode = inParam.getString("IN_MODE_CODE","0");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员恢复的批量任务
        IData mebBatData = new DataMap();

        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("STATE_FLAG", "OPEN");//停机标识

        mebCondStrData.put("NEED_RULE", false);
        mebBatData.put("BATCH_OPER_TYPE", "CREDITOPENGROUPKDMEM");
        mebBatData.put("BATCH_TASK_NAME", "集团成员商务宽带信控缴费开机");
        mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inModeCode);//反向一键注销不走服务开通
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";

        relaList = RelaUUInfoQry.qryRelationUUAllForKDMem(userId, "47",null);
       
        // 如果存在成员则状态变更,本身开机的不开机(0状态)
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0; i < relaList.size(); i++)
            {
                IData relaData = relaList.getData(i);
                String serialNum = relaData.getString("SERIAL_NUMBER_B");
                String userIdB = relaData.getString("USER_ID_B");
                
                IDataset userinfos = UserInfoQry.getUserInfoBySn(serialNum,"0");
               
                if (IDataUtil.isNotEmpty(userinfos))
                {
                    String userStateCode = userinfos.getData(0).getString("USER_STATE_CODESET","0");
                    if(!StringUtils.equals("5", userStateCode) && !StringUtils.equals("05", userStateCode))
                    {
                        relaList.remove(i);
                        i--;
                        continue;
                    }
                }
                else
                {
                    relaList.remove(i);
                    i--;
                    continue;
                }
                
                //查看表TF_F_GROUPKDMEM_UU是否有记录，没有记录则不给发起信控的恢复
                IDataset dataList = BatBroadbandMebDealBean.queryGrpKdMeb(userIdB);
                if(IDataUtil.isEmpty(dataList)){
                    relaList.remove(i);
                    i--;
                    continue;
                }
                
                relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
            }

            if(IDataUtil.isNotEmpty(relaList))
            {
            	mebBatchId = BatDealBean.createBat(mebBatData, relaList);
            }
        }

        // 创建集团注销的批量任务
        IData grpBatData = new DataMap();

        IData grpCondStrData = new DataMap();
        grpCondStrData.put("GROUP_ID", groupId);
        grpCondStrData.put("PRODUCT_ID", productId);
        grpCondStrData.put("USER_ID", userId);
        grpCondStrData.put("NEED_RULE", false);
        grpCondStrData.put("STATE_FLAG", "OPEN");//开机标识
        grpBatData.put("BATCH_OPER_TYPE", "CREOPENGROUPKDUSER");
        grpBatData.put("BATCH_TASK_NAME", "集团商务宽带产品缴费开机");
        grpBatData.put("SMS_FLAG", "0");
        grpBatData.put("CREATE_TIME", systime);
        grpBatData.put("ACTIVE_FLAG", "1");
        grpBatData.put("ACTIVE_TIME", systime);
        grpBatData.put("DEAL_TIME", systime);
        grpBatData.put("DEAL_STATE", "1");
        grpBatData.put("IN_MODE_CODE", inModeCode);
        grpBatData.put("CODING_STR", grpCondStrData.toString());
        grpBatData.put("REMARK", "成员批次[" + mebBatchId + "]");

        IData grpData = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
        String grpBatchId = BatDealBean.createBat(grpBatData, IDataUtil.idToIds(grpData));

        builder.append("集团批次[" + grpBatchId + "]");
        
        if (StringUtils.isNotEmpty(mebBatchId))
        {
            builder.append(";成员批次[" + mebBatchId + "]");
            
            // 插入批量关联信息表
            //BatDealBean.createBatRealtion(grpBatchId, mebBatchId, "0");
            
            //发起信控恢复的记录，则删除临时表记录
            BatBroadbandMebDealBean.deleteGrpBroadbandMeb(relaList);
        }

        IData retData = new DataMap();

        retData.put("ORDER_ID", builder.toString());
        retData.put("X_RESULTINFO", "成功!");
        retData.put("X_RESULTCODE", "0");
        
        return IDataUtil.idToIds(retData);
    }
}