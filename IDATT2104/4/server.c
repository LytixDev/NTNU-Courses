#include <unistd.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>
#include <arpa/inet.h>

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
    int fd = *(int *)p;
    struct sockaddr_in client_addr;
    int client_length = sizeof(client_addr);
    BinExpr expr;
    char out[MSG_LEN];

    while (1) {
        if (recvfrom(fd, &expr, sizeof(expr), 0, (struct sockaddr*)&client_addr, &client_length) < 0) {
            printf("Couldn't receive\n");
            continue;
        }
        execute(&expr, out);
        printf("%s\n", out);
        /* send result to client */
        if (sendto(fd, out, MSG_LEN, 0, (struct sockaddr*)&client_addr, client_length) < 0)
            printf("Couldn't send\n");
    }
}

int main(void)
{
    char server_message[2000], client_message[2000];
    
    int sockfd = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
    
    if(sockfd < 0){
        printf("Error while creating socket\n");
        return -1;
    }

    printf("Socket created successfully\n");
    
    /* set port and ip */
    struct sockaddr_in server_addr, client_addr;
    int client_length = sizeof(client_addr);
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(2000);
    server_addr.sin_addr.s_addr = inet_addr(IP);
    
    /* bind port to ip */
    if(bind(sockfd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        printf("Couldn't bind to the port\n");
        return -1;
    }
    printf("Done with binding\n");
    
    printf("Listening for incoming messages...\n\n");
    run(&sockfd);
    
    //if (recvfrom(sockfd, client_message, sizeof(client_message), 0,
    //             (struct sockaddr*)&client_addr, &client_length) < 0) {
    //    printf("Couldn't receive\n");
    //    return -1;
    //}

    //printf("Received message from IP: %s and port: %i\n",
    //       inet_ntoa(client_addr.sin_addr), ntohs(client_addr.sin_port));
    
    printf("Msg from client: %s\n", client_message);
    
    // Close the socket:
    close(sockfd);
    
    return 0;
}
