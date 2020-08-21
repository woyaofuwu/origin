
package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.testcarduser.TestCardUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.testcarduser.requestdata.TestCardUserReqData;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * <br/>
 * @author zhuoyingzhi
 * 20160926
 * 
 */
public class TestCardUserTrade extends BaseTrade implements ITrade
{
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {   	
       this.createOtherTradeInfo( btd);
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {
    	TestCardUserReqData rd = (TestCardUserReqData) btd.getRD();
    	//UcaData ucaData = btd.getRD().getUca();     
    	//String user_id = btd.getRD().getUca().getUser().getUserId();
    	//String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	
    	String sn = rd.getSerialNumber();
    	UcaData uca = UcaDataFactory.getNormalUca(sn);
    	String user_id =uca.getUserId();
        IDataset list=TestCardUserBean.getUserOtherInfoByUserId(user_id);
        IData iData=new DataMap();
        if(IDataUtil.isNotEmpty(list)){
    	    iData=list.first();
    	    OtherTradeData otherTradeData =new OtherTradeData(iData);           
            //测试卡类型
            otherTradeData.setRsrvValue(rd.getRsrvValue());
            //测试卡类型名称
            String rsrvValueName="";
            if("1".equals(rd.getRsrvValue())){
            	rsrvValueName="不限制办理渠道";
            }else if("0".equals(rd.getRsrvValue())){
            	rsrvValueName="限制办理渠道";
            }
            otherTradeData.setRsrvStr2(rsrvValueName);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);           
            otherTradeData.setRemark("测试卡类型修改业务"); //备注       
            btd.add(sn, otherTradeData);
        
        }
    }
}
