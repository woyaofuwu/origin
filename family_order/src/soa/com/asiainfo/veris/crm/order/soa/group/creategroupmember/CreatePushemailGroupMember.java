
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import java.util.Random;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreatePushemailGroupMember extends CreateGroupMember
{

    protected CreatePushemailGroupMemberReqData reqData;

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        IData paramData = reqData.cd.getProductParamMap(reqData.getBaseMemProduct()); // 根据成员主产品获取成员产品参数信息

        String password = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            int k = random.nextInt(10);
            password += k;
        }

        String bizpwdTmp = password;

        if (IDataUtil.isNotEmpty(paramData))
        {
            bizpwdTmp = ("".equals(paramData.getString("PassWord", "")) ? password : paramData.getString("PassWord"));
        }

        reqData.setBizpwd(bizpwdTmp); // 设置业务密码
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // infoRegPushEmailSpSvcAttr(); // 系统里面表不存在  应该业务不能用了
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreatePushemailGroupMemberReqData();
    }

    private void infoRegPushEmailSpSvcAttr() throws Exception
    {
        IData data = new DataMap();

        data.put("SERVICE_ID", "98001501"); // 海南SP服务
        data.put("INFO_CODE", "300"); // 业务类型 服务开通需要
        data.put("INFO_VALUE", "01");
        data.put("INFO_NAME", "业务类型");
        data.put("REMARK", "集团业务");
        data.put("RSRV_STR2", reqData.getBizpwd());// 业务密码;

        super.addPlatSvcAttrChildTrade(IDataUtil.idToIds(data));
    }

    protected void init() throws Exception
    {
        super.init();

        // 获取手机邮箱生效方式标志
        reqData.setEffectNow(true); // YUNN“立即生效”框不可见，默认立即生效
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreatePushemailGroupMemberReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        IDataset dataset = new DatasetList();

        dataset = reqData.cd.getSpSvc();

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            dataset.getData(i).put("RSRV_DATE1", getAcceptTime()); // 当前时间；平台开通报文用;
        }
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId());
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber());
        data.put("RSRV_STR6", reqData.getBizpwd()); // 业务密码

    }
}
