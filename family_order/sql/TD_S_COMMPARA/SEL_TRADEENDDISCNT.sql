SELECT subsys_code,param_attr,param_code,param_name,para_code1,para_code2,para_code3,para_code4,para_code5,
  para_code6,para_code7,para_code8,para_code9,para_code10,para_code11,para_code12,para_code13,para_code14,
  para_code15,para_code16,para_code17,para_code18,para_code19,para_code20,para_code21,para_code22,para_code23,
  para_code24,para_code25,to_char(para_code26,'yyyy-mm-dd hh24:mi:ss') para_code26,
  to_char(para_code27,'yyyy-mm-dd hh24:mi:ss') para_code27,
  to_char(para_code28,'yyyy-mm-dd hh24:mi:ss') para_code28,
  to_char(para_code29,'yyyy-mm-dd hh24:mi:ss') para_code29,
  to_char(para_code30,'yyyy-mm-dd hh24:mi:ss') para_code30,
  to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
  to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
  eparchy_code,a.remark,a.update_staff_id,a.update_depart_id,
  to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_s_commpara a,tf_f_user_discnt b
 WHERE b.User_Id = TO_NUMBER(:USER_ID)
   AND b.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND SYSDATE BETWEEN b.Start_Date AND b.end_date
   AND a.param_attr = 978
   AND a.param_code = :TRADE_TYPE_CODE
   AND (a.para_code2 = :PRODUCT_ID OR a.para_code2 = '-1')
   AND (a.eparchy_code = :EPARCHY_CODE OR a.eparchy_code = 'ZZZZ')
   AND a.para_code1 = to_char(b.discnt_code)
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt
                    WHERE trade_id = :TRADE_ID
                      AND accept_month = :ACCEPT_MONTH
                      AND modify_tag = '1'
                      AND a.para_code1 = to_char(discnt_code))