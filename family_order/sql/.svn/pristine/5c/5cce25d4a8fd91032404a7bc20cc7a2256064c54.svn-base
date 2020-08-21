SELECT T.info_recv_id,
       T.report_serial_number,
       T.report_cust_name,
       coalesce(E.report_cust_province,t.report_cust_province) report_cust_province,
       decode(T.REPORT_CUST_LEVEL,'04','0','03','1','02','2','01','3',T.REPORT_CUST_LEVEL ) REPORT_CUST_LEVEL,
       decode(SUBSTR(coalesce(E.serv_request_type,t.serv_request_type), 5, 2),'04','01','05','02','06','03','07','04',SUBSTR(coalesce(E.serv_request_type,t.serv_request_type), 5, 2)) || SUBSTR(coalesce(E.serv_request_type,t.serv_request_type), 9, 2) serv_request_type,
       T.recv_province,
       to_char(T.report_time,'yyyy-mm-dd hh24:mi:ss') report_time,
       to_char(T.recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,
       to_char(T.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
       T.badness_info,
       coalesce(E.badness_info_province,t.badness_info_province) badness_info_province,
       T.target_province,
       decode(coalesce(E.REPORT_TYPE_CODE,t.REPORT_TYPE_CODE),null,decode(SUBSTR(coalesce(E.REPORT_TYPE_CODE,t.REPORT_TYPE_CODE), 5, 2),'04','01','05','02','06','03','07','04',SUBSTR(coalesce(E.serv_request_type,t.serv_request_type), 5, 2)),coalesce(E.REPORT_TYPE_CODE,t.REPORT_TYPE_CODE))report_type_code,
       T.recv_content,
       T.important_level,
       T.state,
       decode(T.state,'00','待回复','01','待处理','02','已回复','03','已退回','04','已催办','0A','已归档','0B','已短信通知用户',T.state) state_name,
       T.hasten_state,
       T.repeat_report,
       decode(T.repeat_report,'0','否','1','是',T.repeat_report) repeat_report_name,
       T.deal_ramark,
       T.deal_remark_makeup,
       T.OPERATE_STEP,
       T.RSRV_STR1
  FROM tf_f_badness_info t, TI_B_BADNESS_EXTENDS E
         WHERE 1 = 1
           AND T.INFO_RECV_ID=E.INFO_RECV_ID(+)
   AND (t.info_recv_id=:INFO_RECV_ID OR :INFO_RECV_ID IS NULL )
   AND (t.state=:STATE OR :STATE IS NULL)
   AND (t.recv_province=:RECV_PROVINCE OR :RECV_PROVINCE IS NULL)
   AND (E.BADNESS_INFO_PROVINCE != :BADNESS_INFO_PROVINCE or t. badness_info_province=:BADNESS_INFO_PROVINCE)
   AND (E.REPORT_CUST_PROVINCE = :REPORT_CUST_PROVINCE or t. REPORT_CUST_PROVINCE=:REPORT_CUST_PROVINCE)
   AND (t.info_recv_id = :INFO_RECV_ID OR :INFO_RECV_ID IS NULL)
   AND (t.badness_info = :BADNESS_INFO OR :BADNESS_INFO IS NULL)
   AND (t.report_serial_number = :REPORT_SERIAL_NUMBER OR :REPORT_SERIAL_NUMBER IS NULL)
   AND (E.serv_request_type = :SERV_REQUEST_TYPE or  (t.serv_request_type=:SERV_REQUEST_TYPE and  t.RSRV_STR3='1BOSS') OR :SERV_REQUEST_TYPE IS NULL)
   AND (trunc(t.report_time) >= to_date(:REPORT_START_TIME, 'yyyy-mm-dd') OR :REPORT_START_TIME IS NULL)
   AND (trunc(t.report_time) <= to_date(:REPORT_END_TIME, 'yyyy-mm-dd') OR :REPORT_END_TIME IS NULL)
   AND (E.REPORT_TYPE_CODE = :REPORT_TYPE_CODE or t.REPORT_TYPE_CODE=:REPORT_TYPE_CODE OR :REPORT_TYPE_CODE IS NULL)
   ORDER BY T.REPORT_TIME  ,T.REPORT_SERIAL_NUMBER, T.STATE ASC