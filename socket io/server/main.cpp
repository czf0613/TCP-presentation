#include <cstdio>
#include <cstring>
#include <cerrno>
#include <cstdlib>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <dirent.h>

#define MAX_BYTES 8192
bool flag = true;
char current_dir[1024] = "/";
char host_name[256];
char prefix[1024];
char buff[MAX_BYTES];
char cmd_buf[1024];
const static char dot[] = ".", dot_dot[] = "..";

bool not_dot(const char *name) {
    return strcmp(name, dot) != 0 && strcmp(name, dot_dot) != 0;
}

char *getPrefix() {
    memset(prefix, 0, sizeof prefix);
    strcpy(prefix, "[root ");
    strcat(prefix, host_name);
    strcat(prefix, " ");
    strcat(prefix, current_dir);
    strcat(prefix, "] #");
    return prefix;
}

char *cd(const char *param) {
    if (strcmp(param, "..") == 0) {
        if (strcmp(current_dir, "/") == 0)
            return current_dir;
        else {
            int length = strlen(current_dir);
            int index = 1;
            for (int i = length - 1; i > 0; --i) {
                if (param[i] == '/') {
                    index = i;
                    break;
                }
            }
            char parent[1024] = {0};
            strncpy(parent, current_dir, index);
            strcpy(current_dir, parent);
            return current_dir;
        }
    } else if (strcmp(param, "~") == 0) {
        strcpy(current_dir, "/home");
        return current_dir;
    }

    char full_path[1024] = {0};
    strcpy(full_path, current_dir);
    if (param[0] != '/') {
        strcat(full_path, "/");
        strcat(full_path, param);
    }

    DIR *dir = opendir(full_path);
    if (dir == nullptr)
        return nullptr;
    strcpy(current_dir, full_path);
    closedir(dir);
    return current_dir;
}

char *ls() {
    DIR *dir = opendir(current_dir);
    struct dirent *ptr;
    while ((ptr = readdir(dir)) != nullptr) {
        if (not_dot(ptr->d_name) && (ptr->d_type == DT_DIR || ptr->d_type == DT_REG)) {
            strcat(buff, ptr->d_name);
            strcat(buff, "\n");
        }
    }
    closedir(dir);
    return buff;
}

void mkdir(const char *param) {
    memset(cmd_buf, 0, sizeof cmd_buf);
    strcpy(cmd_buf, "mkdir ");

    char path[1024] = {0};
    if (param[0] != '/') {
        strcpy(path, current_dir);
        strcat(path, "/");
    }

    strcat(path, param);
    if (access(path, F_OK) != 0)
        return;
    strcat(cmd_buf, path);
    system(cmd_buf);
}

void rm(const char *param) {
    memset(cmd_buf, 0, sizeof cmd_buf);
    strcpy(cmd_buf, "rm -rf ");

    char path[1024] = {0};
    if (param[0] != '/') {
        strcpy(path, current_dir);
        strcat(path, "/");
    }

    strcat(path, param);
    if (access(path, F_OK) != 0)
        return;
    strcat(cmd_buf, path);
    system(cmd_buf);
}

void touch(const char *param) {
    memset(cmd_buf, 0, sizeof cmd_buf);
    strcpy(cmd_buf, "touch ");

    char path[1024] = {0};
    if (param[0] != '/') {
        strcpy(path, current_dir);
        strcat(path, "/");
    }

    strcat(path, param);
    if (access(path, F_OK) != 0)
        return;
    strcat(cmd_buf, path);
    system(cmd_buf);
}

char *create_empty_file(const char *param) {
    touch(param);
    memset(cmd_buf, 0, sizeof cmd_buf);
    if (param[0] != '/') {
        strcpy(cmd_buf, current_dir);
        strcat(cmd_buf, "/");
    }
    strcat(cmd_buf, param);
    return cmd_buf;
}

char *get_file_dir(const char *param) {
    memset(cmd_buf, 0, sizeof cmd_buf);
    if (param[0] != '/') {
        strcpy(cmd_buf, current_dir);
        strcat(cmd_buf, "/");
    }
    strcat(cmd_buf, param);

    if (access(cmd_buf, F_OK) != 0)
        return nullptr;
    return cmd_buf;
}

int main() {
    int listen_fd, connect_fd;
    struct sockaddr_in server_addr{};
    gethostname(host_name, sizeof host_name);

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
    while (true) {
        if ((connect_fd = accept(listen_fd, (struct sockaddr *) nullptr, nullptr)) == -1) {
            printf("accept socket error: %s(errno: %d)", strerror(errno), errno);
            continue;
        }

        puts("connected!");
        while (flag) {
            send(connect_fd, getPrefix(), 1024, 0);

            int n = recv(connect_fd, buff, 10, 0);
            buff[n] = '\0';

            if (strcmp(buff, "exit") == 0) {
                close(connect_fd);
                flag = false;
            } else if (strcmp(buff, "cd") == 0) {
                memset(buff, 0, sizeof buff);
                n = recv(connect_fd, buff, MAX_BYTES, 0);
                buff[n] = '\0';
                if (cd(buff) == nullptr)
                    send(connect_fd, "No such file or directory!", 30, 0);
            } else if (strcmp(buff, "ls") == 0) {
                memset(buff, 0, sizeof buff);
                ls();
                send(connect_fd, buff, sizeof buff, 0);
            } else if (strcmp(buff, "mkdir") == 0) {
                memset(buff, 0, sizeof buff);
                n = recv(connect_fd, buff, MAX_BYTES, 0);
                buff[n] = '\0';
                mkdir(buff);
            } else if (strcmp(buff, "rm") == 0) {
                memset(buff, 0, sizeof buff);
                n = recv(connect_fd, buff, MAX_BYTES, 0);
                buff[n] = '\0';
                rm(buff);
            } else if (strcmp(buff, "touch") == 0) {
                memset(buff, 0, sizeof buff);
                n = recv(connect_fd, buff, MAX_BYTES, 0);
                buff[n] = '\0';
                touch(buff);
            } else if (strcmp(buff, "upload") == 0) {
                memset(buff, 0, sizeof buff);
                n = recv(connect_fd, buff, MAX_BYTES, 0);
                buff[n] = '\0';

                FILE *file = fopen(create_empty_file(buff), "wb");
                do {
                    memset(buff, 0, sizeof buff);
                    n = recv(connect_fd, buff, MAX_BYTES, 0);
                    fwrite(buff, sizeof buff, 1, file);
                    printf("recv msg from client, read %d bytes\n", n);
                } while (n != 0);
                fclose(file);
                send(connect_fd, "successfully updated file.", 30, 0);
            } else if (strcmp(buff, "download") == 0) {
                memset(buff, 0, sizeof buff);
                n = recv(connect_fd, buff, MAX_BYTES, 0);
                buff[n] = '\0';

                if (get_file_dir(buff) != nullptr) {
                    FILE *file = fopen(get_file_dir(buff), "rb");
                    do {
                        n = fread(buff, sizeof buff, 1, file);
                        send(connect_fd, buff, MAX_BYTES, 0);
                        puts("sending file to client");
                    } while (n != 0);
                    fclose(file);
                } else
                    send(connect_fd, "No such file!", 30, 0);
            } else if (strcmp(buff, "kill") == 0) {
                close(connect_fd);
                close(listen_fd);
                exit(999);
            }
            memset(buff, 0, sizeof buff);
        }
    }
}