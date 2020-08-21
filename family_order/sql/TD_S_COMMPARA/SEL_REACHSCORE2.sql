SELECT a.user_id para_code1,a.serial_number para_code2,b.cust_name para_code3,
a.score_value para_code4, a.open_date para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user a ,tf_f_customer b
 WHERE a.cust_id = b.cust_id
    AND b.partition_id=MOD(TO_NUMBER(a.cust_id),10000)
    AND a.remove_tag = '0'
    AND a.brand_code = 'G001'
    AND a.score_value >= :PARA_CODE1     
    AND a.score_value <= :PARA_CODE2
    AND a.city_code = :PARA_CODE3
    AND a.serial_number >= :PARA_CODE4
    AND a.serial_number <= :PARA_CODE5
    AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
    AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
    AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
    AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
    AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)