/* Task 1 */
{Show 'Hello World'}

/* Task 3 a) */
local X Y=300 Z=30 in
   X = Y * Z
   {Show X}
end

/* Task 3 b) */
local X Y in
   X = "This is a string"
   thread {System.showInfo Y} end
   Y = X
end
/*
   The thread keyword will create a new thread. The current thread will however continue to the next statement (Y = X). 
   The newly created thread will execute the System.showInfo statement once Y is assigned a value.
   Why is this useful? Well, in this case we get threading organization for free and we don't have to write code that
   suspends the thread waiting for Y until Y is assigned. Whether or not this is ACTUALLY useful, I don't know.
   Seems gimmicky and I don't see any immediate practical use cases.

   Y = X is an assignment statement, which will assign the value of X to Y.
*/

/* Task 4 */
declare Max

fun {Max X Y}
   if X > Y then
      X
   else
      Y
   end
end

declare PrintGreater

proc {PrintGreater Number1 Number2}
   {System.showInfo {Max Number1 Number2}}
 end

{PrintGreater 10 2}

/* Task 5 */
declare Circle

proc {Circle R} A D C PI=355.0/113.0 in
   A = PI * R * R
   D = 2.0 * R
   C = PI * D
   {System.showInfo A}
   {System.showInfo D}
   {System.showInfo C}
end

{Circle 2.0}

/* Task 6 */
declare Factorial

fun {Factorial N}
   /* 0! = 1 */
   if N == 0 then
      1
   else
      N * {Factorial N - 1}
   end
end

{System.showInfo {Factorial 4}}

/* Task 7 */
/* a) */
declare Length

fun {Length List}
   case List of nil then 
      0
   [] Head|Tail then 
      1 + {Length Tail}
   end
end


/* b) */
declare Take 
fun {Take List Count} Size={Length List} in
  if Count > Size then
    List
  else
    if Count > 0 then
      List.1|{Take List.2 Count-1}
    else
      nil
    end
  end
end
{Show {Take [2 4 6 8 10] 2}}

/* c) */
declare Drop
fun {Drop List Count} Size={Length List} in
  if Count > Size then
    nil
  else
    if Count > 0 then
      {Drop List.2 Count-1}
    else
      List
    end
  end
end

{Show {Drop [2 4 6 8 10] 2}}

/* d) */
declare Append
fun {Append List1 List2} 
   case List1 of nil then
      List2
   else
      List1.1|{Append List1.2 List2}
   end
end

{Show {Append [2 4] [6 8]}}

/* e) */
declare Member
fun {Member List Element}
   case List of nil then
      false
   else
      if List.1 == Element then
         true
      else
         {Member List.2 Element}
      end
   end
end

{Show {Member [2 4 6 8] 2}}
{Show {Member [2 4 6 8] 3}}

/* f) */
declare Position
fun {Position List Element} Pos=1 in
   case List of nil then
      nil
   else
      if List.1 == Element then
         Pos
      else
         {Position List.2 Element} + 1
      end
   end
end

{Show {Position [2 4 6 8] 4}}

/* Task 8 */

/* a) */
declare Push
fun {Push List Element}
   Element|List
end

{Show {Push [2 4 6 8] 10}}

/* b) */
declare Peek
fun {Peek List}
   case List of nil then
      nil
   else
      List.1
   end
end

{Show {Peek [2 4 6 8]}}

/* c) */
declare Pop
fun {Pop List}
   case List of nil then
      nil
   else
      List.2
   end
end

{Show {Pop [2 4 6 8]}}
