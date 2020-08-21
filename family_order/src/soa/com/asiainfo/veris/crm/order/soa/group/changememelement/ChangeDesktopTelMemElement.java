
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeDesktopTelMemElementReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

/**
 * @description 多媒体桌面电话集团成员产品变更Bean类
 * @author yish
 * @date 2013-10-14
 */
public class ChangeDesktopTelMemElement extends ChangeMemElement
{
    private static transient Logger logger = Logger.getLogger(ChangeDesktopTelMemElement.class);

    protected boolean flag = true; // true为移动号；false为固号

    protected ChangeDesktopTelMemElementReqData reqData = null;
    
    private String colorFalg = "3";

    public ChangeDesktopTelMemElement()
    {

    }

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
        infoTradeSvc();
        infoRegOtherData();
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
        String xTag = data.getString("tag", "");
        String serialNumber = data.getString("SERIAL_NUMBER");
        String bizCode = data.getString("BIZ_CODE");
        if ("0".equals(xTag))
        {
            data.put("USER_ID", querySuperSnInfo(serialNumber));
            data.put("USER_TYPE_CODE", data.getString("USER_TYPE_CODE")); // 类型IW 白 IB黑
            data.put("EC_USER_ID", reqData.getUca().getUserId());
            data.put("BIZ_IN_CODE", bizCode); // 呼入呼出限制 1呼出 0 呼入
            data.put("SERIAL_NUMBER", serialNumber);
            data.put("GROUP_ID", reqData.getGrpUca().getCustGroup().getGroupId());
            data.put("BIZ_CODE", bizCode); // 呼入呼出限制 1呼出 0 呼入
            data.put("BIZ_NAME", "IMSBWLIST");
            data.put("REMARK", reqData.getRemark());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            data.put("SERVICE_ID", serviceId);
            data.put("INST_ID", SeqMgr.getInstId());
            data.put("EXPECT_TIME", getAcceptTime());
            data.put("START_DATE", getAcceptTime());
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("OPER_STATE", "03");
            addTradeBlackwhite(data);
        }
        else if ("1".equals(xTag))
        {
            IData param = new DataMap();
            param.put("EC_USER_ID", reqData.getUca().getUserId());
            param.put("SERVICE_ID", serviceId);
            IDataset bwList = UserBlackWhiteInfoQry.getBlackWhitedataByGSS(param);
            IDataset tempList = DataHelper.filter(bwList, "SERIAL_NUMBER=" + serialNumber + ",BIZ_CODE=" + bizCode);
            if (IDataUtil.isNotEmpty(tempList))
            {
                IData tempMap = tempList.getData(0);
                tempMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                tempMap.put("END_DATE", getAcceptTime());
                addTradeBlackwhite(data);
            }
        }
    }

    /**
     * 作用：写黑白名单表
     * 
     * @author lakenga
     * @param data
     * @throws Exception
     */
    public void addOtherShortDialSn(IData data) throws Exception
    {
        String xTag = data.getString("tag", "");
        String shortDialSn = data.getString("SHORT_NUMBER");
        String longNumber = data.getString("LONG_NUMBER");
        IData dataOther = new DataMap();
        if ("0".equals(xTag))
        {
            dataOther.put("USER_ID", reqData.getUca().getUserId());
            dataOther.put("RSRV_VALUE_CODE", "SHORTDIALSN");
            dataOther.put("RSRV_VALUE", shortDialSn);
            dataOther.put("RSRV_STR1", longNumber);
            dataOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            dataOther.put("START_DATE", getAcceptTime());
            dataOther.put("END_DATE", SysDateMgr.getTheLastTime());
            dataOther.put("INST_ID", SeqMgr.getInstId());
            addTradeOther(dataOther);
        }
        else if ("1".equals(xTag))
        {
            String userId = reqData.getUca().getUserId();
            String rsrvValueCode = "SHORTDIALSN";
            String eparchyCode = reqData.getUca().getUser().getEparchyCode();

            IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, rsrvValueCode, eparchyCode, null);
            IDataset tempList = DataHelper.filter(otherInfos, "RSRV_VALUE=" + shortDialSn + ",RSRV_STR1=" + longNumber);
            if (IDataUtil.isEmpty(tempList))
                return;
            for (int i = 0, tmpSize = tempList.size(); i < tmpSize; i++)
            {
                IData tempMap = tempList.getData(i);

                tempMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                tempMap.put("END_DATE", getAcceptTime());
                addTradeOther(tempMap);
            }
        }
    }

    /**
     * 作用：取消息服务时，删除黑白名单
     * 
     * @param serviceId
     * @throws Exception
     */
    public void delBlackWhite(String serviceId) throws Exception
    {
        String user_id = reqData.getUca().getUserId();
        IData param = new DataMap();
        param.put("EC_USER_ID", user_id);
        param.put("SERVICE_ID", serviceId);
        IDataset tempList = UserBlackWhiteInfoQry.getBlackWhitedataByGSS(param);
        if (IDataUtil.isEmpty(tempList))
            return;
        for (int i = 0, tmpSize = tempList.size(); i < tmpSize; i++)
        {
            IData tempMap = tempList.getData(i);
            tempMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            tempMap.put("END_DATE", getAcceptTime());
            addTradeBlackwhite(tempMap);
        }
    }

    /**
     * 作用：取消息服务时，删除缩位拨号
     * 
     * @param serviceId
     * @throws Exception
     */
    public void delOtherShortDialSn() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String rsrvValueCode = "SHORTDIALSN";
        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(userId, rsrvValueCode, eparchyCode, null);
        if (IDataUtil.isEmpty(otherInfos))
            return;

        for (int i = 0, otherSize = otherInfos.size(); i < otherSize; i++)
        {
            IData tempMap = otherInfos.getData(i);
            tempMap.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            tempMap.put("END_DATE", getAcceptTime());
            addTradeOther(tempMap);
        }
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeDesktopTelMemElementReqData();
    }

    /**
     * @description 处理台账Other子表的数据
     * @author yish
     * @date 2013-10-14
     */
    public void infoRegOtherData() throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>> 进入ChangeDesktopTelMemElement类 infoRegOtherData()>>>>>>>>>>>>>>>>>>");

        String operCode = "08";
        String roleCodeB = reqData.getPower();
        String desc = "成员修改";
        if ("2".equals(roleCodeB))
        {
            operCode = "28";
            desc = "成员(管理员)修改";
        }
        setRegTradeOther("8171", operCode, TRADE_MODIFY_TAG.MODI.getValue(), "CNTRX", desc); // 发送成员创建（管理员创建）报文
        setRegTradeOther("8171", "03", TRADE_MODIFY_TAG.MODI.getValue(), "CNTRX", "多媒体成员业务配置"); // 发CNTRX平台成员配置业务指令
        if (!flag)
        {
            // setRegTradeOther("8172", "08", TRADE_MODIFY_TAG.MODI.getValue(), "HSS", "HSS用户修改"); //创建HSS用户报文
        	
        	//发HSS的报文,改为需要订购、删除彩铃的时候要发送
        	/**
            IDataset dataset = new DatasetList();
            IData hss = new DataMap();
            hss.put("USER_ID", reqData.getUca().getUserId());
            hss.put("RSRV_VALUE_CODE", "HSS");// domain域
            hss.put("RSRV_VALUE", "多媒体桌面电话");
            hss.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            hss.put("RSRV_STR9", "8172"); // 服务id
            hss.put("OPER_CODE", "08"); // 操作类型

            hss.put("RSRV_STR12", "1350");// HSS_SP_SIFC
            hss.put("RSRV_STR20", "101");// HSS_SPIFC_TEMPLATE_ID

            hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            hss.put("START_DATE", getAcceptTime());
            hss.put("END_DATE", SysDateMgr.getTheLastTime());
            hss.put("INST_ID", SeqMgr.getInstId());
            dataset.add(hss);

            addTradeOther(dataset);
            */
            
            if("0".equals(colorFalg))
            {
            	setColorTradeOther(); //新增多媒体彩铃时送HSS
            	setColorTradeOtherForPf();
            }
            else if("1".equals(colorFalg))
            {
            	updateColorTradeOther();//删除多媒体彩铃时送HSS
            	setColorTradeOtherForPf();
            }

        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出ChangeDesktopTelMemElement类 infoRegOtherData()<<<<<<<<<<<<<<<<<<<");
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeDesktopTelMemElementReqData) getBaseReqData();
    }

    /**
     * @description 初始化构建reqData
     * @author yish
     * @date 2013-10-14
     */
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        String netTypeCode = reqData.getUca().getUser().getNetTypeCode();
        reqData.setNetTypeCode(netTypeCode);

        reqData.setPower(map.getString("MEM_ROLE_B", "1"));
        if ("2".equals(reqData.getPower()))
        {
            reqData.setCntrxMembPoer("0");
        }
        else
        {
            reqData.setCntrxMembPoer("5");
        }

        if ("05".equals(netTypeCode))
        {
            flag = false; // 固话
        }
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
        IDataset userInfos = UserGrpInfoQry.qryTabSqlFromAllDb(serialNumber, "0");
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
        data.put("NET_TYPE_CODE", reqData.getNetTypeCode()); // 网别
        data.put("RSRV_STR2", reqData.getCntrxMembPoer());
    }

    /**
     * 作用：处理黑白名单服务中的黑白名单列表
     * 
     * @author yish
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
            String state = tempMap.getString("MODIFY_TAG", "");
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(state))
            {
                if (serviceId.equals("10122821"))
                {
                    delOtherShortDialSn();
                }
                else
                {
                    delBlackWhite(serviceId);
                }
                continue;
            }
            IDataset blackList = tempMap.getDataset("BLACK_LIST");
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
     * @author yish
     * @param serviceId
     * @param operCode
     * @param modifyTag
     * @throws Exception
     */
    public void setRegTradeOther(String serviceId, String operCode, String modifyTag, String valueCode, String rsrvValue) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();
        centreData.put("USER_ID", reqData.getUca().getUserId());
        centreData.put("RSRV_VALUE_CODE", valueCode); // domain域
        centreData.put("RSRV_VALUE", rsrvValue);
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", serviceId); // 服务id
        centreData.put("OPER_CODE", operCode); // 操作类型
        centreData.put("MODIFY_TAG", modifyTag);
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);
        addTradeOther(dataset);
    }

    /**
     * @description 处理user表数据
     * @author yish
     * @date 2013-10-14
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        map.put("NET_TYPE_CODE", reqData.getNetTypeCode()); // 网别
        map.put("RSRV_STR2", reqData.getCntrxMembPoer());
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
                    	colorFalg = "0";
                    	break;
                    } 
                    else if("10122824".equals(elementId) && "S".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
                    {
                    	colorFalg = "1";
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
     * 
     * @throws Exception
     */
    private void updateColorTradeOther() throws Exception
    {
    	IDataset dataset = new DatasetList();
    	 String userId = reqData.getUca().getUserId();
         String rsrvValueCode = "HSS";
         String rsrvValue = "10122824";
         IDataset otherInfos = UserOtherInfoQry.getUserOtherByUserRsrvValue(userId, rsrvValueCode, rsrvValue);
         if (IDataUtil.isNotEmpty(otherInfos))
         {
        	 for(int i=0; i < otherInfos.size(); i++)
     		{
     			IData otherInfo = otherInfos.getData(i);
     			otherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
     			otherInfo.put("END_DATE", SysDateMgr.getSysTime());
     			otherInfo.put("RSRV_STR12", "");// HSS_SP_SIFC
     			otherInfo.put("RSRV_STR20", "");// HSS_SPIFC_TEMPLATE_ID
     			dataset.add(otherInfo);
     		}
        	 
         }
         addTradeOther(dataset);
    }

    private void setColorTradeOtherForPf() throws Exception 
    {
    	IDataset dataset = new DatasetList();
        IData hss = new DataMap();
        hss.put("USER_ID", reqData.getUca().getUserId());
        hss.put("RSRV_VALUE_CODE", "HSS");// domain域
        hss.put("RSRV_VALUE", "多媒体桌面电话");
        hss.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

        hss.put("RSRV_STR9", "8172"); // 服务id
        hss.put("OPER_CODE", "08"); // 操作类型

        hss.put("RSRV_STR12", "1350");// HSS_SP_SIFC
        hss.put("RSRV_STR20", "101");// HSS_SPIFC_TEMPLATE_ID

        hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        hss.put("START_DATE", getAcceptTime());
        hss.put("END_DATE", SysDateMgr.getTheLastTime());
        hss.put("INST_ID", SeqMgr.getInstId());
        dataset.add(hss);

        addTradeOther(dataset);
    }
    
}
