#coding:utf-8
import xmltodict;
import time
import httplib2
import json, io, sysconfig, locale, os
import sys
from pprint import pprint


def fetch(url,numb,home,away,time):
        http_header = {'User-Agent':'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1',
        'Referer':'https://www.sportsdatallc.org/',
        'Host':'api.sportsdatallc.org'}

        doc_name = "/Users/kshen4/code/sportdataanalysis2/matches_summary/" + str(numb) + "_" + str(home) + "_" +str(away) + "_" + str(time) + ".xml"

        h = httplib2.Http('.cache')

        print ("Start downloading data....")
        response, content = h.request(url,headers = http_header)
        print ("Finish downloading data...")

        # print(response['-content-encoding']
        print(response['status'])
        str_content = content.decode('utf-8')
        print(str_content)
        try:
                file = open(doc_name,"w")
                file.write(str_content)
        except IOError as err:
                print('File error: ' + str(err))

def pythonXmlToJson(url,numb,home,away,time):
    """
        demo Python xml to json
    """
    doc_name_xml = "/Users/kshen4/code/sportdataanalysis2/matches_summary/" + str(numb) + "_" + str(home) + "_" +str(away) + "_" + str(time) + ".xml"
    doc_name_json = "/Users/kshen4/code/sportdataanalysis2/matches_summary_json/" + str(numb) + "_" + str(home) + "_" +str(away) + "_" + str(time) + ".json"
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


# make different game files
with open("/Users/kshen4/code/sportdataanalysis2/WC_schedule.json") as dig:
    data = json.load(dig)
#pprint(data)
dig.close()

try:
            for i in range(len(data["schedule"]["matches"]["match"])):
                n = data["schedule"]["matches"]["match"][i]["@id"]
                home = data["schedule"]["matches"]["match"][i]["home"]["@country"]
                away = data["schedule"]["matches"]["match"][i]["away"]["@country"]
                date = data["schedule"]["matches"]["match"][i]["@scheduled"]
                url = "http://api.sportsdatallc.org/soccer-t2/wc/matches/" + str(n) + "/summary.xml?api_key=ug2fudww7a67kcech2jhmemj"
                print (str(url))
                if __name__ == "__main__":
                    fetch(url, n,home,away,date)
                    pythonXmlToJson(url, n,home,away,date)
                time.sleep(2)
except IOError as err:
        print ("File error: " + str(err))


# n = "dc3acf1b-dfb9-43bc-b7c9-f42626b66b51"
# url1 = "http://api.sportsdatallc.org/soccer-t2/wc/matches/" + str(n) + "/summary.xml?api_key=ug2fudww7a67kcech2jhmemj"

# if __name__ == "__main__":
#         fetch(url1, n)
