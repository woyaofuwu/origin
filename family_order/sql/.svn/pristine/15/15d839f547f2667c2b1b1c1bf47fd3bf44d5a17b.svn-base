Select TO_CHAR(A.ACCEPT_DATE, 'yyyy-mm-dd') As ACCEPT_DATE,
       TO_CHAR(A.CANCEL_DATE, 'yyyy-mm-dd') As CANCEL_DATE,
       A.SERIAL_NUMBER,
       B.RSRV_STR4 CITY_CODE,
       B.CONTACT_PHONE,
       A.TRADE_STAFF_ID CANCEL_STAFF_ID,
       (Select d.depart_name
          From td_m_depart d
         Where a.trade_depart_id = d.depart_id) CANCEL_DEPART_NAME,
       (Select d.depart_name
          From td_m_depart d
         Where a.cancel_depart_id = d.depart_id) TRADE_DEPART_NAME,
       B.STAND_ADDRESS,
       B.DETAIL_ADDRESS,
       A.CANCEL_STAFF_ID TRADE_STAFF_ID,
       Decode((Decode((Select substr(a.remark, 0, INSTR(a.remark, '|', 1, 1) - 1) From dual),
                      '1', '端口满',
                      '2', '录入地址不正确',
                      '3', '客户原因撤单',
                      '4', '其他',
                      (Select c.data_name From td_s_static c
                        Where c.data_id = substr(a.remark, 0, INSTR(a.remark, '|', 1, 1) - 1)
                          And c.type_id = 'WIDE_CANCEL_REASON')) || '|' ||
              (Select c.data_name From td_s_static c
                 Where c.data_id = substr(a.remark, 8, INSTR(a.remark, '|', 1, 2) - 8)
                   And c.type_id = 'WIDE_CANCEL_REASON_RELATION') || '|' ||
              (Select Case When length(regexp_replace(Replace(a.remark, '|', '@'), '[^@]+', '')) = 2 Then
                          (Select substr(a.remark, instr(a.remark, '|', 1, 2) + 1) From dual)
                         Else
                          (Select substr(a.remark, instr(a.remark, '|', 1, 2) + 1, instr(a.remark, '|', 1, 3) - instr(a.remark, '|', 1, 2) - 1) From dual)
                       End From dual)), '||', '',
              (Decode((Select substr(a.remark, 0, INSTR(a.remark, '|', 1, 1) - 1) From dual),
                      '1', '端口满',
                      '2', '录入地址不正确',
                      '3', '客户原因撤单',
                      '4', '其他',
                      (Select c.data_name From td_s_static c
                        Where c.data_id = substr(a.remark, 0, INSTR(a.remark, '|', 1, 1) - 1)
                          And c.type_id = 'WIDE_CANCEL_REASON')) || '|' ||
              (Select c.data_name From td_s_static c
                 Where c.data_id = substr(a.remark, 8, INSTR(a.remark, '|', 1, 2) - 8)
                   And c.type_id = 'WIDE_CANCEL_REASON_RELATION') || '|' ||
              (Select Case When length(regexp_replace(Replace(a.remark, '|', '@'), '[^@]+', '')) = 2 Then
                          (Select substr(a.remark, instr(a.remark, '|', 1, 2) + 1) From dual)
                         Else
                          (Select substr(a.remark, instr(a.remark, '|', 1, 2) + 1, instr(a.remark, '|', 1, 3) - instr(a.remark, '|', 1, 2) - 1) From dual)
                       End From dual))) CANCEL_REASON
  From TF_BH_TRADE A, TF_B_TRADE_WIDENET B
 Where A.ACCEPT_MONTH = TO_NUMBER(:ACCEPT_MONTH)
   And trunc(A.ACCEPT_DATE) >= TO_DATE(:START_DATE, 'yyyy-mm-dd')
   And trunc(A.ACCEPT_DATE) <= TO_DATE(:END_DATE, 'yyyy-mm-dd')
   And (A.SERIAL_NUMBER = 'KD_' || :SERIAL_NUMBER Or
       A.SERIAL_NUMBER = :SERIAL_NUMBER)
   And A.CANCEL_STAFF_ID = :CANCEL_STAFF_ID
   And A.TRADE_TYPE_CODE In (600, 612, 613, 622, 623, 606, 3800)
   And A.CANCEL_TAG = '3'
   And A.TRADE_ID = B.TRADE_ID
 Order By ACCEPT_DATE