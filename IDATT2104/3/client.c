#include <arpa/inet.h> // inet_addr()
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include "shared.h"


static void parse(BinExpr *expr)
{
    double a, b;
    char operator[16];
    BinOp op;
    printf("first number\n>");
    scanf("%lf", &a);

    printf("operator (+ | - | * | /)\n>");
    scanf("%s", operator);

    printf("second number\n>");
    scanf("%lf", &b);

    switch (operator[0]) {
        case '+':
            op = PLUS;
            break;
        case '-':
            op = MINUS;
            break;
        case '*':
            op = STAR;
            break;
        case '/':
            op = SLASH;
            break;
        default:
            op = ERR;
            break;
    }

    expr->a = a;
    expr->b = b;
    expr->op = op;
}

void run(int sockfd)
{
    BinExpr expr;
    char out[MSG_LEN];
    while (1) {
        parse(&expr);
        write(sockfd, &expr, sizeof(BinExpr));
        read(sockfd, out, MSG_LEN);
        printf("[from server] %s\n", out);
    }
}

int main()
{
    /* create socket */
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd == -1) {
        printf("socket creation failed...\n");
        exit(0);
    }
    printf("Socket successfully created..\n");
    struct sockaddr_in servaddr = {0};
    struct sockaddr_in cli = {0};

    /* assign IP and PORT */
    servaddr.sin_family = AF_INET;
    servaddr.sin_addr.s_addr = inet_addr(IP);
    servaddr.sin_port = htons(PORT);

    // connect the client socket to server socket
    if (connect(sockfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) != 0) {
        fprintf(stderr, "...connection with the server failed...\n");
        exit(0);
    }
    printf("connected to the server..\n");
    run(sockfd);

    close(sockfd);
}
