//https://www.educative.io/answers/how-to-implement-udp-sockets-in-c
#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>

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

void run(int sockfd, struct sockaddr_in *server)
{
    int len = sizeof(*server);
    BinExpr expr;
    char out[MSG_LEN];
    while (1) {
        parse(&expr);
        if (sendto(sockfd, &expr, sizeof(expr), 0, (struct sockaddr*)server, len) < 0)
            printf("Unable to send message\n");

        if (recvfrom(sockfd, out, MSG_LEN, 0, (struct sockaddr*)server, &len) < 0)
            printf("Error while receiving server's msg\n");
        printf("[from server] %s\n", out);
    }
}
int main(void)
{
    int sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    
    if(sockfd < 0){
        printf("Error while creating socket\n");
        return -1;
    }
    printf("Socket created successfully\n");
    
    struct sockaddr_in server;
    int server_struct_length = sizeof(server);
    server.sin_family = AF_INET;
    server.sin_port = htons(2000);
    server.sin_addr.s_addr = inet_addr(IP);
    
    run(sockfd, &server);
    
    // Close the socket:
    close(sockfd);
    
    return 0;
}

