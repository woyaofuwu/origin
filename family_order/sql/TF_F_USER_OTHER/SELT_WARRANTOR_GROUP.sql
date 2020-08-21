WITH 
tab_user AS (SELECT cust_id ,serial_number FROM tf_F_user WHERE user_id = to_number(:USER_ID)  and partition_id = mod(to_number(:USER_ID),10000) and remove_tag = '0')
select 
(select decode(RSRV_STR5,'0','个人担保','1','集团担保') from TF_F_USER_OTHER where user_id  = to_number(:USER_ID) and rsrv_value_code = 'CRED' and SYSDATE between start_date and end_date) RSRV_STR5,
t2.CLASS_ID CLASS_NAME,t1.CUST_NAME,t2.GROUP_CONTACT_PHONE PHONE,t1.REMARK,t3.serial_number SERIAL_NUMBER
from  TF_F_CUSTOMER t1,TF_F_CUST_GROUP t2,tab_user t3
where t1.cust_id =t3.cust_id
and   t2.cust_id = t1.cust_id