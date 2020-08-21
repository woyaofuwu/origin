
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.share;

import java.util.ArrayList;
import java.util.List;

import com.ailk.service.session.app.ISessionShareObject;

public class CalElementDateShare implements ISessionShareObject
{
    private List<IElementCalDateAction> actions = new ArrayList<IElementCalDateAction>();

    public void add(IElementCalDateAction action)
    {
        this.actions.add(action);
    }

    public void addAll(List<IElementCalDateAction> actions)
    {
        this.actions.addAll(actions);
    }

    @Override
    public void clean()
    {
        this.actions.clear();
    }

    public List<IElementCalDateAction> getActions()
    {
        return this.actions;
    }
}
