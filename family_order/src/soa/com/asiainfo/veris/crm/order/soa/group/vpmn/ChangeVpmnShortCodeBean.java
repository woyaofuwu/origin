
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChangeVpmnShortCodeBean extends ChangeMemElement
{
    private String resShortCode = "";

    @Override
    protected void makInit(IData map) throws Exception
    {
        // 需要拼装产品元素信息
        makElementInfo(map);
        super.makInit(map);
    }

    private void makElementInfo(IData map) throws Exception
    {
        String memUserId = map.getString("MEM_USER_ID");
        String grpUserId = map.getString("USER_ID");
        IDataset serviceinfos = UserSvcInfoQry.getUserProductSvc(memUserId, grpUserId, null);
        if (IDataUtil.isEmpty(serviceinfos))
        {
            CSAppException.apperr(SvcException.CRM_SVC_3, "860");
        }
        IDataset productElements = new DatasetList(); // 元素
        boolean has861 = false;
        for (int i = 0; i < serviceinfos.size(); i++)
        { 
            IData service = serviceinfos.getData(i);
            String serviceId = service.getString("SERVICE_ID");
            if ("860".equals(serviceId))
            { 
            	service.put("QZ_MODIFY", "true"); 
                service.put("OFFER_ID", "860");   //130000000860
                service.put("OFFER_CODE", "860"); 
                service.put("REL_OFFER_ID", "800001");    //临时加上
            	service.put("OFFER_INS_ID", service.getString("INST_ID"));     
                service.put("ACTION", TRADE_MODIFY_TAG.MODI.getValue());
                service.put("OFFER_TYPE", "S"); 
                
                service.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            	service.put("PRODUCT_ID", "800001");  
            	service.put("ELEMENT_TYPE_CODE", "S");  
            	service.put("ELEMENT_ID", "860");   
                productElements.add(service);
            }

            if ("861".equals(serviceId))
            {
                has861 = true;
            	service.put("QZ_MODIFY", "true"); 
                service.put("OFFER_ID", "861");   //130000000861
                service.put("OFFER_CODE", "861"); 
                service.put("REL_OFFER_ID", "800001");    //临时加上
                service.put("OFFER_INS_ID", service.getString("INST_ID"));   
                service.put("ACTION", TRADE_MODIFY_TAG.MODI.getValue());
                service.put("OFFER_TYPE", "S"); 
                
                service.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                service.put("PRODUCT_ID", "800001");  
            	service.put("ELEMENT_TYPE_CODE", "S");  
            	service.put("ELEMENT_ID", "861");   
                productElements.add(service);
            }
        }

        if (!has861)
        {
            IData element = new DataMap();
            element.put("OFFER_INS_ID", "");
            element.put("OFFER_TYPE", "S");
            element.put("ACTION", TRADE_MODIFY_TAG.Add.getValue());
            element.put("REL_OFFER_ID", "800001");
            element.put("EXPIRE_DATE", SysDateMgr.getSysTime());
            element.put("VALID_DATE", SysDateMgr.getTheLastTime()); 
            element.put("OFFER_ID", "861");   //130000000861
            element.put("OFFER_CODE", "861"); 
//            {"INST_ID":"","START_DATE":"2017-05-10 21:10:19","ELEMENT_TYPE_CODE":"D","MODIFY_TAG":"0","PRODUCT_ID":"800001","END_DATE":"2050-12-31 00:00:00.0","PACKAGE_ID":"80000102","ELEMENT_ID":"654"}]
            element.put("INST_ID", "");  
            element.put("PRODUCT_ID", "800001"); 
            element.put("PACKAGE_ID", "80000101"); 
            element.put("ELEMENT_TYPE_CODE", "S");  
            element.put("ELEMENT_ID", "861");
            element.put("START_DATE", SysDateMgr.getSysTime());  
            element.put("END_DATE", SysDateMgr.getTheLastTime());
            element.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());  
            element.put("END_DATE", SysDateMgr.getTheLastTime());
            productElements.add(element);
        }
        map.put("ELEMENT_INFO", productElements);
    }

    protected IData paramInfo = new DataMap(); // 产品参数

    public ChangeVpmnShortCodeBean()
    {

    }

    /**
     * 生成登记信息
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramInfo = getParamInfo();
    }

    /**
     * 增加短号进uu表
     * 
     * @throws Exception
     */
    @Override
    protected void actTradeRelationUU() throws Exception
    {
        IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), ""); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据

        IData relaUU = relaUUList.getData(0);
        String shortCode = paramInfo.getString("SHORT_CODE", "");
        String oldShortCode = paramInfo.getString("OLD_SHORT_CODE", "");
        boolean bool = false;
        // if (StringUtils.isNotEmpty(role_code_b) && !role_code_b.equals(relaUU.getString("ROLE_CODE_B")))
        // {
        // relaUU.put("ROLE_CODE_B", role_code_b);
        // bool = true;
        // }
        if (StringUtils.isNotEmpty(oldShortCode) && !oldShortCode.equals(shortCode))
        {
            relaUU.put("SHORT_CODE", shortCode);
            bool = true;
        }
        if (bool)
        {
            // shortCodeValidate(shortCode); // 第二次验证短号
            resShortCode = shortCode; // 用于插临时表
            insertTemporaryShortCode();// 插临时表
            relaUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            super.addTradeRelation(relaUU);
            reqData.setIsChange(true);
        }
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author lixiuyu@20101115
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVpn();

    }

    /**
     * 获取参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamInfo() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paraminfo = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paraminfo))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_227);
        }
        return paraminfo;
    }

    /**
     * 处理台帐VPN子表的数据
     * 
     * @param Datas
     *            VPN参数
     * @author liaoyi
     * @throws Exception
     */
    private void infoRegDataVpn() throws Exception
    {

        // 获取成员VPN个性信息
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();

        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IData vpnData = UserVpnInfoQry.getMemberVpnByUserId(userId, userIdA, eparchyCode);
        if (IDataUtil.isEmpty(vpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_240);
        }
        else
        {
            // VPN数据
            IDataset dataset = new DatasetList();

            // 个性化参数
            // 待修改
            vpnData.put("SHORT_CODE", paramInfo.getString("SHORT_CODE", ""));// 成员短号码;
            vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

            dataset.add(vpnData);
            addTradeVpnMeb(dataset);

        }
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setNeedSms(false); // 不发完工短信

    }

    /**
     * 防止前台相同短号同时验证通过的情况，对短号进行写入台账前的第二次验证
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public void shortCodeValidate(String shortCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SHORT_CODE", shortCode);
        param.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
        IData retData = new DataMap();
        retData = VpnUnit.shortCodeValidateVpn(param);

        if (IDataUtil.isNotEmpty(retData))
        {
            if (!retData.getBoolean("RESULT"))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_220, retData.getString("ERROR_MESSAGE"));
            }
        }
    }

    /**
     * 新增短号临时表 wangyf6
     * 
     * @param pd
     * @throws Exception
     */
    public void insertTemporaryShortCode() throws Exception
    {
        String userID = reqData.getGrpUca().getUser().getUserId();
        IData infoData = new DataMap();
        infoData.put("SHORT_CODE", resShortCode);

        // 是否有母集团
        IDataset relaData = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userID, "40", Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(relaData))
        {
            String userIDa = relaData.getData(0).getString("USER_ID_A", "");
            if (StringUtils.isNotBlank(userIDa))
            {
                infoData.put("PARTITION_ID", userIDa.substring(userIDa.length() - 4, userIDa.length()));
                infoData.put("USER_ID_A", userIDa);// 母集团的USER_ID
                infoData.put("RSRV_TAG1", "1");// 1标识是母集团
            }
        }
        else
        {
            infoData.put("PARTITION_ID", userID.substring(userID.length() - 4, userID.length()));
            infoData.put("USER_ID_A", userID);// 集团VPMN的USER_ID
        }
        infoData.put("USER_ID_B", reqData.getUca().getUserId());
        infoData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        infoData.put("ACCEPT_DATE", this.getAcceptTime());
        infoData.put("TRADE_ID", getTradeId());
        infoData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        infoData.put("REMARK", "集团VPMN成员新增");

        try
        {
            boolean resultFlag = Dao.insert("TF_F_TEMPORARY_SHORTCODE", infoData, Route.getCrmDefaultDb());
        }
        catch (Exception e)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_219, infoData.getString("SHORT_CODE"), infoData.getString("USER_ID_A"));

        }
    }

}
