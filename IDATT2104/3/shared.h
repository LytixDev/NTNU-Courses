#ifndef SHARED_H
#define SHARED_H

#define MSG_LEN 64
#define IP "127.0.0.1"
#define PORT 8420

/* types */

typedef enum binary_operators {
    PLUS,       // +
    MINUS,      // -
    STAR,       // *
    SLASH,      // /
    ERR,
} BinOp;

typedef struct binary_expr_t {
    double a;
    double b;
    BinOp op;
} BinExpr;

#endif /* SHARED_H */
