select area_code,prov_code,asp,home_type from td_msisdn where ( :SERIAL_NUMBER between begin_msisdn and end_msisdn ) 
    and ( to_char(sysdate,'yyyymmddhh24miss') between begin_time and end_time)