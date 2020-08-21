select '' SUBSYS_CODE , 0 PARAM_ATTR , '' PARAM_CODE , '' PARAM_NAME , (select trade_type from td_s_tradetype where trade_type_code = a. trade_type_code ) para_code1, b.vpn_no para_code2, b.vpn_name para_code3, a.serial_number para_code4, c.res_code para_code5, to_char( a.accept_date, 'yyyy-mm-dd hh24:mi:ss' ) para_code6, (select staff_name from td_m_staff where staff_id = a.trade_staff_id ) para_code7, (select depart_name from td_m_depart where depart_id = a.trade_depart_id ) para_code8, '' PARA_CODE9 , '' PARA_CODE10 , '' PARA_CODE11 , '' PARA_CODE12 , '' PARA_CODE13 , '' PARA_CODE14 , '' PARA_CODE15 , '' PARA_CODE16 , '' PARA_CODE17 , '' PARA_CODE18 , '' PARA_CODE19 , '' PARA_CODE20 , '' PARA_CODE21 , '' PARA_CODE22 , '' PARA_CODE23 , '' PARA_CODE24 , '' PARA_CODE25 , '' PARA_CODE26 , '' PARA_CODE27 , '' PARA_CODE28 , '' PARA_CODE29 , '' PARA_CODE30 , '' START_DATE , '' END_DATE , '' EPARCHY_CODE , '' REMARK , '' UPDATE_STAFF_ID , '' UPDATE_DEPART_ID , '' UPDATE_TIME
from tf_bh_trade a, tf_b_trade_vpn b, tf_b_trade_res c
where a.trade_staff_id = :PARA_CODE1 and 
      a.accept_date >= TO_DATE(:PARA_CODE2, 'yyyy-mm-dd hh24:mi:ss') and
      a.accept_date <= TO_DATE(:PARA_CODE3, 'yyyy-mm-dd hh24:mi:ss') and 
      a.trade_id = b.trade_id and b.trade_id = c.trade_id and
      c.res_type_code = 'S' and
      (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL) and
      (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL) and
      (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL) and
      (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL) and
      (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL) and
      (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL) and
      (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)