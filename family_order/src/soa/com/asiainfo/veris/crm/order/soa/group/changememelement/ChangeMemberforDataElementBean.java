package com.asiainfo.veris.crm.order.soa.group.changememelement;


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
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class ChangeMemberforDataElementBean extends GroupOrderBaseBean {

    public void actOrderDataOther(IData map) throws Exception {
    	int pfWait = 0;
        //取集团产品编码
    	String productId = map.getString("PRODUCT_ID");

        
        //判断是否为VOIP专线

//        String changeMode =  "";
//        String subscribeId = "";
        //集团专线要求按线停开机
        IData param = new DataMap();
        param.put("USER_ID", map.getString("USER_ID", ""));


        //判断是否开环
        map.put("PF_WAIT", pfWait);
        //不发服开
        map.put("OLCOM_TAG", 0);
//        String sheetType = "";
        
        IDataset lineInfos = new DatasetList(map.getString("LineInfos", ""));

       ;


            // 解析专线数据
            if(lineInfos != null  && lineInfos.size() > 0) {
                for (int i = 0; i < lineInfos.size(); i++) {
                    IData maps = new DataMap();
                    maps=lineInfos.getData(i);
//                    maps.put("PRODUCT_NO", lineNo[i]);
//                    maps.put("CHANGE_MODE", changeMode);
//                    maps.put("SUBSCRIBE_ID", subscribeId);
//                    maps.put("CRMNO", subscribeId);
                    map.put("DATALINE", maps);//专线号
//                    map.put("SHEETTYPE", sheetType);
                    map.put("USER_ID", maps.getString("USER_ID", ""));

                    
                  //政企订单中心新流程改造
            		map.put("EOS", new DatasetList(map.getString("EOSDATA", "")));
                    
                    //处理专线台账
                    if(productId.equals("7012")){
                        map.put("PRODUCT_ID", "97012");
                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("70121")){
                        map.put("PRODUCT_ID", "970121");

                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("70122")){
                        map.put("PRODUCT_ID", "970122");

                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("7011")){
                        map.put("PRODUCT_ID", "97011");

                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("70111")){
                        map.put("PRODUCT_ID", "970111");

                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("70112")){
                        map.put("PRODUCT_ID", "970112");

                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("7016")){
                        map.put("PRODUCT_ID", "97016");

                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeMemberDis, "ChangeLineDataClass");

                    }else if(productId.equals("7010")){
                        map.put("PRODUCT_ID", "7010");
                        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeUserDis, "ChangeLineDataClass");

                    }
                }
            }
            
         // 建立部分依赖关系  依赖一笔防止同时完工
            List<BizData> bd = DataBusManager.getDataBus().getGrpBizData();
            IDataset other0 =new DatasetList();
//    		System.out.println("ChangeMemberforDataElementBean-actOrderDataOther size:"+bd.size());
            for (int i = 0; i < bd.size(); i++)
            {
                IDataset other = bd.get(i).getTradeDataLine();
                
                if (null != other && other.size() > 0)
                {
                	if(i==0){
                		other0=other;
                	}else{
//                		System.out.println("ChangeMemberforDataElementBean-actOrderDataOther other0:"+other0.getData(0));
//                		System.out.println("ChangeMemberforDataElementBean-actOrderDataOther other:"+other.getData(0));
                		if(null != other0 && other0.size() > 0){
                            DatalineUtil.createLimit(other0.getData(0).getString("TRADE_ID"), other.getData(0).getString("TRADE_ID"));
                		}
                	}
                }
            }
    }

    @Override
    protected String setOrderTypeCode() throws Exception {
		return "0";
    }


}
