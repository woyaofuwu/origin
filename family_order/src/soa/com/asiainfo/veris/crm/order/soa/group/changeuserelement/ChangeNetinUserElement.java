
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

/**
 * 互联网专线变更
 * 
 * @author lucifer
 */
public class ChangeNetinUserElement extends ChangeUserElement
{
    protected ChangeInternetUserElementReqData reqData = null;

    public ChangeNetinUserElement()
    {
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeInternetUserElementReqData();
    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeInternetUserElementReqData) getBaseReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setInterData(map.getData("ATTRINTERNET"));

        reqData.setDataline(map.getData("DATALINE"));

        reqData.setCommonData(map.getDataset("COMMON_DATA"));

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        String flag = "2" ;//2修改专线 ;0新增专线 ;1 删除专线 
        IDataset commonData = reqData.getCommonData();
        if(null != commonData && commonData.size() > 0){
            for (int i = 0; i < commonData.size(); i++)
            {
                IData sheetTypeData = commonData.getData(i);
                if("SHEETTYPE".equals(sheetTypeData.getString("ATTR_CODE"))){
                    if("32".equals(sheetTypeData.getString("ATTR_VALUE"))){
                        flag = "0"; //0新增专线
                    }else if("34".equals(sheetTypeData.getString("ATTR_VALUE"))){
                        flag = "1"; //1 删除专线 
                    }
                    
                }
            }
        }
        actTradeDataline(flag);
        actTradeDatalineAttr();
        infoRegVispDataOther(flag);
    }

    private void actTradeDatalineAttr() throws Exception
    {
        IData dataline = reqData.getDataline();
        IDataset attrDataList = reqData.getCommonData();
        IData internet = reqData.getInterData();
        IDataset dataset = new DatasetList();

        IData userData = new DataMap();
        userData.put("USER_ID", reqData.getUca().getUserId());
        userData.put("START_DATE", getAcceptTime());
        userData.put("SHEET_TYPE", "6");
        userData.put("BANDWIDTH", internet.getString("pam_NOTIN_LINE_BROADBAND"));

		dataset = DatalineUtil.addTradeUserDataLineAttr(attrDataList, dataline, userData);
        super.addTradeDataLineAttr(dataset);
    }

    private void actTradeDataline(String flag) throws Exception
    {
        IData dataline = reqData.getDataline();
        IData internet = reqData.getInterData();
        IData lineInfo = new DataMap();
        IDataset dataset = new DatasetList();
        //add by REQ201802260030 关于集客业务支撑流程式快速开通功能需求
        if(dataline!=null&&dataline.getString("LINEOPENTAG","").equals("1")){
        	dataline.put("RSRV_NUM1", dataline.getString("LINEOPENTAG",""));
        }
        if(dataline!=null&&!dataline.getString("IPTYPE","").equals("")){
        	dataline.put("RSRV_STR1", dataline.getString("IPTYPE",""));
        }
        if(dataline!=null&&!dataline.getString("CUSAPPSERVIPV4ADDNUM","").equals("")){
        	dataline.put("RSRV_NUM1", dataline.getString("CUSAPPSERVIPV4ADDNUM",""));
        }
        if(dataline!=null&&!dataline.getString("CUSAPPSERVIPV6ADDNUM","").equals("")){
        	dataline.put("RSRV_NUM2", dataline.getString("CUSAPPSERVIPV6ADDNUM",""));
        }
        if (null != dataline && dataline.size() > 0 && flag.equals("2"))
        {
            String productNo = dataline.getString("PRODUCTNO");

            // 查询专线信息
            IData inparam = new DataMap();
            inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
            inparam.put("SHEET_TYPE", "6");
            inparam.put("PRODUCT_NO", productNo);

            IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
            if (null != datalineList && datalineList.size() > 0)
            {
                lineInfo = datalineList.getData(0);
            }

            IData userData = new DataMap();
            userData.put("USER_ID", reqData.getUca().getUserId());
            userData.put("UPDATE_TIME", getAcceptTime());
            userData.put("SHEET_TYPE", "6");

            dataset = DatalineUtil.updateTradeUserDataline(dataline, lineInfo, internet, userData);
            
            super.addTradeDataLine(dataset);
            
        }else if (null != dataline && dataline.size() > 0 && flag.equals("1"))//modify by lim 拆机逻辑
        {
            String productNo = dataline.getString("PRODUCTNO");

            // 查询专线信息
            IData inparam = new DataMap();
            inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
            inparam.put("SHEET_TYPE", "6");
            inparam.put("PRODUCT_NO", productNo);

            IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
            if (null != datalineList && datalineList.size() > 0)
            {
                lineInfo = datalineList.getData(0);
                lineInfo.put("UPDATE_TIME", getAcceptTime());
                lineInfo.put("END_DATE", getAcceptTime());
                lineInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }

            super.addTradeDataLine(datalineList);
            
        }else if(flag.equals("0")){
            IData userData = new DataMap();
            userData.put("USER_ID", reqData.getUca().getUserId());
            userData.put("START_DATE", getAcceptTime());
            userData.put("SHEET_TYPE", "6");

            dataset = DatalineUtil.addTradeUserDataLine(dataline, internet, userData);

            super.addTradeDataLine(dataset);
        }
    }

    public void infoRegVispDataOther(String flag) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData internet = reqData.getInterData();
        IData dataline = reqData.getDataline();

        // 查询专线信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("RSRV_VALUE_CODE", "N001");
        IDataset userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        if (internet != null && internet.size() > 0 && null != userOther && userOther.size() > 0 && flag.equals("2"))
        {
            String numberCode = internet.getString("pam_NOTIN_PRODUCT_NUMBER");

            for (int i = 0; i < userOther.size(); i++)
            {
                IData vispUser = userOther.getData(i);
                IData newVispUser  = (IData) Clone.deepClone(vispUser);

                String lineNumberCode = vispUser.getString("RSRV_STR7");
                //变更时先将原数据END_DATE修改为当前时间
                if (numberCode.equals(lineNumberCode))
                {
                    vispUser.put("UPDATE_TIME", getAcceptTime());
                    vispUser.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                    vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataset.add(vispUser);
                }
                
                //新增一条END_DATE为2050-12-31的记录
                if (numberCode.equals(lineNumberCode))
                {
                    newVispUser.put("USER_ID", reqData.getUca().getUserId());
                    newVispUser.put("START_DATE", getAcceptTime());
                    newVispUser.put("UPDATE_TIME", getAcceptTime());
                    newVispUser.put("END_DATE", SysDateMgr.getTheLastTime());
                    newVispUser.put("RSRV_VALUE_CODE", "N001");
                    newVispUser.put("INST_ID", SeqMgr.getInstId());
                    newVispUser.put("IS_NEED_PF", "1");

                    if (null != dataline && dataline.size() > 0)
                    {
                        String changeMode = dataline.getString("CHANGEMODE");
                        if (StringUtils.isNotBlank(changeMode))
                        {
                            if ("停机".equals(changeMode))
                            {
                                newVispUser.put("END_DATE", getAcceptTime());
                            }
                            else if ("复机".equals(changeMode))
                            {
                                newVispUser.put("END_DATE", SysDateMgr.getTheLastTime());
                            }
                        }
                    }

                    newVispUser.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")) + 1);
                    // 专线
                    newVispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
                    // 专线带宽
                    //modify by fufn BUG20180116174029 esop扩容开通单台账
                    if(dataline!=null&&dataline.getString("BANDWIDTH")!=null){
                    	newVispUser.put("RSRV_STR2", dataline.getString("BANDWIDTH"));
                    }else{
                    	newVispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND", ""));
                    }
//                    newVispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
                    // 专线价格
                    newVispUser.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
                    // 安装调试费
                    newVispUser.put("RSRV_STR4", internet.getString("pam_NOTIN_INSTALLATION_COST"));
                    // 专线一次性通信服务费
                    newVispUser.put("RSRV_STR5", internet.getString("pam_NOTIN_ONE_COST"));
                    // IP地址使用费
                    newVispUser.put("RSRV_STR6", internet.getString("pam_NOTIN_IP_PRICE"));
                    // 业务标识
                    newVispUser.put("RSRV_STR7", internet.getString("pam_NOTIN_PRODUCT_NUMBER"));
                    //软件应用服务费（元）add by chenzg@20180620
                    newVispUser.put("RSRV_STR8", internet.getString("pam_NOTIN_SOFTWARE_PRICE"));	
                    // 专线实例号
                    newVispUser.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
                    //网络技术支持服务费（元） add by chenzg@20180620
                    newVispUser.put("RSRV_STR10", internet.getString("pam_NOTIN_NET_PRICE"));		

                    newVispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                    newVispUser.put("IS_NEED_PF", "1");

                    dataset.add(newVispUser);
                }
            }
            addTradeOther(dataset);
        }else if(internet != null && internet.size() > 0 && null != userOther && userOther.size() > 0 && flag.equals("1")){//modify by lim 移机处理

            String numberCode = internet.getString("pam_NOTIN_PRODUCT_NUMBER");

            for (int i = 0; i < userOther.size(); i++)
            {
                IData vispUser = userOther.getData(i);
                String lineNumberCode = vispUser.getString("RSRV_STR7");
                
                if (numberCode.equals(lineNumberCode))
                {
                    vispUser.put("UPDATE_TIME", getAcceptTime());
                    vispUser.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                    vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    
                    dataset.add(vispUser);
                }
                
                
            }
            
            addTradeOther(dataset);

        }
		else if(flag.equals("0")){
            //新增
            IData data = new DataMap();
            if (null != internet && internet.size() > 0)
            {
                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("RSRV_VALUE_CODE", "N001");
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("INST_ID", SeqMgr.getInstId()); 

                data.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")) + 1);
                // 专线
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
                // 专线带宽
                data.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
                // 专线价格
                data.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
                // 安装调试费
                data.put("RSRV_STR4", internet.getString("pam_NOTIN_INSTALLATION_COST"));
                // 专线一次性通信服务费
                data.put("RSRV_STR5", internet.getString("pam_NOTIN_ONE_COST"));
                // IP地址使用费
                data.put("RSRV_STR6", internet.getString("pam_NOTIN_IP_PRICE"));
                // 业务标识
                data.put("RSRV_STR7", internet.getString("pam_NOTIN_PRODUCT_NUMBER"));
                //软件应用服务费（元） add by chenzg@20180620
                data.put("RSRV_STR8", internet.getString("pam_NOTIN_SOFTWARE_PRICE"));	
                // 专线实例号
                data.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));
                //网络技术支持服务费（元） add by chenzg@20180620
                data.put("RSRV_STR10", internet.getString("pam_NOTIN_NET_PRICE"));		
                
                // 服务开通
                data.put("IS_NEED_PF", "1");

                dataset.add(data);

                addTradeOther(dataset);
            }
        }

    }

    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();

        IData map = bizData.getTrade();

        //CRM发起资料变更走开环
        if (null == reqData.getDataline() || null == reqData.getCommonData() || reqData.getDataline().size() < 1)
        {
            map.put("OLCOM_TAG", "0");
        }
        else
        {
            //ESOP发起变更根据PFWAIT处理，真正的变更闭环处理，开通转变更的根据ESOP传的PFWAIT处理
            int pfWait = 0;
            IDataset commonData = reqData.getCommonData();
            if(null != commonData && commonData.size() > 0){
                for (int i = 0; i < commonData.size(); i++)
                {
                    IData data = commonData.getData(i);
                    
                    if("PF_WAIT".equals(data.getString("ATTR_CODE")) && StringUtils.isNotBlank(data.getString("ATTR_CODE"))){
                        
                        pfWait = data.getInt("ATTR_VALUE");
                    }
                }
            }
            
            map.put("OLCOM_TAG","1");
            map.put("PF_WAIT", pfWait);

        }
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }
}
