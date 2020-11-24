import json
import uuid
import requests
from kamene.all import *
from requests_toolbelt.multipart.encoder import MultipartEncoder
from websocket import create_connection

ip = '139.155.183.247'
udp_port = 12345
domain = f'http://{ip}:10086/api'
endPoint = f'ws://{ip}:10086/api/ws'


def http():
    print("""
1, send GET request
2, send POST request
3, send DELETE request
enter the sequence number to select:""")
    funCode = int(input())
    if funCode < 1 or funCode > 3:
        raise RuntimeError('No such method')

    if funCode == 1:
        print('enter your nick name to test:')
        param = input()
        response = requests.get('%s/tcp/get/%s' % (domain, param))
        print(response.text)

    elif funCode == 2:
        print('enter a valid absolute file address:')
        param = input()
        if not os.path.exists(param):
            raise RuntimeError('File not found')
        file = open(param, 'rb')
        (_, fileName) = os.path.split(file.name)
        part = MultipartEncoder(fields={
            'file': (fileName, file, 'application/octet-stream')
        })
        head = {'content-type': part.content_type}
        response = requests.post('%s/tcp/post' % domain, data=part, headers=head)
        print(response.text)

    elif funCode == 3:
        print('enter your nick name to test:')
        param = input()
        response = requests.delete('%s/tcp/delete/%s' % (domain, param))
        print(response.text)


def ws():
    print('enter your nick name to test:')
    param = input()

    webs = create_connection('%s/%s' % (endPoint, param))
    print(webs.recv())
    print('enter "stop" to stop')

    while True:
        content = input()
        if content == 'stop':
            webs.close()
            break
        webs.send(content)
        print(webs.recv())


def icmp():
    result = os.popen(f'ping {ip}')
    print(result.read())


def arp():
    pattern = re.compile(r'((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})(\.((2(5[0-5]|[0-4]\d))|[0-1]?\d{1,2})){3}')
    print('input target ip to find, 192.168.1.255 by default')
    targetIP = input()
    if len(targetIP.strip()) == 0:
        targetIP = '192.168.1.255'
    if pattern.match(targetIP) is None:
        raise RuntimeError('Wrong IP')

    hostname = socket.gethostname()
    localIP = socket.gethostbyname(hostname)
    temp = uuid.UUID(int=uuid.getnode()).hex[-12:]
    mac = ":".join([temp[e:e + 2] for e in range(0, 11, 2)])

    sr1(ARP(op=1, hwsrc=mac, hwdst='00:00:00:00:00:00', psrc=localIP, pdst=targetIP), timeout=10)


def udp():
    print('Say something:')
    content = input()
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.sendto(content.encode(), (ip, udp_port))
    print('Message has been sent, check server recent records to see whether it has reached')
    time.sleep(2)
    response = requests.get(f'{domain}/udp/recent')
    arr = json.loads(response.content)
    for i in arr:
        print(f'content: {i["content"]}    time: {i["time"]}')


if __name__ == '__main__':
    try:
        while True:
            print(f"""
1, send HTTP packs
2, send WebSocket packs
3, send ICMP packs
4, send ARP packs
5, send UDP packs
### you can use ip: {ip} as filter in WireShark ###
enter the sequence number to select:""")
            fun = int(input())
            if fun < 1 or fun > 5:
                raise RuntimeError('No such method')

            if fun == 1:
                http()
            elif fun == 2:
                ws()
            elif fun == 3:
                icmp()
            elif fun == 4:
                arp()
            elif fun == 5:
                udp()

            time.sleep(1)
    except Exception:
        print('Error!')
