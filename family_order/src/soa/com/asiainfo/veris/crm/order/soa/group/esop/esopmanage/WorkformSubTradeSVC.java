package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.*;
import org.apache.log4j.Logger;


public class WorkformSubTradeSVC extends CSBizService
{

    private static final Logger logger = Logger.getLogger(WorkformSubTradeSVC.class);

	public IDataset execute(IData data) throws Exception
	{
		
        String ibsysid = data.getString("BI_SN","");
        String subBusiFormId = data.getString("BUSIFORM_ID","");
        String busiformNodeId = data.getString("BUSIFORM_NODE_ID","");

        // 拆分子流程时，将 recodeNum 存入  rele 表
        IDataset releInfos = WorkformReleBean.qryBySubBusiformId(subBusiFormId);
        if(IDataUtil.isEmpty(releInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据BUSIFORM_ID:" + subBusiFormId + ",查询主流程失败！");
        }

        String recodeNum = releInfos.first().getString("RELE_VALUE", "");

        IData productInfo = WorkformProductBean.qryProductByPk(ibsysid,recodeNum);
        if (IDataUtil.isEmpty(productInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"通过IBSYSID:"+ibsysid+"找不到TF_B_EOP_PRODUCT表的记录！");
        }

        String productId = productInfo.getString("PRODUCT_ID");
        String serialNumber = productInfo.getString("SERIAL_NUMBER");

        //受理融合v网，需要校验是否 受理了多媒体桌面电话 ， 没有受理，则 改为等待
        IDataset subscribeInfo = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
        String operType = subscribeInfo.first().getString("RSRV_STR7");

        String groupId = subscribeInfo.first().getString("GROUP_ID");

        IData ucaInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);
        if("8001".equals(productId) && (BizCtrlType.MinorecCreateUser.equals(operType) || StringUtils.isEmpty(operType)))
        {
            IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(ucaInfo.getString("CUST_ID"),"2222");
            if(IDataUtil.isEmpty(results) && results.size() <= 0)
            {
                data.put("TEMPLET_STATE", "W");

                IData error = new DataMap();
                error.put("LOG_INFO","集团客户【"+ucaInfo.getString("CUST_ID")+"】未订购多媒体桌面电话 请先订购多媒体桌面电话!");
                error.put("STEP_ID",data.getString("STEP_ID"));
                error.put("LOG_ID",SeqMgr.getLogId());
                error.put("BUSIFORM_ID",subBusiFormId);
                error.put("BUSIFORM_NODE_ID",busiformNodeId);
                error.put("VALID_TAG","0");
                error.put("ACCEPT_MONTH",data.getString("ACCEPT_MONTH"));
                error.put("UPDATE_DATE",data.getString("CREATE_DATE"));
                WorkformErrorLogBean.insertErrorLogInfo(error);
                return IDataUtil.idToIds(data);
            }
        }
        else if ("2222".equals(productId) && BizCtrlType.MinorecDestroyUser.equals(operType))
        {
            IDataset results = UserProductInfoQry.getGrpProductByGrpCustIdProID(ucaInfo.getString("CUST_ID"),"8001");
            if(IDataUtil.isEmpty(results) && results.size() <= 0)
            {
                data.put("TEMPLET_STATE", "W");

                IData error = new DataMap();
                error.put("LOG_INFO","集团客户【"+ucaInfo.getString("CUST_ID")+"】未注销融合v网， 请先注销融合v网!");
                error.put("STEP_ID",data.getString("STEP_ID"));
                error.put("LOG_ID",SeqMgr.getLogId());
                error.put("BUSIFORM_ID",subBusiFormId);
                error.put("BUSIFORM_NODE_ID",busiformNodeId);
                error.put("VALID_TAG","0");
                error.put("ACCEPT_MONTH",data.getString("ACCEPT_MONTH"));
                error.put("UPDATE_DATE",data.getString("CREATE_DATE"));
                WorkformErrorLogBean.insertErrorLogInfo(error);
                return IDataUtil.idToIds(data);
            }
        }

        IData svcParam = new DataMap();
        svcParam.put("IBSYSID",ibsysid);
        svcParam.put("PRODUCT_ID",productId);
        svcParam.put("SERIAL_NUMBER",serialNumber);
        svcParam.put("RSRV_STR1","EC");

        // 根据 条件，查询 集团受理 con表  的 参数 && 服务；
        IDataset svcList = CSAppCall.call("SS.QuickOrderCondSVC.getConInfoByIbsysidAndSnAndProductId",svcParam);
        if(IDataUtil.isEmpty(svcList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据订单号【"+ibsysid+"】,产品id：【"+productId+"】,服务号码：【"+serialNumber+"】查询TF_B_EOP_QUICKORDER_COND表流程信息为空!");
        }

        IData svcData = svcList.first();
        IData svcInput = getCodingStr(svcData);

        String svc = svcInput.getString("SVC");

        // 构建  tradeExt 数据
        IData eosInfo = svcInput.getData("EOS_INFO",new DataMap());
        eosInfo.put("ATTR_CODE", "ESOP");
        eosInfo.put("ATTR_VALUE", ibsysid);
        eosInfo.put("RSRV_STR1", data.getString("NODE_ID"));
        eosInfo.put("RSRV_STR6", recodeNum);
        eosInfo.put("RSRV_STR8", subBusiFormId);
        svcInput.put("EOS", new DatasetList(eosInfo));

        logger.debug("=============================调用服务 :"+svc+";  数据 :"+svcInput);

        // 调登记服务
        IDataset results = CSAppCall.call( svc, svcInput);

        logger.debug("=============================服务返回结果: "+results );
        // 调登记服务

        IData result = results.first();
        String userId = result.getString("USER_ID");
        String tradeId = result.getString("TRADE_ID");

        /*
         * 将 user_id && trade_id 回写至 product 表
         * */
        productInfo.put("USER_ID",userId);
        productInfo.put("TRADE_ID",tradeId);
        WorkformProductBean.updProductByTradeidAndUserid(productInfo);
		return results;
	}


	public IData getCodingStr(IData param)
    {
        if(IDataUtil.isEmpty(param))
        {
            return null;
        }

        String codingStr = "";

        for(int i = 1 ;i< 11 ; i++)
        {
            if(StringUtils.isNotEmpty(param.getString("CODING_STR"+i)))
            {
                codingStr += param.getString("CODING_STR"+i);
            }
            else
            {
                break;
            }
        }

        return new DataMap(codingStr);

    }
}
