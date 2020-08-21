
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.undo;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;


public class UndoCancelUnlimitedPlansAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String userId = mainTrade.getString("USER_ID");
        String mainSn = mainTrade.getString("SERIAL_NUMBER");
        String productID = mainTrade.getString("PRODUCT_ID","");
        String cancelTag = mainTrade.getString("CANCEL_TAG","");
    	String tradeId = mainTrade.getString("TRADE_ID");
    	String eparchyCode = mainTrade.getString("EPARCHY_CODE");
        
    	if(StringUtils.isNotBlank(productID) && StringUtils.isNotBlank(cancelTag) && "2".equals(cancelTag)){	
            IData commpara = new DataMap();
            commpara.put("SUBSYS_CODE", "CSM");
            commpara.put("PARAM_ATTR", "5544");
            commpara.put("PARAM_CODE", "SHARE");
            commpara.put("PARA_CODE1", productID);
            IDataset commparaDs = CommparaInfoQry.getCommparaInfoBy1To7(commpara);
            IDataset MemberData = ShareInfoQry.queryMemberRela(userId, "01");
            if(IDataUtil.isNotEmpty(commparaDs) && IDataUtil.isNotEmpty(MemberData))
            {
                IDataset mebList = ShareInfoQry.queryRelaByShareIdAndRoleCode(MemberData.getData(0).getString("SHARE_ID"), "02");
                //添加重复取消校验
                if(IDataUtil.isNotEmpty(mebList))
                {
                	IDataset mebs = new DatasetList();
                	IDataset memberdatas = new DatasetList();
                	boolean flag = false;
                	for(int i=0;i<mebList.size();i++)
                	{
                		IData meb = mebList.getData(i);
                		if(SysDateMgr.compareTo(meb.getString("START_DATE"), SysDateMgr.getSysDateYYYYMMDDHHMMSS()) >0)
                		{
                            String instId = meb.getString("INST_ID");
                            // 删除的号码
                            IData delMeb = new DataMap();
                            delMeb.put("SERIAL_NUMBER_B", meb.getString("SERIAL_NUMBER"));
                            delMeb.put("tag", "1");
                            delMeb.put("INST_ID", instId);
                            mebs.add(delMeb);
                		}
                		
                		String rsrvTag1 = meb.getString("RSRV_TAG1","");
//                		if("80003014".equals(productID) && "1".equals(rsrvTag1))
//                		{
//                			IDataset uuDs = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userId, "56", "1");
//                			if(IDataUtil.isNotEmpty(uuDs))
//                			{
//                				IDataset uuDb = RelaUUInfoQry.qryRelaUUBySerNumAndSerNumB(uuDs.getData(0).getString("SERIAL_NUMBER_A"), meb.getString("SERIAL_NUMBER"), "56");
//                				if(IDataUtil.isNotEmpty(uuDb))
//                				{
//                					flag = true;
//                					IData membone = new DataMap();
//                    				membone.put("SERIAL_NUMBER_B", meb.getString("SERIAL_NUMBER"));
//                    				//membone.put("START_DATE", rsrvStr3);
//                    				membone.put("END_DATE", SysDateMgr.getSysDate());
//                    				membone.put("MODIFY_TAG", "1");
//                    				memberdatas.add(membone);
//                				}
//                			}            	
//                		}
                	}
                	IData params = new DataMap();
                	// 调用业务服务
                    params.put("SERIAL_NUMBER", mainSn);
                    params.put("MEB_LIST", mebs);
                    params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
                    IDataset rtDataset = CSAppCall.call("SS.ShareMealRegSVC.tradeReg", params);
                    
//                    if("80003014".equals(productID) && flag)
//                    {
//                    	IData tradeData = new DataMap();
//        				tradeData.put("SERIAL_NUMBER", mainSn);
//        				tradeData.put("AUTH_SERIAL_NUMBER", mainSn);
//        				tradeData.put("MEMBER_DATAS", memberdatas);
//        				tradeData.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
//        				tradeData.put("REMARK", "158不限量套餐连带取消"+tradeId);
//        				IDataset dataset = CSAppCall.call("SS.FamilyUnionPayRegSVC.tradeReg", tradeData);
//                    }      
                }   
            }
    	}
    }
}
