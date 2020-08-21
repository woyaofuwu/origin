
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.UUException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChangeVpnMemElement extends ChangeMemElement
{

    protected IData paramInfo = new DataMap(); // 产品参数

    private String smsType = "VpnMebChg"; // 短信类型

    private String discntName = "";

    private String discntNameOld = "";
    
    private IData discnt = new DataMap();
    
    private String resShortCode = "";

    private String nextMonthFirstTime = "";

    private boolean isNormalPackage = true; // 是否标准VPMN成员优惠包

    public ChangeVpnMemElement()
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
        paramInfo = getParamInfo();

        // 3/5/8元套餐间变更特殊处理
        dealSpecialDiscntData();
        // 655代理商套餐(VPMN JPA)处理
        infoDiscntDatadeal();
        // 联络员优惠处理
        dealLinkManDiscnt();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 短号码变更处理
        infoRegDataShortCode();
        // 655元素删除时操作
        infoRegDataOther();

        if (StringUtils.isNotBlank(resShortCode))
        {
            // shortCodeValidate(resShortCode);//第二次校验，觉得没有必要，先屏蔽
            // 有短号变更，操作短号临时表
            insertTemporaryShortCode();
        }
        
        
        infoRegSvcClipType();
    }

    /**
     * 增加短号进uu表
     * 
     * @throws Exception
     */
    @Override
    protected void actTradeRelationUU() throws Exception
    {
        if (IDataUtil.isNotEmpty(paramInfo))
        {
            String role_code_b = reqData.getMemRoleB();

            IDataset relaUUList = RelaUUInfoQry.getUUInfoByUserIdAB(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), "20"); // 变更的时候查UU关系必须有一条记录，这里不校验为空的情况，然这种情况报错，修数据
            if (IDataUtil.isEmpty(relaUUList))
            {
                CSAppException.apperr(UUException.CRM_UU_105, reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), "20");
            }
            IData relaUU = relaUUList.getData(0);
            String shortCode = paramInfo.getString("SHORT_CODE", "");
            String oldShortCode = paramInfo.getString("NOTIN_OLD_SHORT_CODE", "");
            boolean bool = false;
            if (StringUtils.isNotEmpty(role_code_b) && !role_code_b.equals(relaUU.getString("ROLE_CODE_B")))
            {
                relaUU.put("ROLE_CODE_B", role_code_b);
                bool = true;
            }
            if (StringUtils.isNotEmpty(role_code_b) && !oldShortCode.equals(shortCode))
            {
                relaUU.put("SHORT_CODE", shortCode);
                bool = true;
            }
            if (bool)
            {
                relaUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                super.addTradeRelation(relaUU);
                reqData.setIsChange(true);
            }
        }
    }

    /**
     * 变更成员角色，处理联络员的优惠 1-联系人,2-集团领导人;如果角色变更为2-集团领导人,则成员新增配置的该成员产品优惠,且查出配置中成员已订购的优惠结束掉
     * 
     * @author tengg
     * @throws Exception
     */
    public void dealLinkManDiscnt() throws Exception
    {
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        // 查询UU关系表
        IData uuInfo = RelaUUInfoQry.getRelaByPK(reqData.getGrpUca().getUserId(), reqData.getUca().getUser().getUserId(), relationTypeCode);
        if (IDataUtil.isEmpty(uuInfo))
        {
            CSAppException.apperr(UUException.CRM_UU_105, reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), relationTypeCode);
        }
        String oldRoleCodeB = uuInfo.getString("ROLE_CODE_B", "");
        String roleCodeB = reqData.getMemRoleB();
        IDataset discnts = reqData.cd.getDiscnt();
        if (!oldRoleCodeB.equals(roleCodeB) && "2".equals(roleCodeB)) // 如果角色变更为2-集团领导人
        {
            // 成员新增配置的该成员产品优惠
            IDataset datas = ParamInfoQry.getCommparaByCode("CSM", "6018", "", CSBizBean.getTradeEparchyCode());

            if (IDataUtil.isNotEmpty(datas))
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    IData discnt = new DataMap();
                    discnt.put("PRODUCT_ID", ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId()));
                    discnt.put("PACKAGE_ID", "-1");
                    discnt.put("ELEMENT_ID", datas.getData(i).getString("PARAM_CODE"));
                    discnt.put("DISCNT_CODE", datas.getData(i).getString("PARAM_CODE"));
                    
                    discnt.put("INST_ID", SeqMgr.getInstId());
                    discnt.put("START_DATE", this.getAcceptTime());
                    discnt.put("END_DATE", SysDateMgr.getTheLastTime());
                    discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                    discnt.put("DIVERSIFY_ACCT_TAG", "1");
                    discnts.add(discnt);
                }

            }
        }
        if (!oldRoleCodeB.equals(roleCodeB) && "2".equals(oldRoleCodeB))
        {
            // 查出配置中成员已订购的优惠结束掉
            IDataset datas = ParamInfoQry.getCommparaByCode("CSM", "6018", "", CSBizBean.getTradeEparchyCode());
            String memUserId = reqData.getUca().getUserId();
            if (IDataUtil.isNotEmpty(datas))
            {
                for (int i = 0; i < datas.size(); i++)
                {
                    IData data = (IData) datas.get(i);
                    IDataset discntInfos = UserDiscntInfoQry.getNowVaildByUserIdDiscnt(memUserId, data.getString("PARAM_CODE"));

                    if (IDataUtil.isNotEmpty(discntInfos))
                    {
                        IData discnt = discntInfos.getData(i);
                        discnt.put("ELEMENT_ID", discnt.getString("DISCNT_CODE"));
                        discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        discnt.put("END_DATE", this.getAcceptTime());
                        // 分散账期修改 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                        discnt.put("DIVERSIFY_ACCT_TAG", "1");
                        discnts.add(discnt);
                    }

                }

            }
        }
    }

    /**
     * 3/5/8元优惠之间变更，军网28元、48元套餐的月底失效处理
     * 
     * @throws Exception
     */
   
    public void dealSpecialDiscntData() throws Exception
    {                           
        IDataset discnts = reqData.cd.getDiscnt();
        String memUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        // 获取当前账期的最后一天
        // String newDate = DiversifyAcctUtil.getLastTimeThisAcctday(reqData.getUca().getUserId(), null);
        // 本账期最后时间
        String lastTimeThisAcctday = DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, SysDateMgr.getLastDateThisMonth());
        // 下账期第一时间
        String firstTimeNextAcct = DiversifyAcctUtil.getFirstTimeNextAcct(memUserId);
        IData delDiscnt = new DataMap(); // 删除的资费
        IData addDiscnt = new DataMap(); // 新增的资费
        boolean discntChange = true; // 没有新增资费则为true
        if (IDataUtil.isNotEmpty(discnts))
        {
            for (int i = 0; i < discnts.size(); i++)
            {
                IData data = discnts.getData(i);
                String packageId = data.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，防止获取集团VPMN成员产品跨省优惠包
                if (TRADE_MODIFY_TAG.Add.getValue().equals(data.getString("MODIFY_TAG")) && "80000102".equals(packageId))
                {
                    addDiscnt = discnts.getData(i);
                    discntChange = false;
                }
            }
            String addDiscntCode = ""; // 新增套餐的 discntcode

            if (IDataUtil.isNotEmpty(addDiscnt))  
            {
                addDiscntCode = addDiscnt.getString("ELEMENT_ID");
            }
            for (int i = 0; i < discnts.size(); i++)
            {
                IData data = discnts.getData(i);
                String packageId = data.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，防止获取集团VPMN成员产品跨省优惠包
                if (TRADE_MODIFY_TAG.DEL.getValue().equals(data.getString("MODIFY_TAG")) && "80000102".equals(packageId))
                {
                    delDiscnt = discnts.getData(i);
                    String delDiscntCode = delDiscnt.getString("ELEMENT_ID");
                    if (("1285".equals(delDiscntCode) || "1286".equals(delDiscntCode) || "1391".equals(delDiscntCode)) && ("1285".equals(addDiscntCode) || "1286".equals(addDiscntCode) || "1391".equals(addDiscntCode)))
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                        addDiscnt.put("START_DATE", firstTimeNextAcct);
                    }
                    if ("695".equals(delDiscntCode)||"696".equals(delDiscntCode))//军网28元、48元套餐注销后月底失效
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                        addDiscnt.put("START_DATE", firstTimeNextAcct);
                    }
                    if (("687".equals(delDiscntCode) || "688".equals(delDiscntCode) || "689".equals(delDiscntCode)) 
                            && ("1285".equals(addDiscntCode) || "1286".equals(addDiscntCode) || "1391".equals(addDiscntCode)))
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                        addDiscnt.put("START_DATE", firstTimeNextAcct);
                    }
                    else if (("1285".equals(delDiscntCode) || "1286".equals(delDiscntCode) || "1391".equals(delDiscntCode)) 
                            && ("687".equals(addDiscntCode) || "688".equals(addDiscntCode) || "689".equals(addDiscntCode)))
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                        addDiscnt.put("START_DATE", firstTimeNextAcct);
                    } 
                    else if(("687".equals(delDiscntCode) || "688".equals(delDiscntCode) || "689".equals(delDiscntCode)) 
                            && ("687".equals(addDiscntCode) || "688".equals(addDiscntCode) || "689".equals(addDiscntCode)))
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                        addDiscnt.put("START_DATE", firstTimeNextAcct);
                    }
                    
                    if (("79000".equals(delDiscntCode) || "79001".equals(delDiscntCode) || "79002".equals(delDiscntCode)) 
                            && ("79000".equals(addDiscntCode) || "79001".equals(addDiscntCode) || "79002".equals(addDiscntCode)))
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                        addDiscnt.put("START_DATE", firstTimeNextAcct);
                    }
                }
            }
        }

        String delendData = delDiscnt.getString("END_DATE", "");
        String delStartDate = delDiscnt.getString("START_DATE", "");
        String elementId = delDiscnt.getString("ELEMENT_ID", "");

        // 获取成员该集团用户优惠订购信息
        IDataset memUserDiscnt = UserDiscntInfoQry.getUserProductDis(memUserId, grpUserId);
        if (discntChange) // 如果没有新增资费
        {
            if (delendData.compareTo(delStartDate) < 0) // 如果删除的delDisCode是预约未生效的优惠，则把所有nowDisCode结束时间是本账期最后时间的优惠延长到2050年
            {
                for (int i = 0; i < memUserDiscnt.size(); i++)
                {
                    IData discnt = memUserDiscnt.getData(i);
                    String oldDate = discnt.getString("END_DATE");
                    String packageId = discnt.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，防止获取集团VPMN成员产品跨省优惠包
                    if (oldDate.equals(lastTimeThisAcctday) && "80000102".equals(packageId))
                    {
                        discnt.put("ELEMENT_ID", discnt.getString("DISCNT_CODE"));
                        discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        discnt.put("END_DATE", SysDateMgr.getTheLastTime());
                        discnts.add(discnt);
                    }
                }
            }
            else
            // 如果取消的delDisCode是已生效的优惠nowDisCode且nowDisCode不是本账期最后时间失效，那么delDisCode就立即失效，否则delDisCode还是本账期最后时间失效
            {
                delDiscnt.put("END_DATE", this.getAcceptTime());
                for (int i = 0; i < memUserDiscnt.size(); i++)
                {
                    IData discnt = memUserDiscnt.getData(i);
                    String oldDate = discnt.getString("END_DATE");
                    String discntCode = discnt.getString("DISCNT_CODE");
                    String packageId = discnt.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，防止获取集团VPMN成员产品跨省优惠包
                    if (oldDate.equals(lastTimeThisAcctday) && elementId.equals(discntCode) && "80000102".equals(packageId))
                    {
                        delDiscnt.put("END_DATE", lastTimeThisAcctday);
                    }
                }
            }
        }
        // 分散账期修改 加入分散账期元素处理标志 DIVERSIFY_ACCT_TAG=1
        addDiscnt.put("DIVERSIFY_ACCT_TAG", "1");
        delDiscnt.put("DIVERSIFY_ACCT_TAG", "1");
    }

    /**
     * 获取资费信息
     * 
     * @return
     * @throws Exception
     */
    private String getDiscntCode() throws Exception
    {
        String discntCode = "";
        String mebProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IDataset element = reqData.cd.getProductsElement(mebProductId);
        if (IDataUtil.isNotEmpty(element))
        {
            for (int i = 0; i < element.size(); i++)
            {
                IData map = element.getData(i);
                if ("D".equals(map.getString("ELEMENT_TYPE_CODE", "")) && map.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    discntCode = map.getString("DISCNT_CODE");
                    break;
                }
            }
        }
        return discntCode;
    }

    /**
     * 获取参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamInfo() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paraminfo = reqData.cd.getProductParamMap(baseMemProduct);
        return paraminfo;
    }

    /**
     * 获取参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamInfoByInit(IData map) throws Exception
    {              
        IData paraminfo = new DataMap();   
        IDataset paraminfos = map.getDataset("PRODUCT_PARAM_INFO");
        if (IDataUtil.isNotEmpty(paraminfos))
        {
            IDataset dstmp = paraminfos.getData(0).getDataset("PRODUCT_PARAM");
            if (IDataUtil.isNotEmpty(dstmp))
            {
                paraminfo = GroupModuleParserBean.transAttrList2Map(dstmp);
            }
        }
        return paraminfo;
    }

    /**
     * 655代理商套餐(VPMN JPA)，特殊处理：如果655被删除，那么查该用户是否订购有（1401，1402，1403，4807） 优惠，如果有则删除此优惠且结束时间变为本账期最后一天
     */
    public void infoDiscntDatadeal() throws Exception
    {
        IDataset dataset = reqData.cd.getDiscnt();
        String memUserId = reqData.getUca().getUserId();
        if (IDataUtil.isNotEmpty(dataset))
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = (IData) dataset.get(i);
                String element_id = data.getString("ELEMENT_ID", "");
                String state = data.getString("MODIFY_TAG", "");
                if (TRADE_MODIFY_TAG.DEL.getValue().equals(state) && "655".equals(element_id))
                {
                    IDataset discnts = UserDiscntInfoQry.getAllValidDiscntByUserId(memUserId); // 查成员用户下所有优惠
                    if (IDataUtil.isNotEmpty(discnts))
                    {
                        for (int j = 0; j < discnts.size(); j++)
                        {
                            IData discnt = (IData) discnts.get(j);
                            String discnt_code = discnt.getString("DISCNT_CODE");
                            if ("1401".equals(discnt_code) || "1402".equals(discnt_code) || "1403".equals(discnt_code) || "4807".equals(discnt_code))
                            {
                                discnt.put("ELEMENT_ID", discnt_code);
                                discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                data.put("END_DATE", DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, null)); // 本账期最后一天
                                // 加入分散账期元素处理标志DIVERSIFY_ACCT_TAG=1
                                discnt.put("DIVERSIFY_ACCT_TAG", "1");
                                dataset.add(discnt);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 生成Other表数据
     * 
     * @throws Exception
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IDataset discnts = reqData.cd.getDiscnt();
        String memUserId = reqData.getUca().getUserId();

        if (null != discnts && discnts.size() > 0)
        {
            for (int i = 0; i < discnts.size(); i++)
            {
                IData discnt = (IData) discnts.get(i);
                String element_id = discnt.getString("ELEMENT_ID", "");
                String state = discnt.getString("MODIFY_TAG", "");
                if (TRADE_MODIFY_TAG.DEL.getValue().equals(state) && "655".equals(element_id))
                {

                    IDataset userOther = UserOtherInfoQry.getUserOther(memUserId, "CHNL");
                    if (null != userOther && userOther.size() > 0)
                    {
                        for (int j = 0; j < userOther.size(); j++)
                        {

                            IData data = (IData) userOther.get(j);

                            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            data.put("END_DATE", DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, null));
                            dataset.add(data);
                        }
                    }
                }
            }
        }
        this.addTradeOther(dataset);
    }

    /**
     * 短号码变更处理
     * 
     * @throws Exception
     */
    public void infoRegDataShortCode() throws Exception
    {
        if (IDataUtil.isNotEmpty(paramInfo))
        {
            String grpUserId = reqData.getGrpUca().getUserId();
            // 短号码变更处理
            if (!paramInfo.getString("NOTIN_OLD_SHORT_CODE", "").equals(paramInfo.getString("SHORT_CODE", "")))
            {
                // 短号码变更处理VPN_MEB表
                infoRegDataVpn();

                // 短号验证
                String shortCode = paramInfo.getString("SHORT_CODE", "");
                if (StringUtils.isNotBlank(shortCode))
                {
                    IData inData = new DataMap();
                    inData.put("USER_ID_A", grpUserId);
                    inData.put("SHORT_CODE", shortCode);
                    inData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    IData reData = VpnUnit.shortCodeValidateVpn(inData);
                    if (IDataUtil.isNotEmpty(reData))   
                    {
                        String result = reData.getString("RESULT");
                        if ("false".equals(result))
                        {
                            String err = inData.getString("ERROR_MESSAGE");
                            if (!"".equals(err))
                            {
                                CSAppException.apperr(VpmnUserException.VPMN_USER_213, err);
                            }
                        }
                    }
                }
                else
                {// add by lixiuyu@20100816 集团订购“漫游短号服务（发指令）（801）”,成员短号不能为空
                    IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(grpUserId, "801");
                    if (IDataUtil.isNotEmpty(svcDataset) && "".equals(shortCode))
                    {
                        CSAppException.apperr(VpmnUserException.VPMN_USER_27);
                    }
                }
            }
        }

    }

    /**
     * 处理台帐VPN子表的数据
     * 
     * @param Datas
     *            VPN参数
     * @author liaoyi
     * @throws Exception
     */
    private void infoRegDataVpn() throws Exception
    {
        // 获取成员VPN个性信息
        String userId = reqData.getUca().getUserId();
        String userIdA = reqData.getGrpUca().getUserId();

        String eparchyCode = reqData.getUca().getUser().getEparchyCode();

        IData vpnData = UserVpnInfoQry.getMemberVpnByUserId(userId, userIdA, eparchyCode);
        if (IDataUtil.isEmpty(vpnData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_240);
        }
        else
        {
            String shortCode = paramInfo.getString("SHORT_CODE", "");
            if (StringUtils.isNotBlank(shortCode))
            {
                // VPN数据
                IDataset dataset = new DatasetList();
                // 个性化参数
                vpnData.put("SHORT_CODE", paramInfo.getString("SHORT_CODE", ""));// 成员短号码;
                vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                dataset.add(vpnData);
                addTradeVpnMeb(dataset);
            }
        }
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        // 判断是否发短信 start
        boolean isSms = map.getBoolean("IF_SMS", true);

        String mebUserId = reqData.getUca().getUserId();
        IDataset discnts = reqData.cd.getDiscnt();
        IDataset res = reqData.cd.getRes();
        nextMonthFirstTime = DiversifyAcctUtil.getFirstTimeNextAcct(mebUserId);
        if (IDataUtil.isNotEmpty(discnts))
        {
            for (int i = 0; i < discnts.size(); i++)
            {
                IData data = discnts.getData(i);
                if (TRADE_MODIFY_TAG.Add.getValue().equals(data.getString("MODIFY_TAG")))
                {
                	discnt = discnts.getData(i);
                    discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE", ""));
                    String packageId = data.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，区别集团VPMN成员产品其他优惠包
                    if (!"80000102".equals(packageId))
                    {
                        isNormalPackage = false;
                    }
                }else if (TRADE_MODIFY_TAG.DEL.getValue().equals(data.getString("MODIFY_TAG"))){
                	discntNameOld = UDiscntInfoQry.getDiscntNameByDiscntCode(data.getString("DISCNT_CODE", ""));
                }
            }
        }
        if (IDataUtil.isNotEmpty(res))
        {
            for (int i = 0; i < res.size(); i++)
            {
                IData data = (IData) res.get(i);
                if (TRADE_MODIFY_TAG.Add.getValue().equals(data.getString("MODIFY_TAG")))
                {
                    resShortCode = data.getString("RES_CODE");
                }
            }
        }
        if (StringUtils.isBlank(discntName) && StringUtils.isBlank(resShortCode))
        {
            isSms = false;
        }
        reqData.setNeedSms(isSms); // 是否发短信

        // 判断是否发短信 end

    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        makBeforeInit(map);
        super.makInit(map);

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        // 因为在makInit已经查出三户资料了，所以这里不再查
    }

    /**
     * VPMN成员产品变更登记服务子台帐，走服务开通
     * 
     * @param map
     * @return
     * @throws Exception
     */
    public IData makBeforeInit(IData map) throws Exception
    {
        // 添加860服务modi；如果短号变了，添加861服务modi start
        IData paramInfo = getParamInfoByInit(map);
        IDataset elelist = map.getDataset("ELEMENT_INFO", null); // reqData.cd.getSvc();
        boolean has860 = false;
        boolean has861 = false;
        if (IDataUtil.isNotEmpty(elelist))
        {
            for (int i = 0; i < elelist.size(); i++)
            {
                IData info = elelist.getData(i);
                if ("S".equals(info.getString("ELEMENT_TYPE_CODE")) && "860".equals(info.getString("ELEMENT_ID")))
                {
                    has860 = true;
                }
                if ("S".equals(info.getString("ELEMENT_TYPE_CODE")) && "861".equals(info.getString("ELEMENT_ID")))
                {
                    has861 = true;
                }
            }
        }
        if (!has860)
        {
            IDataset dataset860 = UserSvcInfoQry.getUserSingleProductSvc(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), null, null, "860", null, null);
            if (IDataUtil.isNotEmpty(dataset860))
            {
                IData elementData = dataset860.getData(0);
                elementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                elementData.put("ELEMENT_TYPE_CODE", "S");
                elementData.put("ELEMENT_ID", elementData.getString("SERVICE_ID", "860"));
                elelist.add(elementData);
            }
        }
        if (!has861)
        {
            // 查询UU关系表
            String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
            IData uuInfo = RelaUUInfoQry.getRelaByPK(reqData.getGrpUca().getUserId(), reqData.getUca().getUser().getUserId(), relationTypeCode);
            String oldShortCode = "";
            String newShortCode = paramInfo.getString("SHORT_CODE", "");
            if (IDataUtil.isNotEmpty(uuInfo))
            {
                oldShortCode = uuInfo.getString("SHORT_CODE", "");
            }
            if (!oldShortCode.equals(newShortCode) && StringUtils.isNotBlank(newShortCode))
            {
                IData elementData = new DataMap();
                IDataset dataset861 = UserSvcInfoQry.getUserSingleProductSvc(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), null, null, "861", null, null);
                if (IDataUtil.isNotEmpty(dataset861))
                {
                    IData data861 = dataset861.getData(0);
                    elementData.put("INST_ID", data861.getString("INST_ID"));
                    elementData.put("START_DATE", data861.getString("START_DATE"));
                    elementData.put("END_DATE", data861.getString("END_DATE"));
                    elementData.put("ELEMENT_TYPE_CODE", "S");
                    elementData.put("PRODUCT_ID", data861.getString("PRODUCT_ID"));
                    elementData.put("PACKAGE_ID", data861.getString("PACKAGE_ID"));
                    elementData.put("ELEMENT_ID", "861");
                    elementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                }
                elelist.add(elementData);

                /*
                 * //处理短号 将旧短号删除掉 IDataset reslist = map.getDataset("RES_INFO", new DatasetList()); IData resData = new
                 * DataMap(); resData.put("RES_TYPE_CODE", "S"); resData.put("RES_CODE", oldShortCode);
                 * resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); reslist.add(resData);
                 */
            }

        }
        // 添加860服务modi；如果短号变了，添加861服务modi end
        IData element = new DataMap();
        element.put("ELEMENT_INFO", elelist);
        return element;
    }

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        if (IDataUtil.isNotEmpty(paramInfo))
        {
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

            String oldRoleCodeB = uuInfo.getString("ROLE_CODE_B", "");
            String oldShortCode = uuInfo.getString("SHORT_CODE", "");
            String roleCodeB = reqData.getMemRoleB();// paramInfo.getString("ROLE_CODE_B", "");
            // 角色若改变，角色为2的给该字段赋值，老赋1、 新赋0; j2ee 角色能变吗？
            if (!roleCodeB.equals(oldRoleCodeB))
            {
                if (oldRoleCodeB.equals("2"))
                {
                    data.put("RSRV_STR4", "1");
                }
                else if (roleCodeB.equals("2"))
                {
                    data.put("RSRV_STR4", "0");
                }
            }

            data.put("RSRV_STR5", oldShortCode.equals(paramInfo.getString("SHORT_CODE")) ? "1" : "0");// 短号是否变化，0：变、
            // 1：没变
            data.put("RSRV_STR10", paramInfo.getString("OUT_PROV_DISCNT", "")); // 跨省集团优惠(取决于是否有“添加跨省VPN集团”功能点)
        }
        // 短信start
        data.put("RSRV_STR6", smsType);// 短信类型

        String NOTICE_CONTENT = "";
        //IS_VpnMebChg：只发短号修改的短信，IS_VpnMebChg_Discnt：只发优惠的短信，IS_VpnMebChg_COMB：发短号和优惠的短信，两条
        if (StringUtils.isNotBlank(resShortCode))
        {
            NOTICE_CONTENT += "短号为" + resShortCode + ",";
            data.put("RSRV_STR6", "VpnMebChg");// 短信类型
            if (StringUtils.isNotBlank(discntName))
            {
                if (!isNormalPackage)
                {
                    NOTICE_CONTENT += ",新增套餐为[" + discntName + "].";
                }               
                data.put("RSRV_STR9", discntName);
                data.put("RSRV_STR10", discntNameOld);
                data.put("RSRV_STR6", "VpnMebChgComb");// 短信类型
                
            }
            
            
        }else{
        	if (StringUtils.isNotBlank(discntName))
            {
        		if (!isNormalPackage)
                {
                    NOTICE_CONTENT += ",新增套餐为[" + discntName + "].";
                }  
                data.put("RSRV_STR9", discntName);
                data.put("RSRV_STR10", discntNameOld);
            	data.put("RSRV_STR6", "VpnMebChgDiscnt");// 短信类型
            }
        }
        data.put("RSRV_STR5", discnt.getString("START_DATE", ""));
        data.put("RSRV_STR7", NOTICE_CONTENT);
        data.put("RSRV_STR8", nextMonthFirstTime.substring(0, 10));
        // 短信end
    }

    /**
     * 新增短号临时表 wangyf6
     * 
     * @param pd
     * @throws Exception
     */
    public void insertTemporaryShortCode() throws Exception
    {
        String userID = reqData.getGrpUca().getUser().getUserId();
        IData infoData = new DataMap();
        infoData.put("SHORT_CODE", resShortCode);

        // 是否有母集团
        IDataset relaData = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userID, "40", Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(relaData))
        {
            String userIDa = relaData.getData(0).getString("USER_ID_A", "");
            if (StringUtils.isNotBlank(userIDa))
            {
                infoData.put("PARTITION_ID", userIDa.substring(userIDa.length() - 4, userIDa.length()));
                infoData.put("USER_ID_A", userIDa);// 母集团的USER_ID
                infoData.put("RSRV_TAG1", "1");// 1标识是母集团
            }
        }
        else
        {
            infoData.put("PARTITION_ID", userID.substring(userID.length() - 4, userID.length()));
            infoData.put("USER_ID_A", userID);// 集团VPMN的USER_ID
        }
        infoData.put("USER_ID_B", reqData.getUca().getUserId());
        infoData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        infoData.put("ACCEPT_DATE", this.getAcceptTime());
        infoData.put("TRADE_ID", getTradeId());
        infoData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        infoData.put("REMARK", "集团VPMN成员新增");

        try
        {
            boolean resultFlag = Dao.insert("TF_F_TEMPORARY_SHORTCODE", infoData, Route.getCrmDefaultDb());
        }
        catch (Exception e)
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_219, infoData.getString("SHORT_CODE"), infoData.getString("USER_ID_A"));

        }
    }

    /**
     * 防止前台相同短号同时验证通过的情况，对短号进行写入台账前的第二次验证
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public void shortCodeValidate(String shortCode) throws Exception
    {
        IData param = new DataMap();
        param.put("SHORT_CODE", shortCode);
        param.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
        IData retData = new DataMap();
        retData = VpnUnit.shortCodeValidateVpn(param);

        if (IDataUtil.isNotEmpty(retData))
        {
            if (!retData.getBoolean("RESULT"))
            {
                if (!retData.getBoolean("RESULT"))
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_220, retData.getString("ERROR_MESSAGE"));
                }
            }
        }
    }
    
    
    /**
     * 设置呼叫来显方式
     * @throws Exception
     */
    public void infoRegSvcClipType() throws Exception 
    {
    	if((IDataUtil.isNotEmpty(paramInfo)))
    	{
    		String grpClipType = paramInfo.getString("NOTIN_GRP_CLIP_TYPE","");//GRP_CLIP_TYPE 呼叫来显方式
            //String grpUserClipType = paramInfo.getString("GRP_USER_CLIP_TYPE","");//GRP_USER_CLIP_TYPE 选择号显方式
            String grpUserMod = paramInfo.getString("NOTIN_GRP_USER_MOD","");//GRP_USER_MOD 成员修改号显方式
            String clipType = paramInfo.getString("NOTIN_CLIP_TYPE","");
            String oldClipType = paramInfo.getString("NOTIN_OLD_CLIP_TYPE","");
            boolean isChangeType = false;
            boolean isAddType = false;
            
            if(StringUtils.isNotBlank(grpClipType) && StringUtils.isNotBlank(grpUserMod)
            		&& StringUtils.equals("1", grpClipType) && StringUtils.equals("1", grpUserMod))
            {
            	if(StringUtils.isNotBlank(clipType) && StringUtils.isNotBlank(oldClipType)
            				&& !StringUtils.equals(clipType, oldClipType))
            	{
            		isChangeType = true;
            	}
            	else if(StringUtils.isBlank(oldClipType) && StringUtils.isNotBlank(clipType))
            	{
            		isAddType = true;
            	}
            	if(isChangeType)
            	{
            		String userId = reqData.getUca().getUserId();
            		IDataset mebOtherInfos = UserOtherInfoQry.getUserOtherInfoByAll(userId, "VPMN_MEBCLIP");
            		if(IDataUtil.isNotEmpty(mebOtherInfos))
            		{
            			IData mebOtherInfo = mebOtherInfos.getData(0);
            			mebOtherInfo.put("RSRV_VALUE", clipType);
            			mebOtherInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            			super.addTradeOther(mebOtherInfo);
            		}
            	}
            	if(isAddType)
            	{
            		IDataset lineDataset = new DatasetList();
            		
            		IData data = new DataMap();
                    data.put("USER_ID", reqData.getUca().getUserId());
                    data.put("RSRV_VALUE_CODE", "VPMN_MEBCLIP");
                    data.put("RSRV_VALUE", clipType);
                    data.put("START_DATE", getAcceptTime());
                    data.put("END_DATE", SysDateMgr.getTheLastTime());
                    data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    lineDataset.add(data);
                    
                    super.addTradeOther(lineDataset);
            	}
            }
            
    	}
    	
    }
    
    
}
