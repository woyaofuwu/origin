
package com.asiainfo.veris.crm.order.soa.person.busi.plat.feeprotect;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class FeeProtectBean extends CSBizBean
{
	/**
     * 查询用户办理过的保护资费
     * 
     * @param cycle
     * @return 
     * @throws Exception
     */
    public IDataset getFeeProtectDiscntInfo(IData userInfo) throws Exception
    {
        String userId = userInfo.getString("USER_ID");
        UcaDataFactory.getUcaByUserId(userId);
        IDataset infos =new DatasetList();
        //查询包下所有优惠    
		IDataset  upackageEleInfos=UPackageElementInfoQry.getElementInfoByGroupId("99992222");
		//获取用户的所有的优惠
		IDataset  userDiscntsInfos= UserDiscntInfoQry.getAllValidDiscntByUserId(userId);//获取用户的优惠
		IData userDiscnts = new DataMap(); 
		String discnts = "";
		for(int j=0; j<userDiscntsInfos.size();j++){ 	  
			discnts = userDiscntsInfos.getData(j).getString("DISCNT_CODE") + "_" + discnts;
			userDiscnts.put(userDiscntsInfos.getData(j).getString("DISCNT_CODE"), userDiscntsInfos.getData(j));
		}        
		//循环获取用户已订购的增值保护开关信息
       for(int k=0; k<upackageEleInfos.size();k++){
	       IData tempdis=new DataMap();
	       String  prarmdiscode=upackageEleInfos.getData(k).getString("OFFER_CODE");
		   if(discnts.contains(prarmdiscode)){
    	    	 upackageEleInfos.getData(k).put("CHECKED", "true");
    	    	 upackageEleInfos.getData(k).put("OLD_CHECKED", "true");
    	    	 upackageEleInfos.getData(k).put("HAS_DEAL", true);
    	    	 tempdis.putAll(upackageEleInfos.getData(k));
    	    	 tempdis.put("INST_ID", userDiscnts.getData(prarmdiscode).getString("INST_ID"));
    	    	 tempdis.put("START_DATE", userDiscnts.getData(prarmdiscode).getString("START_DATE"));
    	    	 tempdis.put("END_DATE", userDiscnts.getData(prarmdiscode).getString("END_DATE"));
    	    	 
    	    	 infos.add(tempdis);
    	     }else{
    	    	 upackageEleInfos.getData(k).put("CHECKED", "false");
    	    	 upackageEleInfos.getData(k).put("OLD_CHECKED", "false");
    	    	 upackageEleInfos.getData(k).put("HAS_DEAL", true);
    	    	 tempdis.putAll(upackageEleInfos.getData(k));
    	    	 tempdis.put("INST_ID", "0");
    	    	 tempdis.put("START_DATE", SysDateMgr.getSysTime());
    	    	 tempdis.put("END_DATE", SysDateMgr.getTheLastTime());
	    		 infos.add(tempdis);
    	     }
	    }        
        return infos;
    }
}
