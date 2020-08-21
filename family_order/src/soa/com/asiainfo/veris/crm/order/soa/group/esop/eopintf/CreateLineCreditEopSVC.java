package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductSubBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class CreateLineCreditEopSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static transient Logger logger = Logger.getLogger(CreateLineCreditEopSVC.class);

    public IDataset createLineCredit(IData data) throws Exception {
        
        if(logger.isDebugEnabled()) {
            logger.debug("-----------------pangs查看入参：" + data.toString());
        }

        String ibsysId = data.getString("IBSYSID");
        String productId = data.getString("PRODUCT_ID");
        String recordNum = data.getString("RECORD_NUM", "0");
        String nodeId = data.getString("NODE_ID");
        String busiFormId = data.getString("BUSIFORM_ID");

        //获取订单信息
        IDataset subscriberDatas = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysId + "未查询到订单信息！");
        }
        IData subscriberData = subscriberDatas.first();
        String tradeTypeCode = subscriberData.getString("RSRV_STR1");
        if(StringUtils.isBlank(tradeTypeCode)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到信控业务编码！");
        }
        String busiCode = subscriberData.getString("BUSI_CODE");

        //获取USER_ID,7011,7012信控发起为单条线
        IData productInfo = null;
        if("7010".equals(busiCode)) {
            productInfo = WorkformProductBean.qryProductByPk(ibsysId, "0");
        } else {
            productInfo = WorkformProductSubBean.qryProductByPk(ibsysId, "1");
        }
        String userId = productInfo.getString("USER_ID");
        if(StringUtils.isBlank(userId)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到信控用户编码！");
        }

        IData userinfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        // 取下BRAND_CODE，每次都按照ZZZZ查不合理，有些操作类型没有配置ZZZZ
        if(IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(BofException.CRM_BOF_011, userId);
        }
        String brandCode = userinfo.getString("BRAND_CODE", "");
        String mebProduct = userinfo.getString("PRODUCT_ID", "");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("BRAND_CODE", brandCode);
        param.put("PRODUCT_ID", mebProduct);
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if(IDataUtil.isEmpty(userInfo)) {
            CSAppException.apperr(TradeException.CRM_TRADE_180, userId);// 根据用户编码[%s]，查找用户资料不存在！
        }
        param.put("USER_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        
        IData eos = new DataMap();
        /*IDataset relaList = WorkformReleBean.qryBySubBusiformId(busiFormId);
        if(IDataUtil.isNotEmpty(relaList)) {
            //CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到主流程！");
            eos.put("RECORD_NUM", relaList.first().getString("RELE_VALUE"));
        }*/
        if("7010".equals(mebProduct)) {
            eos.put("RECORD_NUM", "0");
        } else {
            eos.put("RECORD_NUM", "1");
        }
        
        eos.put("IBSYSID", ibsysId);
        eos.put("NODE_ID", nodeId);
        param.put("ESOP", eos);
        return CSAppCall.call("SS.CreditLineRegSvc.creditReg", param);

        /*//保存TRADE_ID
        String tradeId = result.first().getString("TRADE_ID");
        IData inparam = new DataMap();
        inparam.put("IBSYSID", ibsysId);
        inparam.put("RECORD_NUM", recordNum);
        inparam.put("TRADE_ID", tradeId);
        StringBuilder sql = new StringBuilder(500);
        sql.append("UPDATE TF_B_EOP_PRODUCT SET TRADE_ID = :TRADE_ID ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        sql.append(" AND RECORD_NUM = :RECORD_NUM ");
        Dao.executeUpdate(sql, inparam, Route.getJourDb(Route.CONN_CRM_CG));

        return result;*/
    }

}
