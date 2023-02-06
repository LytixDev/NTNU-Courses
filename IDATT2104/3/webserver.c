#include <stdio.h>
#include <netdb.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <pthread.h>

#include "shared.h"
#ifdef PORT
#  undef PORT
#endif
#define PORT 80


void *run(void *p)
{
    int connfd = *(int *)p;
    char out[4096];
    char response[4096 * 2] = "HTTP/1.0 200 OK\n"
        "Content-Type: text/html; charset=utf-8\n\n"
        "<h1>Halla.</h1>\nHeader fra klient er:";

    read(connfd, &out, 4096);
    printf("%s\n", out);
    strcat(response, out);
    write(connfd, response, strlen(response) + 1);
    close(connfd);
    return NULL;
}

int main()
{
    /* create socket */
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) {
        fprintf(stderr, "...socket creation failed...\n");
        exit(1);
    }
    printf("Socket successfully created..\n");

    struct sockaddr_in servaddr = {0};

    /* assign IP and PORT */
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    servaddr.sin_port = htons(PORT);

    /* binding newly created socket to given IP and verification */
    if ((bind(sockfd, (struct sockaddr *)&servaddr, sizeof(servaddr))) != 0) {
        fprintf(stderr, "...socket bind failed...\n");
        exit(1);
    }
    printf("Socket successfully binded..\n");

    /* listen and verify */
    if ((listen(sockfd, 5)) != 0) {
        fprintf(stderr, "...listen failed...\n");
        exit(1);
    }
    printf("Server listening..\n");

    while (1) {
        // Accept the data packet from client and verification
        struct sockaddr_in client = {0};
        unsigned int len = sizeof(client);

        int connfd = accept(sockfd, (struct sockaddr *)&client, &len);
        if (connfd < 0) {
            fprintf(stderr, "server accept failed...\n");
            exit(1);
        }
        printf("server accepted the client...\n");
        pthread_t thread;
        pthread_create(&thread, NULL, run, &connfd);
        //run(&connfd);
    }

    close(sockfd);
}
