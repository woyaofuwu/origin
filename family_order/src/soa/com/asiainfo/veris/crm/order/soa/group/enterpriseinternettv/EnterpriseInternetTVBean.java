
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class EnterpriseInternetTVBean extends MemberBean
{
    private EnterpriseInternetTVReqData reqData = null;

    public EnterpriseInternetTVBean()
    {

    }


    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        infoRegDataOther();

        infoRegDataRes();
        
        checkTerminal();
        
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
        IData other = new DataMap();
        
        IData map = new DataMap();
        map.put("SERIAL_NUMBER", reqData.getKdResId());
        map.put("REMOVE_TAG", "0");
        IDataset userinfo = qrySelAllBySn(map);
        if(IDataUtil.isNotEmpty(userinfo))
        {
            other.put("USER_ID", userinfo.getData(0).getString("USER_ID", ""));
        }else{
            CSAppException.apperr(GrpException.CRM_GRP_197, reqData.getKdResId());
        }
        
        other.put("INST_ID", SeqMgr.getInstId());
        
        other.put("RSRV_VALUE_CODE", "EITV");
        other.put("RSRV_VALUE", "1");// 申领标识
        
        other.put("RSRV_STR1", reqData.getKdSerialNumber());// 宽带号码
        other.put("RSRV_STR2", reqData.getKdResSupplyCoopId());// 终端供应商
        other.put("RSRV_STR3", reqData.getKdResId());// 终端编号
        other.put("RSRV_STR4", reqData.getKdAddr());// 宽带地址
        other.put("RSRV_STR5", reqData.getKdResBrandName());// 终端品牌
        other.put("RSRV_STR6", reqData.getKdResKindName());// 终端型号
        other.put("RSRV_STR7", reqData.getKdResFee());// 终端价格(元)
        other.put("RSRV_STR8", reqData.getCustName());// 宽带联系人
        other.put("RSRV_STR9", reqData.getKdResStateName());// 终端状态
        other.put("RSRV_STR10", reqData.getKdPhone());// 宽带联系电话
        other.put("RSRV_STR11", reqData.getKdUserId());// 宽带user_id
        other.put("RSRV_STR12", reqData.getKdRemark());// 宽带备注
        other.put("RSRV_STR13", reqData.getRemark());// 备注
        other.put("RSRV_STR14", reqData.getUserId());// // 集团user_ID
        
        other.put("RSRV_STR15", reqData.getKdResBrandCode());// 终端品牌编码
        other.put("RSRV_STR16", reqData.getKdResTypeCode());// 终端类型编码
        other.put("RSRV_STR17", reqData.getKdResKindCode());// 终端型号编码
        other.put("RSRV_STR18", reqData.getKdResStateCode());// 终端状态编码
        
        other.put("RSRV_TAG1", reqData.getKdArtificialServices());//上门标识
        
        
        other.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        other.put("START_DATE", getAcceptTime());
        other.put("END_DATE", SysDateMgr.getTheLastTime());
        other.put("REMARK", "企业互联网电视终端申领");
        dataset.add(other);
         
        this.addTradeOther(dataset);
    }
    
    public void infoRegDataRes() throws Exception
    {
        
        IDataset resDataset = new DatasetList();
        IData resData2 = new DataMap();
        
        IData map = new DataMap();
        map.put("SERIAL_NUMBER", reqData.getKdResId());
        map.put("REMOVE_TAG", "0");
        IDataset userinfo = qrySelAllBySn(map);
        if(IDataUtil.isNotEmpty(userinfo))
        {
            resData2.put("USER_ID", userinfo.getData(0).getString("USER_ID", ""));
        }else{
            CSAppException.apperr(GrpException.CRM_GRP_197, reqData.getKdResId());
        }       
        
        resData2.put("USER_ID_A", "-1");
        resData2.put("RES_TYPE_CODE", reqData.getKdResTypeCode());// 终端类型编码
        resData2.put("RES_CODE", reqData.getKdResKindCode());// 终端型号编码
        resData2.put("IMSI", reqData.getKdResId());// 终端编码
        resData2.put("KI", reqData.getKdResSupplyCoopId());// 厂商编码
        resData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        resData2.put("START_DATE", getAcceptTime());
        resData2.put("END_DATE", SysDateMgr.getTheLastTime());
        resData2.put("INST_ID", SeqMgr.getInstId());
        
        resData2.put("REMARK", "企业互联网电视终端申领");
        resData2.put("RSRV_NUM1", reqData.getKdArtificialServices());// 上门标记
        resData2.put("RSRV_STR2", "1");//新业务规则标识
        resData2.put("RSRV_STR3", reqData.getKdPhone());// 联系电话
        resData2.put("RSRV_STR4", reqData.getCustName());// 联系人
        resData2.put("RSRV_STR5", reqData.getKdAddr());// 宽带地址
        
        resData2.put("RSRV_STR1", "J");// 机顶盒标志
        resDataset.add(resData2);
        reqData.cd.putRes(resDataset);
        this.addTradeRes(resDataset);
        
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
        reqData.setSerialNumber(map.getString("SERIAL_NUMBER",""));
        reqData.setKdSerialNumber(map.getString("KD_NUMBER",""));// 宽带号码
        reqData.setKdResSupplyCoopId(map.getString("KD_RES_SUPPLY_COOPID",""));// 终端供应商
        reqData.setKdResId(map.getString("KD_RES_ID",""));// 终端编号
        reqData.setKdAddr(map.getString("KD_ADDR",""));// 宽带地址
        reqData.setKdResBrandName(map.getString("KD_RES_BRAND_NAME",""));// 终端品牌
        reqData.setKdResBrandCode(map.getString("KD_RES_BRAND_CODE",""));// 终端品牌编码
        reqData.setKdResTypeCode(map.getString("KD_RES_TYPE_CODE",""));// 终端类型编码
        reqData.setKdResKindName(map.getString("KD_RES_KIND_NAME",""));// 终端型号
        reqData.setKdResKindCode(map.getString("KD_RES_KIND_CODE",""));// 终端型号编码
        reqData.setKdArtificialServices(map.getString("KD_ARTIFICIAL_SERVICES",""));// 上门标识 
        reqData.setKdResFee(map.getString("KD_RES_FEE",""));// 终端价格(元)
        reqData.setKdDevictCost(map.getString("KD_DEVICE_COST",""));// 终端价格
        reqData.setCustName(map.getString("CUST_NAME",""));// 宽带联系人
        reqData.setKdResStateName(map.getString("KD_RES_STATE_NAME",""));// 终端状态
        reqData.setKdResStateCode(map.getString("KD_RES_STATE_CODE",""));// 终端状态编码
        reqData.setKdPhone(map.getString("KD_PHONE",""));  // 宽带联系电话
        reqData.setKdUserId(map.getString("KD_USERID",""));// 宽带user_id
        reqData.setKdRemark(map.getString("KD_REMARK",""));// 宽带备注
        reqData.setRemark(map.getString("REMARK",""));// 备注
        reqData.setUserId(map.getString("USER_ID",""));// 集团user_ID   
        reqData.setGPSerialNumber(map.getString("SERIAL_NUMBER",""));// 集团产品编码 
    }
    
    @Override
    protected void makUca(IData map) throws Exception
    {
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

    public static IDataset qryRelaBBInfoByRoleCodeBForGrp(IData param, Pagination pagination) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_BB", "SEL_BB_BY_USERID_A", param,pagination);
    }
    
    public static IDataset qrySelAllBySn(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER", "SEL_ALL_BY_SN", param);
    }
    
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        String tradeTypeCode = "4300";
        return tradeTypeCode;
    }
    
    /**
     * @Function: checkTerminal()
     * @Description: 终端校验
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-8-2 下午5:00:03 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-2 yxd v1.0.0 修改原因
     */
    public void checkTerminal() throws Exception
    {
        IData param=new DataMap();
        param.put("RES_NO", reqData.getKdResId());
        param.put("SERIAL_NUMBER",reqData.getGPSerialNumber());
        param.put("MONTH_PICK", "1");

        IDataset retDataset =HwTerminalCall.expandSetTopBoxOccupyTime(param);   
        
        if(IDataUtil.isEmpty(retDataset))
        {
            String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
        }
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
        IData param = new DataMap();
        param.put("RES_NO", reqData.getKdResId());//串号
        param.put("REMARK", "企业互联网电视终端申领");
        param.put("SALE_FEE", "0");//销售费用:不是销售传0
        param.put("PARA_VALUE1", reqData.getKdPhone());//购机用户的手机号码
        param.put("PARA_VALUE4", reqData.getKdUserId());
        param.put("PARA_VALUE7", "0");//代办费
        param.put("DEVICE_COST", reqData.getKdDevictCost());//进货价格--校验接口取
        param.put("TRADE_ID ",  getTradeId());//台账流水 
        param.put("X_CHOICE_TAG", "0");//0-终端销售,1—终端销售退货
        param.put("RES_TYPE_CODE", "4");//资源类型,终端的传入4
        param.put("CONTRACT_ID",  getTradeId());//销售订单号
        param.put("INFO_TAG", "1");
        param.put("PRODUCT_MODE", "0");
        param.put("X_RES_NO_S", reqData.getKdResId());
        param.put("X_RES_NO_E", reqData.getKdResId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("PARA_VALUE10", reqData.getKdResKindCode());
        param.put("PARA_VALUE11", sdf.format(new Timestamp(System.currentTimeMillis())));//销售时间
        param.put("PARA_VALUE12", CSBizBean.getVisit().getDepartId());//销售部门
        param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
        param.put("PARA_VALUE14",  reqData.getKdDevictCost());//裸机价格  从检验接口取裸机价格
        param.put("PARA_VALUE15", "0");//客户购机折让价格
        param.put("PARA_VALUE16", "0");
        param.put("PARA_VALUE17", "0");
        param.put("PARA_VALUE18", "0");//客户实缴费用总额  //如果没有合约，就和实际付款相等就可以。 
        param.put("PARA_VALUE9", "03");//客户捆绑合约类型 //合约类型：01—全网统一预存购机 02—全网统一购机赠费 03—预存购机 
        param.put("PARA_VALUE1", reqData.getKdPhone());//客户号码
        param.put("USER_NAME", reqData.getCustName());//客户姓名
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());//销售员工
        param.put("RES_TRADE_CODE", "IMobileDeviceModifyState");

        IDataset sysResults = HwTerminalCall.occupyTerminalByTerminalId(param);
        if(!StringUtils.equals(sysResults.first().getString("X_RESULTCODE"), "0")){//0为成功，其他失败
            String x_resultinfo=sysResults.first().getString("X_RESULTINFO");
            if(StringUtils.isNotBlank(sysResults.first().getString("X_RESULTINFO"))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,x_resultinfo);
            }
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"华为接口调用异常！");
        }
    } 
    
}
