package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.action.finish;
 
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 无手机宽带拆机成功后对手机号销户。
 * @author duhj_kd
 */
public class NoPhoneWideDestroyNewSn implements ITradeFinishAction {

	@Override
	public void  executeAction(IData mainTrade) throws Exception 
	{ 
		String wsn = mainTrade.getString("SERIAL_NUMBER",""); 
	    String wideUserId = mainTrade.getString("USER_ID","");       
        String phoneSn = "";
	    //根据宽带账号查询出开魔百和的手机号
        IDataset relaUUInfos = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(wideUserId,"47","1");
        if(IDataUtil.isNotEmpty(relaUUInfos)){
              phoneSn = relaUUInfos.first().getString("SERIAL_NUMBER_A");
        }else{
            //新模型无手机宽带开户，如果不加魔百和，没有47关系 ,KD_17889847063
            String serialNumber = wsn.substring(3);
            //新数据判断依据 1.宽带账号去除KD_以后，可以查到有效的用户信息，因为老的数据宽带账号去除kd_后没有对应的手机用户信息
            IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
            if(IDataUtil.isNotEmpty(userInfos)){
                IDataset productInfos = UserProductInfoQry.queryMainProductNow(userInfos.getData(0).getString("USER_ID"));//查询有效的产品信息
                if(IDataUtil.isNotEmpty(productInfos)){
                    IDataset productConfig = StaticUtil.getStaticList("NO_PHONE_PRODUCT");//无手机宽带开户，手机号码虚拟产品id
                    String phoneproductId = productInfos.getData(0).getString("PRODUCT_ID");
                    if(IDataUtil.isNotEmpty(productConfig)){
                        String nophoneProductId = productConfig.first().getString("DATA_ID", "20191209");
                        if(phoneproductId.equals(nophoneProductId)){//这里卡一下手机号码产品id是否是新配的无手机产品id,以防万一
                            phoneSn = serialNumber;
                        }
                    }
                }
            }
        }
        
        if(StringUtils.isNotBlank(phoneSn)){
            // 调用销户接口参数
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", phoneSn);
            param.put(Route.ROUTE_EPARCHY_CODE, "0898");
            param.put("TRADE_TYPE_CODE", "192");
            param.put("ORDER_TYPE_CODE", "192");
            param.put("SKIP_RULE","TRUE");
            param.put("REMARK", "无手机宽带拆机连带手机号码销户");
            IDataset stopResultList = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", param);
        }
   		
	}
}
