
package com.asiainfo.veris.crm.order.soa.group.dataline;

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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;



public class GrpUserDatalineUnbindBean extends GroupBean
{
    //数据专线（专网专线）解绑
    
    protected GrpUserDatalineReqData reqData = null;
    
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegUserOther();
        
    }
    
    /**
     * 
     * @throws Exception
     */
    public void infoRegUserOther() throws Exception{
        IDataset dataLineInfos = reqData.getDataLine();
        
        IDataset newOtherData = new DatasetList();
        IDataset newOtherDataline = new DatasetList();
        
        if(IDataUtil.isNotEmpty(dataLineInfos)){
            
            for (int i = 0; i < dataLineInfos.size(); i++){
                IData dataLineInfo = dataLineInfos.getData(i);
                
                String userId = dataLineInfo.getString("USER_ID","");
                String tradeId = dataLineInfo.getString("TRADE_ID","");
                String productNo = dataLineInfo.getString("PRODUCT_NO","");
                
                IDataset userOtherInfos = TradeOtherInfoQry.queryUserOtherInfosByTradeId(tradeId,userId,productNo);
                if(IDataUtil.isNotEmpty(userOtherInfos)){
                    for (int j = 0; j < userOtherInfos.size(); j++)
                    {
                        IData userOtherInfo = userOtherInfos.getData(j);
                        if(IDataUtil.isNotEmpty(userOtherInfo)){
                            userOtherInfo.put("UPDATE_TIME", getAcceptTime());
                            userOtherInfo.put("END_DATE", SysDateMgr.getSysTime());
                            userOtherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            newOtherData.add(userOtherInfo);
                        }
                    }
                }
                
                IDataset userOtherDataLine = TradeOtherInfoQry.queryUserOtherDataLineByTradeId(tradeId,userId,productNo);
                if(IDataUtil.isNotEmpty(userOtherDataLine)){
                    for (int j = 0; j < userOtherDataLine.size(); j++)
                    {
                        IData userLine = userOtherDataLine.getData(j);
                        if(IDataUtil.isNotEmpty(userLine)){
                            userLine.put("UPDATE_TIME", getAcceptTime());
                            userLine.put("END_DATE", SysDateMgr.getSysTime());
                            userLine.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            newOtherDataline.add(userLine);
                        }
                    }
                }
                
            }
        }
        
        this.addTradeOther(newOtherData);
        this.addTradeDataLine(newOtherDataline);
    }
    
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GrpUserDatalineReqData();
    }
    
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GrpUserDatalineReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setGRP_USER_ID(map.getString("GRP_USER_ID"));
        reqData.setDataLine(map.getDataset("DataLine")); 
        //reqData.setCRMNO_ATTR_VALUE(map.getString("ATTR_VALUE")); 
        
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        super.makUca(map);
        
        String grpUserId = map.getString("GPR_USER_ID","");
        if(StringUtils.isBlank(grpUserId)){
            CSAppException.apperr(GrpException.CRM_GRP_891);
        }
        
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUserId);
        UcaData grpUca = UcaDataFactory.getNormalUcaByUserIdForGrp(inparam);
        reqData.setUca(grpUca);
        
    }
    
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "3004";//数据专线（专网专线）解绑
    }
    
}