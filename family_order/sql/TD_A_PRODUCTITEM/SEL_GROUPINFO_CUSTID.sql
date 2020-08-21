SELECT to_char(cust_id) product_id,cust_name product_name,'0' flag,
0 note_item_code,group_id note_item,prevalue1  rsrv_info1 ,
prevalue2 rsrv_info2,0 rsrv_num1,'0' eparchy_code 
FROM
(select to_char(a.cust_id) cust_id,to_char(cust_name) cust_name,group_id,to_char(prevalue1) prevalue1,to_char(prevalue2) prevalue2 
from tf_F_user a,tf_f_cust_group b 
where a.cust_id=b.cust_id 
and a.serial_number=to_char(:SERIAL_NUMBER))