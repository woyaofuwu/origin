package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

public class CreateVoipGroupUserNewBean extends GroupOrderBaseBean {

    public void actOrderDataOther(IData map) throws Exception {
        // 创建集团用户信息
        int pfWait = 1;
        String mainTradeId = "";
        String userId = "";
        String serialNumber = "";
        String productId = map.getString("PRODUCT_ID");
        String acctId = "";
        IDataset attrInternet = new DatasetList(map.getString("PRODUCT_PARAM_INFO"));
        //判断是否开环
        pfWait = DatalineUtil.getDatalineOrderPfWait(attrInternet);
        map.put("PF_WAIT", pfWait);

        // 生成主用户
        GrpInvoker.ivkProduct(map, BizCtrlType.CreateUser, "CreateUserClass");

        // 获取主用户TRADE
        List<BizData> gbd = DataBusManager.getDataBus().getGrpBizData();

        if(null != gbd && gbd.size() > 0) {
            IDataset user = gbd.get(0).getTradeUser();
            mainTradeId = user.getData(0).getString("TRADE_ID");
            userId = user.getData(0).getString("USER_ID");
            serialNumber = user.getData(0).getString("SERIAL_NUMBER");
            acctId =  gbd.get(0).getTrade().getString("ACCT_ID");
        }

        // 解析专线数据
        IDataset internet = DatalineUtil.parseDataInfo(attrInternet);

        IDataset commonData = DatalineUtil.parseCommonDataInfo(attrInternet);

        IDataset dataline = DatalineUtil.parseDataLineInfo(attrInternet);

        // 解析中继信息
        //IDataset internetzj = DatalineUtil.parseVoipZjInfo(attrInternet);

        // 生成专线用户
        //IData interzj = null;
        if(null != internet && internet.size() > 0) {

            for (int i = 0; i < internet.size(); i++) {
                IData inter = internet.getData(i);

                String lineNumberCode = inter.getString("pam_NOTIN_LINE_NUMBER_CODE");
                if(null != dataline && dataline.size() > 0) {
                    for (int j = 0; j < dataline.size(); j++) {
                        IData datas = dataline.getData(j);
                        String numberCode = datas.getString("pam_NOTIN_LINE_NUMBER_CODE");

                        if(numberCode.equals(lineNumberCode)) {
                            IData maps = new DataMap();
                            maps.put("ATTRINTERNET", inter);
                            maps.put("ATTRZJ", inter);
                            maps.put("DATALINE", datas);
                            maps.put("COMMON_DATA", commonData);
                            maps.put("CUST_ID", map.getString("CUST_ID"));
                            maps.put("USER_ID", userId);
                            maps.put("PRODUCT_ID", productId);
                            maps.put("SERIAL_NUMBER", serialNumber);
                            maps.put("TRADE_TYPE_CODE", map.getString("TRADE_TYPE_CODE"));
                            maps.put("ACCT_ID", acctId);
                            maps.put("PF_WAIT", pfWait);
                            EOS.getData(0).put("RSRV_STR6", Integer.parseInt(numberCode)+1);
                            maps.put("EOS", EOS);
                            GrpInvoker.ivkProduct(maps, BizCtrlType.CreateUser, "CreateClass");
                        }
                    }
                }
            }
        }

        // 建立部分依赖关系
        List<BizData> bd = DataBusManager.getDataBus().getGrpBizData();
        for (int i = 0; i < bd.size(); i++) {
            IDataset other = bd.get(i).getTradeOther();

            if(null != other && other.size() > 0) {

                //                DatalineUtil.createLimit(mainTradeId, other.getData(0).getString("TRADE_ID"));
                DatalineUtil.createAllLimit(other.getData(0).getString("TRADE_ID"), mainTradeId);

            }
        }

    }

    /**
     * VOIP专线（专网专线）集团产品受理业务类型
     */
    @Override
    protected String setOrderTypeCode() throws Exception {
        return "2990";
    }

}
