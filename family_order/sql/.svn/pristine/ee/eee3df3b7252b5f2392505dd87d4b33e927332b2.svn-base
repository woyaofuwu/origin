SELECT a.serial_number para_code1,
       a.cust_name para_code2,
       decode(b.modify_tag,'0','新增','1','删除','2','修改','未知') para_code3,
       b.short_code para_code4,
       a.accept_date para_code5,
       a.trade_staff_id para_code6,
       a.trade_depart_id para_code7,
       '' para_code8, '' para_code9, '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
       '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_bh_trade a,tf_b_trade_relation b
 WHERE a.accept_date>=to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss')
   AND a.accept_date<=to_date(:PARA_CODE3,'yyyy-mm-dd hh24:mi:ss')
   AND a.trade_staff_id = :PARA_CODE1
   AND a.trade_type_code=260
   AND b.trade_id=a.trade_id
   AND b.accept_month=a.accept_month
   AND b.role_code_b='2'
   AND (:PARA_CODE4 IS NULL OR :PARA_CODE4='')
   AND (:PARA_CODE5 IS NULL OR :PARA_CODE5='')
   AND (:PARA_CODE6 IS NULL OR :PARA_CODE6='')
   AND (:PARA_CODE7 IS NULL OR :PARA_CODE7='')
   AND (:PARA_CODE8 IS NULL OR :PARA_CODE8='')
   AND (:PARA_CODE9 IS NULL OR :PARA_CODE9='')
   AND (:PARA_CODE10 IS NULL OR :PARA_CODE10='')