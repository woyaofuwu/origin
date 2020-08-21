select a.user_id,
       a.city_code,
       a.serial_number,
       a.sim_card_no,
       a.staff_id,
       a.stock_id,
       decode(a.stock_id,
              '',
              '',
              (select depart_name
                 from td_m_depart
                where depart_id = a.stock_id)),
       a.fee_code,
       b.item_name,
       to_char(sum(a.fee)/1000,'99999999990.99') fee
  from tf_b_simcardmissbill a, td_b_detailitem b
 where a.accept_date = :ACCEPT_DATE
   and a.city_code = :CITY_CODE
   and a.fee_code = b.item_id
 group by a.serial_number,
          a.fee_code,
          a.USER_ID,
          a.CITY_CODE,
          a.SIM_CARD_NO,
          a.STAFF_ID,
          a.STOCK_ID,
          b.item_name