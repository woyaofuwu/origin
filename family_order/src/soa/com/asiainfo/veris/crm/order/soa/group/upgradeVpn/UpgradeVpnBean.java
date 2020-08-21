
package com.asiainfo.veris.crm.order.soa.group.upgradeVpn;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class UpgradeVpnBean extends CSBizBean
{
    /**
     * 构造函数
     */
    public UpgradeVpnBean()
    {
    }

    /**
     * VPMN升级融合VPMN入口 创建批量任务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */

    public IDataset batDealImsVpmnInfo(IData inParam) throws Exception
    {
        String grpUserId = inParam.getString("USER_ID", ""); // 集团userid
        if (StringUtils.isBlank(grpUserId))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_131);

        }
        IData grpUserInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(grpUserId);
        if (IDataUtil.isEmpty(grpUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_189, grpUserId);
        }
        String vpnno = grpUserInfo.getString("SERIAL_NUMBER", "");
        String grpCustId = grpUserInfo.getString("CUST_ID", "");
        IData grpCustInfo = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpCustInfo)) // 未找到对应的集团
        {
            CSAppException.apperr(GrpException.CRM_GRP_791, grpCustId);

        }
        String removeTag = grpCustInfo.getString("REMOVE_TAG", "");
        if (!"0".equals(removeTag))
        {
            CSAppException.apperr(GrpException.CRM_GRP_792, grpCustId);

        }

        String groupid = grpCustInfo.getString("GROUP_ID");

        // 集团定制优惠
        IDataset grp_package = new DatasetList(inParam.getString("GRP_PACKAGE_INFO", ""));
        if (IDataUtil.isEmpty(grp_package))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_125);
        }
        // 判断是否已经提交过升级，并且未完工
        IData paramsinfo = new DataMap();
        paramsinfo.put("BATCH_OPER_TYPE", "VPNUSERUPGRADEIMS");
        paramsinfo.put("SERIAL_NUMBER", grpUserId);
        IDataset batchinfos = BatDealInfoQry.queryBatchDealInfos(paramsinfo);
        if (IDataUtil.isNotEmpty(batchinfos))
        {
            IData dealstateinfo = batchinfos.getData(0);
            String deal_state = dealstateinfo.getString("DEAL_STATE", "");
            if (!deal_state.equals("3") && !deal_state.equals("6") && !deal_state.equals("9"))
            {
                String exec_time = dealstateinfo.getString("EXEC_TIME");
                CSAppException.apperr(VpmnUserException.VPMN_USER_132, exec_time.substring(0, exec_time.length() - 2));
            }
        }

        // 获取可升级成员资费列表 1285 1286 1391
        IDataset defualtDiscntset = ParamInfoQry.getCommparaByParamattr("CSM", "512", "0", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(defualtDiscntset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_133);
        }

        // 成员默认优惠
        String defaultDiscnt = inParam.getString("MEM_DISCNT_CODE", "");
        if (StringUtils.isBlank(defaultDiscnt))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_134);
        }

        // 可升级资费列表
        ArrayList<String> defaultDisList = new ArrayList<String>();
        for (int i = 0; i < defualtDiscntset.size(); i++)
        {
            defaultDisList.add(defualtDiscntset.getData(i).getString("PARA_CODE1", ""));
        }

        IData coninfo = new DataMap();
        coninfo.put("X_SUBTRANS_CODE", "ITF_CRM_TcsGrpIntf");
        coninfo.put("X_TRANS_CODE", "GrpBat");
        coninfo.put("USER_ID", grpUserId);
        coninfo.put("CUST_ID", grpCustId);
        coninfo.put("PRODUCT_ID", "22000020"); // 暂时存放成员产品ID
        coninfo.put("GROUP_ID", groupid);

        // -------------------集团成员--------------
        IDataset discntset = new DatasetList();
        IData discnt = new DataMap();
        discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        discnt.put("CANCEL_TAG", "0");
        discnt.put("ENABLE_TAG", "0");
        String inst_id = SeqMgr.getInstId();
        discnt.put("INST_ID", inst_id);
        discnt.put("START_DATE", SysDateMgr.getSysTime());
        discnt.put("END_DATE", SysDateMgr.getTheLastTime());
        discnt.put("ELEMENT_TYPE_CODE", "D");
        discnt.put("ELEMENT_ID", defaultDiscnt); // 界面下拉框选择的资费

        boolean hasdefaultpackage = false;
        for (int i = 0; i < grp_package.size(); i++)
        { // 界面选择的成员定制资费
            IData discntinfo = grp_package.getData(i);
            discntinfo.remove("STATE_NAME");
            discntinfo.remove("PACKAGE_NAME");
            discntinfo.remove("START_OFFSET");
            discntinfo.remove("START_ABSOLUTE_DATE");
            discntinfo.remove("ELEMENT_TYPE_NAME");
            discntinfo.remove("PRODUCT_NAME");
            discntinfo.remove("ELEMENT_NAME");
            discntinfo.remove("OLD_END_DATE");
            discntinfo.remove("FEE_TYPE_CODE");
            discntinfo.remove("OLD_PROD_ID");
            discntinfo.remove("OLD_START_DATE");
            discntinfo.remove("END_UNIT");
            discntinfo.remove("START_UNIT");
            discntinfo.remove("ELEMENT_INDEX");
            discntinfo.remove("TRADE_STAFF_ID");
            discntinfo.remove("OLD_PACK_ID");
            discntinfo.remove("END_ABSOLUTE_DATE");
            discntinfo.remove("END_OFFSET");
            discntinfo.remove("FEE");
            discntinfo.remove("PAY_MODE");
            String currdiscode = discntinfo.getString("ELEMENT_ID", "");

            // add by xuecd 20100317
            IData inparam = new DataMap();
            inparam.put("USER_ID", grpUserId);
            inparam.put("REMOVE_TAG", "0");
            IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(grpUserId, "0");
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_189, grpUserId);
            }

            if (currdiscode.equals(defaultDiscnt))
            {
                hasdefaultpackage = true;
                discnt.put("PRODUCT_ID", discntinfo.getString("PRODUCT_ID", ""));
                discnt.put("PACKAGE_ID", discntinfo.getString("PACKAGE_ID", ""));
            }
        }
        if (!hasdefaultpackage)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_135, defaultDiscnt);
        }

        discntset.add(discnt);
        coninfo.put("DISCNT", discntset);
        // ////***************************************
        String exec_time = VpnUnit.createVpmnExecTime();
        String productId = "8000";
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId); // 关系类型

        IDataset relaList = RelaUUInfoQry.getAllMebByUSERIDA(grpUserId, relationTypeCode);
        StringBuilder builder = new StringBuilder(50);
        IData mebBatData = new DataMap();
        String mebBatchId = "";
        String mebOperType = "VPNMEMBERUPGRADEIMS";
        // 如果存在成员则注销成员
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);

                relaData.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
                // 获取成员资费信息
                String memUserId = relaData.getString("USER_ID_B");
                IDataset memDisSet = UserDiscntInfoQry.getUserProductDis(memUserId, grpUserId);
                boolean bFound = false;
                for (int m = 0; m < memDisSet.size(); m++)
                {
                    String discode = memDisSet.getData(m).getString("DISCNT_CODE");
                    if (defaultDisList.contains(discode))
                    {
                        bFound = true;
                        break;
                    }
                }

                // 调试期间注释 判断是否订购了358套餐资费 1285 1286 1391，没有就不能升级
                if (!bFound)
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_136, relaData.getString("SERIAL_NUMBER_B"), defaultDisList.toString());
                }
            }
            mebBatData.put("BATCH_OPER_TYPE", mebOperType);
            mebBatData.put("BATCH_TASK_NAME", groupid + "升级融合V网[VPNNO=" + vpnno + "成员处理]");
            mebBatData.put("SMS_FLAG", "0");
            mebBatData.put("CODING_STR", coninfo.toString());
            mebBatData.put("EXEC_TIME", exec_time);
            mebBatData.put("DEAL_STATE", "1"); // 批量启动了
            mebBatData.put("DEAL_TIME", exec_time);
            mebBatData.put("ACTIVE_FLAG", "1"); // 批量启动1：激活标志（启动按钮触发）
            mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        }

        // ------------------ --------------------
        // 创建集团的批量任务
        IData grpBatData = new DataMap();

        IData grpCondStrData = new DataMap();
        grpCondStrData.put("PRODUCT_ID", "8000");// 集团产品ID
        grpCondStrData.put("CUST_ID", grpCustId);
        grpCondStrData.put("USER_ID", grpUserId);
        grpCondStrData.put("GROUP_ID", groupid);
        grpCondStrData.put("GRP_PACKAGE", grp_package); // 集团定制优惠 在此处传入
        grpCondStrData.put("GRP_USER_EPARCHYCODE", inParam.getString("USER_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
        String grpOperType = "VPNUSERUPGRADEIMS";
        grpBatData.put("BATCH_OPER_TYPE", grpOperType);
        grpBatData.put("BATCH_TASK_NAME", groupid + "升级融合V网[VPNNO=" + vpnno + "集团处理]");
        grpBatData.put("SMS_FLAG", "0");
        grpBatData.put("CODING_STR", grpCondStrData.toString());
        grpBatData.put("START_DATE", SysDateMgr.getSysDate()); // common.decodeTimestamp("yyyy-MM-dd",
        // common.getSysDate())
        grpBatData.put("END_DATE", VpnUnit.getLastDay(SysDateMgr.getSysDate()));
        grpBatData.put("EXEC_TIME", exec_time);
        grpBatData.put("DEAL_STATE", "1");
        grpBatData.put("DEAL_TIME", exec_time);
        grpBatData.put("ACTIVE_FLAG", "1"); // 批量启动1：激活标志（启动按钮触发）
        IData grpData = UcaInfoQry.qryUserInfoByUserIdForGrp(grpUserId);

        String grpBatchId = BatDealBean.createBat(grpBatData, IDataUtil.idToIds(grpData));

        builder.append("\n集团批次号[" + getRedirectHtml(grpBatchId, grpOperType) + "]");
        // 插入批量关联信息表
        if (StringUtils.isNotBlank(mebBatchId)) //
        {
            builder.append(";成员批次号[" + getRedirectHtml(mebBatchId, mebOperType) + "]");
            BatDealBean.createBatRealtion(mebBatchId, grpBatchId, "0", "0");
        }
        // ------------------ --------------------

        IData retData = new DataMap();
        retData.put("ORDER_ID", builder.toString());
        return IDataUtil.idToIds(retData);
    }

    public String getRedirectHtml(String batchId, String batchOperType)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<a jwcid=\"@Redirect\" value=\"");
        sb.append(batchId);
        sb.append("\" onclick=\"javascript:openNav('导入详情','group.bat.BatBatchDetial', 'batchDetialQuery', '&cond_BATCH_ID=");
        sb.append(batchId);
        sb.append("&cond_BATCH_OPER_TYPE=");
        sb.append(batchOperType);
        sb.append("', '');\">");
        sb.append(batchId);
        sb.append("</a>");
        return sb.toString();
    }

}
