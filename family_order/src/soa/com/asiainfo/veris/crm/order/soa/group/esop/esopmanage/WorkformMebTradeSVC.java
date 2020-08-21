package com.asiainfo.veris.crm.order.soa.group.esop.esopmanage;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupQueryBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.QuickOrderCondBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformErrorLogBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformProductExtBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformReleBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

/**
 * 集团产品受理成功后，通过集团编码，集团用户编码，产品编码，ibsysid 查询成员信息，并进行成员产品受理
 *
 * */
public class WorkformMebTradeSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(WorkformMebTradeSVC.class);

    public IDataset execute(IData data) throws Exception {
        String busiformId = data.getString("BUSIFORM_ID", "");
        String mainIbsysid = data.getString("BI_SN", "");

        // 查询集团子流程RECORD_NUM;
        IDataset eweReleInfo = WorkformReleBean.qryBySubBusiformId(busiformId);
        if (DataUtils.isEmpty(eweReleInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID" + busiformId + "查询TF_B_EWE_RELE为空");
        }

        String mebRecordNum = eweReleInfo.getData(0).getString("RELE_VALUE", "");
        String ecBusiformId = eweReleInfo.getData(0).getString("BUSIFORM_ID", "");

        // 查询主流程RECORD_NUM
        IDataset eweMebRelaInfo = WorkformReleBean.qryBySubBusiformId(ecBusiformId);
        if (DataUtils.isEmpty(eweMebRelaInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据SUB_BUSIFORM_ID" + busiformId + "查询TF_B_EWE_RELE为空");
        }

        String parentRecordNum = eweMebRelaInfo.getData(0).getString("RELE_VALUE");

        // 查询成员数据
        IData mebExtInfos = WorkformProductExtBean.qryProductByPk(mainIbsysid, mebRecordNum, parentRecordNum);
        if (IDataUtil.isEmpty(mebExtInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID=" + mainIbsysid + "查询TF_B_EOP_PRODUCT_EXT表数据失败！");
        }

        String serialNumber = mebExtInfos.getString("SERIAL_NUMBER", "");
        String mebProductId = mebExtInfos.getString("PRODUCT_ID", "");

        // 根据主流程ibsysid和recordNum查询TF_B_EOP_PRODUCT
        IData productInfo = WorkformProductBean.qryProductByPk(mainIbsysid, parentRecordNum);
        if (DataUtils.isEmpty(productInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据IBSYSID" + mainIbsysid + "查询TF_B_EOP_PRODUCT为空");
        }

        String ecUserId = productInfo.getString("USER_ID", "");
        String productId = productInfo.getString("PRODUCT_ID", "");


        boolean IMSFlag = true;
        if (StringUtils.isNotBlank(mebProductId) && "8001".equals(productId)) {
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
            String ibsysid = data.getString("BI_SN", "");
            IDataset subscribeInfo = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysid);
            String groupId = subscribeInfo.first().getString("GROUP_ID");

            IData ucaInfo = UcaInfoQry.qryGrpInfoByGrpId(groupId);

            if (DataUtils.isNotEmpty(userInfo)) {
                String netTypeCode = userInfo.getString("NET_TYPE_CODE", "");
                if ("05".equals(netTypeCode)) {// 固话号码
                    String userId = userInfo.getString("USER_ID", "");

                    IData inparams = new DataMap();
                    String custId = ucaInfo.getString("CUST_ID", "");
                    // 查用户关系表，判断是否有uu关系
                    inparams.put("USER_ID_B", userId);
                    inparams.put("CUST_ID", custId);
                    inparams.put("RELATION_TYPE_CODE", "S1");
                    IDataset idsUU = GroupQueryBean.qryRelationUUByCustIdAndUserIdB(inparams);

                    if (DataUtils.isEmpty(idsUU)) {// 没有开通多媒体桌面电话
                        IMSFlag = false;
                    }
                }
            } else {
                IMSFlag = false;
            }
        }

        if (!IMSFlag) {
            data.put("TEMPLET_STATE", "W");

            IData error = new DataMap();
            error.put("LOG_INFO", "成员号码【" + serialNumber + "】未订购多媒体桌面电话 请先订购多媒体桌面电话!");
            error.put("STEP_ID", data.getString("STEP_ID"));
            error.put("LOG_ID", SeqMgr.getLogId());
            error.put("BUSIFORM_ID", busiformId);
            error.put("BUSIFORM_NODE_ID", data.getString("BUSIFORM_NODE_ID"));
            error.put("VALID_TAG", "0");
            error.put("ACCEPT_MONTH", data.getString("ACCEPT_MONTH"));
            error.put("UPDATE_DATE", data.getString("CREATE_DATE"));
            WorkformErrorLogBean.insertErrorLogInfo(error);
            return IDataUtil.idToIds(data);
        }
        IDataset result = new DatasetList();
        if ("7341".equals(productId)) {// 宽带成员开户特殊处理

            IData svcParam = new DataMap(getCondString(mainIbsysid, "", serialNumber, "WIDENET"));
            if (DataUtils.isEmpty(svcParam)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "成员开户信息为空");
            }

            // 生成CRM工单
            svcParam.put("SERIAL_NUMBER", svcParam.getString("EC_SERIAL_NUMBER"));
            svcParam.put("EC_USER_ID", ecUserId);
            svcParam.put("NODE_ID", data.getString("NODE_ID"));
            svcParam.put("IBSYSID", mainIbsysid);
            svcParam.put("RECORD_NUM", mebRecordNum);
            svcParam.put("BUSIFORM_ID", busiformId);

            logger.debug("==================集团成员宽带受理入参: " + svcParam + "===========");
            result = CSAppCall.call("SS.MergeWideUserCreateRegSVC.tradeReg", svcParam);
            if (DataUtils.isEmpty(result)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "生成CRM工单失败！");
            }
            updateProductExt(result.first(), mebRecordNum, mainIbsysid);// 更新TF_B_EOP_PRODUCT_EXT表中的数据
        } else {
            IData svcParam = getCondString(mainIbsysid, mebProductId, serialNumber, "MEB");
            if (DataUtils.isEmpty(svcParam)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "成员开户信息为空");
            }
            svcParam.put("USER_ID", ecUserId);

            // 构建 tradeExt 数据
            IData eosInfo = svcParam.getData("EOS_INFO", new DataMap());
            eosInfo.put("ATTR_CODE", "ESOP");
            eosInfo.put("ATTR_VALUE", mainIbsysid);
            eosInfo.put("RSRV_STR1", data.getString("NODE_ID"));
            eosInfo.put("RSRV_STR6", mebRecordNum);
            eosInfo.put("RSRV_STR8", busiformId);
            svcParam.put("EOS", new DatasetList(eosInfo));

            // 生成CRM工单
            String svc = svcParam.getString("SVC");
            result = CSAppCall.call(svc, svcParam);
            if (DataUtils.isEmpty(result)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "生成CRM工单失败！");
            }

            updateProductExt(result.first(), mebRecordNum, mainIbsysid);// 更新TF_B_EOP_PRODUCT_EXT表中的数据

        }
        return result;

    }

    public void updateProductExt(IData result, String recordNum, String ibsysid) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        param.put("USER_ID", result.getString("USER_ID"));
        param.put("TRADE_ID", result.getString("TRADE_ID"));

        WorkformProductExtBean.updProductExtByTradeidAndUserid(param);
    }

    public IData getCondString(String ibsysid, String productId, String serialNumber, String rsrvStr1) throws Exception {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("PRODUCT_ID", productId);
        param.put("RSRV_STR1", rsrvStr1);
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset condInfo = QuickOrderCondBean.getConInfoByIbsysidAndSnAndProductId(param);
        if (DataUtils.isEmpty(condInfo)) {
            return new DataMap();
        }
        String condString = "";
        IData condData = condInfo.first();

        for (int i = 1; i < 11; i++) {
            String str = condData.getString("CODING_STR" + i);
            if (StringUtils.isNotEmpty(str)) {
                condString += str;
            } else {
                break;
            }

        }

        return new DataMap(condString);
    }
}
