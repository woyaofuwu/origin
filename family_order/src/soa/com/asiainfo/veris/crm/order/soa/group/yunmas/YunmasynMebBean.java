
package com.asiainfo.veris.crm.order.soa.group.yunmas;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;


public class YunmasynMebBean extends MemberBean
{
    protected YunmasynMebReqData reqData = null;

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        //super.actTradeSub();
        this.addTradeBlackwhite(reqData.getblackWhite());
        

        // 成员号码统付
    }
    
    protected BaseReqData getReqData() throws Exception
    {
        return new YunmasynMebReqData();
    }
    
    protected void setTradeBase() throws Exception
    {
        super.setTradeBase();
    }
    
    protected void chkTradeBefore(IData map) throws Exception
    {

    }
    
    protected void makUca(IData map) throws Exception
    {
       makUcaForMeb(map);
    }
    
    protected  void makUcaForMeb(IData map) throws Exception
    {
        String serialNumber = map.getString("SERIAL_NUMBER");

        UcaData uca = DataBusManager.getDataBus().getUca(serialNumber);

        if (uca == null)
        {
        	//QR-20200420-02 MAS成员导入处理失败  当生成的4694商品工单未完工时，生成4699会报用户资料找不到的错误导致无法完工
        	IData userData = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, null);
        	if (IDataUtil.isEmpty(userData))
            {
        		StringBuilder buf = new StringBuilder();
        		IData param = new DataMap();
                param.put("SERIAL_NUMBER", serialNumber);
                buf.append(" SELECT * FROM TF_B_TRADE_USER T ");
                buf.append(" WHERE T.SERIAL_NUMBER=:SERIAL_NUMBER ");
                IDataset userIds = Dao.qryBySql(buf, param,Route.getJourDb());
                if(IDataUtil.isNotEmpty(userIds)){
                	userData = userIds.getData(0);
                	uca = new UcaData();
                	uca.setUser(new UserTradeData(userData));
                }
            }
        	else
        	{
        		uca = UcaDataFactory.getNormalUcaForGrp(serialNumber);
        	}
        }

        reqData.setUca(uca);
        IData grpData = new DataMap();
        grpData.put("USER_ID", map.getString("EC_USER_ID"));
        UcaData grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(grpData);
        reqData.setGrpUca(grpUCA);

    }



    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData =  (YunmasynMebReqData) getBaseReqData();
    }
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setblackWhite(map);
    }
    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "4699";
    }

    @Override
    protected String setOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "4699";
    }
    @Override
    protected String setTradeId() throws Exception
    {

        // 生成业务流水号
        String id = SeqMgr.getTradeId();

        return id;
    }
}
