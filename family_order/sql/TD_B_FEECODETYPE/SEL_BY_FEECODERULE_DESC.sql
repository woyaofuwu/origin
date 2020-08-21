--IS_CACHE=Y
SELECT  eparchy_code,fee_code_rule_code,
        fee_code_type_code,fee_type_code,net_type_code,fee_code_rule,
        to_char(fee_code_fee) fee_code_fee,class_id,order_serial,
        to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,
        update_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,
        to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
        to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
        to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
        rsrv_str1,rsrv_str2,rsrv_str3
  FROM (SELECT  /*+INDEX(a PK_TD_B_FEECODETYPE )*/ eparchy_code,fee_code_rule_code,
                fee_code_type_code,fee_type_code,net_type_code,fee_code_rule,
                fee_code_fee,class_id,order_serial,
                update_time,update_staff_id,
                update_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,
                rsrv_date1,rsrv_date2,rsrv_date3,
                rsrv_str1,rsrv_str2,rsrv_str3 
           FROM td_b_feecodetype a
          WHERE eparchy_code=:EPARCHY_CODE
            AND fee_code_type_code||''=:FEE_CODE_TYPE_CODE
            AND RSRV_TAG1||''=:RSRV_TAG1
            AND (:NET_TYPE_CODE IS NULL OR net_type_code=:NET_TYPE_CODE) 
          ORDER BY order_serial DESC, fee_code_fee DESC
        )
 WHERE :SERIAL_NUMBER LIKE fee_code_rule
   AND ROWNUM < 2