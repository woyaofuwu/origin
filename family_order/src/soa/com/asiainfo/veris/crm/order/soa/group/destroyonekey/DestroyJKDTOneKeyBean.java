package com.asiainfo.veris.crm.order.soa.group.destroyonekey;

import com.ailk.common.data.IData;


import org.apache.log4j.Logger;



import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BrandException;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class DestroyJKDTOneKeyBean extends CSBizBean{
	private final static Logger logger = Logger.getLogger(DestroyJKDTOneKeyBean.class);
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
        
        String userId = inParam.getString("USER_ID");
        IData grpUserInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userId);
        if(IDataUtil.isEmpty(grpUserInfo))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "34110:用户资料不存在，业务不能继续！");
        }
        
        String custId = grpUserInfo.getString("CUST_ID");
    	IData grpInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
        if(IDataUtil.isEmpty(grpInfo))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "34111:客户资料不存在，业务不能继续！");
        }
        String groupId = grpInfo.getString("GROUP_ID");
        
        IData grpMainProdInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        String productId = grpMainProdInfo.getString("PRODUCT_ID");
        
        String inmodecode = inParam.getString("IN_MODE_CODE");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员注销的批量任务
        IData mebBatData = new DataMap();

        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("NEED_RULE", false);
        //add by chenzg@20180709--REQ201804280001集团合同管理界面优化需求-----
        
        mebCondStrData.put("RECEPTIONHALLMEM", "JKDT");//daidl  集客大厅受理标识
        mebCondStrData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());//设置接口路由
        mebCondStrData.put("ACTION","0");//操作类型  0-删除
        mebCondStrData.put("TRANSIDO",inParam.getString("TRANSIDO"));//操作类型  0-删除
        mebCondStrData.put("PRODUCTID",inParam.getString("PRODUCTID"));
        mebCondStrData.put("BIPCODE",inParam.getString("BIPCODE"));
        mebCondStrData.put("ORDER_NO",inParam.getString("ORDER_NO"));
        mebCondStrData.put("KIND_ID",inParam.getString("KIND_ID"));
      
        
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
      
       
        if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE))
        {
            if (StringUtils.equals("BOSG", brandCode))
            {
                relaList = RelaBBInfoQry.qryRelationBBAllForBBossMem(userId, null, relationTypeCode, "1");
            }
            else 
            {   
            	relaList = RelaBBInfoQry.qryRelationBBAll(userId, null, relationTypeCode);           	
            }
        }
        else
        {
            relaList = RelaUUInfoQry.qryRelationUUAll(userId, null, relationTypeCode);
      
        }

        // 如果存在成员则注销成员
        if (IDataUtil.isNotEmpty(relaList))
        {
        	IDataset relaListtmp = new DatasetList();
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);           
                if(!relaData.getString("SERIAL_NUMBER_B").startsWith("09")){
                	relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
                	relaListtmp.add(relaData);
                }
            }

            mebBatchId = BatDealBean.createBat(mebBatData, relaListtmp);
        }

        // 创建集团注销的批量任务
        IData grpBatData = new DataMap();

        IData grpCondStrData = new DataMap();
        grpCondStrData.put("GROUP_ID", groupId);
        grpCondStrData.put("PRODUCT_ID", productId);
        grpCondStrData.put("USER_ID", userId);
        grpCondStrData.put("NEED_RULE", false);
        grpCondStrData.put("RECEPTIONHALLMEM","JKDT");//集客大厅受理标识
        grpCondStrData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());//设置接口路由
        grpCondStrData.put("ACTION","0");//操作类型  0-删除
        grpCondStrData.put("TRANSIDO",inParam.getString("TRANSIDO"));//操作类型  0-删除
        
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
