package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.adcmas.DealAdcMasComm;

public class GrpAdcMasIntf extends CSBizService
{
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(GrpAdcMasIntf.class);
    private static final String SERIAL_NUMBER_B = "SERIAL_NUMBER_B";
    private static final String SERIAL_NUMBER_A = "SERIAL_NUMBER_A";
    private static final String X_RESULTINFO = "X_RESULTINFO";
    private static final String X_RESULTCODE = "X_RESULTCODE";
    private static final String ORDER_ID = "ORDER_ID";
    private static final String STATUSDESC = "STATUSDESC";
    private static final String SUBSTATUS = "SUBSTATUS";

    /*
     * @description AdcMas成员业务反向接口
     * @author liaolc
     * @date 2014-07-08
     */
    public static IDataset dealAdcMasMemBiz(IData data) throws Exception
    {
        if (log.isDebugEnabled())
        {
            log.debug("==============ADCMAS反向接口入参=================="+data);
        }
        // 1.初始化处理接口传入的数据
        dealIntfData(data);

        // 2.根据成员操作类型调用不同的服务进行处理
        DealAdcMasComm dealAdcMasComm = new DealAdcMasComm();
        IDataset callSvcRst = new DatasetList();
        IDataset resultSet = new DatasetList();
        IData resultData = new DataMap();
        
        // 学护卡平台反向订购业务办理工号处理
        dealXhkStaffId(data);

        // 3.获取结果数据
        try
        {
            IData dealResult = dealAdcMasComm.dealAdcMasCommData(data);
            // 获取成员操作类型
            String bizCtrlType = dealResult.getString("BIZ_CTRL_TYPE", "");
            Boolean isOutNet = dealResult.getBoolean("IS_OUT_NET");

            if (isOutNet)
            {// 网外号码处理
                callSvcRst = CSAppCall.call("SS.MgrBlackWhiteOutSVC.crtTrade", dealResult);
            }
            else
            {
                if (BizCtrlType.CreateMember.equals(bizCtrlType))
                {// 成员新增
                    callSvcRst = CSAppCall.call("CS.CreateGroupMemberSvc.createGroupMember", dealResult);
                }
                else if (BizCtrlType.DestoryMember.equals(bizCtrlType))
                {// 成员删除
                    callSvcRst = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", dealResult);
                }
                else if (BizCtrlType.ChangeMemberDis.equals(bizCtrlType))
                {// 成员变更
                    callSvcRst = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", dealResult);
                }
            }

        }
        catch (Exception e)
        {
            IData resErrInfo = dealErrItfCode(e);
            resultData.put("STATUS", "1");
            resultData.put(SUBSTATUS, resErrInfo.getString(SUBSTATUS, "99"));
            resultData.put(STATUSDESC, resErrInfo.getString(STATUSDESC, "其它错误"));
            resultData.put("MODIFY_TAG", "1");
            resultSet.add(resultData);
            return resultSet;
        }
        resultData.put("STATUS", "2");
        resultData.put(SUBSTATUS, "00");
        resultData.put(STATUSDESC, "TradeOK!");
        resultData.put("MODIFY_TAG", "1");
        resultData.put(ORDER_ID, callSvcRst.getData(0).getString(ORDER_ID));
        resultSet.add(resultData);
        return resultSet;
    }

    /*
     * @description 成员个性化参数
     * @author liaolc
     * @date 2014/05/30
     */
    public static IDataset xfkFamNumSynAttr(IData data) throws Exception
    {
        // 返回结果
        IDataset resultInfos = new DatasetList();
        IData result = new DataMap();
        result.put(X_RESULTINFO, "OK");
        result.put(X_RESULTCODE, "0");
        try
        {
            IDataset tradeResult = DealAdcMasComm.xfkFamNumSynAttr(data);
            result.put(ORDER_ID, tradeResult.getData(0).getString(ORDER_ID));
            resultInfos.add(result);
            return resultInfos;
        }
        catch (Exception e)
        {
            result.put("X_LAST_RESULTINFO", e.getMessage());
            result.put(X_RESULTINFO, e.getMessage());
            result.put(X_RESULTCODE, "99");

            resultInfos.add(result);
            return resultInfos;
        }
    }

    /**
     * 异常信息转换
     *
     * @param e
     * @return
     * @throws Exception
     */
    public static IData dealErrItfCode(Exception e) throws Exception
    {
        IData resErrData = new DataMap();
        String substatus = "";
        String statusdesc = "";

        Throwable u = Utility.getBottomException(e);// 取有用的异常信息
        int leng = 512;
        StringWriter sw = new StringWriter();
        u.printStackTrace(new PrintWriter(sw));
        String errStr = sw.toString();
        int errLong = (int) errStr.length();
        if (errLong < 512)
        {
            leng = errLong;
        }

        String errInfo = Utility.parseExceptionMessage(e);

        boolean isNull = errInfo.contains("●");// 处理空指针异常情况
        String[] errInfos = errInfo.split("●");

        if (isNull && !"-1".equals(errInfos[0]))// 如果把空指针异常或在td_s_erritfcode表里没有配置的异常走else
        {
            substatus = errInfos[0];
            statusdesc = errInfos[1];
        }
        else
        {
            substatus = "99";
            statusdesc = errStr.substring(0, leng);
        }
        resErrData.put(SUBSTATUS, substatus);
        resErrData.put(STATUSDESC, statusdesc);
        return resErrData;
    }

    /**
     * 初始化处理接口传入的数据
     *
     * @param data
     * @return
     * @throws Exception
     */
    private static void dealIntfData(IData data) throws Exception
    {
        BizVisit visit = CSBizBean.getVisit();
        String directTion = data.getString("DIRECTION", "");// 业务方向
        if ("PADC".equals(directTion)||"PXXT".equals(directTion) || "PXHK".equals(directTion)
                || "PDCX".equals(directTion)|| "PLKF".equals(directTion)
                || "PTXL".equals(directTion)|| "PYKT".equals(directTion)
                || "PQJZ".equals(directTion)|| "PQYX".equals(directTion))    //PXXT 校讯通
        {
            visit.setInModeCode("P");// ADC平台方向过来
        }
        if ("IAGW".equals(directTion))
        {
            visit.setInModeCode("G");// 网关方向过来
        }
        
        String tradeStaffId = data.getString("TRADE_STAFF_ID", "");
        if ("PXHK".equals(directTion) && !"".equals(tradeStaffId)) {
            
            visit.setStaffId(tradeStaffId);
        }
    }
    
    /**
     * 初始化处理接口传入的数据
     *
     * @param data
     * @return
     * @throws Exception
     */
    private static void dealXhkStaffId(IData data) throws Exception
    {
        BizVisit visit = CSBizBean.getVisit();
        String directTion = data.getString("DIRECTION", "");// 业务方向
        
        String tradeStaffId = data.getString("TRADE_STAFF_ID", "");
        if ("PXHK".equals(directTion) && !"".equals(tradeStaffId)) {
            IDataset staffs = StaffInfoQry.qryStaffInfoByStaffId(tradeStaffId); 
            if(IDataUtil.isNotEmpty(staffs)){
                String cityCode = staffs.getData(0).getString("CITY_CODE","");
                String departId = staffs.getData(0).getString("DEPART_ID","");
                String eparchyCode = staffs.getData(0).getString("EPARCHY_CODE","");
                visit.setStaffId(tradeStaffId);
                visit.setDepartId(departId);
                visit.setCityCode(cityCode);
                visit.setStaffEparchyCode(eparchyCode);
            }
        }
    }

    /**
     * 新校讯通0000查询
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset qryXXTMembersBySerialnumber(IData data) throws Exception
    {
        // 返回结果
        IDataset result = new DatasetList();
        IData info = new DataMap();
        IData preRst = new DataMap();
        IData nowRst = new DataMap();

        try
        {
            result = CSAppCall.call("CS.RelaXXTInfoQrySVC.qryMemInfoBySNForUIP", data);
            
            for (int i = 1; i < result.size(); i++)
            {
                preRst = result.getData(i - 1);
                nowRst = result.getData(i);
                
                if (nowRst.getString(SERIAL_NUMBER_A).equals(preRst.getString(SERIAL_NUMBER_A))
                        && nowRst.getString(SERIAL_NUMBER_B).equals(preRst.getString(SERIAL_NUMBER_B))
                        && nowRst.getString("RSRV_STR1").equals(preRst.getString("RSRV_STR1")))
                {
                    result.remove(i);
                    i = i - 1;
                }
            }
            
            for (int i = 0; i < result.size(); i++)
            {
            	IData map = result.getData(i);
            	
            	String billflg = "";
            	String price = "";
            	IDataset discntStrs4 = UpcCall.queryTempChaByOfferTableField("D", map.getString("ELEMENT_ID"), "TD_B_DISCNT", "RSRV_STR4");
            	if(IDataUtil.isNotEmpty(discntStrs4))
            	{
            		billflg = discntStrs4.getData(0).getString("FIELD_VALUE");
            	}
            	IDataset discntStrs5 = UpcCall.queryTempChaByOfferTableField("D", map.getString("ELEMENT_ID"), "TD_B_DISCNT", "RSRV_STR5");
            	if(IDataUtil.isNotEmpty(discntStrs5))
            	{
            		price = discntStrs5.getData(0).getString("FIELD_VALUE");
            	}
            	map.put("DISCNT_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(map.getString("ELEMENT_ID")));
            	map.put("BILLFLG", billflg);
            	map.put("PRICE", price);
            }
        }
        catch (Exception e)
        {
            info.put("X_LAST_RESULTINFO", e.getMessage());
            info.put(X_RESULTINFO, e.getMessage());
            info.put(X_RESULTCODE, "99");
            result.add(info);
        }
        return result;
    }

    /**
     * 新校讯通0000查询退订接口
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset destoryXXTMembersByElementId(IData data) throws Exception
    {
        IData info = null;
        IDataset resultInfos = new DatasetList();
        IDataset callSvcRst = new DatasetList();
        try {
            IData splitData = DealAdcMasComm.splitInParamUserSerialNumA(data);
            Iterator iterator = splitData.keySet().iterator();

            IDataset serialNumberAInfos = null;
            IData serialNumAInfo = null;
            IData param = null;
            while (iterator.hasNext())
            {
                Object datakey = iterator.next();
                serialNumberAInfos = splitData.getDataset(String.valueOf(datakey));
                param = DealAdcMasComm.dealXxtCommDataFromUIP(serialNumberAInfos, data);
                String cancel = param.getString("ALL_ALL_CANCEL");

                try
                {
                    if ("true".equals(cancel))
                    {// 成员删除
                        callSvcRst = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", param);
                    }
                    else
                    {// 成员变更
                        callSvcRst = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", param);
                    }
                }
                catch (Exception e)
                {
                    if (serialNumberAInfos != null && serialNumberAInfos.size() > 0)
                    {
                        for (int i = 0; i < serialNumberAInfos.size(); i++)
                        {
                            serialNumAInfo = serialNumberAInfos.getData(i);
                            info = new DataMap();
                            info.put(X_RESULTINFO, e.getMessage());
                            info.put(X_RESULTCODE, "99");
                            info.put("X_ERR_CODE", "-1");
                            info.put("X_ERR_INFO", e.getMessage());
                            info.put("X_ERR_ID", serialNumAInfo.getString(SERIAL_NUMBER_A) + "_" + serialNumAInfo.getString(SERIAL_NUMBER_B) + "_" + serialNumAInfo.getString("ELEMENT_ID"));
                            resultInfos.add(info);
                        }
                    }
                    continue;
                }

                for (int i = 0; i < serialNumberAInfos.size(); i++)
                {
                    serialNumAInfo = serialNumberAInfos.getData(i);
                    info = new DataMap();
                    info.put(X_RESULTINFO, "OK");
                    info.put(X_RESULTCODE, "0");
                    info.put("X_ERR_CODE", "0");
                    info.put("X_ERR_INFO", "退订成功！");
                    info.put("X_ERR_ID", serialNumAInfo.getString(SERIAL_NUMBER_A) + "_" + serialNumAInfo.getString(SERIAL_NUMBER_B) + "_" + serialNumAInfo.getString("ELEMENT_ID"));
                    resultInfos.add(info);
                }
            }

            
        } catch (Exception ex) {
            log.error(ex.getMessage());
            CSAppException.apperr(CrmCommException.CRM_COMM_103, ex.getMessage()); 
        }
        return resultInfos;
    }
}


