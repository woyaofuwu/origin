WITH 
tab_user AS (SELECT cust_id ,serial_number FROM tf_F_user WHERE user_id = to_number(:USER_ID)  and partition_id = mod(to_number(:USER_ID),10000) and remove_tag = '0')
select 
(select decode(RSRV_STR5,'0','个人担保','1','集团担保') from TF_F_USER_OTHER where user_id  = to_number(:USER_ID) and rsrv_value_code = 'CRED' and SYSDATE between start_date and end_date) RSRV_STR5,
(select CLASS_NAME from TF_F_CUST_VIP t1,TD_M_VIPCLASS t2 where t1.user_id =to_number(:USER_ID) and t1.remove_tag = '0' and t1.vip_type_code = t2.vip_type_code and t1.class_id = t2.class_id) CLASS_NAME,
t1.cust_name CUST_NAME,t2.phone PHONE,t1.remark REMARK,t3.serial_number SERIAL_NUMBER
from TF_F_CUSTOMER t1,TF_F_CUST_PERSON t2,tab_user t3
where t1.cust_id =t3.cust_id
and   t2.cust_id = t1.cust_id