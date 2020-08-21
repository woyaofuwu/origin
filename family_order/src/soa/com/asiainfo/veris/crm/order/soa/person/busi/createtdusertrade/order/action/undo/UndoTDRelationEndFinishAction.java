
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 
 * BUG20180323113750_号码开户返销统一付费关系未解除
 * <br/>
 * @author zhuoyingzhi
 * 20180408
 * 返销业务处理统一付费关系
 *
 */
public class UndoTDRelationEndFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        
        String serialNumberB = mainTrade.getString("SERIAL_NUMBER");
        
        String relationTypeCode="56";
        //查询是否存在统一付费关系
        IDataset  relauuInfo=RelaUUInfoQry.queryRelaUUBySnb(serialNumberB, relationTypeCode);
        //System.out.println("-----UndoTDRelationEndFinishAction-----relauuInfo:"+relauuInfo);
        if(IDataUtil.isNotEmpty(relauuInfo)){
        	//查询主号下所有统一付费关系
        	String  userIdA=relauuInfo.getData(0).getString("USER_ID_A","");
        	//通过虚拟主号userid
        	IDataset relauuInfoAll=RelaUUInfoQry.getAllRelaUUInfoByUserIda(userIdA, relationTypeCode);
            //System.out.println("-----UndoTDRelationEndFinishAction-----relauuInfoAll:"+relauuInfoAll);
        	if(IDataUtil.isNotEmpty(relauuInfoAll)){
        		for(int i=0;i<relauuInfoAll.size();i++){
        			
        			IData info=relauuInfoAll.getData(i);
        			
        			//截止统一付费关系时间
					IData param = new DataMap();
					
						  //update条件
						  param.put("USER_ID_A", info.getString("USER_ID_A", ""));
						  param.put("USER_ID_B", info.getString("USER_ID_B", ""));
						  param.put("RELATION_TYPE_CODE", relationTypeCode);
						  param.put("ROLE_CODE_B", info.getString("ROLE_CODE_B", ""));
						  param.put("INST_ID", info.getString("INST_ID", ""));
						  
						  //截止时间
						  param.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
						  //RSRV_STR1
						  param.put("RSRV_STR1", "业务返销,截止统一付费关系时间");
						  
				     //System.out.println("-----UndoTDRelationEndFinishAction-----param:"+param);
				     Dao.executeUpdateByCodeCode("TF_F_RELATION_UU", "UPD_END_BY_INFO", param);
        		}
        	}
        }
    }

}
