SELECT b.info_recv_id info_recv_id,
       b.deal_staff_id deal_staff_id,
       b.deal_depart_id deal_depart_id,
       b.contact_serial_number contact_serial_number,
       decode(b.state,'00','01','03','04','0A',02,'0B','02',b.state) state,
       a.repeat_report repeat_report,
       '' IS_VALID
 FROM TF_F_BADNESS_INFO a,TF_F_BADNESS_INO_DEAL b
WHERE a.info_recv_id=b.info_recv_id
AND a.state=b.state
AND a.info_recv_id = :INFO_RECV_ID