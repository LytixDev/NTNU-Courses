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

        elseif {IsFloat Head} then
            number({String.toFloat Head}) | {Tokenize Tail}
        else
            {System.showInfo "Unrecognized lexeme"}
        end 
    end
end



declare fun {ExpressionTree Tokens}
    fun {ExpressionTreeInternal Tokens ExpressionStack}
       case Tokens
       of nil then
          ExpressionStack.1

       [] number(N)|Tail then
          {ExpressionTreeInternal Tail {Push ExpressionStack N}}
        
       [] operator(type:Type)|Tail then
            local B
                  A
                  NewStack
            in
                B = {PopFront ExpressionStack}
                NewStack = {Drop ExpressionStack 1}
                A = {PopFront NewStack}
                case Type
                of plus then
                    {ExpressionTreeInternal Tail {Push {Drop NewStack 1} plus(B A)}}
                [] minus then
                    {ExpressionTreeInternal Tail {Push {Drop NewStack 1} minus(B A)}}
                [] multiply then
                    {ExpressionTreeInternal Tail {Push {Drop NewStack 1} multiply(B A)}}
                [] divide then
                    {ExpressionTreeInternal Tail {Push {Drop NewStack 1} divide(B A)}}
                end
            end
       end
    end
in
    {ExpressionTreeInternal Tokens nil}
end



{System.showInfo "START"}
{System.show {ExpressionTree {Tokenize {Lex "3 10 9 * - 7 +"}}}}
{System.showInfo "END"}
