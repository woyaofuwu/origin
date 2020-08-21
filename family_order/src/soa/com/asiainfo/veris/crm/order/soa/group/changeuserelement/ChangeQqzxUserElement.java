
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeQqzxUserElement extends ChangeUserElement
{

    public ChangeQqzxUserElement()
    {
    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }
    
    protected void makReqDataElement() throws Exception
    {
        super.makReqDataElement();
        IDataset elementParams = reqData.cd.getElementParam();
        
        if(IDataUtil.isNotEmpty(elementParams)){
            boolean addB = false;
            int addElementInt = 0;
            boolean delB = false;
            int delElementInt = 0;
            for(int i = 0 , len = elementParams.size(); i < len ;i++){
                IData elementParam = elementParams.getData(i);
                String attrCode = elementParam.getString("ATTR_CODE","");
                String modifyTag = elementParam.getString("MODIFY_TAG","");
                
                if(attrCode.equals("35000000") && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                    addB = true;
                    addElementInt = i;
                }else  if(attrCode.equals("35000000") && modifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                    delB = true;
                    delElementInt = i;
                }
                
            }
            if(addB && delB) {
                elementParams.getData(addElementInt).put("START_DATE", reqData.getAcceptTime());
                elementParams.getData(delElementInt).put("END_DATE", SysDateMgr.getLastSecond(reqData.getAcceptTime()));
                reqData.cd.putElementParam(elementParams);
            }
        }
        
    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegVispDataOther();
    }
    
    public IData getTradeUserExtendData() throws Exception
    {
        IData userExtenData = super.getTradeUserExtendData();
        return userExtenData;
    }

    public void infoRegVispDataOther() throws Exception
    {

        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();
        
        IDataset wideData = new DatasetList(param.getString("NOTIN_WideData", "[]"));
        IDataset serialData = new DatasetList(param.getString("NOTIN_SerialData", "[]"));
        IDataset fixedData = new DatasetList(param.getString("NOTIN_FixedData", "[]"));
        
        String addressStr = param.getString("NOTIN_INSTALL_ADDRESS");
        String pathStr = param.getString("NOTIN_PATH");
        
        IData pathParam = new DataMap();
        pathParam.put("USER_ID", reqData.getUca().getUser().getUserId());
        pathParam.put("RSRV_VALUE_CODE", "PATH");
        IDataset path = TradeOtherInfoQry.queryUserOtherInfoByUserId(pathParam);
        if(null != path && path.size() > 0){
            IData pathData = path.getData(0);
            pathData.put("RSRV_STR10", pathStr);
            pathData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            dataset.add(pathData);
        }
        
        IData addressParam = new DataMap();
        addressParam.put("USER_ID", reqData.getUca().getUser().getUserId());
        addressParam.put("RSRV_VALUE_CODE", "ADDRESS");
        IDataset address = TradeOtherInfoQry.queryUserOtherInfoByUserId(addressParam);
        
        if(null != address && address.size() > 0){
            IData addressData = address.getData(0);
            addressData.put("RSRV_STR10", addressStr);
            addressData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            dataset.add(addressData);
        }
        
        IDataset wide = regWideDate(wideData);
        IDataset fixed = regFixedDate(fixedData);
        IDataset serial = regSerialDate(serialData);
        dataset.addAll(wide);
        dataset.addAll(fixed);
        dataset.addAll(serial);
        
        addTradeOther(dataset);
    }
    
    //宽带
    private IDataset regWideDate(IDataset wideData) throws Exception {
        IDataset dataset = new DatasetList();
        IData inparamWide = new DataMap();
        inparamWide.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparamWide.put("RSRV_VALUE_CODE", "BINDWIDE");
        IDataset wideOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparamWide);
        
        for (int j = 0; j < wideData.size(); j++)
        {
            IData internet = wideData.getData(j);
            String xtage = internet.getString("pam_NOTIN_NOTIN_X_TAG");
            String oldAcctId = internet.getString("pam_NOTIN_OLD_WIDE_ACCT_ID");
            String flag = internet.getString("tag", "");
            
            if ("0".equals(flag) && StringUtils.isBlank(oldAcctId))
            {
                IData vispUsers = new DataMap();

                vispUsers.put("USER_ID", reqData.getUca().getUserId());
                vispUsers.put("START_DATE", getAcceptTime());
                vispUsers.put("END_DATE", SysDateMgr.getTheLastTime());
                
                vispUsers.put("RSRV_VALUE_CODE", "BINDWIDE");
                // 宽带账号
                vispUsers.put("RSRV_VALUE", internet.getString("pam_NOTIN_WIDE_ACCT_ID"));
                // 宽带时长
                vispUsers.put("RSRV_STR1", internet.getString("pam_NOTIN_WIDE_MONTH"));
                // 带宽
                vispUsers.put("RSRV_STR2", internet.getString("pam_NOTIN_WIDE_NET_LINE"));
                
                vispUsers.put("INST_ID", SeqMgr.getInstId());
                
                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(vispUsers);
            }
            
            for (int i = 0; i < wideOther.size(); i++)
            {
                IData vispUser = wideOther.getData(i);
                String acctIdOther = vispUser.getString("RSRV_VALUE");
                if (oldAcctId.equals(acctIdOther))
                {
                    if ("1".equals(flag))
                    {
                        if(null != xtage && "0".equals(xtage)){
                            vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            vispUser.put("UPDATE_TIME", getAcceptTime());
                            vispUser.put("END_DATE", getAcceptTime());
                            dataset.add(vispUser);
                        }
                    }
                    else if ("2".equals(flag))
                    {
                        vispUser.put("UPDATE_TIME", getAcceptTime());
                        // 宽带账号
                        vispUser.put("RSRV_VALUE", internet.getString("pam_NOTIN_WIDE_ACCT_ID"));
                        // 宽带时长
                        vispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_WIDE_MONTH"));
                        // 带宽
                        vispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_WIDE_NET_LINE"));

                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        dataset.add(vispUser);

                    } 
                }
            }
        }
        
        return dataset;
    }
    
    
    //固话
    private IDataset regFixedDate(IDataset fixedData) throws Exception {
        IDataset dataset = new DatasetList();
        IData inparamWide = new DataMap();
        inparamWide.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparamWide.put("RSRV_VALUE_CODE", "BINDFIXED");
        IDataset wideOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparamWide);
        
        for (int j = 0; j < fixedData.size(); j++)
        {
            IData internet = fixedData.getData(j);
            String xtage = internet.getString("pam_NOTIN_NOTIN_X_TAG");
            String oldAcctId = internet.getString("pam_NOTIN_OLD_FIXED_PHONE");
            String flag = internet.getString("tag", "");
            
            if ("0".equals(flag) && StringUtils.isBlank(oldAcctId))
            {
                IData vispUsers = new DataMap();

                vispUsers.put("USER_ID", reqData.getUca().getUserId());
                
                vispUsers.put("RSRV_VALUE_CODE", "BINDFIXED");
                // 固定话号
                vispUsers.put("RSRV_VALUE", internet.getString("pam_NOTIN_FIXED_PHONE"));
                // 金额
                vispUsers.put("RSRV_STR1", internet.getString("pam_NOTIN_FIXED_MONEY"));

                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                
                vispUsers.put("INST_ID", SeqMgr.getInstId());
                vispUsers.put("START_DATE", getAcceptTime());
                vispUsers.put("END_DATE", SysDateMgr.getTheLastTime());
                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(vispUsers);
            }
            
            for (int i = 0; i < wideOther.size(); i++)
            {
                IData vispUser = wideOther.getData(i);
                String acctIdOther = vispUser.getString("RSRV_VALUE");
                if (oldAcctId.equals(acctIdOther))
                {
                    if ("1".equals(flag))
                    {
                        if(null != xtage && "0".equals(xtage)){
                            vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            vispUser.put("UPDATE_TIME", getAcceptTime());
                            vispUser.put("END_DATE", getAcceptTime());
                            dataset.add(vispUser);
                        }
                    }
                    else if ("2".equals(flag))
                    {
                        vispUser.put("UPDATE_TIME", getAcceptTime());
                        // 固定话号
                        vispUser.put("RSRV_VALUE", internet.getString("pam_NOTIN_FIXED_PHONE"));
                        // 金额
                        vispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_FIXED_MONEY"));

                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        dataset.add(vispUser);

                    } 
                }
            }
        }
        
        return dataset;
    }
    
    
    //手机
    private IDataset regSerialDate(IDataset serialData) throws Exception {
        IDataset dataset = new DatasetList();
        IData inparamWide = new DataMap();
        inparamWide.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparamWide.put("RSRV_VALUE_CODE", "BINDSERIAL");
        IDataset wideOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparamWide);
        
        for (int j = 0; j < serialData.size(); j++)
        {
            IData internet = serialData.getData(j);
            String xtage = internet.getString("pam_NOTIN_NOTIN_X_TAG");
            String oldAcctId = internet.getString("pam_NOTIN_OLD_SERIAL_PHONE");
            String flag = internet.getString("tag", "");
            
            if ("0".equals(flag) && StringUtils.isBlank(oldAcctId))
            {
                IData vispUsers = new DataMap();

                vispUsers.put("USER_ID", reqData.getUca().getUserId());
                
                vispUsers.put("RSRV_VALUE_CODE", "BINDSERIAL");
                // 宽带账号
                vispUsers.put("RSRV_VALUE", internet.getString("pam_NOTIN_SERIAL_PHONE"));
                // 宽带时长
                vispUsers.put("RSRV_STR1", internet.getString("pam_NOTIN_SERIAL_MONEY"));
                
                vispUsers.put("INST_ID", SeqMgr.getInstId());
                vispUsers.put("START_DATE", getAcceptTime());
                vispUsers.put("END_DATE", SysDateMgr.getTheLastTime());
                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(vispUsers);
            }
            
            for (int i = 0; i < wideOther.size(); i++)
            {
                IData vispUser = wideOther.getData(i);
                String acctIdOther = vispUser.getString("RSRV_VALUE");
                if (oldAcctId.equals(acctIdOther))
                {
                    if ("1".equals(flag))
                    {
                        if(null != xtage && "0".equals(xtage)){
                            vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            vispUser.put("UPDATE_TIME", getAcceptTime());
                            vispUser.put("END_DATE", getAcceptTime());
                            dataset.add(vispUser);
                        }
                    }
                    else if ("2".equals(flag))
                    {
                        vispUser.put("UPDATE_TIME", getAcceptTime());
                        // 宽带账号
                        vispUser.put("RSRV_VALUE", internet.getString("pam_NOTIN_SERIAL_PHONE"));
                        // 宽带时长
                        vispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_SERIAL_MONEY"));

                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        dataset.add(vispUser);

                    } 
                }
            }
        }
        
        return dataset;
    }
    

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
    }
}
