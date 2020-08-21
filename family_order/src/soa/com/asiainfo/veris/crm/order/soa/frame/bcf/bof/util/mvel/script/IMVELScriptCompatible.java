
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script;

public interface IMVELScriptCompatible
{
    public boolean containKey(String key);

    public Object getData(String key);

    public void setData(String key, Object v);
}
