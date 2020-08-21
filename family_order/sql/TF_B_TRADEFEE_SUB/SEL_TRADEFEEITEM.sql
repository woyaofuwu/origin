SELECT fee_mode,
       '营业费用' fee_mode_name,
       fee_type_code,
       b.feeitem_name fee_type,
       TO_CHAR(a.oldfee / 100) oldfee,
       TO_CHAR(a.fee / 100) fee,
       '1' MUST_TAG
  FROM tf_b_tradefee_sub a, td_b_feeitem b
 WHERE a.fee_type_code = b.feeitem_code
   AND trade_id = :TRADE_ID
   AND (eparchy_code = :TRADE_EPARCHY_CODE or eparchy_code = 'ZZZZ')
   AND fee_mode = '0'