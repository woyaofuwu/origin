INSERT INTO TF_F_USER_DISCNT(partition_id,user_id,user_id_a,product_id,package_id,discnt_code,spec_tag,relation_type_code,inst_id,
    campn_id,start_date,end_date,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,
    rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3)
SELECT mod(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),-1,a.product_id,a.package_id,b.para_code2,'0',a.relation_type_code,a.inst_id,
    a.campn_id,a.start_date,a.end_date,sysdate,a.update_staff_id,a.update_depart_id,a.remark,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,
    a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,
    a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3
FROM tf_b_trade_discnt a,td_s_commpara b
WHERE a.trade_id=to_number(:TRADE_ID)
AND a.accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
AND a.modify_tag='0'
AND a.discnt_code=b.para_code1
AND b.param_attr='595'
AND sysdate BETWEEN b.start_date AND b.end_date
AND NOT EXISTS(SELECT 1 FROM tf_f_user_discnt
                WHERE user_id = to_number(:USER_ID)
                  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                  AND product_id = a.product_id
                  AND package_id = a.package_id
                  AND user_id_a = a.user_id_a
                  AND discnt_code= b.para_code2
                  AND end_date>start_date
                  AND end_date>a.start_date)