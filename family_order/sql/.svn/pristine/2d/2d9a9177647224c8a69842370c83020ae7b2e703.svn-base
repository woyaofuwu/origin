--IS_CACHE=Y
select a.* from TD_M_MENU_STAFF a where 1=1
 and a.STATUS_TAG = '0'
 and a.STAFF_ROLE in 
 (select r.STAFF_ROLE from TD_S_ECHANNAL_STAFF s,TD_S_ECHANNAL_RELATION r
 where s.ID_VALUE = r.STAFF_ID
 and s.ID_VALUE = :ID_VALUE)