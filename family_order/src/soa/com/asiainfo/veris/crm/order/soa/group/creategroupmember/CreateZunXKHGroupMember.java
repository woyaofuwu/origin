
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;

public class CreateZunXKHGroupMember extends CreateGroupMember
{
    private IData paramData = new DataMap();

    /**
     * 构造函数
     * 
     * @author chenzg
     * @date 2018-5-29
     */
    public CreateZunXKHGroupMember()
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
        this.checkDiscntLimit();
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
     * 校验尊享语音优惠包下优惠和与多媒体桌面电话优惠套餐[800109]的互斥
     * @throws Exception
     * @author chenzg
     * @date 2018-6-1
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
    			if("0".equals(modifyTag) && "41004805".equals(packageId)){
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
}
