
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

/**
 * @description 多媒体桌面电话集团成员产品注销Bean类
 * @author yish
 * @date 2013-10-14
 */
public class DestroyDesktopTelGroupMember extends DestroyGroupMember
{
    private static transient Logger logger = Logger.getLogger(DestroyDesktopTelGroupMember.class);

    String netTypeCode = "";

    String power = "";

    // modify by yish 20131212 begin for ims o.9.2版本
    private boolean delFlag = true;

    private String userType = "0"; // 用户类型

    // modify by yish 20131212 end

    protected DestroyDesktopTelGroupMemberReqData reqData = null;
    
    private String colorFalg = "3";

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
        infoTradeSvc();
        infoRegOtherData();
        infoRegDataImpu();
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyDesktopTelGroupMemberReqData();
    }

    /**
     * @description 将手机用户登记在impu表
     * @author yish
     * @date 2013-10-14
     * @throws Exception
     */
    public void infoRegDataImpu() throws Exception
    {
        if (delFlag) // 删除成员
        {
            // 查询是否存在IMPU信息；
            String eparchyCode = reqData.getUca().getUser().getEparchyCode();
            String userId = reqData.getUca().getUserId();

            IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(userId, userType, eparchyCode);
            if (IDataUtil.isNotEmpty(impuInfo))
            {
                IDataset dataset = new DatasetList();
                for (int i = 0, size = impuInfo.size(); i < size; i++)
                {
                    IData impuData = (IData) impuInfo.get(i);
                    String rsrvstr1 = impuData.getString("RSRV_STR1");
                    if ("1".equals(rsrvstr1))
                    {
                        impuData.put("RSRV_STR5", "0");// 用于信控，标识为1是发了HSS,ENUM信息，为0则是发了后又取消
                        impuData.put("RSRV_STR4", "");
                        impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        dataset.add(impuData);
                    }
                    else
                    {
                        impuData.put("RSRV_STR4", "");
                        impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        dataset.add(impuData);
                    }
                }
                addTradeImpu(dataset);
            }
        }
    }

    /**
     * @description 处理台账Other子表的数据
     * @author yish
     * @date 2013-10-14
     */
    public void infoRegOtherData() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入DestroyDesktopTelGroupMember类 infoRegOtherData()>>>>>>>>>>>>>>>>>>");
        String user_id = reqData.getUca().getUserId();
        String product_id = reqData.getUca().getProductId();

        IDataset dataset = new DatasetList();
        if (delFlag)
        {
            // 1、 发CNTRX指令
            IData centreData = new DataMap();
            centreData.put("USER_ID", user_id);
            centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData.put("RSRV_VALUE", "多媒体桌面电话");
            centreData.put("RSRV_STR1", product_id);// 产品ID
            centreData.put("RSRV_STR9", "8171"); // 服务id

            if ("2".equals(power))
            {
                centreData.put("OPER_CODE", "27"); // 操作类型:注销管理员

                if (logger.isDebugEnabled())
                    logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 管理员注销操作   数据插入other表用于向centrex平台发注销管理员报文>>>>>>>>>>>>>>>>>>");
            }
            else
            {
                centreData.put("OPER_CODE", "02"); // 操作类型：注销成员

                if (logger.isDebugEnabled())
                    logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 成员注销操作   数据插入other表用于向centrex平台发注销成员报文>>>>>>>>>>>>>>>>>>");
            }

            centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            centreData.put("START_DATE", getAcceptTime());
            centreData.put("END_DATE", getAcceptTime()); // 立即截止
            centreData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData);
        }

        // 2、 判断是否需要发指令给HSS和ENUM
        if (/* "05".equals(netTypeCode) && */delFlag) // 固话号码，没有订购其他ims产品
        {
            if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入固话号码发送HSS、ENUM指令处理部分>>>>>>>>>>>>>>>>>>");

            IData hss = new DataMap();
            hss.put("USER_ID", user_id);
            hss.put("RSRV_VALUE_CODE", "HSS");// domain域
            hss.put("RSRV_VALUE", "多媒体桌面电话");
            hss.put("RSRV_STR1", product_id);// 产品ID
            hss.put("RSRV_STR9", "8172"); // 服务id
            hss.put("OPER_CODE", "02");// 用于服务开通操作码
            hss.put("RSRV_STR12", "");// HSS_SP_SIFC
            hss.put("RSRV_STR20", "");// HSS_SPIFC_TEMPLATE_ID

            hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            hss.put("START_DATE", getAcceptTime());
            hss.put("END_DATE", getAcceptTime()); // 立即截止
            hss.put("INST_ID", SeqMgr.getInstId());
            dataset.add(hss);

            if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 注销操作   数据插入other表用于向HSS平台发注销成员报文>>>>>>>>>>>>>>>>>>");

            IData enumData = new DataMap();
            enumData.put("USER_ID", user_id);
            enumData.put("RSRV_VALUE_CODE", "ENUM");// domain域
            enumData.put("RSRV_VALUE", "多媒体桌面电话");
            enumData.put("RSRV_STR1", product_id);// 产品ID

            enumData.put("RSRV_STR9", "8173"); // 服务id
            enumData.put("OPER_CODE", "02"); // 操作类型 02 删除

            enumData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            enumData.put("START_DATE", getAcceptTime());
            enumData.put("END_DATE", getAcceptTime()); // 立即截止
            enumData.put("INST_ID", SeqMgr.getInstId());
            dataset.add(enumData);

            if (logger.isDebugEnabled())
                logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 注销操作   数据插入other表用于向ENUM平台发注销成员报文>>>>>>>>>>>>>>>>>>");
        }

        // 3、 发CNTRX平台成员退订业务指令
        /*
         * IData data = new DataMap(); data.put("USER_ID", user_id); data.put("RSRV_VALUE_CODE", "CNTRX"); // domain域
         * data.put("RSRV_VALUE", "多媒体桌面电话成员CNTRX服务"); data.put("RSRV_STR1", product_id); // 产品ID data.put("RSRV_STR9",
         * "8171"); // 服务id data.put("OPER_CODE", "07"); // 操作类型 退订个人业务 data.put("MODIFY_TAG",
         * TRADE_MODIFY_TAG.DEL.getValue()); data.put("START_DATE", getAcceptTime()); data.put("END_DATE",
         * getAcceptTime()); // 立即截止 data.put("INST_ID", inst_id); dataset.add(data);
         */
        if("1".equals(colorFalg))
        {
        	 String userId = reqData.getUca().getUserId();
             String rsrvValueCode = "HSS";
             String rsrvValue = "10122824";
             IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValue(userId, rsrvValueCode, rsrvValue);
             if (IDataUtil.isNotEmpty(otherInfos))
             {
            	 IData otherInfo = otherInfos.getData(0);
            	 otherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            	 otherInfo.put("END_DATE", SysDateMgr.getSysTime());
            	 dataset.add(otherInfo);
             }
        }

        addTradeOther(dataset);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出DestroyDesktopTelGroupMember类 infoRegOtherData()<<<<<<<<<<<<<<<<<<<");
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DestroyDesktopTelGroupMemberReqData) getBaseReqData();
    }

    /**
     * @description 初始化构建reqData
     * @author yish
     * @date 2013-10-14
     */
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        String cust_id = reqData.getGrpUca().getCustId(); // 集团客户id
        String user_id_a = reqData.getGrpUca().getUserId(); // 集团用户id
        String user_id_b = reqData.getUca().getUserId(); // 成员用户id
        netTypeCode = reqData.getUca().getUser().getNetTypeCode(); // 网别类型

        String relaTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IData relationUserInfo = RelaUUInfoQry.getRelationUUByPk(user_id_a, user_id_b, relaTypeCode, null);
        if (IDataUtil.isEmpty(relationUserInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_235);
        }
        power = relationUserInfo.getString("ROLE_CODE_B");

        delFlag = GroupImsUtil.getDelMebFlag(cust_id, user_id_b); // 判断成员是否还有订购其他ims产品，如果没有则可发删除成员报文，返回true
        if (!"05".equals(netTypeCode))
        {
            userType = "1";// 1 : 传统移动用户
        }

        if (delFlag)
        {
            String tmp = GroupImsUtil.getImpuStr4(user_id_b, userType, 0);
            if (StringUtils.isNotBlank(tmp))
            {
                power = tmp; // 已订购ims业务的取impu表, RSRV_STR4 中的成员角色值
            }
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
        data.put("NET_TYPE_CODE", netTypeCode); // 网别
    }

    /**
     * @description 处理user表数据
     * @author yish
     * @date 2013-10-14
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        map.put("NET_TYPE_CODE", netTypeCode); // 网别
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
                            && modifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
                    {
                    	colorFalg = "1";
                    	break;
                    }
                }
    		}
    	}
    }

}
