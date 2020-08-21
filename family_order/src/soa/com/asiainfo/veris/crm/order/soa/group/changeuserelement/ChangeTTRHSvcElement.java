
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class ChangeTTRHSvcElement extends GroupBean
{
    private static final Logger logger = Logger.getLogger(ChangeTTRHSvcElement.class);

    private static String newStatecode = "";

    private static String oldStatecode = "";

    private static String stateCode = "";

    public void actTradeSub() throws Exception
    {
        actTradeDataUser();
        actTradeDataSvcState();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        map.put("CHK_FLAG", "BaseInfo");

        map.put("TRADE_TYPE_CODE", this.getTradeTypeCode());

        newStatecode = map.getString("USER_STATE_CODESET");

        stateCode = map.getString("STATE_CODE");
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForGrpNormal(map);
    }

    public void actTradeDataUser() throws Exception
    {
        IData userInfo = reqData.getUca().getUser().toData();
        oldStatecode = userInfo.getString("USER_STATE_CODESET", "");
        if (!oldStatecode.equals(newStatecode))
        {
            userInfo.put("USER_STATE_CODESET", newStatecode);
            userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

            String net_typecode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getUca().getProductId());
            if (StringUtils.isEmpty(net_typecode))
                net_typecode = "00";

            userInfo.put("NET_TYPE_CODE", net_typecode);
        }
        this.addTradeUser(userInfo);
    }

    public void actTradeDataSvcState() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();

        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", productId, "20", CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(commparaInfos))
            CSAppException.apperr(GrpException.CRM_GRP_713, "当前[" + productId + "]没有配置主体服务!");

        String serviceId = commparaInfos.getData(0).getString("PARA_CODE1");

        IDataset idataset = UserSvcStateInfoQry.getUserSvcStateBySvcId(userId, serviceId, stateCode);

        if (IDataUtil.isEmpty(idataset))
            return;

        IDataset result = new DatasetList();
        // 截止老服务
        IData userSvcSateData = idataset.getData(0);
        userSvcSateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        userSvcSateData.put("END_DATE", getAcceptTime());

        result.add(userSvcSateData);

        // 新增服务状态
        IData addSvcStateData = new DataMap();
        addSvcStateData.put("USER_ID", reqData.getUca().getUserId());
        addSvcStateData.put("STATE_CODE", newStatecode); // 正常
        addSvcStateData.put("MAIN_TAG", "1");
        addSvcStateData.put("START_DATE", this.getAcceptTime());
        addSvcStateData.put("END_DATE", SysDateMgr.getTheLastTime());
        addSvcStateData.put("SERVICE_ID", serviceId);
        addSvcStateData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        addSvcStateData.put("INST_ID", SeqMgr.getInstId());// 实例ID
        result.add(addSvcStateData);

        this.addTradeSvcstate(result);
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        String net_typecode = UProductInfoQry.getNetTypeCodeByProductId(reqData.getUca().getProductId());
        if (StringUtils.isEmpty(net_typecode))
            net_typecode = "00";

        data.put("NET_TYPE_CODE", net_typecode);

    }

    @Override
    protected final void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();
        String ctrlType = "";

        if ("1".equals(stateCode))
        {
            ctrlType = BizCtrlType.SVCOpen;
        }
        else
        {
            ctrlType = BizCtrlType.SVCStop;
        }
        getProductCtrlInfo(productId, ctrlType);

    }

}
