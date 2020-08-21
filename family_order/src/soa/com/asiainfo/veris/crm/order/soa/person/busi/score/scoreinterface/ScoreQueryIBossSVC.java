
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreinterface.CheckIdentityUtil;

public class ScoreQueryIBossSVC extends CSBizService
{
    public IData scoreQryDetails(IData inparam) throws Exception
    {
    	IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
    	
    	//积分明细鉴权
    	CheckIdentityUtil.checkIdentitySvc(inparam, inparam.getString("SERIAL_NUMBER"));
    	
        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        IData temp = AcctCall.queryScoreDetailToScorePla(inparam.getString("STARTTIME",""), inparam.getString("ENDTIME",""),"1",userInfo.getString("USER_ID"));
        
        IData data = new DataMap();
	    data.put("RSRV_STR1", inparam.getString("RSRV_STR1"));
	    
	    IDataset releaseTime = new DatasetList();
        IDataset pointType = new DatasetList();
        IDataset releasePoint = new DatasetList();
        IDataset validateTime = new DatasetList();
        IDataset comments = new DatasetList();
        if (IDataUtil.isNotEmpty(temp))
        {
            IDataset scoreInfos = temp.getDataset("SCORE_INFO");
            if(IDataUtil.isNotEmpty(scoreInfos)){
            	int size = scoreInfos.size();
            	for(int i=0;i<size;i++){
            		IData scoreInfo=scoreInfos.getData(i);
            		releaseTime.add(scoreInfo.getString("TRADE_DATE", ""));
            		pointType.add(scoreInfo.getString("SCORE_TYPE_CODE", ""));
            		releasePoint.add(scoreInfo.getString("SCORE_VALUE", ""));
            		validateTime.add(scoreInfo.getString("VALID_DATE", ""));
            		comments.add(scoreInfo.getString("DETAIL_ITEM", ""));
            	}
            	
            }
        }
        data.put("RELEASE_TIME", releaseTime);
	    data.put("RELEASE_POINTTYPE", pointType);
	    data.put("RELEASE_POINT", releasePoint);
	    data.put("VALIDATE_TIME", validateTime);
	    data.put("COMMENTS", comments);
		
	    data.put("X_RESULTCODE", "0");
	    data.put("X_RESULTINFO", "OK!");
        return data;
    }

    public IData scoreQryUseDetails(IData inparam) throws Exception
    {
    	IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
    	
    	//积分使用明细鉴权
    	CheckIdentityUtil.checkIdentitySvc(inparam, inparam.getString("SERIAL_NUMBER"));
    	
        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userInfo)){
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        IData temp = AcctCall.queryScoreDetailToScorePla(inparam.getString("STARTTIME",""), inparam.getString("ENDTIME",""),"2",userInfo.getString("USER_ID"));
        
        IData data = new DataMap();
	    data.put("RSRV_STR1", inparam.getString("RSRV_STR1"));
	    
	    IDataset useTime=new DatasetList();
        IDataset usePoint=new DatasetList();
        IDataset comments=new DatasetList();
        IDataset useWayCode=new DatasetList();
        IDataset useWayDesc=new DatasetList();
        if (IDataUtil.isNotEmpty(temp))
        {
            IDataset scoreInfos = temp.getDataset("SCORE_INFO");
            if(IDataUtil.isNotEmpty(scoreInfos)){
            	int size = scoreInfos.size();
            	for(int i=0;i<size;i++){
            		IData scoreInfo=scoreInfos.getData(i);
            		useTime.add(scoreInfo.getString("TRADE_DATE", ""));
            		usePoint.add(scoreInfo.getString("SCORE_VALUE", ""));
            		comments.add(scoreInfo.getString("DETAIL_ITEM", ""));
            		String svcTypeCode=scoreInfo.getString("SVC_TYPE_CODE", "");
            		if("01".equals(svcTypeCode)){
            			useWayCode.add("00");
                		useWayDesc.add("积分消费");
            		}else if("02".equals(svcTypeCode)){
            			useWayCode.add("01");
                		useWayDesc.add("积分转赠（转出）");
            		}else{
            			useWayCode.add("02");
                		useWayDesc.add("其它用途【省BOSS根据情况填写具体原因】");
            		}
            	}
            	
            }
        }
        data.put("USE_TIME", useTime);
	    data.put("USE_POINT", usePoint);
	    data.put("COMMENTS", comments);
	    data.put("USE_WAYCODE", useWayCode);
	    data.put("USER_WAYDESC", useWayDesc);
	    data.put("X_RESULTCODE", "0");
	    data.put("X_RESULTINFO", "OK!");
        return data;
    }
}
