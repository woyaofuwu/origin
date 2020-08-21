
package com.asiainfo.veris.crm.order.soa.group.groupintf.credit;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;
import com.asiainfo.veris.crm.order.soa.group.groupintf.credit.esop.EsopCreditBean;

public class CreditLineRegBean extends GroupOrderBaseBean
{
    public void actOrderDataOther(IData map) throws Exception
    {
        //System.out.println("fufn20180312 actOrderDataOther-map:"+map);
        // 创建集团用户信息
        int pfWait = 0;
        String mainTradeId = "";
        String userId = "";
        String serialNumber = "";
        //取集团产品编码
        String productId = map.getString("PRODUCT_ID");
        if("97011".equals(productId) || "97012".equals(productId)) {
            IDataset grpProducts = UProductMebInfoQry.queryGrpProductInfosByMebProductId(productId);
            if(IDataUtil.isEmpty(grpProducts)) {
                CSAppException.apperr(GrpException.CRM_GRP_713, "根据成员产品编码【" + productId + "】,没有找到对应的集团产品编码！");
            }
            productId = grpProducts.first().getString("PRODUCT_ID");
        }
        map.put("PRODUCT_ID", productId);
        
        //判断是否为VOIP专线
        boolean isVoipLine = "7010".equals(productId);

        String changeMode =  "";
        String TradeTypeCode = map.getString("TRADE_TYPE_CODE","");
        map.put("OLD_TRADE_TYPE_CODE", TradeTypeCode);
        IData esopData = map.getData("ESOP");
        String subscribeId = "";
        if(DataUtils.isNotEmpty(esopData)) {
            subscribeId = esopData.getString("IBSYSID", "");
        }
        String serialNo="";
        if(subscribeId!=null&&!subscribeId.equals("")){
        	serialNo="ESOP"+subscribeId+"1";
        }
        //System.out.print("fufn20180416 subscribeId"+subscribeId+" serialNo"+serialNo);
        //集团专线要求按线停开机
        String productNo = "";
        String lineNos = "";
        IData param = new DataMap();
        param.put("USER_ID", map.getString("USER_ID", ""));
        param.put("TRADE_TYPE_CODE", TradeTypeCode);
        EsopCreditBean bean = new EsopCreditBean();
        IDataset result = bean.queryPauseBackLines(param);
        if(result != null && result.size() > 0) {
            if(isVoipLine) {
                for (int i = 0; i < result.size(); i++) {
                    lineNos = lineNos + result.getData(i).getString("PRODUCT_NO", "") + ";";
                }
                if(lineNos.length() > 1) {
                    lineNos = lineNos.substring(0, lineNos.length() - 1);
                }
            } else {
                productNo = result.getData(0).getString("PRODUCT_NO", "");
            }

        }

        //判断是否开环
        map.put("PF_WAIT", pfWait);
        //不发服开
        map.put("OLCOM_TAG", 0);
        String sheetType = "";
        
        if ("7110".equals(TradeTypeCode) || "7220".equals(TradeTypeCode) || "7305".equals(TradeTypeCode))
        {
        	changeMode =  "信控停机";
            sheetType = "41";
        }
        else if ("7303".equals(TradeTypeCode) || "7304".equals(TradeTypeCode) || "7301".equals(TradeTypeCode)|| "7317".equals(TradeTypeCode))
        {
        	changeMode = "信控开机";
            sheetType = "42";
        }

        if(!isVoipLine) {
            IData maps = new DataMap();
            maps.put("PRODUCT_NO", productNo);
            maps.put("CHANGE_MODE", changeMode);
            maps.put("SERIALNO", serialNo);
            maps.put("SUBSCRIBE_ID", subscribeId);
            maps.put("CRMNO", subscribeId);
            map.put("OLCOM_TAG", 1);
            map.put("DATALINE", maps);
            map.put("SHEETTYPE", sheetType);
        }

        // 生成主用户台账
        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeUserDis, "CreateCreditClass");

        if(isVoipLine) {
            // 获取主用户TRADE
            List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();
            if(null != gbd && gbd.size() > 0) {
                IDataset user = gbd.get(0).getTradeUser();
                mainTradeId = user.getData(0).getString("TRADE_ID");
                userId = user.getData(0).getString("USER_ID");
                serialNumber = user.getData(0).getString("SERIAL_NUMBER");
            }
            // 解析专线数据
            if(lineNos != null && !lineNos.equals("") && lineNos.length() > 0) {
                String[] lineNo = lineNos.split(";");
                for (int i = 0; i < lineNo.length; i++) {
                    IData maps = new DataMap();
                    maps.put("PRODUCT_NO", lineNo[i]);
                    maps.put("CHANGE_MODE", changeMode);
                    maps.put("SERIALNO", serialNo);
                    maps.put("SUBSCRIBE_ID", subscribeId);
                    maps.put("CRMNO", subscribeId);
                    map.put("DATALINE", maps);//专线号
                    map.put("OLCOM_TAG", 1);
                    map.put("SHEETTYPE", sheetType);
                    //map.put("TRADE_STAFF_ID", "SUPERUSR");
        
                    //处理专线台账
                    GrpInvoker.ivkProduct(map, BizCtrlType.ChangeUserDis, "CreateLineClass");
                }
            }
            // 建立部分依赖关系
            List<BizData> bd = DataBusManager.getDataBus().getGrpBizData();
            for (int i = 0; i < bd.size(); i++) {
                IDataset other = bd.get(i).getTradeOther();
        
                if(null != other && other.size() > 0) {
                    DatalineUtil.createAllLimit(other.getData(0).getString("TRADE_ID"), mainTradeId);
        
                }
            }
        }

    }

    /**
     * VOIP专线（专网专线）集团产品受理业务类型
     */
    protected String setOrderTypeCode() throws Exception
    {
        return "2990";
    }

}
