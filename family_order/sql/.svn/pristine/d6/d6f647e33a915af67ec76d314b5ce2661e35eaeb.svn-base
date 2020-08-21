INSERT INTO tf_f_postinfo(partition_id,id,id_type,cust_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,
            post_code,email,fax_nbr,start_date,end_date,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,
            rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,
            rsrv_tag1,rsrv_tag2,rsrv_tag3)
select mod(id,10000),id,id_type,cust_type,post_name,post_tag,post_content,post_typeset,post_cyc,post_address,
       post_code,email,fax_nbr,start_date,end_date,sysdate,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,
       rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,
       rsrv_tag1,rsrv_tag2,rsrv_tag3
from tf_b_trade_post
where trade_id=to_number(:TRADE_ID)