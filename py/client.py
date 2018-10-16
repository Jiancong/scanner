import requests

#http://docs.python-requests.org/en/latest/user/quickstart/#post-a-multipart-encoded-file

url = "http://10.26.7.144:5000/api/upload"
#url = "http://121.42.164.2/uploader"
fin = open('null_test(1).jpg', 'rb')
#fin = open('1.png', 'rb')
files = {'file': fin}
clientinfos = {'client_ver': '1.2222',"client_type":"android"}

try:
  r = requests.post(url, files=files, data=clientinfos)
  print(r.text)

finally:
  fin.close()