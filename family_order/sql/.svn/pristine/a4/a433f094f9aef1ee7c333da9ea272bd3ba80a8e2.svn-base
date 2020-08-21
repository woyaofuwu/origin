INSERT INTO tf_b_trade_discnt(trade_id,accept_month,user_id,user_id_a,package_id,product_id,discnt_code,spec_tag,relation_type_code,
    inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark)
SELECT trade_id,accept_month,TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),:PACKAGE_ID,:PRODUCT_ID,:DISCNT_CODE,'0','',
     to_number(:INST_ID),to_number(:CAMPN_ID),open_date,ADD_MONTHS(TRUNC(open_date,'mm'),TO_NUMBER(:MONTHS))-1/24/3600,'0',
     sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,:REMARK
  FROM tf_b_trade_user
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                      AND discnt_code = :DISCNT_CODE
                      AND modify_tag = '0')