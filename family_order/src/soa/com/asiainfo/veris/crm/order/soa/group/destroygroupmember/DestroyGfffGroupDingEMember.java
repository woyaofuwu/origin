
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyGfffGroupDingEMember extends DestroyGroupMember
{

    public DestroyGfffGroupDingEMember()
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
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        String delEndDate = infoUserDiscnt();
        
        infoRegDataSpecial(delEndDate);
        
        infoResDataUU(delEndDate);
    }

    /**
     * 
     * @throws Exception
     */
    private String infoUserDiscnt() throws Exception
    {        
        String delEndDate = "";
        IDataset discntDatas = reqData.cd.getDiscnt();
        
        if(IDataUtil.isNotEmpty(discntDatas)){
            for(int i=0; i < discntDatas.size(); i++){
                IData discntData = discntDatas.getData(i);
                if(IDataUtil.isNotEmpty(discntData)){
                    
                    String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
                    String modifyTag =  discntData.getString("MODIFY_TAG","");
                    
                    IDataset packageInfos = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430002");
                    if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos, "ELEMENT_ID="+discntData.getString("DISCNT_CODE")))
                    		&& "D".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                        
                        delEndDate = discntData.getString("END_DATE","");
                        break;
                    } 
                }
            }
        }
        
        return delEndDate;
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoRegDataSpecial(String endDate) throws Exception
    {
        String userid = reqData.getUca().getUserId();
        IData mebcenpayParam = new DataMap();
        mebcenpayParam.put("USER_ID", userid);
        mebcenpayParam.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
        mebcenpayParam.put("PRODUCT_OFFER_ID", reqData.getGrpUca().getProductId());
        mebcenpayParam.put("OPER_TYPE", "4");//指定用户，定额统付
        
        IDataset mebCenPayInfoDataset = UserGrpInfoQry.queryMebCenPayInfoByUserIdAll(mebcenpayParam);
        if (IDataUtil.isNotEmpty(mebCenPayInfoDataset))
        {
            for (int i = 0, sizeI = mebCenPayInfoDataset.size(); i < sizeI; i++)
            {
                IData mebCenpay = mebCenPayInfoDataset.getData(i);

                mebCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                if(StringUtils.isNotEmpty(endDate)){
                    mebCenpay.put("END_DATE", endDate);
                } else {
                    mebCenpay.put("END_DATE", SysDateMgr.getSysTime());
                }
            }
        }
        
        this.addTradeMebCenpay(mebCenPayInfoDataset);
        
    }
    
    
    /**
     * 
     * @throws Exception
     */
    private void infoResDataUU(String endDate) throws Exception
    {
    	IDataset relationDatasets = bizData.getTradeRelation();
    	
        if (IDataUtil.isNotEmpty(relationDatasets))
        {
            for (int i = 0, sizeI = relationDatasets.size(); i < sizeI; i++)
            {
                IData relationData = relationDatasets.getData(i);

                if(StringUtils.isNotEmpty(endDate)){
                	relationData.put("END_DATE", endDate);
                }
            }
        }
    }
    
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        //makReqDataMemElement();
        
    }
    
//    private void makReqDataMemElement() throws Exception{
//        
//        IDataset userDiscntList = reqData.cd.getDiscnt();
//        
//        if (IDataUtil.isNotEmpty(userDiscntList)){
//            for (int i = 0, iSize = userDiscntList.size(); i < iSize; i++)
//            {
//                IData userDiscntData = userDiscntList.getData(i); // 取每个元素
//                if ("D".equals(userDiscntData.getString("ELEMENT_TYPE_CODE")) &&
//                        "73430002".equals(userDiscntData.getString("PACKAGE_ID"))
//                        && TRADE_MODIFY_TAG.DEL.getValue().equals(userDiscntData.getString("MODIFY_TAG"))){ // 优惠
//                    userDiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
//                }
//            }
//        }
//       
//    }
    
    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
    	super.setTradeDiscnt(map);
    	IDataset packageInfos = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430002");
    	if (IDataUtil.isNotEmpty(DataHelper.filter(packageInfos, "ELEMENT_ID="+map.getString("DISCNT_CODE"))) && TRADE_MODIFY_TAG.DEL.getValue().equals(map.getString("MODIFY_TAG"))){ // 优惠
    		map.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
    }
    
    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
    	if ("7343".equals(map.getString("ATTR_CODE")) &&
                TRADE_MODIFY_TAG.DEL.getValue().equals(map.getString("MODIFY_TAG"))){ // 属性
    		map.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        }
    }
    
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

}
