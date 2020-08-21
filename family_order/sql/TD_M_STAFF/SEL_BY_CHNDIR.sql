--IS_CACHE=Y
SELECT STAFF_ID,DEPART_ID,STAFF_NAME,DIMISSION_TAG,
f_sys_getcodename('depart_id',depart_id,null,null) depart_name
 FROM TD_M_STAFF
 WHERE STAFF_ID=:STAFF_ID
  and cust_manager_flag IN ('2','3') AND dimission_tag='0'