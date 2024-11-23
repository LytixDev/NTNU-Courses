declare Length

fun {Length List}
   case List of nil 
      then 0
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