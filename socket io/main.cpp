#include <cstdio>
#include <cstring>
#include <cerrno>
#include <cstdlib>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>

#define MAX_BYTES 4096
bool flag = true;
char current_dir[1024] = "/";
enum COMMAND {
    CD,
    LS,
    MKDIR,
    RM,
    TOUCH,
    UPLOAD,
    DOWNLOAD
};

int main() {
    int listen_fd, connect_fd;
    struct sockaddr_in server_addr{};
    char buff[MAX_BYTES];

    if ((listen_fd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        printf("create socket error: %s(errno: %d)\n", strerror(errno), errno);
        exit(1);
    }

    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons(6666);

    if (bind(listen_fd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
        printf("bind socket error: %s(errno: %d)\n", strerror(errno), errno);
        exit(1);
    }

    if (listen(listen_fd, 10) == -1) {
        printf("listen socket error: %s(errno: %d)\n", strerror(errno), errno);
        exit(1);
    }

    printf("======waiting for client's request======\n");
    while (flag) {
        if ((connect_fd = accept(listen_fd, (struct sockaddr *) nullptr, nullptr)) == -1) {
            printf("accept socket error: %s(errno: %d)", strerror(errno), errno);
            continue;
        }
        puts("connected!");
        int n;

        do {
            n = recv(connect_fd, buff, MAX_BYTES, 0);
            printf("recv msg from client, read %d bytes\n",n);
        } while (n != 0);

        close(connect_fd);
    }
    close(listen_fd);
}