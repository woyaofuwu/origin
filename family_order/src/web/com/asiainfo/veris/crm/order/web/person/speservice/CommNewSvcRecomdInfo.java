
package com.asiainfo.veris.crm.order.web.person.speservice;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CommNewSvcRecomdInfo extends PersonBasePage
{
    private static final String QUERY_TAG = "109";

    /**
     * 主要用来获取页面初始化时的用户推荐业务信息
     * 
     * @data 2013-9-25
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        IData userInfo = new DataMap(pagedata.getString("HINT_INFO"));

        String userId = userInfo.getString("USER_ID");
        String sn = userInfo.getString("SERIAL_NUMBER");

        // 加载三户资料
        String open_date = userInfo.getString("OPEN_DATE", "").substring(0, 10);
        String current_date = SysDateMgr.getSysTime().toString().substring(0, 10);

        if (current_date.equals(open_date))
        {
            userInfo.put("TRADE_TYPE_CODE_A", "A");
        }

        IDataset RecomdList = new DatasetList();
        IData tempdata = new DataMap();

        tempdata.putAll(userInfo);
        RecomdList = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getRecomdInfo", tempdata);
        // IData aa = new DataMap();
        // aa.put("OBJECT_ID", "131");
        // aa.put("OBJECT_TYPE_DESC", "报停");
        // IData bb = new DataMap();
        // bb.put("OBJECT_ID", "60");
        // bb.put("OBJECT_TYPE_DESC", "客户资料变更");
        // IData cc = new DataMap();
        // cc.put("OBJECT_ID", "110");
        // cc.put("OBJECT_TYPE_DESC", "产品变更");
        // IData dd = new DataMap();
        // dd.put("OBJECT_ID", "100");
        // dd.put("OBJECT_TYPE_DESC", "开户");
        // RecomdList.add(aa);
        // RecomdList.add(bb);
        // RecomdList.add(cc);
        // RecomdList.add(dd);

        // 获取用户推荐集

        // 小栏框跳转地址
        IData inputParam = new DataMap();
        inputParam.put("QUERY_TAG", QUERY_TAG);
        inputParam.put("TRADE_TYPE_CODE", "00");
        inputParam.put("SERIAL_NUMBER", sn);
        IDataset commparaInfos = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getParaInfo", inputParam);

        String url = "";
        if (IDataUtil.isNotEmpty(commparaInfos))
        {
            IData commparaInfo = commparaInfos.getData(0);
            url = commparaInfo.getString("PARA_CODE20");
        }

        // 小栏框最大显示数
        inputParam.clear();
        inputParam.put("QUERY_TAG", QUERY_TAG);
        inputParam.put("TRADE_TYPE_CODE", "001");
        inputParam.put("SERIAL_NUMBER", sn);
        IDataset maxPushNumPara = CSViewCall.call(this, "SS.NewSvcRecomdInfoSVC.getParaInfo", inputParam);
        int maxPushNum = 3;
        if (IDataUtil.isNotEmpty(maxPushNumPara))
        {
            IData maxPushNumInfo = maxPushNumPara.getData(0);
            maxPushNum = Integer.valueOf(maxPushNumInfo.getString("PARA_CODE1"));
        }


        // 小栏框展示信息
        IDataset URLInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(RecomdList))
        {
        	boolean ascFlag = true;
        	for (int i = 0; i < RecomdList.size(); i++)
            {
        		IData recommInfo = RecomdList.getData(i);
        		String  priorityId = recommInfo.getString("PRIORITY_ID");
                if(StringUtils.isBlank(priorityId))
                {
                	ascFlag = false;
                }
            }
        	
        	if(ascFlag)
        	{
        		//根据优先级展示
            	DataHelper.sort(RecomdList, "PRIORITY_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            	
        	}
            int size = RecomdList.size();
            if (maxPushNum < size)
            {
                size = maxPushNum;
            }
            for (int i = 0; i < size; i++)
            {
                IData URLInfo = new DataMap();
                IData recomdInfo = RecomdList.getData(i);
        		String tradeTypeCode = recomdInfo.getString("TRADE_TYPE_CODE","");
        		String  recomdCityCode = recomdInfo.getString("CITY_CODE","");
        		String bpName  = recomdInfo.getString("BP_NAME","");
        			 
        				String cityCode = getVisit().getCityCode();
        				
        				if(StringUtils.isBlank(recomdCityCode) || "HNHN".equals(recomdCityCode) || "HNAN".equals(recomdCityCode)){
        				       
                                    URLInfo.put("NAME", bpName);
                                    URLInfo.put("URL", url);
                                    URLInfo.put("SERIAL_NUMBER", sn);
                                    URLInfos.add(URLInfo);
                                 
                           
        				    
        				}else{
        				    if(cityCode.equals(recomdCityCode)){
                                
                                    
                                        URLInfo.put("NAME", bpName);
                                        URLInfo.put("URL", url);
                                        URLInfo.put("SERIAL_NUMBER", sn);
                                        URLInfos.add(URLInfo);
                                     
                                   
        				    }
        				    
        				}
        				
                     
            		   
        		
        		 
        		
            }

        }

        setInfos(URLInfos);

    }

    public abstract void setInfos(IDataset infos);

}
