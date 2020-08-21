--IS_CACHE=Y
select a.STAFF_ROLE || '|' || a.MENU_ID || '|' || a.STATUS_TAG KEY, 
	  a.*,b.MENU_EPARCHY from TD_M_MENU_STAFF a,TD_S_CHANNEL_MENU b 
where a.MENU_ID=b.MENU_ID
 and a.STAFF_ROLE = :STAFF_ROLE
 and a.MENU_ID = :MENU_ID
 and a.STATUS_TAG = :STATUS_TAG
 and a.START_DATE >= to_date(:START_DATE, 'yyyy-mm-dd')
 and a.END_DATE <= to_date(:END_DATE, 'yyyy-mm-dd')
 order by a.STAFF_ROLE, a.MENU_ID