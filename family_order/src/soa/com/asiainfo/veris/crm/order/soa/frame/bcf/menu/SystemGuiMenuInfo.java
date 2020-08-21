package com.asiainfo.veris.crm.order.soa.frame.bcf.menu;

import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;

public class SystemGuiMenuInfo {

	public static String getMenuTextByMenuId(String menuId) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(SystemGuiMenuCache.class);
        
        if(cache==null)
        {
        	return "";
        }
        String menuText = (String) cache.get(menuId);

        return menuText;
    }
}
