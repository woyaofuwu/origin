
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.group.gfffmanage.GfffUserMaxGprsSetQry;

public class ChangeGfffGroupLimitaionMemElement extends ChangeMemElement
{

    private IDataset mebcenpayList = new DatasetList();
    
    public ChangeGfffGroupLimitaionMemElement()
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
       
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoUserDiscnt();
        
        infoMebCenPay();
        
    }
    
    /**
     * 获取新增优惠的结束时间、
     * @param endDate
     * @throws Exception
     */
    private void infoUserDiscnt() throws Exception
    {
        IDataset discntDatas = reqData.cd.getDiscnt();
        if(IDataUtil.isNotEmpty(discntDatas)){
            for(int i=0; i < discntDatas.size(); i++){
                IData discntData = discntDatas.getData(i);
                if(IDataUtil.isNotEmpty(discntData)){
                    String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
                    String modifyTag =  discntData.getString("MODIFY_TAG","");
                    String elementId =  discntData.getString("ELEMENT_ID","");
                    String startDate =  discntData.getString("START_DATE","");
                    String endDate =  discntData.getString("END_DATE","");
                    IDataset packageInfos = UPackageElementInfoQry.getPackageElementInfoByPackageId("73440003");
                    if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                        IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "7347", "GRP_GFFF",elementId, "0898");
                        if(IDataUtil.isEmpty(commInfos)){
                            //获取限量统付流量叠加包转换异常
                            CSAppException.apperr(GrpException.CRM_GRP_851);
                        }
                        
                        IData commData = commInfos.getData(0);
                        commData.put("DISCNT_START_DATE", startDate);
                        commData.put("DISCNT_END_DATE", endDate);
                        
                        mebcenpayList.add(commData);
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
        
        if(IDataUtil.isNotEmpty(mebcenpayList)){
            
            for (int row = 0, size = mebcenpayList.size(); row < size; row++){
                
                IData data = mebcenpayList.getData(row);
                IData mebcenpay = new DataMap();
                       
                mebcenpay.put("USER_ID", reqData.getUca().getUserId());
                mebcenpay.put("INST_ID", SeqMgr.getInstId());
                mebcenpay.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
                
                mebcenpay.put("PAY_TYPE", "1");
                mebcenpay.put("OPER_TYPE", "5");//5:指定用户、限量统付
                mebcenpay.put("PRODUCT_OFFER_ID", reqData.getGrpUca().getProductId());
                mebcenpay.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
                
                mebcenpay.put("ELEMENT_ID", data.getString("PARA_CODE2",""));//个人流量包ID
                mebcenpay.put("LIMIT_FEE", data.getString("PARA_CODE3","0"));//包含流量（MB）
                
                mebcenpay.put("START_DATE", data.getString("DISCNT_START_DATE",""));
                mebcenpay.put("END_DATE", data.getString("DISCNT_END_DATE",""));
                
                mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
                mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                
                mebcenpay.put("RSRV_STR1", data.getString("PARA_CODE1",""));
                mebcenpay.put("RSRV_STR2", data.getString("PARAM_NAME",""));
                
                //add by chenzg@20170830 统付总流量上限校验
                GfffUserMaxGprsSetQry.checkGfffMebGprsMax(reqData.getGrpUca().getUserId(), mebcenpay.getLong("LIMIT_FEE", 0));
                
                this.addTradeMebCenpay(mebcenpay);
            }
            
        }
        
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }
    
}
