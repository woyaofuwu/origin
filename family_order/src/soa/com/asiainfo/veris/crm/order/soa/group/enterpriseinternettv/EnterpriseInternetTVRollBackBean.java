
package com.asiainfo.veris.crm.order.soa.group.enterpriseinternettv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class EnterpriseInternetTVRollBackBean extends MemberBean
{
    private EnterpriseInternetTVReqData reqData = null;

    public EnterpriseInternetTVRollBackBean()
    {

    }


    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        infoRegDataOther();

        infoRegDataRes();
        
        updateModem();

    }
    
    /**
     * 生成Other表数据
     * 
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        String memUserId = reqData.getUserId();

        IDataset userOther = UserOtherInfoQry.getUserOther(memUserId, "EITV");
        if(IDataUtil.isNotEmpty(userOther))
        {
            IData data = (IData) userOther.get(0);

            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            data.put("END_DATE", getAcceptTime());
            dataset.add(data);
            this.addTradeOther(dataset);
            
        }
    }
    
    /**
     * 处理资源信息
     * 
     * @throws Exception
     */
    public void infoRegDataRes() throws Exception
    {
        IDataset dataset = new DatasetList();
        String memUserId = reqData.getUserId();
        IDataset userRes = UserResInfoQry.qrySetTopBoxByUserIdAndRsrv1AllColumns(memUserId, "4", "J");
        if(IDataUtil.isNotEmpty(userRes))
        {
            IData data = (IData) userRes.get(0);
            
            data.put("END_DATE", getAcceptTime());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataset.add(data);
            reqData.cd.putRes(dataset);
            this.addTradeRes(dataset);
        }
    }
     

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new EnterpriseInternetTVReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (EnterpriseInternetTVReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setSerialNumber(map.getString("cond_SERIAL_NUMBER",""));
        reqData.setUserId(map.getString("cond_USER_ID_A",""));
    }
    
    @Override
    protected void makUca(IData map) throws Exception
    {
        map.put("GRP_SERIAL_NUMBER", map.getString("cond_SERIAL_NUMBER",""));
        super.makUcaForMebNormal(map);
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }
    
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        String tradeTypeCode = "4301";
        return tradeTypeCode;
    }
    
    @Override
    protected String setOrderTypeCode() throws Exception
    {
        return "4301";
    }
    
    /**
     * @Function: updateModem()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void updateModem() throws Exception
    {   
        String userId = reqData.getUserId();
        IData boxInfo = UserResInfoQry.qrySetTopBoxByUserIdAndRsrv1AllColumns(userId, "4", "J").first();

        //测试时发现查询信息为空时，后面会报空指针
        if(IDataUtil.isEmpty(boxInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"查机顶盒信息为空！");
        }
        
      //先调用华为进行回退机顶盒
        IData returnParam=new DataMap();
        returnParam.put("RES_NO", boxInfo.getString("IMSI"));
        returnParam.put("PARA_VALUE1", "");
        returnParam.put("SALE_FEE", boxInfo.getString("RSRV_NUM5","0"));
        returnParam.put("PARA_VALUE7", "0");
        returnParam.put("DEVICE_COST", boxInfo.getString("RSRV_NUM4","0"));
        returnParam.put("X_CHOICE_TAG", "1");
        returnParam.put("RES_TYPE_CODE", "4");
        returnParam.put("PARA_VALUE11", boxInfo.getString("UPDATE_TIME"));
        returnParam.put("PARA_VALUE14", boxInfo.getString("RSRV_NUM5","0"));
        returnParam.put("PARA_VALUE17", boxInfo.getString("RSRV_NUM5","0"));
        returnParam.put("PARA_VALUE1", "");
        returnParam.put("USER_NAME", "");
        returnParam.put("STAFF_ID", boxInfo.getString("UPDATE_STAFF_ID"));
        returnParam.put("TRADE_ID", boxInfo.getString("INST_ID"));
        
        IDataset returnResult=HwTerminalCall.returnTopSetBoxTerminal(returnParam);
        if(IDataUtil.isEmpty(returnResult)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口报错！");
        }else{
            String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
            if(!resultCode.equals("0")){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用华为接口错误："+returnResult.
                        getData(0).getString("X_RESULTINFO",""));
            }
        }  
        
        
    } 
    
}
