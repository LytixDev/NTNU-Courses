#include <stdio.h>
#include <netdb.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h> // read(), write(), close()
#include <pthread.h>

#include "shared.h"


static void execute(BinExpr *expr, char out[MSG_LEN])
{
    double res;
    switch (expr->op) {
        case PLUS:
            res = expr->a + expr->b;
            break;
        case MINUS:
            res = expr->a - expr->b;
            break;
        case STAR:
            res = expr->a * expr->b;
            break;
        case SLASH:
            res = expr->a / expr->b;
            break;
        case ERR:
            strncpy(out, "error: operation not supported", MSG_LEN);
    }
    snprintf(out, MSG_LEN, "result: %lf", res);

}

void *run(void *p)
{
    int connfd = *(int *)p;
    //char buff[MAX];
    BinExpr expr;
    char out[MSG_LEN];
    int n;

    while (1) {
        read(connfd, &expr, sizeof(BinExpr));
        execute(&expr, out);
        printf("%s\n", out);
        /* send result to client */
        write(connfd, out, MSG_LEN);
    }
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

    // Accept the data packet from client and verification
    while (1) {
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
    }
    //run(connfd);

    close(sockfd);
}
