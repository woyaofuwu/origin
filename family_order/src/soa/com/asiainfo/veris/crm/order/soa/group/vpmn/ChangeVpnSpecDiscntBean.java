
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangeVpnSpecDiscntBean extends ChangeMemElement
{
	private static final Logger logger = Logger.getLogger(ChangeVpnSpecDiscntBean.class);
	
    private ChangeVpnSpecDiscntReqData reqData = null;

    private String smsType = "VpnMebChgDiscnt"; // 短信类型

    private String discntCode = "";
    
    private String discntName = "";

    private String discntNameOld = "";
    
    private String starDate = "";
    
    public ChangeVpnSpecDiscntBean()
    {

    }

    /**
     * 生成登记信息
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author lixiuyu@20101115
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDiscnt();
        infoRegTradeSvc();
        if (reqData.getHas655())
        {
            infoRegDataOther();
        }
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeVpnSpecDiscntReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeVpnSpecDiscntReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        // 判断是否发短信 start
        boolean isSms = map.getBoolean("IF_SMS", true);
        discntCode = map.getString("DISCNT_CODE");
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
        if (IDataUtil.isNotEmpty(memberDiscntOrder))
        {
            for (int i = 0; i < memberDiscntOrder.size(); i++)
            {
                IData delDiscntData = (IData) memberDiscntOrder.get(i);
                String discnt_code = delDiscntData.getString("DISCNT_CODE");
                if (!discntCode.equals(discnt_code)){
                    discntNameOld = UDiscntInfoQry.getDiscntNameByDiscntCode(discnt_code);  
                }
         
            }
        }
       
        if (!StringUtils.isBlank(discntCode))
        {
        	discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);  
        }
        logger.info("test_guonj_ChangeVpnSpecDiscntBean_makReqData_test_guonj.discntName="+discntName+"; "+"discntCode="+discntCode + "; "+"discntNameOld="+discntNameOld);      
        if (!StringUtils.isBlank(discntName))
        {
            isSms = true;
        }
        reqData.setNeedSms(isSms); // 是否发短信

        reqData.setDISCNT_CODE(discntCode);
        reqData.setHas655(false); // 初始给false
        
        // 判断是否发短信 end

    }

    /**
     * 添加优惠
     * 
     * @throws Exception
     */
    public void infoRegDiscnt() throws Exception
    {
        IDataset grpUserDis = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(reqData.getGrpUca().getUserId(), null);
        if (IDataUtil.isEmpty(grpUserDis))
        {
            CSAppException.apperr(GrpException.CRM_GRP_669);
        }
        IDataset discntsets = new DatasetList();
        IData discntData = new DataMap();

        for (int i = 0; i < grpUserDis.size(); i++)
        {
            IData dicntInfo = grpUserDis.getData(i);
            if (reqData.getDISCNT_CODE().equals(dicntInfo.getString("ELEMENT_ID")))
            {
                discntData.put("DISCNT_CODE", reqData.getDISCNT_CODE());
                discntData.put("USER_ID", reqData.getUca().getUserId());
                discntData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                discntData.put("PRODUCT_ID", dicntInfo.getString("PRODUCT_ID"));
                discntData.put("PACKAGE_ID", dicntInfo.getString("PACKAGE_ID"));
                discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                discntData.put("START_DATE", this.getAcceptTime());
                discntData.put("END_DATE", dicntInfo.getString("END_DATE"));
                discntData.put("INST_ID", SeqMgr.getInstId());
                discntData.put("REMARK", "批量办理VPMN套餐");
                discntsets.add(discntData);
            }

        }
        if (IDataUtil.isEmpty(discntData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_666, reqData.getDISCNT_CODE());
        }
        IDataset memberDiscntOrder = UserDiscntInfoQry.getUserProductDis(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
        if (IDataUtil.isNotEmpty(memberDiscntOrder))
        {
            String lastTimeThisAcct = DiversifyAcctUtil.getLastTimeThisAcctday(reqData.getUca().getUserId(), null); // 本账期最后时间
            String firstDayNextAcct = DiversifyAcctUtil.getFirstDayNextAcct(reqData.getUca().getUserId()); // 下账期第一天
            for (int i = 0; i < memberDiscntOrder.size(); i++)
            {
                IData delDiscntData = (IData) memberDiscntOrder.get(i);
                String delDiscntCode = delDiscntData.getString("DISCNT_CODE");
                if (reqData.getDISCNT_CODE().equals(delDiscntCode))
                {
                    CSAppException.apperr(GrpException.CRM_GRP_665, reqData.getDISCNT_CODE());
                }

                if (!"493".equals(delDiscntCode))
                {
                    if ("1285".equals(discntCode) || "1286".equals(discntCode) || "1391".equals(discntCode))
                    {
                        // 分散账期修改
                        delDiscntData.put("END_DATE", lastTimeThisAcct);
                        delDiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        // 分散账期修改
                        discntData.put("START_DATE", firstDayNextAcct);
                    }
                    else
                    {
                        delDiscntData.put("END_DATE", this.getAcceptTime());
                        delDiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    }
                    delDiscntData.put("REMARK", "批量办理VPMN套餐");
                    // 分散账期修改 表示分散账期已经处理,不需要 公用逻辑处理
                    delDiscntData.put("DIVERSIFY_ACCT_TAG", "1");
                    discntsets.add(delDiscntData);
                }
                if ("655".equals(discntCode))
                {
                    reqData.setHas655(true); // 有delete 655集团成员资费的时候，插other
                    IDataset discnts = UserDiscntInfoQry.getAllValidDiscntByUserId(reqData.getUca().getUserId());
                    if (IDataUtil.isNotEmpty(discnts))
                    {
                        for (int j = 0; j < discnts.size(); j++)
                        {
                            IData discnt = (IData) discnts.get(j);
                            String discnt_code = discnt.getString("DISCNT_CODE");
                            if ("1401".equals(discnt_code) || "1402".equals(discnt_code) || "1403".equals(discnt_code) || "4807".equals(discnt_code))
                            {
                                discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                // 分散账期修改
                                discnt.put("END_DATE", lastTimeThisAcct);
                                // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                                discnt.put("DIVERSIFY_ACCT_TAG", "1");
                                discnt.put("REMARK", "批量办理VPMN套餐");
                                discntsets.add(discnt);
                            }
                        }
                    }
                }
            }
        }
        this.addTradeDiscnt(discntsets);
        if (IDataUtil.isNotEmpty(discntsets))
        {
        	for (int i = 0; i < discntsets.size(); i++)
            {
                IData dicntInfo = discntsets.getData(i);
                if (discntCode.equals(dicntInfo.getString("DISCNT_CODE")))
                {
                	starDate = dicntInfo.getString("START_DATE");
                	break;
                }

            }
        }
        logger.info("test_guonj_ChangeVpnSpecDiscntBean_makReqData.infoRegDiscnt.starDate="+starDate+"; discntCode="+discntCode); 
    }

    public void infoRegTradeSvc() throws Exception
    {
        String grpUserId = reqData.getGrpUca().getUserId();
        String memUserId = reqData.getUca().getUserId();

        IDataset svcds = new DatasetList();
        // 查询成员订购的服务资费
        IDataset memberSvcOrder = UserSvcInfoQry.getUserProductSvc(memUserId, grpUserId, null);
        if (IDataUtil.isNotEmpty(memberSvcOrder))
        {
            for (int i = 0; i < memberSvcOrder.size(); i++)
            {
                IData svcData = (IData) memberSvcOrder.get(i);
                if ("860".equals(svcData.getString("SERVICE_ID")))
                {
                    svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    svcData.put("ELEMENT_ID", "860");
                    // 分散账期修改 表示分散账期已经处理,不需要在GroupBaseBean中的公用逻辑处理
                    svcData.put("DIVERSIFY_ACCT_TAG", "1");
                    svcds.add(svcData);
                }
            }
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_173);
        }
        if (IDataUtil.isEmpty(svcds))
        {
            // common.error("1111", "成员没有订购VPMN成员服务包中的860服务或者860服务已失效，业务不嫩继续！");
            CSAppException.apperr(VpmnUserException.VPMN_USER_174);
        }
        addTradeSvc(svcds);

    }

    /**
     * 生成Other表数据
     * 
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset userOther = UserOtherInfoQry.getUserOther(reqData.getUca().getUserId(), "CHNL");
        if (IDataUtil.isNotEmpty(userOther))
        {
            IDataset dataset = new DatasetList();
            String lastTimeThisAcct = DiversifyAcctUtil.getLastTimeThisAcctday(reqData.getUca().getUserId(), null); // 本账期最后时间
            for (int j = 0; j < userOther.size(); j++)
            {
                IData data = (IData) userOther.get(j);
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                // 分散账期修改 获取成员账期信息 add start
                data.put("END_DATE", lastTimeThisAcct);
                // add end
                dataset.add(data);
            }
            addTradeOther(dataset);
        }
    }

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        data.put("RSRV_STR1", reqData.getUca().getUser().getUserId());
        data.put("RSRV_STR2", relationTypeCode);
        data.put("RSRV_STR3", reqData.getUca().getSerialNumber());
        // 查询UU关系表
        IData uuInfo = RelaUUInfoQry.getRelaByPK(reqData.getGrpUca().getUserId(), reqData.getUca().getUser().getUserId(), relationTypeCode); // TF_F_RELATION_UU", "SEL_BY_PK
        if (IDataUtil.isEmpty(uuInfo))
        {
            return;
        }
 
        // 短信start
        data.put("RSRV_STR6", smsType);// 短信类型

        if (!StringUtils.isBlank(discntName))
        {
            data.put("RSRV_STR9", discntName);
            data.put("RSRV_STR10", discntNameOld);
        	data.put("RSRV_STR6", "VpnMebChgDiscnt");// 短信类型
        }
        data.put("RSRV_STR5", starDate);// 短信类型
        //data.put("RSRV_STR7", NOTICE_CONTENT);
        //data.put("RSRV_STR8", nextMonthFirstTime.substring(0, 10));
        // 短信end
    }
    
}
