
package com.asiainfo.veris.crm.order.soa.group.destroyonekey;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BrandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class DestroyOneKeyBean extends CSBizBean
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
        // 创建集团成员注销的批量任务
        IData mebBatData = new DataMap();

        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        //add by chenzg@20180709--REQ201804280001集团合同管理界面优化需求-----
        mebCondStrData.put("MEB_VOUCHER_FILE_LIST", inParam.getString("MEB_VOUCHER_FILE_LIST", ""));
        mebCondStrData.put("AUDIT_STAFF_ID", inParam.getString("AUDIT_STAFF_ID", ""));
        if("8000".equals(productId)){
        	 IData acctInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
             if (IDataUtil.isEmpty(acctInfo))
             {
                 CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_114);
             }
             String acctId = acctInfo.getString("ACCT_ID");
             
             //查询是否有特殊代付关系(集团产品统一付费关系)
             IDataset payRelas = PayRelaInfoQry.getAdvPayRelaByGrpUserIdAndAcctId(userId, acctId);
             if (IDataUtil.isNotEmpty(payRelas))
             {
            	 CSAppException.apperr(BrandException.CRM_BRAND_50);
             }
        }
        
        mebCondStrData.put("NEED_RULE", true);
        
        mebBatData.put("BATCH_OPER_TYPE", "GROUPMEMCANCEL");
        mebBatData.put("BATCH_TASK_NAME", "一键注销集团下所有的成员");
        mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inmodecode);//反向一键注销不走服务开通
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId); // 关系类型

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId); // 品牌信息

        if (StringUtils.isEmpty(brandCode))
        {
            CSAppException.apperr(BrandException.CRM_BRAND_3);
        }
        // 设置默认值
        String judgeOweTag = "1"; // 任何情况下都判断欠费
        String acctBalance = "0"; // 实时结余

 if (!"0".equals(judgeOweTag) && !"GS01".equals(brandCode) && !brandCode.startsWith("VP") && !"BOSG".equals(brandCode))
	 //if (!"0".equals(judgeOweTag) && !"GS01".equals(brandCode) && !brandCode.startsWith("VP"))

        {
            IData oweFeeData = AcctCall.getOweFeeByUserId(userId);

            acctBalance = oweFeeData.getString("ACCT_BALANCE", "0");
        }

        // 查询用户预受理业务
        IDataset userOtherList = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, "PRTA", Route.CONN_CRM_CG, null);

        int prtaSize = 0;

        if (IDataUtil.isNotEmpty(userOtherList))
        {
            prtaSize = userOtherList.size();
        }

        if (Double.parseDouble(acctBalance) < 0 && prtaSize == 0)
        {
            CSAppException.apperr(FeeException.CRM_FEE_108);
            
        }

        if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE))
        {
            if (StringUtils.equals("BOSG", brandCode))
            {
                relaList = RelaBBInfoQry.qryRelationBBAllForBBossMem(userId, null, relationTypeCode, "1");
            }
            else 
            {   
                
                relaList = UserBlackWhiteInfoQry.qryblackWhiteByEcUserId(userId);// 获取该集团当前成员数量
            }
        }
        else
        {
            relaList = RelaUUInfoQry.qryRelationUUAll(userId, null, relationTypeCode);
        }

        // 如果存在成员则注销成员
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);
                if (brandCode.matches("|ADCG|MASG|"))
                {
                    relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER"));
                }
                else
                {
                	//REQ201902250017 【五项考核】流量统付落地一键注销批量任务问题优化--先判断成员号码是否属于销户号码，
                	//若是属于注销号码，则跳过不插tf_b_trade_batdeal表数据
                	String serialNum = relaData.getString("SERIAL_NUMBER_B");
                	IData epary = RouteInfoQry.getMofficeInfoBySn(serialNum);
                	if (IDataUtil.isNotEmpty(epary))
                    {
	                	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNum, epary.getString("EPARCHY_CODE"));
	                	if(IDataUtil.isNotEmpty(userInfo)){
	                		relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
	                	}
                    }
                }
            }

            mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        }

        // 创建集团注销的批量任务
        IData grpBatData = new DataMap();

        IData grpCondStrData = new DataMap();
        grpCondStrData.put("GROUP_ID", groupId);
        grpCondStrData.put("PRODUCT_ID", productId);
        grpCondStrData.put("USER_ID", userId);
        
        grpCondStrData.put("NEED_RULE", true);
        
        grpBatData.put("BATCH_OPER_TYPE", "DESTROYGROUPUSER");
        grpBatData.put("BATCH_TASK_NAME", "一键注销集团用户");
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
