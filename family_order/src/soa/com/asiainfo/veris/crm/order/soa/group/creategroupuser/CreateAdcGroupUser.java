
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import java.util.List;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateAdcGroupUserReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateAdcGroupUser extends CreateGroupUser
{
    protected CreateAdcGroupUserReqData reqData = null;

    public CreateAdcGroupUser() throws Exception
    {
        super();
    }

    /**
     * 作用：生成其它台帐数据（生成台帐后）
     * 
     * @author liaolc 2014-03-01
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.infoRegSpecialSvcParams();

        // ADC一卡通设备租赁费 added by zhaoyi@2012-09-13
        this.infoRegHireFee();

        // 处理信誉度台帐子表(信用度重算)
        this.actTradeCredit();
        
        //处理EC客户信息
        actTrade();
    }

    // 处理信誉度台帐子表(信用度重算)
    protected void actTradeCredit() throws Exception
    {
        IData map = new DataMap();
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("MODIFY_TAG", map.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));
        super.addTradeCredit(map);
    }

    @Override
    protected void setTradefeeDefer(IData map) throws Exception
    {
        super.setTradefeeDefer(map);
        // ADC一卡通业务收取系统集成费
        // 设置挂账表参数 一卡通业务收取系统集成费
        if ("10005743".equals(reqData.getUca().getProductId()))
        {
            map.put("FEE_MODE", "0");
            map.put("ACT_TAG", "1");
            map.put("FEE_TYPE_CODE", "999");
            map.put("DEFER_CYCLE_ID", "-1");
            map.put("DEFER_ITEM_CODE", "10475");// 一卡通系统集成费

        }

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateAdcGroupUserReqData();
    }

    private void infoRegHireFee() throws Exception
    {

        if (!"10005743".equals(reqData.getUca().getProductId()))
        {
            return;
        }
        String hireFee = reqData.getHireFee();
        if (StringUtils.isNotBlank(hireFee))
        {
            IData other = new DataMap();
            other.put("INST_ID", SeqMgr.getInstId());
            other.put("USER_ID", reqData.getUca().getUserId());
            other.put("RSRV_VALUE_CODE", "OFEE");
            other.put("RSRV_VALUE", reqData.getHireFee());// 设备租赁费
            other.put("RSRV_STR1", reqData.getFeeCycle());// 计费周期
            other.put("RSRV_STR2", "AYKT");// adc的一卡通
            other.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            other.put("START_DATE", getAcceptTime());
            other.put("END_DATE", SysDateMgr.getTheLastTime());
            other.put("REMARK", "企业一卡通设备租赁费");
            addTradeOther(other);
        }
    }

    /**
     * 作用：处理ADC的服务个性化参数 暂时包括platsvc表和 TF_F_USR_GRP_MOLIST业务上行指令表
     * 
     * @author liaolc 2013-08-10
     * @throws Exception
     */
    public void infoRegSpecialSvcParams() throws Exception
    {

        IDataset serparamset = reqData.cd.getSpecialSvcParam();

        if (IDataUtil.isEmpty(serparamset))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1005);
        }

        for (int i = 0; i < serparamset.size(); i++)
        {
            IDataset serparaminfoset = (IDataset) serparamset.get(i);// 个性化参数信息结构
            IData serparam = serparaminfoset.getData(1);
            String serviceId = serparam.getString("ID");// 取集团service_id
            String instId = serparam.getString("INST_ID");// 服务对应的实例标识,同attr表的rela_inst_id相对应/
            IData platsvc = serparam.getData("PLATSVC"); // platsvc表个性参数
            platsvc = IDataUtil.replaceIDataKeyDelPrefix(platsvc, "pam_");
            IDataset tempMolists = IDataUtil.getDataset(serparam, "MOLIST");// 业务上行指令参数
            if (IDataUtil.isNotEmpty(platsvc))
            {
                setRegPlatSvc(platsvc, serviceId, instId);

                // if (serviceId.equals("641300"))// 企信通彩信版开户时 需要自动绑定异网虚拟号码J2EE没有找到这个服务ID，逻辑删除
                // {
                // crateOutnetSnreg(platsvc);
                // }
            }
            if (IDataUtil.isNotEmpty(tempMolists))
            {
                setRegGrpMoList(tempMolists, serviceId);
            }
        }
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateAdcGroupUserReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        // req产品控制信息
        mekReqProductCtrlInfo();

        // 企业一卡通设备租赁费（由前台界面录入）
        if (("10005743").equals(reqData.getUca().getProductId()))
        {
            mekReqUserOther();// 企业一卡通设备租赁费（由前台界面录入）
        }

    }

    private void mekReqProductCtrlInfo() throws Exception
    {
        /*********** 海南往adc平台只传一个优惠****start******目前需要同步的产品10005801 、10005841 ************************/
        String productId = reqData.getUca().getProductId();
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, BizCtrlType.CreateUser);
        // 得到是否向黑白名单台帐表中拼套餐信息
        String synDiscntFlag = ctrlInfo.getAttrValue("SynDiscntFlag");
        reqData.setSynDiscntFlag(synDiscntFlag);
    }

    // 企业一卡通设备租赁费（由前台界面录入）
    private void mekReqUserOther() throws Exception
    {
        // 企业一卡通设备租赁费（由前台界面录入）
        if ("D".equals(reqData.getUca().getUser().getRsrvTag3()))
        {
            reqData.setRsrvStr9("D");
        }
        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        reqData.setHireFee(productParam.getString("NOTIN_HIRE_FEE", ""));
        reqData.setFeeCycle(productParam.getString("NOTIN_FEE_CYCLE", ""));
    }

    /**
     * 作用：处理ADC的指令参数，写入TF_F_USER_GRP_MOLIST
     * 
     * @author liaolc 2013-09-02 16:24
     * @param serparamset
     * @throws Exception
     */
    public void setRegGrpMoList(IDataset molist, String serviceId) throws Exception
    {

        if (IDataUtil.isEmpty(molist))// 执行登记业务上行指令台帐操作
            return;

        for (int j = 0; j < molist.size(); j++)
        {
            IData moinfo = molist.getData(j);
            IData moinforeg = new DataMap();

            moinforeg.put("SEQ_ID", SeqMgr.getGrpMolist());
            moinforeg.put("USER_ID", reqData.getUca().getUserId());
            moinforeg.put("SERVICE_ID", serviceId);
            moinforeg.put("MO_CODE", moinfo.getString("MO_CODE", ""));
            moinforeg.put("MO_MATH", moinfo.getString("MO_MATH", ""));
            moinforeg.put("MO_TYPE", moinfo.getString("MO_TYPE", ""));
            moinforeg.put("DEST_SERV_CODE", moinfo.getString("DEST_SERV_CODE", ""));
            moinforeg.put("DEST_SERV_CODE_MATH", moinfo.getString("DEST_SERV_CODE_MATH", ""));
            moinforeg.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 标识新增

            moinforeg.put("REMARK", moinfo.getString("REMARK", ""));
            moinforeg.put("RSRV_NUM1", moinfo.getString("RSRV_NUM1", ""));
            moinforeg.put("RSRV_NUM2", moinfo.getString("RSRV_NUM2", ""));
            moinforeg.put("RSRV_NUM3", moinfo.getString("RSRV_NUM3", ""));
            moinforeg.put("RSRV_NUM4", moinfo.getString("RSRV_NUM4", ""));
            moinforeg.put("RSRV_NUM5", moinfo.getString("RSRV_NUM5", ""));
            moinforeg.put("RSRV_STR1", moinfo.getString("RSRV_STR1", ""));
            moinforeg.put("RSRV_STR2", moinfo.getString("RSRV_STR2", ""));
            moinforeg.put("RSRV_STR3", moinfo.getString("RSRV_STR3", ""));
            moinforeg.put("RSRV_STR4", moinfo.getString("RSRV_STR4", ""));
            moinforeg.put("RSRV_STR5", moinfo.getString("RSRV_STR5", ""));
            moinforeg.put("RSRV_DATE1", moinfo.getString("RSRV_DATE1", ""));
            moinforeg.put("RSRV_DATE2", moinfo.getString("RSRV_DATE2", ""));
            moinforeg.put("RSRV_DATE3", moinfo.getString("RSRV_DATE3", ""));
            moinforeg.put("RSRV_TAG1", moinfo.getString("RSRV_TAG1", ""));
            moinforeg.put("RSRV_TAG2", moinfo.getString("RSRV_TAG2", ""));
            moinforeg.put("RSRV_TAG3", moinfo.getString("RSRV_TAG3", ""));

            addTradeGrpMolist(moinforeg);
        }
    }

    /**
     * 作用：处理ADC的服务个性化参数，写入TF_F_USER_GRP_PLATSVC表
     * 
     * @author liaolc 2013-09-02 15:43
     * @param serparamset
     *            从页面中获取的个性化参数串
     * @throws Exception
     */
    public void setRegPlatSvc(IData platsvc, String serviceId, String instId) throws Exception
    {

        if (IDataUtil.isEmpty(platsvc))// 完成对platsvc参数表的处理
            return;
        IData data = new DataMap();
        data.put("INST_ID", instId);
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());
        data.put("GROUP_ID", reqData.getUca().getCustGroup().getGroupId());
        data.put("CS_TEL", IDataUtil.getAndDelColValueFormIData(platsvc, "CS_TEL"));
        data.put("ACCESS_NUMBER", platsvc.getString("BIZ_IN_CODE"));
        data.put("BIZ_STATE_CODE", "A"); // cb里无此界面
        data.put("BIZ_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_CODE")); // 1//
        // 业务代码
        data.put("BIZ_NAME", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_NAME")); // 2//
        // 业务名称
        data.put("SERV_CODE", platsvc.getString("BIZ_IN_CODE")); // 3//
        // 服务代码
        data.put("BILLING_TYPE", IDataUtil.getAndDelColValueFormIData(platsvc, "BILLING_TYPE")); // 4//
        // 计费类型//
        // (包月。。)
        data.put("ACCESS_MODE", platsvc.getString("ACCESS_MODE", "")); // 5对应1.3.3版业务承载方式01－SMS，02－WAPPush，03－MMS
        data.put("BIZ_STATUS", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_STATUS")); // 6//
        // 业务状态
        // (正常商用..)
        data.put("BIZ_ATTR", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_ATTR")); // 7
        // 业务属性(订购关系，白黑名单)
        data.put("PRICE", IDataUtil.getAndDelColValueFormIData(platsvc, "PRICE")); // 8 单价
        data.put("BIZ_TYPE_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_TYPE_CODE")); // 9// 业务细类
        // (短信,wap..)NOW这个要加到其他要展现的地方做获取
        data.put("USAGE_DESC", IDataUtil.getAndDelColValueFormIData(platsvc, "USAGE_DESC")); // 10业务方法描述
        data.put("BIZ_PRI", IDataUtil.getAndDelColValueFormIData(platsvc, "BIZ_PRI")); // 11业务优先级
        data.put("INTRO_URL", IDataUtil.getAndDelColValueFormIData(platsvc, "INTRO_URL")); // 12
        // 业务介绍网址
        data.put("CS_URL", IDataUtil.getAndDelColValueFormIData(platsvc, "CS_URL")); // 13EC
        // 的
        // URL
        // 14 计费模式 (上行，下行..)NOW暂无表字段
        data.put("PRE_CHARGE", IDataUtil.getAndDelColValueFormIData(platsvc, "PRE_CHARGE")); // 15
        // 预付费标记
        data.put("MAX_ITEM_PRE_DAY", IDataUtil.getAndDelColValueFormIData(platsvc, "MAX_ITEM_PRE_DAY")); // 16
        // 每天最大短信数
        data.put("IS_TEXT_ECGN", IDataUtil.getAndDelColValueFormIData(platsvc, "IS_TEXT_ECGN")); // 17
        // 短信正文签名
        data.put("MAX_ITEM_PRE_MON", IDataUtil.getAndDelColValueFormIData(platsvc, "MAX_ITEM_PRE_MON")); // 18
        // 每月最大短信
        data.put("DEFAULT_ECGN_LANG", IDataUtil.getAndDelColValueFormIData(platsvc, "DEFAULT_ECGN_LANG")); // 19
        // 签名语言
        data.put("TEXT_ECGN_EN", IDataUtil.getAndDelColValueFormIData(platsvc, "TEXT_ECGN_EN")); // 20中文签名
        data.put("ADMIN_NUM", IDataUtil.getAndDelColValueFormIData(platsvc, "ADMIN_NUM"));// 21 管理员手机号码
        data.put("TEXT_ECGN_ZH", IDataUtil.getAndDelColValueFormIData(platsvc, "TEXT_ECGN_ZH")); // 22
        // 英文签名
        data.put("AUTH_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "AUTH_CODE"));// 23业务接入号鉴权方式
        data.put("FORBID_START_TIME_A", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_A"));
        data.put("FORBID_END_TIME_A", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_A"));
        data.put("FORBID_START_TIME_B", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_B"));
        data.put("FORBID_END_TIME_B", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_B"));
        data.put("FORBID_START_TIME_C", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_C"));
        data.put("FORBID_END_TIME_C", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_C"));
        data.put("FORBID_START_TIME_D", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_START_TIME_D"));
        data.put("FORBID_END_TIME_D", IDataUtil.getAndDelColValueFormIData(platsvc, "FORBID_END_TIME_D"));
        data.put("FIRST_DATE", getAcceptTime());
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());

        data.put("REMARK", IDataUtil.getAndDelColValueFormIData(platsvc, "REMARK"));
        data.put("OPR_EFF_TIME", getAcceptTime());
        data.put("RSRV_NUM1", "");
        data.put("RSRV_NUM2", "");
        data.put("RSRV_NUM3", "");
        data.put("RSRV_NUM4", IDataUtil.getAndDelColValueFormIData(platsvc, "SVR_CODE_HEAD"));// 服务代码头
        data.put("RSRV_NUM5", "");
        data.put("RSRV_STR1", platsvc.getString("MO_ACCESS_NUM", ""));// 短信上行访问码
        //adc产品 RSRV_STR2保存属性值,RSRV_STR4保存属性名，RSRV_STR3优惠编码，RSRV_STR5存优惠名称
        if("100022".equals(serviceId) || "100024".equals(serviceId))
        {
            data.put("RSRV_STR2", platsvc.getString("SP_CODE", ""));//校讯通填写合作伙伴编码
            
        }
        else if("100082".equals(serviceId) || "100083".equals(serviceId))
        {
            data.put("RSRV_STR2", platsvc.getString("CITY_CODE", "")+","+platsvc.getString("CUST_MANAGER_ID", "")
                    +","+platsvc.getString("STAFF_NAME", "")+","+serviceId);//集团通讯录改造
            
        }
        else
        {
            data.put("RSRV_STR2", platsvc.getString("RSRV_STR2", ""));// 企业邮箱业务时前台传入的邮箱域名
        }

        data.put("RSRV_STR3", "02");// 对应行业网关2.0规范的EC业务信息同步isMsgReturn字段
        if("100082".equals(serviceId) || "100083".equals(serviceId))
        {
            data.put("RSRV_STR4", "CITY_CODE,CUST_MANAGER_ID,STAFF_NAME,SERVICE_ID");
            
        }

        /*********** 海南往adc平台只传一个优惠****start******目前需要同步的产品10005801 、10005841 ************************/

        IDataset dctDataset = reqData.cd.getDiscnt();// 前台页面办理的资费
        String isSysDisFlag = reqData.getSynDiscntFlag();
        String strDntList = "";
        String strDntName = "";

        if (IDataUtil.isNotEmpty(dctDataset))// 海南往adc平台只传一个优惠,所以如果有优惠,先对优惠的起始时间进行排序,然后取最后生效的这条
        {
            DataHelper.sort(dctDataset, "START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            // sort("START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            strDntList = dctDataset.getData(0).getString("ELEMENT_ID", "-1");
            strDntName = UDiscntInfoQry.getDiscntNameByDiscntCode(strDntList);
        }
        else
        {
            strDntList = "-1";
            strDntName = "-1";
        }
        if ("true".equals(isSysDisFlag))// 对于需要同步优惠给adc平台的 则保存在这字段
        {
            data.put("RSRV_STR3", strDntList);// 存优惠编码
            data.put("RSRV_STR5", strDntName);// 存优惠名称
        }
        else
        {
            data.put("RSRV_STR3", "");
            data.put("RSRV_STR5", "");
        }
        /************************************* end ***********************************************************************/

        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", platsvc.getString("RSRV_TAG1", ""));// 全网ADC时 产品受理付费模式
        data.put("RSRV_TAG2", platsvc.getString("RSRV_TAG2", "1"));// 集团客户等级

        // J2EE以前标识是否发服务开通，现在没用了.反向接口用TF_B_TRADE.in_mode_code字段,值为P只发网关送， G只发ADC平台， 其他值adc平台和网关都发送.
        data.put("RSRV_TAG3", platsvc.getString("RSRV_TAG3", "0"));

        data.put("IS_NEED_PF", platsvc.getString("IS_NEED_PF", "1"));// 1：走服务开通发指令, 0：不走服务开通不发指令

        data.put("OPER_STATE", "01");// 01表示新增

        data.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE", ""));// 业务接入号
        data.put("SI_BASE_IN_CODE", IDataUtil.getAndDelColValueFormIData(platsvc, "SIBASE_INCODE"));// SI基本接入号 todayadd
        // 2 rows,这里填扩展前的接入号
        data.put("SI_BASE_IN_CODE_A", platsvc.getString("SIBASE_INCODE_A"));// SI 基本接入号属性

        data.put("SERVICE_ID", serviceId);// "92000076");
        data.put("EC_BASE_IN_CODE", platsvc.getString("BIZ_IN_CODE", ""));// 09年12月9号联调，网关平台要求ECBaseServCode与BizServCode一致
        data.put("EC_BASE_IN_CODE_A", data.getString("SI_BASE_IN_CODE_A")); // 现直接写SI接入号的属性
        data.put("PLAT_SYNC_STATE", platsvc.getString("PLAT_SYNC_STATE"));
        data.put("BILLING_MODE", platsvc.getString("BILLING_MODE", "")); // 计费模式
        data.put("DELIVER_NUM", platsvc.getString("DELIVER_NUM", "0")); // 限制下发次数(0为不限制)：

        data.put("MODIFY_TAG", platsvc.getString("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()));// MODIFY_TAG不能为空

        data.put("RSRV_NUM2", platsvc.getString("SERVICE_TYPE", "")); // 业务类型
        data.put("RSRV_NUM3", platsvc.getString("WHITE_TOWCHECK", "")); // 白名单二次确认
        data.put("RSRV_NUM5", platsvc.getString("SMS_TEMPALTE", "")); // 模板短信管理
        data.put("RSRV_STR1", platsvc.getString("PORT_TYPE", "")); // 端口类别

        addTradeGrpPlatsvc(data);
    }
    
  //集团一卡通特殊处理
    protected void actTradePayMode() throws Exception
    {
        IDataset dataset = new DatasetList();

        List<PayMoneyData> payMoneyList = reqData.getPayMoneyList();

        if (payMoneyList == null || payMoneyList.size() == 0)
        {
            return;
        }

        for (PayMoneyData fd : payMoneyList)
        {
            dataset.add(fd.toData());
        }
        
        IDataset deferDataset = new DatasetList();
        IDataset checkDataset = new DatasetList();

        for (int i = 0, row = dataset.size(); i < row; i++)
        {
            IData data = dataset.getData(i);

            // 得到付款方式
            String payMoneyCode = data.getString("PAY_MONEY_CODE");
            if (payMoneyCode.equals("Z"))
            {
                // 支票
                checkDataset.add(data);
            }
            else if (payMoneyCode.equals("A"))
            {
                // 挂帐
                deferDataset.add(data);
            }
        }

        // 处理支票子表
        if (checkDataset.size() > 0)
        {
            setRegCheck(checkDataset);
        }

        // 处理挂账子表
        if (deferDataset.size() > 0)
        {
            IDataset commParaInfos = CommparaInfoQry.getCommParas("CSM", "7788", reqData.getUca().getProductId(), "DEFER", CSBizBean.getUserEparchyCode());// 配置挂账信息
            if (IDataUtil.isNotEmpty(commParaInfos))
            {
                IData commParaInfo = commParaInfos.getData(0);
                for (int i = 0, size = deferDataset.size(); i < size; i++)
                {
                    IData deferData = deferDataset.getData(i);
                    deferData.put("ACT_TAG", commParaInfo.getString("PARA_CODE2", "1"));
                    deferData.put("FEE_MODE", commParaInfo.getString("PARA_CODE3", "0")); // 费用类型：0-营业费用项，1-押金，2-预存
                    deferData.put("FEE_TYPE_CODE", commParaInfo.getString("PARA_CODE4")); // 营业费用类型
                    deferData.put("DEFER_CYCLE_ID", commParaInfo.getString("PARA_CODE5", "-1")); // 账期
                    deferData.put("DEFER_ITEM_CODE", commParaInfo.getString("PARA_CODE6")); // 挂帐帐目:为明细帐目,不指定时挂到默认的帐目
                }
            }

            super.addTradefeeDefer(deferDataset);
         }
    }
    
    @Override
    protected void actTradefeeSub() throws Exception{
        //ADC一卡通特殊处理
        
    }
    
    /**
     * 处理EC客户信息
     * @throws Exception
     */
    private void actTrade() throws Exception{
        if ("9230".equals(reqData.getUca().getProductId())){
        	//直接存台账 
        	IData trade = bizData.getTrade();
        	trade.put("RSRV_STR1", reqData.getUca().getCustGroup().getProvinceCode());//EC归属省编码
        	IData qryParam = new DataMap();
			qryParam.put("CUST_MANAGER_ID", reqData.getUca().getCustGroup().getCustManagerId());
			IDataset maninfos = Dao.qryByCode("TF_F_CUST_MANAGER_STAFF", "SEL_BY_PK", qryParam, Route.CONN_CRM_CEN);
			if (maninfos.size()>0) {
				trade.put("RSRV_STR2", maninfos.getData(0).getString("SERIAL_NUMBER",""));//EC客户经理电话
			}
        }
    }
    

}
