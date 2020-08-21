
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.changeuserelement.ChangeInternetUserElementReqData;

public class CreditLineCreditRegBean extends ChangeUserElement
{

    private String TradeTypeCode;
    
    protected ChangeInternetUserElementReqData reqData = null;
    
    private String operType;

    private String userId;
    
    private String sheetType;

    private String productId;

    /**
     * 立即计费/下账期计费
     */
    private String acctpValue;

    private String oldTradeTypeCode;

    private IData eosInfo;

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 其它台帐处理-重点
     */
    @Override
    public void actTradeSub() throws Exception
    {
        String flag = "2" ;//2修改专线 ;0新增专线 ;1 删除专线 
        
        actTradeDataline(flag);
        actTradeDatalineAttr();
        infoRegVispDataOther(flag);
        if(StringUtils.isBlank(operType) && "7010".equals(productId)) {
        } else {
            infoRegDataTradeExt();
        }
    }

    private void actTradeDataline(String flag) throws Exception
    {
        IData dataline = reqData.getDataline();
        System.out.print("fufn20180321 dataline:"+dataline);
        String productNo = dataline.getString("PRODUCT_NO", "");
        String changeMode = dataline.getString("CHANGE_MODE", "");

        if (flag.equals("2"))
        {
            // 查询专线信息
            IData inparam = new DataMap();
            inparam.put("USER_ID", userId);
//            inparam.put("SHEET_TYPE", "7");
            inparam.put("PRODUCT_NO", productNo);

            IDataset datalineList = new DatasetList();
            
            if("信控停机".equals(changeMode) || "人工停机".equals(changeMode))
            {
            	datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);
            } else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode)) {
            	datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNoForDatalineKJ(inparam);
            }
            
            if (null != datalineList && datalineList.size() > 0)
            {            
            	datalineList.getData(0).putAll(dataline);
                datalineList.getData(0).put("SHEETTYPE", sheetType);//专线停开机
                if("信控停机".equals(changeMode) || "人工停机".equals(changeMode))
                {
//                	datalineList.getData(0).put("END_DATE", getAcceptTime());//停机截止dataline会引起esop开机无法导入数据
                	datalineList.getData(0).put("RSRV_STR3", "1");
                }
                else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode))
                {
//                	datalineList.getData(0).put("END_DATE", SysDateMgr.getTheLastTime());
                	datalineList.getData(0).put("RSRV_STR3", "0");
                }
                datalineList.getData(0).put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());                
            }

            super.addTradeDataLine(datalineList);
        }

    }
    
    private void actTradeDatalineAttr() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData dataline = reqData.getDataline();
        String productNo = dataline.getString("PRODUCT_NO", "");
        String changeMode = dataline.getString("CHANGE_MODE", "");
        String serialNo =dataline.getString("SERIALNO", "");
        String subscribeId =dataline.getString("SUBSCRIBE_ID", "");
        String crmNo =dataline.getString("CRMNO", "");

        IData userData = new DataMap();
        userData.put("USER_ID", userId);
        userData.put("START_DATE", getAcceptTime());
//        userData.put("SHEET_TYPE", "7");
        String changeModeSend=changeMode;
        /*if("信控停机".equals(changeMode) || "人工停机".equals(changeMode)) {
            changeModeSend = "信控停机";
        } else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode)) {
            changeModeSend = "信控复机";
        }*/

        IDataset datalineTrades = TradeOtherInfoQry.queryDatalineAttrTrade(productNo, "PRODUCTNO");
        int j = 0;
        int k = 0;
        if(datalineTrades!=null&&datalineTrades.size()>0){
            // 查询专线信息
        	IDataset datalineAttrs = TradeOtherInfoQry.queryDatalineAttr(datalineTrades.getData(0).getString("TRADE_ID", ""), null);
        	for(int i = 0;i<datalineAttrs.size();i++){
        		IData attr = datalineAttrs.getData(i);
        		attr.put("UPDATE_TIME", getAcceptTime());
        		if(attr.getString("ATTR_CODE", "").equals("SHEETTYPE")){
                    datalineAttrs.getData(i).put("ATTR_VALUE", sheetType);//专线变更
        		}
        		if(attr.getString("ATTR_CODE", "").equals("CHANGEMODE")){
        			datalineAttrs.getData(i).put("ATTR_VALUE", changeModeSend);//专线变更
        			j = 1;
        		}
                if(attr.getString("ATTR_CODE", "").equals("ISCONTROL")) {
                    if(StringUtils.isBlank(operType)) {
                        datalineAttrs.getData(i).put("ATTR_VALUE", "是");//专线变更
                    } else {
                        datalineAttrs.getData(i).put("ATTR_VALUE", "否");
                    }
                    k = 1;
                }
        		if(attr.getString("ATTR_CODE", "").equals("SUBSCRIBE_ID")){
        			if(!subscribeId.equals("")){
        				datalineAttrs.getData(i).put("ATTR_VALUE", subscribeId);
        			}else{
            			datalineAttrs.getData(i).put("ATTR_VALUE", getTradeId());
            		}
        			
        		}
        		if(attr.getString("ATTR_CODE", "").equals("SERIALNO")){
        			if(!serialNo.equals("")){
        				datalineAttrs.getData(i).put("ATTR_VALUE", serialNo);
        			}else{
            			datalineAttrs.getData(i).put("ATTR_VALUE", "ESOP"+getTradeId()+"1");
            		}
        			
        		}
        		if(attr.getString("ATTR_CODE", "").equals("CRMNO")){
        			if(!crmNo.equals("")){
        				datalineAttrs.getData(i).put("ATTR_VALUE", crmNo);
        			}else{
            			datalineAttrs.getData(i).put("ATTR_VALUE", getTradeId());
            		}
        		}
        		if(attr.getString("ATTR_CODE", "").equals("TITLE")){
    				datalineAttrs.getData(i).put("ATTR_VALUE", changeModeSend);
        		}
        		
        		
        	}
        	if(j==0){
        		IData attrData = new DataMap(datalineAttrs.getData(0));
        		attrData.put("ATTR_CODE", "CHANGEMODE");
        		attrData.put("ATTR_VALUE", changeModeSend);
        		datalineAttrs.add(attrData);
        		/*datalineAttrs.getData(0).put("ATTR_CODE", "CHANGEMODE");
        		datalineAttrs.getData(0).put("ATTR_VALUE", changeMode);*/
            }
            if(k == 0) {
                IData attrData = new DataMap(datalineAttrs.getData(0));
                attrData.put("ATTR_CODE", "ISCONTROL");
                if(StringUtils.isBlank(operType)) {
                    attrData.put("ATTR_VALUE", "是");
                } else {
                    attrData.put("ATTR_VALUE", "否");
                }
                datalineAttrs.add(attrData);
                /*datalineAttrs.getData(0).put("ATTR_CODE", "CHANGEMODE");
                datalineAttrs.getData(0).put("ATTR_VALUE", changeMode);*/
            }
        	super.addTradeDataLineAttr(datalineAttrs);
        }   
    }

    public void infoRegVispDataOther(String flag) throws Exception
    {
        IDataset dataset = new DatasetList();
        
        IData dataline = reqData.getDataline();
        String productNo = dataline.getString("PRODUCT_NO", "");
        String changeMode = dataline.getString("CHANGE_MODE", "");
        //System.out.println("fufn20180322 dataline:"+dataline);
        // 查询专线信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", "N001");
        inparam.put("USER_ID_A", productNo);//专线号
        String stopTag = "";
        if(StringUtils.isNotBlank(operType)) {
            stopTag = "7";//人工停机
        } else {
            stopTag="9";//信控停机
        }
        inparam.put("RSRV_STR13", stopTag);
        
        
        IDataset userOther = new DatasetList();
        
        if("信控停机".equals(changeMode) || "人工停机".equals(changeMode)) {
        	userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);
        } else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode)) {
            userOther = TradeOtherInfoQry.queryUserOtherInfoByUserIdForVOIPDatalineKJ(inparam);
        }
        if(IDataUtil.isEmpty(userOther)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "为找到专线other表信息！");
        }

        for (int i = 0; i < userOther.size(); i++) {
            IData vispUser = userOther.getData(i);
            IData newVispUser = (IData) Clone.deepClone(vispUser);

            vispUser.put("UPDATE_TIME", getAcceptTime());
            //vispUser.put("END_DATE", SysDateMgr.getLastMonthLastDate());
            vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            
            //变更时先将原数据END_DATE修改为当前时间
            //newVispUser.put("START_DATE", getAcceptTime());
            newVispUser.put("UPDATE_TIME", getAcceptTime());
            //newVispUser.put("END_DATE", SysDateMgr.getTheLastTime());
            newVispUser.put("RSRV_VALUE_CODE", "N001");
            //newVispUser.put("IS_NEED_PF", "1");
            newVispUser.put("CHANGEMODE", changeMode);
            newVispUser.put("SHEETTYPE", "33");//专线变更
            newVispUser.put("INST_ID", SeqMgr.getInstId());
            newVispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            if(StringUtils.isNotBlank(changeMode)) {
                if("信控停机".equals(changeMode) || "人工停机".equals(changeMode)) {
                    String endDate = SysDateMgr.getLastMonthLastDate();
                    if(StringUtils.isNotBlank(acctpValue)&&"1".equals(acctpValue)) {
                        endDate = SysDateMgr.getLastDateThisMonth();
                    }
                    vispUser.put("RSRV_STR13", stopTag);
                    vispUser.put("RSRV_DATE1", vispUser.getString("END_DATE"));
                    vispUser.put("END_DATE", endDate);
                    //newVispUser.put("RSRV_TAG1", "1");
                    dataset.add(vispUser);
                } else if("信控开机".equals(changeMode) || "人工复机".equals(changeMode)) {
                    String sysdate = SysDateMgr.getCurDay();
                    //复机计费方式：25号之前的当月计费，25号及25号之后的次月1日计费。
                    String startDate = "";
                    if(Integer.valueOf(sysdate) < 25) {
                        startDate = SysDateMgr.getFirstDayOfThisMonth();
                    } else {
                        startDate = SysDateMgr.getFirstDayOfNextMonth();
                    }
                    //newVispUser.put("RSRV_TAG1", "0");
                    newVispUser.put("START_DATE", startDate);
                    newVispUser.put("END_DATE", vispUser.getString("RSRV_DATE1"));
                    //复机一次性资费置为0
                    newVispUser.put("RSRV_STR4", "0");
                    vispUser.put("RSRV_STR13", "");
                    dataset.add(vispUser);
                    dataset.add(newVispUser);

                }
            }

        }
        if(IDataUtil.isNotEmpty(dataset)) {
            super.addTradeOther(dataset);
        }
    }

    public void infoRegDataTradeExt() throws Exception {
        if(DataUtils.isEmpty(eosInfo)) {
            return;
        }
        IDataset extDatas = new DatasetList();
        IData data = new DataMap();
        data.put("ATTR_CODE", "ESOP");
        data.put("ATTR_VALUE", eosInfo.getString("IBSYSID"));
        data.put("RSRV_STR1", eosInfo.getString("NODE_ID"));
        data.put("RSRV_STR10", "EOS");
        data.put("RSRV_STR5", "NEWFLAG");
        data.put("RSRV_STR6", eosInfo.getString("RECORD_NUM"));
        extDatas.add(data);
        addTradeExt(extDatas);

    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeInternetUserElementReqData) getBaseReqData();
    }
    
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeInternetUserElementReqData();
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);  
        reqData.setDataline(map.getData("DATALINE")); 
    }

    @Override
    protected void makInit(IData data) throws Exception
    {
        super.makInit(data);

        TradeTypeCode = data.getString("TRADE_TYPE_CODE");
        userId = data.getString("USER_ID");
        productId = data.getString("PRODUCT_ID");
        oldTradeTypeCode = data.getString("TRADE_TYPE_CODE");
        operType = data.getString("OPER_TYPE");
        sheetType = data.getString("SHEETTYPE");
        acctpValue = data.getString("ACCEPTTANCE_PERIOD");
        eosInfo = data.getData("ESOP");
        //CSBizBean.getVisit().setStaffId(data.getString("TRADE_STAFF_ID","SUPERUSR"));
    }

    @Override
    protected void makUca(IData data) throws Exception
    {
        makUcaForGrpNormal(data);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        if("7110".equals(oldTradeTypeCode) || "7220".equals(oldTradeTypeCode) || "7305".equals(oldTradeTypeCode) || "stop".equals(operType)) {
            TradeTypeCode = "4200";// 集团暂停
        } else if("7303".equals(oldTradeTypeCode) || "7304".equals(oldTradeTypeCode) || "7301".equals(oldTradeTypeCode) || "7317".equals(oldTradeTypeCode) || "back".equals(operType)) {
            TradeTypeCode = "4201";// 集团恢复
        }
        /*if(productId.equals("7010")){
        	TradeTypeCode = "2991";
        }else if(productId.equals("7011")){
        	TradeTypeCode = "3081";
        }else if(productId.equals("7012")){
        	TradeTypeCode = "3011";
        }*/
        return TradeTypeCode;

    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        if(StringUtils.isBlank(operType)) {
            tradeData.put("SUBSCRIBE_TYPE", "200");
        }
    }

    @Override
    protected void regOrder() throws Exception
    {
        super.regOrder();
        IData tradeData = bizData.getTrade();
        if(StringUtils.isBlank(operType)) {
            tradeData.put("SUBSCRIBE_TYPE", "200");
        }
    }
    
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();
        IData map = bizData.getTrade();
        if("7110".equals(oldTradeTypeCode) || "7220".equals(oldTradeTypeCode) || "7305".equals(oldTradeTypeCode) || "stop".equals(operType)) {
            map.put("PF_WAIT", "0");
        } else if("7303".equals(oldTradeTypeCode) || "7304".equals(oldTradeTypeCode) || "7301".equals(oldTradeTypeCode) || "7317".equals(oldTradeTypeCode) || "back".equals(operType)) {
            map.put("PF_WAIT", "0");
        }
    }

}
