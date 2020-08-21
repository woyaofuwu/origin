package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyUnionPayBean;

public class FamilyUnionPayBusiTpDealSVC extends ReqBuildService {

    // 统一付费关系
    private final String relationTypeCode = "56";

    @Override
    public IData initReqData(IData input) throws Exception {
        FamilyUnionPayBean bean = BeanManager.createBean(FamilyUnionPayBean.class);
        //1、根据号码判断是主卡还是副卡
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset uuList = RelaUUInfoQry.getRelaUUInfoByRol(ucaData.getUserId(), relationTypeCode);
        if (IDataUtil.isEmpty(uuList)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_730);
        }

        //2、如果是主卡则取消下面全部副卡；3、如果是副卡则取消当前号码本身
        IData svcParam = new DataMap();
        IData uu = uuList.first();
        String roleCodeB = uu.getString("ROLE_CODE_B");
        if("1".equals(roleCodeB)) {//主卡
            input.put("USER_STATE_CODESET",ucaData.getUser().getUserStateCodeset());
            input.put("USER_ID",ucaData.getUser().getUserId());
            IDataset dataset = bean.loadChildTradeInfo(input);
            if(DataUtils.isEmpty(dataset)){
                CSAppException.apperr(TpOrderException.TP_ORDER_40000);
            }
            IDataset memberList = dataset.getData(0).getDataset("QRY_MEMBER_LIST");
            IData famPara = dataset.getData(0).getData("FAM_PARA");
            svcParam = getIntfMsg(serialNumber,famPara,memberList);
        }else if("2".equals(roleCodeB))  {//副卡
            //获取主卡下的成员信息
            IData data = new DataMap();
            String userIdA = uu.getString("USER_ID_A");
            IDataset uuInfos = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA,"1",relationTypeCode);
            if(DataUtils.isEmpty(uuInfos)){
                CSAppException.apperr(TpOrderException.TP_ORDER_40000);
            }
            String mainCardNumber = uuInfos.first().getString("SERIAL_NUMBER_B");
            UcaData ucaMainCardData = UcaDataFactory.getNormalUca(mainCardNumber);
            data.put("SERIAL_NUMBER",mainCardNumber);
            data.put("USER_ID",ucaMainCardData.getUser().getUserId());
            data.put("USER_STATE_CODESET",ucaMainCardData.getUser().getUserStateCodeset());

            IDataset dataset = bean.loadChildTradeInfo(data);
            if(DataUtils.isEmpty(dataset)){
                CSAppException.apperr(TpOrderException.TP_ORDER_40000);
            }

            //去掉其他不需要移除的成员
            IDataset memberList = dataset.getData(0).getDataset("QRY_MEMBER_LIST");
            if(DataUtils.isNotEmpty(memberList)){
                for(int i = 0; i < memberList.size();i++){
                    IData member = memberList.getData(i);
                    String serialNumberB = member.getString("SERIAL_NUMBER_B");
                    if(!serialNumber.equals(serialNumberB)){
                        memberList.remove(member);
                        i--;
                    }
                }
            }

            IData famPara = dataset.getData(0).getData("FAM_PARA");
            svcParam = getIntfMsg(mainCardNumber,famPara,memberList);

        }else{
            CSAppException.apperr(FamilyException.CRM_FAMILY_66000239,serialNumber,roleCodeB);
        }

        //4、返回参数报文
        return svcParam;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "325";
    }

    /**
     * 拼装接口数据
     * @param serialNumber
     * @param memberList
     * @param famPara
     * @return
     * @throws Exception
     */
    private IData getIntfMsg(String serialNumber,IData famPara,IDataset memberList) throws Exception {
        //1、成员集合数据重构
        if(DataUtils.isNotEmpty(memberList)){
            for (int i = 0; i < memberList.size();i++){
                IData member = memberList.getData(i);
                member.put("ORG_END_DATE",member.getString("END_DATE"));
                member.put("RSRV_TAG1",member.getString("RSRV_TAG1NAME"));
                member.put("NOW_ACCT_DAY",member.getString("ACCT_DAY"));
                member.put("MODIFY_TAG","1");
                member.put("END_DATE",famPara.getString("END_DATE_THIS_ACCT"));
            }
        }
        IData svcParam = new DataMap();
        svcParam.put("MEMBER_DATAS",memberList);
        svcParam.put("SERIAL_NUMBER",serialNumber);
        svcParam.put("START_CYCLE_ID",famPara.getString("START_CYCLE_ID"));
        svcParam.put("END_CYCLE_ID",famPara.getString("END_CYCLE_ID"));
        svcParam.put("END_DATE_THIS_ACCT",famPara.getString("END_DATE_THIS_ACCT"));
        svcParam.put("COMM_START_DATE",famPara.getString("COMM_START_DATE"));
        svcParam.put("COMM_END_DATE",famPara.getString("COMM_END_DATE"));
        svcParam.put("MAIN_ACCT_DAY",famPara.getString("MAIN_ACCT_DAY"));
        svcParam.put("LAST_TIME_THIS_MONTH",famPara.getString("LAST_TIME_THIS_MONTH"));
        svcParam.put("MEB_LIM",famPara.getString("MEB_LIM"));
        svcParam.put("PAYITEM_CODE",famPara.getString("PAYITEM_CODE"));
        svcParam.put("TRADE_TYPE_CODE","325");
        svcParam.put(Route.ROUTE_EPARCHY_CODE,getTradeEparchyCode());
        return svcParam;
    }

//    public IData testFamilyUnionPayBusiTpDeal(IData data)throws Exception{
//        IData param = new DataMap();
//        param.put("SERIAL_NUMBER","13866660022");
//        IData dd = createInfMessage(param);
//        ServiceResponse serviceResponse = BizServiceFactory.call("SS.FamilyUnionPayRegSVC.tradeReg",dd);
//        return serviceResponse.getBody();
//    }

}
