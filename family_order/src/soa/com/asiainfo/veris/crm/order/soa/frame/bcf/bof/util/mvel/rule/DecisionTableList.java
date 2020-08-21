
package com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DecisionTableList implements List<DecisionTableItem>
{
    private List<DecisionTableItem> items = new ArrayList<DecisionTableItem>();

    public boolean add(DecisionTableItem arg0)
    {
        return items.add(arg0);
    }

    public void add(int arg0, DecisionTableItem arg1)
    {
        items.add(arg0, arg1);
    }

    public void add(String script, String rid)
    {
        items.add(new DecisionTableItem(script, rid));
    }

    public boolean addAll(Collection<? extends DecisionTableItem> arg0)
    {
        return items.addAll(arg0);
    }

    public boolean addAll(int arg0, Collection<? extends DecisionTableItem> arg1)
    {
        return items.addAll(arg0, arg1);
    }

    public void clear()
    {
        items.clear();

    }

    public boolean contains(Object arg0)
    {
        return items.contains(arg0);
    }

    public boolean containsAll(Collection<?> arg0)
    {
        return items.containsAll(arg0);
    }

    public DecisionTableItem get(int arg0)
    {
        return items.get(arg0);
    }

    public int indexOf(Object arg0)
    {
        return items.indexOf(arg0);
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public Iterator<DecisionTableItem> iterator()
    {
        return items.iterator();
    }

    public int lastIndexOf(Object arg0)
    {
        return items.lastIndexOf(arg0);
    }

    public ListIterator<DecisionTableItem> listIterator()
    {
        return items.listIterator();
    }

    public ListIterator<DecisionTableItem> listIterator(int arg0)
    {
        return items.listIterator(arg0);
    }

    public DecisionTableItem remove(int arg0)
    {
        return items.remove(arg0);
    }

    public boolean remove(Object arg0)
    {
        return items.remove(arg0);
    }

    public boolean removeAll(Collection<?> arg0)
    {
        return items.removeAll(arg0);
    }

    public boolean retainAll(Collection<?> arg0)
    {
        return items.retainAll(arg0);
    }

    public DecisionTableItem set(int arg0, DecisionTableItem arg1)
    {
        return items.set(arg0, arg1);
    }

    public int size()
    {
        return items.size();
    }

    public List<DecisionTableItem> subList(int arg0, int arg1)
    {
        return items.subList(arg0, arg1);
    }

    public Object[] toArray()
    {
        return items.toArray();
    }

    public <T> T[] toArray(T[] arg0)
    {
        return items.toArray(arg0);
    }

}
