SELECT /*+ first_rows(1)  index(t pk_tf_r_ticket)*/COUNT(1) recordcount
  FROM tf_r_ticket t
  WHERE t.ticket_id = :TICKET_ID
  AND t.ticket_state_code= :TICKET_STATE_CODE
  AND t.tax_no = :TAX_NO
  and rownum < 2