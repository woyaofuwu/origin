SELECT info_recv_id,
       report_serial_number,
       report_cust_name,
       report_cust_province,
       decode(SUBSTR(T.serv_request_type, 5, 2),'04','01','05','02','06','03','07','04',SUBSTR(T.serv_request_type, 5, 2)) || SUBSTR(T.serv_request_type, 9, 2) serv_request_type,
       recv_province,
       TO_CHAR(RECV_TIME, 'yyyy-mm-dd hh24:mi:ss') RECV_TIME,
       to_char(report_time,'yyyy-mm-dd hh24:mi:ss') report_time,
       to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
       badness_info,
       badness_info_province,
       target_province,
       decode(t.report_type_code,null,decode(SUBSTR(T.serv_request_type, 5, 2),'04','01','05','02','06','03','07','04',SUBSTR(T.serv_request_type, 5, 2)),t.report_type_code)report_type_code,
       recv_content,
       important_level,
       state,
       decode(state,'00','待回复','01','待处理','02','已回复','03','已退回','04','已催办','0A','已归档','0B','已短信通知用户',state) state_name,
       decode(state,'00','否','01','否','02','是','03','','04','','0A','','0B','是',state) badness_state,
       hasten_state,
       repeat_report,
       decode(repeat_report,'0','否','1','是',repeat_report) repeat_report_name,
       stick_list,
       record_file_list,
       deal_ramark,
       deal_remark_makeup,
       OPERATE_STEP,
       RSRV_STR1,
       T.RECV_IN_TYPE
  FROM tf_f_badness_info_log t
 WHERE 1 = 1
   AND (t.info_recv_id=:INFO_RECV_ID OR :INFO_RECV_ID IS NULL )
   AND (t.state=:STATE OR :STATE IS NULL)
   AND (t.recv_province=:RECV_PROVINCE OR :RECV_PROVINCE IS NULL)
   AND (t.badness_info_province = :BADNESS_INFO_PROVINCE OR :BADNESS_INFO_PROVINCE IS NULL)
   AND (t.report_cust_province = :REPORT_CUST_PROVINCE OR :REPORT_CUST_PROVINCE IS NULL)
   AND (t.badness_info = :BADNESS_INFO OR :BADNESS_INFO IS NULL)
   AND (t.report_serial_number = :REPORT_SERIAL_NUMBER OR :REPORT_SERIAL_NUMBER IS NULL)
   AND (t.serv_request_type = :SERV_REQUEST_TYPE OR :SERV_REQUEST_TYPE IS NULL)
   AND (trunc(t.report_time) >= to_date(:REPORT_START_TIME, 'yyyy-mm-dd') OR :REPORT_START_TIME IS NULL)
   AND (trunc(t.report_time) <= to_date(:REPORT_END_TIME, 'yyyy-mm-dd') OR :REPORT_END_TIME IS NULL)
   AND (t.report_type_code = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)
   ORDER BY T.REPORT_TIME  ,T.REPORT_SERIAL_NUMBER, T.STATE ASC