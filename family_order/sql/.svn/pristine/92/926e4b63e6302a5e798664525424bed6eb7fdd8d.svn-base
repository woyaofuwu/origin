--IS_CACHE=Y
SELECT eparchy_code,discnt_code,discnt_name,to_char(money) money,to_char(high_fee) high_fee,item_type,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,to_char(rsrv_fee3) rsrv_fee3,to_char(rsrv_fee4) rsrv_fee4,update_eparchy_code,update_depart_id,update_staff_id 
  FROM td_a_queryfeeset
 WHERE eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ'
 ORDER BY discnt_code,item_type,rsrv_fee1,rsrv_fee2