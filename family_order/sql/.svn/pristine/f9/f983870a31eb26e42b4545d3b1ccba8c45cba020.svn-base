SELECT to_char(manager_tel) product_id,manager_name product_name,'0' flag,
0 note_item_code,group_id note_item,prevalue1  rsrv_info1 ,
prevalue2 rsrv_info2,0 rsrv_num1,'0' eparchy_code 
FROM
(select to_char(a.serial_number) manager_tel,to_char(a.vip_manager_name) manager_name,b.group_id,to_char(b.prevalue1) prevalue1,to_char(b.prevalue2) prevalue2 
from tf_f_managerstaff a,tf_f_cust_group b 
where b.cust_id = :CUST_ID
and a.VIP_MANAGER_ID = b.manager_staff_id)