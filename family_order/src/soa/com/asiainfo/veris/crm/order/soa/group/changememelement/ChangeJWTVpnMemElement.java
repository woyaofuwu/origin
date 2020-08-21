
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;

public class ChangeJWTVpnMemElement extends ChangeMemElement
{

    protected IData paramInfo = new DataMap(); // 产品参数

    private static final Logger logger = Logger.getLogger(ChangeJWTVpnMemElement.class);

    public ChangeJWTVpnMemElement()
    {
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        // 解析产品参数
        makReqDataProductParam();

        paramInfo = getParamData();

        // 需要拼装产品元素信息
        makElementInfo(map);

        super.makReqData(map);

    }

    // 重写
    public void makReqDataElement() throws Exception
    {
        // 解析产品元素
        GroupModuleParserBean.mebElement(reqData, moduleData);

        // 解析资源
        GroupModuleParserBean.mebRes(reqData, moduleData);
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // 变更短号
        infoRegDataVpn();

    }

    public IData getParamData() throws Exception
    {

        String baseMebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = reqData.cd.getProductParamMap(baseMebProductId);
        if (IDataUtil.isEmpty(paramData))
            CSAppException.apperr(ParamException.CRM_PARAM_345);

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行ChangeJWTVpnMember类 getParamData() 得到产品页面传过来的参数<<<<<<<<<<<<<<<<<<<");

        return paramData;
    }

    // 重写基类
    public void actTradeRelationUU() throws Exception
    {
        String role_code_b = reqData.getMemRoleB();

        IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), ""); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据
        if (IDataUtil.isEmpty(relaUUList))
            return;

        IData relaUU = relaUUList.getData(0);
        IData productParam = paramInfo;
        String shortCode = productParam.getString("SHORT_CODE", "");
        String oldShortCode = relaUU.getString("SHORT_CODE", "");

        boolean isChanged = false;

        if (!StringUtils.equals(role_code_b, relaUU.getString("ROLE_CODE_B")))
        {
            relaUU.put("ROLE_CODE_B", role_code_b);
            isChanged = true;
        }
        if (!StringUtils.equals(oldShortCode, shortCode))
        {
            relaUU.put("SHORT_CODE", shortCode);
            isChanged = true;
        }
        if (!isChanged)
            return;

        relaUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        super.addTradeRelation(relaUU);
        reqData.setIsChange(true);
    }

    private void infoRegDataVpn() throws Exception
    {
        // VPN数据
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IData vpnData = UserVpnInfoQry.getMemberVpnByUserId(userId, userIdA, eparchyCode);

        if (IDataUtil.isEmpty(vpnData))
            return;

        IData productParam = paramInfo;

        boolean isChanged = false;

        String oldShortCode = vpnData.getString("SHORT_CODE");
        String shortCode = productParam.getString("SHORT_CODE", "");
        if (!StringUtils.equals(oldShortCode, shortCode))
        {
            vpnData.put("SHORT_CODE", shortCode);
            isChanged = true;
        }
        if (!isChanged)
            return;

        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        addTradeVpnMeb(IDataUtil.idToIds(vpnData));
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getUca().getUserId());
        data.put("RSRV_STR2", relationTypeCode);
        data.put("RSRV_STR3", reqData.getUca().getSerialNumber());

        IDataset uuInfos = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUser().getUserId(), reqData.getUca().getUserId(), relationTypeCode, null);
        if (IDataUtil.isEmpty(uuInfos))
            return;
        String oldRoleCodeB = uuInfos.getData(0).getString("ROLE_CODE_B");
        String oldShortCode = uuInfos.getData(0).getString("SHORT_CODE");
        String newMemRoleB = reqData.getMemRoleB();
        if (!oldRoleCodeB.equals(newMemRoleB))
        {
            if ("2".equals(oldRoleCodeB))
            {
                data.put("RSRV_STR4", "1");
            }
            else if ("2".equals(newMemRoleB))
            {
                data.put("RSRV_STR4", "0");
            }
        }

        IData productParam = paramInfo;
        // hy data.put("RSRV_STR5", oldShortCode.equals(productParam.getString("SHORT_CODE","")) ? "1" : "0");
        data.put("RSRV_STR10", productParam.getString("OUT_PROV_DISCNT", ""));

    }

    private void makElementInfo(IData map) throws Exception
    {

        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();

        IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB(userId, userIdA, ""); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据
        if (IDataUtil.isEmpty(relaUUList))
            return;

        IData relaUU = relaUUList.getData(0);

        IData productParam = paramInfo;

        String shortCode = productParam.getString("SHORT_CODE", "");
        String oldShortCode = relaUU.getString("SHORT_CODE", "");

        if (StringUtils.equals(oldShortCode, shortCode))// 短号没变更
            return;

        IDataset serviceinfos = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);
        if (IDataUtil.isEmpty(serviceinfos))
        {
            CSAppException.apperr(SvcException.CRM_SVC_3, "860");
        }
        IDataset productElements = new DatasetList(); // 元素
        boolean has861 = false;
        for (int i = 0, iSize = serviceinfos.size(); i < iSize; i++)
        {
            IData service = serviceinfos.getData(i);
            String serviceId = service.getString("SERVICE_ID");
            if ("860".equals(serviceId))
            {
                service.put("ELEMENT_ID", "860");
                service.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                service.put("ELEMENT_TYPE_CODE", "S");
                productElements.add(service);
            }

            if ("861".equals(serviceId))
            {
                has861 = true;
                service.put("ELEMENT_ID", "861");
                service.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                service.put("ELEMENT_TYPE_CODE", "S");
                productElements.add(service);
            }
        }

        if (!has861)
        {
            IData element = new DataMap();
            element.put("INST_ID", "");
            element.put("ELEMENT_TYPE_CODE", "S");
            element.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            element.put("PRODUCT_ID", "800001");
            element.put("START_DATE", SysDateMgr.getSysTime());
            element.put("END_DATE", SysDateMgr.getTheLastTime());
            element.put("ATTR_PARAM", "");
            element.put("PACKAGE_ID", "80000101");
            element.put("ELEMENT_ID", "861");
            productElements.add(element);
        }
        map.put("ELEMENT_INFO", productElements);
    }
}
