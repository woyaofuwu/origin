
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyAdcGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyNewXxtGroupMember extends DestroyGroupMember
{

    protected DestroyAdcGroupMemberReqData reqData = null;

    public DestroyNewXxtGroupMember()
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
        this.regXxtUUAndBlackWhite();// 注销新校讯通relationXxt表
        this.regMemPlatsvc();

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyAdcGroupMemberReqData();
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

        super.addTradeGrpMebPlatsvc(memplatsvcset);
    }

    /**
     * 注销校讯通绑定的学生姓名和非移动号码的UU表
     * 
     * @throws Exception
     */
    public void regXxtUUAndBlackWhite() throws Exception
    {

        String mainSn = reqData.getUca().getSerialNumber();// 取得主号码,校讯通改造
        String mebUserId = reqData.getUca().getUserId();
        String roleCodeA = "1";
        String roleCodeB = "1";
        String relationTypeCode = "XT";// 注销校讯通
        String ecUserid = reqData.getGrpUca().getUser().getUserId();
        IDataset outSNRelaDatas = RelaUUInfoQry.getXXTRelation(mainSn, mebUserId, roleCodeA, roleCodeB, relationTypeCode,ecUserid);
        Boolean isMain = false;
        if (IDataUtil.isNotEmpty(outSNRelaDatas))
        {
            IData outSnRelaUU = null;
            IData relaxxt = null;

            for (int i = 0; i < outSNRelaDatas.size(); i++)
            {
                outSnRelaUU = outSNRelaDatas.getData(i);

                String outSn = outSnRelaUU.getString("SERIAL_NUMBER_A");// relation_uu表保存的是异网号码
                if (!outSn.equals(mainSn))
                {
                	mebUserId = outSnRelaUU.getString("USER_ID_A", "");
                }
                else
                {
                    mebUserId = reqData.getUca().getUserId();
                    isMain = true;
                    
                }
                String grpUserId = reqData.getGrpUca().getUserId();

                // 注销relation_uu
                outSnRelaUU.put("END_DATE", getAcceptTime());
                outSnRelaUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                super.addTradeRelation(outSnRelaUU);// 注销成员号与异网号relation_uu表的记录

                // 注销 blackwhite异网号码
                IDataset blackwhiteDatas = UserBlackWhiteInfoQry.getBlackWhitedataByUserIdEcuserid(mebUserId, grpUserId);
                IData bwData = null;
                for (int j = 0; j < blackwhiteDatas.size(); j++)
                {
                    bwData = blackwhiteDatas.getData(j);
                    bwData.put("OPER_STATE", "02");// 终止
                    bwData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    bwData.put("END_DATE", getAcceptTime());
                    bwData.put("IS_NEED_PF", "1");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
                    bwData.put("RSRV_TAG3", "0");// J2EE修改 0 走服务开通发指令，1 只ADC平台，2 只行业网关，
                    bwData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 删除标志

                    super.addTradeBlackwhite(bwData);// 删除异网号的黑白名单记录
                }
                // 注销 relation_XXT
                IDataset relaDatas = RelaXxtInfoQry.queryMemInfoByOutSnMebUserIdGrpUserId(outSn, mainSn, grpUserId);

                for (int a = 0; a < relaDatas.size(); a++)
                {
                    relaxxt = relaDatas.getData(a);
                    relaxxt.put("SERVICE_ID", "915001"); // 新校讯通目前只支持 校讯通成员短信服务
                    relaxxt.put("END_DATE", getAcceptTime());
                    relaxxt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    super.addTradeRelationXxt(relaxxt);// 注销学生参数XXT
                }

            }
            if(!isMain)
            {
                IDataset blackwhiteMainDatas = UserBlackWhiteInfoQry.getBlackWhitedataByUserIdEcuserid(reqData.getUca().getUserId(),  reqData.getGrpUca().getUserId());
                IData bwMainData = null;
                for (int j = 0; j < blackwhiteMainDatas.size(); j++)
                {
                    bwMainData = blackwhiteMainDatas.getData(j);
                    bwMainData.put("OPER_STATE", "02");// 终止
                    bwMainData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    bwMainData.put("END_DATE", getAcceptTime());
                    bwMainData.put("IS_NEED_PF", "0");// J2EE新增IS_NEED_PF字段表示是否走服务开通，1或者是空： 走服务开通发指令,0：不走服务开通不发指令
                    bwMainData.put("RSRV_TAG3", "0");// J2EE修改 0 走服务开通发指令，1 只ADC平台，2 只行业网关，
                    bwMainData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());// 删除标志

                    super.addTradeBlackwhite(bwMainData);// 删除主号码的黑白名单记录
                } 
                
            }

        }

    }
}
