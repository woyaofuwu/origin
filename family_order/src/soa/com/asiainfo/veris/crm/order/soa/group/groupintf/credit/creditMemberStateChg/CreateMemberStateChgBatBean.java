
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.creditMemberStateChg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CreateMemberStateChgBatBean extends CSBizBean
{

    public IDataset crtBat(IData inParam) throws Exception
    {
        IDataset relaList = new DatasetList();

        StringBuilder builder = new StringBuilder(50);

        String userId = inParam.getString("USER_ID");
        String dealFlag = inParam.getString("DEAL_FLAG");
        String systime = SysDateMgr.getSysTime();

        // 创建集团成员暂停恢复批量任务
        IData BatData = new DataMap();

        IData CondStrData = new DataMap();
        CondStrData.put("USER_ID", userId);
        CondStrData.put("DEAL_FLAG", dealFlag);

        BatData.put("BATCH_OPER_TYPE", "COLORRINGSVCSTATECHG");
        BatData.put("BATCH_TASK_NAME", "集团成员暂停恢复");
        BatData.put("SMS_FLAG", "0");
        BatData.put("CODING_STR", CondStrData.toString());
        BatData.put("CREATE_TIME", systime);
        BatData.put("ACTIVE_FLAG", "1");
        BatData.put("ACTIVE_TIME", systime);
        BatData.put("DEAL_TIME", systime);
        BatData.put("DEAL_STATE", "1");
        BatData.put(Route.USER_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());

        IData param = new DataMap();
        param.put("USER_ID", userId);

        // 查询集团用户资料
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 查询集团客户资料
        String grpCustId = userInfo.getString("CUST_ID", "");

        IData grpCustInfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpCustInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }

        String productId = userInfo.getString("PRODUCT_ID");

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);// 关系类型

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);// 查询品牌

        if (brandCode.matches(GroupBaseConst.BB_BRAND_CODE))
        {

            relaList = RelaBBInfoQry.qryRelationBB(userId, relationTypeCode);

        }
        else
        {

            relaList = RelaUUInfoQry.qryRelationUU(userId, relationTypeCode);

        }

        IData retData = new DataMap();
        
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);

                relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
            }

            String BatchId = BatDealBean.createBat(BatData, relaList);

            builder.append("批次号[" + BatchId + "]");
        }
        else
        {
            if("6200".equals(productId)){
                //没有成员数据时，返回成功给信控 
                retData.put("X_RESULTINFO", "成功!");
                retData.put("X_RESULTCODE", "0");
                retData.put("m_resultCode", "115040");
            }
            if(!"6200".equals(productId)){
                CSAppException.apperr(GrpException.CRM_GRP_764);
            }
        }

        retData.put("ORDER_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }
}
