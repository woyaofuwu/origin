select * from td_b_noteitem noteitem where noteitem.templet_code='50000001' 
   and noteitem.print_level=1 and noteitem.parent_item_code=:PARENT_ITEM_CODE
   and exists (
 select 1     
 FROM tf_f_user_payitem payitem
 WHERE USER_ID=:USER_ID
  AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)
  AND PLAN_ID=:PLAN_ID
  AND ACT_TAG='1'
  and (rsrv_str1=:RSRV_STR1 or :RSRV_STR1 is null)
  AND START_CYCLE_ID<=to_char(SYSDATE,'yyyyMMdd')
  AND END_CYCLE_ID>=to_char(SYSDATE,'yyyyMMdd')
  and payitem.payitem_code=noteitem.note_item_code
)