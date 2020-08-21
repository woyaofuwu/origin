select '' SUBSYS_CODE, 0 PARAM_ATTR, '' PARAM_CODE, '' PARAM_NAME,
a.city_code para_code1, a.serial_number para_code2, d.group_id para_code3, 
d.cust_name para_code4, 
nvl((select depart_name from td_m_depart where depart_id=e.develop_depart_id),'') para_code5,
b.serv_para1 para_code6, b.serv_para2 para_code7, b.serv_para3 para_code8, b.serv_para4 PARA_CODE9 , 
b.serv_para5 PARA_CODE10 ,b.serv_para6 PARA_CODE11 ,b.serv_para7 PARA_CODE12 , b.serv_para8 PARA_CODE13 ,
a.product_id PARA_CODE14 , a.user_id PARA_CODE15 ,
'' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 ,
'' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 ,
'' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 ,
'' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , 
'' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , 
'' UPDATE_DEPART_ID , '' UPDATE_TIME
from tf_f_user a,tf_f_user_svc b,tf_f_cust_product c,tf_f_cust_group d,tf_f_user_file e
where 
a.remove_tag='0'
and a.product_id=:PARA_CODE1
and a.user_id=b.user_id
and a.user_id=c.rsrv_str5
and c.cust_id=d.cust_id
and a.user_id=e.user_id
and (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL) 
and (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL) 
and (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL) 
and (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) 
and (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) 
and (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) 
and (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) 
and (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
and (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)