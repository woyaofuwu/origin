
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.gfffmanage.GfffUserMaxGprsSetQry;

/**
 * 集团流量自由充成员产品、指定用户，限量统付（流量池）
 * @author think
 *
 */
public class CreateGfffGroupLimitaionMember extends CreateGroupMember
{
    private IData paramData = new DataMap();
    private String smsFlag = "";
    private String sendForSms = "";
    private String sendNoticeContent = "";
    
    public CreateGfffGroupLimitaionMember()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();
        
        paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            smsFlag = paramData.getString("NOTIN_SMS_FLAG");
            if(StringUtils.isNotBlank(smsFlag) && "1".equals(smsFlag)){
                sendForSms = paramData.getString("NOTIN_sendForSms");
                if(StringUtils.isNotBlank(sendForSms) && "1".equals(sendForSms)){//编辑短信内容
                    reqData.setNeedSms(false); 
                    sendNoticeContent = paramData.getString("NOTIN_SmsInfo");
                } else {
                    reqData.setNeedSms(true);
                }
            } else {
                reqData.setNeedSms(false);
            }
            String endDate = paramData.getString("NOTIN_PAY_END_DATE");
            if(StringUtils.isNotBlank(endDate)){
                paramData.put("NOTIN_PAY_END_DATE",endDate + " 23:59:59");
            }
        }
    }

    private IData getParamData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoMebUserUU();
        infoMebCenPay();
        infoMebPayPlan();
        infoMebProduct();
        
      //手工编辑短信内容
        if(StringUtils.isNotBlank(smsFlag) && "1".equals(smsFlag) &&
                StringUtils.isNotBlank(sendForSms) && "1".equals(sendForSms)){
            if(StringUtils.isNotBlank(sendNoticeContent)){
                infoRegDataSms(); 
            }
        }
    }

    /**
     * 修改uu关系的结束时间为界面选择的统付时间
       * @throws Exception
     */
    private void infoMebUserUU() throws Exception
    {
        if (IDataUtil.isNotEmpty(paramData))
        {
            String endDate = paramData.getString("NOTIN_PAY_END_DATE");
            if(StringUtils.isNotBlank(endDate)){
                IDataset tradeUUDatas = bizData.getTradeRelation();
                if(IDataUtil.isNotEmpty(tradeUUDatas)){
                    for(int i=0; i < tradeUUDatas.size(); i++){
                        IData tradeUUData = tradeUUDatas.getData(i);
                        if(IDataUtil.isNotEmpty(tradeUUData)){
                           tradeUUData.put("END_DATE", endDate);
                        }
                    }
                }
            }
        }
        
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoMebCenPay() throws Exception
    {
        IData mebcenpay = new DataMap();
        String endDate = "";
        String limitFee = "0";
        
        if (IDataUtil.isNotEmpty(paramData)){
            endDate = paramData.getString("NOTIN_PAY_END_DATE");
        }
        
        mebcenpay.put("USER_ID", reqData.getUca().getUserId());
        mebcenpay.put("INST_ID", SeqMgr.getInstId());
        //mebcenpay.put("MP_GROUP_CUST_CODE", "0");
        mebcenpay.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
        
        mebcenpay.put("PAY_TYPE", "1");
        //REQ201707170012关于新增流量自由充省内通用定额套餐的需求----------------20170828 start----------
        String payType = "";
        IDataset disCnts = reqData.cd.getDiscnt();
        if(IDataUtil.isNotEmpty(disCnts)&&disCnts.size()>0){
        	for(int i=0; i< disCnts.size();i++){
        		IData disCnt = disCnts.getData(i);
        		String disCntCode = disCnt.getString("DISCNT_CODE", "");
        		String modifyTag = disCnt.getString("MODIFY_TAG", "");
        		if("0".equals(modifyTag))
        		{
        			IDataset comInfosDataset = RouteInfoQry.getCommparaByCode("CSM", "8813", disCntCode, "0898");
        			if(IDataUtil.isNotEmpty(comInfosDataset)&&comInfosDataset.size()>0){
        				payType = comInfosDataset.getData(0).getString("PARA_CODE1", "");
        				if(StringUtils.isNotBlank(payType)){
        					mebcenpay.put("PAY_TYPE", payType);
        				}
        			}
        		}
        	}
        }else{
        	IDataset grpDatas = UserGrpInfoQry.queryGrpCenPayInfo2(reqData.getGrpUca().getUserId());
        	if(IDataUtil.isNotEmpty(grpDatas)&&grpDatas.size()>0){
        		payType = grpDatas.getData(0).getString("PAY_TYPE", "");
				if(StringUtils.isNotBlank(payType)){
					mebcenpay.put("PAY_TYPE", payType);
				}
        	}
        }
        //REQ201707170012关于新增流量自由充省内通用定额套餐的需求----------------20170828 end----------
        mebcenpay.put("OPER_TYPE", "5");//5:指定用户、限量统付
        //mebcenpay.put("PRODUCT_OFFER_ID", "0");
        mebcenpay.put("PRODUCT_OFFER_ID", reqData.getGrpUca().getProductId());
        mebcenpay.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        
        if (IDataUtil.isNotEmpty(paramData)){
            limitFee = paramData.getString("NOTIN_PAY_LIMIT_FEE");//限量的统付的成员流量大小
        }
        if(StringUtils.isNotBlank(limitFee)){
            mebcenpay.put("LIMIT_FEE", limitFee);
        }
        //mebcenpay.put("ELEMENT_ID", "");
        
        mebcenpay.put("START_DATE", SysDateMgr.getSysTime());
        if(StringUtils.isNotBlank(endDate)){
            mebcenpay.put("END_DATE", endDate);
        } else {
            mebcenpay.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
        mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
        //mebcenpay.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        
        //add by chenzg@20170830 统付总流量上限校验
        GfffUserMaxGprsSetQry.checkGfffMebGprsMax(reqData.getGrpUca().getUserId(), Long.valueOf(limitFee));
        
        this.addTradeMebCenpay(mebcenpay);

    }
    
    /**
     * 修改付费计划关系的结束时间为界面选择的统付时间
       * @throws Exception
     */
    private void infoMebPayPlan() throws Exception
    {
        if (IDataUtil.isNotEmpty(paramData))
        {
            String endDate = paramData.getString("NOTIN_PAY_END_DATE");
            if(StringUtils.isNotBlank(endDate)){
                IDataset tradePayDatas = bizData.getTradeUserPayplan();
                if(IDataUtil.isNotEmpty(tradePayDatas)){
                    for(int i=0; i < tradePayDatas.size(); i++){
                        IData tradePayData = tradePayDatas.getData(i);
                        if(IDataUtil.isNotEmpty(tradePayData)){
                            tradePayData.put("END_DATE", endDate);
                        }
                    }
                }
            }
        }
        
    }
    
    /**
     * 修改产品关系的结束时间为界面选择的统付时间
       * @throws Exception
     */
    private void infoMebProduct() throws Exception
    {
        if (IDataUtil.isNotEmpty(paramData))
        {
            String endDate = paramData.getString("NOTIN_PAY_END_DATE");
            if(StringUtils.isNotBlank(endDate)){
                IDataset tradeProDatas = bizData.getTradeProduct();
                if(IDataUtil.isNotEmpty(tradeProDatas)){
                    for(int i=0; i < tradeProDatas.size(); i++){
                        IData tradeProData = tradeProDatas.getData(i);
                        if(IDataUtil.isNotEmpty(tradeProData)){
                            tradeProData.put("END_DATE", endDate);
                        }
                    }
                }
            }
        }
        
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoRegDataSms() throws Exception{
        IDataset smsDataSet = new DatasetList();
        IData smsData = new DataMap();
        smsData.put("TEMPLATE_REPLACED", sendNoticeContent);//短信内容
        smsDataSet.add(smsData);
        super.dealSucSms(smsDataSet);
    }
    
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

}
