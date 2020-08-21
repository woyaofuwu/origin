
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeJTCLBMemElement extends ChangeMemElement
{
    protected IData paramInfo = new DataMap(); // 产品参数

    public ChangeJTCLBMemElement()
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
        paramInfo = getParamInfo();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        // 产品和个性化参数        
        //this.checkDiscntLimit();
    }
    
    protected void deleteTradeAttrParam() throws Exception
    {
        IDataset dataset = bizData.getTradeAttr();
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        
        IDataset svcDatas = new DatasetList();
        for(int i=0;i<dataset.size();i++)
        {
            IData data = dataset.getData(i);
            String rela_inst_id = data.getString("RELA_INST_ID","");
            String modify_tag = data.getString("MODIFY_TAG","");
            String user_id = data.getString("USER_ID","");
            String element_id = data.getString("ELEMENT_ID","");
            
            if("1".equals(modify_tag))
            {
                IData attrds = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCodeTWO(user_id, rela_inst_id, "114485", element_id, "0898");
                
                if (IDataUtil.isEmpty(attrds))
                {
                    continue;
                }else
                {
                    IData map = new DataMap();
                    map = attrds;
                    map.put("END_DATE", SysDateMgr.getSysDate());
                    //map.put("INST_ID", SeqMgr.getInstId());
                    map.put("RSRV_TAG1", "0");
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    svcDatas.add(map);
                }
                
                
            }else
            {
                continue;
            }
        }
        
        if (svcDatas.size() > 0)
        {
            addTradeAttr(svcDatas);
        }
    }
    
    protected void actTradeDiscntParam() throws Exception
    {
        IDataset dataset = bizData.getTradeAttr();
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        
        for(int i=0;i<dataset.size();i++)
        {
            IData data = dataset.getData(i);
            String rela_inst_id = data.getString("RELA_INST_ID","");
            String modify_tag = data.getString("MODIFY_TAG","");
            for(int j=i+1;j<dataset.size();j++)
            {
                IData param = dataset.getData(j);
                String rela_inst_idN = param.getString("RELA_INST_ID","");
                String modify_tagG = param.getString("MODIFY_TAG","");
                
                if(rela_inst_id.equals(rela_inst_idN) && !modify_tag.equals(modify_tagG))
                {             
                    if("0".equals(modify_tag))
                    {
                        data.put("START_DATE", SysDateMgr.firstDayOfMonth(1));
                        data.put("END_DATE", SysDateMgr.getTheLastTime());
                        data.put("RSRV_TAG1", "1");
                        param.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                        break;
                    }else
                    {
                        param.put("START_DATE", SysDateMgr.firstDayOfMonth(1));
                        param.put("END_DATE", SysDateMgr.getTheLastTime());
                        param.put("RSRV_TAG1", "1");
                        data.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                        break;
                    }
                }
            }
        }
    }
    

    /**
     * 获取参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamInfo() throws Exception
    {
        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paraminfo = reqData.cd.getProductParamMap(memProductId);
        return paraminfo;
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IData data = bizData.getTrade();
        // 主台帐预留字段
        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId()); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        data.put("RSRV_STR2", relationTypeCode);// 关系类型
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());// corpnumber集团编号
        data.put("RSRV_STR6", reqData.getGrpUca().getCustomer().getCustName());
    }
    
    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        super.makInit(map);

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        // 因为在makInit已经查出三户资料了，所以这里不再查
    }
    
    /**
     * 校验手机畅聊包下优惠和与多媒体桌面电话优惠套餐[800109]的互斥
     * @throws Exception
     * @author chenzg
     * @date 2018-6-5
     */
    private void checkDiscntLimit() throws Exception{
    	String userId = this.reqData.getUca().getUserId();
    	String serialNumber = this.reqData.getUca().getSerialNumber();
    	IDataset tradeDiscnts = this.bizData.getTradeDiscnt();
    	if(IDataUtil.isNotEmpty(tradeDiscnts)){
    		for(int i=0;i<tradeDiscnts.size();i++){
    			IData each = tradeDiscnts.getData(i);
    			String packageId = each.getString("PACKAGE_ID", "");
    			String modifyTag = each.getString("MODIFY_TAG", "");
    			if("0".equals(modifyTag) && "41005005".equals(packageId)){
    				//判断当前IMS用户是否已加入"多媒体桌面电话[2222]"成员
    				IDataset relaUUs = RelaUUInfoQry.qryDesktopMemRela(userId);
    				if(IDataUtil.isNotEmpty(relaUUs)){
    					String userIda = relaUUs.getData(0).getString("USER_ID_A");
    					//查询该集团产品用户是否办理了“自定义费用套餐[800109]”
    					IDataset userDiscnts = UserDiscntInfoQry.queryDeskTopUserDiscnt(userIda);
    					if(IDataUtil.isNotEmpty(userDiscnts)){
    						CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码["+serialNumber+"]已是多媒体桌面电话产品成员，且该集团产品用户办理了自定义费用套餐[800109]！");
    					}    					
    				}
    			}
    		}
    	}
    }
    
    /**
     * REQ201805280003关于修改融合通信业务语音优惠活动内容的需求
     * 用户是和商务标识用户，则畅聊包优惠不能打折扣
     */
    @Override
    protected void setTradeAttr(IData map) throws Exception {
		if(IDataUtil.isNotEmpty(map)){
			String userId = this.reqData.getUca().getUserId();
			String modifyTag = map.getString("MODIFY_TAG", "");
			String instType = map.getString("INST_TYPE", "");
			String attrCode = map.getString("ATTR_CODE", "");
			String attrVal = map.getString("ATTR_VALUE", "0");
			if("D".equals(instType)&&"18605".equals(attrCode) && ("0".equals(modifyTag)||"2".equals(modifyTag))){
				/*判断用户是否和商务标识用户*/
				IDataset otherDs = UserOtherInfoQry.queryUserOtherInfoForPg(userId, "PG_UNIONPAY");
				//和商务标识用户不能打折扣
				if(IDataUtil.isNotEmpty(otherDs)){
					int iAttrVal = Integer.parseInt(attrVal);
					if(iAttrVal != 100){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "该成员是和商务标识用户，畅聊包优惠不能打折扣，请确认!");
					}
				}					
			}
		}
		super.setTradeAttr(map);
	}
}
