
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean;

import java.math.BigDecimal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 畅享流量
 * 
 * @author
 */
public class LargessFluxAddBean extends MemberBean
{

    protected LargessFluxAddDiscntReqData reqData = null;

    /**
     * 生成台帐表其它数据（拼台帐前）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        
        infoRegDiscnt();
        
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

    }

    public void infoRegDiscnt() throws Exception
    {
        
        IDataset discntsets = new DatasetList();
        IDataset attrsets = new DatasetList();
        
        String bindTeamTag = reqData.getBindTeam();
        if(StringUtils.isNotBlank(bindTeamTag) && "0".equals(bindTeamTag)){//绑定到用户手机上
            
            String discntCode = reqData.getDISCNT_CODE();
            String limitFee = reqData.getLimitFee();
            String instId = SeqMgr.getInstId();
            
            //int intlimitFee = Integer.parseInt(limitFee);
            double intlimitFee = Double.parseDouble(limitFee);
            BigDecimal bd = new BigDecimal(intlimitFee * 1024 * 1024 );//.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                     
            if (StringUtils.isNotBlank(discntCode)){
                IData discntData = new DataMap();
                discntData.put("DISCNT_CODE", reqData.getDISCNT_CODE());
                discntData.put("USER_ID", reqData.getUca().getUserId());
                discntData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                discntData.put("PRODUCT_ID", "-1");
                discntData.put("PACKAGE_ID", "-1");
                discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                discntData.put("START_DATE", this.getAcceptTime());
                discntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                discntData.put("INST_ID", instId);
                discntData.put("SPEC_TAG","0");
                discntData.put("REMARK", "批量办理畅享流量优惠-用户");
                discntsets.add(discntData);
                
                if(StringUtils.isNotBlank(limitFee)){
                    IData attrData = new DataMap();
                    attrData.put("USER_ID", reqData.getUca().getUserId());
                    attrData.put("INST_TYPE", "D");
                    attrData.put("RELA_INST_ID", instId);
                    attrData.put("ATTR_CODE", "7346");
                    attrData.put("ATTR_VALUE", bd.toString());
                    attrData.put("START_DATE", SysDateMgr.getSysTime());
                    attrData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                    attrData.put("INST_ID", SeqMgr.getInstId());
                    attrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    attrData.put("ELEMENT_ID", reqData.getDISCNT_CODE());
                    attrsets.add(attrData);
                    
                }
                
            }
            
            
        } else if(StringUtils.isNotBlank(bindTeamTag) && "1".equals(bindTeamTag)){//绑定到集团产品上
            
            String discntCode = reqData.getDISCNT_CODE();
            String limitFee = reqData.getLimitFee();
            String instId = SeqMgr.getInstId();
            
            //int intlimitFee = Integer.parseInt(limitFee);
            double intlimitFee = Double.parseDouble(limitFee);
            BigDecimal bd = new BigDecimal(intlimitFee * 1024 * 1024 );//.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            
            if (StringUtils.isNotBlank(discntCode)){
                IData discntData = new DataMap();
                discntData.put("DISCNT_CODE", reqData.getDISCNT_CODE());
                discntData.put("USER_ID", reqData.getGrpUca().getUserId());
                discntData.put("USER_ID_A", "-1");
                discntData.put("PRODUCT_ID", "-1");
                discntData.put("PACKAGE_ID", "-1");
                discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                discntData.put("START_DATE", this.getAcceptTime());
                //discntData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(6));
                discntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                discntData.put("INST_ID", instId);
                discntData.put("SPEC_TAG","0");
                discntData.put("REMARK", "批量办理畅享流量优惠-集团产品 ");
                discntsets.add(discntData);
                
                if(StringUtils.isNotBlank(limitFee)){
                    IData attrData = new DataMap();
                    attrData.put("USER_ID", reqData.getGrpUca().getUserId());
                    attrData.put("INST_TYPE", "D");
                    attrData.put("RELA_INST_ID", instId);
                    attrData.put("ATTR_CODE", "7346");
                    attrData.put("ATTR_VALUE", bd.toString());
                    attrData.put("START_DATE", SysDateMgr.getSysTime());
                    //attrData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(6));
                    attrData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                    attrData.put("INST_ID", SeqMgr.getInstId());
                    attrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    attrData.put("ELEMENT_ID", reqData.getDISCNT_CODE());
                    attrsets.add(attrData);
                    
                }
                
            }
        }
                     
        this.addTradeDiscnt(discntsets);
        this.addTradeAttr(attrsets);
    }
    

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new LargessFluxAddDiscntReqData();
    }
    
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        
        reqData = (LargessFluxAddDiscntReqData) getBaseReqData();
        
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setDISCNT_CODE(map.getString("DISCNT_CODE"));
        reqData.setBindTeam(map.getString("BindTeam"));
        reqData.setLimitFee(map.getString("LimitFee"));
        
        String bindTeam = map.getString("BindTeam");
        if(StringUtils.isNotBlank(bindTeam) && "1".equals(bindTeam)){
            reqData.setGRP_USER_ID(map.getString("GRP_USER_ID"));
            reqData.setGRP_SERIAL_NUMBER(map.getString("GRP_SERIAL_NUMBER")); 
        }
    }
    
    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        
        String bindTeam = map.getString("BindTeam","");
        //UcaData grpUcas = reqData.getGrpUca();
        //UcaData uca = reqData.getUca();
        if(StringUtils.isNotBlank(bindTeam) && "0".equals(bindTeam)){//绑定到用户手机上
            
            String grpSn = map.getString("GRP_SERIAL_NUMBER","");
            if (StringUtils.isNotBlank(grpSn)){
                IData inparam = new DataMap();
                inparam.put("SERIAL_NUMBER", grpSn);
                UcaData grpUca = UcaDataFactory.getNormalUcaBySnForGrp(inparam);
                reqData.setGrpUca(grpUca);
            }
            
            String sn = map.getString("SERIAL_NUMBER","");
            if (StringUtils.isNotBlank(sn)){
                UcaData snUca = UcaDataFactory.getNormalUca(sn);
                reqData.setUca(snUca);
            }
            
            //UcaData grpUcas2 = reqData.getGrpUca();
            //UcaData uca2 = reqData.getUca();
        } else if(StringUtils.isNotBlank(bindTeam) && "1".equals(bindTeam)){ //绑定到集团产品上
            String grpSn = map.getString("SERIAL_NUMBER","");//优惠需要绑定到的集团产品编码
            if (StringUtils.isNotBlank(grpSn)){
                IData inparam = new DataMap();
                inparam.put("SERIAL_NUMBER", grpSn);
                UcaData grpUca = UcaDataFactory.getNormalUcaBySnForGrp(inparam);
                reqData.setGrpUca(grpUca);
                reqData.setUca(grpUca);
            }
        }
        
    }
    
    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        String bindTeamTag = reqData.getBindTeam();
        String limitFee = reqData.getLimitFee();
        //int intlimitFee = Integer.parseInt(limitFee);
        double intlimitFee = Double.parseDouble(limitFee);
        BigDecimal bd = new BigDecimal(intlimitFee * 1024 * 1024 );
        
        if(StringUtils.isNotBlank(bindTeamTag) && "0".equals(bindTeamTag)){//绑定到用户手机上
            
            CustPersonTradeData  custData = reqData.getUca().getCustPerson();
            String custName = "";
            if (custData != null){
                custName = custData.getCustName();
            }
            
            data.put("CUST_NAME", custName); // 客户名称
            data.put("CUST_ID",  reqData.getUca().getCustId());
            data.put("ACCT_ID",  reqData.getUca().getAccount().getAcctId()); // 帐户标识
            data.put("NET_TYPE_CODE", "00"); // 网别编码
            data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
            data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
            data.put("PRODUCT_ID", reqData.getUca().getProductId()); // 产品标识
            data.put("BRAND_CODE", reqData.getUca().getBrandCode());// 品牌编码
            data.put("CUST_ID_B", reqData.getGrpUca().getCustId()); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
            data.put("ACCT_ID_B", reqData.getGrpUca().getAcctId()); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
            
            data.put("USER_ID", reqData.getUca().getUserId()); // 网外的
            data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

            data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 集团的
            data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());

            data.put("REMARK", "批量办理畅享流量优惠-用户");
            
            data.put("RSRV_STR1", bindTeamTag); // 预留字段1-个人用户
            data.put("RSRV_STR2", bd.toString()); // 预留字段2
            data.put("RSRV_STR3", ""); // 预留字段3
            data.put("RSRV_STR4", ""); // 预留字段4
            data.put("RSRV_STR5", ""); // 预留字段5
            data.put("RSRV_STR6", ""); // 预留字段6
            data.put("RSRV_STR7", ""); // 预留字段7
            data.put("RSRV_STR8", ""); // 预留字段8
            data.put("RSRV_STR9", ""); // 预留字段9
            
        } else if(StringUtils.isNotBlank(bindTeamTag) && "1".equals(bindTeamTag)){//绑定到集团产品上
         
            data.put("CUST_NAME", reqData.getGrpUca().getCustomer().getCustName()); // 客户名称
            data.put("CUST_ID", reqData.getGrpUca().getCustId());
            data.put("ACCT_ID", reqData.getGrpUca().getAcctId()); // 帐户标识
            data.put("NET_TYPE_CODE", "00"); // 网别编码
            data.put("EPARCHY_CODE", reqData.getGrpUca().getUser().getEparchyCode()); // 归属地州
            data.put("CITY_CODE", reqData.getGrpUca().getUser().getCityCode()); // 归属业务区
            data.put("PRODUCT_ID", reqData.getGrpUca().getProductId()); // 产品标识
            data.put("BRAND_CODE", reqData.getGrpUca().getBrandCode());// 品牌编码
            
            data.put("USER_ID", reqData.getUca().getUserId()); 
            data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

            data.put("USER_ID_B", "-1"); // 集团的
            data.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
            data.put("ACCT_ID_B", "-1"); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
            
            data.put("REMARK", "批量办理畅享流量优惠-集团产品");
            
            data.put("RSRV_STR1", bindTeamTag); // 预留字段1-集团产品
            data.put("RSRV_STR2", bd.toString()); // 预留字段2
            //给集团产品分配时，需要把集团产品的编码和user_id分别存放到预留字段RSRV_STR3和RSRV_STR4
            data.put("RSRV_STR3", reqData.getGRP_SERIAL_NUMBER()); // 预留字段3
            data.put("RSRV_STR4", reqData.getGRP_USER_ID()); // 预留字段4
            data.put("RSRV_STR5", ""); // 预留字段5
            data.put("RSRV_STR6", ""); // 预留字段6
            data.put("RSRV_STR7", ""); // 预留字段7
            data.put("RSRV_STR8", ""); // 预留字段8
            data.put("RSRV_STR9", ""); // 预留字段9
        }
       
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "4001"; //畅享流量
    }
}
