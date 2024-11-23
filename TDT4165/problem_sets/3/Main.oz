declare QuadraticEquation Sum RightFold SumR LengthR Quadratic LazyNumberGenerator SumTailRecursive in

% Task 1

proc {QuadraticEquation A B C ?RealSol ?X1 ?X2}
    local Discriminant in
        Discriminant = B*B - 4.0*A*C
        if Discriminant < 0.0 then
            RealSol = false
        else
            local Sqrt in
                {Float.sqrt Discriminant Sqrt}
                RealSol = true
                X1 = (~B + Sqrt) / (2.0*A)
                X2 = (~B - Sqrt) / (2.0*A)
            end
        end
    end
end


local RealSol X1 X2 in
    {QuadraticEquation 2.0 1.0 ~1.0 RealSol X1 X2}
    if RealSol then
        {System.showInfo "Has real solutions:"}
        {System.showInfo X1}
        {System.showInfo X2}
    else
        {System.showInfo "No real solutions"}
    end
end

local RealSol X1 X2 in
    {QuadraticEquation 2.0 1.0 2.0 RealSol X1 X2}
    if RealSol then
        {System.showInfo "Has real solutions:"}
        {System.showInfo X1}
        {System.showInfo X2}
    else
        {System.showInfo "No real solutions"}
    end
end

% Task 2

fun {Sum List} 
    case List
    of nil then 
        0
    [] H|T then 
        H + {Sum T}
    end
end

{System.showInfo {Sum [1 2 3]}}

% Task 3

fun {RightFold List Op U}
    case List
    of nil then
        U
    [] H|T then
        {Op H {RightFold T Op U}} % right-associative
    end
end

fun {SumR List}
    {RightFold List fun {$ X Y} X+Y end 0}
end  

fun {LengthR List}
    {RightFold List fun {$ X Y} 1+Y end 0}
end
  
{System.showInfo {SumR [1 2 3 4]}}
{System.showInfo {LengthR [1 2 3 4]}}

% Task 4

fun {Quadratic A B C}
    fun {$ X} A*X*X + B*X + C end
end

{System.show {{Quadratic 3 2 1} 2}}

% Task 5

fun {LazyNumberGenerator StartValue}
    StartValue # fun {$} {LazyNumberGenerator (StartValue + 1)} end
end

{System.show {LazyNumberGenerator 0}.1}
{System.show {{LazyNumberGenerator 0}.2}.1}
{System.show {{{{{{LazyNumberGenerator 0}.2}.2}.2}.2}.2}.1}

% Task 6

fun {SumTailRecursive List}
    fun {SumTailRecursiveInternal List A}
        case List
        [] H|T then
            {SumTailRecursiveInternal T A+H}
        of nil then
            A
        end
    end 
    {SumTailRecursiveInternal List 0}
end