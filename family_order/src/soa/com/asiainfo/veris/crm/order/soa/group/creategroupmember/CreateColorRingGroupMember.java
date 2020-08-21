
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateColorRingGroupMember extends CreateGroupMember
{
    private IData paramData = new DataMap();

    /**
     * 构造函数
     * 
     * @author 孙翰韬
     * @param pd
     */
    public CreateColorRingGroupMember()
    {
    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        // add by lixiuyu@20100505 对集团关键人的处理
        paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("JOIN_IN", "")))
            {
                validchkCustManagerId(); // 判读是否有操作权限
            }
        }
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("CANCEL_LING", "")))
            {
                infoRegDataColorRingOther();
            }
        }

    }

    /**
     * 获取VPMN个性化参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamData() throws Exception
    {
        // VPMN个性化参数
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());

        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }

    /**
     * 生成Other表数据
     * 
     * @throws Exception
     */
    public void infoRegDataColorRingOther() throws Exception
    {
        String memUserId = reqData.getUca().getUser().getUserId();
        String firstTimeNextAcct = DiversifyAcctUtil.getFirstTimeNextAcct(memUserId); // 下账期第一时间
        IData data = new DataMap();

        data.put("USER_ID", memUserId);
        data.put("START_DATE", diversifyBooking ? firstTimeNextAcct : getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("RSRV_VALUE_CODE", "DLMR");
        data.put("RSRV_VALUE", "不可来电取消彩铃业务");
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        this.addTradeOther(data);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setNeedSms(map.getBoolean("IF_SMS", true)); // 是否发短信
        //add by chenzg@20180314 REQ201803020016关于优化集团V网、集团彩铃的集团成员级业务二次确认短信的需求
        this.paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("TWOCHECK_SMS_FLAG", "")))
            {
                map.put("PAGE_SELECTED_TC", "true");	//页面上选择了下发二次确认短信选项
            }
        }
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER

        if (IDataUtil.isNotEmpty(paramData))
        {
            if ("1".equals(paramData.getString("JOIN_IN", ""))) // 1000000000000000000000000000000000000000
            {
                // 替换第一位
                setProcessTag(1, "1");
            }
        }
    }

    /**
     * 判断集团关键人的操作是否属于分管客户经理
     * 
     * @throws Exception
     * @author lixiuyu@20100505
     */
    public void validchkCustManagerId() throws Exception
    {
        String staffId = CSBizBean.getVisit().getStaffId();

        String memUserId = reqData.getUca().getUserId();
        IDataset mebInfos = GrpMebInfoQry.getGroupInfoByMember(memUserId, reqData.getUca().getUserEparchyCode(), null);
        if (IDataUtil.isNotEmpty(mebInfos))
        {
            IData mebInfo = mebInfos.getData(0);
            String memberKind = mebInfo.getString("MEMBER_KIND", "");
            String custManagerId = mebInfo.getString("CUST_MANAGER_ID", "");
            if ("1".equals(memberKind) || "2".equals(memberKind)) // 1-联络员, 2-集团管理员
            {
                if (!custManagerId.equals(staffId))
                {
                    // 错误，集团关键人操作仅限分管客户经理
                    CSAppException.apperr(GrpException.CRM_GRP_673);
                }
            }
        }
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        makBeforeInit(map);
        super.makInit(map);

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        // 因为在makInit已经查出三户资料了，所以这里不再查
    }

    /**
     * VPMN成员产品变更登记服务子台帐，走服务开通
     * 
     * @param map
     * @return
     * @throws Exception
     */
    public void makBeforeInit(IData map) throws Exception
    {

        IDataset elelist = map.getDataset("ELEMENT_INFO", null);

//        if (IDataUtil.isNotEmpty(elelist))
//        {
//            for (int i = 0; i < elelist.size(); i++)
//            {
//                IData info = elelist.getData(i);
//                if ("S".equals(info.getString("ELEMENT_TYPE_CODE")) && "20".equals(info.getString("ELEMENT_ID")))
//                {
//                    // add by lixiuyu@20100125 用户要求集团彩铃成员新增如果有个人彩铃(20)时，就不绑定集团彩铃包的个人彩铃
//                    IDataset userSvcInfos = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(reqData.getUca().getUser().getUserId(), "20");
//                    if (IDataUtil.isNotEmpty(userSvcInfos))
//                    {
//                        elelist.remove(i);
//                        break;
//                    }
//                }
//
//            }
//        }

    }
}
