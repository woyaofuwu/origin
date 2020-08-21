SELECT b.partition_id,to_char(b.user_id) user_id,to_char(b.serial_number) serial_number, b.BIZ_CODE,b.BIZ_NAME,to_char(b.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(b.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,b.ec_user_id,to_char(b.ec_serial_number) ec_serial_number,b.rsrv_num1,b.rsrv_num2,b.rsrv_num3,to_char(b.rsrv_num4) rsrv_num4,to_char(b.rsrv_num5) rsrv_num5,b.RSRV_STR1,b.RSRV_STR2,to_char(b.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(b.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(b.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,b.remark,b.update_staff_id,b.update_depart_id,to_char(b.update_time,'yyyy-mm-dd hh24:mi:ss') update_time 

,a.biz_type_code RSRV_STR3,decode (a.biz_status,'A','正常','N','暂停','S','内部测试','T','测试待审','R','试商用','E','终止') RSRV_STR4,decode (a.rb_list,'0','定购','1','白名单','2','黑名单') RSRV_STR5

  FROM TF_F_USER_GRPMBMP a ,TF_F_USER_GRPMBMP_SUB b

  where a.serial_number=b.EC_SERIAL_NUMBER

    and    a.BIZ_CODE=b.BIZ_CODE

    and    a.BIZ_CODE=:BIZ_CODE
    and    a.end_date>sysdate
    and    b.end_date>sysdate