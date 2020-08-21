insert into tf_b_trade_discnt(trade_id,accept_month,user_id,user_id_a,package_id,product_id,discnt_code,spec_tag,
   relation_type_code,inst_id,campn_id,start_date,end_date,modify_tag,
   update_time,update_staff_id,update_depart_id,remark)
select to_number(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),user_id,user_id_a,package_id,product_id,discnt_code,spec_tag,
   relation_type_code,inst_id,campn_id,start_date,TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-0.00001,'1',
   sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,'无线音乐会员优惠自动处理'
from tf_f_user_discnt a
 WHERE a.partition_id=mod(to_number(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.end_date > sysdate
   AND EXISTS (SELECT 1 FROM td_s_commpara
                WHERE param_attr = :PARAM_ATTR
                  AND para_code3 = a.discnt_code
                  AND (eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')
                  AND sysdate BETWEEN start_date AND end_date)