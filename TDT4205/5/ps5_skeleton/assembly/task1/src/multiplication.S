.globl main

.data

n_rows: .quad 10
n_cols: .quad 10
space_for_numbers: .quad 3

space: .string " "
newline: .string "\n"

integer_slot: .string "%ld"

.text
/*
    Main "function" - program starts here!
    It works as a regular function, but is called at when running the program instead of by other code.

    We allow main to use the callee-save/"global" registers:
    %rbx, %rbp, and %r12-r15

    Our program will probably use loops to iterate through the table.
    Thus, we will probably use variables/counters tracking the current loop,
    and possibly another to store the multiplication result.

    How you handle this is up to you,
    but you may assign certain registers to always represent certain variables.
    E.g.:
        r12 = i
        r13 = j
        multiplication_result = r14
        etc...

    Note that we do have a finite amount of "global" registers.
    We use these as we *know* they cannot (should not) be overwritten by other functions.

    We recommend that you keep a AT&T Assembly sheet cheat like this nearby:
    https://cs.brown.edu/courses/cs033/docs/guides/x64_cheatsheet.pdf
    
*/
main:
    pushq %rbp
    movq %rsp, %rbp

    # USING:
    # r12 = i
    # r13 = j
    # multiplication_result (MR) = r14

    mov $1, %r12

    OUTER_LOOP_START:
        leaq n_rows(%rip), %rdi # load the address of n_rows into rdi
        movq (%rdi), %rax       # load the value at n_rows into rax
        cmp %rax, %r12          # loop while i > n_rows
        jg OUTER_LOOP_END
        mov $1, %r13 # j = 1

        INNER_LOOP_START:
            leaq n_cols(%rip), %rdi # load the address of n_cols into rdi
            movq (%rdi), %rax       # load the value at n_cols into rax
            cmp %rax, %r13          # loop while j > n_cols
            jg INNER_LOOP_END

                mov %r12, %rax
                mulq %r13       # rax = rax * j -> i * j
                mov %rax, %r14  # store result in MR

                mov %r14, %rdi
                call get_digits
                sub $3, %rax    # -padding = digit count - 3.
                neg %rax        # flip sign giving us the padding
                mov %rax, %rsi  # put padding value into rsi
                mov %r14, %rdi  # put number into rdi
                # NOTE: I think I should be pushing and popping local variables here to adhere to the
                #       standard calling convention, however, it appears to work so I won't bother :-).
                call print_padded_number
                call print_space

            inc %r13
            jmp INNER_LOOP_START
        INNER_LOOP_END:

        call print_newline
        inc %r12
        jmp OUTER_LOOP_START
    OUTER_LOOP_END:

    movq $0, %rax
    leave
    ret


/*
    Functions are defined here.
    These will be executed when we call them using the `call` keyword.
*/

/*
    Function: print_padded_number

    Prepends PADDING spaces before NUMBER, and prints it to output

    Args:
        (%rdi) NUMBER (quad)  - Number to print
        (%rsi) PADDING (quad) - Number of padding spaces before number 
*/
print_padded_number:
    pushq %rbp
    movq %rsp, %rbp

    # USING:
    # r8 = NUMBER
    # r9 = PADDING
    # r11 = spaces printed
    mov %rdi, %r8
    mov %rsi, %r9
    mov $0, %r11  # spaces printed

    PADDING_LOOP_START: 
        cmp %r9, %r11  # if spaces printed >= padding, then stop printing spaces
        jge PADDING_LOOP_END

        push %r8
        push %r9
        push %r11
        call print_space
        pop %r11
        pop %r9
        pop %r8

        inc %r11
        jmp PADDING_LOOP_START
    PADDING_LOOP_END: 

    leaq integer_slot(%rip), %rdi
    mov %r8, %rsi  # load number
    call safe_printf

    # Done
    leave
    ret

/*
    Function: print_space

    Simply prints a space.
*/
print_space:
    pushq %rbp
    movq %rsp, %rbp

    leaq space(%rip), %rdi
    call safe_printf

    leave
    ret

/*
    Function: print_newline

    Simply prints a newline.
*/
print_newline:
    pushq %rbp
    movq %rsp, %rbp

    leaq newline(%rip), %rdi
    call safe_printf

    leave
    ret

/*
    Function: get_digits

    Calculates the digits of NUMBER, and returns it as N_DIGITS

    Args:
        (%rdi) NUMBER (quad)  - Number to print

    Returns:
        (%rax) N_DIGITS (quad) - Number of digits in NUMBER 
*/
get_digits:
    pushq %rbp
    movq %rsp, %rbp

    movq $1, %r11           # r11 = counter = 1

    # Ready argument for division
    movq %rdi, %rax
    
gd_loop:
    # Divide NUMBER by 10
    movq $10, %r8

    # Ensure that %rdx = 0; divq will use it as upper bits...
    movq $0, %rdx
    # %rax = %rax / 10
    divq %r8

    # If quotient == 0, exit the loop
    movq $0, %r8

    cmp %rax, %r8
    je gd_loop_end

    # Else, increment digit counter, and repeat
    # Note, result from division is already in the correct register
    inc %r11
    jmp gd_loop

gd_loop_end:
    # Our counter contains number of digits; move it to return register %rax
    movq %r11, %rax

    leave
    ret


/*
    Function: safe_printf

    Prints to output - like printf - but makes sure that stack is alligned before calling printf
    Convinience function that avoids a lot of crashes caused by printf

    Args:
        (all) any value, but always a string pointer in (%rdi)
*/
safe_printf:
	pushq %rbp
	movq %rsp, %rbp

	andq $-16, %rsp
	call printf
    
	leave
	ret

// Workaround for macOS prefixing symbols with _
#ifdef __APPLE__
    .set printf, _printf
    .set putchar, _putchar
    .set _main, main
    .global _main
#endif
