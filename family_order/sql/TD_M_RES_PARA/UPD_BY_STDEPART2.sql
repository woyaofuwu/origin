UPDATE TD_M_RES_PARA
   SET 
para_value4=TO_NUMBER(NVL(para_value4,0))+:PARA_VALUE4,
rdvalue1=sysdate,
update_time=SYSDATE,
update_staff_id=:UPDATE_STAFF_ID,
update_depart_id=:UPDATE_DEPART_ID
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 is null or para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 is null or para_code1=:PARA_CODE2)