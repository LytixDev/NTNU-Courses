\insert 'List.oz' /* NOTE: You may have to change this path */

declare fun {IsFloat S}
   try
      _ = {String.toFloat S}
      true
   catch
      _ then
      false
   end
end

declare fun {Lex Input}
    {String.tokens Input & }
end

declare fun {Tokenize Lexemes}
    case Lexemes of nil then
        nil
    [] Head|Tail then 
        if Head.1 == 47 then /* / */
            operator(type:divide) | {Tokenize Tail}
        elseif Head.1 == 42 then /* * */
            operator(type:multiply) | {Tokenize Tail}
        elseif Head.1 == 43 then /* + */
            operator(type:plus) | {Tokenize Tail}
        elseif Head.1 == 45 then /* - */
            operator(type:minus) | {Tokenize Tail}

        elseif Head.1 == 112 then /* p */
            command(print) | {Tokenize Tail}
        elseif Head.1 == 100 then /* d */
            command(duplicate) | {Tokenize Tail}
        elseif Head.1 == 105 then /* i */
            command(invert) | {Tokenize Tail}
        elseif Head.1 == 99 then /* c */
            command(clear) | {Tokenize Tail}

        elseif {IsFloat Head} then
            number({String.toFloat Head}) | {Tokenize Tail}
        else
            {System.showInfo "Unrecognized lexeme"}
        end 
    end
end

declare fun {Interpret Tokens}
    fun {InterpretRecurse Tokens Stack}
       case Tokens
       of nil then
          Stack

       [] number(N)|Tail then
          {InterpretRecurse Tail {Push Stack N}}

       [] command(C)|Tail then
          case C of print then
            {System.show Stack}
            {InterpretRecurse Tail Stack}
          [] duplicate then
            local A
            in
               A = {PopFront Stack}
               {InterpretRecurse Tail {Push Stack A}} 
            end
          [] invert then
            local A
                  NewStack
            in
               A = {PopFront Stack}
               NewStack = {Drop Stack 1}
               {InterpretRecurse Tail {Push NewStack 0 - A}}
            end
          [] clear then
            {InterpretRecurse Tail nil}
          else
            {System.showInfo "Err: Unknown command"}
            nil
          end

       [] operator(type:Type)|Tail then
          case Type of plus then
             local B
                   A
                   NewStack
             in
                B = {PopFront Stack}
                NewStack = {Drop Stack 1}
                A = {PopFront NewStack}
                {InterpretRecurse Tail {Push {Drop NewStack 1} A + B}}
            end
          [] minus then
             local B
                   A
                   NewStack
             in
                B = {PopFront Stack}
                NewStack = {Drop Stack 1}
                A = {PopFront NewStack}
                {InterpretRecurse Tail {Push {Drop NewStack 1} A - B}}
             end
          [] multiply then
             local B
                   A
                   NewStack
             in
                B = {PopFront Stack}
                NewStack = {Drop Stack 1}
                A = {PopFront NewStack}
                {InterpretRecurse Tail {Push {Drop NewStack 1} A * B}}
             end
          [] divide then
             local B
                   A
                   NewStack
             in
                B = {PopFront Stack}
                NewStack = {Drop Stack 1}
                A = {PopFront NewStack}
                if A == 0 then
                   {System.showInfo "Err: Division by zero"}
                   nil
                else
                   {InterpretRecurse Tail {Push {Drop NewStack 1} B / A}} % I am unable to convert numbers to floats.
                end
             end
          else
             {System.showInfo "Err: Unknown operator"}
             nil
          end
       else
          {System.showInfo "Err: Invalid token"}
          nil
       end
    end
 in
    {InterpretRecurse Tokens nil}
 end

{System.showInfo "START"}
{Show {Interpret {Tokenize {Lex "2 4 /"}}}}
{System.showInfo "END"}