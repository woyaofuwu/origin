
package com.asiainfo.veris.crm.order.soa.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;

public class DestroyGgciGroupUser extends DestroyGroupUser
{

    /**
     * 构造函数
     */
    public DestroyGgciGroupUser()
    {

    }

    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        //this.regOtherInfoTrade();
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

    /**
     * Other表台帐处理
     * @throws Exception
     * @author chenzg
     * @date 2018-6-20
     */
    public void regOtherInfoTrade() throws Exception
    {
    	String curProductId = reqData.getUca().getProductId();
        String userId = this.reqData.getUca().getUserId();
        //获取user other表数据
    	IDataset ggciInfos = UserOtherInfoQry.queryUserOtherInfoForGcci(userId, "GGCI");
        if(IDataUtil.isNotEmpty(ggciInfos)){
        	IDataset dataset = new DatasetList();
			for(int i=0;i<ggciInfos.size();i++){
				IData each = ggciInfos.getData(i);
				each.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
				each.put("END_DATE", SysDateMgr.getSysTime());
				each.put("UPDATE_TIME", SysDateMgr.getSysTime());
				each.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				each.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                dataset.add(each);
			}
            addTradeOther(dataset);
        }
        
    }
}
