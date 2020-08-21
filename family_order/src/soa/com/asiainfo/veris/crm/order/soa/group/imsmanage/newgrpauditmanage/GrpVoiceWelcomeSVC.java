
package com.asiainfo.veris.crm.order.soa.group.imsmanage.newgrpauditmanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class GrpVoiceWelcomeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData data) throws Exception
    {
        GrpVoiceWelcomeBean bean = new GrpVoiceWelcomeBean();

        return bean.crtTrade(data);
    }

    /**
     * 查询文件
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGrpVoiceWelcomeFiles(IData input) throws Exception {
        GrpVoiceWelcomeBean memberFileBean = (GrpVoiceWelcomeBean) BeanManager
                .createBean(GrpVoiceWelcomeBean.class);
        return memberFileBean.queryGrpVoiceWelcomeFiles(input, getPagination());
    }
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryImsGrpFileByFileId(IData input) throws Exception {
        GrpVoiceWelcomeBean memberFileBean = (GrpVoiceWelcomeBean) BeanManager
                .createBean(GrpVoiceWelcomeBean.class);
        return memberFileBean.queryImsGrpFileByFileId(input);
    }
    
    /**
     * 融合总机上传欢迎词
     * 
     * @return
     * @throws Exception
     */
    public static IDataset sendGrpWordMessage(IData inData) throws Exception
    {
        return IBossCall.sendGrpWordMessage(inData);
    }
    
    /**
     * 融合总机激活欢迎词
     * 
     * @return
     * @throws Exception
     */
    public static IDataset activeGrpWordMessage(IData inData) throws Exception
    {
        return IBossCall.activeGrpWordMessage(inData);
    }
    
    /**
     * 审核欢迎词成功，更新标识RSRV_TAG1为9
     * @param inData
     * @throws Exception
     */
    public static IDataset saveCheckWordWelcome(IData inData) throws Exception 
    {
        GrpVoiceWelcomeBean fileBean = (GrpVoiceWelcomeBean) BeanManager.createBean(
                GrpVoiceWelcomeBean.class);
        boolean flag = fileBean.saveCheckWordWelcome(inData);
        
        IData retData = new DataMap();
        retData.put("RESULT_FLAG", flag);
        
        return IDataUtil.idToIds(retData);
    }
}
