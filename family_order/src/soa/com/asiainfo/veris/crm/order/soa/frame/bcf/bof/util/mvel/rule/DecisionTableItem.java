
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule;

public class DecisionTableItem
{
    private String script;

    private String relatedId;

    public DecisionTableItem(String script, String relatedId)
    {
        super();
        this.script = script;
        this.relatedId = relatedId;
    }

    String getRelatedId()
    {
        return relatedId;
    }

    String getScript()
    {
        return script;
    }

    void setRelatedId(String relatedId)
    {
        this.relatedId = relatedId;
    }

    void setScript(String script)
    {
        this.script = script;
    }

}
