package com.asiainfo.veris.crm.order.soa.person.busi.check4g;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.PersonUtil;
import com.asiainfo.veris.crm.order.soa.person.rule.run.productchange.Check4GUserCommRule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public  class Check4GUserCommSVC extends CSBizService {  
	private static final long serialVersionUID = 1L;

    /**
     * @Description: 是否是4G用户、是否存在4G互斥优惠
     * @param elementId
     * @return
     * @throws Exception
     * @author: wujj
     * @date: May 23, 2014 5:18:41 PM
     */
  
    public IDataset judge4Gfor1008611(IData inbuf) throws Exception
    {
    	IDataset outDataset = new DatasetList();
    	String  serialNumber = "";
    	String  userId ="";
    	serialNumber = inbuf.getString("SERIAL_NUMBER"); 
    	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
    	
    	userId  =ucaData.getUser().getUserId();
    	//TODO:返回 字段 4GFLAG: 1 表示能办理4G业务 0表示不能办理4G业务
    	//TODO:1判断用户是否4G卡用户,如不是4G卡用户返回0
    	
    	Check4GUserCommRule rule = new Check4GUserCommRule();
    	boolean is4GCard = false;
    	boolean isMutex = false;
    	IData outData = new DataMap();
    	is4GCard = rule.isLteCardUser(userId);
    	if(!is4GCard){
    		outData.put("4GFLAG","0");
    		outDataset.add(outData);
    		return outDataset;
    		//TODO:判断结束，返回标识
    	}
    	
    	//TODO:获取用户所有的优惠，循环判断是否与4G业务有冲突
    	List<DiscntTradeData> discnts= ucaData.getUserDiscnts();
        IDataset commpara8550 = CommparaInfoQry.getCommPkInfo("CSM", "8550", "4G",CSBizBean.getTradeEparchyCode());//获得用户所有4G互斥优惠
        
    	for(int i = 0; i < discnts.size();i ++)
    	{
    		for(int  j =0 ; j<commpara8550.size();j++)
    		{
    			String  distinct4GString = commpara8550.getData(j).getString("PARA_CODE1");
    			if(discnts.get(i).getDiscntCode().equals(distinct4GString))
    			{
    				isMutex = true;
    				break;
    			}
    		}
    		
    	}
		if(!isMutex)
		{
    		//TODO:判断结束，返回标识  如果是4G卡且不存在互斥
			outData.put("4GFLAG","1");
    		outDataset.add(outData);
    		return outDataset;
	      }
		
		outData.put("4GFLAG","0");
		outDataset.add(outData);
		return outDataset;
    }
   
}
