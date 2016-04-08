#coding:utf-8
import httplib2

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
                file = open('/Users/kshen4/code/sportdataanalysis2/WC_Schedule.xml',"w")
                file.write(str_content)
        except IOError as err:
                print('File error: ' + str(err))

if __name__ == "__main__":
        fetch("http://api.sportsdatallc.org/soccer-t2/wc/matches/schedule.xml?api_key=ug2fudww7a67kcech2jhmemj")
