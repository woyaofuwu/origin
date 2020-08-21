
package com.asiainfo.veris.crm.order.soa.group.imsmanage.blackwhiteuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 作用：IMS中集团级黑白名单处理
 * 
 * @author
 * @date 2009-6-8
 */
public class ImsBlackWhiteMemberBean extends MemberBean
{

    boolean opbool = true; // 判断是否只有删除操作,如果为true 则发删除报文，否则发变更报文

    protected ImsBlackWhiteMemberBeanReqData reqData = null;

    /**
     * 生成台帐表其它数据（拼台帐前）
     * 
     * @author tengg
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        regBlackWhite();

        infoRegDataCentreOther(); // 写OTHER表，发报文
    }

    /**
     * 作用：新增BlackWhite表中的记录
     * 
     * @param data
     * @throws Exception
     */
    public void addBlackWhite(IData data, String xTag) throws Exception
    {
        data.put("RSRV_STR1", "1"); // 成员

        if ("2".equals(xTag))
        {
            data.put("USER_ID", data.getString("USER_ID"));
        }
        else
        {
            data.put("USER_ID", reqData.getMem_user_id());
        }
        data.put("USER_TYPE_CODE", reqData.getUser_type_code()); // 类型IW 白 IB黑
        data.put("EC_USER_ID", reqData.getUca().getUserId());
        data.put("BIZ_IN_CODE", data.getString("BIZ_CODE")); // 呼入呼出限制 1呼出 0 呼入
        data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        // data.put("GROUP_ID", ""); // 成员业务 group_id置为空
        data.put("BIZ_CODE", data.getString("BIZ_CODE")); // 呼入呼出限制 1呼出 0 呼入
        data.put("BIZ_NAME", "IMSGROUPBWLIST");
        data.put("INST_ID", SeqMgr.getInstId());

        data.put("REMARK", reqData.getRemark());
        data.put("MODIFY_TAG", xTag); // luoyong 2011-1-11 "0"->xTag

        data.put("SERVICE_ID", "8171"); // 服务id 成员

        data.put("EXPECT_TIME", getAcceptTime());
        if ("2".equals(xTag))
        {
            data.put("START_DATE", data.getString("START_DATE").substring(0, 19));
        }
        else
        {
            data.put("START_DATE", getAcceptTime());
        }
        data.put("END_DATE", SysDateMgr.getTheLastTime());

        addTradeBlackwhite(data);
    }

    /**
     * 作用：校难是否可以操作
     * 
     * @param operCode
     *            0新增 1删除 2修改
     * @param ecUserId
     *            集团IMS用户
     * @param serialNumber
     *            新增服务代码
     * @throws Exception
     */
    public IData checkBlackWhiteForEcuserId(String operCode, IData data) throws Exception
    {
        String ecUserId = reqData.getGrp_user_id();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String imsBwAttr = reqData.getUser_type_code();
        String imsCallAttr = data.getString("BIZ_CODE", "");
        IData param = new DataMap();
        param.put("EC_USER_ID", ecUserId);
        // param.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode());
        param.put("SERVICE_ID", "8171"); // 服务id 成员

        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_TYPE_CODE", imsBwAttr);
        param.put("BIZ_CODE", imsCallAttr);

        IDataset indatas = UserBlackWhiteInfoQry.getBlackWhitedataByGSS(param);
        if ("0".equals(operCode))
        {
            if (IDataUtil.isNotEmpty(indatas))
            {
                CSAppException.apperr(CustException.CRM_CUST_79, serialNumber);
            }
        }
        else if ("1".equals(operCode))
        {
            if (IDataUtil.isEmpty(indatas))
            {
                CSAppException.apperr(CustException.CRM_CUST_68, serialNumber);
            }
            else
            {
                return indatas.getData(0);
            }

        }
        else if ("2".equals(operCode))
        {
            return indatas.getData(0);
        }
        return null;
    }

    /**
     * 作用：删除BlackWhite表中的记录
     * 
     * @param data
     * @throws Exception
     */
    public void deleteBlackWhite(IData data) throws Exception
    {
        data.put("END_DATE", getAcceptTime());

        data.put("START_DATE", data.getString("START_DATE").substring(0, 19));
        data.put("EXPECT_TIME", "".equals(data.getString("EXPECT_TIME", "")) ? "" : data.getString("EXPECT_TIME", "").substring(0, 19));
        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

        addTradeBlackwhite(data);
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new ImsBlackWhiteMemberBeanReqData();
    }

    /**
     * 覆写父类方法
     */
    protected void getTradeAfterElementData(IData ruleParam, IData tradeAllData, IData tableDataClone) throws Exception
    {

    }

    /**
     * 作用：写other表，决定是否发报文
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        IDataset dataset = new DatasetList();

        // 发送集团配置业务指令
        IData data = new DataMap();

        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        data.put("RSRV_VALUE", "多媒体桌面电话集团配置业务");
        data.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

        data.put("RSRV_STR9", "8171"); // 服务id 成员
        data.put("OPER_CODE", "03"); // 操作类型
        data.put("START_DATE", getAcceptTime());
        if (opbool)
        { // 删除所有号码则opretionType=1，发删除报文
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            data.put("END_DATE", getAcceptTime()); // 立即截止
        }
        else
        {
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            data.put("END_DATE", SysDateMgr.getTheLastTime());
        }

        data.put("INST_ID", SeqMgr.getInstId());
        dataset.add(data);
        addTradeOther(dataset);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ImsBlackWhiteMemberBeanReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setBW_LISTS(map.getDataset("BW_LISTS"));
        reqData.setUser_type(map.getString("USER_TYPE"));
        reqData.setGrp_user_id(map.getString("GRP_USER_ID"));
        reqData.setUser_type_code(map.getString("USER_TYPE_CODE"));
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForMebOnly(map);
    }

    /**
     * 作用:根据serialNumber 获用户信息,获取不到就生成USER_ID
     * 
     * @author
     * @param superSerialNumber
     * @throws Exception
     */
    public void querySuperSnInfo(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("REMOVE_TAG", "0");
        IDataset userInfos = UserGrpInfoQry.qryTabSqlFromAllDb(serialNumber, "0"); // 0 remove_tag
        if (IDataUtil.isEmpty(userInfos))
        {
            String userid = SeqMgr.getUserId();
            reqData.setMem_user_id(userid);
        }
        else
        {
            reqData.setMem_user_id(userInfos.getData(0).getString("USER_ID"));
        }
    }

    /**
     * 登记黑名单
     * 
     * @author
     * @date 2009-5-19
     * @throws Exception
     */
    public void regBlackWhite() throws Exception
    {
        IDataset bwDataLists = reqData.getBW_LISTS();
        if (IDataUtil.isEmpty(bwDataLists))
        {
            CSAppException.apperr(CustException.CRM_CUST_193, "黑白名单数据为空!");
        }
        for (int i = 0; i < bwDataLists.size(); i++)
        {
            IData tempInfo = bwDataLists.getData(i);
            String xTag = tempInfo.getString("tag", ""); // 0 新增 1 删除 2 修改
            String serialNumber = tempInfo.getString("SERIAL_NUMBER", "");
            IData bwInfo = checkBlackWhiteForEcuserId(xTag, tempInfo);
            if (IDataUtil.isEmpty(bwInfo))
            {
                opbool = false;
                querySuperSnInfo(serialNumber);
                addBlackWhite(tempInfo, xTag);
            }
            else if ("2".equals(xTag))
            {
                opbool = false;
                addBlackWhite(bwInfo, xTag);
            }
            else if ("1".equals(xTag))
            {
                deleteBlackWhite(bwInfo);
            }
        }
    }

    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_ID", reqData.getUca().getUser().getCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustPerson().getCustName()); // 客户名称
        data.put("USER_ID", reqData.getUca().getUserId()); // 用户标识
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识

        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); // 服务号码

        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识
        data.put("BRAND_CODE", reqData.getUca().getBrandCode()); // 品牌编码

        data.put("RSRV_STR1", reqData.getUser_type_code());
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "2998"; // 成员黑白名单
    }

    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR1", reqData.getUser_type_code());
    }
}
