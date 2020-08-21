
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateJTCLBGroupMember extends CreateGroupMember
{
    private IData paramData = new DataMap();

    /**
     * 构造函数
     * 
     * @author chenzg
     * @date 2018-5-29
     */
    public CreateJTCLBGroupMember()
    {
    }

    /**
     * 生成登记信息
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramData = getParamData();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        this.specDealDiscntStartDate();
        //this.checkDiscntLimit();
    }

    public IData getParamData() throws Exception
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
     * 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR1", reqData.getGrpUca().getUserId()); // 集团USER_ID
        data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpProductId())); // 关系类型编码
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER
        data.put("RSRV_STR10", reqData.getGrpUca().getSerialNumber()); // 集团SERIAL_NUMBER

    }
    
    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        super.makInit(map);
    }

    /**
     * 校验手机畅聊包下优惠和与多媒体桌面电话优惠套餐[800109]的互斥
     * @throws Exception
     * @author chenzg
     * @date 2018-6-5
     */
    @SuppressWarnings("unused")
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
    /**
     * REQ201807090024关于将IMS套餐立即生效的时间进行修改的需求
     * 特殊处理畅聊包优惠的开始时间
     * 25号前办理则立即生效
     * 25号及之后办理则下月1号生效
     * @throws Exception
     * @author chenzg
     * @date 2018-7-19
     */
    private void specDealDiscntStartDate() throws Exception{
    	IDataset tradeDiscnts = this.bizData.getTradeDiscnt();
    	IDataset tradeAttrs = this.bizData.getTradeAttr();
    	int cDay = BizEnv.getEnvInt("grp.discnt.cday", 25);		//集团业务IMS套餐生效时间分界值
    	if(IDataUtil.isNotEmpty(tradeDiscnts)){
    		for(int i=0;i<tradeDiscnts.size();i++){
    			IData each = tradeDiscnts.getData(i);
    			String packageId = each.getString("PACKAGE_ID", "");
    			String modifyTag = each.getString("MODIFY_TAG", "");
    			String discntCode = each.getString("DISCNT_CODE", "");
    			String startDate = each.getString("START_DATE", "");
    			if("0".equals(modifyTag) && "41005005".equals(packageId)){
    				String curDay = SysDateMgr.getCurDay();
    				if(Integer.valueOf(curDay) >= cDay){
    					startDate = SysDateMgr.getFirstDayOfNextMonth();
        				each.put("START_DATE", startDate);
        				//同时修改优惠属性的开始时间
        				if(IDataUtil.isNotEmpty(tradeAttrs)){
    						IDataset attrs = DataHelper.filter(tradeAttrs, "ELEMENT_ID="+discntCode);
    						if(IDataUtil.isNotEmpty(attrs)){
    							for(int j=0;j<attrs.size();j++){
    								attrs.getData(j).put("START_DATE", startDate);
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    }
}
