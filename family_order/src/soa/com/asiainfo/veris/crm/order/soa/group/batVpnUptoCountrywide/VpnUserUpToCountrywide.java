
package com.asiainfo.veris.crm.order.soa.group.batVpnUptoCountrywide;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class VpnUserUpToCountrywide extends ChangeUserElement
{
    private static transient Logger logger = Logger.getLogger(VpnUserUpToCountrywide.class);

    private VpnUserUpToCountrywideReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new VpnUserUpToCountrywideReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (VpnUserUpToCountrywideReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setProductParam(new DataMap(map.getString("productParam", "{}")));
        reqData.setHasDel801(map.getBoolean("HAS_DEL_801", false));
        reqData.setHasAdd801(map.getBoolean("HAS_ADD_801", false));
        reqData.setOld801Flag(map.getBoolean("OLD_801_FLAG", false));
        reqData.setHasVpnScare(map.getBoolean("HAS_VPN_SCARE", false));
    }

    public VpnUserUpToCountrywide()
    {

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg 2011-1-25
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegTradeSvc();

        // add by lixiuyu@20100512 VPMN订购漫游短号服务标志flag = 1时不修改VPN表资料
        if (!"1".equals(reqData.getProductParam().getString("FLAG", "")))
        {
            infoRegDataVpn();
        }
    }

    private void infoRegDataVpn() throws Exception
    {
        // VPN数据
        IData map = new DataMap();
        String user_id = reqData.getUca().getUserId();
        IDataset vpnDatas = UserVpnInfoQry.qryUserVpnByUserId(user_id);
        if (IDataUtil.isEmpty(vpnDatas))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_6, user_id);
        }
        IData vpnData = vpnDatas.getData(0);
        String logicalVpn = vpnData.getString("VPN_NO");
        IData productParam = reqData.getProductParam();
        vpnData.put("VPN_NO", productParam.getString("VPN_NO", ""));
        vpnData.put("RSRV_STR3", logicalVpn); // 老的vpnno
        vpnData.put("RSRV_STR4", productParam.getString("DEFAULT_DISCNTCODE", ""));
        vpnData.put("RSRV_TAG1", productParam.getString("DIAL_TYPE_CODE", ""));
        vpnData.put("RSRV_STR1", productParam.getString("PRO_SRC", ""));
        vpnData.put("RSRV_STR2", productParam.getString("SCP_GT", ""));
        vpnData.put("SCP_CODE", productParam.getString("SCP_CODE", ""));
        vpnData.put("VPN_SCARE_CODE", productParam.getString("VPN_SCARE_CODE", "2"));
        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeVpn(IDataUtil.idToIds(vpnData));
    }

    /**
     * @throws Exception
     */
    public void infoRegTradeSvc() throws Exception
    {
        String grpUserId = reqData.getUca().getUserId();

        IDataset svcDatas = new DatasetList();
        if (reqData.isHasVpnScare()) // 选择了跨省V网
        {
            IData svc801data = new DataMap();
            svc801data.put("USER_ID_A", "-1");
            svc801data.put("USER_ID", grpUserId);
            svc801data.put("START_DATE", getAcceptTime());
            svc801data.put("END_DATE", SysDateMgr.getTheLastTime());
            svc801data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            svc801data.put("SERVICE_ID", "9211");
            svc801data.put("CANCEL_TAG", "0");
            svc801data.put("NEED_SERV_PARAM", "false");
            svc801data.put("PARAM_VERIFY_SUCC", "true");
            svc801data.put("ELEMENT_TYPE_CODE", "S");
            svc801data.put("ENABLE_TAG", "0");
            svc801data.put("PRODUCT_MODE", "10");
            svc801data.put("PACKAGE_ID", "80000001");
            svc801data.put("PRODUCT_ID", "8000");
            svc801data.put("INST_ID", SeqMgr.getInstId());// 实例标识
            svc801data.put("REMARK", "V网升级为跨省V网");
            svcDatas.add(svc801data);

            IData svc802data = new DataMap();
            svc802data.put("START_DATE", getAcceptTime());
            svc802data.put("END_DATE", SysDateMgr.getTheLastTime());
            svc802data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            svc802data.put("CANCEL_TAG", "0");
            svc802data.put("NEED_SERV_PARAM", "false");
            svc802data.put("PARAM_VERIFY_SUCC", "true");
            svc802data.put("ELEMENT_TYPE_CODE", "S");
            svc802data.put("ENABLE_TAG", "0");
            svc802data.put("USER_ID_A", "-1");
            svc802data.put("USER_ID", grpUserId);
            svc802data.put("PRODUCT_MODE", "10");
            svc802data.put("PACKAGE_ID", "80000001");
            svc802data.put("PRODUCT_ID", "8000");
            svc802data.put("SERVICE_ID", "9200");
            svc802data.put("INST_ID", SeqMgr.getInstId());// 实例标识
            svc802data.put("REMARK", "V网升级为跨省V网");
            svcDatas.add(svc802data);
        }
        boolean old801Flag = false;
        IDataset old801svcs = UserSvcInfoQry.qryUserSvcByUserSvcId(reqData.getUca().getUserId(), "801");
        if (IDataUtil.isNotEmpty(old801svcs))
        {
            old801Flag = true;
        }
        // 办理VPMN升级时取消漫游短号服务（801）
        if (reqData.isHasDel801() && old801Flag)
        {
            IData old801svc = (IData) old801svcs.get(0);
            IData delSvcId801data = new DataMap();
            delSvcId801data.put("USER_ID", grpUserId);
            delSvcId801data.put("USER_ID_A", "-1");
            delSvcId801data.put("PACKAGE_ID", "80000001");
            delSvcId801data.put("PRODUCT_ID", "8000");
            delSvcId801data.put("SERVICE_ID", "801");
            delSvcId801data.put("START_DATE", old801svc.getString("START_DATE", "").substring(0, 19));
            delSvcId801data.put("END_DATE", SysDateMgr.decodeTimestamp(this.getAcceptTime(), SysDateMgr.PATTERN_STAND_YYYYMMDD));
            delSvcId801data.put("INST_ID", old801svc.getString("INST_ID", ""));
            delSvcId801data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            svcDatas.add(delSvcId801data);

        }
        // 办理了漫游短号服务（801）
        if (reqData.isHasAdd801())
        {
            IData svc801 = new DataMap();
            svc801.put("START_DATE", getAcceptTime());
            svc801.put("END_DATE", SysDateMgr.getTheLastTime());
            svc801.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            svc801.put("CANCEL_TAG", "0");
            svc801.put("NEED_SERV_PARAM", "false");
            svc801.put("PARAM_VERIFY_SUCC", "true");
            svc801.put("ELEMENT_TYPE_CODE", "S");
            svc801.put("ENABLE_TAG", "0");
            svc801.put("USER_ID_A", "-1");
            svc801.put("USER_ID", grpUserId);
            svc801.put("PRODUCT_MODE", "10");
            svc801.put("PACKAGE_ID", "80000001");
            svc801.put("PRODUCT_ID", "8000");
            svc801.put("SERVICE_ID", "801");
            svc801.put("INST_ID", SeqMgr.getInstId());// 实例标识
            svc801.put("REMARK", "V网升级为跨省V网");
            svcDatas.add(svc801);
        }

        // 集团产品变更漫游短号服务(801)，登记服务子台帐，走服务开通
        if (reqData.isHasAdd801() || (reqData.isHasDel801() && old801Flag))
        {
            IDataset tmpSvc = UserSvcInfoQry.qryUserSvcByUserSvcId(reqData.getUca().getUserId(), "800");
            if (IDataUtil.isEmpty(tmpSvc))
            {
                CSAppException.apperr(GrpException.CRM_GRP_684);
            }
            IData tmpSvcData = (IData) tmpSvc.get(0);
            tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            tmpSvcData.put("SERVICE_ID", tmpSvcData.getString("SERVICE_ID", ""));
            svcDatas.add(tmpSvcData);
        }
        addTradeSvc(svcDatas);
    }

}
