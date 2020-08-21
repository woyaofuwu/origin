
package com.asiainfo.veris.crm.order.soa.person.busi.ziyoubusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ziyoubusiness.ZiYouBusinessPageElementQry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ZiYouBusiDynamicPageBean extends CSBizBean
{

    private static final Logger logger = LoggerFactory.getLogger(ZiYouBusiDynamicPageBean.class);

    private void deal3After(IDataset pageEles, IData current) throws ScriptException
    {
        String id = current.getString("ID");
        for (int i = 0; i < pageEles.size(); i++)
        {
            IData ele = pageEles.getData(i);
            String condition = ele.getString("CONDITION", "");
            if (condition!=null&&!("".equals(condition)))
            {
                if (!evalCondition(condition))
                {
                    continue;
                }
            }

            if (ele.getString("PARENT_ID", "").equals(id))
            {
                IDataset children = (IDataset)(current.get("CHILDREN"));
                if (children == null)
                {
                    children = new DatasetList();
                    current.put("CHILDREN", children);
                }
                pageEles.remove(i--);
                children.add(ele);
                deal3After(pageEles, ele);// pop一个
            }
        }
    }

    private static ScriptEngineManager sem = new ScriptEngineManager();

    private static ScriptEngine se = sem.getEngineByName("JavaScript");

    private Bindings bindings;

    public Bindings getBindings()
    {
        return bindings;
    }

    public void setBindings(IData input)
    {
        this.bindings = se.createBindings();
        this.bindings.putAll(input);
    }

    public boolean evalCondition(String condition) throws ScriptException
    {
        String[] cc = condition.split("==|!=|<=|>=|>|<");
        String bl = cc[0].trim();
        if (bl.startsWith("typeof") || this.getBindings().containsKey(bl))
        {
            Object rs = se.eval(condition, this.getBindings());
            if (Boolean.parseBoolean(rs.toString()))
            {
                return true;
            }
        }
        return false;
    }
    public IDataset initPageElement(IData input) throws Exception {
        IData result = new DataMap();
        IDataset results = new DatasetList();
        try {
            IDataset pageEles = ZiYouBusinessPageElementQry.getFbPageElements(input.getString("TRADE_TYPE_CODE"), input.getString("LV", "1"));
            IDataset newPages = new DatasetList();

            IData pageMap = new DataMap();
            for (int i = 0; i < pageEles.size(); i++)
            {
                IData ele = pageEles.getData(i);
                String parentId = ele.getString("PARENT_ID");
                String condition = ele.getString("CONDITION");
                int level = Integer.parseInt(ele.getString("LEVEL"));
                pageEles.remove(i--);
                if (condition!=null&&!("".equals(condition)))
                {

                    if (!evalCondition(condition))
                    {
                        continue;
                    }
                }
                if (level == 1)
                {
                    newPages.add(ele);
                    pageMap = ele;
                }
                else// 第二层
                {
                    if (parentId.equals(pageMap.getString("ID")))
                    {
                        IDataset children = (IDataset)(pageMap.get("CHILDREN"));
                        if (children == null)
                        {
                            children = new DatasetList();
                            pageMap.put("CHILDREN", children);
                        }
                        children.add(ele);
                        deal3After(pageEles, ele);
                    }
                }
            }

            result.put("newPages", newPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        results.add(result);
        return results;
    }
}
