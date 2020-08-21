
package com.asiainfo.veris.crm.order.soa.group.stopgroupbroadband;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BrandException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class StopOneKeyBean extends CSBizBean
{

    /**
     * 创建批量任务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset crtBat(IData inParam) throws Exception
    {
        IDataset relaList = new DatasetList();

        StringBuilder builder = new StringBuilder(50);

        String groupId = inParam.getString("GROUP_ID");
        String productId = inParam.getString("PRODUCT_ID");
        String userId = inParam.getString("USER_ID");
        String inmodecode = inParam.getString("IN_MODE_CODE");
        String systime = SysDateMgr.getSysTime();
        
        String stateflag=inParam.getString("STATE_FLAG"); //停开机标识
        
        // 创建集团成员注销的批量任务
        IData mebBatData = new DataMap();

        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("STATE_FLAG", "STOP");//停机标识

        mebCondStrData.put("NEED_RULE", false);
        mebBatData.put("BATCH_OPER_TYPE", "STOPGROUPKDMEM");
        mebBatData.put("BATCH_TASK_NAME", "一键暂停集团下所有的商务宽带成员");
        mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inmodecode);//反向一键注销不走服务开通
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";

       // String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId); // 关系类型

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId); // 品牌信息

        if (StringUtils.isEmpty(brandCode))
        {
            CSAppException.apperr(BrandException.CRM_BRAND_3);
        }
        
        if (StringUtils.equals("BNBD", brandCode))
        {
            relaList = RelaUUInfoQry.qryRelationUUAllForKDMem(userId, "47",null);
        }
        else
        {
            CSAppException.apperr(BrandException.CRM_BRAND_9);
        }
        
        // 如果存在成员则状态变更,本身非正常状态的不停机(非0状态)
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);
                String serialNum = relaData.getString("SERIAL_NUMBER_B");
                
                IDataset userinfos = UserInfoQry.getUserInfoBySn(serialNum,"0");
               
                if (IDataUtil.isNotEmpty(userinfos))
                {
                    String userStateCode=userinfos.getData(0).getString("USER_STATE_CODESET","0");
                    if(!StringUtils.equals("0", userStateCode))
                    {
                        continue;
                    }
                }
                else
                {
                    continue;
                }

                relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
            }

            mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        }

        // 创建集团注销的批量任务
        IData grpBatData = new DataMap();

        IData grpCondStrData = new DataMap();
        grpCondStrData.put("GROUP_ID", groupId);
        grpCondStrData.put("PRODUCT_ID", productId);
        grpCondStrData.put("USER_ID", userId);
        grpCondStrData.put("NEED_RULE", false);
        grpCondStrData.put("STATE_FLAG", "STOP");//停机标识
        grpBatData.put("BATCH_OPER_TYPE", "STOPGROUPKDUSER");
        grpBatData.put("BATCH_TASK_NAME", "一键暂停集团商务宽带用户");
        grpBatData.put("SMS_FLAG", "0");
        grpBatData.put("CREATE_TIME", systime);
        grpBatData.put("ACTIVE_FLAG", "1");
        grpBatData.put("ACTIVE_TIME", systime);
        grpBatData.put("DEAL_TIME", systime);
        grpBatData.put("DEAL_STATE", "1");
        grpBatData.put("IN_MODE_CODE", inmodecode);
        grpBatData.put("CODING_STR", grpCondStrData.toString());
        grpBatData.put("REMARK", "成员批次[" + mebBatchId + "]");

        IData grpData = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);

        String grpBatchId = BatDealBean.createBat(grpBatData, IDataUtil.idToIds(grpData));

        builder.append("集团批次[" + grpBatchId + "]");

        if (StringUtils.isNotEmpty(mebBatchId))
        {
            builder.append(";成员批次[" + mebBatchId + "]");

            // 插入批量关联信息表
            BatDealBean.createBatRealtion(grpBatchId, mebBatchId, "0");
        }

        IData retData = new DataMap();

        retData.put("ORDER_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }
}
