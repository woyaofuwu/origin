select flow_id,CUST_CONTACT_ID,modify_tag,modify_desc,update_staff_id,update_depart_id,update_city_code,update_eparchy_code,update_time,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark
from tl_b_custcontact_tradelog
where update_staff_id = :UPDATE_STAFF_ID
and   update_time >= TO_DATE(:START_TIME,'YYYY-MM-DD HH24:MI:SS')
and   update_time <= TO_DATE(:END_TIME,'YYYY-MM-DD HH24:MI:SS') +1