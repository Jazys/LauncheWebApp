import qrcode
import zlib

strText="""2021-02-18 06:01:03,711 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"138"},{"fsk_br":"600"},{"trame":"2a04b12107680f"}]}'}
2021-02-18 06:01:03,729 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:03,891 INFO     periph_SwingRaw AgentOmt[736] E > a12a04b12107680f
2021-02-18 06:01:03,904 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:03.898 SITR-OMT> pc:88, pa:138, RR                                                , (nr:1)           ,  (2a04b12107680f)"}
2021-02-18 06:01:05,958 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:05.953 OMT-SITR> pc:88, pa:138, Fin de dialogue                                   , ()               ,  (2804b1fd077bef)"}
2021-02-18 06:01:06,121 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:06,131 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:07,172 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:07,189 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:01:10,674 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2c04b100130109edf5"}]}'}
2021-02-18 06:01:10,787 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:01:11,100 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:01:11,215 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:11,373 INFO     periph_SwingRaw AgentOmt[736] E > a12c04b100130109edf5
2021-02-18 06:01:11,387 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:11.380 SITR-OMT> pc:88, pa:139, Demande de controle general                       , (nr:0,ns:0)      ,  (2c04b100130109edf5)"}
2021-02-18 06:01:13,655 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:13.646 OMT-SITR> pc:88, pa:139, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (2e04b120070101083f00000eb055)"}
2021-02-18 06:01:13,921 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2e04b121077822"}]}'}
2021-02-18 06:01:13,932 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:14,099 INFO     periph_SwingRaw AgentOmt[736] E > a12e04b121077822
2021-02-18 06:01:14,117 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:14.108 SITR-OMT> pc:88, pa:139, RR  
2021-02-18 06:01:03,904 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:03.898 SITR-OMT> pc:88, pa:138, RR                                                , (nr:1)           ,  (2a04b12107680f)"}
2021-02-18 06:01:05,958 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:05.953 OMT-SITR> pc:88, pa:138, Fin de dialogue                                   , ()               ,  (2804b1fd077bef)"}
2021-02-18 06:01:06,121 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:06,131 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:07,172 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:07,189 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:01:10,674 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2c04b100130109edf5"}]}'}
2021-02-18 06:01:10,787 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:01:11,100 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:01:11,215 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:11,373 INFO     periph_SwingRaw AgentOmt[736] E > a12c04b100130109edf5
2021-02-18 06:01:11,387 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:11.380 SITR-OMT> pc:88, pa:139, Demande de controle general                       , (nr:0,ns:0)      ,  (2c04b100130109edf5)"}
2021-02-18 06:01:13,655 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:13.646 OMT-SITR> pc:88, pa:139, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (2e04b120070101083f00000eb055)"}
2021-02-18 06:01:13,921 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2e04b121077822"}]}'}
2021-02-18 06:01:13,932 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:14,099 INFO     periph_SwingRaw AgentOmt[736] E > a12e04b121077822
2021-02-18 06:01:14,117 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:14.108 SITR-OMT> pc:88, pa:139, RR 
"""

strText=" super toto est dans la nature avec tutu tu il toto toioioezjh"
cle=24 # Décalage par rapport à Y (code ASCII : 24 + 1 = 25e lettre de l'alphabet)

acrypter=strText.upper()
print (acrypter)
lg=len(acrypter)
MessageCrypte=""

for i in range(lg):
    if acrypter[i]==' ':
        MessageCrypte+=' '
    else:
        asc=ord(acrypter[i])+cle
        MessageCrypte+=chr(asc+26*((asc<0)-(asc>254)))

print (MessageCrypte)

zlibCompress=zlib.compress(MessageCrypte.encode(),6)
zlibCompressStr=(''.join(format(x, '02x') for x in zlibCompress))
print (''.join(format(x, '02x') for x in zlibCompress))

print(str(len(strText))+" : "+str(len(zlibCompressStr))+ " : "+str(len(zlibCompress)))

img = qrcode.make(zlibCompressStr)
print(type(img))
print(img.size)

MessageCrypte = str(zlib.decompress(zlibCompress).decode("utf-8"))
print (MessageCrypte)
MessageClair=""
cle=24 # Décalage par rapport à Y (code ASCII : 24 + 1 = 25e lettre de l'alphabet)

for i in range(lg):
    if MessageCrypte[i]==' ':
        MessageClair+=' '
    else:
        asc=ord(MessageCrypte[i])-cle
        MessageClair+=chr(asc+26*((asc<0)-(asc>254)))

print (MessageClair)



img.save('qrcode_test.png')

exit(0)

import pypandoc
import gzip
# Example file:
docxFilename = 'cgu.docx'
output = pypandoc.convert_file(docxFilename, 'json', outputfile="somefile.json")
assert output == ""

import pypandoc
docxFilename = 'somefile.json'
output = pypandoc.convert_file(docxFilename, 'docx', outputfile="text.docx")
assert output == ""




strText="""2021-02-18 06:01:03,711 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"138"},{"fsk_br":"600"},{"trame":"2a04b12107680f"}]}'}
2021-02-18 06:01:03,729 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:03,891 INFO     periph_SwingRaw AgentOmt[736] E > a12a04b12107680f
2021-02-18 06:01:03,904 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:03.898 SITR-OMT> pc:88, pa:138, RR                                                , (nr:1)           ,  (2a04b12107680f)"}
2021-02-18 06:01:05,958 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:05.953 OMT-SITR> pc:88, pa:138, Fin de dialogue                                   , ()               ,  (2804b1fd077bef)"}
2021-02-18 06:01:06,121 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:06,131 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:07,172 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:07,189 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:01:10,674 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2c04b100130109edf5"}]}'}
2021-02-18 06:01:10,787 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:01:11,100 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:01:11,215 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:11,373 INFO     periph_SwingRaw AgentOmt[736] E > a12c04b100130109edf5
2021-02-18 06:01:11,387 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:11.380 SITR-OMT> pc:88, pa:139, Demande de controle general                       , (nr:0,ns:0)      ,  (2c04b100130109edf5)"}
2021-02-18 06:01:13,655 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:13.646 OMT-SITR> pc:88, pa:139, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (2e04b120070101083f00000eb055)"}
2021-02-18 06:01:13,921 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2e04b121077822"}]}'}
2021-02-18 06:01:13,932 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:14,099 INFO     periph_SwingRaw AgentOmt[736] E > a12e04b121077822
2021-02-18 06:01:14,117 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:14.108 SITR-OMT> pc:88, pa:139, RR  
"""

str3="""2021-02-18 06:01:03,711 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"138"},{"fsk_br":"600"},{"trame":"2a04b12107680f"}]}'}
2021-02-18 06:01:03,729 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:03,891 INFO     periph_SwingRaw AgentOmt[736] E > a12a04b12107680f
2021-02-18 06:01:03,904 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:03.898 SITR-OMT> pc:88, pa:138, RR                                                , (nr:1)           ,  (2a04b12107680f)"}
2021-02-18 06:01:05,958 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:05.953 OMT-SITR> pc:88, pa:138, Fin de dialogue                                   , ()               ,  (2804b1fd077bef)"}
2021-02-18 06:01:06,121 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:06,131 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:07,172 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:07,189 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:01:10,674 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2c04b100130109edf5"}]}'}
2021-02-18 06:01:10,787 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:01:11,100 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:01:11,215 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:11,373 INFO     periph_SwingRaw AgentOmt[736] E > a12c04b100130109edf5
2021-02-18 06:01:11,387 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:11.380 SITR-OMT> pc:88, pa:139, Demande de controle general                       , (nr:0,ns:0)      ,  (2c04b100130109edf5)"}
2021-02-18 06:01:13,655 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:13.646 OMT-SITR> pc:88, pa:139, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (2e04b120070101083f00000eb055)"}
2021-02-18 06:01:13,921 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"139"},{"fsk_br":"600"},{"trame":"2e04b121077822"}]}'}
2021-02-18 06:01:13,932 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:14,099 INFO     periph_SwingRaw AgentOmt[736] E > a12e04b121077822
2021-02-18 06:01:14,117 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:14.108 SITR-OMT> pc:88, pa:139, RR                                                , (nr:1)           ,  (2e04b121077822)"}
2021-02-18 06:01:16,130 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:16.120 OMT-SITR> pc:88, pa:139, Fin de dialogue                                   , ()               ,  (2c04b1fd076bc2)"}
2021-02-18 06:01:16,345 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:16,351 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:17,381 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:17,394 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:01:20,719 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"93"},{"fsk_br":"600"},{"trame":"7402b1001301093441"}]}'}
2021-02-18 06:01:20,783 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:01:21,052 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:01:21,169 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:21,329 INFO     periph_SwingRaw AgentOmt[736] E > a17402b1001301093441
2021-02-18 06:01:21,338 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:21.333 SITR-OMT> pc:88, pa:93, Demande de controle general                       , (nr:0,ns:0)      ,  (7402b1001301093441)"}
2021-02-18 06:01:23,396 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:23.389 OMT-SITR> pc:88, pa:93, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (7602b120070101080000000e9816)"}
2021-02-18 06:01:23,672 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"93"},{"fsk_br":"600"},{"trame":"7602b12107a046"}]}'}
2021-02-18 06:01:23,679 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:23,841 INFO     periph_SwingRaw AgentOmt[736] E > a17602b12107a046
2021-02-18 06:01:23,854 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:23.850 SITR-OMT> pc:88, pa:93, RR                                                , (nr:1)           ,  (7602b12107a046)"}
2021-02-18 06:01:25,657 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:25.649 OMT-SITR> pc:88, pa:93, Fin de dialogue                                   , ()               ,  (7402b1fd07b3a6)"}
2021-02-18 06:01:25,870 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:25,881 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:26,917 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:26,973 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:01:30,710 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"234"},{"fsk_br":"600"},{"trame":"a806b100130100000b1424"}]}'}
2021-02-18 06:01:30,770 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:01:31,092 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:01:31,201 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:31,361 INFO     periph_SwingRaw AgentOmt[736] E > a1a806b100130100000b1424
2021-02-18 06:01:31,376 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:31.371 SITR-OMT> pc:88, pa:234, Demande de controle general                       , (nr:0,ns:0)      ,  (a806b100130100000b1424)"}
2021-02-18 06:01:33,482 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:33.476 OMT-SITR> pc:88, pa:234, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (aa06b120070101088000000e4186)"}
2021-02-18 06:01:33,761 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"234"},{"fsk_br":"600"},{"trame":"aa06b121074bbc"}]}'}
2021-02-18 06:01:33,774 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:33,936 INFO     periph_SwingRaw AgentOmt[736] E > a1aa06b121074bbc
2021-02-18 06:01:33,946 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:33.942 SITR-OMT> pc:88, pa:234, RR                                                , (nr:1)           ,  (aa06b121074bbc)"}
2021-02-18 06:01:35,743 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:01:35.739 OMT-SITR> pc:88, pa:234, Fin de dialogue                                   , ()               ,  (a806b1fd07585c)"}
2021-02-18 06:01:35,962 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:01:35,972 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:01:37,001 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:01:37,059 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:06:16,647 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"138"},{"fsk_br":"600"},{"trame":"2804b1001301099b9a"}]}'}
2021-02-18 06:06:16,707 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:06:16,972 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:06:17,086 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:06:17,249 INFO     periph_SwingRaw AgentOmt[736] E > a12804b1001301099b9a
2021-02-18 06:06:17,261 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:06:17.256 SITR-OMT> pc:88, pa:138, Demande de controle general                       , (nr:0,ns:0)      ,  (2804b1001301099b9a)"}
2021-02-18 06:06:19,529 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:06:19.522 OMT-SITR> pc:88, pa:138, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (2a04b120070101083f00100e57c5)"}
2021-02-18 06:06:19,746 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"138"},{"fsk_br":"600"},{"trame":"2a04b12107680f"}]}'}
2021-02-18 06:06:19,761 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:06:19,919 INFO     periph_SwingRaw AgentOmt[736] E > a12a04b12107680f
2021-02-18 06:06:19,934 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:06:19.928 SITR-OMT> pc:88, pa:138, RR                                                , (nr:1)           ,  (2a04b12107680f)"}
2021-02-18 06:06:21,996 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:06:21.990 OMT-SITR> pc:88, pa:138, Fin de dialogue                                   , ()               ,  (2804b1fd077bef)"}
2021-02-18 06:06:22,259 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:06:22,271 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:06:23,299 INFO     periph_SwingRaw AgentOmt[736] E > d0
2021-02-18 06:06:23,357 INFO     protocol_swing AgentOmt[736] R > Passage en Mode DIGITAL
2021-02-18 06:19:50,693 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"355"},{"fsk_br":"600"},{"trame":"8c0ab100130109d129"}]}'}
2021-02-18 06:19:50,811 INFO     periph_SwingRaw AgentOmt[736] E > a3
2021-02-18 06:19:51,073 INFO     protocol_swing AgentOmt[736] R > Passage en Mode ANALOG
2021-02-18 06:19:51,182 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:19:51,341 INFO     periph_SwingRaw AgentOmt[736] E > a18c0ab100130109d129
2021-02-18 06:19:51,353 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:19:51.349 SITR-OMT> pc:88, pa:355, Demande de controle general                       , (nr:0,ns:0)      ,  (8c0ab100130109d129)"}
2021-02-18 06:19:53,663 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:19:53.658 OMT-SITR> pc:88, pa:355, Message de controle general PA 1/4 dynamique      , (nr:1,ns:0,ack)  ,  (8e0ab120070104082a00000e6c20)"}
2021-02-18 06:19:53,830 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"FFSK_TX":[{"num_ERA":"355"},{"fsk_br":"600"},{"trame":"8e0ab12107fe66"}]}'}
2021-02-18 06:19:53,843 INFO     periph_SwingRaw AgentOmt[736] E > c1420258
2021-02-18 06:19:54,007 INFO     periph_SwingRaw AgentOmt[736] E > a18e0ab12107fe66
2021-02-18 06:19:54,021 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:19:54.013 SITR-OMT> pc:88, pa:355, RR                                                , (nr:1)           ,  (8e0ab12107fe66)"}
2021-02-18 06:19:56,092 INFO     periph_CrrZmqBus AgentOmt[736] E > (/somero/omt) {"msg": "2021-02-18 06:19:56.087 OMT-SITR> pc:88, pa:355, Fin de dialogue                                   , ()               ,  (8c0ab1fd07ed86)"}
2021-02-18 06:19:56,314 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr': u'primaire', u'msg': u'{"ANALOG_STOP":[]}'}
2021-02-18 06:19:56,326 INFO     periph_SwingRaw AgentOmt[736] E > c1420258"""
strTextCmp="""2021-02-18 06:01:03,711 INFO     periph_CrrZmqBus AgentOmt[736] R > {u'topic': u'/somero/omt/down/bs/104', u'crr`B@!primaire`B1!msg`B1!{"FFSK_TX":[{"num_ERA":"138"},{"fsk_br":"600`B-!trame":"2a04b12107680f"}]}'}
`R8#29`N8#SwingRaw`L7#E > c1420258`Rh!89`O&$`Xh!a1`Kd"`Rp!904`gz$E > (`Hn$) {"msg": "`Pw!.898 SITR-OMT> pc:88, pa:138, RR`B0"`B!!`G&!`Q0!`E$!, (nr:1)`J/! (`KD#)"`Q+%5,958`~G#`SG#5.953 OMT-SITR`NG#Fin de dialogue`dG#`IC#`FG#804b1fd077be`TG#6,12`~r)`tr)ANALOG_STOP":[`T9)6,13`kK(`X9)7,172`jh!d0`Sb!8`Im*rotocol_swing`P1$Passage en Mode DIGITAL`Ov!10,67`h'*`~/%`b'.9`\'.c04b100130109edf5`U+.10,787`k=-3`Pb!1,100`y~$`C['`Ru!215`~O'`Fh!373`ml/`NK$`St!`PE$`~E-`Cw!.380`Tp/9, Demande de controle general`Zp/0,ns:0`DN-`BE-`NH#`RI-13,65`O3%`~K#`Cw!.646`Tt/9, Me`Cx&`QK#PA 1/4 dynamiq`E30`DA2`BK#,ack)`DK#e`BA20070101083f00000eb05`VU#9`~$0`~o+`mo+`CC#1077822`Vk+3,93`ko/`X**4,09`kh9a1`Kd"`Rp!11`~&*`TU'4.10`Uz89`~z8`IO6`JD#`SG'6,13`I4/`~G#`Iw!.120`W"*`~z8`LQ,fd076bc`VG#34`hM,`~r)`nz816,35`~z8`Eh!7,38`kh!`Qz817,39`I~7`~z8`Fv!20,71`O{*`~/%`x'.93`[&.7402`G|23441`U*.20,78`lJ6`Py821,05`Ip.`~y8`Hu!16`~kG`Gh!3`lYHa1`OK$`St!3`~DE`SD-21.333`Ro/93`~x8`Hx8`OG#`RG-23,396`~J#`SJ#3.389`Rr/93`~w8`Hw87602`Iw8`Bu800e9816`UT#6`PPE`~l+`~l+`Xl+`CB#107a046`Vh+3,67`~'*`Gh!84`mvP`Kd"`Sp!5`~vP`VS'85`S"B93`~u8`HJ6`KC#`SE'5,65`~B;`TF#5.64`W})`~t8`LM,fd07b3a`VF#87`hA;`~o)`nt825,8`l(8`X7)6,9`P=@`mt826,9`J)K`~t8`Fv!30,71`~/%`~$.`I$.234`[%.a806`Fy20000b1424`U-.30,7`PS'`my831,09`~y8`Qy831,20`~S'`Gh!36`mn/`SO$`Sx!7`~O6`SL-31.371`Rv/234`~~8`H~8`SL#`RU-33,48`hJ6`kO#3.47`S"Q`BO#`~%9`F%9aa06`I%9`D&9e418`S0033,7`P('`~{+`~{+`Y{+`CC#1074bbc`Vs+3,7`P3^`t2*3,93`O4)`[2*`Jd"`Sp!4`~**`VU'942`W**`~'9`G**`JD#`SG'5,74`O#H`~G#`Cw!.73`S'9`BG#`~(9`LU,fd07585`VG#96`~{A`~(9`W(935,9`l4h`X9)7,0`l=3`Q(937,05`~"i`P(96:16,64`he?`~/%`~-v":"`CNo`E'K9b9a`S+.`B<#70`}"i`Bb!`KT&`~$9`Fu!7,08`k~.`UO'`Bh!24`nr_`NK$`St!`ip3`hE-6:17.256`Wvw`~~8`LKu`IH#`PI-6:19,5`PLT`~K#`Cw!.522`Wzw`~z8`G"i`CGz`J"i100e57c`Q"i`BU#7`iz4`~o+`~o+":"`Kn|`Vk+9`R8;`t**9,9`P!_`[**`Jd"`Sp!3`~&Q`VU'92`U"i8`~z8`IUf`JD#`RG'21,9`~*Z`TG#21.99`U"i8`~z8`IG#`BQ,fd077be`TG#2,2`Jc5`~r)`~z8`O]"7`~9)`Eh!3,2`l?q`Oz8`Bb!3`JiV`~z8`Cv!19:50,69`hZ?`~/%`_'.355`['.8c0a`G}2d129`R+.`C<#81`lSN`Mz819:51,0`{,U`Rz8`Cu!1`PcL`pO'`Ch!3`nv_`OK$"""
import qrcode
import zlib

f = open("somefile.json", "r")
deflate_compress = zlib.compressobj(9, zlib.DEFLATED, -zlib.MAX_WBITS)
#deflate_compress=  zlib.compressobj(9, zlib.DEFLATED, zlib.MAX_WBITS)
#deflate_compress= zlib.compressobj(6,zlib.DEFLATED,-zlib.MAX_WBITS,zlib.DEF_MEM_LEVEL,0)

#strText="fhgehfgehje  fberberb  fgerg >><"
print(len(strText))
print(len(strTextCmp))
#zlibCompress=zlib.compress(str.encode(f.read()))
zlibCompress=deflate_compress.compress(strText.encode())+ deflate_compress.flush()
print (''.join(format(x, '02x') for x in zlibCompress))
zlibCompress=zlib.compress(strText.encode(),3)
print (''.join(format(x, '02x') for x in zlibCompress))
zlibCompress=zlib.compress(strText.encode(),4)
print (''.join(format(x, '02x') for x in zlibCompress))
zlibCompress=zlib.compress(strText.encode(),5)
print (''.join(format(x, '02x') for x in zlibCompress))
zlibCompress=zlib.compress(strText.encode(),7)
print (''.join(format(x, '02x') for x in zlibCompress))
zlibCompress=zlib.compress(strText.encode(),9)
print (''.join(format(x, '02x') for x in zlibCompress))
zlibCompress=zlib.compress(strText.encode(),6)
zlibCompressStr=(''.join(format(x, '02x') for x in zlibCompress))
print (''.join(format(x, '02x') for x in zlibCompress))





# <class 'qrcode.image.pil.PilImage'>
# (290, 290)



print(str(len(strText))+" : "+str(len(zlibCompressStr))+ " : "+str(len(zlibCompress)))

img = qrcode.make(zlibCompressStr)
print(type(img))
print(img.size)
print (zlib.decompress(zlibCompress))


img.save('qrcode_test.png')
exit(0)
print(str(zlib.decompress(zlibCompress)))
Messageacrypter=str(zlib.decompress(zlibCompress))
acrypter=Messageacrypter.upper()
lg=len(acrypter)
MessageCrypte=""

for i in range(lg):
    if acrypter[i]==' ':
        MessageCrypte+=' '
    else:
        asc=ord(acrypter[i])+24
        MessageCrypte+=chr(asc+26*((asc<65)-(asc>90)))

print (MessageCrypte)
lg=len(MessageCrypte)
MessageClair=""
cle=24 # Décalage par rapport à Y (code ASCII : 24 + 1 = 25e lettre de l'alphabet)

for i in range(lg):
    if MessageCrypte[i]==' ':
        MessageClair+=' '
    else:
        asc=ord(MessageCrypte[i])-24
        MessageClair+=chr(asc+26*((asc<65)-(asc>90)))

print (MessageClair)


import objcrypt, json

crypter = objcrypt.Crypter('key')
dictionary = {
  'test': 'test value'
}
encrypted_dict = crypter.encrypt_object(dictionary)

# encrypted_dict now has encrypted values

json_dict = json.loads(dictionary)
enc_json = crypter.encrypt_json(json_dict)

# enc_json is now encrypted

dec_dict = crypter.decrypt_object(encrypted_dict)

# decoded now

dec_json = crypter.decrypt_json(enc_json)

# decoded json object now

