SELECT clct_day,
       staff_id,
       hsn_duns,
       SUM(NVL(sale_money,0))/1000 recv_fee,
       DECODE(fee_dir,'0','缴入','支取') ibbfeedir,
       SUM(NVL(sale_num,0)) recv_fee_num,
       DECODE(cancel_tag,'0','正常','返销') cancel_tag,
       DECODE(bip_status,'0','成功','失败') bipstatus
  FROM ts_s_ibpfee_day_staff a
 WHERE a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND a.staff_id >= :start_staff_id
   AND a.staff_id <= :end_staff_id 
   AND fee_dir_tag = '0'
 GROUP BY clct_day,staff_id,hsn_duns,fee_dir,cancel_tag,bip_status