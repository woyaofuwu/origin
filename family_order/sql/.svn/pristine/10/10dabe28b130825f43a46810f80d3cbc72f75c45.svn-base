SELECT T.info_recv_id,
       T.report_serial_number,
       T.report_cust_name,
       coalesce(E.report_cust_province,t.report_cust_province) report_cust_province,
       REPORT_CUST_LEVEL,
       decode(SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 5, 2),'04','01','05','02','06','03','07','04',SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 5, 2)) || SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 9, 2) serv_request_type,
       case when length(coalesce(E.serv_request_type,t.serv_request_type)) >= 10 then SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 9, 2) else '' end as fourth_type_code,
       case when length(coalesce(E.serv_request_type,t.serv_request_type)) >= 12 then SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 9, 4) else '' end as fifth_type_code,
       case when length(coalesce(E.serv_request_type,t.serv_request_type)) >= 14 then SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 9, 6) else '' end as sixth_type_code,
       case when length(coalesce(E.serv_request_type,t.serv_request_type)) >= 16 then SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 9, 6) else '' end as seven_type_code,
       T.recv_province,
       TO_CHAR(T.RECV_TIME, 'yyyy-mm-dd hh24:mi:ss') RECV_TIME,
       to_char(T.report_time,'yyyy-mm-dd hh24:mi:ss') report_time,
       to_char(T.DEAL_DATE,'yyyy-mm-dd hh24:mi:ss') DEAL_DATE,
       to_char(T.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
       T.badness_info,
        coalesce(E.badness_info_province,t.badness_info_province)  badness_info_province ,
       T.target_province,
       coalesce(E.serv_request_type,t.serv_request_type) all_serv_request_type,
       decode( coalesce(E.REPORT_TYPE_CODE,t.REPORT_TYPE_CODE) ,null,decode(SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 5, 2),'04','01','05','02','06','03','07','04',SUBSTR(coalesce(E.serv_request_type,t.serv_request_type) , 5, 2)), coalesce(E.REPORT_TYPE_CODE,t.REPORT_TYPE_CODE) )report_type_code,
       T.recv_content,
       T.important_level,
       T.state,
       decode(T.state,'00','待回复','01','待处理','02','已回复','03','已退回','04','已催办','0A','已归档','0B','已短信通知用户',T.state) state_name,
       decode(T.state,'00','否','01','否','02','是','03','','04','','0A','','0B','是',T.state) badness_state,
       T.hasten_state,
       T.repeat_report,
       decode(repeat_report,'0','否','1','是',T.repeat_report) repeat_report_name,
       T.stick_list,
       T.record_file_list,
       T.deal_ramark,
       T.deal_remark_makeup,
       T.OPERATE_STEP,
       coalesce(E.serv_request_type,t.serv_request_type)  serv_request_type1,
       T.SORT_RESULT_TYPE,
       T.RSRV_STR1,
       T.RECV_IN_TYPE
  FROM tf_f_badness_info t, TI_B_BADNESS_EXTENDS E
         WHERE 1 = 1
           AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
   AND (t.info_recv_id=:INFO_RECV_ID OR :INFO_RECV_ID IS NULL )
   AND (t.state=:STATE OR :STATE IS NULL)
   AND (t.recv_province=:RECV_PROVINCE OR :RECV_PROVINCE IS NULL)
   AND (E.BADNESS_INFO_PROVINCE = :BADNESS_INFO_PROVINCE OR :BADNESS_INFO_PROVINCE IS NULL or t. badness_info_province=:BADNESS_INFO_PROVINCE )
   AND (E.REPORT_CUST_PROVINCE = :REPORT_CUST_PROVINCE OR :REPORT_CUST_PROVINCE IS NULL  or t. REPORT_CUST_PROVINCE=:REPORT_CUST_PROVINCE )
   AND (t.badness_info = :BADNESS_INFO OR :BADNESS_INFO IS NULL)
   AND (t.report_serial_number = :REPORT_SERIAL_NUMBER OR :REPORT_SERIAL_NUMBER IS NULL)
   AND (E.serv_request_type = :SERV_REQUEST_TYPE OR :SERV_REQUEST_TYPE IS NULL  or  (t.serv_request_type=:SERV_REQUEST_TYPE and t.RSRV_STR3='1BOSS'))
   AND (trunc(t.report_time) >= to_date(:REPORT_START_TIME, 'yyyy-mm-dd') OR :REPORT_START_TIME IS NULL)
   AND (trunc(t.report_time) <= to_date(:REPORT_END_TIME, 'yyyy-mm-dd') OR :REPORT_END_TIME IS NULL)
   AND (E.REPORT_TYPE_CODE = :REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL or t.REPORT_TYPE_CODE=:REPORT_TYPE_CODE )
   ORDER BY T.REPORT_TIME  DESC