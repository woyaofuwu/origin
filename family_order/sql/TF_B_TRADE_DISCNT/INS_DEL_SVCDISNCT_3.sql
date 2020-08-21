INSERT INTO TF_B_TRADE_DISCNT(trade_id,accept_month,user_id,user_id_a,package_id,product_id,discnt_code,spec_tag,relation_type_code,
              inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,
              rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,
              rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3)
SELECT  :TRADE_ID,:ACCEPT_MONTH,user_id,user_id_a,package_id,product_id,discnt_code,spec_tag,relation_type_code,
        inst_id,campn_id,start_date,to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),'1',sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,
        :REMARK,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,
        rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
FROM TF_F_USER_DISCNT a 
WHERE user_id=to_number(:USER_ID)
  AND partition_id=mod(to_number(:USER_ID),10000)
  AND end_date>TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
  AND exists (SELECT 1 from td_b_attr_itemb
                WHERE id = :SERVICE_ID
                  AND id_type='S'
                  AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
                  AND attr_field_code = to_char(a.discnt_code)
                  AND SYSDATE BETWEEN start_date AND end_date)