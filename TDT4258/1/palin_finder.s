.global _start


_start:
	ldr r0, =input	        // load input string into r0
	bl str_len		// call str_len. len is now stored in r0
	ldr r1, =input	        // load input string into r1
	bl check_if_palindrome	// r0 is not the result
	bl display_result	// uses r0
	b _exit



// r0 should be the length of the input string
// r1 should be the input string
//
// stores a boolean result in r0
check_if_palindrome:
	push {lr}               // save the link register on the stack
	// r1 = L, r2 = R
	add r2, r1, r0          // address of the last character (right pointer)
	
	// correctly sets r0
	bl check_if_palindrome_loop
	
	pop {pc}	        // restore the link register and return

// stores true in r0 if palindrome else false
check_if_palindrome_loop:
	push {lr}               // save the link register on the stack
	
	// ignore all whitespace
	// r4 is value at L
	// r5 is value at R
cont_l:
	ldrb r4, [r1]	        // get byte at L
	cmp r4, #32		// 32 is ' ' in ascii
	bne .+12
	add r1, r1, #1	        // l++
	b cont_l		// continue ignore whitespace
	
	// ignore all whitespace
cont_r:
	ldrb r5, [r2]	        // get byte at R
	cmp r5, #32		// 32 is ' ' in ascii
	bne .+12
	sub r2, r2, #1	        // r--
	b cont_l		// continue ignore whitespace
	
	// edge case where either r, l or both was all whitespace
	sub r3, r2, r1	// r - l
	cmp r3, #0
	bgt .+12	        // if diff > 0 then continue as normal
	mov r0, #1 		// store true
	pop {pc}		// restore the link register and return
		
	mov r0, r4		// put byte at L into r0
	bl to_lower
	mov r4, r0		// store "lowered" byte at L in r4
	mov r0, r5		// put byte at R into r0
	bl to_lower
	mov r5, r0		// store "lowered" byte at R in r5
	
	cmp r4, r5
	beq .+12		// if bytes not equal then return false
	mov r0, #0 	        // store false
	pop {pc}	        // restore the link register and return
	
	add r1, r1, #1          // l++
	sub r2, r2, #1          // r--
	sub r3, r2, r1	        // r - l
	cmp r3, #0		// r - l with 0
	bgt check_if_palindrome_loop+4 // while r - l > 0, continue
	mov r0, #1		// store true
	pop {pc}		// restore the link register and return
	


// r0 should be an ascii character
// converts r0 to lower if its not already
to_lower:
        cmp r0, #65    	        // 65 is ascii value of A
        blt .+24		// return r0 < 'A'
        cmp r0, #90             // compare r0 with ascii value of Z
        bgt .+8   		// retyrb if r0 > 'Z'
        add r0, r0, #32         // r0 += 32
        bx lr



// r0 should point to null terminated string
//
// stores the len in r0
str_len:
        push {lr}               // save the link register on the stack
        mov r1, #0              // initialize the length counter
	b str_len_loop

str_len_loop:
        ldrb r2, [r0], #1       // load next byte and increment r0
        cmp r2, #0              // compare with null terminator
        beq str_len_done        // branch if equal to done
        add r1, r1, #1          // len++
        b str_len_loop          // continue

str_len_done:
        mov r0, r1              // move the len to r0
	sub r0, r0, #1		// remove null terminator
        pop {pc}                // restore the link register and return
	


// if r0 is 0 then not a palindrome, else is a palindrome
// example: mov r0, #0
//			b set_led
display_result:
	push {lr}               // save the link register on the stack

	ldr r1, =0xFF200000	// address of LED
	mov r2, #0b11111	// light up last five LEDS
	cmp r0, #0
	bne .+8        		// If not equal branch OVER next instruction
	mov r2, #0b1111100000   // light up last five LEDS
	str r2, [r1]        	// Store the byte value from r1 into the memory location in r0
	
	mov r1, r0	        // store input in r1 as we need to use r0 for something else
	ldr r0, =failure
	cmp r1, #0
	beq .+8
	ldr r0, =success
	bl write_jtag_uart_loop
	
	pop {pc}                // restore the link register and return

write_jtag_uart_loop:
	ldrb r1, [r0], #1	// load byte and increment
	cmp r1, #0		// check for null terminator
	beq write_jtag_uart_end
	ldr r2, =0xff201000	// write address
	str r1, [r2]		// store in write address
	b write_jtag_uart_loop

write_jtag_uart_end:
	bx lr


_exit:
	b .

.data
.align
	input: .asciz "Grav ned den varg"
	success: .asciz "Palindrome detected"
	failure: .asciz "Not a palindrome"
.end
