
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChgVpmnShortCodeBean extends ChangeMemElement
{
    private ChgVpmnShortCodeReqData reqData = null;

    private String smsType = "VpnShortCodeChg"; // 短信类型

    private String resShortCode = "";

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChgVpmnShortCodeReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChgVpmnShortCodeReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        makElementInfo(map);// 拼装产品元素信息
        makResInfo(map); // 拼装资源信息
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        String smsFlag = map.getString("SMS_FLAG");
        if (StringUtils.isNotBlank(smsFlag)) // 如果不为空，则为批量短信修改，批量默认为不发短信
        {
            if ("1".equals(smsFlag))
            {
                reqData.setNeedSms(true); // 批量发完工短信
            }
        }

        reqData.setSHORT_CODE(map.getString("NEW_SHORT_CODE"));

    }

    private void makResInfo(IData map) throws Exception
    {
        String memUserId = map.getString("MEM_USER_ID");
        String grpUserId = map.getString("USER_ID");
        String newShortCode = map.getString("NEW_SHORT_CODE");
        String oldShortCode = map.getString("OLD_SHORT_CODE");
        IDataset resinfos = new DatasetList(); // 资源信息
        String res_type_code = "S";
        IDataset mebress = UserResInfoQry.getResByUserIdResType(memUserId, grpUserId, res_type_code, oldShortCode);

        if (IDataUtil.isEmpty(mebress))
        {
            CSAppException.apperr(ResException.CRM_RES_84);
        }
        resShortCode = newShortCode; // 用于插临时表
        IData mebres = mebress.getData(0);
        mebres.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 状态属性：0-增加，1-删除，2-变更
        mebres.put("END_DATE", SysDateMgr.getSysTime());
        resinfos.add(mebres);

        if (!"".equals(newShortCode))
        {
            IData addres = new DataMap();
            addres.put("MODIFY_TAG", "0");
            addres.put("RES_TYPE_CODE", "S");
            addres.put("RES_CODE", newShortCode);
            resinfos.add(addres);
        }

        map.put("RES_INFO", resinfos);
    }

    private void makElementInfo(IData map) throws Exception
    {
        String memUserId = map.getString("MEM_USER_ID");
        String grpUserId = map.getString("USER_ID");
        String newShortCode = map.getString("NEW_SHORT_CODE");
        String oldShortCode = map.getString("OLD_SHORT_CODE");
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
                service.put("ELEMENT_ID", "860");
                service.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                service.put("ELEMENT_TYPE_CODE", "S");
                productElements.add(service);
            }
            if (!oldShortCode.trim().equals("") && newShortCode.trim().equals(""))
            {
                if ("861".equals(serviceId))
                {
                    has861 = true;
                    service.put("ELEMENT_ID", "861");
                    service.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    service.put("ELEMENT_TYPE_CODE", "S");
                    productElements.add(service);
                }
            }
            else
            {
                if ("861".equals(serviceId))
                {
                    has861 = true;
                    service.put("ELEMENT_ID", "861");
                    service.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    service.put("ELEMENT_TYPE_CODE", "S");
                    productElements.add(service);
                }
            }
        }
        if (oldShortCode.trim().equals("") && !newShortCode.trim().equals(""))
        {
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
        }

        map.put("ELEMENT_INFO", productElements);
    }

    protected IData paramInfo = new DataMap(); // 产品参数

    public ChgVpmnShortCodeBean()
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
        String role_code_b = reqData.getMemRoleB();
        IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), ""); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据

        IData relaUU = relaUUList.getData(0);
        String shortCode = paramInfo.getString("SHORT_CODE", "");
        String oldShortCode = paramInfo.getString("OLD_SHORT_CODE", "");
        boolean bool = false;
        if (StringUtils.isNotEmpty(role_code_b) && !role_code_b.equals(relaUU.getString("ROLE_CODE_B")))
        {
            relaUU.put("ROLE_CODE_B", role_code_b);
            bool = true;
        }
        if (StringUtils.isNotEmpty(oldShortCode) && !oldShortCode.equals(shortCode))
        {
            relaUU.put("SHORT_CODE", shortCode);
            bool = true;
        }
        if (bool)
        {
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
        insertTemporaryShortCode();// 查短号临时表
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

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getUca().getUser().getUserId());
        data.put("RSRV_STR2", relationTypeCode);
        data.put("RSRV_STR3", reqData.getUca().getSerialNumber());

        // 查询UU关系表
        IData uuInfo = RelaUUInfoQry.getRelaByPK(reqData.getGrpUca().getUserId(), reqData.getUca().getUser().getUserId(), relationTypeCode); // TF_F_RELATION_UU", "SEL_BY_PK
        if (IDataUtil.isEmpty(uuInfo))
        {
            return;
        }

        String oldRoleCodeB = uuInfo.getString("ROLE_CODE_B", "");
        String oldShortCode = uuInfo.getString("SHORT_CODE", "");
        String roleCodeB = reqData.getMemRoleB();
        // 角色若改变，角色为2的给该字段赋值，老赋1、 新赋0;
        if (StringUtils.isNotBlank(roleCodeB))
        {
            if (!oldRoleCodeB.equals(roleCodeB))
            {
                if ("2".equals(oldRoleCodeB))
                {
                    data.put("RSRV_STR4", "1");
                }
                else if (roleCodeB.equals("2"))
                {
                    data.put("RSRV_STR4", "0");
                }
            }
        }

        data.put("RSRV_STR5", oldShortCode.equals(reqData.getSHORT_CODE()) ? "1" : "0");// 短号是否变化，0：变、 1：没变
        data.put("RSRV_STR6", smsType);// 短信类型
        data.put("RSRV_STR7", reqData.getSHORT_CODE()); // 短信变量
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
