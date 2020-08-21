SELECT to_char(a.user_id) user_id,a.serial_number, a.purchase_attr, a.purchase_desc,
       a.purchase_mode, a.purchase_info,to_char(a.trade_id) trade_id,to_char(a.rpay_mpfee/100) rpay_mpfee,
       a.feeitem_code,to_char(a.rpay_foregift/100) rpay_foregift,a.foregift_code,to_char(a.rpay_deposit/100) rpay_deposit,
       a.rpay_deposit_code,to_char(a.left_deposit/100) left_deposit,to_char(a.mrtn_fee/100) mrtn_fee,a.rtn_type,
       a.rtn_months,a.left_months,to_char(a.gpay_deposit/100) gpay_deposit,a.gpay_deposit_code,
       to_char(a.left_gdeposit/100) left_gdeposit,to_char(a.mgift_fee/100) mgift_fee,a.gift_type,
       a.gtotal_months,a.gleft_months,to_char(a.month_fee/100) month_fee,a.ntotal_months,
       a.nleft_months,a.device_type,a.imei,a.assure_no,a.discnt_code,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,
       a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,
       a.process_tag,a.staff_id,a.depart_id,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
       a.remark  FROM tf_f_user_purchase a,
       (SELECT purchase_attr
          FROM td_b_purchasetrade
         WHERE para_code5 = (SELECT para_code5
                               FROM td_b_purchasetrade c, tf_f_user d
                              WHERE purchase_attr = :PURCHASE_ATTR
                                AND purchase_mode = :PURCHASE_MODE
                                AND (c.eparchy_code = d.eparchy_code or c.eparchy_code = 'ZZZZ')
                                AND d.user_id = :USER_ID)) b
 WHERE end_date > SYSDATE
   AND process_tag = :PROCESS_TAG
   AND user_id = :USER_ID
   AND a.purchase_attr = b.purchase_attr
   AND a.end_date >=
       (SELECT MAX(start_date)
          FROM tf_f_user_purchase a,
               (SELECT purchase_attr
                  FROM td_b_purchasetrade
                 WHERE para_code5 =
                       (SELECT para_code5
                          FROM td_b_purchasetrade c, tf_f_user d
                         WHERE purchase_attr = :PURCHASE_ATTR
                           AND purchase_mode = :PURCHASE_MODE
                           AND (c.eparchy_code = d.eparchy_code or c.eparchy_code = 'ZZZZ')
                           AND d.user_id = :USER_ID)) b
         WHERE end_date > SYSDATE
           AND process_tag = :PROCESS_TAG
           AND user_id = :USER_ID
           AND a.purchase_attr = b.purchase_attr)