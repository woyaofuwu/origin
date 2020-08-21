--IS_CACHE=Y
select b.menu_id, b.CHAN_ID, b.BRAND_CODE, b.MENU_EPARCHY, b.MENU_LEVEL, b.MENU_CODE, b.MENU_NAME, c.PARAM_NAME, c.PARAM_MIN_VALUE
from TD_M_MENU_USER a, TD_S_CHANNEL_MENU b, TD_S_CHANNEL_MENU_ATTR c
where 1=1 
and a.STATUS_TAG = '0' 
and ((a.start_date <= sysdate and sysdate <=a.end_date) or (a.end_date is NULL))
and a.menu_id like :MENU_ID || '%'
and a.menu_id = b.menu_id 
and b.menu_id = c.menu_id(+)
order by b.menu_id