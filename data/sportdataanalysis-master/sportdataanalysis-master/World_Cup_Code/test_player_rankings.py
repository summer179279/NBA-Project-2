#coding:utf-8
import xmltodict;
import time
import httplib2
import json, io, sysconfig, locale, os
import sys
from pprint import pprint

def fetch(url):
        http_header = {'User-Agent':'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1',
        'Referer':'https://www.sportsdatallc.org/',
        'Host':'api.sportsdatallc.org'}

        h = httplib2.Http('.cache')

        print ("Start downloading data....")
        response, content = h.request(url,headers = http_header)
        print ("Finish downloading data...")

        # print(response['-content-encoding']
        print(response['status'])
        str_content = content.decode('utf-8')
        print(str_content)
        try:
                file = open('/Users/kshen4/code/sportdataanalysis2/player_ranking.xml',"w")
                file.write(str_content)
        except IOError as err:
                print('File error: ' + str(err))

def pythonXmlToJson(url):
    """
        demo Python xml to json
    """
    doc_name_xml = '/Users/kshen4/code/sportdataanalysis2/player_ranking.xml'
    doc_name_json = '/Users/kshen4/code/sportdataanalysis2/player_ranking.json'
    f = open(doc_name_xml,"r", encoding='utf-8')
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
            with open(doc_name_json, "w") as dig:
                    print(jsonStr, file=dig)
    except IOError as err:
            print('File error: ' + str(err))

if __name__ == "__main__":
        fetch("http://api.sportsdatallc.org/soccer-t2/wc/players/leader.xml?api_key=3jq2sehnbpnthqtgpkknsekt")
        pythonXmlToJson("http://api.sportsdatallc.org/soccer-t2/wc/players/leader.xml?api_key=3jq2sehnbpnthqtgpkknsekt")
