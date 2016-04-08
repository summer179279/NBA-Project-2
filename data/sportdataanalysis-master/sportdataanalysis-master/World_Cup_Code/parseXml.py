#!/usr/bin/env python3
#encoding=utf-8
"""
Function:
【折腾】Python中xml和Json之间相互转换：xml转json，json转xml

"""
import xmltodict;
import json , io ,sysconfig,locale,os
import sys



# sys.stdout = sys.stdout.detach()
# env = os.environ.copy()
# env['LANG'] = 'en_US.UTF-8'
# # locale.setlocale(locale.LC_ALL, None)
# print(locale.getdefaultlocale())
# print(locale.getpreferredencoding())
# # print(sysconfig.get_config_var('encoding'))
print(sys.stdout.encoding)
# print(sys.getdefaultencoding())
# print('你好')

def pythonXmlToJson():
    """
        demo Python xml to json
    """

    f = open("/Users/kshen4/code/sportdataanalysis2/Team_Hierarchy.xml","r", encoding='utf-8')
    print(f.encoding)
    # all_the_text = f.readlines()
    # print(all_the_text.encode('utf-8'))
    # sample = f.read(4)
    # print(sample)
    bin_data = f.read()

    print(type(bin_data))

    print(bin_data)
    convertedDict = xmltodict.parse(bin_data)
    jsonStr = json.dumps(convertedDict)
    print("jsonStr=", jsonStr)

    try:
            with open("/Users/kshen4/code/sportdataanalysis2/Team_Hierarchy.json", "w") as dig:
                    print(jsonStr, file=dig)
    except IOError as err:
            print('File error: ' + str(err))

###############################################################################
if __name__=="__main__":
    pythonXmlToJson();
