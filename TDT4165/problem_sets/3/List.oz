/* Task 4 */
declare Max

fun {Max X Y}
   if X > Y then
      X
   else
      Y
   end
end

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

/* d) */
declare Append
fun {Append List1 List2} 
   case List1 of nil then
      List2
   else
      List1.1|{Append List1.2 List2}
   end
end

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

/* Task 8 */

/* a) */
declare Push
fun {Push List Element}
   Element|List
end

/* b) */
declare Peek
fun {Peek List}
   case List of nil then
      nil
   else
      List.1
   end
end

/* c) */
declare Pop
fun {Pop List}
   case List of nil then
      nil
   else
      List.2
   end
end

declare PopFront
fun {PopFront List}
   case List of nil then
      nil
   else
      List.1
   end
end
