INSERT INTO tf_b_trade_discnt(trade_id,accept_month,user_id,user_id_a,package_id,product_id,discnt_code,
     spec_tag,relation_type_code,inst_id,campn_id,start_date,end_date,modify_tag,update_time,
     update_staff_id,update_depart_id,remark)
SELECT TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH,TO_NUMBER(:USER_ID),user_id_a,package_id,product_id,TO_NUMBER(:DISCNT_CODE),
     spec_tag,relation_type_code,inst_id,campn_id,start_date,TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),'1',sysdate,
     :UPDATE_STAFF_ID,:UPDATE_DEPART_ID,:REMARK
FROM tf_f_user_discnt a
WHERE partition_id=MOD(to_number(:USER_ID),10000)
  AND user_id=to_number(:USER_ID)
  AND discnt_code=TO_NUMBER(:DISCNT_CODE)
  AND end_date>TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
  AND NOT EXISTS(SELECT 1 FROM tf_b_trade_discnt 
                   WHERE trade_id=TO_NUMBER(:TRADE_ID)
                     AND accept_month=:ACCEPT_MONTH
                     AND discnt_code=a.discnt_code
                     AND user_id=a.user_id
                     AND package_id=a.package_id
                     AND product_id=a.product_id
                     AND inst_id = a.inst_id
                     AND modify_tag='1')