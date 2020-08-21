
package com.asiainfo.veris.crm.order.soa.group.upgradeVpn;

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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class UpVpnToCountrywideBean extends CSBizBean
{
    /**
     * 构造函数
     */
    public UpVpnToCountrywideBean()
    {
    }

    /**
     * 验证函数 j2ee 转规则配置
     * 
     * @param pd
     * @param step
     *            当前步骤
     * @param data
     *            数据
     * @return (true:正确 false:错误)
     * @author xiajj
     */
    public boolean validchk(String chkFlag, IData data) throws Exception
    {
        // String userId = td.getUserId();
        // String serialNumber = td.getUserInfo().getString("SERIAL_NUMBER");
        // CSAppEntity dao = new CSAppEntity(pd, "cg");
        // IData param = new DataMap();
        // param.put("GROUP_USER_ID", userId);
        //
        //
        // param.put("PRODUCT_ID", td.getUserInfo().getString("PRODUCT_ID"));
        // param.put("TRADE_TYPE_CODE", td.getTradeTypeCode());
        // param.put("USER_ID", userId);
        // GroupBaseBean.setDbConCode(pd, "cg");
        // IDataset tradeInfo = TradeInfoQry.getTradeByUserIdProductIdTradetype(pd, param);
        // if (tradeInfo.size() > 0)
        // {
        // common.warn("V网用户【" + serialNumber + "】有未完工单，业务不能继续！");
        // }
        //
        // // 集团客户标识
        // param.put("CUST_ID", td.getCustInfo().getString("CUST_ID"));
        //
        // // 集团用户标识
        // param.put("ID", td.getUserInfo().getString("USER_ID"));
        //
        // CheckForGrp.chkGrpUserChg(pd, param);
        return true;
    }

    /**
     * VPMN升级跨省VPMN入口 创建批量任务,分三种情况1.VPN升级为跨省VPN（标识：hasVpnscare） 2.订购“漫游短号服务”（标识：hasAdd801） 3.升级为跨省VPN同时订购漫游短号服务
     * 
     * @param inParam
     * @return
     * @throws Exception
     */

    public IDataset batDealVpmnInfo(IData inParam) throws Exception
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
            return null;
        }
        String vpnno = inParam.getString("VPN_NO", "");// 选择的跨省VPN_NO
        String grpCustId = grpUserInfo.getString("CUST_ID", "");
        IData grpCustInfo = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(grpCustInfo)) // 未找到对应的集团
        {
            CSAppException.apperr(GrpException.CRM_GRP_791, grpCustId);
            return null;
        }
        String removeTag = grpCustInfo.getString("REMOVE_TAG", "");
        if (!"0".equals(removeTag))
        {
            CSAppException.apperr(GrpException.CRM_GRP_792, grpCustId);
            return null;
        }

        // 集团订购了漫游短号服务
        boolean hasAdd801 = inParam.getBoolean("HAS_ADD_801", false);
        if (hasAdd801)
        {
            // 校验订购“漫游短号服务”的条件
            String operType = "2"; // 校验成员是否有校园卡用户和验证短号正确性
            VpnUnit.validchk801Svc(grpUserId, operType);
        }

        String groupid = grpCustInfo.getString("GROUP_ID");

        // 集团定制优惠
        IDataset grp_package = inParam.getDataset("GRP_PACKAGE_INFO", null);

        if(IDataUtil.isNotEmpty(grp_package))
        {
        	int size = grp_package.size();
        	for(int i = size - 1;i >= 0; i--)
        	{
        		IData grpPackage = grp_package.getData(i);
        		if(IDataUtil.isNotEmpty(grpPackage))
        		{
        			String modifyTag = grpPackage.getString("MODIFY_TAG","");
        			String typeCode = grpPackage.getString("ELEMENT_TYPE_CODE","");
        			if(StringUtils.equals("EXIST", modifyTag) && StringUtils.equals("D", typeCode))
        			{
        				grp_package.remove(i);
        			}
        		}
        	}
        }
        
        // 判断是否已经提交过升级，并且未完工
        IData paramsinfo = new DataMap();
        paramsinfo.put("BATCH_OPER_TYPE", "VPNUSERSCARECHANGE");
        paramsinfo.put("SERIAL_NUMBER", grpUserInfo.getString("SERIAL_NUMBER"));
        IDataset batchinfos = BatDealInfoQry.queryBatchDealInfos(paramsinfo);
        if (IDataUtil.isNotEmpty(batchinfos))
        {
            IData dealstateinfo = batchinfos.getData(0);
            String deal_state = dealstateinfo.getString("DEAL_STATE", "");
            if (!deal_state.equals("3") && !deal_state.equals("6") && !deal_state.equals("9"))
            {
                String exec_time = dealstateinfo.getString("EXEC_TIME");
                CSAppException.apperr(VpmnUserException.VPMN_USER_176, exec_time.substring(0, exec_time.length() - 2));
            }
        }
        String defaultDiscnt = inParam.getString("DEFAULT_DISCNTCODE", "");
        String defaultProId = inParam.getString("DEFAULT_PRODUCT_ID", "");
        String defaultPackageId = inParam.getString("DEFAULT_PACKAGE_ID", "");
        if (StringUtils.isBlank(defaultDiscnt))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_180);
        }
        String oldvpnscare = inParam.getString("OLD_VPN_SCARE_CODE");
        String vpnscare = inParam.getString("VPN_SCARE_CODE");
        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId("8000");
        // add by lixiuyu@20100802 判断用户是否已经订购“漫游短号服务”
        boolean old801Flag = inParam.getBoolean("OLD_801_FLAG"); // 资料是否已存在801服务，是：true 否：false
        IDataset dataset = new DatasetList();
        IData coninfo = new DataMap();
        coninfo.put("X_SUBTRANS_CODE", "ITF_CRM_TcsGrpIntf");
        coninfo.put("X_TRANS_CODE", "GrpBat");
        coninfo.put("VPN_SCARE_CODE", vpnscare);
        coninfo.put("USER_ID", grpUserId);
        coninfo.put("CUST_ID", grpCustId);
        coninfo.put("PRODUCT_ID", memProductId); // 暂时存放成员产品ID
        coninfo.put("GROUP_ID", groupid);

        // add by lixiuyu@20100816 判断集团订购“漫游短号服务（发指令）（801）”的标志
        if (old801Flag)
        {
            coninfo.put("ADD_FLAG", "1");
        }
        else
        {
            coninfo.put("ADD_FLAG", "0");
        }

        // -------------------集团成员--------------
        boolean hasVpnscare = inParam.getBoolean("HAS_VPN_SCARE");
        if (hasVpnscare)
        {
            IDataset discntset = new DatasetList();
            IData discnt = new DataMap();
            discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            discnt.put("CANCEL_TAG", "0");
            discnt.put("ENABLE_TAG", "0");
            discnt.put("INST_ID", "");
            discnt.put("START_DATE", SysDateMgr.getSysTime());
            discnt.put("END_DATE", SysDateMgr.getTheLastTime());
            discnt.put("ELEMENT_TYPE_CODE", "D");
            discnt.put("ELEMENT_ID", defaultDiscnt);
            discnt.put("PRODUCT_ID", defaultProId);
            discnt.put("PACKAGE_ID", defaultPackageId);
            discnt.put("ATTR_PARAM", "");
            discntset.add(discnt);
            coninfo.put("DISCNT", discntset);
        }

        String start_date = SysDateMgr.getSysTime();
        String exec_time = VpnUnit.createVpmnExecTime();

        String productId = "8000";
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId); // 关系类型

        IDataset relaList = RelaUUInfoQry.getAllMebByUSERIDA(grpUserId, relationTypeCode);
        StringBuilder builder = new StringBuilder(50);
        IData mebBatData = new DataMap();
        String mebBatchId = "";
        String mebOperType = "VPNMEMBERSCARECHANGE";
        // 如果存在成员则
        IDataset memberDataset = new DatasetList();
        if (IDataUtil.isNotEmpty(relaList))
        {
            for (int i = 0, row = relaList.size(); i < row; i++)
            {
                IData relaData = relaList.getData(i);

                IData data = new DataMap();
                data.put("DEAL_STATE", "1");
                data.put("EXEC_TIME", exec_time);
                data.put("SERIAL_NUMBER", relaData.getString("SERIAL_NUMBER_B"));
                memberDataset.add(data);

            }
            String taskName = "";
            if (hasVpnscare)
            {
                taskName = groupid + "跨省升级[VPNNO=" + vpnno + "成员处理]";
            }
            else
            {
                taskName = groupid + "集团订购漫游短号服务成员处理";
            }
            mebBatData.put("BATCH_OPER_TYPE", mebOperType);
            mebBatData.put("BATCH_TASK_NAME", taskName);
            mebBatData.put("SMS_FLAG", "0"); // 1：发短信
            mebBatData.put("CODING_STR", coninfo.toString());
            mebBatData.put("EXEC_TIME", exec_time);
            mebBatData.put("DEAL_STATE", "1"); // 批量启动了
            mebBatData.put("ACTIVE_FLAG", "1"); // 批量启动1：激活标志（启动按钮触发）
            mebBatData.put("DEAL_TIME", exec_time);
            mebBatchId = BatDealBean.createBat(mebBatData, memberDataset);
        }

        // -------------------集团用户--------
        // 创建集团的批量任务
        IData grpBatData = new DataMap();

        IData paraprod = new DataMap();
        if (hasVpnscare)
        {
            String dial_typee = inParam.getString("DIAL_TYPE_CODE", ""); // 拨打方式
            paraprod.put("DIAL_TYPE_CODE", dial_typee);
            paraprod.put("VPN_NO", vpnno);
            paraprod.put("SCP_CODE", "11");
            paraprod.put("DEFAULT_DISCNTCODE", defaultDiscnt);
            IData vpnpara = new DataMap();
            vpnpara.put("VPN_NO", vpnno);
            vpnpara.put("GROUP_ID", groupid);
            vpnpara.put("RSRV_TAG1", "0");
            vpnpara.put("RSRV_TAG2", "0");
            IDataset vpnnoinfos = GrpExtInfoQry.getVPNNOByVPNNO(vpnpara);
            if (IDataUtil.isEmpty(vpnnoinfos))
            {
                // j2ee common.error("获取全国下发的集团号信息失败!");
                CSAppException.apperr(VpmnUserException.VPMN_USER_179);
            }
            IData vpninfo = vpnnoinfos.getData(0);
            paraprod.put("PRO_SRC", vpninfo.getString("RSRV_STR2", ""));
            paraprod.put("SCP_GT", vpninfo.getString("RSRV_STR3", ""));
        }
        else
        {
            paraprod.put("FLAG", "1");// VPMN订购漫游短号服务传个标志不修改VPN表资料
        }

        boolean hasDel801 = inParam.getBoolean("HAS_DEL_801");
        IData grpCondStrData = new DataMap();
        grpCondStrData.put("PRODUCT_ID", "8000");// 集团产品ID
        grpCondStrData.put("CUST_ID", grpCustId);
        grpCondStrData.put("USER_ID", grpUserId);
        grpCondStrData.put("GROUP_ID", groupid);
        grpCondStrData.put("GRP_PACKAGE", grp_package); // 集团定制优惠 在此处传入
        grpCondStrData.put("GRP_USER_EPARCHYCODE", inParam.getString("USER_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
        grpCondStrData.put("VPN_SCARE_CODE", vpnscare);
        grpCondStrData.put("productParam", paraprod);
        grpCondStrData.put("OLD_801_FLAG", old801Flag);
        grpCondStrData.put("HAS_DEL_801", hasDel801);
        grpCondStrData.put("HAS_ADD_801", hasAdd801);
        grpCondStrData.put("HAS_VPN_SCARE", hasVpnscare);

        String grpOperType = "VPNUSERSCARECHANGE";
        String grpTaskName = "";
        if (hasVpnscare)
        {
            grpTaskName = groupid + "跨省升级[VPNNO=" + vpnno + "集团处理]";
        }
        else
        {
            grpTaskName = groupid + "集团订购漫游短号服务集团处理";
        }
        grpBatData.put("BATCH_OPER_TYPE", grpOperType);
        grpBatData.put("BATCH_TASK_NAME", grpTaskName);
        grpBatData.put("SMS_FLAG", "0");
        grpBatData.put("CODING_STR", grpCondStrData.toString());
        grpBatData.put("START_DATE", SysDateMgr.getSysDate());
        grpBatData.put("END_DATE", VpnUnit.getLastDay(SysDateMgr.getSysDate()));
        grpBatData.put("EXEC_TIME", exec_time);
        grpBatData.put("DEAL_STATE", "1");
        grpBatData.put("ACTIVE_FLAG", "1"); // 批量启动1：激活标志（启动按钮触发）
        grpBatData.put("DEAL_TIME", exec_time);
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

    /**
     * 作用：组织元素数据
     * 
     * @param pd
     * @param td
     * @param selectedElements
     * @throws Exception
     * @author lixiuyu
     */
    public IData genElementsInfo(IDataset selectedElements) throws Exception
    {
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();
        IDataset svcAttrList = new DatasetList();
        IDataset grppackages = new DatasetList();
        IDataset elementList = new DatasetList();
        IData elementData = new DataMap();

        // 分解全量SELECTED_ELEMENTS串为服务、优惠串
        for (int i = 0; i < selectedElements.size(); i++)
        {
            elementList = null; // j2ee GroupUtil.getDataset(selectedElements.getData(i), "ELEMENTS");
            for (int j = 0; j < elementList.size(); j++)
            {
                elementData = elementList.getData(j);
                // 如果是成员产品 则进GRP_PACKAGE
                if ("22000020".equals(elementData.getString("PRODUCT_ID", "")))
                {
                    grppackages.add(elementData);
                }
                else
                {
                    if ("S".equals(elementData.getString("ELEMENT_TYPE_CODE", "")))
                    {
                        svcList.add(elementData);
                    }
                    if ("D".equals(elementData.getString("ELEMENT_TYPE_CODE", "")))
                    {
                        discntList.add(elementData);
                    }
                }
                // 服务参数拼串
                if (elementData.containsKey("SERV_PARAM"))
                {
                    IDataset servParamList = new DatasetList(elementData.getString("SERV_PARAM"));
                    IData servData = null;
                    IData inData = new DataMap();
                    // IDataset servAttrList = new DatasetList();
                    for (int k = 0; k < servParamList.size(); k++)
                    {
                        servData = servParamList.getData(k);
                        inData = new DataMap();
                        inData.put("INST_TYPE", elementData.getString("ELEMENT_TYPE_CODE", "S"));
                        inData.put("ATTR_CODE", servData.getString("ATTR_CODE"));
                        inData.put("ATTR_VALUE", servData.getString("ATTR_VALUE"));
                        svcAttrList.add(inData);
                    }
                }
            }
        }
        IData toTdData = new DataMap();
        toTdData.put("X_TRADE_SVC", svcList);
        toTdData.put("X_TRADE_DISCNT", discntList);
        if (svcAttrList.size() > 0)
        {
            toTdData.put("X_TRADE_ATTR", svcAttrList);
        }
        if (grppackages.size() > 0)
        {
            toTdData.put("X_TRADE_GRP_PACK", grppackages);
        }
        return toTdData;
    }

}
