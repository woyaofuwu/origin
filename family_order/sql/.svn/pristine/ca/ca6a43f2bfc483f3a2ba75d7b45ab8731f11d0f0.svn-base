--IS_CACHE=Y
select menu.* from TD_S_CHANNEL_MENU menu where 1 = 1
 and menu.START_DATE >= to_date(:START_DATE, 'yyyy-mm-dd')
 and menu.END_DATE <= to_date(:END_DATE, 'yyyy-mm-dd')
 and menu.MENU_ID = :MENU_ID
 and menu.BRAND_CODE = :BRAND_CODE
 and menu.CHAN_ID = :CHAN_ID
 and menu.MENU_NAME like '%' || :MENU_NAME || '%'
 and menu.MENU_EPARCHY = :MENU_EPARCHY
 and menu.MENU_LEVEL = :MENU_LEVEL
 and menu.MENU_CODE = :MENU_CODE
 order by menu.MENU_ID