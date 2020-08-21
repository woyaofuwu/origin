select to_char(e.PARTITION_ID) PARTITION_ID,
       to_char(e.USER_ID) USER_ID,
       to_char(e.SERVICE_ID) SERVICE_ID,
       e.serial_number,
       e.info_code,
       DECODE(E.INFO_CODE,'AIOBS_PASSWORD',F_DECRYPT(E.INFO_VALUE),E.INFO_VALUE) INFO_VALUE,
       e.info_name,
       to_char(e.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       e.update_staff_id,
       e.update_depart_id,
       e.remark,
       to_char(e.rsrv_num1) rsrv_num1,
       to_char(e.rsrv_num2) rsrv_num2,
       e.rsrv_str1,
       e.rsrv_str2,
       to_char(e.rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(e.rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(e.rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3
       
       from TF_F_USER_PLATSVC_ATTR e 
       where e.user_id = :USER_ID
       AND e.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
       and e.info_code = :INFO_CODE
       and e.SERVICE_ID = TO_NUMBER(:SERVICE_ID)