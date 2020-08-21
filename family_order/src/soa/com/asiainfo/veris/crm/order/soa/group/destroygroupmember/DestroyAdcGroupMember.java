
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyAdcGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;
import com.asiainfo.veris.crm.order.soa.group.common.query.LimitBlackwhiteBean;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class DestroyAdcGroupMember extends DestroyGroupMember
{
    protected StringBuilder namePhone; // 需要发送给平台的学生姓名和非移动号码

    protected StringBuilder namePhoneCode; // 需要发送给平台的学生姓名和非移动号码CODE

    protected DestroyAdcGroupMemberReqData reqData = null;

    private IDataset OutmebBWList = new DatasetList();

    public DestroyAdcGroupMember()
    {

    }

    /**
     * 作用:TF_F_RELATION_BB表中的关系 ADCMAS产品之前插UU表的，J2EE项目之后则改成插BB表
     * 
     * @author admin
     * @throws Exception
     */
    public void actTradeRelationUU() throws Exception
    {
        String uesrIdA = reqData.getGrpUca().getUser().getUserId();
        String userIdB = reqData.getUca().getUserId();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IDataset uuInfos = RelaBBInfoQry.qryBB(uesrIdA, userIdB, relationTypeCode, null);
        if (IDataUtil.isEmpty(uuInfos))
        {
            return;
        }

        IData uuInfo = uuInfos.getData(0);
        reqData.setRoleCodeB(uuInfo.getString("ROLE_CODE_B"));

        uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        uuInfo.put("END_DATE", getAcceptTime()); // 立即结束

        this.addTradeRelationBb(uuInfo);
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author LUOJH
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        regXXTRelation();// 注销校讯通绑定的学生姓名和非移动号码的UU表
        regBlackWhite();
        regMemPlatsvc();
        if ("G".equals(CSBizBean.getVisit().getInModeCode()))// 如果是从短信营业厅发起的，则发完工短信
        {
            // infoRegDataOther();
        }
        if ("10005742".equals(reqData.getGrpUca().getProductId()))
        {
            regOutMemBlackWhite();
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyAdcGroupMemberReqData();
    }

    /**
     * 作用：注销其它服务,暂为农信通产品用到
     * 
     * @param svcList
     * @throws Exception
     */
    public void infoRegUserSvc(IDataset svcList) throws Exception
    {
        IData svcData = null;
        for (int i = 0; i < svcList.size(); i++)// 处理成员服务平台参数信息
        {
            svcData = svcList.getData(i);
            svcData.put("END_DATE", getAcceptTime());
            svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 新增删除标志

        }

        addTradeSvc(svcList);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyAdcGroupMemberReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // ADC校讯通（由前台界面录入）
        if (("10005742").equals(reqData.getGrpUca().getProductId()))
        {
            map.put("OUT_MEBBW_LIST", OutmebBWList);
        }
    }

    /**
     * 作用:处理成员 黑白名单参数信息
     * 
     * @author liaolc 2014-04-03 16:39
     * @throws Exception
     */
    public void regBlackWhite() throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
        IDataset bw = UserBlackWhiteInfoQry.getSEL_VALID_PLAT_BLACKWHITE(inparam);
        String serialNumber = reqData.getUca().getUser().getSerialNumber();
        String custName = reqData.getUca().getCustPerson().getCustName();

        IData data = null;
        for (int i = 0, iSize = bw.size(); i < iSize; i++)
        {
            data = bw.getData(i);

            String grpUserId = reqData.getGrpUca().getUser().getUserId();
            String mebServiceId = data.getString("SERVICE_ID");
            IData platsvc = MemParams.getUserAPlatSvcParam(grpUserId, mebServiceId);

            String grpplatsvcstate = platsvc.getString("PLAT_SYNC_STATE", "");
            // 判断集团服务是否处于暂停状态
//            if ("P".equals(grpplatsvcstate))
//            {
//                IDataset svc = reqData.cd.getSvc();
//                String svcname = "";
//                for (int si = 0, siSize = svc.size(); si < siSize; si++)
//                {
//                    String svcid = svc.getData(si).getString("ELEMENT_ID", "");
//                    if (svcid.equals(mebServiceId))
//                    {
//                        svcname = svc.getData(si).getString("ELEMENT_NAME", "");
//                        break;
//                    }
//                }
//                if (svcname.equals(""))
//                {
//                    svcname = platsvc.getString("BIZ_NAME", "");
//                }
//
   //            CSAppException.apperr(GrpException.CRM_GRP_639, svcname);
//            }

            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("END_DATE", getAcceptTime());
            data.put("REMARK", "");

            if ("10005742".equals(reqData.getUca().getProductId()) && namePhone.length() > 0)
            {
                data.put("RSRV_STR2", namePhone);
            }
            if ("10005742".equals(reqData.getUca().getProductId()) && namePhoneCode.length() > 0)
            {
                data.put("RSRV_STR3", namePhoneCode);
            }
            /**
             * 2.5规范 String bizAttr = platsvc.getString("BIZ_ATTR", ""); if ("0".equals(bizAttr))// 订购关系 {
             * data.put("OPER_STATE", "3");// 退订 } else // 黑白名单 { data.put("OPER_STATE", "1");// 退出 }
             **/
            data.put("OPER_STATE", "02");// 02表示中止，J2EE按接口规范

            String startdate = data.getString("START_DATE", "");
            if (startdate.length() > 19)
            {
                startdate = startdate.substring(0, 19);
            }
            data.put("START_DATE", startdate);

            data.put("EXPECT_TIME", getAcceptTime());

            if ("9188".equals(reqData.getUca().getProductId()))// 如果是商信通产品，则用户期望生效时间 加一天
            {
                data.put("EXPECT_TIME", SysDateMgr.getAddHoursDate(data.getString("EXPECT_TIME"), 24));
            }

            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            // ------------ end --------------------------
            data.put("IS_NEED_PF", "1");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", "0");// J2EE修改 0 默认值没意义，1 只ADC平台，2 只行业网关，
            // 以后有只发一个平台的产品时，可与 in_mode_code绑定用,服开暂时没取RSRV_TAG3字段;现在用的主表in_mode_code字段,字段值为P 只向网关发送数据 值为G 只向ADC发送数据 其他值
            // adc平台和网关都发送.
            String batchId = reqData.getBatchId();
            String batchOperType = reqData.getBatchDealType();
            if (StringUtils.isNotBlank(batchId) && "GROUPMEMCANCEL".equals(batchOperType))// 集团一点注销过来的 默认不走服务开通
            {
                data.put("IS_NEED_PF", "0");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
            }
            // 2013-10-16 -------------- liqi添加的需求----start-----------
            String bizInCode = data.getString("BIZ_IN_CODE");
            String userTypeCode = data.getString("USER_TYPE_CODE");
            // G代表从反向网关过来的
            if ("G".equals(CSBizBean.getVisit().getInModeCode()))
            {
                if ("W".equals(userTypeCode))
                // 白名单
                {
                    LimitBlackwhiteBean.insertLimitBlackWhite(serialNumber, bizInCode, custName);
                }
                if ("B".equals(userTypeCode))
                // 黑名单
                {
                    LimitBlackwhiteBean.deleteLimitBlackWhite(serialNumber, bizInCode, custName);
                }
            }
            // 非G代表从网关过来
            else
            {
                if ("B".equals(userTypeCode))
                // 黑名单
                {
                    IDataset limitDataset = LimitBlackwhiteBean.queryLimitBlackWhite(serialNumber, bizInCode);
                    if (IDataUtil.isNotEmpty(limitDataset))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_1027, serialNumber);
                    }
                }
            }
            // -------2013-10-16 liqi添加的需求----------end--------------------------------------------
        }
        addTradeBlackwhite(bw);

    }

    /**
     * 作用:处理成员平台服务参数信息
     * 
     * @author liaolc 2014-03-09 16:54
     * @throws Exception
     */
    public void regMemPlatsvc() throws Exception
    {
        String mebUserId = reqData.getUca().getUser().getUserId();
        String gepUserId = reqData.getGrpUca().getUser().getUserId();
        IDataset memplatsvcset = UserGrpMebPlatSvcInfoQry.getGrpMemPlatSvcByUserIdEcUserId(mebUserId, gepUserId);

        IData data = null;

        for (int i = 0; i < memplatsvcset.size(); i++)// 处理成员服务平台参数信息
        {
            data = memplatsvcset.getData(i);
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            data.put("EC_USER_ID", reqData.getGrpUca().getUser().getUserId());
            data.put("EC_SERIAL_NUMBER", reqData.getGrpUca().getUser().getSerialNumber());
            data.put("REMARK", "");
            data.put("END_DATE", getAcceptTime());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 新增删除标志

        }

        addTradeGrpMebPlatsvc(memplatsvcset);
    }

    protected void regOutMemBlackWhite() throws Exception
    {
        String mebMainNumber = reqData.getUca().getSerialNumber();// 取得主号码,校讯通改造
        String mebUserId = reqData.getUca().getUserId();
        String roleCodeA = "1";
        String roleCodeB = "1";
        String relationTypeCode = "XX";// 号码
        String ecUserid = reqData.getGrpUca().getUser().getUserId();
        IDataset xxtxmRelaDatas = RelaUUInfoQry.getXXTRelation(mebMainNumber, mebUserId, roleCodeA, roleCodeB, relationTypeCode,ecUserid);

        if (IDataUtil.isNotEmpty(xxtxmRelaDatas))
        {
            IData hmMap = null;
            for (int i = 0; i < xxtxmRelaDatas.size(); i++)
            {
                hmMap = xxtxmRelaDatas.getData(i);
                String outUserId = hmMap.getString("USER_ID_A");// 非移动号码userId
                String outSn = hmMap.getString("SERIAL_NUMBER_A");// 非移动号码SN
                String grpUserId = reqData.getGrpUca().getUserId();
                IDataset blackwhiteDatas = UserBlackWhiteInfoQry.getXxtBlackWhite(outUserId, grpUserId, outSn);
                for (int z = 0, zSize = blackwhiteDatas.size(); z < zSize; z++)
                {
                    IData outMebBlackwhite = blackwhiteDatas.getData(z);
                    outMebBlackwhite.put("OUT_USER_ID", outMebBlackwhite.getString("USER_ID"));
                    outMebBlackwhite.put("OUT_SN", outMebBlackwhite.getString("SERIAL_NUMBER"));

                    outMebBlackwhite.put("USER_ID", reqData.getGrpUca().getUser().getUserId());// 用户第二笔台账构建集团的UCA
                    outMebBlackwhite.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());// 用户第二笔台账构建成员的UCA

                    outMebBlackwhite.put("OPER_STATE", "02");
                    outMebBlackwhite.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    outMebBlackwhite.put("END_DATE", getAcceptTime());
                    outMebBlackwhite.put("RSRV_TAG3", "2");// 2代表只发网关
                    OutmebBWList.add(outMebBlackwhite);
                }
            }
        }
    }

    /*
     * @method 退订校讯通业务,主号码绑定的学生姓名和非运营商号码的blackwhite表也要注销
     */

    /**
     * 注销校讯通绑定的学生姓名和非移动号码的UU表
     * 
     * @throws Exception
     */
    public void regXXTRelation() throws Exception
    {
        namePhone = new StringBuilder();
        namePhoneCode = new StringBuilder();
        String mebMainNumber = reqData.getUca().getSerialNumber();// 取得主号码,校讯通改造
        String mebUserId = reqData.getUca().getUserId();
        String roleCodeA = "1";
        String roleCodeB = "1";
        String relationTypeCode = "XM";// 姓名
        String ecUserid = reqData.getGrpUca().getUser().getUserId();
        IDataset xxtxmRelaDatas = RelaUUInfoQry.getXXTRelation(mebMainNumber, mebUserId, roleCodeA, roleCodeB, relationTypeCode,ecUserid);

        if (IDataUtil.isNotEmpty(xxtxmRelaDatas))
        {
            IData xmMap = null;
            for (int i = 0; i < xxtxmRelaDatas.size(); i++)
            {
                xmMap = xxtxmRelaDatas.getData(i);
                xmMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                xmMap.put("END_DATE", getAcceptTime());
                if (i == 0)
                {
                    namePhone.append(xmMap.getString("SERIAL_NUMBER_A", ""));
                    namePhoneCode.append(xmMap.getString("RSRV_STR1", ""));
                }
                else
                {
                    namePhone.append("," + xmMap.getString("SERIAL_NUMBER_A", ""));
                    namePhoneCode.append("," + xmMap.getString("RSRV_STR1", ""));
                }
                super.addTradeRelation(xmMap);// 注销学生姓名relation_uu表的记录
            }
        }

        // 注销异网移动号码
        String relationTypeCodeXX = "XX";// 号码
        IDataset xxthmRelaDatas = RelaUUInfoQry.getXXTRelation(mebMainNumber, mebUserId, roleCodeA, roleCodeB, relationTypeCodeXX,ecUserid);
        if (IDataUtil.isNotEmpty(xxthmRelaDatas))
        {
            IData hmMap = null;
            for (int i = 0; i < xxthmRelaDatas.size(); i++)
            {
                hmMap = xxthmRelaDatas.getData(i);
                hmMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                hmMap.put("END_DATE", getAcceptTime());
                namePhone.append("," + hmMap.getString("SERIAL_NUMBER_A", ""));
                namePhoneCode.append("," + hmMap.getString("RSRV_STR1", ""));
                super.addTradeRelation(hmMap);// 注销异网号码relation_uu表的记录
            }
        }
    }
}
