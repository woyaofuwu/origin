
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.bizcommon.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

/**
 * 集团定制云产品修改
 * 
 * @author 
 */
public class ChangeGDZYUserElement extends ChangeUserElement
{
    
    public ChangeGDZYUserElement()
    {
    }


    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

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
    protected void chkTradeAfter() throws Exception
    {
        super.chkTradeAfter();
    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        regOtherInfoTrade();
    }
    
    public void regOtherInfoTrade() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();

        IDataset otherTradeInfo = new DatasetList(param.getString("NOTIN_AttrGdzy", "[]"));

        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("RSRV_VALUE_CODE", "GDZY");
        IDataset userOtherInfo = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        for (int j = 0; j < otherTradeInfo.size(); j++)
        {
            IData otherTrade = otherTradeInfo.getData(j);
            String numberCode = otherTrade.getString("pam_NOTIN_OPER_TAG");
            String flag = otherTrade.getString("tag", "");

            if(IDataUtil.isNotEmpty(userOtherInfo)) 
            {
                for (int i = 0; i < userOtherInfo.size(); i++)
                {
                    IData oldUserOther = userOtherInfo.getData(i);
                    String lineNumberCode = oldUserOther.getString("RSRV_VALUE");
                    if (numberCode.equals(lineNumberCode))
                    {                        
                        if ("2".equals(flag))
                        {
                            //收费名称,用收费截止时间来填写end_date
                            String feeNameV = otherTrade.getString("pam_NOTIN_FEE_NAME");
                            if("4".equals(feeNameV)) //4其他一次性费用的修改不用做改end_date
                            {
                            	
                            }
                            else 
                            {
                           	 	String endDate = otherTrade.getString("pam_NOTIN_FEE_END_DATE");
                                if(StringUtils.isNotBlank(endDate) && (endDate.length() == 10))
                                {
                               	 	endDate = endDate + " 23:59:59";
                                }
                                oldUserOther.put("END_DATE", endDate);
                            }
                            
                            //收费金额
                            oldUserOther.put("RSRV_STR3", otherTrade.getString("pam_NOTIN_FEE_COST"));
                            //收费截止时间
                            oldUserOther.put("RSRV_STR4", otherTrade.getString("pam_NOTIN_FEE_END_DATE"));
                            //备注
                            oldUserOther.put("RSRV_STR6", otherTrade.getString("pam_NOTIN_REMARK"));
                            
                            oldUserOther.put("UPDATE_TIME", getAcceptTime());
                            oldUserOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            oldUserOther.put("REMARK", "修改数据");
                            dataset.add(oldUserOther);
                        }
                        
                        
                        if ("1".equals(flag))
                        {
                        	//对原来的数据删除，并截止掉
                            oldUserOther.put("UPDATE_TIME", getAcceptTime());
                            oldUserOther.put("END_DATE", getAcceptTime());
                            oldUserOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            oldUserOther.put("REMARK", "修改截止掉时间");
                            dataset.add(oldUserOther);
                        }
                        
                    }
                }
            }
            
            
            if ("0".equals(flag))
            {
                IData newUserOther = new DataMap();

                newUserOther.put("USER_ID", reqData.getUca().getUserId());
                newUserOther.put("RSRV_VALUE_CODE", "GDZY");
                
                //收费名称,用收费截止时间来填写end_date
                String feeNameV = otherTrade.getString("pam_NOTIN_FEE_NAME");
                if("4".equals(feeNameV)) //4其他一次性费用
                {
                	newUserOther.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                }
                else 
                {
               	 	String endDate = otherTrade.getString("pam_NOTIN_FEE_END_DATE");
                    if(StringUtils.isNotBlank(endDate) && (endDate.length() == 10))
                    {
                   	 	endDate = endDate + " 23:59:59";
                    }
                    newUserOther.put("END_DATE", endDate);
                }
                
                //新增一条新的数据
                newUserOther.put("RSRV_VALUE", String.valueOf(otherTrade.getString("pam_NOTIN_OPER_TAG")));
                //项目名称
                newUserOther.put("RSRV_STR1", otherTrade.getString("pam_NOTIN_PROJECT_NAME"));
                //收费名称
                newUserOther.put("RSRV_STR2", otherTrade.getString("pam_NOTIN_FEE_NAME"));
                //收费金额
                newUserOther.put("RSRV_STR3", otherTrade.getString("pam_NOTIN_FEE_COST"));
                //收费截止时间
                newUserOther.put("RSRV_STR4", otherTrade.getString("pam_NOTIN_FEE_END_DATE"));
                //备注
                newUserOther.put("RSRV_STR6", otherTrade.getString("pam_NOTIN_REMARK"));

                newUserOther.put("START_DATE", getAcceptTime());
                //newUserOther.put("END_DATE", SysDateMgr.getTheLastTime());

                newUserOther.put("INST_ID", SeqMgr.getInstId());
                newUserOther.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                newUserOther.put("REMARK", "修改里做新增费项");
                dataset.add(newUserOther);
            }
            
        }
        
        addTradeOther(dataset);
    }

    
}
