
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateGgciGroupUser extends CreateGroupUser
{        
    private static transient Logger logger = Logger.getLogger(CreateGgciGroupUser.class);

    /**
     * 构造函数
     */
    public CreateGgciGroupUser()
    {

    }
    

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        this.regOtherInfoTrade();
    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
    }
    
    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
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
        
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if(IDataUtil.isNotEmpty(paramData)){
        	String ggciDsStr = paramData.getString("GGCI_TABLE_DATA", "[]");
        	if(StringUtils.isNotBlank(ggciDsStr)){
        		IDataset ggciDs = new DatasetList(ggciDsStr);
        		if (IDataUtil.isNotEmpty(ggciDs))
                {
        			IDataset dataset = new DatasetList();
        			for(int i=0;i<ggciDs.size();i++){
        				IData each = ggciDs.getData(i);
        				IData data = new DataMap();
                        data.put("USER_ID", reqData.getUca().getUserId());
                        data.put("RSRV_VALUE_CODE", "GGCI");
                        data.put("RSRV_VALUE", each.getString("GGCI_FEE_TYPECODE"));//收费类型编码:0-信息系统使用功能费,1-信息系统集成费,2-信息系统服务费
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        data.put("START_DATE", each.getString("GGCI_START_DATE"));	//生效时间
                        data.put("RSRV_STR10", each.getString("GGCI_PROJ_NAME"));	//项目名称
                        data.put("RSRV_STR1", each.getString("GGCI_FEE_NUM"));		//收费金额（元）
                        data.put("RSRV_STR2", each.getString("GGCI_FEE_NAME"));		//收费名称
                        data.put("END_DATE", each.getString("GGCI_END_DATE")+" 23:59:59");		//收费截止时间
                        data.put("REMARK", each.getString("GGCI_REMARK"));			//备注
                        dataset.add(data);
        			}
                    addTradeOther(dataset);
                }
        		/*删除该产品属性，否则插属性台帐时会出现超长的情况*/
        		IDataset tradeAttrs = this.bizData.getTradeAttr();
        		if(IDataUtil.isNotEmpty(tradeAttrs)){
        			for(int i=0;i<tradeAttrs.size();i++){
        				IData each = tradeAttrs.getData(i);
        				if("GGCI_TABLE_DATA".equals(each.getString("ATTR_CODE", ""))){
        					tradeAttrs.remove(i);
        					i--;
        				}
        			}
        		}
        	}
        }
        
    }
}
