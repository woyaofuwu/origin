package com.asiainfo.veris.crm.order.soa.group.flow;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.FlowPlatCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean; 

public class GprsSaleOrderBean extends GroupBean
{
    private static final Logger log = Logger.getLogger(GprsSaleOrderBean.class);
    
	private IData paramData = new DataMap();// 参数数据
	
	private IDataset pckOrderset = new DatasetList();
	
	public static String TIME_FORMAT ="yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    
    public static String TIME_FORMAT2 ="yyyy-MM-dd HH:mm:ss";
    
	public GprsSaleOrderBean()
    {

    }
	
	/**
     * 其它台帐处理
     * 
     * @author sungq3
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        setTradeDatapckInStock();// 流量入库
        
        IData resultData = callFlowPlat();
        if(IDataUtil.isNotEmpty(resultData))
        {
           if(!"200".equals(resultData.getString("RESULTE_CODE")))
           {
               CSAppException.apperr(GrpException.CRM_GRP_901,resultData.getString("RESULTE_INFO",""));
           }
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_894);
        }
    }
	
    // 调用流量平台接口
    public IData callFlowPlat() throws Exception 
    {
        IDataset packageList = new DatasetList();
        String groupId = "";
        String transId =getTradeId();
        for (int i = 0;i<pckOrderset.size();i++)
        {
            IData tmpData = pckOrderset.getData(i);
            groupId = reqData.getUca().getCustGroup().getGroupId();
            String pckNum = tmpData.getString("DATAPCK_COUNT");
            String pckmoney = String.valueOf(Integer.parseInt((tmpData.getString("DATAPCK_PRICE")))*100);
            String pckGprs = tmpData.getString("DATAPCK_VALUE");
            String endDate = tmpData.getString("END_DATE");
            String endDatestr = endDate.substring(0, 10) + SysDateMgr.END_DATE;
            //转标准时间格式
            SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT2);
            Date  date = formatter.parse(endDatestr);
            
            //转带时区格式，流量平台用
            String endtimeStr = new SimpleDateFormat(TIME_FORMAT).format(date);
            
            IData packageData = new DataMap();
            packageData.put("PAK_NUM", pckNum); //流量包个数
            packageData.put("PAK_MONEY", pckmoney);  //流量包单价
            packageData.put("PAK_GPRS", pckGprs);    //每个流量包的流量
            packageData.put("PAK_END_DTAE", endtimeStr);//流量包失效时间
            
            packageList.add(packageData);
        }
        IData svcData = new DataMap();
        IData contData = new DataMap();
        contData.put("TRANS_IDO", transId);
        contData.put("GRP_ID", groupId);
        contData.put("ITEM", packageList);
        svcData.put("CONTENT", contData);
        return  FlowPlatCall.callHttpFlowPlat(svcData);
    }
    
    public void setTradeDatapckInStock() throws Exception
    {
        IDataset pkgOrderInfos = new DatasetList();
        
        for (int i = 0;i<pckOrderset.size();i++)
        {
            IData data = new DataMap();
            IData datapckTraffic = pckOrderset.getData(i);
            
            data.put("INST_ID", SeqMgr.getInstId());
            data.put("ID_TYPE", "G");//集团客户
            data.put("ID", reqData.getUca().getCustId());//集团客户CUST_ID
            data.put("DATAPCK_TYPE", "D");// 流量包类型：D流量包，S短信包，V语音包，M彩信包
            data.put("USER_ID",reqData.getUca().getUser().getUserId());
            data.put("DATAPCK_VALUE", datapckTraffic.getString("DATAPCK_VALUE"));
            data.put("DATAPCK_COUNT", datapckTraffic.getString("DATAPCK_COUNT"));
            String discnt=datapckTraffic.getString("DISCOUNT","");
            //log.info("("**********cxy********DISCOUNT="+discnt);
            if("".equals(discnt)){
                discnt="100";
            }
            data.put("RSRV_STR2", datapckTraffic.getString("DISCOUNT"));
            int iprice = Integer.parseInt((datapckTraffic.getString("DATAPCK_PRICE")))*100;
            int itotalfee =iprice* Integer.parseInt(datapckTraffic.getString("DATAPCK_COUNT"))*Integer.parseInt(discnt)/100;
            data.put("PRICE", iprice);
            data.put("TOTAL_FEE", itotalfee);
            data.put("START_DATE", SysDateMgr.getSysTime());
            data.put("END_DATE", datapckTraffic.getString("END_DATE"));
            data.put("MODIFY_TAG",TRADE_MODIFY_TAG.Add.getValue());
            pkgOrderInfos.add(data);
        }
        super.addTradeDataPckInStock(pkgOrderInfos);
    }
    
    
    protected void regTrade() throws Exception
    {
    	super.regTrade();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        paramData = map;
        
        IDataset pckOrders = new DatasetList(paramData.getString("DATAPCK_ORDER", ""));//订购的流量包
        pckOrderset.addAll(pckOrders);
        
    }

    protected void makUca(IData map) throws Exception
    {
    	this.makUcaForGrpNormal(map);
    }
    
    protected void regOrder() throws Exception
    {
    	super.regOrder();
        IData data = bizData.getOrder();

        data.put("CUST_ID", reqData.getUca().getCustomer().getCustId()); // 客户标识
        data.put("CUST_NAME", reqData.getUca().getCustomer().getCustName()); // 客户名称
        data.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getUca().getUser().getCityCode()); // 归属业务区
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "1111"; // 
    }
	
    
    @Override
    protected void setTradefeeSub(IData map) throws Exception
    {
        super.setTradefeeSub(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }
	
}
