SELECT A.eparchy_code,
       to_char(A.charge_id) charge_id,
       to_char(apply_id) apply_id,
       A.serial_number,
       A.city_code,
       to_char(a.user_id) user_id,
       to_char(a.acct_id) acct_id,
       to_char(a.recv_fee) recv_fee,
       a.deposit_code,
       to_char(a.audit_batch_no) audit_batch_no,
       to_char(a.link_charge_id) link_charge_id,
       a.touch_type_id,
       to_char(a.ticket_id) ticket_id,
       a.charge_source_code,
       to_char(a.recv_time, 'yyyy-mm-dd hh24:mi:ss') recv_time,
       a.recv_eparchy_code,
       a.recv_city_code,
       a.recv_depart_id,
       a.recv_staff_id,
       to_char(trade_time, 'yyyy-mm-dd hh24:mi:ss') trade_time,
       trade_eparchy_code,
       trade_city_code,
       trade_depart_id,
       trade_staff_id,
       to_char(chk_time, 'yyyy-mm-dd hh24:mi:ss') chk_time,
       chk_eparchy_code,
       chk_city_code,
       chk_depart_id,
       chk_staff_id,
       chk_tag,
       deal_tag,
       chk_remark,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       b.cancel_tag rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       rsrv_str8,
       rsrv_str9,
       rsrv_str10,
       remark
  FROM tf_a_cancelverify a, tf_a_paylog b
 WHERE (trade_eparchy_code = :TRADE_EPARCHY_CODE OR :TRADE_EPARCHY_CODE IS NULL)
   AND (trade_city_code = :TRADE_CITY_CODE OR :TRADE_CITY_CODE IS NULL)
   AND (trade_depart_id = :TRADE_DEPART_ID OR :TRADE_DEPART_ID IS NULL)
   AND (trade_staff_id = :TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL)
   AND deal_tag = :DEAL_TAG
   AND chk_tag = :CHK_TAG
   and a.charge_id = b.charge_id
   and (b.cancel_tag = :RSRV_STR4 OR :RSRV_STR4 IS NULL)