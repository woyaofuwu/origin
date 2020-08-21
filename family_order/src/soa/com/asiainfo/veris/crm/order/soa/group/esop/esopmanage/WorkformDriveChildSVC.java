package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;
import com.asiainfo.veris.crm.order.soa.group.esop.query.*;
import org.apache.log4j.Logger;

/**
 *
 * 业务规则：  【融合V网】 依赖 【多媒体桌面电话】
 *
 * 多媒体 桌面电话受理 完工之后，如果有 融合v网受理 的 子流程，需要将驱动该子流程；
 *
 * 融合v网注销之后，如果有 多媒体桌面电话注销 的 子流程，需要将驱动该子流程；
 *
 * */
public class WorkformDriveChildSVC extends CSBizService
{

	public void execute(IData data) throws Exception
	{
        String ibsysid = data.getString("BI_SN","");
        String subBusiFormId = data.getString("BUSIFORM_ID","");

        // 拆分子流程时，将 recodeNum 存入  rele 表
        IDataset releInfos = WorkformReleBean.qryBySubBusiformId(subBusiFormId);
        if(IDataUtil.isEmpty(releInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BUSIFORM_ID:" + subBusiFormId + ",查询TF_B_EWE_RELE失败！");
        }

        String recodeNum = releInfos.first().getString("RELE_VALUE", "");

        IData productInfo = WorkformProductBean.qryProductByPk(ibsysid, recodeNum);
        if(IDataUtil.isEmpty(releInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BI_SN:" + ibsysid + ",查询TF_B_EOP_PRODUCT失败！");
        }

        String productId = productInfo.getString("PRODUCT_ID");
        if( !"2222".equals(productId) && !"8001".equals(productId))
        {
            return ;
        }


        IDataset subscribeInfo = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        if(IDataUtil.isEmpty(subscribeInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BI_SN:" + ibsysid + ",查询TF_B_EOP_SUBSCRIBE失败！");
        }

        String operType = subscribeInfo.first().getString("RSRV_STR7");
        String groupId =  subscribeInfo.first().getString("GROUP_ID");

        if("2222".equals(productId) && ( BizCtrlType.MinorecCreateUser.equals(operType) || StringUtils.isEmpty(operType)))
        {
            // 集团产品 【多媒体桌面电话】 受理完成之后， 如果有融合v网的工单，需要 驱动 融合v网 业务受理
            IDataset productInfoList = WorkformProductBean.qryProductListByIbsysidProductId(ibsysid , "8001");

            if (IDataUtil.isNotEmpty(productInfoList))
            {
                // 生成融合v网的子流程之后，会将 融合v网子流程的 busiform_id 回写至 product表当前产品的RSRV_STR2
                String busiformId = productInfoList.first().getString("RSRV_STR2");

                IDataset eweNodeList = EweNodeQry.qryEweNodeByBusiformIdState(busiformId,"W");

                if(IDataUtil.isNotEmpty(eweNodeList))
                {
                    IData eweNode = eweNodeList.first();
                    String busiformNodeId = eweNode.getString("BUSIFORM_NODE_ID");

                    //受理融合v网，需要校验是否 受理了多媒体桌面电话 ， 没有受理，则 改为等待
                    IData ucaInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                    String custId = ucaInfo.getString("CUST_ID");
                    IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(custId,"2222");
                    if(IDataUtil.isEmpty(results) && results.size() <= 0)
                    {
                        return;
                    }
                    else
                    {
                        WorkformStepBean.updByBusiformNodeIdStepId(busiformNodeId,"10000012","0","before");

                        return ;
                    }
                }
            }
        }
        else if ( "8001".equals(productId) && BizCtrlType.MinorecDestroyUser.equals(operType) )
        {
            // 融合v网注销时 ，如果存在 多媒体桌面电话注销 的 子流程 ，需要驱动多媒体桌面电话注销的子流程
            IDataset productInfoList = WorkformProductBean.qryProductListByIbsysidProductId(ibsysid , "2222");

            if (IDataUtil.isNotEmpty(productInfoList))
            {
                String busiformId = productInfoList.first().getString("RSRV_STR2");

                IDataset eweNodeList = EweNodeQry.qryEweNodeByBusiformIdState(busiformId,"W");

                if(IDataUtil.isNotEmpty(eweNodeList))
                {
                    IData eweNode = eweNodeList.first();

                    String busiformNodeId = eweNode.getString("BUSIFORM_NODE_ID");

                    //注销多媒体桌面电话 ，需要校验是否 注销了融合v网 ， 没有注销 ，则 改为等待
                    IData ucaInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
                    String custId = ucaInfo.getString("CUST_ID");
                    IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(custId,"8001");
                    if(IDataUtil.isNotEmpty(results))
                    {
                        return;
                    }
                    else
                    {
                        WorkformStepBean.updByBusiformNodeIdStepId(busiformNodeId,"10000012","0","before");

                        return ;
                    }
                }
            }

        }
	}
}
