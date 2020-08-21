package com.asiainfo.veris.crm.iorder.web.igroup.minorec.minorecSpeedinessApply;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSBasePage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public abstract class MinorecSpeedinessApplyForPhone extends CSBasePage {

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */

    public void initPage(IRequestCycle cycle) throws Exception {

    }


    public abstract void setInfo(IData info) throws Exception;

    public abstract void setInfos(IDataset infos) throws Exception;

}
