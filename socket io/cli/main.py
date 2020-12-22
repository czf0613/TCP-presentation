import os
import socket
from pathlib import Path
from time import sleep

ip = '139.155.183.247'
port = 6666
flag = True
max_bytes = 8192

if __name__ == '__main__':
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((ip, port))
    print(f'connect success to {ip}, port is {port} via TCP protocol')
    while flag:
        reply = s.recv(max_bytes).decode('utf-8')
        print(reply, end='')

        a = input()
        params = a.split(' ')

        if params[0] == 'exit' or params[0] == 'kill':
            flag = False
            print(f'connection to {ip} has been shut down.')

        s.send(params[0].encode('utf-8'))

        if params[0] == 'cd' or params[0] == 'mkdir' or params[0] == 'rm' or params[0] == 'touch':
            s.send(params[1].encode('utf-8'))
        elif params[0] == 'upload':
            path = Path(params[1])
            if not path.exists():
                print('No such file!')
                s.send('nope'.encode('utf-8'))
                continue
            else:
                s.send(params[2].encode('utf-8'))
                sleep(0.5)
                s.send(str(os.path.getsize(params[1])).encode('utf-8'))
                sleep(0.5)
                file = open(params[1], 'rb')
                s.send(file.read())
                file.close()
        elif params[0] == 'download':
            s.send(params[1].encode('utf-8'))
            file = open(params[2], 'wb')
            size = int(s.recv(10).decode('utf-8'))
            while size > 0:
                buf = s.recv(max_bytes)
                file.write(buf)
                size -= len(buf)
            file.close()

        if params[0] == 'cd' or params[0] == 'ls' or params[0] == 'upload':
            reply = s.recv(max_bytes).decode('utf-8')
            print(reply, end='')
