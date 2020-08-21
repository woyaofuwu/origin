SELECT a.PROSECUTION_ID para_code1,a.serial_number para_code2,b.cust_name para_code3,
       d.area_name para_code4,b.home_address para_code23,a.PROSECUTEE_NUMBER para_code6,
       a.PROSECUTION_INFO para_code24,a.trade_staff_id para_code8,a.trade_depart_id para_code9,
       to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') para_code21,
       decode(a.prosecution_way,'00','10086999','01','营业厅方式','未知方式') para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,'' para_code22,
       '' para_code5,'' para_code7,'' para_code25,'' para_code26,'' para_code27,'' para_code28,
       '' para_code29,'' para_code30,'' start_date,'' end_date,'' eparchy_code,a.remark remark,
       '' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,
       0 param_attr,'' param_code,'' param_name
  FROM TF_F_USER_PROSECUTION a, TF_F_CUST_PERSON b, TF_F_USER c,TD_M_AREA d
 WHERE    a.serial_number = c.serial_number
   AND c.remove_tag = '0'
   AND b.cust_id = c.cust_id
   AND c.city_code = d.area_code
   AND  (a.accept_date > TO_DATE(:PARA_CODE2, 'yyyy-mm-dd hh24:mi:ss'))
   AND (a.accept_date < TO_DATE(:PARA_CODE3, 'yyyy-mm-dd hh24:mi:ss') )
   AND (a.serial_number = :PARA_CODE1 OR :PARA_CODE1 IS NULL )
   AND (a.PROSECUTEE_NUMBER = :PARA_CODE4  OR :PARA_CODE4 IS NULL )   
   AND (a.prosecution_way = :PARA_CODE5  OR :PARA_CODE5 IS NULL)