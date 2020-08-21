
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateDesktopTelGroupMemberReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

/**
 * @description 多媒体桌面电话集团成员产品受理Bean类
 * @author yish
 * @date 2013-10-14
 */
public class CreateDesktopTelGroupMember extends CreateGroupMember
{
    private static transient Logger logger = Logger.getLogger(CreateDesktopTelGroupMember.class);

    String netTypeCode = "05";

    String cntrxMembPoer = "2";

    String power = "";

    private boolean crtFlag = true;

    private String userType = "0"; // 用户类型

    protected boolean flag = true; // true为移动号；false为固号
    
    private boolean colorFalg = false;

    protected CreateDesktopTelGroupMemberReqData reqData = null;

    /**
     * @description 业务执行前处理
     * @author yish
     * @date 2013-10-14
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * @description 子类执行的动作
     * @author yish
     * @date 2013-10-14
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        setBlackWhiteLists();
        if (crtFlag) // 创建成员
        {
            infoRegDataImpu(userType, reqData.getROLE_SHORT());
        }
        infoTradeSvc();

        infoRegOtherData();
        
        this.checkDiscntLimit();
    }

    /**
     * 作用：写黑白名单表
     * 
     * @author lakenga
     * @param data
     * @throws Exception
     */
    public void addBlackWhite(IData data, String serviceId) throws Exception
    {
        String sn = data.getString("SERIAL_NUMBER");
        data.put("USER_ID", querySuperSnInfo(sn));
        data.put("USER_TYPE_CODE", data.getString("USER_TYPE_CODE")); // 类型IW 白 IB黑
        data.put("EC_USER_ID", reqData.getUca().getUserId());
        data.put("BIZ_IN_CODE", data.getString("BIZ_CODE")); // 呼入呼出限制 1呼出 0 呼入
        data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        data.put("GROUP_ID", reqData.getGrpUca().getCustGroup().getGroupId());
        data.put("BIZ_CODE", data.getString("BIZ_CODE")); // 呼入呼出限制 1呼出 0 呼入
        data.put("BIZ_NAME", "IMSBWLIST");
        data.put("INST_ID", SeqMgr.getInstId());
        data.put("REMARK", reqData.getRemark());
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        data.put("SERVICE_ID", serviceId);
        data.put("EXPECT_TIME", getAcceptTime());
        data.put("START_DATE", getAcceptTime());
        data.put("END_DATE", SysDateMgr.getTheLastTime());
        data.put("OPER_STATE", "03");
        // data.put("RSRV_STR1", "1"); //个人黑白名单
        addTradeBlackwhite(data);
    }

    /**
     * 作用：写缩位拨号到other表
     * 
     * @author lakenga
     * @param data
     * @throws Exception
     */
    public void addOtherShortDialSn(IData data) throws Exception
    {
        IData dataOther = new DataMap();

        dataOther.put("USER_ID", reqData.getUca().getUserId());
        dataOther.put("RSRV_VALUE_CODE", "SHORTDIALSN");
        dataOther.put("RSRV_VALUE", data.getString("SHORT_NUMBER"));
        dataOther.put("RSRV_STR1", data.getString("LONG_NUMBER"));
        dataOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        dataOther.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        dataOther.put("END_DATE", SysDateMgr.getTheLastTime());
        addTradeOther(dataOther);
    }

    /**
     * @description 得到成员个性化参数
     * @param mebProductId
     * @return
     * @throws Exception
     */
    private IData getMebParamInfo(String mebProductId) throws Exception
    {
        IData paramData = reqData.cd.getProductParamMap(mebProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行CreateDesktopTelGroupMember类 getParamData() 得到成员产品页面传过来的参数：paramData=" + paramData + "<<<<<<<<<<<<<<<<<<<");

        return paramData;
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CreateDesktopTelGroupMemberReqData();
    }

    /**
     * @description 处理台账Other子表的数据
     * @author yish
     */
    public void infoRegOtherData() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入CreateDesktopTelGroupMember类 infoRegOtherData()>>>>>>>>>>>>>>>>>>");

        String operCode = "01";

        String desc = "成员创建";
        if ("2".equals(power))
        {
            operCode = "26";
            desc = "成员(管理员)创建";
        }
        if (crtFlag)
        {
            setRegTradeOther("8171", operCode, TRADE_MODIFY_TAG.Add.getValue(), "CNTRX", desc); // 发送成员创建（管理员创建）报文
        }
        else
        {
            operCode = "08";
            desc = "成员修改";
            if ("2".equals(power))
            {
                operCode = "28";
                desc = "成员(管理员)修改";
            }
            setRegTradeOther("8171", operCode, TRADE_MODIFY_TAG.MODI.getValue(), "CNTRX", desc); // 发送成员修改（管理员修改）报文
        }
        setRegTradeOther("8171", "03", TRADE_MODIFY_TAG.Add.getValue(), "CNTRX", "多媒体成员业务配置"); // 发CNTRX平台成员配置业务指令
        if (!flag && crtFlag) // 判断是否05固话and未创建成员，需要发指令给HSS和ENUM
        {
            setRegTradeOther("8172", "01", TRADE_MODIFY_TAG.Add.getValue(), "HSS", "创建HSS用户"); // 创建HSS用户报文
            setRegTradeOther("8173", "01", TRADE_MODIFY_TAG.Add.getValue(), "ENUM", "创建ENUM用户"); // 创建ENUM用户报文
            if(colorFalg)//多媒体彩铃的值
            {
            	setColorTradeOther();
            }

        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CreateDesktopTelGroupMember类 infoRegOtherData()<<<<<<<<<<<<<<<<<<<");
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (CreateDesktopTelGroupMemberReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String cust_id = reqData.getGrpUca().getCustId(); // 集团客户id
        String user_id_b = reqData.getUca().getUserId(); // 成员用户id
        String eparchy_code = reqData.getUca().getUser().getEparchyCode(); // 成员地州
        netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别

        if ("05".equals(netTypeCode))
        {
            flag = false;
        }
        power = map.getString("MEM_ROLE_B", "1");
        if ("2".equals(power))
        {
            cntrxMembPoer = "0";
        }

        // 查impu信息
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(user_id_b, eparchy_code);
        if (IDataUtil.isNotEmpty(impuInfo))
        {
            IData datatmp = impuInfo.getData(0);
            userType = datatmp.getString("RSRV_STR1", ""); // 用户类型
        }
        if ("00".equals(netTypeCode))
        {
            userType = "1"; // 1: 传统移动用户
        }

        // 判断成员是否已经订购其他ims产品，如果没有订购则返回true
        crtFlag = GroupImsUtil.getCreateMebFlag(cust_id, user_id_b);
        if (!crtFlag)
        {
            // 如果成员角色已经存在，则覆盖当前成员角色
            String tmp = GroupImsUtil.getImpuStr4(user_id_b, userType, 0);
            if (StringUtils.isNotBlank(tmp))
            {
                power = tmp; // 已订购ims业务的取impu表, RSRV_STR4 中的成员角色值
            }
        }

        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        // 得到成员参数
        IData paramData = getMebParamInfo(mebProductId);
        String short_code = paramData.getString("SHORT_CODE");
        paramData.put("CNRT_SHORT_CODE", short_code); // 固话短号发报文

        String stype = GroupImsUtil.getMebOrderImsVpn(cust_id, user_id_b) == true ? "1" : "0";
        paramData.put("ServiceType", stype); // 短号有效标志，0-多媒体电话无效；1-融合V网有效
        paramData.remove("OLD_SHORT_CODE"); // 移除该字段，保证短号入attr表

        reqData.cd.putProductParamList(mebProductId, IDataUtil.iData2iDataset(paramData, "ATTR_CODE", "ATTR_VALUE")); // 参数插入attr表

        String role_short = power + "|" + short_code; // impu表RSRV_STR4字段拼值
        reqData.setROLE_SHORT(role_short);
    }

    /**
     * 作用:根据serialNumber 获用户信息,获取不到就生成USER_ID
     * 
     * @author
     * @param superSerialNumber
     * @throws Exception
     */
    public String querySuperSnInfo(String serialNumber) throws Exception
    {
        IDataset userInfos = UserGrpInfoQry.qryTabSqlFromAllDb(serialNumber, "0"); // 0 remove_tag
        if (IDataUtil.isEmpty(userInfos))
        {
            String userid = SeqMgr.getUserId();
            return userid;
        }
        else
        {
            IData tmp = userInfos.getData(0);
            String userId = tmp.getString("USER_ID");
            return userId;
        }
    }

    /**
     * @description 处理主台账表数据
     * @author yish
     * @date 2013-10-14
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        data.put("RSRV_STR2", cntrxMembPoer);
        data.put("NET_TYPE_CODE", netTypeCode); // 网别
        if (!flag) // 取发ENUM的指令序列
        {
            data.put("RSRV_STR1", "1"); // 写死为1，部分数值hss平台报错
        }
    }

    /**
     * 作用：处理黑白名单服务中的黑白名单列表
     * 
     * @author lakenga
     * @throws Exception
     */
    public void setBlackWhiteLists() throws Exception
    {
        IDataset serparamset = reqData.cd.getSpecialSvcParam();
        if (IDataUtil.isEmpty(serparamset))
        {
            serparamset = new DatasetList();
        }
        for (int i = 0, size = serparamset.size(); i < size; i++)
        {
            IDataset specialServicDataset = (IDataset) serparamset.get(i);
            IData tempMap = specialServicDataset.getData(1);

            String serviceId = tempMap.getString("SERVICE_ID", "");
            IDataset blackList = tempMap.getDataset("BLACK_LIST"); // 处理黑名单列表
            if (IDataUtil.isNotEmpty(blackList))
            {
                for (int j = 0, bsize = blackList.size(); j < bsize; j++)
                { // 处理黑名单列表
                    IData blackMap = blackList.getData(j);
                    blackMap.put("USER_TYPE_CODE", "IB");
                    addBlackWhite(blackMap, serviceId);
                }
            }

            IDataset whiteList = tempMap.getDataset("WHITE_LIST"); // 处理白名单列表
            if (IDataUtil.isNotEmpty(whiteList))
            {
                for (int k = 0, wsize = whiteList.size(); k < wsize; k++)
                {
                    IData whiteMap = whiteList.getData(k);
                    whiteMap.put("USER_TYPE_CODE", "IW");
                    addBlackWhite(whiteMap, serviceId);
                }
            }

            IDataset longList = tempMap.getDataset("LOGN_LIST"); // 处理缩位拨号到other表
            if (IDataUtil.isNotEmpty(longList))
            {
                for (int l = 0, lsize = longList.size(); l < lsize; l++)
                {
                    IData longMap = longList.getData(l);
                    addOtherShortDialSn(longMap);
                }
            }
        }
    }

    /**
     * 作用：写other表，用来发报文用
     * 
     * @author luojh 2010-09-03 10:45
     * @param serviceId
     * @param operCode
     * @param modifyTag
     * @throws Exception
     */
    public void setRegTradeOther(String serviceId, String operCode, String modifyTag, String valueCode, String rsrvValue) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();
        centreData.put("RSRV_VALUE_CODE", valueCode); // domain域
        centreData.put("RSRV_VALUE", rsrvValue);
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", serviceId); // 服务id

        centreData.put("RSRV_STR12", "221"); // HSS_SP_SIFC
        if ("HSS".equals(valueCode))
        {
            centreData.put("RSRV_STR12", "1350"); // HSS_SP_SIFC
        }
        
        centreData.put("RSRV_STR20", "101"); // HSS_SPIFC_TEMPLATE_ID

        if ("HSS".equals(valueCode))
        {
            centreData.put("RSRV_STR10", "100"); // 模版id
        }
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("MODIFY_TAG", modifyTag);
        centreData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);
        addTradeOther(dataset);
    }

    /**
     * 重写父类setTradeRelation，往uu表插入短号
     */
    protected void setTradeRelation(IData map) throws Exception
    {
        super.setTradeRelation(map);
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = getMebParamInfo(mebProductId);
        map.put("SHORT_CODE", paramData.getString("SHORT_CODE"));
    }

    /**
     * @description 处理user表数据
     * @author yish
     * @date 2013-10-14
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR2", cntrxMembPoer);
        map.put("NET_TYPE_CODE", netTypeCode); // 网别
        if (!flag) // 取发ENUM的指令序列
        {
            map.put("RSRV_STR1", "1"); // 写死为1，部分数值hss平台报错
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoTradeSvc() throws Exception
    {
    	IDataset tradeSvcs = reqData.cd.getSvc();
    	
    	if(IDataUtil.isNotEmpty(tradeSvcs))
    	{
    		
    		for(int i=0; i < tradeSvcs.size(); i++)
    		{
    			IData tradeSvc = tradeSvcs.getData(i);
    			
                if(IDataUtil.isNotEmpty(tradeSvc))
                {
                    String eleTypeCode = tradeSvc.getString("ELEMENT_TYPE_CODE","");
                    String modifyTag =  tradeSvc.getString("MODIFY_TAG","");
                    String elementId =  tradeSvc.getString("ELEMENT_ID","");
                    
                    if("10122824".equals(elementId) && "S".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
                    {
                    	colorFalg = true;
                    	break;
                    }
                }
    		}
    	}
    }
    
    /**
     * 作用：写多媒体彩铃服务
     * @throws Exception
     */
    private void setColorTradeOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();
        centreData.put("RSRV_VALUE_CODE", "HSS"); // domain域
        centreData.put("RSRV_VALUE", "10122824");// 服务id
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", "10122824"); // 服务id

        centreData.put("RSRV_STR12", "1400"); // HSS_SP_SIFC
        centreData.put("RSRV_STR20", "101"); // HSS_SPIFC_TEMPLATE_ID
        centreData.put("RSRV_STR10", "100"); // 模版id
     
        centreData.put("OPER_CODE", "01"); // 操作类型
        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        centreData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);
        addTradeOther(dataset);
    }
    
    /**
     * 校验“自定义费用套餐[800109]”和"尊享客户集团产品"尊享语音优惠包优惠的互斥关系
     * 校验“自定义费用套餐[800109]”和"集团固话畅聊包产品"手机畅聊包优惠的互斥关系
     * @throws Exception
     * @author chenzg
     * @date 2018-6-1
     */
    private void checkDiscntLimit() throws Exception{
    	String grpUserId = this.reqData.getGrpUca().getUserId();
    	String userId = this.reqData.getUca().getUserId();
    	String serialNumber = this.reqData.getUca().getSerialNumber();
    	//判断多媒体桌面电话集团产品用户是否办理了80019套餐
    	IDataset grpUserDiscnts = UserDiscntInfoQry.queryDeskTopUserDiscnt(grpUserId);
    	if(IDataUtil.isNotEmpty(grpUserDiscnts)){
    		//判断当前成员号码是否办理了尊享语音优惠包优惠
    		IDataset discnts = UserDiscntInfoQry.queryUserZunXDiscnt(userId);
			if(IDataUtil.isNotEmpty(discnts)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码["+serialNumber+"]已办理[尊享语音优惠包],不能加入有[自定义费用套餐(800109)]套餐的多媒体桌面电话产品作为成员！");
			}
    	}
    }

}
