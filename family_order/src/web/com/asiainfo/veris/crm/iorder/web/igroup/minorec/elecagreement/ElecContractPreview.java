package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import org.apache.tapestry.IRequestCycle;

/**
 * @author ckh
 * @date 2018/10/25.
 */
public abstract class ElecContractPreview extends GroupBasePage
{
    public void init(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String archivesId = param.getString("ARCHIVES_ID");
        if (StringUtils.isEmpty(archivesId))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "档案ID为空，请检查合同资料！");
        }

        IDataset retLists = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElectronicAgreementInfo", param);
        if (DataUtils.isEmpty(retLists))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "通过" + archivesId + "未找到对应的档案信息，请检查档案资料！");
        }
        IData archiveInfo = retLists.first();
        String archivesAttach = archiveInfo.getString("PDF_FILE");
        if (StringUtils.isEmpty(archivesAttach))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "通过" + archivesId + "找到的档案信息中缺失合同文件,请检查档案资料！");
        }
        setAttachInfos(param, archivesId, archivesAttach);
    }

    /**
     * 设置页面元素
     *
     * @param param          传入参数
     * @param archivesId     电子档案id
     * @param archivesAttach 电子档案/协议附件信息
     * @throws Exception 设置异常
     */
    private void setAttachInfos(IData param, String archivesId, String archivesAttach) throws Exception
    {
        IDataset attachInfos = new DatasetList(archivesAttach);
        for (int i = 0; i < attachInfos.size(); i++)
        {
            IData attachInfo = attachInfos.getData(i);
            String fileId = attachInfo.getString("FILE_ID");
            attachInfo.put("URL", "attach?action=show&needSuffix=false&realName=" +attachInfo.getString("FILE_NAME") + "&fileId=" + fileId);
            attachInfo.put("ARCHIVES_ID", archivesId);
        }

        setAttachInfos(attachInfos);
        setInfo(param);
    }
    

    public abstract void setAttachInfos(IDataset attachInfos);

    public abstract void setInfo(IData info) throws Exception;
}
